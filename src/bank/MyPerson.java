package ru.ifmo.rain.kuliev.bank;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface MyPerson extends Remote, Serializable {

    /**
     * Returns name.
     *
     * Returns name of current person.
     *
     * @return name of Person
     * @throws RemoteException if error occurs
     */
    String getName() throws RemoteException;

    /**
     * Returns surname.
     *
     * Returns surname of current person.
     *
     * @return name of Person
     * @throws RemoteException if error occurs
     */
    String getSurName() throws RemoteException;

    /**
     * Returns password id.
     *
     * Returns password id of current person.
     *
     * @return password id of Person
     * @throws RemoteException if error occurs
     */
    String getPasswordID() throws RemoteException;

    /**
     * Creates new account.
     *
     * Creates new account for current person. If account exists, returns it.
     *
     * @param newID {@link Integer} number of new account
     * @param startAmount default number of money at this account
     * @return new account
     * @throws RemoteException if error occurs
     */
    MyAccount addAccount(int newID, long startAmount) throws RemoteException;

    /**
     * Returns account.
     *
     * Returns account by short id. If there is no account with this ID, creates new one.
     *
     * @param accountID id
     * @return account by id
     * @throws RemoteException if error occurs
     */
    MyAccount getAccount(int accountID) throws RemoteException;

    /**
     * Returns account.
     *
     * Returns account by long id. If there is no account with this ID, creates new one.
     *
     * @param accountID id
     * @return account by id
     * @throws RemoteException if error occurs
     */
    MyAccount getAccount(String accountID) throws RemoteException;

    /**
     * Returns all accounts.
     *
     * Returns all accounts of current person.
     *
     * @return accounts of current person
     * @throws RemoteException if error occurs
     */
    Map<String, MyAccountBase> getAccounts() throws RemoteException;

}
