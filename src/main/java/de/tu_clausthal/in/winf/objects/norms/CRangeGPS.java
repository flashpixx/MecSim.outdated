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

package de.tu_clausthal.in.winf.objects.norms;

import de.tu_clausthal.in.winf.mas.norm.IRange;
import de.tu_clausthal.in.winf.objects.ICar;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.geom.Point2D;


/**
 * range for GPS rectangle
 */
public class CRangeGPS implements IRange<INormCar> {

    /**
     * geoposition of the upper left corner of the rectangle
     */
    private GeoPosition m_upperleft = null;
    /**
     * geoposition of the lower right corner of the rectangle
     */
    private GeoPosition m_lowerright = null;
    /**
     * color of the rectangle fill color *
     */
    private Color m_regioColor = new Color(200, 0, 0, 35);
    /**
     * border color of the rectangle *
     */
    private Color m_borderColor = new Color(200, 0, 0);



    /**
     * ctor to create the rectangle
     *
     * @param p_upperleft  left upper corner position
     * @param p_lowerright right lower corner position
     */
    public CRangeGPS(GeoPosition p_upperleft, GeoPosition p_lowerright) {
        if ((p_lowerright == null) || (p_upperleft == null))
            throw new IllegalArgumentException("parameter need not to be null");
        if ((p_upperleft.getLongitude() > p_lowerright.getLongitude()) || (p_upperleft.getLatitude() < p_lowerright.getLatitude()))
            throw new IllegalArgumentException("geoposition are not in the correct order, first argument is the upper-left ");

        m_upperleft = p_upperleft;
        m_lowerright = p_lowerright;
    }


    @Override
    public boolean isWithin(INormCar p_object) {
        return (m_upperleft.getLongitude() <= p_object.getCurrentPosition().getLongitude()) && (m_lowerright.getLatitude() >= p_object.getCurrentPosition().getLatitude());
    }


    @Override
    public void paint(Graphics2D graphics2D, TileFactory factory, int zoom) {
        graphics2D = (Graphics2D) graphics2D.create();
        Rectangle l_viewportBounds = COSMViewer.getInstance().getViewportBounds();
        graphics2D.translate(-l_viewportBounds.x, -l_viewportBounds.y);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point2D l_upperleft  = factory.geoToPixel(m_upperleft, zoom);
        Point2D l_lowerright = factory.geoToPixel(m_lowerright, zoom);
        Rectangle l_rectangle = new Rectangle( new Point((int)l_upperleft.getX(), (int)l_upperleft.getY()) );
        l_rectangle.union( new Rectangle( new Point((int)l_lowerright.getX(), (int)l_lowerright.getY()) ) );

        System.out.println(l_rectangle);
        System.out.println(m_upperleft+"      "+m_lowerright);
        System.out.println(l_upperleft+"      "+l_lowerright);
        System.out.println(l_viewportBounds);
        System.out.println();

        graphics2D.setColor(m_borderColor);
        graphics2D.draw(l_rectangle);
        graphics2D.setColor(m_regioColor);
        graphics2D.fill(l_rectangle);
    }
}
