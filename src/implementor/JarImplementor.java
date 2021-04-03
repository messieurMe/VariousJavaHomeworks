package ru.ifmo.rain.kuliev.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.JarImpler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.util.*;
import java.io.File;
import java.nio.file.Path;
import java.lang.reflect.*;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.io.BufferedWriter;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.jar.Attributes;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.jar.JarOutputStream;
import java.nio.file.InvalidPathException;

/**
 * Code generating implementation of the {@code Impler} and {@code JarImpler} interfaces.
 * Capable of generating {@code .java} and {@code .jar} files for classes,
 * implementing the provided {@code Class} token using {@code Reflection API}.
 *
 * @author Aslan Kuliev
 * @see Impler
 * @see JarImpler
 * @see java.lang.reflect
 */
public class JarImplementor implements Impler, JarImpler {

    /**
     * Extension for generated {@code .java} files.
     */
    private static final String JAVA = ".java";
    /**
     * Extension for generated {@code .class} files.
     */
    private static final String CLASS = ".class";
    /**
     * System defined line separator for generated {@code .java} files.
     */
    private static final String LINE_SEP = System.lineSeparator();
    /**
     * Space code indentation for generated {@code .java} files.
     */
    private static final String SPACE = " ";
    /**
     * Command line option. When present, runs the application in {@code jar} mode.
     */
    private static final String JAR_MODE = "--jar";

    /**
     * Default constructor. Creates a new instance of {@code Implementor}.
     */
    public JarImplementor() {
    }

