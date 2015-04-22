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
import java.util.Set;


/**
 * class of an adjacence matrix
 */
public class CAdjacencyMatrix<T, N>
{
    /**
     * map with data *
     */
    private final Map<Pair<T, T>, N> m_matrix = new HashMap();
    /** **/
    private final Map<T, Set<Pair<T, T>>> m_column = new HashMap<>();
    /** **/
    private final Map<T, Set<Pair<T, T>>> m_rows = new HashMap<>();


    /**
     * clears matrix
     */
    public void clear()
    {
        m_rows.clear();
        m_column.clear();
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
        m_matrix.remove( new ImmutablePair<T, T>( p_row, p_column ) );
    }

    /**
     * @param p_row
     * @param p_column
     * @param p_value
     */
    public void set( final T p_row, final T p_column, final N p_value )
    {
        m_matrix.put( new ImmutablePair<T, T>( p_row, p_column ), p_value );
    }

    /**
     * @param p_row
     * @param p_column
     * @return
     */
    public N get( final T p_row, final T p_column )
    {
        return m_matrix.get( new ImmutablePair<T, T>( p_row, p_column ) );
    }

    /**
     * @param p_row
     * @param p_column
     * @return
     */
    public boolean exist( final T p_row, final T p_column )
    {
        return m_matrix.containsKey( new ImmutablePair<T, T>( p_row, p_column ) );
    }


    /**
     * returns all row values
     *
     * @param p_row row value
     * @return hashmap with row data
     */
    public Map<Pair<T, T>, N> getRow( final T p_row )
    {
        if ( !m_rows.containsKey( p_row ) )
            return null;

        final Map<Pair<T, T>, N> l_data = new HashMap<>();
        for ( Pair<T, T> l_item : m_rows.get( p_row ) )
            l_data.put( l_item, m_matrix.get( l_item ) );

        return l_data;
    }


    /**
     * returns all column values
     *
     * @param p_column column value
     * @return hashmap with row data
     */
    public Map<Pair<T, T>, N> getColumn( final T p_column )
    {
        if ( !m_column.containsKey( p_column ) )
            return null;

        final Map<Pair<T, T>, N> l_data = new HashMap<>();
        for ( Pair<T, T> l_item : m_column.get( p_column ) )
            l_data.put( l_item, m_matrix.get( l_item ) );

        return l_data;
    }


    /**
     * returns a matrix
     *
     * @return matrix representation
     */
    public CCell[][] getMatrix()
    {
        final CCell<T, N>[][] l_matrix = new CCell[m_matrix.size()][m_matrix.size()];
        return l_matrix;
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
