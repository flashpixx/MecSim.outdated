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

import java.util.HashSet;
import java.util.Set;


/**
 * weight class for forbidden edges
 *
 * @see https://github.com/graphhopper/graphhopper/blob/master/docs/core/weighting.md
 */
public class CForbiddenEdges implements IWeighting
{

    /**
     * list with edges *
     */
    private final Set<EdgeIteratorState> m_forbidden = new HashSet<>();
    /**
     * active flag *
     */
    private boolean m_active = false;


    /**
     * returns the set of edges
     *
     * @return set with edges
     */
    public Set<EdgeIteratorState> getEdges()
    {
        return m_forbidden;
    }


    @Override
    public final double getMinWeight( final double p_weight )
    {
        return 0;
    }

    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        return m_forbidden.contains( p_edge ) ? Double.POSITIVE_INFINITY : 0;
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
}
