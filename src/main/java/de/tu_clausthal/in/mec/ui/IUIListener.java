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

import de.tu_clausthal.in.mec.simulation.CSimulation;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * class to handle UI events with encapsulate the default lister structures
 */
public abstract class IUIListener implements MouseListener
{

    /**
     * maximum zoom value *
     */
    protected static final int c_maxzoom = 9;
    /**
     * minimum icon size *
     */
    protected static final int c_miniconsize = 3;


    /**
     * ctor to register component on the viewer
     */
    public IUIListener()
    {
        if ( CSimulation.getInstance().hasUI() )
            CSimulation.getInstance().getUI().<CSwingWrapper<COSMViewer>>getTab( "OSM" ).getComponent().addMouseListener( this );
    }

    /**
     * returns the icon size
     *
     * @param p_viewer viewer object
     * @return circle size
     */
    protected final int iconsize( final JXMapViewer p_viewer )
    {
        return Math.max( c_maxzoom - p_viewer.getZoom(), c_miniconsize );
    }

    /**
     * returns the normalized zoom level
     *
     * @param p_viewer viewer object
     * @return value in [0,1] with scale of the current zoom level
     */
    protected final double iconscale( final JXMapViewer p_viewer )
    {
        return iconsize( p_viewer ) / (double) ( c_maxzoom - c_miniconsize );
    }

    /**
     * click method which is called by a click on the object
     *
     * @param p_event  mouse event
     * @param p_viewer viewer
     */
    public void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
    }

    /**
     * release of the event handler
     */
    public void release()
    {
        CSimulation.getInstance().getUI().<CSwingWrapper<COSMViewer>>getTab( "OSM" ).getComponent().removeMouseListener( this );
    }

    @Override
    public void mouseClicked( final MouseEvent p_event )
    {
    }

    @Override
    public final void mousePressed( final MouseEvent p_event )
    {
        if ( SwingUtilities.isLeftMouseButton( p_event ) )
            this.onClick( p_event, CSimulation.getInstance().getUI().<CSwingWrapper<COSMViewer>>getTab( "OSM" ).getComponent() );
    }

    @Override
    public void mouseReleased( final MouseEvent p_event )
    {
    }

    @Override
    public void mouseEntered( final MouseEvent p_event )
    {
    }

    @Override
    public void mouseExited( final MouseEvent p_event )
    {
    }

}
