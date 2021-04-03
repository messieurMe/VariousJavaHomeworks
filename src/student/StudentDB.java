package ru.ifmo.rain.kuliev.student;


import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;

public class StudentDB implements StudentGroupQuery {

    private Comparator<Student> studentNameSortir = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getId);

    private Stream<Map.Entry<String, List<Student>>> getStream(Collection<Student> students) {
        return students
                .stream()
                .collect(Collectors.groupingBy(Student::getGroup))
                .entrySet()
                .stream();
    }


    private List<Group> getSortedGroups(Collection<Student> students, Comparator<Student> comparator) {
        return getStream(students)
                .map(entries -> new Group(entries.getKey(),
                                          getSortedStudents(entries.getValue(), comparator))
                )
                .sorted(Comparator.comparing(Group::getName))
                .collect(Collectors.toList());
    }

    private String getLargest(Collection<Student> students, Comparator<Group> comparator) {
        return getStream(students)
                .map(entries -> new Group(entries.getKey(), entries.getValue()))
                .max(comparator)
                .map(Group::getName)
                .orElse("");
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getSortedGroups(students, studentNameSortir);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getSortedGroups(students, Comparator.comparing(Student::getId));
    }

    @Override
    public String getLargestGroup(Collection<Student> students) {
        return getLargest(students,
                          Comparator.<Group, Integer>comparing(group -> group.getStudents().size())
                                  .thenComparing(Comparator.comparing(Group::getName).reversed()));
    }

    @Override
    public String getLargestGroupFirstName(Collection<Student> students) {
        return getLargest(students,
                          Comparator.<Group, Integer>comparing(
                                  group -> getDistinctFirstNames(group.getStudents()).size())
                                  .thenComparing(Comparator.comparing(Group::getName).reversed()));
    }

    private static List<String> getListOfStudents(List<Student> students, Function<Student, String> function) {
        return students
                .stream()
                .map(function)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getListOfStudents(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getListOfStudents(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return getListOfStudents(students, Student::getGroup);
    }


    @Override
    public List<String> getFullNames(List<Student> students) {
        return getListOfStudents(students, s -> String.join(" ", s.getFirstName(), s.getLastName()));
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students
                .stream()
                .map(Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students
                .stream()
                .min(Comparator.comparing(Student::getId))
                .map(Student::getFirstName)
                .orElse("");
    }


    private List<Student> getSortedStudents(Collection<Student> students, Comparator<Student> comparator) {
        return students
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return getSortedStudents(students, Comparator.comparingInt(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return getSortedStudents(students, studentNameSortir);
    }

    private List<Student> getFilteredStudents(Collection<Student> students, Predicate<Student> predicate) {
        return students
                .stream()
                .filter(predicate)
                .sorted(studentNameSortir)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return getFilteredStudents(students, s -> s.getFirstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return getFilteredStudents(students, s -> s.getLastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return getFilteredStudents(students, s -> s.getGroup().equals(group));
    }


    private Map<String, String> getMappedStudents(Collection<Student> students, Predicate<Student> predicate) {
        return students
                .stream()
                .filter(predicate)
                .collect(
                        Collectors.toMap(
                                Student::getLastName,
                                Student::getFirstName,
                                BinaryOperator.minBy(String::compareTo)

                        )
                );
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return getMappedStudents(students, s -> s.getGroup().equals(group));
    }

    @Override
    public List<Map.Entry<String, String>> findStudentNamesByGroupList(List<Student> students, String group) {
        return findStudentNamesByGroup(students, group)
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }
}
