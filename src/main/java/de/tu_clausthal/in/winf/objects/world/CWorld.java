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

package de.tu_clausthal.in.winf.objects.world;


import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * world layer collection
 */
public class CWorld implements Map<String, IWorldLayer>, Painter {

    /**
     * viewer *
     */
    protected COSMViewer m_viewer = null;
    /**
     * map with layer *
     */
    protected Map<String, IWorldLayer> m_layer = new HashMap();


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

        for (Map.Entry<String, IWorldLayer> l_item : m_layer.entrySet())
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
    public IWorldLayer get(Object key) {
        return m_layer.get(key);
    }

    @Override
    public IWorldLayer put(String key, IWorldLayer value) {
        return m_layer.put(key, value);
    }

    @Override
    public IWorldLayer remove(Object key) {
        return m_layer.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends IWorldLayer> m) {
        m_layer.putAll(m);
    }

    @Override
    public void clear() {
        m_layer.clear();
    }

    @Override
    public Set<String> keySet() {
        return m_layer.keySet();
    }

    @Override
    public Collection<IWorldLayer> values() {
        return m_layer.values();
    }

    @Override
    public Set<Entry<String, IWorldLayer>> entrySet() {
        return m_layer.entrySet();
    }

    @Override
    public void paint(Graphics2D graphics2D, Object o, int i, int i2) {
        for (IWorldLayer l_layer : m_layer.values())
            if (l_layer.isVisible())
                l_layer.paint(graphics2D, m_viewer);
    }
}
