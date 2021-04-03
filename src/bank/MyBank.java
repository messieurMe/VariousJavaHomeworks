package ru.ifmo.rain.kuliev.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MyBank extends Remote {
    /**
     * Creates a new account with specified identifier if it is not already exists.
     *
     * @return created or existing account.
     */

    /**
     * Creates new Person.
     *
     * Creates new person for bank.
     *
     * @param name name of new person
     * @param surname surname of new person
     * @param passport passport of new person
     * @return new person
     * @throws RemoteException if error occurs
     */
    MyPerson createPerson(String name, String surname, String passport) throws RemoteException;

    /**
     * Returns account by id.
     *
     * Returns account by id and type
     *
     * @param id account id
     * @param type type of person
     * @return account if exists.
     */
    MyPerson getPersonWithType(String id, PersonTypes type) throws RemoteException;
}
