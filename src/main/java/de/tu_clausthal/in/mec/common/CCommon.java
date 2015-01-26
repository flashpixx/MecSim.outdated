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

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Collection;


/**
 * class for any helper calls
 */
public class CCommon
{

    /**
     * converts any collection type into a typed array
     *
     * @param p_collection collection
     * @param <T>          collection / array type
     * @return typed array
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
     * @param p_object object for label
     * @param p_label  label name of the object
     * @return resource string
     */
    public static String getResouceString( Object p_object, String p_label, Object... p_parameter )
    {
        return MessageFormat.format( CConfiguration.getInstance().getResourceBundle().getString( p_object.getClass().getSimpleName().toLowerCase() + "." + p_label.toLowerCase().replace( " ", "" ) ), p_parameter );
    }

    /**
     * returns a string of the resouce file
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @return resource string
     */
    public static String getResouceString( Class p_class, String p_label, Object... p_parameter )
    {
        return MessageFormat.format( CConfiguration.getInstance().getResourceBundle().getString( p_class.getSimpleName().toLowerCase() + "." + p_label.toLowerCase().replace( " ", "" ) ), p_parameter );
    }

}
