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

import com.graphhopper.util.PointList;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;


/**
 * overlay renderer for routes *
 */
public class CRouteOverlay implements Painter {

    /**
     * map with route and color *
     */
    private HashMap<PointList, Color> m_points = new HashMap();
    /**
     * default color of a route *
     */
    private Color m_DefaultColor = Color.RED;
    /**
     * line thickness *
     */
    private int m_LineThickness = 2;


    /**
     * default ctor *
     */
    public CRouteOverlay() {
    }


    /**
     * ctor to set default color
     *
     * @param p_color color
     */
    public CRouteOverlay(Color p_color) {
        m_DefaultColor = p_color;
    }


    /**
     * ctor to set the line thickness
     *
     * @param p_LineThickness line thickness
     */
    public CRouteOverlay(int p_LineThickness) {
        if (p_LineThickness < 1)
            throw new IllegalArgumentException("line thickness value must be greater zero");
        m_LineThickness = p_LineThickness;
    }


    /**
     * ctor to set line color and line thickness
     *
     * @param p_color         default color
     * @param p_LineThickness line thickness
     */
    public CRouteOverlay(Color p_color, int p_LineThickness) {
        if (p_LineThickness < 1)
            throw new IllegalArgumentException("line thickness value must be greater zero");

        m_DefaultColor = p_color;
        m_LineThickness = p_LineThickness;
    }


    /**
     * adds a points list (of geo positions)
     *
     * @param p_points point list
     */
    public void add(PointList p_points) {
        m_points.put(p_points, m_DefaultColor);
    }


    /**
     * adds a points list (of geo positions) with a color
     *
     * @param p_points points list
     * @param p_color  color of the line
     */
    public void add(PointList p_points, Color p_color) {
        m_points.put(p_points, p_color);
    }


    /**
     * removes a points list
     *
     * @param p_points point list object
     */
    public void remove(PointList p_points) {
        m_points.remove(p_points);
    }


    /**
     * clears list *
     */
    public void clear() {
        m_points.clear();
    }


    @Override
    public void paint(Graphics2D graphics2D, Object o, int i, int i2) {
        graphics2D = (Graphics2D) graphics2D.create();
        Rectangle l_viewportBounds = COSMViewer.getInstance().getViewportBounds();
        graphics2D.translate(-l_viewportBounds.x, -l_viewportBounds.y);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setStroke(new BasicStroke(m_LineThickness));

        for (Map.Entry<PointList, Color> l_item : m_points.entrySet()) {
            int l_lastX = -1;
            int l_lastY = -1;

            graphics2D.setColor(l_item.getValue());
            for (int n = 0; n < l_item.getKey().size(); n++) {
                Point2D l_point = COSMViewer.getInstance().getTileFactory().geoToPixel(new GeoPosition(l_item.getKey().getLatitude(n), l_item.getKey().getLongitude(n)), COSMViewer.getInstance().getZoom());
                if ((l_lastX != -1) && (l_lastY != -1))
                    graphics2D.drawLine(l_lastX, l_lastY, (int) l_point.getX(), (int) l_point.getY());
                l_lastX = (int) l_point.getX();
                l_lastY = (int) l_point.getY();
            }
        }

        graphics2D.dispose();
    }

}
