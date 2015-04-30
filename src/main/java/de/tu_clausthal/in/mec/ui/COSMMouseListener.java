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

import de.tu_clausthal.in.mec.object.waypoint.CCarWayPointLayer;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarRandomWayPoint;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;


/**
 * mouse listener for JxViewer
 */
class COSMMouseListener extends PanMouseInputListener
{
    /**
     * active layer
     */
    private COSMViewer.EClickableLayer m_currentlayer;


    /**
     * ctor
     *
     * @param p_viewer OSM viewer reference
     * @param p_activelayer reference to the active layer
     */
    public COSMMouseListener( final JXMapViewer p_viewer, COSMViewer.EClickableLayer p_activelayer )
    {
        super( p_viewer );
        m_currentlayer = p_activelayer;
    }

    /**
     * @bug incomplete - error messages
     */
    @Override
    public void mouseClicked( final MouseEvent p_event )
    {
        if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 2 ) )
            ( (CCarWayPointLayer) CSimulation.getInstance().getWorld().get( "Car WayPoints" ) ).add(
                    new CCarRandomWayPoint(
                            this.getMouseGeoPosition(
                                    p_event, (COSMViewer) p_event.getSource()
                            ), 0.5, Color.red
                    )
            );
    }


    /**
     * returns the geoposition of a mouse position
     *
     * @param p_event mouse event
     * @param p_viewer OSM viewer
     * @return geoposition
     */
    private GeoPosition getMouseGeoPosition( final MouseEvent p_event, final COSMViewer p_viewer )
    {
        final Point2D l_position = this.getMousePosition( p_event, p_viewer );
        return p_viewer.getTileFactory().pixelToGeo( l_position, p_viewer.getZoom() );
    }

    /**
     * returns the 2D position of a mouse position
     *
     * @param p_event mouse event
     * @param p_viewer OSM viewer
     * @return point
     */
    private Point2D getMousePosition( final MouseEvent p_event, final COSMViewer p_viewer )
    {
        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        return new Point( l_viewportBounds.x + p_event.getPoint().x, l_viewportBounds.y + p_event.getPoint().y );
    }

}
