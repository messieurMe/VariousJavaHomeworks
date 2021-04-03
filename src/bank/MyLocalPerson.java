package ru.ifmo.rain.kuliev.bank;

import java.util.Map;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class MyLocalPerson implements Serializable, MyPerson {
    private String name;
    private String surname;
    private String passportID;
    private Map<String, MyAccountBase> accounts = new ConcurrentHashMap<>();


    public MyLocalPerson(String name, String surname, String passportID, Map<String, MyAccountBase> accountsq) {
        this.name = name;
        this.surname = surname;
        this.passportID = passportID;
        for (String i : accountsq.keySet()) {
            this.accounts.put(i, accountsq.get(i));
        }
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
    public MyAccount addAccount(int newID, long startAmount) {
        if (accounts.containsKey(newID + ":" + passportID)) {
            return accounts.get(newID + ":" + passportID);
        }
        MyAccountBase mb = new MyAccountBase(newID + ":" + passportID, startAmount);
        return accounts.put(newID + ":" + passportID, mb);
    }

    @Override
    public MyAccount getAccount(int accountID) {
        if (!accounts.containsKey(accountID + ":" + passportID)) {
            MyAccountBase mb = new MyAccountBase(accountID + ":" + passportID, 0L);
            accounts.put(accountID + ":" + passportID, mb);
        }
        return accounts.get(accountID + ":" + passportID);
    }

    @Override
    public MyAccount getAccount(String accountID) {
        String[] parts = accountID.split(":");
        return getAccount(Integer.parseInt(parts[parts.length - 1]));
    }


    @Override
    public Map<String, MyAccountBase> getAccounts() throws RemoteException {
        return accounts;
    }
}
