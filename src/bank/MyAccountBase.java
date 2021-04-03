package ru.ifmo.rain.kuliev.bank;

import java.io.Serializable;
import java.rmi.RemoteException;

public class MyAccountBase implements MyAccount, Serializable {
    String id;
    long amount;

    MyAccountBase(String id, long amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getShortID() throws RemoteException {
        String[] shortID = this.id.split(":");
        return shortID[shortID.length - 1];
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(long amount) {
        this.amount = amount;
    }
}
