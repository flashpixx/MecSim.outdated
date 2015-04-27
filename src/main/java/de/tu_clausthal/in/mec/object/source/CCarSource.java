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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.source.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.source.generator.IGenerator;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppableTarget;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;


/**
 * class with default source implementation for cars
 */
public class CCarSource extends ISource<ICar, ICarFactory, IGenerator>
{
    /**
     * waypoint color
     */
    private final Color m_color = Color.BLACK;
    /**
     * image of the waypoint
     */
    private transient BufferedImage m_image;
    /**
     * last zoom (if the zoom changed the image need to be resized)
     */
    private int m_lastZoom = 0;
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
     * @param p_position position
     */
    public CCarSource( final GeoPosition p_position )
    {
        super( p_position );
    }

    /**
     * ctor
     *
     * @param p_position position
     * @param p_generator generator
     * @param p_factory car factory
     */
    public CCarSource( final GeoPosition p_position, final IGenerator p_generator, final ICarFactory p_factory )
    {
        super( p_position, p_generator, p_factory );
    }

    @Override
    public Collection<IReturnSteppableTarget<ICar>> getTargets()
    {
        return m_target;
    }

    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( m_image == null )
            return;

        //if the zoom change calculate the new scaled image
        if ( p_viewer.getZoom() != m_lastZoom )
        {
            int l_newWidth = 20;
            int l_newHeight = 34;

            l_newHeight = (int) ( l_newHeight * this.iconscale( p_viewer ) );
            l_newWidth = (int) ( l_newWidth * this.iconscale( p_viewer ) );

            this.setImage( l_newWidth, l_newHeight );
        }

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        p_graphic.drawImage( m_image, (int) l_point.getX() - m_image.getWidth() / 2, (int) l_point.getY() - m_image.getHeight(), null );
    }

    /**
     * creates an image with a specific scale
     *
     * @param p_width image width
     * @param p_height image height
     */
    public void setImage( final int p_width, final int p_height )
    {
        if ( m_color == null )
            return;

        try
        {
            BufferedImage l_image = ImageIO.read( DefaultWaypointRenderer.class.getResource( "/images/standard_waypoint.png" ) );
            l_image = this.getScaledImage( l_image, p_width, p_height );

            // modify blue value to the color of the waypoint
            m_image = new BufferedImage( l_image.getColorModel(), l_image.copyData( null ), l_image.isAlphaPremultiplied(), null );
            for ( int i = 0; i < l_image.getHeight(); i++ )
                for ( int j = 0; j < l_image.getWidth(); j++ )
                {
                    final Color l_color = new Color( l_image.getRGB( j, i ) );
                    if ( l_color.getBlue() > 0 )
                        m_image.setRGB( j, i, m_color.getRGB() );
                }

        }
        catch ( final Exception l_exception )
        {
            CLogger.warn( l_exception );
        }
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

    /**
     * creates the image
     */
    public void setImage()
    {
        if ( m_color == null )
            return;

        try
        {
            final BufferedImage l_image = ImageIO.read( DefaultWaypointRenderer.class.getResource( "/images/standard_waypoint.png" ) );

            // modify blue value to the color of the waypoint
            m_image = new BufferedImage( l_image.getColorModel(), l_image.copyData( null ), l_image.isAlphaPremultiplied(), null );
            for ( int i = 0; i < l_image.getHeight(); i++ )
                for ( int j = 0; j < l_image.getWidth(); j++ )
                {
                    final Color l_color = new Color( l_image.getRGB( j, i ) );
                    if ( l_color.getBlue() > 0 )
                        m_image.setRGB( j, i, m_color.getRGB() );
                }

        }
        catch ( final Exception l_exception )
        {
            CLogger.warn( l_exception );
        }
    }
}
