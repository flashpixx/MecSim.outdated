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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Jason common functions
 */
public class CLiteralStorage implements Collection<Literal>
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
    protected Set<Literal> m_literals = new HashSet();


    /**
     * ctor - creates default filters
     */
    public CLiteralStorage()
    {
        m_methodfilter.add( new CDefaultMethodFilter() );
        m_fieldfilter.add( new CDefaultFieldFilter() );
    }

    /**
     * creates a Jason literal with optional data
     *
     * @param p_name name of the literal
     * @param p_data data of the literal
     * @return literal object
     */
    private static Literal getLiteral( String p_name, Object p_data )
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( "name need not to be empty" );

        // null value into atom
        if ( p_data == null )
            return ASSyntax.createAtom( p_name );

        // number value into number
        if ( p_data instanceof Number )
            return ASSyntax.createLiteral( p_name, ASSyntax.createNumber( ( (Number) p_data ).doubleValue() ) );

        // collection into term list
        if ( p_data instanceof Collection )
            return ASSyntax.createLiteral( p_name, ASSyntax.createList( (Collection) p_data ) );

        // otherwise into string
        return ASSyntax.createLiteral( p_name, ASSyntax.createString( p_data.toString() ) );
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

    /**
     * transform the object fields to Jason literals with values and returns a map with all object reference data
     *
     * @param p_object object with data
     * @return map with literal name and pair of field and object definition
     */
    public Map<String, Pair<Field, Object>> addObjectFields( Object p_object )
    {
        Map<String, Pair<Field, Object>> l_data = new HashMap();

        for ( Field l_field : p_object.getClass().getDeclaredFields() )
        {
            for ( IPropertyFilter l_filter : m_fieldfilter )
            {
                if ( !l_filter.filter( p_object, l_field ) )
                    continue;

                try
                {
                    Literal l_literal = getLiteral( l_field.getName(), l_field.get( p_object ) );
                    m_literals.add( l_literal );
                    l_data.put( l_field.getName(), new ImmutablePair<Field, Object>( l_field, p_object ) );
                }
                catch ( Exception l_exception )
                {
                    CLogger.error( l_exception );
                }
            }
        }

        return l_data;
    }


    /**
     * transform the object methods to Jason literals
     *
     * @param p_object object with methods
     * @return map with literal names and pair of methods and object reference
     */
    public Map<String, Pair<Method, Object>> addObjectMethods( Object p_object )
    {
        Map<String, Pair<Method, Object>> l_data = new HashMap();

        for ( Method l_method : p_object.getClass().getDeclaredMethods() )
        {
            for ( IMethodFilter l_filter : m_methodfilter )
            {

                if ( !l_filter.filter( p_object, l_method ) )
                    continue;

                Literal l_literal = getLiteral( l_method.getName(), null );
                m_literals.add( l_literal );
                l_data.put( l_literal.toString(), new ImmutablePair<Method, Object>( l_method, p_object ) );
            }
        }

        return l_data;
    }

    /**
     * converts a map into Jason literals
     *
     * @param p_data map with data
     * @return literal list
     */
    public void addAll( Map<String, Object> p_data )
    {
        if ( p_data == null )
            throw new IllegalArgumentException( "map need not be empty" );

        for ( Map.Entry<String, Object> l_data : p_data.entrySet() )
            m_literals.add( getLiteral( l_data.getKey(), l_data.getValue() ) );
    }


    /**
     * adds a string literal
     *
     * @param p_value string literal
     */
    public void add( String p_value )
    {
        m_literals.add( getLiteral( p_value, null ) );
    }


    /**
     * removes a literal
     *
     * @param p_value string literal
     * @return bool flag
     */
    public boolean remove( String p_value )
    {
        return m_literals.remove( getLiteral( p_value, null ) );
    }


    /**
     * contains method for string literals
     *
     * @param p_value string literal
     * @return bool for exist
     */
    public boolean contains( String p_value )
    {
        return this.contains( p_value, null );
    }

    /**
     * contains of a literals with value
     *
     * @param p_name  literal name
     * @param p_value literal value
     * @return bool for exist
     */
    public boolean contains( String p_name, Object p_value )
    {
        return m_literals.contains( getLiteral( p_name, p_value ) );
    }


    @Override
    public int size()
    {
        return m_literals.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_literals.isEmpty();
    }

    @Override
    public boolean contains( Object o )
    {
        return m_literals.contains( o );
    }

    @Override
    public Iterator<Literal> iterator()
    {
        return m_literals.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return m_literals.toArray();
    }

    @Override
    public <T> T[] toArray( T[] a )
    {
        return m_literals.toArray( a );
    }

    @Override
    public boolean add( Literal literal )
    {
        return m_literals.add( literal );
    }

    @Override
    public boolean remove( Object o )
    {
        return m_literals.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c )
    {
        return m_literals.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends Literal> c )
    {
        return m_literals.addAll( c );
    }

    @Override
    public boolean removeAll( Collection<?> c )
    {
        return m_literals.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return m_literals.retainAll( c );
    }

    @Override
    public void clear()
    {
        m_literals.clear();
    }
}
