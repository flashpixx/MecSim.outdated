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

package de.tu_clausthal.in.mec.common;


import de.tu_clausthal.in.mec.CLogger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
    public static CGetSet getClassField( final Class<?> p_class, final String p_field ) throws IllegalArgumentException
    {
        Field l_field = null;
        for ( Class<?> l_class = p_class; ( l_field == null ) && ( l_class != null ); l_class = l_class.getSuperclass() )
            try
            {
                l_field = l_class.getDeclaredField( p_field );
            }
            catch ( Exception l_exception )
            {
            }

        if ( l_field == null )
            throw new IllegalArgumentException( CCommon.getResourceString( CReflection.class, "fieldnotfound", p_field, p_class.getCanonicalName() ) );

        l_field.setAccessible( true );
        CGetSet l_struct = null;
        try
        {
            l_struct = new CGetSet( l_field, MethodHandles.lookup().unreflectGetter( l_field ), MethodHandles.lookup().unreflectSetter( l_field ) );
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
    public static Map<String, CGetSet> getClassFields( final Class<?> p_class )
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
    public static Map<String, CGetSet> getClassFields( final Class<?> p_class, final IFieldFilter p_filter )
    {
        Map<String, CGetSet> l_fields = new HashMap<>();
        for ( Class<?> l_class = p_class; l_class != null; l_class = l_class.getSuperclass() )
            for ( Field l_field : l_class.getDeclaredFields() )
            {
                l_field.setAccessible( true );
                if ( ( p_filter != null ) && ( !p_filter.filter( l_field ) ) )
                    continue;

                try
                {
                    l_fields.put( l_field.getName(), new CGetSet( l_field, MethodHandles.lookup().unreflectGetter( l_field ), MethodHandles.lookup().unreflectSetter( l_field ) ) );
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
    public static CMethod getClassMethod( final Class<?> p_class, final String p_method ) throws IllegalArgumentException, IllegalAccessException
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
    public static CMethod getClassMethod( final Class<?> p_class, final String p_method, final Class<?>[] p_parameter ) throws IllegalArgumentException, IllegalAccessException
    {
        Method l_method = null;
        for ( Class<?> l_class = p_class; ( l_method == null ) && ( l_class != null ); l_class = l_class.getSuperclass() )
            try
            {
                l_method = l_class.getDeclaredMethod( p_method, p_parameter );
            }
            catch ( Exception l_exception )
            {
            }

        if ( l_method == null )
            throw new IllegalArgumentException( CCommon.getResourceString( CReflection.class, "methodnotfound", p_method, p_class.getCanonicalName() ) );

        l_method.setAccessible( true );
        return new CMethod( l_method );
    }


    /**
     * returns filtered methods of a class and the super classes
     *
     * @param p_class  class
     * @param p_filter filtering object
     * @return map with method name
     */
    public static Map<String, CMethod> getClassMethods( final Class<?> p_class, final IMethodFilter p_filter )
    {
        Map<String, CMethod> l_methods = new HashMap<>();
        for ( Class<?> l_class = p_class; l_class != null; l_class = l_class.getSuperclass() )
            for ( Method l_method : l_class.getDeclaredMethods() )
            {
                l_method.setAccessible( true );
                if ( ( p_filter != null ) && ( !p_filter.filter( l_method ) ) )
                    continue;

                try
                {
                    l_methods.put( l_method.getName(), new CMethod( l_method ) );
                }
                catch ( IllegalAccessException l_exception )
                {
                    CLogger.error( l_exception );
                }
            }
        return l_methods;
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
     * interface of method filtering
     */
    public interface IMethodFilter
    {

        /**
         * filter method
         *
         * @param p_method method object
         * @return true field will be added, false method will be ignored
         */
        public boolean filter( Method p_method );
    }


    /**
     * class for storing method access
     */
    public static class CMethod
    {
        /**
         * method object *
         */
        protected Method m_method = null;
        /**
         * method handle *
         */
        protected MethodHandle m_handle = null;


        /**
         * ctor
         *
         * @param p_method method object
         */
        public CMethod( Method p_method ) throws IllegalAccessException
        {
            m_method = p_method;
            m_handle = MethodHandles.lookup().unreflect( m_method );
        }

        /**
         * returns the method object
         *
         * @return method object
         */
        public Method getMethod()
        {
            return m_method;
        }

        /**
         * returns the method handle object
         *
         * @return handle object
         */
        public MethodHandle getHandle()
        {
            return m_handle;
        }

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
         * field of the property
         */
        protected Field m_field = null;


        /**
         * ctor
         *
         * @param p_field  field of the getter / setter
         * @param p_getter getter handle or null
         * @param p_setter setter handle or null
         */
        public CGetSet( Field p_field, MethodHandle p_getter, MethodHandle p_setter )
        {
            if ( p_field == null )
                throw new IllegalArgumentException( CCommon.getResourceString( this, "fieldnotnull" ) );

            m_field = p_field;
            m_getter = p_getter;
            m_setter = p_setter;
        }


        /**
         * returns the field of the bind
         *
         * @return field
         */
        public Field getField()
        {
            return m_field;
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


    /**
     * cache class of method invoker structure *
     */
    public static class CMethodCache<T>
    {

        /**
         * object reference *
         */
        protected T m_object = null;
        /**
         * forbidden names *
         */
        protected Set<String> m_forbidden = new HashSet<>();
        /**
         * cache map *
         */
        protected Map<Pair<String, Class<?>[]>, CMethod> m_cache = new HashMap<>();


        /**
         * ctor
         *
         * @param p_bind bind object
         */
        public CMethodCache( T p_bind )
        {
            m_object = p_bind;
        }

        /**
         * object getter
         *
         * @return bind object
         */
        public T getObject()
        {
            return m_object;
        }


        /**
         * get forbidden set
         *
         * @return set with names
         */
        public Set<String> getForbidden()
        {
            return m_forbidden;
        }


        /**
         * get the method handle
         *
         * @param p_methodname method name
         * @return handle
         */
        public CMethod get( String p_methodname ) throws IllegalAccessException
        {
            return get( p_methodname, null );
        }


        /**
         * get the method handle
         *
         * @param p_methodname method name
         * @param p_arguments  method arguments
         * @return handle
         */
        public CMethod get( String p_methodname, Class<?>[] p_arguments ) throws IllegalAccessException
        {
            Pair<String, Class<?>[]> l_method = new ImmutablePair<String, Class<?>[]>( p_methodname, p_arguments );
            if ( m_cache.containsKey( l_method ) )
                return m_cache.get( l_method );

            if ( m_forbidden.contains( p_methodname ) )
                throw new IllegalAccessException( CCommon.getResourceString( this, "access", p_methodname ) );

            CMethod l_handle = CReflection.getClassMethod( m_object.getClass(), p_methodname, p_arguments );
            m_cache.put( l_method, l_handle );
            return l_handle;
        }

    }

}
