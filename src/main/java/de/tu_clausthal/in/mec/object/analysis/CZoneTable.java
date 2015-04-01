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

package de.tu_clausthal.in.mec.object.analysis;


import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * representation of a zone table - how many elemnts within a zone
 *
 * @todo must be tested with database layer
 * @see http://en.wikipedia.org/wiki/Trip_distribution
 */
public class CZoneTable<T, S>
{

    /**
     * map with zone names and zone definition *
     */
    protected final Map<String, Pair<S, Integer>> m_zones = new HashMap<>();
    /**
     * check object *
     */
    protected CValidation<T, S> m_validation = null;

    /**
     * ctor
     *
     * @param p_validate validation object
     */
    public CZoneTable( final CValidation<T, S> p_validate )
    {
        if ( p_validate == null ) throw new IllegalArgumentException( CCommon.getResourceString( this, "validation" ) );

        m_validation = p_validate;
    }

    /**
     * adds a new zone with the elements
     *
     * @param p_zonename unique zone name
     * @param p_data     zone dat
     */
    public final void put( final String p_zonename, final S p_data )
    {
        m_zones.put( p_zonename, new ImmutablePair<S, Integer>( p_data, new Integer( 0 ) ) );
    }

    /**
     * removes a zone
     *
     * @param p_zonename zone name
     */
    public final void remove( final String p_zonename )
    {
        m_zones.remove( p_zonename );
    }

    /**
     * removes all zones *
     */
    public final void clear()
    {
        m_zones.clear();
    }

    /**
     * resets all zones *
     */
    public final void reset()
    {
        for ( Pair<S, Integer> l_item : m_zones.values() )
            l_item.getRight().valueOf( 0 );
    }

    /**
     * counts a value
     *
     * @param p_data data values
     */
    public final void count( final T p_data )
    {
        for ( Pair<S, Integer> l_item : m_zones.values() )
            if ( m_validation.validate( p_data, l_item.getLeft() ) )
            {
                l_item.getRight().valueOf( l_item.getRight().intValue() + 1 );
                return;
            }
    }

    /**
     * returns the table with all values
     *
     * @return zonename with count values
     */
    public final Map<String, Integer> getTable()
    {
        final Map<String, Integer> l_table = new HashMap<>();
        for ( Map.Entry<String, Pair<S, Integer>> l_item : m_zones.entrySet() )
            l_table.put( l_item.getKey(), l_item.getValue().getRight() );
        return l_table;
    }


    /**
     * interface of a validation class *
     */
    public interface CValidation<V, W>
    {

        public boolean validate( V p_zone, W p_input );

    }

}
