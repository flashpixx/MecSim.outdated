/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.common;


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * boilerplate code of the reflection API
 */
public class CReflection
{
    /**
     * class index *
     *
     * @note use a whitelist for filtering, because it is faster
     */
    private static final CClassIndex c_classindex = new CClassIndex( CClassIndex.EFilter.WhiteList )
    {{

            filter(
                    CConfiguration.getPackage(),
                    // defines GeoPosition
                    "org.jxmapviewer",
                    // defines EdgeIteratorState
                    "com.graphhopper",
                    // defines triple & pair
                    "org.apache.commons.lang3.tuple",
                    // defines Java defaults
                    "java.lang", "java.util"
            );

            for ( final String l_path : new HashSet<String>()
            {{
                    addAll( Arrays.asList( System.getProperty( "java.class.path" ).split( File.pathSeparator ) ) );
                    addAll( Arrays.asList( System.getProperty( "java.ext.dirs" ).split( File.pathSeparator ) ) );
                    addAll( Arrays.asList( System.getProperty( "java.library.path" ).split( File.pathSeparator ) ) );
                }} )
            {
                final File l_file = new File( l_path );
                if ( !l_file.exists() )
                    continue;

                if ( l_path.toLowerCase().endsWith( ".jar" ) )
                    scanJar( l_file );
                else
                    scanDirectory( l_file );
            }

        }};


    /**
     * private ctor - avoid instantiation
     */
    private CReflection()
    {
    }

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
            catch ( final Exception l_exception )
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
        catch ( final IllegalAccessException l_exception )
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
     * @param p_class class
     * @param p_filter filtering object
     * @return map with field name and getter / setter handle
     */
    public static Map<String, CGetSet> getClassFields( final Class<?> p_class, final IFieldFilter p_filter )
    {
        final Map<String, CGetSet> l_fields = new HashMap<>();
        for ( Class<?> l_class = p_class; l_class != null; l_class = l_class.getSuperclass() )
            for ( final Field l_field : l_class.getDeclaredFields() )
            {
                l_field.setAccessible( true );
                if ( ( p_filter != null ) && ( !p_filter.filter( l_field ) ) )
                    continue;

                try
                {
                    l_fields.put(
                            l_field.getName(), new CGetSet(
                                    l_field, MethodHandles.lookup().unreflectGetter( l_field ), MethodHandles.lookup().unreflectSetter( l_field )
                            )
                    );
                }
                catch ( final IllegalAccessException l_exception )
                {
                    CLogger.error( l_exception );
                }
            }
        return l_fields;
    }

    /**
     * retruns the class index
     *
     * @return class index object
     */
    public static CClassIndex getClassIndex()
    {
        return c_classindex;
    }

