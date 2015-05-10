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

package de.tu_clausthal.in.mec.common;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * class of an adjacence matrix
 */
public class CAdjacencyMatrix<T, N>
{
    /**
     * map with data *
     */
    private final Map<Pair<T, T>, N> m_matrix = new HashMap<>();


    /**
     * clears matrix
     */
    public void clear()
    {
        m_matrix.clear();
    }


    /**
     * remove a full cell
     *
     * @param p_row
     * @param p_column
     */
    public void remove( final T p_row, final T p_column )
    {
        final Pair<T, T> l_key = new ImmutablePair<>( p_row, p_column );

        m_matrix.remove( l_key );
    }

    /**
     * @param p_row
     * @param p_column
     * @param p_value
     */
    public void set( final T p_row, final T p_column, final N p_value )
    {
        m_matrix.put( new ImmutablePair<>( p_row, p_column ), p_value );
    }

    /**
     * @param p_row
     * @param p_column
     * @return
     */
    public N get( final T p_row, final T p_column )
    {
        return m_matrix.get( new ImmutablePair<>( p_row, p_column ) );
    }

    /**
     * @param p_row
     * @param p_column
     * @return
     */
    public boolean exist( final T p_row, final T p_column )
    {
        return m_matrix.containsKey( new ImmutablePair<>( p_row, p_column ) );
    }


    /**
     * cell element representation of a graph element
     */
    public class CCell<P, M>
    {
        /**
         * row key *
         */
        private final P m_rowkey;
        /**
         * column key *
         */
        private final P m_columnkey;
        /**
         * value *
         */
        private final M m_value;

        /**
         * @param p_rowkey row key
         * @param p_columnkey column key
         * @param p_value value
         */
        public CCell( final P p_rowkey, final P p_columnkey, final M p_value )
        {
            m_rowkey = p_rowkey;
            m_columnkey = p_columnkey;
            m_value = p_value;
        }

        /**
         * returns row key
         *
         * @return row key
         */
        public P getRowKey()
        {
            return m_rowkey;
        }


        /**
         * returns column key
         *
         * @return column key
         */
        public P getColumnKey()
        {
            return m_columnkey;
        }


        /**
         * returns value
         *
         * @return value
         */
        public M getValue()
        {
            return m_value;
        }

    }

}
