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
     * ctor
     *
     * @param p_encoder flag encoder
     */
    public CTrafficJam( final FlagEncoder p_encoder, final CGraphHopper p_graph )
    {
        m_graph = p_graph;
        m_encoder = p_encoder;
    }

    @Override
    public final double getMinWeight( final double p_weight )
    {
        return 0;
    }

    @Override
    public double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse, final int p_nextprevedgeid )
    {
        final CEdge<ICar, ?> l_edge = m_graph.getEdge( p_edge );
        return Double.MAX_VALUE * (double) l_edge.getNumberOfObjects() / l_edge.getEdgeCells();
    }

    @Override
    public FlagEncoder getFlagEncoder()
    {
        return m_encoder;
    }

}
