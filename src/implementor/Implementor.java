package ru.ifmo.rain.kuliev.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

/**
 * Code generating implementation of the {@code Impler} interface.
 * Capable of generating {@code .java} files for classes,
 * implementing the provided {@code Class} token using {@code Reflection API}.
 *
 * @author Aslan Kuliev
 * @see Impler
 * @see JarImpler
 * @see java.lang.reflect
 */
public class Implementor implements Impler {

    /**
     * Main method. A command line utility for {@code Implementor}.
     * If any arguments are invalid or an error occurs, execution stops
     * with describing message.
     *
     * @param args list of command line arguments
     * @see Impler
     * @see JarImpler
     * @see ImplerException
     */
    public static void main(String[] args) {
        Objects.requireNonNull(args);
        if (args.length < 2 || args.length > 3 || (args.length == 3 && !("--jar").equals(args[0]))) {
            System.out.println(("Invalid arguments"));
            return;
        }
        Objects.requireNonNull(args[0]);
        Objects.requireNonNull(args[1]);
        try {
            if (args.length == 2) {
                new JarImplementor().implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                new JarImplementor().implementJar(Class.forName(args[1]), Paths.get(args[2]));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid class: " + e.getMessage());
        } catch (InvalidPathException e) {
            System.out.println("Invalid specified path: " + e.getMessage());
        } catch (ImplerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Produces code implementing interface specified by provided {@code token}.
     * The generated {@code .java} fi location is specified by {@code root}.
     *
     * @throws ImplerException if can't implement token
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        new JarImplementor().implement(token, root);
    }
}