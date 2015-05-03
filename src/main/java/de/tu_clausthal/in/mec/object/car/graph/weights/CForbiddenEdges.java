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

package de.tu_clausthal.in.mec.object.car.graph.weights;


import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.object.car.graph.CEdge;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;


/**
 * weight class for forbidden edges
 *
 * @see https://github.com/graphhopper/graphhopper/blob/master/docs/core/weighting.md
 */
public class CForbiddenEdges extends HashSet<Integer> implements IWeighting, Painter<JXMapViewer>
{
    /**
     * graph reference
     */
    private final CGraphHopper m_graph;
    /**
     * active flag *
     */
    private boolean m_active = false;
    /**
     * marked edge to allow mouse-interaction
     */
    private Integer m_reserveedge;
    /**
     * gradient paint
     */
    //private static final GradientPaint s_gradient = new GradientPaint(3, 3, Color.red, 5, 5, Color.black, true);


    /**
     * ctor
     *
     * @param p_graph reference to graph
     */
    public CForbiddenEdges( final CGraphHopper p_graph )
    {
        m_graph = p_graph;
    }

    /**
     * reserve an edge for an action
     *
     * @param p_edge edge
     */
    public final void reserve( final Integer p_edge )
    {
        m_reserveedge = p_edge;
    }

    /**
     * adds the reserved edge to the list
     */
    public final void add()
    {
        if ( m_reserveedge == null )
            return;

        this.add( m_reserveedge );
        m_reserveedge = null;
    }

    /**
     * removes a reserved edge from the list
     */
    public final void remove()
    {
        if ( m_reserveedge == null )
            return;

        this.remove( m_reserveedge );
        m_reserveedge = null;
    }


    @Override
    public final double getMinWeight( final double p_weight )
    {
        return 0;
    }

    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        return this.contains( p_edge ) ? Double.POSITIVE_INFINITY : 0;
    }

    @Override
    public final boolean isActive()
    {
        return m_active;
    }

    @Override
    public final void setActive( final boolean p_value )
    {
        m_active = p_value;
    }

    /**
     * @bug incomplete
     */
    @Override
    public void paint( final Graphics2D p_graphic, final JXMapViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_active )
            return;

        /*
        final Graphics2D l_graphic = (Graphics2D)p_graphic.create();
        l_graphic.setColor( Color.ORANGE );

        for( Integer l_item : this )
            this.paintRectangle( p_graphic, p_viewer, m_graph.getEdge( l_item.intValue() ) );

        l_graphic.dispose();
        */
    }

    /**
     * paints a rectangle
     *
     * @param p_graphic graphic object
     * @param p_viewer viewer object
     * @param p_edge edge object
     */
    private void paintRectangle( final Graphics2D p_graphic, final JXMapViewer p_viewer, final CEdge<?, ?> p_edge )
    {
        final Point2D l_start = p_viewer.convertGeoPositionToPoint( p_edge.getGeoPositions( 0 ) );
        final Point2D l_end = p_viewer.convertGeoPositionToPoint( p_edge.getGeoPositions( -1 ) );
        p_graphic.fillRect( (int) l_start.getX(), (int) l_start.getY(), (int) l_end.getX(), (int) l_end.getY() );
    }

}
