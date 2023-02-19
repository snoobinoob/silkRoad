package silkRoad.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListenerList<T> implements List<T> {
    private List<T> realList;
    private List<Runnable> modificationListeners;

    public ListenerList() {
        realList = new ArrayList<>();
        modificationListeners = new ArrayList<>();
    }

    public void addListener(Runnable listener) {
        modificationListeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : modificationListeners) {
            listener.run();
        }
    }

    @Override
    public String toString() {
        return realList.toString();
    }

    @Override
    public int size() {
        return realList.size();
    }

    @Override
    public boolean isEmpty() {
        return realList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return realList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return realList.iterator();
    }

    @Override
    public Object[] toArray() {
        return realList.toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return realList.toArray(a);
    }

    @Override
    public boolean add(T e) {
        boolean res = realList.add(e);
        notifyListeners();
        return res;
    }

    @Override
    public boolean remove(Object o) {
        boolean res = realList.remove(o);
        notifyListeners();
        return res;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return realList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean res = realList.addAll(c);
        notifyListeners();
        return res;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean res = realList.addAll(index, c);
        notifyListeners();
        return res;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean res = realList.removeAll(c);
        notifyListeners();
        return res;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean res = realList.retainAll(c);
        notifyListeners();
        return res;
    }

    @Override
    public void clear() {
        clear(true);
    }

    public void clear(boolean shouldNotify) {
        realList.clear();
        if (shouldNotify)
            notifyListeners();
    }

    @Override
    public T get(int index) {
        return realList.get(index);
    }

    @Override
    public T set(int index, T element) {
        T res = realList.set(index, element);
        notifyListeners();
        return res;
    }

    @Override
    public void add(int index, T element) {
        realList.add(index, element);
        notifyListeners();
    }

    @Override
    public T remove(int index) {
        T res = realList.remove(index);
        notifyListeners();
        return res;
    }

    @Override
    public int indexOf(Object o) {
        return realList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return realList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return realList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return realList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return realList.subList(fromIndex, toIndex);
    }
}
