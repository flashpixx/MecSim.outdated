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

package de.tu_clausthal.in.winf.ui;

import de.tu_clausthal.in.winf.objects.ICar;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * overlay renderer for cars *
 */
public class CCarOverlay implements Painter {

    /**
     * reference to the viewer *
     */
    private JXMapViewer m_viewer = null;
    /**
     * map with car objects *
     */
    private Set<ICar> m_cars = new HashSet();


    /**
     * ctor to set the viewer
     *
     * @param p_viewer viewer
     */
    public CCarOverlay(JXMapViewer p_viewer) {
        m_viewer = p_viewer;
    }


    /**
     * adds a car to the list
     *
     * @param p_car car object
     */
    public void add(ICar p_car) {
        m_cars.add(p_car);
        m_viewer.repaint();
    }


    /**
     * adds a collection
     *
     * @param p_cars collection
     */
    public void add(ICar[] p_cars) {
        Collections.addAll(m_cars, p_cars);
        m_viewer.repaint();
    }


    /**
     * removes a car
     *
     * @param p_car car object
     */
    public void remove(ICar p_car) {
        m_cars.remove(p_car);
        m_viewer.repaint();
    }


    /**
     * clears list *
     */
    public void clear() {
        m_cars.clear();
        m_viewer.repaint();
    }


    @Override
    public void paint(Graphics2D graphics2D, Object o, int i, int i2) {
        graphics2D = (Graphics2D) graphics2D.create();
        Rectangle l_viewportBounds = COSMViewer.getInstance().getViewportBounds();
        graphics2D.translate(-l_viewportBounds.x, -l_viewportBounds.y);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (ICar l_car : m_cars)
            l_car.paint(graphics2D, COSMViewer.getInstance().getTileFactory(), COSMViewer.getInstance().getZoom());

        graphics2D.dispose();
    }

}
