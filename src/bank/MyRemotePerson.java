package ru.ifmo.rain.kuliev.bank;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class MyRemotePerson implements MyPerson {

    private String name;
    private String surname;
    private String passportID;
    protected final int port;
    private Map<String, MyAccountBase> accounts;

    public MyRemotePerson(String name, String surname, String passportID, int port) throws RemoteException {
        this.name = name;
        this.surname = surname;
        this.passportID = passportID;
        this.port = port;
        this.accounts = new ConcurrentHashMap<>();
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSurName() {
        return this.surname;
    }

    @Override
    public String getPasswordID() {
        return this.passportID;
    }


    @Override
    public MyAccount addAccount(int newID, long startAmount) throws RemoteException {
        if (accounts.containsKey(newID + ":" + passportID)) {
            System.out.println("Account exists" + newID + " " + passportID);
            return accounts.get(newID + ":" + passportID);
        }
        MyAccountBase its = new MyAccountBase(newID + ":" + passportID, startAmount);
        System.out.println("Creating account:" + newID + " " + passportID);
        UnicastRemoteObject.exportObject(its, port);
        accounts.put(newID + ":" + passportID, its);
        return its;
    }

    @Override
    public MyAccount getAccount(int accountID) throws RemoteException {
        if (accounts.containsKey(accountID + ":" + passportID)) {
            System.out.println("Contains account:" + accountID + " " + passportID);
            return accounts.get(accountID + ":" + passportID);
        }
        System.out.println("Creating account: " + accountID + " " + passportID);
        MyAccountBase its = new MyAccountBase(accountID + ":" + passportID, 0L);
        UnicastRemoteObject.exportObject(its, port);
        accounts.put(accountID + ":" + passportID, its);
        return its;
    }

    @Override
    public MyAccount getAccount(String accountID) throws RemoteException {
        String[] parts = accountID.split(":");
        return getAccount(Integer.parseInt(parts[parts.length - 1]));
    }

    @Override
    public Map<String, MyAccountBase> getAccounts() throws RemoteException {
        return accounts;
    }
}
