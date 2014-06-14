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

package de.tu_clausthal.in.winf.object.norm;

import de.tu_clausthal.in.winf.ui.inspector.CInspector;
import de.tu_clausthal.in.winf.ui.inspector.IInspector;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Map;


/**
 * range for GPS rectangle
 */
public class CRangeGPS extends IInspector implements IRange<INormObject> {

    /**
     * institution of the range
     */
    protected IInstitution<INormObject> m_institution = null;
    /**
     * geoposition of the upper left corner of the rectangle
     */
    protected GeoPosition m_upperleft = null;
    /**
     * geoposition of the lower right corner of the rectangle
     */
    protected GeoPosition m_lowerright = null;
    /**
     * rectangle of the geoposition *
     */
    protected Rectangle.Double m_georectangle = null;
    /**
     * color of the rectangle fill color *
     */
    protected Color m_regioColor = new Color(200, 0, 0, 35);
    /**
     * border color of the rectangle *
     */
    protected Color m_borderColor = new Color(200, 0, 0, 75);


    /**
     * ctor to create the rectangle
     *
     * @param p_institution institution of the range
     * @param p_upperleft   left upper corner position
     * @param p_lowerright  right lower corner position
     */
    public CRangeGPS(IInstitution<INormObject> p_institution, GeoPosition p_upperleft, GeoPosition p_lowerright) {
        if ((p_lowerright == null) || (p_upperleft == null))
            throw new IllegalArgumentException("parameter need not to be null");
        if ((p_upperleft.getLongitude() > p_lowerright.getLongitude()) || (p_upperleft.getLatitude() < p_lowerright.getLatitude()))
            throw new IllegalArgumentException("geoposition are not in the correct order, first argument is the upper-left ");
        if (p_institution == null)
            throw new IllegalArgumentException("institution need not to be null");

        m_upperleft = p_upperleft;
        m_lowerright = p_lowerright;
        m_institution = p_institution;
        m_georectangle = new Rectangle.Double(Math.min(m_upperleft.getLatitude(), m_lowerright.getLatitude()), Math.min(m_upperleft.getLongitude(), m_lowerright.getLongitude()), Math.abs(m_upperleft.getLatitude() - m_lowerright.getLatitude()), Math.abs(m_upperleft.getLongitude() - m_lowerright.getLongitude()));
    }

    @Override
    public Map<String, Object> inspect() {
        Map<String, Object> l_map = super.inspect();

        l_map.put("upper left", m_upperleft);
        l_map.put("lower right", m_lowerright);
        l_map.put("institution", m_institution.getName());

        return l_map;
    }


    @Override
    public boolean check(INormObject p_object) {
        synchronized (p_object) {
            return m_georectangle.contains(p_object.getCurrentPosition().getLatitude(), p_object.getCurrentPosition().getLongitude());
        }
    }

    @Override
    public IInstitution<INormObject> getInstitution() {
        return m_institution;
    }


    @Override
    public void paint(Graphics2D graphics2D, JXMapViewer viewer) {
        Point2D l_upperleft = viewer.getTileFactory().geoToPixel(m_upperleft, viewer.getZoom());
        Point2D l_lowerright = viewer.getTileFactory().geoToPixel(m_lowerright, viewer.getZoom());

        Rectangle l_rectangle = new Rectangle((int) Math.min(l_upperleft.getX(), l_lowerright.getX()), (int) Math.min(l_upperleft.getY(), l_lowerright.getY()),
                (int) Math.abs(l_upperleft.getX() - l_lowerright.getX()), (int) Math.abs(l_upperleft.getY() - l_lowerright.getY()));
        graphics2D.setColor(m_borderColor);
        graphics2D.draw(l_rectangle);
        graphics2D.setColor(m_regioColor);
        graphics2D.fill(l_rectangle);
    }

    @Override
    public void onClick(MouseEvent e, JXMapViewer viewer) {
        Point2D l_upperleft = viewer.getTileFactory().geoToPixel(m_upperleft, viewer.getZoom());
        Point2D l_lowerright = viewer.getTileFactory().geoToPixel(m_lowerright, viewer.getZoom());
        Rectangle l_rectangle = new Rectangle((int) (Math.min(l_upperleft.getX(), l_lowerright.getX()) - viewer.getViewportBounds().getX()), (int) (Math.min(l_upperleft.getY(), l_lowerright.getY()) - viewer.getViewportBounds().getY()),
                (int) Math.abs(l_upperleft.getX() - l_lowerright.getX()), (int) Math.abs(l_upperleft.getY() - l_lowerright.getY()));

        if (!l_rectangle.contains(e.getPoint()))
            return;

        if (e.isControlDown()) {
            m_institution.getRange().remove(this);
            this.release();
        } else
            CInspector.getInstance().set(this);
    }
}
