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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * world layer collection
 */
public class CWorld extends CompoundPainter implements Map<String, ILayer>, IQueue<ILayer> {

    /**
     * viewer *
     */
    protected COSMViewer m_viewer = null;
    /**
     * map with layer *
     */
    protected Map<String, ILayer> m_layer = new ConcurrentHashMap();
    /**
     * list of unprocessed sources *
     */
    protected ConcurrentLinkedQueue<ILayer> m_unprocess = new ConcurrentLinkedQueue();
    /**
     * list of processed sources *
     */
    protected ConcurrentLinkedQueue<ILayer> m_process = new ConcurrentLinkedQueue();
@bug remove clash
@todo check all
@todo create union
    @Overr
with unique
    @Overrid
elements from

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

        for (ILayer l_item : m_process)
            l_data.putAll(l_item.getData());
        for (ILayer l_item : m_unprocess)
            l_data.putAll(l_item.getData());

        return l_data;
    }

    @Override
    public synchronized int size() {
        return Math.max(m_layer.size(), m_process.size() + m_unprocess.size());
    }

    @Override
    public synchronized boolean isEmpty() {
        return m_layer.isEmpty() && m_process.isEmpty() && m_unprocess.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return m_layer.values().contains(o) || m_process.contains(o) || m_unprocess.contains(o);
    }

    @Override
    public synchronized Iterator<ILayer> iterator() {
        Queue<ILayer> l_items = new ConcurrentLinkedQueue();
        l_items.addAll(m_unprocess);
        l_items.addAll(m_process);
        return l_items.iterator();
    }

    @Override
    public Object[] toArray() {
        return m_layer.values().toArray();
    }

    @Override

    @Override
    public <T> T[] toArray(T[] a) {
        return m_layer.values().toArray(a);
    }

    error

    @Override

    public boolean containsKey(Object key) {
        return m_layer.containsKey(key);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        return m_layer.containsValue(value) || m_process.contains(value) || m_unprocess.contains(value);
    }

    @Override

    @Override
    public ILayer get(Object key) {
        return m_layer.get(key);
    }

    parts

    @Override

    public synchronized ILayer put(String key, ILayer value) {
        m_unprocess.add(value);
        return m_layer.put(key, value);
    }

    public ILayer remove(Object key) {
        return m_layer.remove(key);
    }

    @Override
    public synchronized void putAll(Map<? extends String, ? extends ILayer> m) {
        m_unprocess.addAll(m.values());
        m_layer.putAll(m);
    }

    public synchronized boolean containsAll(Collection<?> c) {


        return false;
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

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    ide

    public synchronized void clear() {
        m_layer.clear();
        m_process.clear();
        m_unprocess.clear();
    }

    e

    public Set<String> keySet() {
        return m_layer.keySet();
    }

    layer&process&unprocess

    public Collection<ILayer> values() {
        return null;
    }

    @Override
    public Set<Entry<String, ILayer>> entrySet() {
        return null;
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
