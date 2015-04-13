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


import java.util.HashMap;
import java.util.Map;


/**
 * named-base hashmap for Json serialization
 */
public class CNameHashMap extends HashMap<String, Object>
{

    /**
     * ctor
     */
    public CNameHashMap()
    {
        super();
    }

    /**
     * creates a plain name-hash-map from a map of maps
     *
     * @param p_data map
     */
    @SuppressWarnings( "unchecked" )
    public CNameHashMap( final Map<String, Object> p_data )
    {
        super();
        for ( Map.Entry<String, Object> l_item : p_data.entrySet() )
            this.put( l_item.getKey(), ( l_item.getValue() instanceof Map ) ? new CNameHashMap( (Map) l_item.getValue() ) : l_item.getValue() );
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
    private static <T> void setTraverse( final CPath p_path, final int p_currentindex, final T p_value, final Map<String, Object> p_map )
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
                setTraverse( p_path, p_currentindex + 1, p_value, (Map) l_return );
        }
    }

    /**
     * traverse and sets the value
     *
     * @param p_path path
     * @param p_value value
     * @tparam T type
     * @bug exception
     */
    public final <T> void setTraverse( final CPath p_path, final T p_value )
    {
        this.setTraverse( p_path, 0, p_value, this );
    }

    /**
     * traverse and sets the value
     *
     * @param p_path path
     * @param p_value value
     * @tparam T type
     */
    public final <T> void setTraverse( final String p_path, final T p_value )
    {
        this.setTraverse( new CPath( p_path ), p_value );
    }


    /**
     * traverse into the map
     *
     * @param p_path path of the items
     * @return object
     * @tparam T type
     */
    @SuppressWarnings( "unchecked" )
    public final <T> T getTraverse( final CPath p_path )
    {
        if ( p_path.isEmpty() )
            return null;

        final Object l_return = this.get( p_path.get( 0 ) );
        if ( l_return instanceof CNameHashMap )
            return ( (CNameHashMap) l_return ).getTraverse( p_path.getSubPath( 1 ) );
        if ( l_return instanceof Map )
            return (T) ( (Map) l_return ).get( p_path.get( 0 ) );

        return (T) l_return;
    }


    /**
     * traverse into the map
     *
     * @param p_path string path
     * @return object
     * @tparam T type
     */
    public final <T> T getTraverse( final String p_path )
    {
        return this.<T>getTraverse( new CPath( p_path ) );
    }


    /**
     * get a type-cast value of the object
     *
     * @param p_key key
     * @return null or casted value
     * @tparam T type
     */
    @SuppressWarnings( "unchecked" )
    public final <T> T getTypedValue( final String p_key )
    {
        return (T) this.get( p_key );
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
            for ( Map.Entry<? extends String, ?> l_item : p_data.entrySet() )
                this.put( l_item.getKey(), l_item.getValue() instanceof Map ? new CImmutable( (Map) l_item.getValue() ) : l_item.getValue() );
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
            for ( Map.Entry<? extends String, ?> l_item : p_map.entrySet() )
                this.put( l_item.getKey(), l_item.getValue() instanceof Map ? new CImmutable( (Map) l_item.getValue() ) : l_item.getValue() );
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
            for ( Map.Entry<String, Object> l_item : this.entrySet() )
                l_return.put( l_item.getKey(), l_item.getValue() instanceof CImmutable ? ( (CImmutable) l_item.getValue() ).toHashMap() : l_item.getValue() );

            return l_return;
        }

    }

}
