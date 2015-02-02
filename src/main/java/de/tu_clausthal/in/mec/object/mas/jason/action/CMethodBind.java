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

package de.tu_clausthal.in.mec.object.mas.jason.action;


import de.tu_clausthal.in.mec.common.CCommon;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * action to invoke any method on an object
 */
public class CMethodBind extends IAction
{

    /**
     * bind object *
     */
    protected Map<String, ImmutablePair<Object, Set<String>>> m_bind = new HashMap();


    /**
     * ctor - default
     */
    public CMethodBind()
    {

    }

    /**
     * ctor bind an object
     *
     * @param p_name   name of the binding object
     * @param p_object object
     */
    public CMethodBind( String p_name, Object p_object )
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
    /**
     * @bug incomplete - method call does not work
     */
    public void act( Structure p_args )
    {
        // check number of argument first
        List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 2 )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "argument" ) );

        // first & second argument must changed to a string (cast calls are not needed, we use the object string call)
        String l_objectname = l_args.get( 0 ).toString();
        ImmutablePair<Object, Set<String>> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "object", l_objectname ) );

        String l_methodname = l_args.get( 1 ).toString();
        try
        {

            // check accessible of the method
            for ( String l_name : l_object.getRight() )
                if ( l_name.equals( l_methodname ) )
                    throw new IllegalAccessException( CCommon.getResouceString( this, "access", l_methodname, l_objectname ) );

            // try to get method
            Method l_method = null;
            for ( Class l_class = l_object.getLeft().getClass(); ( l_class != null ) && ( l_method == null ); l_class = l_class.getSuperclass() )
                try
                {
                    // add signature components
                    l_method = CCommon.getClassMethod( l_class, l_methodname );
                }
                catch ( Exception l_exception )
                {
                }

            if ( l_method == null )
                throw new IllegalArgumentException( CCommon.getResouceString( this, "methodnotfound", l_methodname, l_objectname ) );
            if ( ( Modifier.isFinal( l_method.getModifiers() ) ) || ( Modifier.isAbstract( l_method.getModifiers() ) ) || ( Modifier.isInterface( l_method.getModifiers() ) ) || ( Modifier.isStatic( l_method.getModifiers() ) ) )
                throw new IllegalAccessException( CCommon.getResouceString( this, "modifier", l_methodname, l_objectname ) );


            // invoke method with parameter
        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }
    }
}
