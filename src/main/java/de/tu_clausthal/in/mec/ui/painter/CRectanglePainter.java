/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.ui.painter;

import org.jxmapviewer.painter.Painter;

import java.awt.*;


/**
 * painter to create a rectangle
 *
 * @see http://www.locked.de/2011/01/21/selektionsfenster-in-swingx-ws-jxmapkit/
 * @see https://github.com/msteiger/jxmapviewer2/tree/master/examples/src/sample3_interaction
 * @deprecated move to norm range class
 */
public class CRectanglePainter implements Painter<Object>
{

    /**
     * color of the rectangle fill color *
     */
    protected Color m_regioColor = new Color( 0, 0, 200, 35 );
    /**
     * border color of the rectangle *
     */
    protected Color m_borderColor = new Color( 0, 0, 200 );
    /**
     * start rectangle *
     */
    protected Rectangle m_start = null;
    /**
     * current full rectangle *
     */
    protected Rectangle m_rectangle = null;


    /**
     * ctor for blank initialization *
     */
    public CRectanglePainter()
    {
    }

    /**
     * ctor
     */
    public CRectanglePainter( Point p_point )
    {
        if ( p_point == null )
            throw new IllegalArgumentException( "point need not to be null" );
        m_start = new Rectangle( p_point );
    }


    /**
     * sets the border color
     *
     * @param p_color color
     */
    public void setBorderColor( Color p_color )
    {
        if ( p_color == null )
            throw new IllegalArgumentException( "color need not to be null" );

        m_borderColor = p_color;
    }


    public void setRegioColor( Color p_color )
    {
        if ( p_color == null )
            throw new IllegalArgumentException( "color need not to be null" );

        m_regioColor = p_color;
    }


    /**
     * sets the position
     */
    public void to( Point p_point )
    {
        m_rectangle = m_start.union( new Rectangle( p_point ) );
    }


    /**
     * sets the starts point *
     */
    public void from( Point p_point )
    {
        m_start = new Rectangle( p_point );
    }


    /**
     * clears the rectangle *
     */
    public void clear()
    {
        m_start = null;
        m_rectangle = null;
    }


    /**
     * returns the upper-left corner
     *
     * @return point
     */
    public Point getFrom()
    {
        if ( m_rectangle == null )
            return null;

        return m_rectangle.getLocation();
    }


    /**
     * returns the lower-right corner
     *
     * @return point
     */
    public Point getTo()
    {
        if ( m_rectangle == null )
            return null;

        return new Point( (int) ( this.getFrom().getX() + m_rectangle.getWidth() ), (int) ( this.getFrom().getY() + m_rectangle.getHeight() ) );
    }


    /**
     * returns the rectangle or null
     *
     * @return rectangle object
     */
    public Rectangle getRectangle()
    {
        return m_rectangle;
    }


    @Override
    public void paint( Graphics2D graphics2D, Object o, int i, int i2 )
    {
        if ( m_rectangle == null )
            return;

        graphics2D.setColor( m_borderColor );
        graphics2D.draw( m_rectangle );
        graphics2D.setColor( m_regioColor );
        graphics2D.fill( m_rectangle );
    }
}
