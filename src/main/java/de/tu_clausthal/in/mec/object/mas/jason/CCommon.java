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

package de.tu_clausthal.in.mec.object.mas.jason;


import com.graphhopper.util.EdgeIteratorState;
import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * common method for Jason
 */
public class CCommon
{

    /**
     * private ctor - avoid instantiation
     */
    private CCommon()
    {
    }


    /**
     * Jason creates quoted string, so we need to clean the string to create corrected Java strings
     *
     * @param p_input input Jason string
     * @return cleanup Java String
     */
    public static String clearString( final String p_input )
    {
        return p_input.replaceAll( "\"", "" ).replaceAll( "'", "" );
    }

    /**
     * converts Jason Term to Java types to complex types e.g. List -> Geoposition
     *
     * @param p_term Jason term
     * @param p_argumenttypes type list with the target type
     * @return Java value
     */
    @SuppressWarnings( "unchecked" )
    public static Object getJavaValue( final Term p_term, final Class<?> p_argumenttypes ) throws NoValueException
    {
        if ( GeoPosition.class.equals( p_argumenttypes ) )
        {
            if ( ( !p_term.isList() ) || ( ( (ListTerm) p_term ).size() != 2 ) )
                throw new IllegalArgumentException(
                        de.tu_clausthal.in.mec.common.CCommon.getResourceString(
                                CCommon.class, "jasontocustomjava", GeoPosition.class.getCanonicalName(), p_term
                        )
                );

            return new GeoPosition(
                    ( (Number) getJavaValue( ( (ListTerm) p_term ).get( 0 ) ) ).doubleValue(), ( (Number) getJavaValue(
                    ( (ListTerm) p_term ).get( 1 )
            ) ).doubleValue()
            );
        }

        if ( p_argumenttypes.isInstance( Number.class ) )
            return getJavaValue( ( (NumberTerm) p_term ).solve(), p_argumenttypes );

        return getJavaValue( p_term );
    }

