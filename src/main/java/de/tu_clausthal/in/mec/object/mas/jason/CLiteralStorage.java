/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.*;


/**
 * Jason collection structure for storing any literal
 *
 * @note atoms can be added only at the initial state of an agent
 * @deprecated
 */
public class CLiteralStorage
{
    /**
     * set with all method filters *
     */
    protected Set<IMethodFilter> m_methodfilter = new HashSet();
    /**
     * set with all field filters *
     */
    protected Set<IPropertyFilter> m_fieldfilter = new HashSet();
    /**
     * set with all literals *
     */
    protected Set<Literal> m_staticliterals = new HashSet();

    protected Map<Literal, Pair<Field, Object>> m_fieldliterals = new HashMap();


    public CLiteralStorage()
    {
        this.initialize();
    }

    /**
     * creates a Jason literal with optional data
     *
     * @param p_name name of the literal
     * @param p_data data of the literal
     * @return literal object
     */
    public static Literal getLiteral( String p_name, Object p_data )
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( "name need not to be empty" );


        // first char must be lower-case - split on spaces and create camel-case
        String[] l_parts = p_name.split( " " );
        for ( int i = 0; i < l_parts.length; i++ )
            l_parts[i] = ( i == 0 ? l_parts[i].substring( 0, 1 ).toLowerCase() : l_parts[i].substring( 0, 1 ).toUpperCase() ) + l_parts[i].substring( 1 );
        String l_name = StringUtils.join( l_parts ).replaceAll( "\\W", "" );


        // null value into atom
        if ( p_data == null )
            return ASSyntax.createLiteral( l_name, ASSyntax.createString( null ) );

        // number value into number
        if ( p_data instanceof Number )
            return ASSyntax.createLiteral( l_name, ASSyntax.createNumber( ( (Number) p_data ).doubleValue() ) );

        // collection into term list
        if ( p_data instanceof Collection )
            return ASSyntax.createLiteral( l_name, ASSyntax.createList( (Collection) p_data ) );

        // otherwise into string
        return ASSyntax.createLiteral( l_name, ASSyntax.createString( p_data.toString() ) );
    }

    /**
     * default initialization *
     */
    protected void initialize()
    {
        m_methodfilter.add( new CDefaultMethodFilter() );
        m_fieldfilter.add( new CDefaultFieldFilter() );
    }


    /**
     * get set with method filters
     *
     * @return filter set
     */
    public Set<IMethodFilter> getMethodFilter()
    {
        return m_methodfilter;
    }

    /**
     * get set with field filter
     *
     * @return filter set
     */
    public Set<IPropertyFilter> getPropertyFilter()
    {
        return m_fieldfilter;
    }


    public void addStaticLiteral( String p_name, Object p_data )
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            return;

        m_staticliterals.add( getLiteral( p_name, p_data ) );
    }

    /**
     * transform the object fields to Jason literals with values and returns a map with all object reference data
     *
     * @param p_object object with data
     */
    public void addObjectFields( Object p_object )
    {
        if ( p_object == null )
            return;

        for ( Field l_field : p_object.getClass().getDeclaredFields() )
        {
            for ( IPropertyFilter l_filter : m_fieldfilter )
            {
                if ( !l_filter.filter( p_object, l_field ) )
                    continue;

                try
                {
                    m_fieldliterals.put( getLiteral( l_field.getName(), l_field.get( p_object ) ), new ImmutablePair<Field, Object>( l_field, p_object ) );
                }
                catch ( Exception l_exception )
                {
                    CLogger.error( l_exception );
                }
            }
        }
    }


    public Set<Literal> getStaticLiteral()
    {
        return m_staticliterals;
    }

    public Set<Literal> getObjectFields()
    {
        return m_fieldliterals.keySet();
    }

/*
    public void syncLiteral( Literal p_literal )
    {
        if (m_staticliterals.contains( p_literal ))
        {
            m_staticliterals.add( p_literal );
            return;
        }

        if (m_fieldliterals.containsKey( p_literal ))
        {
            Pair<Field, Object> l_reference = m_fieldliterals.get( p_literal );
            List<Term> l_terms = p_literal.getTerms();
            if ( l_terms.size() == 1 )
            {
                this.setTermToObject( l_reference.getRight(), l_reference.getLeft(), l_terms.get( 0 ) );
            }
            return;
        }
    }


    protected void setTermToObject( Object p_object, Field p_field, Term p_term )
    {

        try {

            if (p_term.isNumeric())
                p_field.set( p_object, ((NumberTerm)p_term).solve() );

            if (p_term.isString())
            {
                String l_str = ((StringTerm)p_term).getString();
                if (l_str.equalsIgnoreCase( "null" ))
                    p_field.set( p_object, null );
                else
                    p_field.set( p_object, l_str );
            }

            //if (p_term.isList())


        } catch (Exception l_exception)
        {
            CLogger.error( l_exception );
        }
    }
*/


    /**
     * transform the object methods to Jason literals
     *
     * @param p_object object with methods
     * @return map with literal names and pair of methods and object reference
     *
    public Map<String, Pair<Method, Object>> addObjectMethods( Object p_object )
    {
    Map<String, Pair<Method, Object>> l_data = new HashMap();
    if ( p_object == null )
    return l_data;

    for ( Method l_method : p_object.getClass().getDeclaredMethods() )
    {
    for ( IMethodFilter l_filter : m_methodfilter )
    {

    if ( !l_filter.filter( p_object, l_method ) )
    continue;

    Literal l_literal = getLiteral( l_method.getName(), null, m_nulltoatom );
    m_literals.add( l_literal );
    l_data.put( l_literal.toString(), new ImmutablePair<Method, Object>( l_method, p_object ) );
    }
    }

    return l_data;
    }
     */

}
