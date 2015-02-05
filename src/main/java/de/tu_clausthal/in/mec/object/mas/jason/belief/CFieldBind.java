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

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * belief structure to bind object properties
 */
public class CFieldBind implements IBelief
{
    /**
     * bind object *
     */
    protected Map<String, ImmutablePair<Object, Set<String>>> m_bind = new HashMap();
    /**
     * set with literals
     */
    protected Set<Literal> m_literals = new HashSet();

    /**
     * ctor - default
     */
    public CFieldBind()
    {

    }

    /**
     * ctor bind an object
     *
     * @param p_name   name / annotion of the object
     * @param p_object bind object
     */
    public CFieldBind( String p_name, Object p_object )
    {
        this.push( p_name, p_object );
    }

    /**
     * creates a Jason literal with optional data
     *
     * @param p_name name of the literal
     * @param p_data data of the literal
     * @return literal object
     */
    protected static Literal getLiteral( String p_name, Object p_data )
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
     * adds a new bind object
     *
     * @param p_name   name / annotion of the object
     * @param p_object bind object
     */
    public void push( String p_name, Object p_object )
    {
        m_bind.put( p_name, new ImmutablePair<Object, Set<String>>( p_object, new HashSet() ) );
    }

    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public void remove( String p_name )
    {
        m_bind.remove( p_name );
    }

    /**
     * returns the forbidden set
     *
     * @param p_name bind name
     * @return set with forbidden names
     */
    public Set<String> getForbidden( String p_name )
    {
        return m_bind.get( p_name ).getRight();
    }

    @Override
    public Set<Literal> getLiterals()
    {
        return m_literals;
    }

    /**
     * @todo modify field access with cache
     */
    @Override
    public void update()
    {
        for ( Map.Entry<String, ImmutablePair<Object, Set<String>>> l_item : m_bind.entrySet() )
            for ( Field l_field : CReflection.getClassFields( l_item.getValue().getLeft().getClass() ) )
            {
                if ( ( l_item.getValue().getRight().contains( l_field.getName() ) ) || ( Modifier.isStatic( l_field.getModifiers() ) ) || ( Modifier.isInterface( l_field.getModifiers() ) ) ||
                        ( Modifier.isAbstract( l_field.getModifiers() ) ) || ( Modifier.isFinal( l_field.getModifiers() ) ) )
                    continue;

                // we get access to all objects field, create a literal of the field data and add the annotation to the object name
                try
                {

                    Literal l_literal = getLiteral( l_field.getName(), l_field.get( l_item.getValue().getLeft() ) );
                    l_literal.addAnnot( ASSyntax.createLiteral( "source", ASSyntax.createAtom( l_item.getKey() ) ) );
                    m_literals.add( l_literal );

                }
                catch ( Exception l_exception )
                {
                    CLogger.error( l_exception );
                }
            }
    }

    @Override
    public void clear()
    {
        m_literals.clear();
    }
}
