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

package de.tu_clausthal.in.mec.object.mas.inconsistency;

/**
 * generic discrete metric
 *
 * @see http://mathworld.wolfram.com/DiscreteMetric.html
 */
public class CDiscreteMetric<T> implements IMetric<T>
{

    @Override
    public double calculate( final T p_first, final T p_second )
    {
        // equal objects create zero value
        if ( p_first.equals( p_second ) )
            return 0;


        // create aggregate belief-base
        /*
        final Set<N> l_aggregate = new HashSet<N>()
        {{
                addAll( p_current.getBeliefs() );
                addAll( p_other.getBeliefs() );
            }};
        */

        // difference of contradiction is the sum of difference of contradictions on each belief-base (closed-world-assumption)
        //return new Integer( ( ( l_aggregate.size() - p_current.getBeliefs().size() ) + ( l_aggregate.size() - p_other.getBeliefs().size() ) ) );

        return 1;

    }
}
