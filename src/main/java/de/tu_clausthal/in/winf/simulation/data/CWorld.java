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


import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.painter.CompoundPainter;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * world layer collection
 */
public class CWorld<T> extends CompoundPainter<T> implements Map<String, IMultiLayer> {

    /**
     * viewer *
     */
    protected COSMViewer m_viewer = null;
    /**
     * map with layer *
     */
    protected Map<String, IMultiLayer> m_layer = new HashMap();


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
    public boolean containsKey(Object key) {
        return m_layer.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return m_layer.containsValue(value);
    }

    @Override
    public IMultiLayer get(Object key) {
        return m_layer.get(key);
    }

    @Override
    public IMultiLayer put(String key, IMultiLayer value) {
        super.addPainter(value);
        return m_layer.put(key, value);
    }

    @Override
    public IMultiLayer remove(Object key) {
        IMultiLayer l_layer = m_layer.remove(key);
        super.removePainter(l_layer);
        return l_layer;
    }

    @Override
    public void putAll(Map<? extends String, ? extends IMultiLayer> m) {
        for (IMultiLayer l_layer : m.values())
            super.addPainter(l_layer);
        m_layer.putAll(m);
    }

    @Override
    public void clear() {
        for (IMultiLayer l_layer : m_layer.values())
            super.removePainter(l_layer);
        m_layer.clear();
    }

    @Override
    public Set<String> keySet() {
        return m_layer.keySet();
    }

    @Override
    public Collection<IMultiLayer> values() {
        return m_layer.values();
    }

    @Override
    public Set<Entry<String, IMultiLayer>> entrySet() {
        return m_layer.entrySet();
    }

    @Override
    protected void doPaint(Graphics2D g, T component, int width, int height) {
        for (IMultiLayer l_layer : m_layer.values()) {
            if (!l_layer.isVisible())
                continue;

            Graphics2D temp = (Graphics2D) g.create();
            try {
                l_layer.paint(temp, component, width, height);
                if (super.isClipPreserved()) {
                    g.setClip(temp.getClip());
                }
            } finally {
                temp.dispose();
            }
        }
    }

}
