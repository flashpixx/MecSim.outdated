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

package de.tu_clausthal.in.mec.common;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * class of an adjacence matrix
 */
@JsonSerialize( using = CAdjacencyMatrix.CJson.class )
public final class CAdjacencyMatrix<T, N> extends HashMap<Pair<T, T>, N>
{
    /**
     * column name
     */
    private final String m_colname;
    /**
     * set of keys
     */
    private final Set<T> m_keys = new HashSet<>();
    /**
     * row name
     */
    private final String m_rowname;

    /**
     * ctor
     */
    public CAdjacencyMatrix()
    {
        this( "row", "col" );
    }

    /**
     * ctor
     *
     * @param p_rowname row name
     * @param p_colname column name
     */
    public CAdjacencyMatrix( final String p_rowname, final String p_colname )
    {
        super();
        m_rowname = p_rowname;
        m_colname = p_colname;
    }

    /**
     * @param p_row row element
     * @param p_column column element
     * @return boolean for existance
     */
    public boolean exist( final T p_row, final T p_column )
    {
        return this.containsKey( new ImmutablePair<>( p_row, p_column ) );
    }

    /**
     * @param p_row row element
     * @param p_column column element
     * @return value
     */
    public N get( final T p_row, final T p_column )
    {
        return this.get( new ImmutablePair<>( p_row, p_column ) );
    }

    @Override
    public N put( final Pair<T, T> p_key, final N p_value )
    {
        m_keys.add( p_key.getKey() );
        m_keys.add( p_key.getValue() );
        return super.put( p_key, p_value );
    }

    @Override
    public N remove( final Object p_key )
    {
        throw new IllegalStateException( CCommon.getResourceString( this, "remove" ) );
    }

    /**
     * @param p_row row element
     * @param p_column column element
     * @param p_value value
     */
    public void set( final T p_row, final T p_column, final N p_value )
    {
        m_keys.add( p_row );
        m_keys.add( p_column );
        this.put( new ImmutablePair<>( p_row, p_column ), p_value );
    }

    /**
     * serializer for Json access
     */
    public static class CJson extends JsonSerializer<CAdjacencyMatrix<?, ?>>
    {

        @Override
        public void serialize( final CAdjacencyMatrix<?, ?> p_matrix, final JsonGenerator p_generator, final SerializerProvider p_serializer
        ) throws IOException
        {
            p_generator.writeStartObject();
            p_generator.writeArrayFieldStart( "cells" );
            for ( final Map.Entry<?, ?> l_item : p_matrix.entrySet() )
            {
                p_generator.writeStartObject();
                p_generator.writeObjectField( p_matrix.m_rowname, ( (Pair<?, ?>) l_item.getKey() ).getLeft() );
                p_generator.writeObjectField( p_matrix.m_colname, ( (Pair<?, ?>) l_item.getKey() ).getRight() );
                p_generator.writeObjectField( "value", l_item.getValue() );
                p_generator.writeEndObject();
            }
            p_generator.writeEndArray();
            p_generator.writeEndObject();
        }

    }

}
