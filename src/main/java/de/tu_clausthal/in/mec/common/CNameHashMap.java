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


import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;


/**
 * named-base hashmap for Json serialization
 *
 * @warning map within map should be converted with copy-ctor
 */
@SuppressWarnings( "serial" )
public class CNameHashMap extends HashMap<String, Object> implements Iterable<Map.Entry<CPath, Object>>
{
    /**
     * ctor
     */
    public CNameHashMap()
    {
        super();
    }

    /**
     * copy-ctor - creates a plain name-hash-map from a map of maps
     * with a full deep copy of keys and values
     *
     * @param p_data map
     */
    @SuppressWarnings( "unchecked" )
    public CNameHashMap( final Map<String, Object> p_data )
    {
        super();
        for ( Map.Entry<String, Object> l_item : p_data.entrySet() )
            this.put(
                    l_item.getKey(),
                    l_item.getValue() instanceof Map ? new CNameHashMap( (Map) l_item.getValue() ) : l_item.getValue()
            );
    }

    /**
     * traverse into the map
     *
     * @param p_path path of the items
     * @param p_map browsing map
     * @return object
     *
     * @tparam T type
     */
    @SuppressWarnings( "unchecked" )
    private static <T> T get( final CPath p_path, final Map<String, Object> p_map )
    {
        if ( p_path.isEmpty() )
            return null;

        final Object l_return = p_map.get( p_path.get( 0 ) );
        if ( p_path.size() == 1 )
            return (T) l_return;
        if ( l_return instanceof Map )
            return (T) get( p_path.getSubPath( 1 ), (Map) l_return );

        return (T) l_return;
    }

    /**
     * static traverse to set data
     *
     * @param p_path path
     * @param p_currentindex current path index
     * @param p_value value
     * @param p_map current map
     * @tparam T value type
     */
    @SuppressWarnings( "unchecked" )
    private static <T> void set( final CPath p_path, final int p_currentindex, final T p_value, final Map<String, Object> p_map )
    {
        if ( p_currentindex >= p_path.size() )
            return;

        // check if last element
        if ( p_currentindex == p_path.size() - 1 )
            p_map.put( p_path.get( p_currentindex ), p_value );
        else
        {
            if ( !p_map.containsKey( p_path.get( p_currentindex ) ) )
                throw new IllegalStateException( CCommon.getResourceString( CNameHashMap.class, "notfound", p_path ) );

            final Object l_return = p_map.get( p_path.get( p_currentindex ) );
            if ( l_return instanceof Map )
                set( p_path, p_currentindex + 1, p_value, (Map) l_return );
        }
    }

    /**
     * check if a path exists
     *
     * @param p_path path
     * @param p_map browsing map
     * @return boolean
     */
    @SuppressWarnings( "unchecked" )
    private static boolean traverseContainsKey( final CPath p_path, final Map<String, Object> p_map )
    {
        if ( p_path.isEmpty() )
            return false;
        if ( !p_map.containsKey( p_path.get( 0 ) ) )
            return false;

        // get return value - if a map is found and the path does not contain one element, start traversing, otherwise break
        final Object l_return = p_map.get( p_path.get( 0 ) );
        if ( ( l_return instanceof Map ) && ( p_path.size() > 1 ) )
            return traverseContainsKey( p_path.getSubPath( 1 ), (Map) l_return );

        return p_path.size() == 1;
    }

    /**
     * traverse into the map
     *
     * @param p_path path of the items
     * @return object
     *
     * @tparam T type
     */
    @SuppressWarnings( "unchecked" )
    public final <T> T get( final CPath p_path )
    {
        return get( p_path, this );
    }

    /**
     * traverse into the map
     *
     * @param p_path string path
     * @return object
     *
     * @tparam T type
     */
    public final <T> T get( final String p_path )
    {
        return this.<T>get( new CPath( p_path ) );
    }

    /**
     * returns a value or a default
     *
     * @param p_path path of the value
     * @param p_default default
     * @return value
     *
     * @tparam T type
     */
    @SuppressWarnings( "unchecked" )
    public <T> T getOrDefault( final CPath p_path, final Object p_default )
    {
        return this.traverseContainsKey( p_path ) ? this.<T>get( p_path ) : (T) p_default;
    }

    /**
     * returns a value or a default
     *
     * @param p_path path of the value
     * @param p_default default
     * @return value
     *
     * @tparam T type
     */
    public <T> T getOrDefault( final String p_path, final Object p_default )
    {
        return this.getOrDefault( new CPath( p_path ), p_default );
    }

