package ru.ifmo.rain.kuliev.bank;

import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class MyPersonTest1 {

    static MyAccount acc = new MyAccountBase("4", 5);
    static MyRemotePerson per;

    @Before
    public void main(){}

    @Before
    public void prepare() {
        try {
            per = new MyRemotePerson("1", "2", "3", 8888);
            per.addAccount(4, 5);
            //acc = per.getAccount(4);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getName() {
        assertEquals("1", per.getName());
    }

    @Test
    public void getSurName() {
        assertEquals("2", per.getSurName());
    }

    @Test
    public void getPasswordID() {
        assertEquals("3", per.getPasswordID());
    }

    @Test
    public void addAccount() throws RemoteException {
        assertEquals(1, per.getAccounts().size());
    }

    @Test
    public void getAccount() throws RemoteException {
        MyAccount ac = per.getAccount(3);
        assertEquals(String.valueOf(3), ac.getShortID());
    }

    @Test
    public void testGetAccount() throws RemoteException {
        assertNotEquals(null, per.getAccount(4));
    }

    @Test
    public void getAccounts() throws RemoteException {
        assertNotEquals(null, per.getAccounts());
    }


}
