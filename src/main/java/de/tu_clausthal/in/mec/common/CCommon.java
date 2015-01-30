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

package de.tu_clausthal.in.mec.common;

import de.tu_clausthal.in.mec.CConfiguration;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.Collection;


/**
 * class for any helper calls
 */
public class CCommon
{

    /**
     * package base for translation *
     */
    private static final String s_packageroot = "de.tu_clausthal.in.mec.";


    /**
     * converts any collection type into a typed array
     *
     * @param p_class      class array
     * @param p_collection collection
     * @return typed array
     * @tparam T collection / array type
     */
    public static <T> T[] ColletionToArray( Class<T[]> p_class, Collection<T> p_collection )
    {
        T[] l_return = p_class.cast( Array.newInstance( p_class.getComponentType(), p_collection.size() ) );
        p_collection.toArray( l_return );
        return l_return;
    }

    /**
     * returns a string of the resouce file
     *
     * @param p_object    object for label
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResouceString( Object p_object, String p_label, Object... p_parameter )
    {
        return MessageFormat.format( CConfiguration.getInstance().getResourceBundle().getString( p_object.getClass().getCanonicalName().toLowerCase().replace( s_packageroot, "" ) + "." + p_label.toLowerCase().replace( " ", "" ) ), p_parameter );
    }

    /**
     * returns a string of the resouce file
     *
     * @param p_class     class for static calls
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResouceString( Class p_class, String p_label, Object... p_parameter )
    {
        return MessageFormat.format( CConfiguration.getInstance().getResourceBundle().getString( p_class.getCanonicalName().toLowerCase().replace( s_packageroot, "" ) + "." + p_label.toLowerCase().replace( " ", "" ) ), p_parameter );
    }


    /**
     * returns a private field of a class
     *
     * @param p_class class
     * @param p_field fieldname
     * @return field
     */
    public static Field getClassField( Class p_class, String p_field )
    {
        Field l_field = null;
        try
        {
            l_field = p_class.getDeclaredField( p_field );
            l_field.setAccessible( true );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( "field [" + p_field + "] not found in class [" + p_class.getCanonicalName() + "]" );
        }
        return l_field;
    }


    /**
     * returns a void-method from a class
     *
     * @param p_class  class
     * @param p_method methodname
     * @return method
     */
    public static Method getClassMethod( Class p_class, String p_method )
    {
        return getClassMethod( p_class, p_method, null );
    }


    /**
     * returns a void-method from a class
     *
     * @param p_class     class
     * @param p_method    methodname
     * @param p_parameter array with type-classes to define method parameter e.g. new Class[]{Integer.TYPE,
     *                    Integer.TYPE};
     * @return method
     */
    public static Method getClassMethod( Class p_class, String p_method, Class[] p_parameter )
    {
        Method l_method = null;
        try
        {
            l_method = p_class.getDeclaredMethod( p_method, p_parameter );
            l_method.setAccessible( true );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( "method [" + p_method + "] not found in class [" + p_class.getCanonicalName() + "]" );
        }
        return l_method;
    }

}
