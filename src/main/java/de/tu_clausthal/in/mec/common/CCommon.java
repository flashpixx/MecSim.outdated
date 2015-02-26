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

import de.tu_clausthal.in.mec.CConfiguration;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Collection;


/**
 * class for any helper calls
 */
public class CCommon
{

    /**
     * remove from a string the system package name
     *
     * @param p_package package / class path
     * @return path without main package name
     */
    public static String removePackageName( String p_package )
    {
        return p_package.replace( "de.tu_clausthal.in.mec.", "" );
    }


    /**
     * converts any collection type into a typed array
     *
     * @param p_class      class array
     * @param p_collection collection
     * @return typed array
     * @tparam T collection / array type
     */
    public static <T> T[] CollectionToArray( Class<T[]> p_class, Collection<T> p_collection )
    {
        T[] l_return = p_class.cast( Array.newInstance( p_class.getComponentType(), p_collection.size() ) );
        p_collection.toArray( l_return );
        return l_return;
    }

    /**
     * returns a string of the resource file
     *
     * @param p_object    object for label
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResourceString( Object p_object, String p_label, Object... p_parameter )
    {
        return MessageFormat.format( CConfiguration.getInstance().getResourceBundle().getString( removePackageName( p_object.getClass().getCanonicalName().toLowerCase() ) + "." + p_label.toLowerCase().replace( " ", "" ) ), p_parameter );
    }

    /**
     * returns a string of the resource file
     *
     * @param p_class     class for static calls
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResourceString( Class<?> p_class, String p_label, Object... p_parameter )
    {
        return MessageFormat.format( CConfiguration.getInstance().getResourceBundle().getString( removePackageName( p_class.getCanonicalName().toLowerCase() ) + "." + p_label.toLowerCase().replace( " ", "" ) ), p_parameter );
    }

    /**
     * adds a file extension if necessary
     *
     * @param p_file   file object
     * @param p_suffix suffix
     * @return file with extension
     */
    public static File addFileExtension( File p_file, String p_suffix )
    {
        File l_file = p_file;
        if ( !l_file.getAbsolutePath().endsWith( p_suffix ) )
            l_file = new File( l_file + p_suffix );
        return l_file;
    }


    /**
     * returns the hash of a string
     *
     * @param p_string input string
     * @param p_hash   hash algorithm
     * @return hexadecimal hash value
     */
    public static String getHash( final String p_string, final String p_hash )
    {
        try
        {
            return getBytes2Hex( MessageDigest.getInstance( p_hash ).digest( p_string.getBytes() ) );
        }
        catch ( Exception l_exception )
        {
        }

        return null;
    }


    /**
     * @param p_file input file
     * @param p_hash hash algorithm
     * @return hexadecimal hash value
     */
    public static String getHash( final File p_file, final String p_hash )
    {
        try
        {
            return getBytes2Hex( MessageDigest.getInstance( p_hash ).digest( Files.readAllBytes( Paths.get( p_file.toString() ) ) ) );
        }
        catch ( Exception l_exception )
        {
        }

        return null;
    }

    /**
     * returns a string with hexadecimal bytes
     *
     * @param p_bytes input bytes
     * @return hexadecimal string
     */
    protected static String getBytes2Hex( byte[] p_bytes )
    {
        StringBuilder l_str = new StringBuilder( 2 * p_bytes.length );
        for ( byte l_byte : p_bytes )
            l_str.append( String.format( "%02x", l_byte & 0xff ) );

        return l_str.toString();
    }

}
