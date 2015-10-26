/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.object.waypoint.point;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.benchmark.IBenchmark;
import de.tu_clausthal.in.mec.ui.CInspector;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * abstract class for a waypoint
 */
public abstract class IWayPointBase<T, P extends IFactory<T>, N extends IGenerator> extends IInspectorDefault implements IWayPoint<T>
{
    /**
     * visible definition - wapoints can be full invisible
     */
    protected final boolean m_visible = true;
    /**
     * factory of this source
     */
    protected final P m_factory;
    /**
     * generator of this source
     */
    protected final N m_generator;
    /**
     * color of the source (needed in web-ui)
     */
    private final Color m_color;
    /**
     * image of the waypoint
     */
    private final transient BufferedImage m_initializeimage;
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
        putAll( IWayPointBase.super.inspect() );
    }};
    /**
     * last zoom (if the zoom changed the image need to be resized)
     */
    private transient int m_lastZoom = 0;
    /**
     * user defined name of the source (so that users can differ between sources)
     */
    private final String m_name;
    /**
     * position of the source within the map
     */
    private final GeoPosition m_position;
    /**
     * current scaled image
     */
    private transient BufferedImage m_scaledimage;

    /**
     * ctor for empty waypoints
     *
     * @param p_position position
     * @param p_color color
     * @param p_name name
     */
    public IWayPointBase( final GeoPosition p_position, final Color p_color, final String p_name )
    {
        m_position = p_position;
        m_generator = null;
        m_factory = null;
        m_name = p_name;
        m_color = p_color;

        m_initializeimage = this.initializeImage( 20, 34, p_color );
        m_scaledimage = m_initializeimage;
    }

    /**
     * ctor - source generates elements
     *
     * @param p_position position
     * @param p_generator generator
     * @param p_factory factory
     * @param p_color color
     * @param p_name name
     */
    public IWayPointBase( final GeoPosition p_position, final N p_generator, final P p_factory, final Color p_color, final String p_name )
    {
        m_position = p_position;
        m_generator = p_generator;
        m_factory = p_factory;
        m_name = p_name;
        m_color = p_color;

        m_initializeimage = this.initializeImage( 20, 34, p_color );
        m_scaledimage = m_initializeimage;

        if ( this.hasFactoryGenerator() )
        {
            m_inspect.putAll( m_generator.inspect() );
            m_inspect.putAll( m_factory.inspect() );
            m_inspect.put( CCommon.getResourceString( IWayPointBase.class, "name" ), m_name );
            m_inspect.put( CCommon.getResourceString( IWayPointBase.class, "color" ), "#" + Integer.toHexString( m_color.getRGB() ).substring( 2 ) );
        }
    }

    @Override
    public final Color getColor()
    {
        return m_color;
    }

    @Override
    public final String getName()
    {
        return m_name;
    }

    @Override
    public final GeoPosition getPosition()
    {
        return m_position;
    }

    @Override
    public boolean hasFactoryGenerator()
    {
        return ( m_generator != null ) && ( m_factory != null );
    }

    /**
     * method to scale a buffered image
     *
     * @param p_source image which should be scaled
     * @param p_width new width
     * @param p_height new height
     * @return new image
     */
    public BufferedImage getScaledImage( final BufferedImage p_source, final int p_width, final int p_height )
    {
        final BufferedImage l_newimage = new BufferedImage( p_width, p_height, BufferedImage.TRANSLUCENT );
        final Graphics2D l_graphics = l_newimage.createGraphics();
        l_graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        l_graphics.drawImage( p_source, 0, 0, p_width, p_height, null );
        l_graphics.dispose();
        return l_newimage;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

    @Override
    public final void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
        if ( ( !m_visible ) || ( m_position == null ) )
            return;

        final int l_zoom = this.iconsize( p_viewer );
        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        final Ellipse2D l_circle = new Ellipse2D.Double(
                l_point.getX() - p_viewer.getViewportBounds().getX() - l_zoom,
                l_point.getY() - p_viewer.getViewportBounds().getY() - l_zoom,
                l_zoom * 2, l_zoom * 2
        );

        if ( l_circle.contains( p_event.getX(), p_event.getY() ) )
            CSimulation.getInstance().getStorage().<CInspector>get( "inspector" ).set( this );
    }

    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_visible )
            return;

        //if the zoom change calculate the new scaled image
        if ( p_viewer.getZoom() != m_lastZoom )
            m_scaledimage = this.getScaledImage(
                    m_initializeimage, (int) ( m_initializeimage.getWidth() * this.iconscale( p_viewer ) ),
                    (int) ( m_initializeimage.getHeight() * this.iconscale( p_viewer ) )
            );

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        p_graphic.drawImage( m_scaledimage, (int) l_point.getX() - m_scaledimage.getWidth() / 2, (int) l_point.getY() - m_scaledimage.getHeight(), null );
    }

    @Override
    @IBenchmark
    public Collection<T> step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        if ( !this.hasFactoryGenerator() )
            return null;

        return m_factory.generate( this.getPath(), m_generator.getCount( p_currentstep ) );
    }

    /**
     * creates an image with a specific scale
     *
     * @param p_width image width
     * @param p_height image height
     * @param p_color color of the waypoint
     * @return image
     */
    private BufferedImage initializeImage( final int p_width, final int p_height, final Color p_color )
    {
        try
        {
            final BufferedImage l_image = this.getScaledImage(
                    ImageIO.read( DefaultWaypointRenderer.class.getResource( "/images/standard_waypoint.png" ) ), p_width, p_height
            );
            for ( int i = 0; i < l_image.getHeight(); i++ )
                for ( int j = 0; j < l_image.getWidth(); j++ )
                {
                    final Color l_color = new Color( l_image.getRGB( j, i ) );
                    if ( l_color.getBlue() > 0 )
                        l_image.setRGB( j, i, p_color.getRGB() );
                }

            return l_image;
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }

        return null;
    }
}
