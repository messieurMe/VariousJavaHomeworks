package ru.ifmo.rain.kuliev.bank;

import java.util.Map;
import java.rmi.Remote;
import java.util.Comparator;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentMap;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class MyRemoteBank implements MyBank, Remote {
    private final int port;
    private final Map<String, MyRemotePerson> persons = new ConcurrentHashMap<>();
    private final ConcurrentSkipListSet<String> passportIDs = new ConcurrentSkipListSet<>(Comparator.naturalOrder());

    public MyRemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public MyPerson getPersonWithType(String id, PersonTypes type) throws RemoteException {
        if (!persons.containsKey(id)) {
            return null;
        }
        System.out.println("Getting person:");
        System.out.println(persons.get(id).getAccounts().size() + " Keys: " + persons.get(id).getAccounts().keySet());

        switch (type) {
            case REMOTE:
                return persons.get(id);
            case LOCAL:
                MyPerson person = persons.get(id);
                return new MyLocalPerson(person.getName(), person.getSurName(), person.getPasswordID(),
                                         person.getAccounts());
            default:
                return null;
        }
    }

    @Override
    public MyPerson createPerson(String name, String surname, String passportID) throws RemoteException {
        if (persons.containsKey(passportID)) {
            System.out.println("Person exists");
            return persons.get(passportID);
        }
        System.out.println("Creating person");
        MyRemotePerson rp = new MyRemotePerson(name, surname, passportID, port);
        persons.put(passportID, rp);
        UnicastRemoteObject.exportObject(rp, port);
        //passportIDs.add(passportID);
        System.out.println("After creating:");
        System.out.println(persons.get(passportID).getAccounts().size() + " Keys: " +
                           persons.get(passportID).getAccounts().keySet());
        return rp;
    }
}
