package ru.ifmo.rain.kuliev.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> data;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        comparator = null;
        data = Collections.emptyList();
    }

    public ArraySet(Collection<? extends T> other) {
        this(other, null);
    }

    public ArraySet(Comparator<? super T> cmp) {
        data = Collections.emptyList();
        comparator = cmp;
    }

    public ArraySet(Collection<? extends T> other, Comparator<? super T> cmp) {
        comparator = cmp;
        TreeSet<T> tmp = new TreeSet<>(cmp);
        tmp.addAll(other);
        data = new ArrayList<>(tmp);
    }

    private ArraySet(List<T> arr, Comparator<? super T> cmp) {
        comparator = cmp;
        data = arr;
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    private boolean indexIsCorrect(int i) {
        return (0 <= i) && (i <= size());
    }

    private int getIndex(T element) {
        int res = Collections.binarySearch(data, Objects.requireNonNull(element), comparator);
        if (res < 0) {
            res = -res - 1;
        }
        if (indexIsCorrect(res)) {
            return res;
        }
        return -1;
    }


    private SortedSet<T> subSet(T first, T second, boolean include) {
        int left = getIndex(first);
        int right = getIndex(second);
        if (right >= 0 && include) {
            right++;
        }
        if (left >= right || left == -1 || right == -1) {
            return new ArraySet<>(comparator);
        }
        return new ArraySet<>(data.subList(left, right), comparator);
    }


    private int compare(T first, T second) {
        return comparator() == null ? ((Comparable<? super T>) first).compareTo(second) : comparator().compare(first, second);
    }


    @Override
    public SortedSet<T> subSet(T first, T second) {
        if (compare(first, second) > 0) {
            throw new IllegalArgumentException("First element is greater than second element");
        }
        return subSet(first, second, false);
    }


    @Override
    public SortedSet<T> headSet(T second) {
        if (data.isEmpty()) {
            return new ArraySet<T>(comparator);
        }
        return subSet(first(), second, false);
    }

    @Override
    public SortedSet<T> tailSet(T second) {
        if (data.isEmpty()) {
            return new ArraySet<T>(comparator);
        }
        return subSet(second, last(), true);
    }

    @Override
    public T first() {
        if (!data.isEmpty()) {
            return data.get(0);
        }
        throw new NoSuchElementException();
    }

    @Override
    public T last() {
        if (!data.isEmpty()) {
            return data.get(size() - 1);
        }
        throw new NoSuchElementException();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(data, (T) o, comparator) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }
}