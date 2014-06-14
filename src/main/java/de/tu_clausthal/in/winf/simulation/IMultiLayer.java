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

package de.tu_clausthal.in.winf.simulation;

import de.tu_clausthal.in.winf.ui.IViewLayer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * multilayer to create a collection for elements
 * offer, poll, peek operates only of the queue
 * add, remove, element operates on the painter and on the queue
 */
public abstract class IMultiLayer<T extends Painter> extends CompoundPainter<T> implements IQueue<T>, IViewLayer, IDataLayer, ISimulationLayer, IVoidStepable {

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
        return m_visible;
    }

    @Override
    public void setVisible(boolean p_visible) {
        m_visible = p_visible;
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }


    @Override
    public synchronized boolean add(T t) {
        super.addPainter(t);
        return m_process.add(t);
    }

    @Override
    public synchronized T remove() {
        if (!m_active)
            return null;

        T l_item = m_unprocess.remove();
        super.removePainter(l_item);
        return l_item;
    }

    @Override
    public T element() {
        if (!m_active)
            return null;

        return m_unprocess.element();
    }

    @Override
    public boolean offer(T t) {
        return m_process.offer(t);
    }

    @Override
    public T poll() {
        if (!m_active)
            return null;

        return m_unprocess.poll();
    }

    @Override
    public T peek() {
        if (!m_active)
            return null;

        return m_unprocess.peek();
    }

    public void step(int p_currentstep) {
    }


    @Override
    public synchronized void reset() {
        m_unprocess.addAll(m_process);
        m_process.clear();
    }

    @Override
    protected void doPaint(Graphics2D g, T component, int width, int height) {
        if (!m_visible)
            return;

        super.doPaint(g, component, width, height);
    }
}
