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
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.mas.general.defaultdehaviour.CNumberAtom;
import de.tu_clausthal.in.mec.object.mas.general.defaultdehaviour.CStringAtom;
import de.tu_clausthal.in.mec.object.mas.general.defaultdehaviour.CTermList;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.ITerm;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CLiteral;
import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
     * converts an atom into a string atom
     *
     * @param p_atom atom to convert
     * @return string atom
     */
    public static CStringAtom convertGeneric( final Atom p_atom )
    {
        return new CStringAtom( clearString( p_atom.getFunctor() ) );
    }

    /**
     * converts a StringTerm into a string atom
     *
     * @param p_term string term to convert
     * @return string atom
     */
    public static CStringAtom convertGeneric( final StringTerm p_term )
    {
        return new CStringAtom( clearString( p_term.getString() ) );
    }

    /**
     * converts a jason specific set of literals to a generic one
     *
     * @param p_literals set of jason-specific literals
     * @return converted Set of generic literals
     */
    public static Set<ILiteral<Literal>> convertGeneric( final List<Literal> p_literals )
    {
        return new HashSet<ILiteral<Literal>>()
        {{
                for ( final Literal l_literal : p_literals )
                    add( new CLiteral( l_literal ) );
            }};
    }


    /**
     * converts a NumberTerm into a Double Atom
     *
     * @param p_number NumberTerm
     * @return Double Atom
     */
    public static CNumberAtom convertGeneric( final NumberTerm p_number )
    {
        try
        {
            return new CNumberAtom( p_number.solve() );
        }
        catch ( NoValueException l_exception )
        {
            CLogger.error( l_exception );
        }

        return null;
    }


    /**
     * converts a term into a generic ITerm
     *
     * @param p_term original term
     * @return converted generic term
     */
    public static ITerm convertGeneric( final Term p_term )
    {
        if ( p_term == null )
            return new ITerm()
            {
                @Override
                public boolean instanceOf( Class<?> p_class )
                {
                    return ITerm.class.isAssignableFrom( p_class );
                }
            };

        if ( p_term.isNumeric() )
            return convertGeneric( (NumberTerm) p_term );

        if ( p_term instanceof StringTerm )
            return convertGeneric( (StringTerm) p_term );

        if ( p_term.isAtom() )
            return convertGeneric( (Atom) p_term );

        if ( p_term.isLiteral() )
            return new CLiteral( (Literal) p_term );

        if ( p_term.isList() )

            return new CTermList()
            {
                {
                    for ( final Term l_term : (ListTerm) p_term )
                        add( convertGeneric( l_term ) );
                }
            };

        throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( CCommon.class, "convertgenericfail" ) );
    }

    /**
     * converts Jason Term to Java types to complex types e.g. List -> Geoposition
     *
     * @param p_term Jason term
     * @param p_type type list with the target type
     * @return Java value
     */
    @SuppressWarnings( "unchecked" )
    public static Object getJavaValue( final Term p_term, final Class<?> p_type ) throws NoValueException
    {
        if ( GeoPosition.class.isAssignableFrom( p_type ) )
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

        // check for numbers
        if ( Number.class.isAssignableFrom( p_type ) )
            return getJavaValue( ( (NumberTerm) p_term ).solve(), p_type );

        return getJavaValue( p_term );
    }

    /**
     * Jason does not support Java-type binding, so an explicit converting of
     * Jason types to the corresponding Java type is needed
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
        return ASSyntax.createLiteral( p_name, getTerm( p_data ) );
    }


    /**
     * checks a literal name and convert it to the correct syntax
     *
     * @param p_name name of the literal
     * @return converted literal name
     *
     * @note note the precendence of the data types
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

        return StringUtils.join( l_parts ); //.replaceAll("\\W", "");
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

        // number value into number
        if ( p_data instanceof Number )
            return ASSyntax.createNumber( ( (Number) p_data ).doubleValue() );

        // GeoPosition
        if ( p_data instanceof GeoPosition )
            return ASSyntax.createLiteral(
                    "geoposition",
                    ASSyntax.createLiteral( "latitude", ASSyntax.createNumber( ( (GeoPosition) p_data ).getLatitude() ) ),
                    ASSyntax.createLiteral( "longitude", ASSyntax.createNumber( ( (GeoPosition) p_data ).getLongitude() ) )
            );

        // Edge
        if ( p_data instanceof EdgeIteratorState )
            return ASSyntax.createLiteral(
                    "edge",
                    ASSyntax.createLiteral( "id", ASSyntax.createNumber( ( (EdgeIteratorState) p_data ).getEdge() ) ),
                    ASSyntax.createLiteral( "name", ASSyntax.createString( ( (EdgeIteratorState) p_data ).getName() ) ),
                    ASSyntax.createLiteral( "distance", ASSyntax.createNumber( ( (EdgeIteratorState) p_data ).getDistance() ) )
            );

        // pair into complex term
        if ( p_data instanceof Pair )
            return ASSyntax.createLiteral(
                    "pair",
                    ASSyntax.createLiteral( "left", getTerm( ( (Pair) p_data ).getLeft() ) ),
                    ASSyntax.createLiteral( "right", getTerm( ( (Pair) p_data ).getRight() ) )
            );

        // triple into complex term
        if ( p_data instanceof Triple )
            return ASSyntax.createLiteral(
                    "triple",
                    ASSyntax.createLiteral( "left", getTerm( ( (Triple) p_data ).getLeft() ) ),
                    ASSyntax.createLiteral( "right", getTerm( ( (Triple) p_data ).getRight() ) ),
                    ASSyntax.createLiteral( "middle", getTerm( ( (Triple) p_data ).getMiddle() ) )
            );

        // map into complex term list
        if ( p_data instanceof Map )
            return ASSyntax.createList(
                    new LinkedList<Term>()
                    {{
                            for ( final Object l_item : ( (Map) p_data ).entrySet() )
                                add(
                                        ASSyntax.createLiteral(
                                                ( (Map.Entry<?, ?>) l_item ).getKey().toString(), getTerm(
                                                        ( (Map.Entry<?, ?>) l_item ).getValue()
                                                )
                                        )
                                );
                        }}
            );

        // collection into term list
        if ( p_data instanceof Collection )
            return ASSyntax.createList( (Collection) p_data );

        // otherwise to string
        return ASSyntax.createString( p_data );
    }

}