    /**
     * Main method. A command line utility for {@code Implementor}.
     * Supports two modes:
     * <ol>
     *     <li><b>java</b>: {@code <className> <outputPath>}.
     *     Creates a {@code .java} file by passing the arguments to {@link #implement(Class, Path)}.</li>
     *     <li><b>jar</b>: {@code -jar <className> <outputPath>}.
     *     Creates a {@code .jar} file by passing the arguments to {@link #implementJar(Class, Path)}.</li>
     * </ol>
     * If any arguments are invalid or an error occurs, execution stops
     * with describing message.
     *
     * @param args list of command line arguments
     * @see Impler
     * @see JarImpler
     * @see ImplerException
     *
     */
    public static void main(String[] args) {
        Objects.requireNonNull(args);
        if (args.length < 2 || args.length > 3 || (args.length == 3 && !JAR_MODE.equals(args[0]))) {
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
     * The generated {@code .java} file location is specified by {@code root}.
     *
     * @throws ImplerException if can't implement token
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        Objects.requireNonNull(token);
        Objects.requireNonNull(root);
        if (isUnimplementable(token)) {
            throw new ImplerException("Unable to implement token");
        }

        Path filePath;
        try {
            filePath = getFullPath(root, token);
        } catch (InvalidPathException e) {
            throw new ImplerException("Invalid output path", e);
        }

        createDirectory(filePath);
        try (BufferedWriter out = Files.newBufferedWriter(filePath)) {
            out.write(toUnicode(getClass(token)));
        } catch (IOException e) {
            throw new ImplerException("Error when working with output file: " + filePath, e);
        }
    }

    /**
     * Creates parent directory for {@code file}.
     * If directory exists, returns without errors,
     * if directory can't be created, throws {@link ImplerException},
     * if he able to create directory, creates.
     *
     * @param file file which parent directory will be created
     * @throws ImplerException if {@code file} hasn't path to parent
     */

    public static void createDirectory(Path file) throws ImplerException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new ImplerException("Cannot create directory: " + parent, e);
            }
        }
    }


    /**
     * Creates directory with prefix "jar-implementor" in {@code root} path.
     * If directory can't be created, throws {@link ImplerException},
     * else creates directory with prefix "jar-implementor"
     *
     * @param root path, in which directory will be created
     * @return  path to new directory
     * @throws ImplerException if can't create directory
     */
    public static Path createTempDirectory(Path root) throws ImplerException {
        if (root == null) {
            throw new ImplerException("Cannot create directory: ");
        }
        root = root.toAbsolutePath();
        try {
            return Files.createTempDirectory(root, "jar-implementor");
        } catch (IOException e) {
            throw new ImplerException("Could not create temporary directory");
        }
    }


    /**
     * Produces {@code .jar} file implementing interface specified by provided {@code token}.
     * The generated {@code .jar} file location is specified by {@code jarFile}.
     *
     * @throws ImplerException if can't implement jar
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Objects.requireNonNull(token);
        Objects.requireNonNull(jarFile);

        createDirectory(jarFile);
        Path temp = createTempDirectory(jarFile.toAbsolutePath().getParent());
        implement(token, temp);
        compile(token, temp);
        buildJar(token, jarFile, temp);
    }

    /**
     * Compiles the {@code token} implementation {@code .java} file.
     * Stores the resulting {@code .class} file at {@code temp}.
     *
     * @param token type token, the implementation of which is stored at {@code temp}
     * @param temp  working directory containing the source of {@code token} implementation
     * @throws ImplerException if an error occurs during compilation
     * @see JavaCompiler
     */
    private void compile(Class<?> token, Path temp) throws ImplerException {
        String tokenClassPath = getClassPath(token);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String[] args = {"-cp",
                temp.toString() + File.pathSeparator + tokenClassPath, getFullPath(temp, token).toString()};
        if (compiler == null || compiler.run(null, null, null, args) != 0) {
            throw new ImplerException("Failed to compile class");
        }
    }

    /**
     * Builds a {@code .jar} file containing compiled implementation of {@code token}.
     *
     * @param token   type token, the implementation of which is stored at {@code temp}
     * @param jarFile resulting {@code .jar} file destination
     * @param temp    directory containing the compiled {@code .class} files
     * @throws ImplerException if en error occurs when working with {@code .jar} file
     */
    private void buildJar(Class<?> token, Path jarFile, Path temp) throws ImplerException {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            String localName = getPackageDir(token, "/") + "/" + getClassName(token) + CLASS;
            out.putNextEntry(new ZipEntry(localName));
            Files.copy(temp.resolve(localName), out);
        } catch (IOException e) {
            throw new ImplerException("Error when working with jar file", e);
        }
    }

    /**
     * Generates full {@code token} implementation class source code.
     *
     * @param token the type token to be implemented
     * @return a {@link String} representation of {@code token} implementation
     * @see #getPackage(Class)
     * @see #getClassCode(Class)
     */
    private String getClass(Class<?> token) {
        return concat(Arrays.asList(getPackage(token), getClassCode(token)), Function.identity(), LINE_SEP + LINE_SEP);
    }

    /**
     * Returns the package declaration for specified {@code token}.
     *
     * @param token the type token
     * @return a {@code String} representing the package declaration of provided {@code token},
     * or an empty {@code String} if the package is default.
     */
    private String getPackage(Class<?> token) {
        String packageName = token.getPackageName();
        return packageName.isEmpty() ? "" : String.format("package %s;", packageName);
    }

    /**
     * Generates {@code token} implementation class content.
     * Returns a {@code String} representing of the {@code token} implementation,
     *
     * @param token the type token to be implemented
     * @return a {@code String} representation of {@code token} implementation contents
     */
    private String getClassCode(Class<?> token) {
        String classDeclaration = String.format("public class %s implements %s", getClassName(token),
                                                token.getCanonicalName());
        String classBody = concat(Collections.singletonList(getMethods(token)), Function.identity(), LINE_SEP + LINE_SEP);
        return declAndBody(classDeclaration, classBody);
    }


    /**
     * Generates {@code token} method implementations.
     * Finds all methods, and provides a default implementation using {@link #getMethod(Method)}.
     *
     * @param token the type token
     * @return a {@code String} representation of generated {@code abstract} methods
     * @see #getMethod(Method)
     */
    private String getMethods(Class<?> token) {
        List<Method> methods = new ArrayList<>(Arrays.asList(token.getMethods()));
        return concat(methods, this::getMethod, LINE_SEP + LINE_SEP);
    }

    /**
     * Generates {@code method} default implementation.
     * Uses {@link Method#getModifiers()}, {@link Method#getReturnType()}, {@link Method#getName()},
     * {@link Method#getParameters()}, and {@link #getDefaultValue(Class)} to generate the method.
     *
     * @param method the method
     * @return a {@code String} representation of the specified {@code method} implementation
     * @see #getDefaultValue(Class)
     */
    private String getMethod(Method method) {
        Class<?> returnType = method.getReturnType();
        String returnValue = getDefaultValue(returnType);
        String body = "return " + (returnValue.isEmpty() ? "" : " " + returnValue) + ";";
        return getFunction(method.getModifiers(), method.getParameters(), method.getExceptionTypes(), body,
                           returnType.getCanonicalName(), method.getName());
    }

    /**
     * Generates a {@code String} representation of a function, using
     * {@link #getFunctionDeclaration(int, Parameter[], Class[], String...)} and {@code body}.
     *
     * @param modifiers      modifiers for function
     * @param parameters     an array of {@link Parameter} objects, describing the function parameters
     * @param exceptionTypes an array of {@link Class} objects, describing exception types, thrown by the function
     * @param body           body of function
     * @param tokens         additional qualifiers inserted before the parameters
     *                       Usually contain the name and/or return type of the function
     * @return a {@code String} representation of the function
     * @see #getFunctionDeclaration(int, Parameter[], Class[], String...)
     * @see #declAndBody(String, String)
     */
    private String getFunction(int modifiers, Parameter[] parameters, Class<?>[] exceptionTypes, String body, String... tokens) {
        String declaration = getFunctionDeclaration(modifiers, parameters, exceptionTypes, tokens);
        return declAndBody(declaration, body);
    }

    /**
     * Generates a {@code String} representation of a function declaration,
     * using {@link #getAccessModifier(int)}, {@link #getParameters(Parameter[])} and {@link #getExceptions(Class[])}.
     *
     * @param modifiers      a set of modifiers
     * @param parameters     an array of {@code Parameter} objects, describing the function parameters
     * @param exceptionTypes an array of {@code Class} objects, describing exception types, thrown by the function
     * @param tokens         additional qualifiers inserted before the parameters.
     *                       Usually contains the name and/or return type of the function
     * @return a {@code String} representation of the function declaration
     * @see #getExceptions(Class[])
     */
    private String getFunctionDeclaration(int modifiers, Parameter[] parameters, Class<?>[] exceptionTypes, String... tokens) {
        return String.join(SPACE, Modifier.toString(getAccessModifier(modifiers)), String.join(SPACE, tokens)) +
               String.format("(%s)", getParameters(parameters)) +
               (getExceptions(exceptionTypes).isEmpty() ? "" : (" throws " + getExceptions(exceptionTypes)));
    }

    /**
     * Returns list of {@code parameters}, separated with comma.
     *
     * @param parameters an array of {@code Parameter} objects
     * @return a {@code String} representation of comma separated {@code parameters}
     * @see Parameter
     */
    private String getParameters(Parameter[] parameters) {
        StringBuilder result = new StringBuilder(" ");
        for (Parameter p : parameters) {
            result.append(p.getType().getCanonicalName()).append(" ").append(p.getName()).append(",");
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * Returns list of {@code exceptions}, separated with comma.
     *
     * @param exceptionTypes an array of exception types
     * @return a {@code String} representation of comma separated {@code exceptions}
     */
    private String getExceptions(Class<?>[] exceptionTypes) {
        return concat(Arrays.asList(exceptionTypes), Class::getCanonicalName, ", ");
    }

    /**
     * Returns the full path to {@code .java} file considering the {@code path} and {@code token} package.
     *
     * @param path  the base directory
     * @param token the type token to create implementation for
     * @return the full path to {@code token} implementation file
     */
    private Path getFullPath(Path path, Class<?> token) {
        return path.resolve(Path.of(getPackageDir(token, File.separator), getClassName(token) + JAVA));
    }

    /**
     * Returns a directory of the provided {@code token} package.
     *
     * @param token     the type token
     * @param separator the file separator
     * @return a {@code String} representation of the resulting directory
     */
    private String getPackageDir(Class<?> token, String separator) {
        return token.getPackageName().replace(".", separator);
    }

    /**
     * Returns the name of {@code token} implementation class with "Impl" at the end.
     *
     * @param token the type token
     * @return name of the class with "Impl" at the end
     */
    private String getClassName(Class<?> token) {
        return token.getSimpleName() + "Impl";
    }

    /**
     * Returns the default return value of an object of type {@code token}.
     * Returns {@code null} for non-primirive types, {@code 0} for numbers,
     * {@code false} for {@code boolean}, empty {@code String} for {@code void}
     *
     * @param token the type token
     * @return default return value for provided {@code token}
     */
    private String getDefaultValue(Class<?> token) {
        if (!token.isPrimitive()) {
            return "null";
        }
        if (token == boolean.class) {
            return "false";
        }
        if (token == void.class) {
            return "";
        }
        return "0";
    }

    /**
     * Returns the access modifier of {@code mod}.
     *
     * @param mod given number of modifier
     * @return a modifier, representing the access modifier of {@code mod}
     */
    private int getAccessModifier(int mod) {
        if (Modifier.isPrivate(mod)) {
            return Modifier.PRIVATE;
        }
        if (Modifier.isProtected(mod)) {
            return Modifier.PROTECTED;
        }
        if (Modifier.isPublic(mod)) {
            return Modifier.PUBLIC;
        }
        return 0;
    }

    /**
     * Returns {@code true} if the {@code token} cannot be implemented.
     * This is the case if the token is final or private
     *
     * @param token the type token
     * @return {@code true}, if the provided {@code token} cannot be implemented
     */
    private boolean isUnimplementable(Class<?> token) {
        int modifiers = token.getModifiers();
        return (Modifier.isFinal(modifiers) || Modifier.isPrivate(modifiers));
    }

    /**
     * Concatenates {@code declaration} and {@code body}.
     *
     * @param declaration the declaration of method
     * @param body        the body of method
     * @return the result of concatenation in {@code String}
     */
    private String declAndBody(String declaration, String body) {
        return declaration + " {" + LINE_SEP + body + "}";
    }



    /**
     * Returns {@code String} of given data.
     * Concatenates in one {@code String} blocks with {@code separator} between each block.
     * Applying {@code toString} to get value fromm {@code String} before further operations
     *
     * @param blocks    objects which will be separated
     * @param toString  mapping function for objects
     * @param separator the separator used between each element
     * @param <T>       the type of elements in {@code blocks} collection
     * @return separated {@code String} with applying {@code toString} function
     */
    private <T> String concat(Collection<T> blocks, Function<T, String> toString, String separator) {
        return blocks.stream().map(toString).collect(Collectors.joining(separator));
    }

    /**
     * Encodes the provided {@code String}, escaping all unicode characters in {@code \\u} notation.
     *
     * @param s the {@code String} to be encoded
     * @return the encoded {@code String}
     */
    private String toUnicode(String s) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            if (c < 128) {
                sb.append(c);
            } else {
                sb.append("\\u").append(String.format("%04x", (int) c));
            }
        }
        return sb.toString();
    }

    /**
     * Returns path to class
     *
     * @param token class token
     * @return path to {@code token} class
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

}