    /**
     * Jason does not support Java-type binding, so an explicit converting of Jason types to the corresponding Java type is needed
     *
     * @param p_term Jason term
     * @return Java type
     *
     * @throws NoValueException on empty value
     */
    @SuppressWarnings( "unchecked" )
    public static Object getJavaValue( final Term p_term ) throws NoValueException
    {
        if ( p_term.isList() )
        {
            final List<Object> l_return = new LinkedList<>();
            for ( final Term l_term : (ListTerm) p_term )
                l_return.add( getJavaValue( l_term ) );
            return l_return;
        }

        if ( p_term.isNumeric() )
            return ( (NumberTerm) p_term ).solve();

        if ( p_term.isString() )
            return ( (StringTerm) p_term ).getString();

        if ( p_term.isAtom() )
            return clearString( p_term.toString() );

        throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( CCommon.class, "jasontodefaultjava", p_term ) );
    }

    /**
     * converts a Double into a number object with correct type structure
     *
     * @param p_value double value
     * @param p_class class that is the target type
     * @return converted boxed-type
     */
    private static Number getJavaValue( final Double p_value, final Class<?> p_class )
    {
        if ( ( p_class.equals( Byte.class ) ) || ( p_class.equals( Byte.TYPE ) ) )
            return new Byte( p_value.byteValue() );
        if ( ( p_class.equals( Double.class ) ) || ( p_class.equals( Double.TYPE ) ) )
            return p_value;
        if ( ( p_class.equals( Float.class ) ) || ( p_class.equals( Float.TYPE ) ) )
            return new Float( p_value.floatValue() );
        if ( ( p_class.equals( Integer.class ) ) || ( p_class.equals( Integer.TYPE ) ) )
            return new Integer( p_value.intValue() );
        if ( ( p_class.equals( Long.class ) ) || ( p_class.equals( Long.TYPE ) ) )
            return new Long( p_value.longValue() );
        if ( ( p_class.equals( Short.class ) ) || ( p_class.equals( Short.TYPE ) ) )
            return new Short( p_value.shortValue() );

        throw new IllegalArgumentException( "class unknown" );
    }

    /**
     * returns an atom
     *
     * @param p_atom name of the atom
     * @return literal object
     */
    private static Literal getLiteral( final String p_atom )
    {
        return ASSyntax.createAtom( getLiteralName( p_atom ) );
    }

    /**
     * creates a Jason literal with optional data
     *
     * @param p_name name of the literal
     * @param p_data data of the literal
     * @return literal object
     */
    @SuppressWarnings( "unchecked" )
    public static Literal getLiteral( final String p_name, final Object p_data )
    {
        return ASSyntax.createLiteral( getLiteralName( p_name ), getTerm( p_data ) );
    }

    /**
     * returns an atom within in a literal
     *
     * @param p_literal name of the literal
     * @param p_atom name of the atom
     * @return literal object
     */
    public static Literal getLiteral( final String p_literal, final String p_atom )
    {
        return ASSyntax.createLiteral( getLiteralName( p_literal ), getLiteral( p_atom ) );
    }

    /**
     * checks a literal name and convert it to the correct syntax
     *
     * @param p_name name of the literal
     * @return converted literal name
     */
    private static String getLiteralName( final String p_name )
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException(
                    de.tu_clausthal.in.mec.common.CCommon.getResourceString(
                            de.tu_clausthal.in.mec.object.mas.jason.CCommon.class, "namenotempty"
                    )
            );

        // first char must be lower-case - split on spaces and create camel-case
        final String[] l_parts = p_name.split( " " );
        for ( int i = 0; i < l_parts.length; i++ )
            l_parts[i] = ( i == 0 ? l_parts[i].substring( 0, 1 ).toLowerCase() : l_parts[i].substring( 0, 1 ).toUpperCase() ) + l_parts[i].substring( 1 );

        return StringUtils.join( l_parts ).replaceAll( "\\W", "" );
    }

    /**
     * convert data type into Jason term
     *
     * @param p_data Jason Term value
     * @return Jason term
     */
    @SuppressWarnings( "unchecked" )
    private static Term getTerm( final Object p_data )
    {
        // null value into atom
        if ( p_data == null )
            return ASSyntax.createAtom( "" );

        // pair into complex term
        if ( p_data instanceof Pair )
            return ASSyntax.createLiteral(
                    "pair",
                    getLiteral( "left", ( (Pair) p_data ).getLeft() ),
                    getLiteral( "right", ( (Pair) p_data ).getRight() )
            );

        // triple into complex term
        if ( p_data instanceof Triple )
            ASSyntax.createLiteral(
                    "tripel",
                    getLiteral( "left", ( (Triple) p_data ).getLeft() ),
                    getLiteral( "right", ( (Triple) p_data ).getRight() ),
                    getLiteral( "middle", ( (Triple) p_data ).getMiddle() )
            );

        // map into complex term list
        if ( p_data instanceof Map )
            ASSyntax.createList(
                    new LinkedList<Term>()
                    {{
                            for ( final Object l_item : ( (Map) p_data ).entrySet() )
                                add( getLiteral( ( (Map.Entry<?, ?>) l_item ).getKey().toString(), ( (Map.Entry<?, ?>) l_item ).getValue() ) );
                        }}
            );

        // individual type - GeoPosition
        if ( p_data instanceof GeoPosition )
            return ASSyntax.createLiteral(
                    "geoposition",
                    ASSyntax.createLiteral( "latitude", getTerm( ( (GeoPosition) p_data ).getLatitude() ) ),
                    ASSyntax.createLiteral( "longitude", getTerm( ( (GeoPosition) p_data ).getLongitude() ) )
            );

        // individual type - Edge
        if ( p_data instanceof EdgeIteratorState )
            return ASSyntax.createLiteral(
                    "edge",
                    ASSyntax.createLiteral( "id", getTerm( ( (EdgeIteratorState) p_data ).getEdge() ) ),
                    ASSyntax.createLiteral( "name", getTerm( ( (EdgeIteratorState) p_data ).getName() ) ),
                    ASSyntax.createLiteral( "distance", getTerm( ( (EdgeIteratorState) p_data ).getDistance() ) )
            );


        // number value into number
        if ( p_data instanceof Number )
            return ASSyntax.createNumber( ( (Number) p_data ).doubleValue() );

        // collection into term list
        if ( p_data instanceof Collection )
            return ASSyntax.createList( (Collection) p_data );

        return ASSyntax.createString( p_data.toString() );
    }

}
