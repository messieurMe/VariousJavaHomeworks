package ru.ifmo.rain.kuliev.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Walk {
    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Sorry, wrong input.\n" + "Please try again");
        } else {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                    String nextFile;
                    while ((nextFile = reader.readLine()) != null) {
                        try {
                            writer.write(String.format("%08x %s", countHash(Paths.get(nextFile)), nextFile));
                            writer.newLine();
                        } catch (InvalidPathException e) {
                            writer.write(String.format("%08x %s", 0, nextFile));
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("IOException in output file");
                    System.out.println(e.getMessage());
                } catch (SecurityException ex) {
                    System.out.println("Sorry, can't access file because of security problems");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Input file not found");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Input exception in input file");
                System.out.println(e.getMessage());
            }
        }
    }

    private static int countHash(Path input) {
        try (FileInputStream isReader = new FileInputStream(input.toString())) {
            int h = 0x811c9dc5;
            byte[] buff = new byte[1024];
            int newData;
            while ((newData = isReader.read(buff)) != -1) {
                for (int i = 0; i < newData; i++) {
                    h = ((h * 0x01000193) ^ (buff[i] & 0xff));
                }
            }
            return h;
        } catch (IOException e) {
            return 0;
        }
    }
}
