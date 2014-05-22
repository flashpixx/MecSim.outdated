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

package de.tu_clausthal.in.winf.util;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * queue class *
 */
public class CQueue<T> implements IQueue<T> {

    /**
     * list of unprocessed sources *
     */
    protected ConcurrentLinkedQueue<T> m_unprocess = new ConcurrentLinkedQueue();
    /**
     * list of processed sources *
     */
    protected ConcurrentLinkedQueue<T> m_process = new ConcurrentLinkedQueue();


    @Override
    public Queue<T> getAll() {
        Queue<T> l_data = new ConcurrentLinkedQueue();
        l_data.addAll(m_unprocess);
        l_data.addAll(m_process);
        return l_data;
    }


    @Override
    public T pop() {
        return m_unprocess.poll();
    }


    @Override
    public void push(T p_item) {
        m_process.add(p_item);
    }


    @Override
    public boolean isEmpty() {
        return m_unprocess.isEmpty() && m_process.isEmpty();
    }


    @Override
    public synchronized void reset() {
        m_unprocess.addAll(m_process);
        m_process.clear();
    }


    @Override
    public int size() {
        return this.unprocessedsize() + this.processsize();
    }


    @Override
    public int unprocessedsize() {
        return m_unprocess.size();
    }


    @Override
    public int processsize() {
        return m_process.size();
    }


    @Override
    public boolean contains(T p_item) {
        return m_unprocess.contains(p_item) || m_process.contains(p_item);
    }


    @Override
    public void unprocessed2array(T[] p_array) {
        m_unprocess.toArray(p_array);
    }


    @Override
    public void process2array(T[] p_array) {
        m_process.toArray(p_array);
    }


    @Override
    public void add(T p_item) {
        m_unprocess.add(p_item);
    }


    @Override
    public void add(Collection<T> p_item) {
        m_unprocess.addAll(p_item);
    }


    @Override
    public synchronized void remove(T p_item) {
        m_unprocess.remove(p_item);
        m_process.remove(p_item);
    }


    @Override
    public synchronized void clear() {
        m_unprocess.clear();
        m_process.clear();
    }

}
