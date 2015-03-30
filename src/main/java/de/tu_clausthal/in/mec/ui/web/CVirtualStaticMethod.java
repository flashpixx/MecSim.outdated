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
 **/

package de.tu_clausthal.in.mec.ui.web;


import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * class to call object methods with an URL access
 */
public class CVirtualStaticMethod implements IVirtualLocation
{

    /**
     * Json module
     */
    protected static final Gson s_json = new GsonBuilder().create();
    /**
     * Json object type mapping
     */
    protected static final Type s_jsonobjecttype = new TypeToken<Map<Object, Object>>()
    {
    }.getType();
    /**
     * type parser
     */
    protected static final TypeParser s_parser = TypeParser.newBuilder().build();
    /**
     * method handle *
     */
    private final MethodHandle m_method;
    /**
     * object
     */
    private final Object m_object;
    /**
     * number of method arguments
     */
    private final int m_arguments;
    /**
     * URI *
     */
    private final String m_uri;


    /**
     * ctor
     *
     * @param p_object object
     * @param p_method method
     * @param p_uri    calling URI
     */
    public CVirtualStaticMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uri )
    {
        m_uri = p_uri.startsWith( "/" ) ? p_uri.replaceAll( "[^a-zA-Z0-9_/]+", "" ) : "/" + p_uri.replaceAll( "[^a-zA-Z0-9_/]+", "" );
        m_method = p_method.getHandle();
        m_object = p_object;
        m_arguments = p_method.getMethod().getParameterCount();

        CLogger.info( p_uri );
    }

    @Override
    public boolean match( final String p_uri )
    {
        return m_uri.equals( p_uri );
    }

    @Override
    @SuppressWarnings("unchecked")
    public String get( final NanoHTTPD.IHTTPSession p_session ) throws Throwable
    {
        // parse data - must be called otherwise an time-out exception is thrown
        if ( NanoHTTPD.Method.PUT.equals( p_session.getMethod() ) || NanoHTTPD.Method.POST.equals( p_session.getMethod() ) )
            p_session.parseBody( null );

        Map<Object, Object> l_return;
        try
        {
            // invoke method
            final Object l_returnvalue;
            switch ( m_arguments )
            {
                case 0:
                    l_returnvalue = m_method.invoke( m_object );
                    break;
                case 1:
                    l_returnvalue = m_method.invoke( m_object, this.convertMap( p_session.getParms() ) );
                    break;
                case 2:
                    l_returnvalue = m_method.invoke( m_object, this.convertMap( p_session.getParms() ), p_session.getHeaders() );
                    break;
                default:
                    throw new IllegalArgumentException( CCommon.getResourceString( this, "argumentnumber", m_arguments ) );
            }

            l_return = ( l_returnvalue instanceof Map ) ? (Map) l_returnvalue : new HashMap<Object, Object>()
            {{
                    put( "data", l_returnvalue );
                }};
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
            l_return = new HashMap<Object, Object>()
            {{
                    put( "error", l_throwable.getMessage() );
                }};
        }

        // http://stackoverflow.com/questions/14944419/gson-to-hashmap
        // http://stackoverflow.com/questions/2779251/how-can-i-convert-json-to-a-hashmap-using-gson
        return s_json.toJson( l_return, s_jsonobjecttype );
    }

    @Override
    public CMarkdownRenderer getMarkDownRenderer()
    {
        return null;
    }

    /**
     * converts the string-string map into a string-object map to create map-in-map structure
     *
     * @param p_input input map
     * @return string-object map
     */
    private Map<String, Object> convertMap( final Map<String, String> p_input )
    {
        final Map<String, Object> l_return = new HashMap<>();
        for ( Map.Entry<String, String> l_item : p_input.entrySet() )
            this.splitKeyValues( l_item.getKey().replace( "]", "" ).split( "\\[" ), 0, l_item.getValue(), l_return );
        return l_return;
    }

    /**
     * split the map into value- and map-types
     *
     * @param p_key      array with key (separater is "[")
     * @param p_keyindex current index of the key
     * @param p_value    value
     * @param p_map      return map
     */
    private void splitKeyValues( final String[] p_key, final int p_keyindex, final String p_value, final Map<String, Object> p_map )
    {
        if ( p_key.length == p_keyindex + 1 )
        {

            p_map.put( p_key[p_keyindex], this.convertValue( p_value, new Class[]{Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class} ) );
            return;
        }

        if ( !p_map.containsKey( p_key[p_keyindex] ) )
            p_map.put( p_key[p_keyindex], new HashMap<>() );

        this.splitKeyValues( p_key, p_keyindex + 1, p_value, (Map) p_map.get( p_key[p_keyindex] ) );
    }


    /**
     * converts a value into class
     *
     * @param p_value string input class
     * @param p_types type classes
     * @return converted type
     */
    private Object convertValue( final String p_value, final Class[] p_types )
    {
        for ( Class l_class : p_types )
            try
            {
                return s_parser.parseType( p_value, l_class );
            }
            catch ( final TypeParserException l_exception )
            {
            }

        return p_value;
    }


    @Override
    public final int hashCode()
    {
        return m_uri.hashCode();
    }


    @Override
    public final boolean equals( final Object p_object )
    {
        if ( p_object instanceof CVirtualLocation ) return this.hashCode() == p_object.hashCode();

        return false;
    }

}
