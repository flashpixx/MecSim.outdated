/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.object.mas.jason.action;


import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSemantics.Agent;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * action to invoke any method on an object
 *
 * @warning methods does not use any primitive datatypes - primitive datatypes must be used with its boxed-type
 */
public class CMethodBind extends IAction
{

    /**
     * bind object *
     */
    protected final Map<String, CReflection.CMethodCache<Object>> m_bind = new HashMap<>();


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
    public CMethodBind( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object );
    }


    /**
     * adds a new bind object
     *
     * @param p_name   name
     * @param p_object bind object
     */
    public void push( final String p_name, final Object p_object )
    {
        m_bind.put( p_name, new CReflection.CMethodCache( p_object ) );
    }


    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public void remove( final String p_name )
    {
        m_bind.remove( p_name );
    }

    /**
     * returns the forbidden set
     *
     * @param p_name name of the object
     * @return set with forbidden names
     */
    public Set<String> getForbidden( final String p_name )
    {
        final CReflection.CMethodCache l_object = m_bind.get( p_name );
        if ( l_object == null )
            return null;

        return l_object.getForbidden();
    }


    @Override
    /**
     * @todo handle term list
     */
    public void act( final Agent p_agent, final Structure p_args )
    {
        // check number of argument first
        final List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 2 )
            throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "argument") );

        // first & second argument must changed to a string (cast calls are not needed, we use the object string call)
        final String l_objectname = l_args.get( 0 ).toString();
        final CReflection.CMethodCache<Object> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "object", l_objectname) );

        try
        {

            // construct method signature with data
            String l_returnname = null;
            Class<?> l_returntype = Void.class;
            Class<?>[] l_argumenttype = null;
            List<Term> l_argumentdata = null;

            if ( ( l_args.size() > 4 ) && ( l_args.get( 4 ).isList() ) )
            {
                l_returnname = l_args.get( 2 ).toString();
                l_returntype = this.convertTermToClass( l_args.get( 3 ) );
                l_argumenttype = this.convertTermListToArray( (ListTerm) l_args.get( 4 ) );
                l_argumentdata = l_args.subList( 5, l_args.size() );
            }
            else if ( ( l_args.size() > 3 ) && ( l_args.get( 3 ).isList() ) )
            {
                l_returntype = this.convertTermToClass( l_args.get( 2 ) );
                l_argumenttype = this.convertTermListToArray( (ListTerm) l_args.get( 3 ) );
                l_argumentdata = l_args.subList( 4, l_args.size() );
            }
            else if ( ( l_args.size() > 2 ) && ( l_args.get( 2 ).isList() ) )
            {
                l_argumenttype = this.convertTermListToArray( (ListTerm) l_args.get( 2 ) );
                l_argumentdata = l_args.subList( 3, l_args.size() );
            }

            if ( ( l_argumentdata == null ) || ( l_argumenttype == null ) || ( l_argumentdata.size() != l_argumenttype.length ) )
                throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "argumentnumber") );


            // build invoke parameter with explizit cast of the argument types
            final List<Object> l_argumentinvokedata = new LinkedList<>();
            l_argumentinvokedata.add( l_object.getObject() );

            for ( int i = 0; i < l_argumentdata.size(); i++ )
                if ( l_argumentdata.get( i ).isNumeric() )
                    l_argumentinvokedata.add( CCommon.convertNumber( l_argumenttype[i], ( (NumberTerm) l_argumentdata.get( i ) ).solve() ) );
                else
                    l_argumentinvokedata.add( l_argumenttype[i].cast( l_argumentdata.get( i ) ) );


            // invoke and cast return data
            final String l_methodname = l_args.get( 1 ).toString();
            final CReflection.CMethod l_invoke = l_object.get( l_methodname, l_argumenttype );
            final Object l_return = l_returntype.cast( ( l_argumentdata == null ) || ( l_argumentdata.size() == 0 ) ? l_invoke.getHandle().invoke( l_argumentinvokedata.get( 0 ) ) : l_invoke.getHandle().invokeWithArguments( l_argumentinvokedata ) );

            // push return data into the agent
            if ( ( l_return != null ) && ( !void.class.equals( l_return.getClass() ) ) )
            {
                final Literal l_literalreturn = de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( ( l_returnname == null ) || ( l_returnname.isEmpty() ) ? l_methodname : l_returnname, l_return );
                l_literalreturn.addAnnot( ASSyntax.createLiteral( "source", ASSyntax.createAtom( l_objectname ) ) );
                p_agent.addBel( l_literalreturn );
            }

        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }
        catch ( Throwable l_throwable )
        {
            throw new IllegalArgumentException( l_throwable.getMessage() );
        }
    }


    /**
     * converts a term value into a class object
     *
     * @param p_term Jason term
     * @return class object
     */
    protected Class<?> convertTermToClass( final Term p_term ) throws IllegalArgumentException
    {
        String l_classname = p_term.toString();
        if ( "void".equalsIgnoreCase( l_classname ) )
            return void.class;

        Class<?> l_class = null;
        try
        {

            switch ( l_classname )
            {
                // check primitive datatypes
                case "bool":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "boolean":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "byte":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "char":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "short":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "int":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "long":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "float":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );
                case "double":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "primitive", l_classname) );

                    // object types
                default:
                    if ( !l_classname.contains( "." ) )
                        l_classname = "java.lang." + l_classname;
                    l_class = Class.forName( l_classname );
            }

        }
        catch ( ClassNotFoundException l_exception )
        {
            throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString(this, "classnotfound", l_classname) );
        }

        return l_class;
    }


    /**
     * converts a list of terms int an array of class objects
     *
     * @param p_list term list
     * @return class array
     */
    protected Class<?>[] convertTermListToArray( final ListTerm p_list ) throws ClassNotFoundException
    {
        final Class<?>[] l_classes = new Class<?>[p_list.size()];
        for ( int i = 0; i < l_classes.length; i++ )
            l_classes[i] = this.convertTermToClass( p_list.get( i ) );
        return l_classes;
    }

    /**
     * converts a term into an array of class objects
     *
     * @param p_term term
     * @return class object array
     */
    protected Class<?>[] convertTermListToArray( final Term p_term ) throws ClassNotFoundException
    {
        final Class<?>[] l_classes = new Class<?>[1];
        l_classes[0] = this.convertTermToClass( p_term );
        return l_classes;
    }
}
