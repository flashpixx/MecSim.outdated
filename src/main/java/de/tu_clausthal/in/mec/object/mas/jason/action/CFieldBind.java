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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * action to synchronize bind-object with agent value
 */
public class CFieldBind extends IAction
{

    /**
     * bind object *
     */
    protected Map<String, ImmutablePair<Object, Set<String>>> m_bind = new HashMap();

    /**
     * ctor - default
     */
    public CFieldBind()
    {

    }

    /**
     * ctor bind an object
     *
     * @param p_name   name of the binding object
     * @param p_object object
     */
    public CFieldBind( String p_name, Object p_object )
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
        return "pushfield";
    }

    @Override
    /**
     * @todo handle term list
     * @bug numeric type cast fails
     */
    public void act( Structure p_args )
    {

        // check number of argument first
        List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 3 )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "argument" ) );

        // first & second argument must changed to a string (cast calls are not needed, we use the object string call)
        String l_objectname = l_args.get( 0 ).toString();
        ImmutablePair<Object, Set<String>> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "object", l_objectname ) );

        String l_fieldname = l_args.get( 1 ).toString();
        try
        {

            // check accessible of the field
            for ( String l_name : l_object.getRight() )
                if ( l_name.equals( l_fieldname ) )
                    throw new IllegalAccessException( CCommon.getResouceString( this, "access", l_fieldname, l_objectname ) );

            // try to get field
            Field l_field = null;
            for ( Class l_class = l_object.getLeft().getClass(); ( l_class != null ) && ( l_field == null ); l_class = l_class.getSuperclass() )
                try
                {
                    l_field = CCommon.getClassField( l_class, l_fieldname );
                }
                catch ( Exception l_exception )
                {
                }

            if ( l_field == null )
                throw new IllegalArgumentException( CCommon.getResouceString( this, "fieldnotfound", l_fieldname, l_objectname ) );
            if ( ( Modifier.isFinal( l_field.getModifiers() ) ) || ( Modifier.isAbstract( l_field.getModifiers() ) ) || ( Modifier.isInterface( l_field.getModifiers() ) ) || ( Modifier.isStatic( l_field.getModifiers() ) ) )
                throw new IllegalAccessException( CCommon.getResouceString( this, "modifier", l_fieldname, l_objectname ) );


            // set field value
            if ( l_args.get( 2 ).isNumeric() )
            {
                // Jason stores numeric values with a native double, but we need to test is the target type / field type
                // a boxed or a non-boxed type e.g. Integer vs int, only boxed-types can be casted
                double l_value = ( (NumberTerm) l_args.get( 2 ) ).solve();
                switch ( l_field.getType().toString() )
                {
                    case "char":
                        l_field.set( l_object.getLeft(), (char) l_value );
                        break;
                    case "byte":
                        l_field.set( l_object.getLeft(), (byte) l_value );
                        break;
                    case "short":
                        l_field.set( l_object.getLeft(), (short) l_value );
                        break;
                    case "int":
                        l_field.set( l_object.getLeft(), (int) l_value );
                        break;
                    case "long":
                        l_field.set( l_object.getLeft(), (long) l_value );
                        break;
                    case "float":
                        l_field.set( l_object.getLeft(), (float) l_value );
                        break;
                    case "double":
                        l_field.set( l_object.getLeft(), l_value );
                        break;

                    default:
                        l_field.set( l_object.getLeft(), l_field.getType().cast( new Double( l_value ) ) );
                }
            }

            if ( l_args.get( 2 ).isString() )
                l_field.set( l_object.getLeft(), ( (StringTerm) l_args.get( 2 ) ).getString() );

        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }
    }
}
