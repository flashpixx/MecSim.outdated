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

import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;

import java.util.HashMap;


/**
 * combination weight, to use different weights in combination
 */
@SuppressWarnings( "serial" )
public class CCombine<T extends Enum> extends HashMap<T, IWeighting> implements Weighting
{

    @Override
    public final double getMinWeight( final double p_weight )
    {
        if ( this.isEmpty() )
            return 0;

        double l_min = Double.POSITIVE_INFINITY;
        for ( final IWeighting l_item : this.values() )
            if ( l_item.isActive() )
                l_min = Math.min( l_min, l_item.getMinWeight( p_weight ) );

        return l_min;
    }

    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        if ( this.isEmpty() )
            return 0;

        double l_max = 0;
        for ( final IWeighting l_item : this.values() )
            if ( l_item.isActive() )
                l_max += l_item.calcWeight( p_edge, p_reverse );

        return l_max;
    }

}
