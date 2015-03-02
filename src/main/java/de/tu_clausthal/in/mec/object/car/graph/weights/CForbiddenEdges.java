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

package de.tu_clausthal.in.mec.object.car.graph.weights;


import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;

import java.util.HashSet;
import java.util.Set;


/**
 * weight class for forbidden edges
 */
public class CForbiddenEdges implements Weighting
{
    /**
     * list with edges *
     */
    protected final Set<EdgeIteratorState> m_forbidden = new HashSet<>();


    /**
     * adds a new edge
     *
     * @param p_edge edge
     */
    public final void setEdge( final EdgeIteratorState p_edge )
    {
        m_forbidden.add( p_edge );
    }


    /**
     * removes an edge
     *
     * @param p_edge edge
     */
    public final void removeEdge( final EdgeIteratorState p_edge )
    {
        m_forbidden.remove( p_edge );
    }


    @Override
    public final double getMinWeight( double p_weight )
    {
        return 0;
    }

    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        return m_forbidden.contains( p_edge ) ? Double.POSITIVE_INFINITY : 0;
    }
}
