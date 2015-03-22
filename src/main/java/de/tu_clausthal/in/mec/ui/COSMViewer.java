/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;


/**
 * openstreetmap viewer class - must be a singleton because we would to use only one instance with a global access
 *
 * @see https://github.com/msteiger/jxmapviewer2
 * @see https://today.java.net/pub/a/today/2007/10/30/building-maps-into-swing-app-with-jxmapviewer.html
 */
public class COSMViewer extends JXMapViewer
{
    /**
     * compounend painter
     */
    private CompoundPainter m_painter = new CompoundPainter<>();
    /**
     * mouse listener
     */
    private COSMMouseListener m_mouse = new COSMMouseListener();
    /**
     * Key Listener
     */
    private COSMKeyListener m_keylistener = new COSMKeyListener();


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
        this.setZoom( CConfiguration.getInstance().get().getZoom() );
        this.setCenterPosition( CConfiguration.getInstance().get().getViewpoint() );
        this.setAddressLocation( CConfiguration.getInstance().get().getViewpoint() );

        this.setOverlayPainter( m_painter );

        // new CenterMapListener(this)
        this.addMouseListener( m_mouse );
        this.addMouseMotionListener( m_mouse );
        this.addMouseListener( new PanMouseInputListener( this ) );
        this.addMouseWheelListener( new ZoomMouseWheelListenerCenter( this ) );
        this.addKeyListener( m_keylistener );

        this.reset();

        CBootstrap.afterOSMViewerInit( this );
    }

    /**
     * static method to get the OSM viewer from the current UI widget
     *
     * @return OSM Viewer
     * @bug UI frame
     */
    public static COSMViewer getSimulationOSM()
    {
        if ( !CSimulation.getInstance().hasUI() )
            return null;

        //return (COSMViewer) CSimulation.getInstance().getUIServer().getWidget( "OSM" );
        return null;
    }

    /**
     * resets the view *
     */
    public void reset()
    {
        this.setZoom( CConfiguration.getInstance().get().getZoom() );
        this.setCenterPosition( CConfiguration.getInstance().get().getViewpoint() );
        this.setAddressLocation( CConfiguration.getInstance().get().getViewpoint() );
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
     * Getter Mouse Listener
     */
    public COSMMouseListener getMouseListener()
    {
        return this.m_mouse;
    }

    /**
     * Getter Key Listener
     */
    public COSMKeyListener getKeyListener()
    {
        return m_keylistener;
    }

}