    /**
     * recrusive traversion with an serial iterator
     *
     * @return iterator
     */
    public final Iterator<Map.Entry<CPath, Object>> iterator()
    {
        return new Iterator<Map.Entry<CPath, Object>>()
        {
            /**
             * path of the current stack
             */
            private final CPath m_path = new CPath();
            /**
             * stack with iterator
             */
            private Stack<Iterator<Entry<String, Object>>> m_stackiterator = new Stack<Iterator<Entry<String, Object>>>()
            {{
                    push( CNameHashMap.super.entrySet().iterator() );
                }};

            @Override
            public boolean hasNext()
            {
                if ( m_stackiterator.isEmpty() )
                    return false;
                if ( m_stackiterator.peek().hasNext() )
                    return true;

                m_stackiterator.pop();
                m_path.removeSuffix();

                return this.hasNext();
            }

            @Override
            @SuppressWarnings( "unchecked" )
            public Map.Entry<CPath, Object> next()
            {
                final Map.Entry<String, Object> l_item = m_stackiterator.peek().next();

                // check empty map, because iterator breaks down
                if ( ( l_item.getValue() instanceof Map ) && ( !( ( (Map) l_item.getValue() ).isEmpty() ) ) )
                {
                    m_path.pushback( l_item.getKey() );
                    m_stackiterator.push( ( (Map) l_item.getValue() ).entrySet().iterator() );
                    return this.next();
                }

                // returns an immutable entry, because modfication must pass traversal, so modification is forbidden
                return new AbstractMap.SimpleImmutableEntry<>( new CPath( m_path, l_item.getKey() ), l_item.getValue() );
            }
        };
    }

    /**
     * traverse and sets the value
     *
     * @param p_path path
     * @param p_value value
     * @tparam T type
     */
    public final <T> void set( final String p_path, final T p_value )
    {
        this.set( new CPath( p_path ), p_value );
    }

    /**
     * traverse and sets the value
     *
     * @param p_path path
     * @param p_value value
     * @tparam T type
     */
    public final <T> void set( final CPath p_path, final T p_value )
    {
        this.set( p_path, 0, p_value, this );
    }

    /**
     * check a key with traversing call
     *
     * @param p_key key name
     * @return boolean of key existance
     */
    public boolean traverseContainsKey( final String p_key )
    {
        return traverseContainsKey( new CPath( p_key ), this );
    }

    /**
     * check a key with traversing call
     *
     * @param p_key key name
     * @return boolean of key existance
     */
    public boolean traverseContainsKey( final CPath p_key )
    {
        return traverseContainsKey( p_key, this );
    }

    /**
     * immutable map
     */
    public static class CImmutable extends CNameHashMap
    {
        /**
         * ctor
         */
        public CImmutable()
        {
            super();
        }

        /**
         * creates a plain name-hash-map from a map of maps
         *
         * @param p_data map
         */
        @SuppressWarnings( "unchecked" )
        public CImmutable( final Map<? extends String, ?> p_data )
        {
            super();
            for ( final Map.Entry<? extends String, ?> l_item : p_data.entrySet() )
                this.put(
                        l_item.getKey(),
                        l_item.getValue() instanceof Map ? new CImmutable( (Map) l_item.getValue() ) : l_item.getValue()
                );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public final Object put( final String p_key, final Object p_value )
        {
            return super.put( p_key, p_value instanceof Map ? new CImmutable( (Map) p_value ) : p_value );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public final void putAll( final Map<? extends String, ?> p_map )
        {
            for ( final Map.Entry<? extends String, ?> l_item : p_map.entrySet() )
                this.put(
                        l_item.getKey(),
                        l_item.getValue() instanceof Map ? new CImmutable( (Map) l_item.getValue() ) : l_item.getValue()
                );
        }

        @Override
        public final Object remove( final Object p_key )
        {
            return this.get( p_key );
        }

        @Override
        public void clear()
        {
        }

        /**
         * converts the immutable map to a default hashmap
         *
         * @return default map
         */
        public final Map<String, Object> toHashMap()
        {
            final Map<String, Object> l_return = new HashMap<>();
            for ( final Map.Entry<String, Object> l_item : this.entrySet() )
                l_return.put( l_item.getKey(), l_item.getValue() instanceof CImmutable ? ( (CImmutable) l_item.getValue() ).toHashMap() : l_item.getValue() );

            return l_return;
        }

    }

}
