package de.tu_clausthal.in.winf.objects.world;

import de.tu_clausthal.in.winf.objects.ICar;
import org.jxmapviewer.JXMapViewer;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 *
 */
public class CCarLayer extends IWorldLayer<ICar> {

    @Override
    public Queue<ICar> getAll() {
        return null;
    }

    @Override
    public ICar pop() {
        return null;
    }

    @Override
    public void push(ICar p_item) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<ICar> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean add(ICar iCar) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends ICar> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean offer(ICar iCar) {
        return false;
    }

    @Override
    public ICar remove() {
        return null;
    }

    @Override
    public ICar poll() {
        return null;
    }

    @Override
    public ICar element() {
        return null;
    }

    @Override
    public ICar peek() {
        return null;
    }

    @Override
    public void paint(Graphics2D graphics2D, JXMapViewer viewer) {

    }
}
