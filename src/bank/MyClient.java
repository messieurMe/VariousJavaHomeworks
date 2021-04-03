package ru.ifmo.rain.kuliev.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

public class MyClient {
    public static void main(final String... args) throws RemoteException {
        final MyBank bank;
        try {
            bank = (MyBank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        }

        bank.createPerson("geo", "sGeo", "123");

        MyPerson lperson = bank.getPersonWithType("123", PersonTypes.LOCAL);
        MyPerson rperson = bank.getPersonWithType("123", PersonTypes.REMOTE);

        //lperson.addAccount("12");
        lperson.getAccount(12).setAmount(10L);
        System.out.println("Lperson: " + lperson.getAccount(12).getAmount());
        System.out.println("Rperson: " + rperson.getAccount(12).getAmount());

        System.out.println("\n");

        //rperson.addAccount("13");
        rperson.getAccount(13).setAmount(100L);
        System.out.println("Lperson: " + lperson.getAccount(13).getAmount());
        System.out.println("Rperson: " + rperson.getAccount(13).getAmount());

        MyPerson lperson1 = bank.getPersonWithType("123", PersonTypes.LOCAL);
        MyPerson rperson1 = bank.getPersonWithType("123", PersonTypes.REMOTE);
        //System.out.println(lperson1.getAccounts().toString());
        //System.out.println(rperson.getAccounts().toString());

        System.out.println("Lperson1 accs:");
        for (String i : lperson1.getAccounts().keySet()) {
            String[] array = i.split(":");
            System.out.println("Acc: " + i + "; Amount: " + lperson1.getAccount(Integer.parseInt(array[0])).getAmount());
        }


        System.out.println("Rperson1 accs:");
        for (String i : rperson1.getAccounts().keySet()) {
            String[] array = i.split(":");
            System.out.println("Acc: " + i + "; Amount: " + rperson1.getAccount(Integer.parseInt(array[0])).getAmount());
        }

    }
}
