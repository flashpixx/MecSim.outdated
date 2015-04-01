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


/**
 * named-base hashmap for Json serialization
 */
public class CNameHashMap extends HashMap<String, Object>
{

    /**
     * immutable map
     */
    public static class CImmutable extends CNameHashMap
    {

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
