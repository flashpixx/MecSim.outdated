/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenpraktikum.   #
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
 @endcond
 **/

package de.tu_clausthal.in.winf.ui;

import org.jdesktop.swingx.painter.Painter;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


/**
 * collection to use mutiple overlays in the viewer *
 */
public class COverlayCollection implements Painter, Collection<Painter> {

    /**
     * list of other overlay painter *
     */
    private HashSet<Painter> m_overlay = new HashSet();


    @Override
    public int size() {
        return m_overlay.size();
    }


    @Override
    public boolean isEmpty() {
        return m_overlay.isEmpty();
    }


    @Override
    public boolean contains(Object p_object) {
        return m_overlay.contains(p_object);
    }


    @Override
    public Iterator<Painter> iterator() {
        return m_overlay.iterator();
    }


    @Override
    public Object[] toArray() {
        return m_overlay.toArray();
    }


    @Override
    public <T> T[] toArray(T[] p_array) {
        return m_overlay.toArray(p_array);
    }


    @Override
    public boolean add(Painter p_painter) {
        return m_overlay.add(p_painter);
    }


    @Override
    public boolean remove(Object p_object) {
        return m_overlay.remove(p_object);
    }


    @Override
    public boolean containsAll(Collection<?> p_objects) {
        return m_overlay.containsAll(p_objects);
    }


    @Override
    public boolean addAll(Collection<? extends Painter> p_painters) {
        return m_overlay.addAll(p_painters);
    }


    @Override
    public boolean removeAll(Collection<?> p_objects) {
        return m_overlay.removeAll(p_objects);
    }


    @Override
    public boolean retainAll(Collection<?> p_objects) {
        return m_overlay.retainAll(p_objects);
    }


    @Override
    public void clear() {
        m_overlay.clear();
    }


    @Override
    public void paint(Graphics2D graphics2D, Object o, int i, int i2) {
        for (Painter l_item : m_overlay)
            l_item.paint(graphics2D, o, i, i2);
    }

}
