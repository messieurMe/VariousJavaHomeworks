package ru.ifmo.rain.kuliev.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;


public class ParallelMapperImpl implements ParallelMapper {
    private Lock lock;
    private Thread[] threads;
    private Condition flagNotFull;
    private Condition flagNotEmpty;
    private Queue<Runnable> threadsQueue;

    private final int THREADS_QUEUE_SIZE = 1000;

    /**
     * Constructor for {@code ParallelMapper}.
     *
     * @param threadsNumber number of thread, which will be started
     */
    public ParallelMapperImpl(int threadsNumber) {
        if (threadsNumber <= 0) {
            throw new IllegalArgumentException("Number must be at least 1");
        }
        lock = new ReentrantLock();
        threadsQueue = new ArrayDeque<>();
        threads = new Thread[threadsNumber];
        flagNotEmpty = lock.newCondition();
        flagNotFull = lock.newCondition();
        for (int i = 0; i < threadsNumber; i++) {
            threads[i] = new Thread(this::doTask);
            threads[i].start();
        }
    }

    private void doTask() {
        Runnable current;
        while (!Thread.interrupted()) {
            lock.lock();
            try {
                while (threadsQueue.isEmpty()) {
                    try {
                        flagNotEmpty.await();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                current = threadsQueue.poll();
                flagNotFull.signalAll();
            } finally {
                lock.unlock();
            }
            current.run();
        }
    }


    /**
     * Maps function {@code f} over specified {@code args}.
     * Mapping for each element performs in parallel.
     *
     * @param f function which will be executed
     * @param args for method
     * @param <T> type of param
     * @param <S> type of return {@code List}
     * @return list with elements of {@code T} type
     * @throws InterruptedException if error occurs
     */
    @Override
    public <T, S> List<S> map(Function<? super T, ? extends S> f, List<? extends T> args) throws InterruptedException {
        List<S> resultThreads = new ArrayList<>(Collections.nCopies(args.size(), null));
        final CountDownLatch finalCountDown = new CountDownLatch(args.size());

        for (var i = 0; i < args.size(); i++) {
            lock.lock();
            try {
                while (threadsQueue.size() == THREADS_QUEUE_SIZE) {
                    flagNotFull.await();
                }
                final int index = i;
                threadsQueue.add(() -> {
                    resultThreads.set(index, f.apply(args.get(index)));
                    finalCountDown.countDown();
                });
                flagNotEmpty.signalAll();
            } finally {
                lock.unlock();
            }
        }
        finalCountDown.await();
        return resultThreads;
    }

    /**
     * Closes threads.
     */
    @Override
    public void close() {
        for (Thread it : threads) {
            it.interrupt();
        }
        for (Thread it : threads) {
            try {
                it.join();
            } catch (InterruptedException ignored) {
            }
        }
    }
}