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
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.CEdge;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;


/**
 * class to create edge weights of the current traffic occurrence
 *
 * @see https://github.com/graphhopper/graphhopper/blob/master/docs/core/weighting.md
 */
public final class CTrafficJam implements Weighting
{
    /**
     * graph reference
     */
    private final CGraphHopper m_graph;
    /**
     * encoder
     */
    private final FlagEncoder m_encoder;
    /**
     * max speed value *
     */
    private final double m_maxspeed;
    /**
     * slope of the sigmoid function
     */
    private final double m_slope;
    /**
     * saddle point of the sigmoid function
     *
     * @note saddle / slope defines the saddle point
     */
    private final double m_saddle;
    /**
     * maximum jam cost
     */
    private final double m_jamcost;


    /**
     * ctor
     *
     * @param p_encoder flag encoder
     * @param p_graph graph
     */
    public CTrafficJam( final FlagEncoder p_encoder, final CGraphHopper p_graph )
    {
        this( p_encoder, p_graph, 8, 6, Integer.MAX_VALUE );
    }

    /**
     * ctor
     *
     * @param p_encoder flag encoder
     * @param p_graph graph
     */
    public CTrafficJam( final FlagEncoder p_encoder, final CGraphHopper p_graph, final double p_slope, final double p_saddle, final double p_jamcost )
    {
        m_saddle = p_saddle;
        m_slope = p_slope;
        m_jamcost = p_jamcost;

        m_graph = p_graph;
        m_encoder = p_encoder;
        m_maxspeed = p_encoder.getMaxSpeed();
    }


    @Override
    public final double getMinWeight( final double p_weight )
    {
        return p_weight / m_maxspeed;
    }

    @Override
    public double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse, final int p_nextprevedgeid )
    {
        final CEdge<ICar, ?> l_edge = m_graph.getEdge( p_edge );

        // get normalized density and scale range in [0, cost] with sigmoid function
        return ( p_edge.getDistance() / m_maxspeed ) +
               ( m_jamcost *
                 1 / ( 1 + Math.exp( -( m_slope * l_edge.getNumberOfObjects() / l_edge.getEdgeCells() - m_saddle ) ) )
               );
    }

    @Override
    public FlagEncoder getFlagEncoder()
    {
        return m_encoder;
    }

}
