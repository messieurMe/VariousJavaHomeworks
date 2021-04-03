package ru.ifmo.rain.kuliev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.net.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;


public class HelloUDPClient implements HelloClient {
    private ExecutorService exec;
    private InetSocketAddress inetSocketAddress;

    public static void main(String[] args) {
        isCorrectArgs(args);
        new HelloUDPClient().run(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]),
                                 Integer.parseInt(args[4]));
    }

    private static void isCorrectArgs(String[] args) {
        if (args == null || args.length != 5) {
            System.err.println("Wrong number of args. Expected 5, found" + (args == null ? 0 : args.length));
            return;
        }
        try {
            for (String i : args) {
                if (i == null) throw new NullPointerException();
            }
            if (Integer.parseInt(args[1]) < 0 || Integer.parseInt(args[3]) < 0 || Integer.parseInt(args[4]) < 0) {
                System.err.println("Arguments 1,3,4 can't be negative");
            }
        } catch (NumberFormatException e) {
            System.err.println("One of arguments is wrong. It's null or non required. Please, check them and retry");
        }
    }


    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        exec = Executors.newFixedThreadPool(threads);
        inetSocketAddress = new InetSocketAddress(host, port);
        for (int i = 0; i < threads; i++) {
            final int threadNumber = i;
            Thread nextThread = new Thread(() -> sendData(prefix, requests, threadNumber));
            exec.submit(nextThread);
        }
        try {
            exec.shutdown();
            exec.awaitTermination(40_000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println("Problems, which can be ignored");
        }

    }

    private void sendData(String prefix, int total, int threadNumber) {
        try (DatagramSocket dSocket = new DatagramSocket()) {
            String returnString = "#";
            int len = dSocket.getReceiveBufferSize();
            DatagramPacket dataToRead = new DatagramPacket(new byte[len], len);
            dSocket.setSoTimeout(2_000);
            for (int i = 0; i < total; i++) {
                String currentData = String.format("%s%s_%s", prefix, threadNumber, i);
                byte[] currentDataBytes = currentData.getBytes(StandardCharsets.UTF_8);
                DatagramPacket toSend = new DatagramPacket(currentDataBytes, currentData.length(), inetSocketAddress);
                do {
                    try {
                        dSocket.send(toSend);
                        dSocket.receive(dataToRead);
                        returnString = new String(dataToRead.getData(), dataToRead.getOffset(), dataToRead.getLength());
                    } catch (IOException ignore) {
                        //System.out.println(e.getMessage());
                    }
                } while (!returnString.contains(currentData));
                //System.out.println(returnString);
            }
        } catch (SocketException e) {
            System.err.println("Socket Exception\n" + e.getMessage());
        }
    }
}
