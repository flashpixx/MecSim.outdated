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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.IReturnStepableTarget;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.inspector.CInspector;
import de.tu_clausthal.in.mec.ui.inspector.IInspector;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


/**
 * class with default source implementation
 */
abstract public class IDefaultSourceFactory extends IInspector implements ISourceFactory, Serializable
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;

    /**
     * position of the source within the map
     */
    protected transient GeoPosition m_position = null;
    /**
     * image of the waypoint
     */
    protected transient BufferedImage m_image = null;
    /**
     * map with targets
     */
    protected transient Collection<IReturnStepableTarget<ICar>> m_target = new HashSet<>();

    /**
     * waypoint color
     */
    protected Color m_color = null;


    /**
     * ctor that sets the geo position of the source
     *
     * @param p_position geo position object
     * @param p_color    color of the source
     */
    public IDefaultSourceFactory( GeoPosition p_position, Color p_color )
    {
        m_position = p_position;
        m_color = p_color;
        this.setImage();
        m_target.add( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) );
    }

    @Override
    public Map<String, Object> inspect()
    {
        Map<String, Object> l_map = super.inspect();
        l_map.put( "geoposition", m_position );
        return l_map;
    }

    @Override
    public void onClick( MouseEvent e, JXMapViewer viewer )
    {
        if ( m_position == null )
            return;

        Point2D l_point = viewer.getTileFactory().geoToPixel( m_position, viewer.getZoom() );
        Ellipse2D l_circle = new Ellipse2D.Double( l_point.getX() - viewer.getViewportBounds().getX(), l_point.getY() - viewer.getViewportBounds().getY(), this.iconsize( viewer ), this.iconsize( viewer ) );

        if ( l_circle.contains( e.getX(), e.getY() ) )
            ( (CInspector) CSimulation.getInstance().getUI().getWidget( "Inspector" ) ).set( this );
    }

    /**
     * creates the image
     */
    private void setImage()
    {
        if ( m_color == null )
            return;

        try
        {
            BufferedImage l_image = ImageIO.read( DefaultWaypointRenderer.class.getResource( "/images/standard_waypoint.png" ) );

            // modify blue value to the color of the waypoint
            m_image = new BufferedImage( l_image.getColorModel(), l_image.copyData( null ), l_image.isAlphaPremultiplied(), null );
            for ( int i = 0; i < l_image.getHeight(); i++ )
                for ( int j = 0; j < l_image.getWidth(); j++ )
                {
                    Color l_color = new Color( l_image.getRGB( j, i ) );
                    if ( l_color.getBlue() > 0 )
                        m_image.setRGB( j, i, m_color.getRGB() );
                }

        }
        catch ( Exception l_exception )
        {
            CLogger.warn( l_exception );
        }
    }


    @Override
    public Collection<IReturnStepableTarget<ICar>> getTargets()
    {
        return m_target;
    }


    @Override
    public void paint( Graphics2D g, COSMViewer object, int width, int height )
    {
        if ( m_image == null )
            return;

        Point2D l_point = object.getTileFactory().geoToPixel( m_position, object.getZoom() );
        g.drawImage( m_image, (int) l_point.getX() - m_image.getWidth() / 2, (int) l_point.getY() - m_image.getHeight(), null );
    }


    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     */
    private void readObject( ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();
        m_position = new GeoPosition( p_stream.readDouble(), p_stream.readDouble() );
        this.setImage();
    }


    /**
     * write call of serialize interface
     *
     * @param p_stream stream
     */
    private void writeObject( ObjectOutputStream p_stream ) throws IOException
    {
        p_stream.defaultWriteObject();
        p_stream.writeDouble( m_position.getLatitude() );
        p_stream.writeDouble( m_position.getLongitude() );
    }


    @Override
    public GeoPosition getPosition()
    {
        return m_position;
    }

}
