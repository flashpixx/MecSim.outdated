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
import java.util.HashSet;


/**
 * collection to use mutiple overlays in the viewer *
 */
public class COverlayCollection implements Painter {

    /**
     * list of other overlay painter *
     */
    private HashSet<Painter> m_overlay = new HashSet();


    /**
     * adds a new painter
     *
     * @param p_item painter object
     */
    public void add(Painter p_item) {
        m_overlay.add(p_item);
    }


    /**
     * removes a painter
     *
     * @param p_item painter object
     */
    public void remove(Painter p_item) {
        m_overlay.remove(p_item);
    }


    /**
     * removes all painters *
     */
    public void clear() {
        m_overlay.clear();
    }


    @Override
    public void paint(Graphics2D graphics2D, Object o, int i, int i2) {
        for (Painter l_item : m_overlay)
            l_item.paint(graphics2D, o, i, i2);
    }

}
