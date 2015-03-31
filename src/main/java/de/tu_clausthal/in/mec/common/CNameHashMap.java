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
     * traverse and sets the value
     *
     * @param p_path  path
     * @param p_value value
     * @tparam T type
     * @bug exception
     */
    public <T> void setTraverse( final CPath p_path, final T p_value )
    {
        if ( p_path.isEmpty() ) return;

        if ( p_path.size() > 1 )
        {
            final Object l_return = this.get( p_path.get( 0 ) );
            if ( l_return instanceof CNameHashMap )
                ( (CNameHashMap) l_return ).setTraverse( p_path.getSubPath( 1 ), p_value );

            throw new IllegalStateException();
        }

        this.put( p_path.get( 0 ), p_value );
    }


    /**
     * traverse and sets the value
     *
     * @param p_path  path
     * @param p_value value
     * @tparam T type
     */
    public <T> void setTraverse( final String p_path, final T p_value )
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
    public <T> T getTraverse( final CPath p_path )
    {
        if ( p_path.isEmpty() ) return null;

        final Object l_return = this.get( p_path.get( 0 ) );
        if ( l_return instanceof CNameHashMap )
            return ( (CNameHashMap) l_return ).getTraverse( p_path.getSubPath( 1 ) );
        if ( l_return instanceof Map ) return (T) ( (Map) l_return ).get( p_path.get( 0 ) );

        return (T) l_return;
    }


    /**
     * traverse into the map
     *
     * @param p_path string path
     * @return object
     * @tparam T type
     */
    public <T> T getTraverse( final String p_path )
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
    public <T> T getTypedValue( final String p_key )
    {
        return (T) this.get( p_key );
    }


    /**
     * immutable map
     */
    public static class CImmutable extends CNameHashMap
    {

        @Override
        public Object remove( final Object p_key )
        {
            return this.get( p_key );
        }

        @Override
        public void clear()
        {
        }

    }

}
