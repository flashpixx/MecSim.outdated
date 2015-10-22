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

package de.tu_clausthal.in.mec.object.car.graph.weights;


import com.graphhopper.routing.util.FlagEncoder;
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
 * @note the hashset defines the set over the edge ID
 * @see https://github.com/graphhopper/graphhopper/blob/master/docs/core/weighting.md
 */
@SuppressWarnings( "serial" )
public final class CForbiddenEdges extends HashSet<Integer> implements IWeighting, Painter<JXMapViewer>
{
    /**
     * stroke definition
     */
    private static final Stroke s_stroke = new BasicStroke( 5 );
    /**
     * active flag *
     */
    private boolean m_active = false;
    /**
     * graph reference
     */
    private final CGraphHopper m_graph;
    /**
     * marked edge to allow mouse-interaction
     */
    private Integer m_reserveedge;

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
     * adds the reserved edge to the list
     */
    public final void add()
    {
        if ( m_reserveedge == null )
            return;

        this.add( m_reserveedge );
        this.clearReserve();
    }

    /**
     * clears the reserve edge
     */
    public final void clearReserve()
    {
        m_reserveedge = null;
    }

    @Override
    public final double getMinWeight( final double p_weight )
    {
        return 0;
    }

    @Override
    public double calcWeight( final EdgeIteratorState p_edgeIteratorState, final boolean p_b, final int p_i )
    {
        return 0;
    }

    @Override
    public FlagEncoder getFlagEncoder()
    {
        return null;
    }

    /*
    @bug
    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        return this.contains( p_edge.getEdge() ) ? Double.POSITIVE_INFINITY : 0;
    }
    */

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

    @Override
    public void paint( final Graphics2D p_graphic, final JXMapViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_active )
            return;

        p_graphic.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        p_graphic.setStroke( s_stroke );

        p_graphic.setColor( Color.RED );
        for ( final Integer l_item : this )
            this.paintLine( p_graphic, p_viewer, l_item );

        p_graphic.setColor( Color.BLUE );
        if ( m_reserveedge != null )
            this.paintLine( p_graphic, p_viewer, m_reserveedge );
    }

    /**
     * removes a reserved edge from the list
     */
    public final void remove()
    {
        if ( m_reserveedge == null )
            return;

        this.remove( m_reserveedge );
        this.clearReserve();
    }

    /**
     * reserve an edge for an action
     *
     * @param p_edge edge ID
     */
    public final void reserve( final Integer p_edge )
    {
        m_reserveedge = p_edge;
    }

    /**
     * removes / adds the reserved edge
     */
    public final void swap()
    {
        if ( m_reserveedge == null )
            return;

        if ( this.contains( m_reserveedge ) )
            this.remove( m_reserveedge );
        else
            this.add( m_reserveedge );

        this.clearReserve();
    }

    /**
     * removes / adds the reserved edge
     */
    public final void swap( final Integer p_edge )
    {
        if ( this.contains( p_edge ) )
            this.remove( p_edge );
        else
            this.add( p_edge );
    }

    /**
     * paints the line
     *
     * @param p_graphic graphic reference
     * @param p_viewer viewer reference
     * @param p_edge edge ID
     */
    private void paintLine( final Graphics2D p_graphic, final JXMapViewer p_viewer, final int p_edge )
    {
        final CEdge<?, ?> l_edge = m_graph.getEdge( m_graph.getEdgeIterator( p_edge ) );
        if ( l_edge == null )
            return;

        final Point2D l_start = p_viewer.convertGeoPositionToPoint( l_edge.getGeoPositions( 0 ) );
        final Point2D l_end = p_viewer.convertGeoPositionToPoint( l_edge.getGeoPositions( -1 ) );

        p_graphic.drawLine( (int) l_start.getX(), (int) l_start.getY(), (int) l_end.getX(), (int) l_end.getY() );
    }
}
