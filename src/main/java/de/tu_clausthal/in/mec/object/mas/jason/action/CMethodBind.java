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
import jason.asSyntax.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
        return "invoke";
    }

    @Override
    /**
     * @see http://docs.oracle.com/javase/7/docs/api/java/lang/invoke/MethodHandle.html
     * @see http://docs.oracle.com/javase/7/docs/api/java/lang/invoke/MethodType.html
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


            // method parameter (return & argument default type)
            Class l_returntype = Void.class;
            Class[] l_argumenttype = null;

            if ( ( l_args.size() > 3 ) && ( !l_args.get( 2 ).isList() ) )
            {
                l_returntype = this.convertTermToClass( l_args.get( 2 ) );
                l_argumenttype = l_args.get( 3 ).isList() ? this.convertTermListToArray( (ListTerm) l_args.get( 3 ) ) : this.convertTermListToArray( l_args.get( 3 ) );
            }
            else if ( l_args.size() > 2 )
            {
                if ( l_args.get( 2 ).isList() )
                    l_argumenttype = this.convertTermListToArray( (ListTerm) l_args.get( 2 ) );
                else
                    l_returntype = this.convertTermToClass( l_args.get( 2 ) );
            }


            // method invokation
            CCommon.getClassMethod( l_object.getLeft().getClass(), l_methodname ).invoke( l_object.getLeft() );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }
    }


    /**
     * converts a term value into a class object
     *
     * @param p_term Jason term
     * @return class object
     */
    protected Class convertTermToClass( Term p_term ) throws ClassNotFoundException
    {
        String l_classname = p_term.toString();
        if ( !l_classname.contains( "." ) )
            l_classname = "java.lang." + l_classname;
        return Class.forName( l_classname );
    }

    /**
     * converts a list of terms int an array of class objects
     *
     * @param p_list term list
     * @return class array
     * @throws ClassNotFoundException
     */
    protected Class[] convertTermListToArray( ListTerm p_list ) throws ClassNotFoundException
    {
        Class[] l_classes = new Class[p_list.size()];
        for ( int i = 0; i < l_classes.length; i++ )
            l_classes[i] = this.convertTermToClass( p_list.get( i ) );
        return l_classes;
    }

    /**
     * converts a term into an array of class objects
     *
     * @param p_term term
     * @return class object array
     * @throws ClassNotFoundException
     */
    protected Class[] convertTermListToArray( Term p_term ) throws ClassNotFoundException
    {
        Class[] l_classes = new Class[1];
        l_classes[0] = this.convertTermToClass( p_term );
        return l_classes;
    }
}
