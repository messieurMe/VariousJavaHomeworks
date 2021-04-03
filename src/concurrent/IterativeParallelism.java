package ru.ifmo.rain.kuliev.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class IterativeParallelism implements ScalarIP {

    private ParallelMapper parallelMapper;

    public IterativeParallelism() {
        parallelMapper = null;
    }

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    private <T, S> S universalFunction(int threadsNumber, List<? extends T> values, Function<Stream<? extends T>, S> threadFunction, Function<Stream<? extends S>, S> performFunction) throws InterruptedException {
        if (threadsNumber > 0) {
            try {
                Objects.requireNonNull(values);
            } catch (NullPointerException e) {
                throw new InterruptedException("Process was interrupted, values is null");
            }
            try {
                Objects.requireNonNull(threadFunction);
            } catch (NullPointerException e) {
                throw new InterruptedException("Process was interrupted, thread function are null");
            }
            try {
                Objects.requireNonNull(performFunction);
            } catch (NullPointerException e) {
                throw new InterruptedException("Interrupted exception, result function are null");
            }
        } else {
            throw new InterruptedException("Not enough threads, expected more than 0");
        }

        int threadsCount = Math.max(Math.min(threadsNumber, values.size()), 1);
        int oneThread = values.size() / threadsCount;
        int restThread = values.size() % threadsCount;

        ArrayList<Stream<? extends T>> valuesParts = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++) {
            int start = i * oneThread + Math.min(i, restThread);
            int flag = 1;
            if (i >= restThread) {
                flag = 0;
            }
            int finish = start + oneThread + flag;
            valuesParts.add(values.subList(start, finish).stream());
        }

        List<S> resultThreads;

        if (parallelMapper != null) {
            resultThreads = parallelMapper.map(threadFunction, valuesParts);
        } else {
            resultThreads = new ArrayList<>(Collections.nCopies(threadsCount, null));
            ArrayList<Thread> threads = new ArrayList<>();
            for (int i = 0; i < threadsCount; i++) {
                final int index = i;
                Thread thread = new Thread(() -> resultThreads.set(index, threadFunction.apply(valuesParts.get(index))));
                threads.add(thread);
                threads.get(index).start();
            }
            for (int i = 0; i < threadsCount; i++) {
                threads.get(i).join();
            }
        }
        return performFunction.apply(resultThreads.stream());
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> cmp) throws InterruptedException {
        return universalFunction(threads, values, (stream) -> stream.min(cmp).orElseThrow(), (stream) -> stream.min(cmp).orElseThrow());
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return universalFunction(threads, values, (stream) -> stream.allMatch(predicate), (stream) -> stream.allMatch(element -> element));
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> cmp) throws InterruptedException {
        return minimum(threads, values, Collections.reverseOrder(cmp));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }
}