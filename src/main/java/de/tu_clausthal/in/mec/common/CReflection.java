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


import de.tu_clausthal.in.mec.CLogger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * boilerplate code of the reflection API
 */
public class CReflection
{

    /**
     * get a class field of the class or super classes and returns getter / setter handles
     *
     * @param p_class class
     * @param p_field fieldname
     * @return getter / setter handle object
     */
    public static CGetSet getClassField( Class p_class, String p_field ) throws IllegalArgumentException
    {
        Field l_field = null;
        for ( Class l_class = p_class; ( l_field == null ) && ( l_class != null ); l_class = l_class.getSuperclass() )
            try
            {
                l_field = l_class.getDeclaredField( p_field );
            }
            catch ( Exception l_exception )
            {
            }

        if ( l_field == null )
            throw new IllegalArgumentException( CCommon.getResouceString( CReflection.class, "fieldnotfound", p_field, p_class.getCanonicalName() ) );

        l_field.setAccessible( true );
        CGetSet l_struct = null;
        try
        {
            l_struct = new CGetSet( MethodHandles.lookup().unreflectGetter( l_field ), MethodHandles.lookup().unreflectSetter( l_field ) );
        }
        catch ( IllegalAccessException l_exception )
        {
            CLogger.error( l_exception );
        }

        return l_struct;
    }

    /**
     * returns all fields of a class and the super classes
     *
     * @param p_class class
     * @return map with field name and getter / setter handle
     */
    public static Map<String, CGetSet> getClassFields( Class p_class )
    {
        return getClassFields( p_class, null );
    }

    /**
     * returns filtered fields of a class and the super classes
     *
     * @param p_class  class
     * @param p_filter filtering object
     * @return map with field name and getter / setter handle
     */
    public static Map<String, CGetSet> getClassFields( Class p_class, IFieldFilter p_filter )
    {
        Map<String, CGetSet> l_fields = new HashMap();
        for ( Class l_class = p_class; l_class != null; l_class = l_class.getSuperclass() )
            for ( Field l_field : l_class.getDeclaredFields() )
            {
                l_field.setAccessible( true );
                if ( ( p_filter != null ) && ( !p_filter.filter( l_field ) ) )
                    continue;

                try
                {
                    l_fields.put( l_field.getName(), new CGetSet( MethodHandles.lookup().unreflectGetter( l_field ), MethodHandles.lookup().unreflectSetter( l_field ) ) );
                }
                catch ( IllegalAccessException l_exception )
                {
                    CLogger.error( l_exception );
                }
            }
        return l_fields;
    }

    /**
     * returns a void-method from a class
     *
     * @param p_class  class
     * @param p_method methodname
     * @return method
     */
    public static MethodHandle getClassMethod( Class p_class, String p_method ) throws IllegalArgumentException, IllegalAccessException
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
    public static MethodHandle getClassMethod( Class p_class, String p_method, Class[] p_parameter ) throws IllegalArgumentException, IllegalAccessException
    {
        Method l_method = null;
        for ( Class l_class = p_class; ( l_method == null ) && ( l_class != null ); l_class = l_class.getSuperclass() )
            try
            {
                l_method = l_class.getDeclaredMethod( p_method, p_parameter );
            }
            catch ( Exception l_exception )
            {
            }

        if ( l_method == null )
            throw new IllegalArgumentException( CCommon.getResouceString( CReflection.class, "methodnotfound", p_method, p_class.getCanonicalName() ) );

        l_method.setAccessible( true );
        return MethodHandles.lookup().unreflect( l_method );
    }

    /**
     * interface of field filtering
     */
    public interface IFieldFilter
    {

        /**
         * filter method
         *
         * @param p_field field object
         * @return true field will be added, false field will be ignored
         */
        public boolean filter( Field p_field );
    }

    /**
     * structure for getter and setter method handles
     */
    public static class CGetSet
    {

        /**
         * getter method handle *
         */
        protected MethodHandle m_getter = null;
        /**
         * setter method handle *
         */
        protected MethodHandle m_setter = null;


        /**
         * ctor
         *
         * @param p_getter getter handle or null
         * @param p_setter setter handle or null
         */
        public CGetSet( MethodHandle p_getter, MethodHandle p_setter )
        {
            m_getter = p_getter;
            m_setter = p_setter;
        }

        /**
         * returns the getter
         *
         * @return handle
         */
        public MethodHandle getGetter()
        {
            return m_getter;
        }

        /**
         * returns the setter
         *
         * @return handle
         */
        public MethodHandle getSetter()
        {
            return m_setter;
        }

        /**
         * check getter exist
         *
         * @return bool flag
         */
        public boolean hasGetter()
        {
            return m_getter != null;
        }


        /**
         * check setter exist
         *
         * @return bool flag
         */
        public boolean hasSetter()
        {
            return m_setter != null;
        }

    }

}
