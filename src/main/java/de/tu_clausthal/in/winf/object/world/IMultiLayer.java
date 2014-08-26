/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.object.world;

import de.tu_clausthal.in.winf.simulation.IQueue;
import de.tu_clausthal.in.winf.simulation.IStepable;
import de.tu_clausthal.in.winf.simulation.IVoidStepable;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import de.tu_clausthal.in.winf.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * multilayer to create a collection for elements
 * offer, poll, peek operates only of the queue
 * add, remove, element operates on the painter and on the queue
 */
public abstract class IMultiLayer<T extends IStepable & Painter> implements Painter<COSMViewer>, IQueue<T>, IViewableLayer, IDataLayer, IVoidStepable, ILayer {

    /**
     * flag for visibility *
     */
    protected boolean m_visible = true;
    /**
     * flag for activity *
     */
    protected boolean m_active = true;
    /**
     * list of unprocessed sources *
     */
    protected ConcurrentLinkedQueue<T> m_unprocess = new ConcurrentLinkedQueue();
    /**
     * list of processed sources *
     */
    protected ConcurrentLinkedQueue<T> m_process = new ConcurrentLinkedQueue();

    @Override
    public boolean isActive() {
        return m_active;
    }

    @Override
    public void setActive(boolean p_active) {
        m_active = p_active;
    }

    @Override
    public boolean isVisible() {
        COSMViewer.getInstance().repaint();
        return m_visible;
    }

    @Override
    public void setVisible(boolean p_visible) {
        m_visible = p_visible;
        COSMViewer.getInstance().repaint();
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }


    @Override
    public synchronized boolean add(T t) {
        boolean l_return = m_process.add(t);
        COSMViewer.getInstance().repaint();

        return l_return;
    }

    @Override
    public synchronized T remove() {
        T l_item = m_unprocess.remove();
        COSMViewer.getInstance().repaint();

        return l_item;
    }

    @Override
    public T element() {
        return m_unprocess.element();
    }

    @Override
    public boolean offer(T t) {
        return m_process.offer(t);
    }

    @Override
    public T poll() {
        return m_unprocess.poll();
    }

    @Override
    public T peek() {
        return m_unprocess.peek();
    }

    @Override
    public synchronized void step(int p_currentstep, ILayer p_layer) {
    }

    /**
     * method which is called before the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object      object
     */
    public synchronized void beforeStepObject(int p_currentstep, T p_object) {
    }


    /**
     * method which is called after the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object      object
     */
    public synchronized void afterStepObject(int p_currentstep, T p_object) {
    }


    @Override
    public synchronized int size() {
        return m_process.size() + m_unprocess.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return m_process.isEmpty() && m_unprocess.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return m_unprocess.contains(o) || m_process.contains(o);
    }

    @Override
    public synchronized Iterator<T> iterator() {
        Queue<T> l_data = new LinkedList();
        l_data.addAll(m_unprocess);
        l_data.addAll(m_process);
        return l_data.iterator();
    }

    @Override
    public synchronized Object[] toArray() {
        Queue<T> l_data = new LinkedList();
        l_data.addAll(m_unprocess);
        l_data.addAll(m_process);
        return l_data.toArray();
    }

    @Override
    public synchronized <S> S[] toArray(S[] a) {
        Queue<T> l_data = new LinkedList();
        l_data.addAll(m_unprocess);
        l_data.addAll(m_process);
        return l_data.toArray(a);
    }

    @Override
    public synchronized boolean remove(Object o) {
        return m_process.remove(o) || m_unprocess.remove(o);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        for (Object l_item : c)
            if ((!m_unprocess.contains(l_item)) && (!m_process.contains(l_item))) {
                COSMViewer.getInstance().repaint();
                return false;
            }

        COSMViewer.getInstance().repaint();
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean l_return = m_process.addAll(c);
        COSMViewer.getInstance().repaint();

        return l_return;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        for (Object l_item : c) {
            if ((m_process.remove(l_item)) || (m_unprocess.remove(l_item)))
                continue;
            COSMViewer.getInstance().repaint();
            return false;
        }

        COSMViewer.getInstance().repaint();
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return m_process.retainAll(c) || m_unprocess.retainAll(c);
    }

    @Override
    public synchronized void clear() {
        m_unprocess.clear();
        m_process.clear();

        COSMViewer.getInstance().repaint();
    }


    @Override
    public synchronized void reset( boolean p_reset ) {
        if (!p_reset)
            return;

        m_unprocess.addAll(m_process);
        m_process.clear();
        COSMViewer.getInstance().repaint();
    }

    @Override
    public void paint(Graphics2D g, COSMViewer object, int width, int height) {
        if (!m_visible)
            return;

        Rectangle l_viewportBounds = object.getViewportBounds();
        g.translate(-l_viewportBounds.x, -l_viewportBounds.y);
        for (T l_item : this)
            l_item.paint(g, object, width, height);
    }

    @Override
    public void resetData() {

    }

}
