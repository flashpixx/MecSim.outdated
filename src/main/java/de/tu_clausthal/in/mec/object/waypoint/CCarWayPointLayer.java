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

package de.tu_clausthal.in.mec.object.waypoint;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPoint;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;

import java.awt.*;


/**
 * layer with all sources
 */
public class CCarWayPointLayer extends IMultiLayer<IWayPoint<ICar>>
{

    @Override
    public final int getCalculationIndex()
    {
        return 2;
    }

    @Override
    public final void release()
    {
        for ( IWayPoint<?> l_item : m_data )
            l_item.release();
    }

    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_visible )
            return;

        //paint sources
        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        p_graphic.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( IWayPoint<?> l_source : this )
            l_source.paint( p_graphic, p_viewer, p_width, p_height );
        ;
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * removes a source
     *
     * @param p_source source which should be removed
     */
    public final void removeSource( final IWayPoint<?> p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidsource" ) );

        p_source.release();
        this.remove( p_source );
    }

    /**
     * creates a new atom target
     *
     * @param p_source source which should be removed
     */
    public final void createTarget( final IWayPoint<?> p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidsource" ) );

        this.repaintOSM();
    }

    /**
     * after a target was created, OSM need to be repainted
     */
    private final void repaintOSM()
    {
        try
        {
            CSimulation.getInstance().getUIComponents().getUI().<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().repaint();
        }
        catch ( Exception l_exception )
        {
        }
    }

}
