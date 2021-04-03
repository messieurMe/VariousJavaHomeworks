package ru.ifmo.rain.kuliev.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MyServer {
    private final static int PORT = 8888;
    public static void main(final String... args) {
        final MyBank bank = new MyRemoteBank(PORT);
        try {
            UnicastRemoteObject.exportObject(bank, PORT);
            Naming.rebind("//localhost/bank", bank);
        } catch (final RemoteException e) {
            System.out.println("Cannot export object: " + e.getMessage());
            e.printStackTrace();
        } catch (final MalformedURLException e) {
            System.out.println("Malformed URL");
        }
        System.out.println("Server started");
    }
}
