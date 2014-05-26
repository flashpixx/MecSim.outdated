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

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;


/**
 * painter to create a rectangle
 */
public class CRectanglePainter implements Painter<JXMapViewer> {

    /** color of the rectangle **/
    protected Color m_color = new Color(200,200,200, 127);
    /** left-upper geoposition **/
    protected GeoPosition m_geoleftupper = null;
    /** right-lower geoposition **/
    protected GeoPosition m_georightlower = null;
    /** left-upper point **/
    protected Point2D m_pointleftupper = null;
    /** right-lower point **/
    protected Point2D m_pointrightlower = null;


    /** ctor for blank initialization
     *
     */
    public CRectanglePainter() {}

    /** ctor for geopositions
     *
     * @param p_leftupper left upper position
     * @param p_rightlower right lower position
     */
    public CRectanglePainter( GeoPosition p_leftupper, GeoPosition p_rightlower ) {
        m_geoleftupper = p_leftupper;
        m_georightlower = p_rightlower;
    }


    /** ctor for geopositions and color
     *
     * @param p_leftupper left upper position
     * @param p_rightlower right lower position
     * @param p_color color of the rectangle
     */
    public CRectanglePainter( GeoPosition p_leftupper, GeoPosition p_rightlower, Color p_color )
    {
        if (p_color == null)
            throw new IllegalArgumentException("color need not to be null");

        m_color = p_color;
        m_geoleftupper = p_leftupper;
        m_georightlower = p_rightlower;
    }


    /** ctor for points
     *
     * @param p_leftupper
     * @param p_rightlower
     */
    public CRectanglePainter( Point2D p_leftupper, Point2D p_rightlower )
    {
        m_pointleftupper = p_leftupper;
        m_pointrightlower = p_rightlower;
    }


    /** ctor for points and color
     *
     * @param p_leftupper
     * @param p_rightlower
     * @param p_color
     */
    public CRectanglePainter( Point2D p_leftupper, Point2D p_rightlower, Color p_color )
    {
        if (p_color == null)
            throw new IllegalArgumentException("color need not to be null");

        m_color = p_color;
        m_pointleftupper = p_leftupper;
        m_pointrightlower = p_rightlower;
    }


    /** sets the color
     *
     * @param p_color color
     */
    public void set( Color p_color )
    {
        if (p_color == null)
            throw new IllegalArgumentException("color need not to be null");

        m_color = p_color;
    }


    /** changes the geoposition
     *
     * @param p_leftupper left-upper corner
     * @param p_rightlower right-lower corner
     */
    public void set( GeoPosition p_leftupper, GeoPosition p_rightlower )
    {
        m_pointleftupper  = null;
        m_pointrightlower = null;
        m_geoleftupper    = p_leftupper;
        m_georightlower   = p_rightlower;
    }


    /** sets the position
     *
     * @param p_leftupper left-upper point
     * @param p_rightlower right-lower point
     */
    public void set( Point2D p_leftupper, Point2D p_rightlower )
    {
        m_pointleftupper  = p_leftupper;
        m_pointrightlower = p_rightlower;
        m_geoleftupper    = null;
        m_georightlower   = null;
    }


    /** returns the left-upper point
     *
     * @param jxMapViewer map viewer
     * @return point
     */
    private Point2D getLeftUpper(JXMapViewer jxMapViewer)
    {
        if ((m_geoleftupper == null) && (m_pointleftupper == null))
            return null;

        if (m_geoleftupper != null)
            return jxMapViewer.getTileFactory().geoToPixel(m_geoleftupper, jxMapViewer.getZoom());

        return m_pointleftupper;
    }


    /** returns the right-lower point
     *
     * @param jxMapViewer map viewer
     * @return point
     */
    private Point2D getRightLower(JXMapViewer jxMapViewer)
    {
        if ((m_georightlower == null) && (m_pointrightlower == null))
            return null;

        if (m_georightlower != null)
            return jxMapViewer.getTileFactory().geoToPixel(m_georightlower, jxMapViewer.getZoom());

        return m_pointrightlower;
    }



    @Override
    public void paint(Graphics2D graphics2D, JXMapViewer jxMapViewer, int i, int i2) {
        Point2D l_left  = this.getLeftUpper(jxMapViewer);
        Point2D l_right = this.getRightLower(jxMapViewer);
        if ( (l_left == null) || (l_right == null) )
            return;

        graphics2D = (Graphics2D) graphics2D.create();
        Rectangle l_rect = jxMapViewer.getViewportBounds();
        graphics2D.translate(-l_rect.x, -l_rect.y);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //graphics2D.setColor(m_color);
        graphics2D.setColor(Color.BLACK);
        //graphics2D.fillRect(100, 100, 500, 500);
        graphics2D.fillRect( (int)Math.min(l_left.getX(), l_right.getX()), (int)Math.min(l_left.getY(), l_right.getY()), (int)Math.abs(l_right.getX()-l_left.getX()), (int)Math.abs(l_right.getY()-l_left.getY()) );

        jxMapViewer.repaint();
    }
}
