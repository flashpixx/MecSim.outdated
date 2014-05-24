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

package de.tu_clausthal.in.winf.ui.painter;

import com.graphhopper.util.PointList;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


/**
 * painter for routes
 */
public class CRoutePainter implements Painter<JXMapViewer> {

    /**
     * draw color *
     */
    private Color m_color = Color.RED;
    /**
     * point list *
     */
    private List<GeoPosition> m_track = new ArrayList();


    /**
     * sets a route
     *
     * @param p_list list
     */
    public CRoutePainter(List<GeoPosition> p_list) {
        m_track = new ArrayList(p_list);
    }


    /**
     * sets a route
     *
     * @param p_list  list
     * @param p_color draw color
     */
    public CRoutePainter(List<GeoPosition> p_list, Color p_color) {
        m_track = new ArrayList(p_list);
        m_color = p_color;
    }


    /**
     * sets a point list with color
     *
     * @param p_list point list
     */
    public CRoutePainter(PointList p_list) {
        m_track = new ArrayList();
        for (int i = 0; i < p_list.size(); i++)
            m_track.add(new GeoPosition(p_list.getLatitude(i), p_list.getLongitude(i)));
    }


    /**
     * sets a pointlist with color
     *
     * @param p_list  point list
     * @param p_color draw color
     */
    public CRoutePainter(PointList p_list, Color p_color) {
        m_color = p_color;
        m_track = new ArrayList();
        for (int i = 0; i < p_list.size(); i++)
            m_track.add(new GeoPosition(p_list.getLatitude(i), p_list.getLongitude(i)));
    }


    @Override
    public void paint(Graphics2D graphics2D, JXMapViewer jxMapViewer, int i, int i2) {
        graphics2D = (Graphics2D) graphics2D.create();
        Rectangle rect = jxMapViewer.getViewportBounds();
        graphics2D.translate(-rect.x, -rect.y);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(m_color);
        graphics2D.setStroke(new BasicStroke(2));
        this.drawRoute(graphics2D, jxMapViewer);
        graphics2D.dispose();
    }


    /**
     * draws the route
     *
     * @param p_graphic graphic
     * @param p_viewer  viewer
     */
    private void drawRoute(Graphics2D p_graphic, JXMapViewer p_viewer) {
        int l_lastX = 0;
        int l_lastY = 0;

        boolean l_first = true;
        for (GeoPosition l_position : m_track) {
            Point2D l_point = p_viewer.getTileFactory().geoToPixel(l_position, p_viewer.getZoom());
            if (!l_first)
                p_graphic.drawLine(l_lastX, l_lastY, (int) l_point.getX(), (int) l_point.getY());

            l_first = false;
            l_lastX = (int) l_point.getX();
            l_lastY = (int) l_point.getY();
        }
    }

}
