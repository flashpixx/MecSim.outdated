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

import de.tu_clausthal.in.winf.CBootstrap;
import de.tu_clausthal.in.winf.simulation.IQueue;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.painter.CompoundPainter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * world layer collection
 */
public class CWorld extends CompoundPainter {


    /**
     * viewer *
     */
    protected COSMViewer m_viewer = null;
    /**
     * map with layer *
     */
    protected CMap m_layer = new CMap();
    /**
     * queue with layer
     */
    protected CQueue m_queue = new CQueue();

    /**
     * ctor
     *
     * @param p_viewer viewer
     */
    public CWorld(COSMViewer p_viewer) {
        m_viewer = p_viewer;
        m_viewer.getCompoundPainter().addPainter(this);
        CBootstrap.AfterWorldInit(this);
    }

    /**
     * returns map data
     *
     * @return map
     */
    public Map<String, IDataLayer> getMap() {
        return m_layer;
    }

    /**
     * returns queue data *
     */
    public Queue<IDataLayer> getQueue() {
        return m_queue;
    }

    /**
     * returns a map with all layer data *
     */
    public synchronized Map<String, Object> getData() {
        Map<String, Object> l_data = new HashMap();

        for (IDataLayer l_item : m_queue)
            l_data.putAll(l_item.getData());

        return l_data;
    }

    /**
     * internal queue representation of the data *
     */
    protected class CQueue implements IQueue<IDataLayer> {

        /**
         * list of unprocessed sources *
         */
        protected ConcurrentLinkedQueue<IDataLayer> m_unprocess = new ConcurrentLinkedQueue();
        /**
         * list of processed sources *
         */
        protected ConcurrentLinkedQueue<IDataLayer> m_process = new ConcurrentLinkedQueue();

        @Override
        public synchronized void reset() {
            m_unprocess.addAll(m_process);
            m_process.clear();
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
            return m_process.contains(o) || m_unprocess.contains(o);
        }

        @Override
        public synchronized Iterator<IDataLayer> iterator() {
            Queue<IDataLayer> l_data = new LinkedList();
            l_data.addAll(m_unprocess);
            l_data.addAll(m_process);
            return l_data.iterator();
        }

        @Override
        public synchronized Object[] toArray() {
            Queue<IDataLayer> l_data = new LinkedList();
            l_data.addAll(m_unprocess);
            l_data.addAll(m_process);
            return l_data.toArray();
        }

        @Override
        public synchronized <T> T[] toArray(T[] a) {
            Queue<IDataLayer> l_data = new LinkedList();
            l_data.addAll(m_unprocess);
            l_data.addAll(m_process);
            return l_data.toArray(a);
        }

        @Override
        public synchronized boolean remove(Object o) {
            m_layer.values().remove(o);
            return m_process.remove(o) || m_unprocess.remove(o);
        }

        @Override
        public synchronized boolean containsAll(Collection<?> c) {
            Queue<IDataLayer> l_data = new LinkedList();
            l_data.addAll(m_unprocess);
            l_data.addAll(m_process);
            return l_data.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends IDataLayer> c) {
            return m_process.addAll(c);
        }

        @Override
        public synchronized boolean removeAll(Collection<?> c) {
            for (Object l_item : c) {
                if (m_process.remove(l_item))
                    continue;
                if (m_unprocess.remove(l_item))
                    continue;

                return false;
            }

            return true;
        }

        @Override
        public synchronized boolean retainAll(Collection<?> c) {
            Queue<IDataLayer> l_data = new LinkedList();
            l_data.addAll(m_unprocess);
            l_data.addAll(m_process);
            l_data.retainAll(c);

            ConcurrentLinkedQueue<IDataLayer> l_newprocess = new ConcurrentLinkedQueue();
            ConcurrentLinkedQueue<IDataLayer> l_newunprocess = new ConcurrentLinkedQueue();
            for (IDataLayer l_item : l_data) {
                if (m_process.contains(l_item)) {
                    l_newprocess.add(l_item);
                    continue;
                }
                if (m_unprocess.contains(l_item)) {
                    l_newunprocess.add(l_item);
                    continue;
                }

                return false;
            }
            m_process = l_newprocess;
            m_unprocess = l_newunprocess;

            return true;
        }

        @Override
        public synchronized void clear() {
            m_process.clear();
            m_unprocess.clear();
        }

        @Override
        public boolean add(IDataLayer iDataLayer) {
            return m_process.add(iDataLayer);
        }

        @Override
        public boolean offer(IDataLayer iDataLayer) {
            return m_process.offer(iDataLayer);
        }

        @Override
        public IDataLayer remove() {
            return m_unprocess.remove();
        }

        @Override
        public IDataLayer poll() {
            return m_unprocess.poll();
        }

        @Override
        public IDataLayer element() {
            return m_unprocess.element();
        }

        @Override
        public IDataLayer peek() {
            return m_unprocess.peek();
        }
    }

    /**
     * internal map representation of the data *
     */
    protected class CMap implements Map<String, IDataLayer> {

        protected Map<String, IDataLayer> m_layer = new ConcurrentHashMap();

        @Override
        public int size() {
            return m_layer.size();
        }

        @Override
        public boolean isEmpty() {
            return m_layer.isEmpty();
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
        public IDataLayer get(Object key) {
            return m_layer.get(key);
        }

        @Override
        public IDataLayer put(String key, IDataLayer value) {
            m_queue.add(value);
            return m_layer.put(key, value);
        }

        @Override
        public IDataLayer remove(Object key) {
            IDataLayer l_layer = m_layer.remove(key);
            if (l_layer != null)
                m_queue.remove(l_layer);
            return l_layer;
        }

        @Override
        public void putAll(Map<? extends String, ? extends IDataLayer> m) {
            m_layer.putAll(m);
            for (IDataLayer l_layer : m.values())
                m_queue.add(l_layer);
        }

        @Override
        public synchronized void clear() {
            m_queue.clear();
            m_layer.clear();
        }

        @Override
        public Set<String> keySet() {
            return m_layer.keySet();
        }

        @Override
        public Collection<IDataLayer> values() {
            return m_layer.values();
        }

        @Override
        public Set<Entry<String, IDataLayer>> entrySet() {
            return m_layer.entrySet();
        }
    }

}
