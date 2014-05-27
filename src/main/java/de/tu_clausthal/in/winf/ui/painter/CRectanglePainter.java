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
 * @see http://www.locked.de/2011/01/21/selektionsfenster-in-swingx-ws-jxmapkit/
 */
public class CRectanglePainter implements Painter<JXMapViewer> {

    /** color of the rectangle **/
    protected Color m_regioColor = new Color(0,0,200, 75);
    protected Color m_borderColor = new Color(0,0,200);
    protected Rectangle m_start = null;
    protected Rectangle m_rectangle = null;


    /** ctor for blank initialization
     *
     */
    public CRectanglePainter( Point p_point ) {
        if (p_point == null)
            throw new IllegalArgumentException("point need not to be null");
        m_start = new Rectangle(p_point);
    }


    /** sets the border color
     *
     * @param p_color color
     */
    public void setBorderColor( Color p_color )
    {
        if (p_color == null)
            throw new IllegalArgumentException("color need not to be null");

        m_borderColor = p_color;
    }


    public void setRegioColor( Color p_color )
    {
        if (p_color == null)
            throw new IllegalArgumentException("color need not to be null");

        m_regioColor = p_color;
    }


    /** sets the position
     *
     */
    public void to( Point p_point )
    {
        m_rectangle = m_start.union( new Rectangle(p_point) );
    }


    @Override
    public void paint(Graphics2D graphics2D, JXMapViewer jxMapViewer, int i, int i2) {
        if (m_rectangle == null)
            return;

        graphics2D.setColor(m_regioColor);
        graphics2D.fillRect(m_rectangle.x, m_rectangle.y, m_rectangle.width, m_rectangle.height);
        graphics2D.setColor(m_borderColor);
        graphics2D.drawRect(m_rectangle.x, m_rectangle.y, m_rectangle.width, m_rectangle.height);
    }
}