    /**
     * returns a void-method from a class
     *
     * @param p_class class
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
     * @param p_class class
     * @param p_method methodname
     * @param p_parameter array with type-classes to define method parameter e.g. new Class[]{Integer.TYPE,
     * Integer.TYPE};
     * @return method
     */
    public static CMethod getClassMethod( final Class<?> p_class, final String p_method, final Class<?>[] p_parameter )
            throws IllegalArgumentException, IllegalAccessException
    {
        Method l_method = null;
        for ( Class<?> l_class = p_class; ( l_method == null ) && ( l_class != null ); l_class = l_class.getSuperclass() )
            try
            {
                l_method = l_class.getDeclaredMethod( p_method, p_parameter );
            }
            catch ( final Exception l_exception )
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
     * @param p_class class
     * @param p_filter filtering object
     * @return map with method name
     */
    public static Map<String, CMethod> getClassMethods( final Class<?> p_class, final IMethodFilter p_filter )
    {
        final Map<String, CMethod> l_methods = new HashMap<>();
        for ( Class<?> l_class = p_class; l_class != null; l_class = l_class.getSuperclass() )
            for ( final Method l_method : l_class.getDeclaredMethods() )
            {
                l_method.setAccessible( true );
                if ( ( p_filter != null ) && ( !p_filter.filter( l_method ) ) )
                    continue;

                try
                {
                    l_methods.put( l_method.getName(), new CMethod( l_method ) );
                }
                catch ( final IllegalAccessException l_exception )
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
        boolean filter( final Field p_field );
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
        boolean filter( final Method p_method );
    }

    /**
     * class to represent a class index
     */
    public static class CClassIndex
    {
        /**
         * extension of the class files (without dot) *
         */
        private static final String c_classextension = "class";
        /**
         * map with classname and class object *
         */
        private final MultiValueMap<String, Class<?>> m_classname = new MultiValueMap<>();
        /**
         * parts (start-with) for ignores *
         */
        private final Set<String> m_filter = new HashSet<>();
        /**
         * filter type
         */
        private final EFilter m_filterdefinition;

        /**
         * ctor
         *
         * @param p_filter filter type
         */
        public CClassIndex( final EFilter p_filter )
        {
            m_filterdefinition = p_filter;
        }

        /**
         * adds an element to the filter list
         *
         * @param p_startwith class part name
         */
        public void filter( final String... p_startwith )
        {
            m_filter.addAll( Arrays.asList( p_startwith ) );
        }

        /**
         * returns the first entry of the name
         *
         * @param p_class simple class name
         * @return first match
         */
        public Class<?> get( final String p_class )
        {
            final Collection<Class<?>> l_return = m_classname.getCollection( p_class );
            if ( ( l_return != null ) && ( l_return.iterator().hasNext() ) )
                return l_return.iterator().next();
            return null;
        }

        /**
         * returns the collection of all classes
         *
         * @param p_class simple class name
         * @return collection of classes objects
         */
        public Collection<Class<?>> getAll( final String p_class )
        {
            return m_classname.getCollection( p_class );
        }

        /**
         * scans a directory for classes
         *
         * @param p_path path
         */
        public void scanDirectory( final File p_path )
        {
            if ( !p_path.isDirectory() )
                throw new IllegalArgumentException( CCommon.getResourceString( this, "notdirectory", p_path ) );

            for ( final File l_item : FileUtils.listFiles( p_path, new String[]{c_classextension}, true ) )
                try
                {
                    final String l_classname = this.getClassnameFromFile( l_item.getAbsolutePath().replace( p_path.getAbsolutePath() + File.separator, "" ) );
                    if ( ( ( EFilter.BlackList.equals( m_filterdefinition ) ) && ( this.startsIgnoreWith( l_classname ) ) ) || ( ( EFilter.WhiteList.equals(
                            m_filterdefinition
                    ) ) && ( !this.startsIgnoreWith( l_classname ) ) ) )
                        continue;

                    final Class<?> l_class = Class.forName( l_classname );
                    if ( !l_class.getSimpleName().isEmpty() )
                        m_classname.put( l_class.getSimpleName(), l_class );
                }
                catch ( final Exception l_exception )
                {
                }
                catch ( final Error l_error )
                {
                }
        }

        /**
         * scans a Jar for classes
         *
         * @param p_jar Jar file
         */
        public void scanJar( final File p_jar )
        {
            if ( !p_jar.isFile() )
                throw new IllegalArgumentException( CCommon.getResourceString( this, "notfile", p_jar ) );

            try (
                    final JarFile l_jar = new JarFile( p_jar )
            )
            {
                for ( final Enumeration<JarEntry> l_entry = l_jar.entries(); l_entry.hasMoreElements(); )
                {
                    final String l_file = l_entry.nextElement().getName();
                    if ( l_file.endsWith( c_classextension ) )
                        try
                        {
                            final String l_classname = this.getClassnameFromFile( l_file );
                            if ( ( ( EFilter.BlackList.equals( m_filterdefinition ) ) && ( this.startsIgnoreWith( l_classname ) ) ) ||
                                 ( ( EFilter.WhiteList.equals( m_filterdefinition ) ) && ( !this.startsIgnoreWith( l_classname ) ) ) )
                                continue;

                            final Class<?> l_class = Class.forName( l_classname );
                            if ( !l_class.getSimpleName().isEmpty() )
                                m_classname.put( l_class.getSimpleName(), l_class );
                        }
                        catch ( final Exception l_exception )
                        {
                        }
                        catch ( final Error l_error )
                        {
                        }
                }
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
            }

        }

        /**
         * replaces a file name with path to a class name
         *
         * @param p_file filename
         * @return class name
         */
        private String getClassnameFromFile( String p_file )
        {
            return p_file.replace( "." + c_classextension, "" ).replace( File.separator, ClassUtils.PACKAGE_SEPARATOR );
        }

        /**
         * checks a string class name with the ignore list
         *
         * @param p_classname class string name
         * @return true on existsing of the ignore list
         */
        private boolean startsIgnoreWith( final String p_classname )
        {
            for ( final String l_item : m_filter )
                if ( p_classname.startsWith( l_item ) )
                    return true;

            return false;
        }

        /**
         * enum to define the filter *
         */
        enum EFilter
        {
            /**
             * blacklisting items *
             */
            BlackList,
            /**
             * whitelisting items *
             */
            WhiteList
        }

    }

    /**
     * structure for getter and setter method handles
     */
    public static class CGetSet
    {

        /**
         * field of the property
         */
        private final Field m_field;
        /**
         * getter method handle *
         */
        private final MethodHandle m_getter;
        /**
         * setter method handle *
         */
        private final MethodHandle m_setter;

        /**
         * ctor
         *
         * @param p_field field of the getter / setter
         * @param p_getter getter handle or null
         * @param p_setter setter handle or null
         */
        public CGetSet( final Field p_field, final MethodHandle p_getter, final MethodHandle p_setter )
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
        public final Field getField()
        {
            return m_field;
        }

        /**
         * returns the getter
         *
         * @return handle
         */
        public final MethodHandle getGetter()
        {
            return m_getter;
        }

        /**
         * returns the setter
         *
         * @return handle
         */
        public final MethodHandle getSetter()
        {
            return m_setter;
        }

        /**
         * check getter exist
         *
         * @return bool flag
         */
        public final boolean hasGetter()
        {
            return m_getter != null;
        }


        /**
         * check setter exist
         *
         * @return bool flag
         */
        public final boolean hasSetter()
        {
            return m_setter != null;
        }

    }

    /**
     * class for storing method access
     */
    public static class CMethod
    {
        /**
         * method handle *
         */
        private final MethodHandle m_handle;
        /**
         * method object *
         */
        private final Method m_method;

        /**
         * ctor
         *
         * @param p_method method object
         */
        public CMethod( final Method p_method ) throws IllegalAccessException
        {
            m_method = p_method;
            m_handle = MethodHandles.lookup().unreflect( m_method );
        }

        /**
         * returns the method handle object
         *
         * @return handle object
         */
        public final MethodHandle getHandle()
        {
            return m_handle;
        }

        /**
         * returns the method object
         *
         * @return method object
         */
        public final Method getMethod()
        {
            return m_method;
        }

    }

    /**
     * cache class of method invoker structure *
     */
    public static class CMethodCache<T>
    {

        /**
         * cache map *
         */
        private final Map<Pair<String, Class<?>[]>, CMethod> m_cache = new HashMap<>();
        /**
         * method filter
         */
        private final IMethodFilter m_filter;
        /**
         * object reference *
         */
        private final T m_object;

        /**
         * ctor
         *
         * @param p_bind bind object
         */
        public CMethodCache( final T p_bind )
        {
            this( p_bind, null );
        }

        /**
         * ctor
         *
         * @param p_bind bind object
         * @param p_filter method filter object
         */
        public CMethodCache( final T p_bind, final IMethodFilter p_filter )
        {
            m_object = p_bind;
            m_filter = p_filter;
        }

        /**
         * get the method handle
         *
         * @param p_methodname method name
         * @return handle
         */
        public final CMethod get( final String p_methodname ) throws IllegalAccessException
        {
            return this.get( p_methodname, null );
        }

        /**
         * get the method handle
         *
         * @param p_methodname method name
         * @param p_arguments method arguments
         * @return handle
         */
        public final CMethod get( final String p_methodname, final Class<?>[] p_arguments ) throws IllegalAccessException
        {
            // check cache
            final Pair<String, Class<?>[]> l_method = new ImmutablePair<String, Class<?>[]>( p_methodname, p_arguments );
            if ( m_cache.containsKey( l_method ) )
                return m_cache.get( l_method );

            // get method and check filter
            final CMethod l_handle = CReflection.getClassMethod( m_object.getClass(), p_methodname, p_arguments );
            if ( ( m_filter != null ) && ( !m_filter.filter( l_handle.getMethod() ) ) )
                throw new IllegalAccessException( CCommon.getResourceString( this, "access", p_methodname ) );

            // add to cache
            m_cache.put( l_method, l_handle );
            return l_handle;
        }

        /**
         * object getter
         *
         * @return bind object
         */
        public final T getObject()
        {
            return m_object;
        }

    }

}
