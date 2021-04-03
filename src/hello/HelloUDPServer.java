package ru.ifmo.rain.kuliev.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.net.*;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;
import java.nio.charset.StandardCharsets;

public class HelloUDPServer implements HelloServer {
    private final int OFFSET = 0;
    private DatagramSocket socket;
    private ExecutorService executor;
    private ExecutorService executorGlobal = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        isCorrectArgs(args);
        new HelloUDPServer().start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        for(int i = 0; i < 1_000_000; i++);;;;
    }

    @Override
    public void start(int port, int threads) {
        if (threads <= 0) {
            System.out.println("Sorry, number of threads must be at least 1.");
            return;
        }
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Sorry, socket could not be opened, or it could not bind to the specified local port.");
            return;
        }
        executor = Executors.newFixedThreadPool(threads);
        executorGlobal.submit(this::getData);
    }

    private static void isCorrectArgs(String[] args) {
        if (args == null || args.length != 2) {
            System.err.println("Wrong number of args. Expected 5, found" + (args == null ? 0 : args.length));
            return;
        }
        try {
            for (String i : args) {
                if (i == null) {
                    throw new NullPointerException();
                }
            }
            if (Integer.parseInt(args[0]) < 0 || Integer.parseInt(args[1]) < 0) {
                System.err.println("First two arguments can't be negative");
            }
        } catch (NumberFormatException e) {
            System.err.println("One of arguments is wrong. It's null or non required. Please, check them and retry");
        }
    }

    private void getData() {
        try {
            while (true) {
                if (socket.isClosed() || Thread.currentThread().isInterrupted()) {
                    break;
                } else {
                    int bufferSize = socket.getReceiveBufferSize();
                    DatagramPacket dataPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
                    socket.receive(dataPacket);
                    executor.submit(() -> answer(dataPacket));
                }
            }
        } catch (PortUnreachableException e) {
            System.out.println("Sorry, your port is unreachable.\n" + e.getMessage());
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout has occurred due to reading from packet.\n" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Input output exception has occurred.\n" + e.getMessage());
        }
    }

    @Override
    public void close() {
        socket.close();
        executor.shutdownNow();
        executorGlobal.shutdownNow();
        try {
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private void answer(DatagramPacket packetData) {
        String returnValue = String.format("Hello, %s", new String(packetData.getData(), packetData.getOffset(),
                                                                   packetData.getLength()));
        try {
            byte[] req = returnValue.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(req, OFFSET, req.length, packetData.getSocketAddress()));
        } catch (IOException e) {
            System.out.println("Input output error occurred due to working\n" + e.getMessage());
        }
    }
}