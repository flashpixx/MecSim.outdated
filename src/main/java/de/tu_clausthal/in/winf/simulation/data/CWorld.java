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

package de.tu_clausthal.in.winf.simulation.data;


import de.tu_clausthal.in.winf.simulation.process.IQueue;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.painter.CompoundPainter;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * world layer collection
 */
public class CWorld<T> extends CompoundPainter<T> implements Map<String, ILayer>, IQueue<ILayer> {

    /**
     * viewer *
     */
    protected COSMViewer m_viewer = null;
    /**
     * map with layer *
     */
    protected Map<String, ILayer> m_layer = new HashMap();
    /**
     * list of unprocessed sources *
     */
    protected ConcurrentLinkedQueue<ILayer> m_unprocess = new ConcurrentLinkedQueue();
    /**
     * list of processed sources *
     */
    protected ConcurrentLinkedQueue<ILayer> m_process = new ConcurrentLinkedQueue();


    /**
     * ctor
     *
     * @param p_viewer viewer
     */
    public CWorld(COSMViewer p_viewer) {
        m_viewer = p_viewer;
        m_viewer.getCompoundPainter().addPainter(this);
    }

    /**
     * returns a map with all layer data *
     */
    public synchronized Map<String, Object> getData() {
        Map<String, Object> l_data = new HashMap();

        for (Map.Entry<String, IMultiLayer> l_item : m_layer.entrySet())
            l_data.putAll(l_item.getValue().getData());

        return l_data;
    }


    @Override
    public int size() {
        return m_layer.size();
    }

    @Override
    public boolean isEmpty() {
        return m_layer.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<ILayer> iterator() {
        return null;
    }


    @Override
    public Object[] toArray() {
        return m_layer.values().toArray()
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return m_layer.values().toArray(a);
    }

    @Override
    public boolean containsKey(Object key) {
        return m_layer.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return m_layer.containsValue(value);
    }

    @Override
    public synchronized ILayer get(Object key) {
        return m_layer.get(key);
    }

    @Override
    public synchronized ILayer put(String key, ILayer value) {
        m_unprocess.add(value)
        return m_layer.put(key, value);
    }

    @Override
    public ILayer remove(Object key) {
        return null;
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return m_layer.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ILayer> c) {
        return m_unprocess.addAll(c);
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
    public synchronized void putAll(Map<? extends String, ? extends ILayer> m) {
        m_layer.putAll(m);
        m_unprocess.addAll(m.values());
    }

    @Override
    public synchronized void clear() {
        m_layer.clear();
        m_process.clear();
        m_unprocess.clear();
    }

    @Override
    public Set<String> keySet() {
        return m_layer.keySet();
    }

    @Override
    public Collection<ILayer> values() {
        return m_layer.values();
    }

    @Override
    public Set<Entry<String, ILayer>> entrySet() {
        return m_layer.entrySet();
    }

    @Override
    public Queue<ILayer> getAll() {
        return new Queue(m_layer.values());
    }

    @Override
    public synchronized void reset() {
        m_unprocess.addAll(m_process);
        m_process.clear();
    }

    @Override
    public boolean add(ILayer iLayer) {
        return m_process.add(iLayer);
    }

    @Override
    public boolean offer(ILayer iLayer) {
        return m_process.offer(iLayer);
    }

    @Override
    public synchronized ILayer remove() {
        return m_unprocess.remove();
    }

    @Override
    public ILayer poll() {
        return m_unprocess.poll();
    }

    @Override
    public ILayer element() {
        return m_unprocess.element();
    }

    @Override
    public ILayer peek() {
        return m_unprocess.peek();
    }
}
