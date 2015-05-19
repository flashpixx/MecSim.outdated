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


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * class of an adjacence matrix
 */
@JsonSerialize( using = CAdjacencyMatrix.CJson.class )
public class CAdjacencyMatrix<T, N> extends HashMap<Pair<T, T>, N>
{

    /**
     * column name
     */
    private final String m_colname;
    /**
     * row name
     */
    private final String m_rowname;
    /**
     * option for serializing result
     */
    private final ESerialzeOutput m_type;


    /**
     * ctor
     */
    public CAdjacencyMatrix()
    {
        this( "row", "col", ESerialzeOutput.Matrix );
    }


    /**
     * ctor
     *
     * @param p_type serializing type
     */
    public CAdjacencyMatrix( final ESerialzeOutput p_type )
    {
        this("row", "col", p_type);
    }

    /**
     * ctor
     *
     * @param p_rowname row name
     * @param p_colname column name
     */
    public CAdjacencyMatrix( final String p_rowname, final String p_colname )
    {
        this( p_rowname, p_colname, ESerialzeOutput.Matrix );
    }


    /**
     * ctor
     *
     * @param p_rowname row name
     * @param p_colname column name
     * @param p_type serializing type
     */
    public CAdjacencyMatrix( final String p_rowname, final String p_colname, final ESerialzeOutput p_type )
    {
        super();
        m_rowname = p_rowname;
        m_colname = p_colname;
        m_type = p_type;
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

    /**
     * @param p_row row element
     * @param p_column column element
     * @param p_value value
     */
    public void set( final T p_row, final T p_column, final N p_value )
    {
        this.put( new ImmutablePair<>( p_row, p_column ), p_value );
    }

    /**
     * enum typ for serializing
     **/
    public enum ESerialzeType
    {
        /**
         * returns the matrix as matrix
         **/
        Matrix,
        /**
         * returns the matrix as tree
         */
        Tree;
    }

    /**
     * enum typ for serializing
     **/
    public enum ESerialzeOutput
    {
        /**
         * returns the matrix as matrix
         **/
        Matrix,
        /**
         * returns the matrix as tree
         */
        Tree;
    }

    /**
     * serializer for Json access
     */
    public static class CJson extends JsonSerializer<CAdjacencyMatrix<?, ?>>
    {

        @Override
        public void serialize( final CAdjacencyMatrix<?, ?> p_matrix, final JsonGenerator p_generator, final SerializerProvider p_serializer
        ) throws IOException, JsonProcessingException
        {
            switch ( p_matrix.m_type )
            {
                case Matrix: this.toMatrix( p_matrix, p_generator ); break;
                case Tree: this.toTree( p_matrix, p_generator ); break;
                default:
            }
        }

        /**
         * converts the matrix to JSON matrix object
         *
         * @param p_matrix matrix object
         * @param p_generator JSON generator
         * @throws IOException is thrown on IO errors
         */
        private void toMatrix( final CAdjacencyMatrix<?, ?> p_matrix, final JsonGenerator p_generator ) throws IOException
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


        /**
         * converts the matrix to JSON tree object
         *
         * @param p_matrix matrix object
         * @param p_generator JSON generator
         * @throws IOException is thrown on IO errors
         */
        private void toTree( final CAdjacencyMatrix<?, ?> p_matrix, final JsonGenerator p_generator ) throws IOException
        {
            p_generator.writeStartObject();
            p_generator.writeArrayFieldStart( "cells" );
            for ( final Map.Entry<?, ?> l_item : p_matrix.entrySet() )
            {

            }
            p_generator.writeEndObject();
        }
    }

}
