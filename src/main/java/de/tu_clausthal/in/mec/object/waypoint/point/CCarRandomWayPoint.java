/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.waypoint.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppableTarget;
import de.tu_clausthal.in.mec.ui.COSMViewer;
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
import java.util.HashSet;


/**
 * class with default source implementation for cars
 *
 * @todo symbol painter should be moved to an own structure
 */
public class CCarRandomWayPoint extends IRandomWayPoint<ICar, ICarFactory, IGenerator>
{
    /**
     * image of the waypoint
     */
    private final transient BufferedImage m_initializeimage;
    /**
     * last zoom (if the zoom changed the image need to be resized)
     */
    private transient int m_lastZoom = 0;
    /**
     * current scaled image
     */
    private transient BufferedImage m_scaledimage;
    /**
     * map with targets
     */
    private transient Collection<IReturnSteppableTarget<ICar>> m_target = new HashSet()
    {{
            add( CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ) );
        }};


    /**
     * ctor
     *
     * @param p_position geo position
     * @param p_generator generator object
     * @param p_factory factory object
     * @param p_radius radius
     * @param p_color color
     */
    public CCarRandomWayPoint( final GeoPosition p_position, final IGenerator p_generator, final ICarFactory p_factory, final double p_radius,
            final Color p_color
    )
    {
        super( p_position, p_generator, p_factory, p_radius );
        m_initializeimage = this.initializeImage( 20, 34, p_color );
        m_scaledimage = m_initializeimage;
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
    public Collection<IReturnSteppableTarget<ICar>> getTargets()
    {
        return m_target;
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

    @Override
    public final void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
        if ( m_position == null )
            return;

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        final Ellipse2D l_circle = new Ellipse2D.Double(
                l_point.getX() - p_viewer.getViewportBounds().getX(), l_point.getY() - p_viewer.getViewportBounds().getY(), this.iconsize( p_viewer ),
                this.iconsize(
                        p_viewer
                )
        );

        if ( l_circle.contains( p_event.getX(), p_event.getY() ) )
            CSimulation.getInstance().getUIComponents().getInspector().set( this );
    }

    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        //if the zoom change calculate the new scaled image
        if ( p_viewer.getZoom() != m_lastZoom )
            m_scaledimage = this.getScaledImage(
                    m_initializeimage, (int) ( m_initializeimage.getWidth() * this.iconscale( p_viewer ) ),
                    (int) ( m_initializeimage.getHeight() * this.iconscale( p_viewer ) )
            );

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        p_graphic.drawImage( m_scaledimage, (int) l_point.getX() - m_scaledimage.getWidth() / 2, (int) l_point.getY() - m_scaledimage.getHeight(), null );
    }
}
