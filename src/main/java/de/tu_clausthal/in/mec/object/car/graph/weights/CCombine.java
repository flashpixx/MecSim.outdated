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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * combination weight, to use different weights in combination
 */
public class CCombine implements Weighting, Map<String, Weighting>
{
    protected final Map<String, Weighting> m_weights = new HashMap<>();


    @Override
    public double getMinWeight( final double p_weight )
    {
        double l_min = Double.POSITIVE_INFINITY;

        for ( Weighting l_item : m_weights.values() )
            l_min = Math.min( l_min, l_item.getMinWeight( p_weight ) );

        return l_min;
    }

    @Override
    public double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        double l_max = Double.NEGATIVE_INFINITY;

        for ( Weighting l_item : m_weights.values() )
            l_max = Math.max( l_max, l_item.calcWeight( p_edge, p_reverse ) );

        return l_max;
    }

    @Override
    public int size()
    {
        return m_weights.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_weights.isEmpty();
    }

    @Override
    public boolean containsKey( Object p_key )
    {
        return m_weights.containsKey( p_key );
    }

    @Override
    public boolean containsValue( Object p_value )
    {
        return m_weights.containsValue( p_value );
    }

    @Override
    public Weighting get( Object p_key )
    {
        return m_weights.get( p_key );
    }

    @Override
    public Weighting put( String p_key, Weighting p_value )
    {
        return m_weights.put( p_key, p_value );
    }

    @Override
    public Weighting remove( Object p_key )
    {
        return m_weights.remove( p_key );
    }

    @Override
    public void putAll( Map<? extends String, ? extends Weighting> p_map )
    {
        m_weights.putAll( p_map );
    }

    @Override
    public void clear()
    {
        m_weights.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return m_weights.keySet();
    }

    @Override
    public Collection<Weighting> values()
    {
        return m_weights.values();
    }

    @Override
    public Set<Entry<String, Weighting>> entrySet()
    {
        return m_weights.entrySet();
    }

}
