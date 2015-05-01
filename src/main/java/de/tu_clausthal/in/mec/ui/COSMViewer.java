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

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * openstreetmap viewer class - must be a singleton because we would to use only one instance with a global access
 *
 * @see https://github.com/msteiger/jxmapviewer2
 * @see https://today.java.net/pub/a/today/2007/10/30/building-maps-into-swing-app-with-jxmapviewer.html
 */
public class COSMViewer extends JXMapViewer
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * route painter
     */
    private final CRoutePainter m_routepainter = new CRoutePainter();
    /**
     * compounend painter
     */
    private CompoundPainter<JXMapViewer> m_painter = new CompoundPainter<>();
    /**
     * clickable layer
     */
    private EClickableLayer m_clickablelayer = EClickableLayer.Sources;

    /**
     * ctor with loading configuration defaults and listener definition
     */
    public COSMViewer()
    {
        super();

        final TileFactoryInfo l_info = new OSMTileFactoryInfo();
        final DefaultTileFactory l_tileFactory = new DefaultTileFactory( l_info );
        l_tileFactory.setThreadPoolSize( Runtime.getRuntime().availableProcessors() );

        LocalResponseCache.installResponseCache( l_info.getBaseURL(), CConfiguration.getInstance().getLocation( "root" ), false );
        this.setTileFactory( l_tileFactory );
        this.setZoom( CConfiguration.getInstance().get().<Integer>get( "ui/zoom" ) );
        this.setCenterPosition( CConfiguration.getInstance().get().<GeoPosition>get( "ui/geoposition" ) );
        this.setAddressLocation( CConfiguration.getInstance().get().<GeoPosition>get( "ui/geoposition" ) );

        this.setOverlayPainter( m_painter );
        m_painter.addPainter( m_routepainter );

        final MouseInputListener l_mouse = new COSMMouseListener( this, m_clickablelayer );
        this.addMouseListener( l_mouse );
        this.addMouseMotionListener( l_mouse );
        this.addMouseWheelListener( new ZoomMouseWheelListenerCenter( this ) );


        this.resetConfiguration();

        CBootstrap.afterOSMViewerInit( this );
    }

    /**
     * resets the view *
     */
    public final void resetConfiguration()
    {
        this.setZoom( CConfiguration.getInstance().get().<Integer>get( "ui/zoom" ) );
        this.setCenterPosition( CConfiguration.getInstance().get().<GeoPosition>get( "ui/geoposition" ) );
        this.setAddressLocation( CConfiguration.getInstance().get().<GeoPosition>get( "ui/geoposition" ) );
    }

    /**
     * sets a new route
     * @param p_route route list
     */
    public void paintRoute( final List<Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke>> p_route )
    {
        m_routepainter.setRoute( p_route );
    }

    /**
     * stores the current configuration
     */
    public final void setConfiguration()
    {
        CConfiguration.getInstance().get().<Integer>set( "ui/zoom", this.getZoom() );
        CConfiguration.getInstance().get().<GeoPosition>set( "ui/geoposition", this.getCenterPosition() );
    }


    /**
     * returns the compounend painter
     *
     * @return painter
     */
    public final CompoundPainter<?> getCompoundPainter()
    {
        return m_painter;
    }


    /**
     * UI method - returns list of clickable layer names and its state
     *
     * @return map with layer list
     */
    private Map<String, Object> web_static_listClickableLayer()
    {
        final Map<String, Object> l_layer = new HashMap<>();
        for ( EClickableLayer l_item : EClickableLayer.class.getEnumConstants() )
            // name() cannot be overwritten but it is used for the ID
            l_layer.put( l_item.toString(), CCommon.getMap( "id", l_item.name(), "click", l_item == m_clickablelayer ) );
        return l_layer;
    }

    /**
     * UI method - sets the current clickable layer
     */
    private void web_static_setClickableLayer( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "id" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "nolayername" ) );

        m_clickablelayer = EClickableLayer.valueOf( (String) p_data.get( "id" ) );
    }


    /**
     * enum clickable layer
     */
    public enum EClickableLayer
    {
        /**
         * source layer *
         */
        Sources( CCommon.getResourceString( EClickableLayer.class, "source" ) ),
        /**
         * forbidden edges layer *
         */
        ForbiddenEdges( CCommon.getResourceString( EClickableLayer.class, "forbiddenedges" ) );


        /**
         * internal string value *
         */
        private final String m_stringvalue;

        /**
         * private ctor
         *
         * @param p_value string
         */
        private EClickableLayer( final String p_value )
        {
            m_stringvalue = p_value;
        }

        /**
         * overload to string - name cannot be overwritten
         *
         * @return string name
         */
        @Override
        public String toString()
        {
            return m_stringvalue;
        }
    }


    /**
     * routing painter class
     */
    private class CRoutePainter implements ActionListener, Painter<JXMapViewer>
    {
        /**
         * timer
         */
        private final Timer m_timer = new Timer( CConfiguration.getInstance().get().<Integer>get( "ui/routepainterdelay" ), this );
        /**
         * route list with painting structure *
         */
        private List<Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke>> m_route;
        /**
         * alpha value
         */
        private float m_alpha = 1;

        /**
         * sets the route and restart the opacity timer
         *
         * @param p_route route list
         */
        public void setRoute( final List<Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke>> p_route )
        {
            m_timer.stop();
            m_alpha = 1;
            m_route = p_route;
            m_timer.start();
        }

        @Override
        public void paint( final Graphics2D p_graphic, final JXMapViewer p_viewer, final int p_width, final int p_height )
        {
            if ( ( m_route == null ) || ( m_route.isEmpty() ) )
                return;

            final Graphics2D l_graphic = (Graphics2D) p_graphic.create();
            l_graphic.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, m_alpha ) );
            l_graphic.translate( -p_viewer.getViewportBounds().x, -p_viewer.getViewportBounds().y );
            l_graphic.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            for ( Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke> l_item : m_route )
            {
                l_graphic.setColor( l_item.getMiddle() );
                l_graphic.setStroke( l_item.getRight() );

                final Point2D l_start = p_viewer.getTileFactory().geoToPixel( l_item.getLeft().getLeft(), p_viewer.getZoom() );
                final Point2D l_end = p_viewer.getTileFactory().geoToPixel( l_item.getLeft().getRight(), p_viewer.getZoom() );
                l_graphic.drawLine( (int) l_start.getX(), (int) l_start.getY(), (int) l_end.getX(), (int) l_end.getY() );
            }

            l_graphic.dispose();
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            m_alpha += -0.01;
            if ( m_alpha <= 0 )
            {
                m_alpha = 0;
                m_timer.stop();
            }
            COSMViewer.this.repaint();
        }
    }

}
