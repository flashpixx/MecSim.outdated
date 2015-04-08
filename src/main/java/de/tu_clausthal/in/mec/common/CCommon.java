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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;


/**
 * class for any helper calls
 */
public class CCommon
{

    /**
     * private ctor - avoid instantiation
     */
    private CCommon()
    {
    }


    /**
     * remove from a string the system package name
     *
     * @param p_package package / class path
     * @return path without main package name
     */
    public static String removePackageName( final String p_package )
    {
        return p_package.replace( "de.tu_clausthal.in.mec.", "" );
    }


    /**
     * convert any object data into Json
     *
     * @return Json object
     */
    public static String toJson( final Object p_data )
    {
        try (
                final ByteArrayOutputStream l_stream = new ByteArrayOutputStream();
        )
        {
            new ObjectMapper().writer().writeValue( l_stream, p_data );
            return l_stream.toString();
        }
        catch ( IOException e )
        {
        }
        return null;
    }


    /**
     * converts a Json string data into a raw map
     *
     * @return raw map data
     */
    public static Map<String, Object> fromJson( final String p_data )
    {
        return fromJson( p_data, Map.class );
    }


    /**
     * converts a Json string into any class object
     *
     * @param p_data  string data
     * @param p_class class type
     * @return deserialized type
     * @tparam T type parameter
     */
    public static <T> T fromJson( final String p_data, final Class<T> p_class )
    {
        try
        {
            return new ObjectMapper().readValue( p_data, p_class );
        }
        catch ( IOException e )
        {
        }

        return null;
    }


    /**
     * creates a map from parameters
     *
     * @param p_objects list with pairs of string and object
     * @return map with data
     */
    public static Map<String, Object> getMap( final Object... p_objects )
    {
        if ( p_objects.length % 2 != 0 )
            throw new IllegalArgumentException( CCommon.getResourceString( CCommon.class, "argumentsnoteven" ) );

        String l_name = null;
        final Map<String, Object> l_return = new HashMap<>();

        for ( int i = 0; i < p_objects.length; ++i )
            if ( i % 2 == 0 ) l_name = (String) p_objects[i];
            else l_return.put( l_name, p_objects[i] );


        return l_return;
    }


    /**
     * converts a value into class
     *
     * @param p_value  string input class
     * @param p_types  type classes
     * @param p_parser type parser
     * @return converted type
     */
    public static Object convertValue( final String p_value, final Class[] p_types, final TypeParser p_parser )
    {
        for ( Class l_class : p_types )
            try
            {
                return p_parser.parseType( p_value, l_class );
            }
            catch ( final TypeParserException l_exception )
            {
            }

        return p_value;
    }


    /**
     * checks a value and returns the checkd value or the default value
     *
     * @param p_input         unchecked value
     * @param p_default       default value
     * @param p_allowedvalues allowed values
     * @return checked value
     * @tparam T              value type
     */
    public static <T> T getCheckedValue( final T p_input, final T p_default, final Collection<T> p_allowedvalues )
    {
        if ( p_input == null ) return p_default;

        for ( T l_item : p_allowedvalues )
            if ( p_input.equals( l_item ) ) return l_item;

        return p_default;
    }


    /**
     * returns a default value of an empty string
     *
     * @param p_input   input value
     * @param p_default default value
     * @return string
     */
    public static String getNonEmptyValue( final String p_input, final String p_default )
    {
        return ( ( p_input == null ) || ( p_input.isEmpty() ) ) ? p_default : p_input;
    }


    /**
     * returns root path of the resource
     *
     * @return URL of file or null
     */
    public static URL getResourceURL()
    {
        return CCommon.class.getClassLoader().getResource( "" );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file
     * @return URL of file or null
     */
    public static URL getResourceURL( final String p_file )
    {
        return getResourceURL( new File( p_file ) );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file relative to the CMain
     * @return URL of file or null
     * @note the Jar path is removed if exists
     */
    public static URL getResourceURL( final File p_file )
    {
        try
        {
            if ( p_file.exists() ) return p_file.toURI().toURL();

            return CCommon.class.getClassLoader().getResource( p_file.toString().replace( CCommon.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "" ) );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( CCommon.getResourceString( CCommon.class, "sourcenotfound", p_file ) );
        }

        return null;
    }


    /**
     * returns the label of a class and string to get access to the resource
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @return label name
     */
    public static String getResourceStringLabel( final Class<?> p_class, final String p_label )
    {
        return removePackageName( p_class.getCanonicalName().toLowerCase() ).replaceAll( "[^a-zA-Z0-9_\\.]+", "" ) + "." + p_label.toLowerCase().replace( " ", "" );
    }


    /**
     * returns a default string of the resource file
     *
     * @param p_object    object for label
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResourceString( final Object p_object, final String p_label, final Object... p_parameter )
    {
        return getResourceString( null, p_object.getClass(), p_label, p_parameter );
    }

    /**
     * returns a string of the resource file
     *
     * @param p_language  language code / empty for default
     * @param p_object    object for label
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResourceString( final String p_language, final Object p_object, final String p_label, final Object... p_parameter )
    {
        return getResourceString( p_language, p_object.getClass(), p_label, p_parameter );
    }

    /**
     * returns a default string of the resource file
     *
     * @param p_class     class for static calls
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResourceString( final Class<?> p_class, final String p_label, final Object... p_parameter )
    {
        return getResourceString( null, p_class, p_label, p_parameter );
    }

    /**
     * returns a string of the resource file
     *
     * @param p_language  language code / empty for default
     * @param p_class     class for static calls
     * @param p_label     label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String getResourceString( final String p_language, final Class<?> p_class, final String p_label, final Object... p_parameter )
    {
        try
        {
            final String l_string = CConfiguration.getInstance().getResourceBundle( p_language ).getString( getResourceStringLabel( p_class, p_label ) );
            return MessageFormat.format( l_string, p_parameter );
        }
        catch ( final MissingResourceException l_exception )
        {
            CLogger.error( l_exception );
        }

        return "";
    }


    /**
     * converts any collection type into a typed array
     *
     * @param p_class      class array
     * @param p_collection collection
     * @return typed array
     * @tparam T collection / array type
     */
    public static <T> T[] CollectionToArray( final Class<T[]> p_class, final Collection<T> p_collection )
    {
        T[] l_return = p_class.cast( Array.newInstance( p_class.getComponentType(), p_collection.size() ) );
        p_collection.toArray( l_return );
        return l_return;
    }


    /**
     * adds a file extension if necessary
     *
     * @param p_file   file object
     * @param p_suffix suffix
     * @return file with extension
     */
    public static File addFileExtension( final File p_file, final String p_suffix )
    {
        return ( p_file.getAbsolutePath().endsWith( p_suffix ) ) ? p_file : new File( p_file + p_suffix );
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
        catch ( final Exception l_exception )
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
        catch ( final Exception l_exception )
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
    private static String getBytes2Hex( final byte[] p_bytes )
    {
        StringBuilder l_str = new StringBuilder( 2 * p_bytes.length );
        for ( byte l_byte : p_bytes )
            l_str.append( String.format( "%02x", l_byte & 0xff ) );

        return l_str.toString();
    }

}
