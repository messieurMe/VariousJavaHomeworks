package ru.ifmo.rain.kuliev.bank;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MyAccount extends Remote {
    /**
     * Returns id.
     * <p>
     * Returns id of account.
     *
     * @return id
     * @throws RemoteException if error occurs
     */
    String getID() throws RemoteException;

    /**
     * Returns short id.
     * <p>
     * Returns short id of account.
     *
     * @return short id
     * @throws RemoteException if error occurs
     */
    String getShortID() throws RemoteException;

    /**
     * Returns amount of money.
     * <p>
     * Returns amount of money of current account
     *
     * @return amount of money
     * @throws RemoteException if error occurs
     */
    long getAmount() throws RemoteException;

    /**
     * Sets amount.
     * <p>
     * Sets amount for this account.
     *
     * @param amount new amount
     * @throws RemoteException if error occurs
     */
    void setAmount(long amount) throws RemoteException;
}