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
 **/

package de.tu_clausthal.in.mec.object.car.graph.weights;

import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * combination weight, to use different weights in combination
 */
public class CCombine implements Weighting, Map<String, IWeighting>
{
    /**
     * map with weights *
     */
    protected final Map<String, IWeighting> m_weights = new HashMap<>();


    @Override
    public final double getMinWeight( final double p_weight )
    {
        if ( m_weights.isEmpty() ) return 0;

        double l_min = Double.POSITIVE_INFINITY;
        for ( IWeighting l_item : m_weights.values() )
            if ( l_item.isActive() ) l_min = Math.min( l_min, l_item.getMinWeight( p_weight ) );

        return l_min;
    }

    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        if ( m_weights.isEmpty() ) return 0;

        double l_max = 0;
        for ( IWeighting l_item : m_weights.values() )
            if ( l_item.isActive() ) l_max += l_item.calcWeight( p_edge, p_reverse );

        return l_max;
    }

    @Override
    public final int size()
    {
        return m_weights.size();
    }

    @Override
    public final boolean isEmpty()
    {
        return m_weights.isEmpty();
    }

    @Override
    public final boolean containsKey( Object p_key )
    {
        return m_weights.containsKey( p_key );
    }

    @Override
    public final boolean containsValue( Object p_value )
    {
        return m_weights.containsValue( p_value );
    }

    @Override
    public final IWeighting get( Object p_key )
    {
        return m_weights.get( p_key );
    }

    @Override
    public final IWeighting put( String p_key, IWeighting p_value )
    {
        return m_weights.put( p_key, p_value );
    }

    @Override
    public final IWeighting remove( Object p_key )
    {
        return m_weights.remove( p_key );
    }

    @Override
    public final void putAll( Map<? extends String, ? extends IWeighting> p_map )
    {
        m_weights.putAll( p_map );
    }

    @Override
    public final void clear()
    {
        m_weights.clear();
    }

    @Override
    public final Set<String> keySet()
    {
        return m_weights.keySet();
    }

    @Override
    public final Collection<IWeighting> values()
    {
        return m_weights.values();
    }

    @Override
    public final Set<Entry<String, IWeighting>> entrySet()
    {
        return m_weights.entrySet();
    }

}
