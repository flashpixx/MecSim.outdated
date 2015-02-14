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

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
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
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;

    /**
     * compounend painter
     */
    private CompoundPainter m_painter = new CompoundPainter<>();
    /**
     * mouse listener
     */
    private COSMMouseListener m_mouse = new COSMMouseListener();


    /**
     * private ctor with loading configuration defaults and listener definition
     */
    public COSMViewer()
    {
        super();

        TileFactoryInfo l_info = new OSMTileFactoryInfo();
        DefaultTileFactory l_tileFactory = new DefaultTileFactory( l_info );
        l_tileFactory.setThreadPoolSize( Runtime.getRuntime().availableProcessors() );

        LocalResponseCache.installResponseCache( l_info.getBaseURL(), CConfiguration.getInstance().getConfigDir(), false );
        this.setTileFactory( l_tileFactory );
        this.setZoom( CConfiguration.getInstance().get().getZoom() );
        this.setCenterPosition( CConfiguration.getInstance().get().getViewpoint() );
        this.setAddressLocation( CConfiguration.getInstance().get().getViewpoint() );

        this.setOverlayPainter( m_painter );

        this.addMouseListener( m_mouse ); // new CenterMapListener(this)
        this.addMouseMotionListener( m_mouse );
        this.addMouseListener( new PanMouseInputListener( this ) );
        this.addMouseWheelListener( new ZoomMouseWheelListenerCenter( this ) );
        this.addKeyListener( new PanKeyListener( this ) );

        CBootstrap.AfterOSMViewerInit( this );
    }


    /**
     * static method to get the OSM viewer from the current UI widget
     *
     * @return OSM Viewer
     */
    public static COSMViewer getSimulationOSM()
    {
        if ( !CSimulation.getInstance().hasUI() )
            return null;

        return (COSMViewer) CSimulation.getInstance().getUI().getWidget( "OSM" );
    }


    /**
     * returns the compounend painter
     *
     * @return painter
     */
    public CompoundPainter<?> getCompoundPainter()
    {
        return m_painter;
    }

}
