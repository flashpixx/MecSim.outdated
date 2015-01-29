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

package de.tu_clausthal.in.mec.object.mas.jason.actions;


import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;


/**
 * action to invoke any method on an object
 */
public class CMethodInvoke extends IAction
{

    /**
     * bind object *
     */
    protected Map<String, ImmutablePair<Object, Set<String>>> m_bind = new HashMap();


    /**
     * ctor - default
     */
    public CMethodInvoke()
    {

    }

    /**
     * ctor bind an object
     *
     * @param p_name   name of the binding object
     * @param p_object object
     */
    public CMethodInvoke( String p_name, Object p_object )
    {
        m_bind.put( p_name, new ImmutablePair<Object, Set<String>>( p_object, new HashSet() ) );
    }


    /**
     * adds a new bind object
     *
     * @param p_name   name
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
     * @return set with forbidden names
     */
    public Set<String> getForbidden( String p_name )
    {
        ImmutablePair<Object, Set<String>> l_object = m_bind.get( p_name );
        if ( l_object == null )
            return null;

        return l_object.getRight();
    }


    @Override
    public String getName()
    {
        return "invokemethod";
    }

    @Override
    public void act( Structure p_args )
    {
        // check number of argument first
        List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 3 )
            throw new IllegalArgumentException( "arguments are incorrect" );

        // first & second argument must changed to a string (cast calls are not needed, we use the object string call)
        String l_objectname = l_args.get( 0 ).toString();
        ImmutablePair<Object, Set<String>> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( "object key [" + l_objectname + "] not found" );

        String l_methodname = l_args.get( 1 ).toString();
        try
        {

            for ( String l_name : l_object.getRight() )
                if ( l_name.equals( l_methodname ) )
                    throw new IllegalAccessException( "method [" + l_methodname + "] not accessible for the object [" + l_objectname + "]" );

        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }
    }
}
