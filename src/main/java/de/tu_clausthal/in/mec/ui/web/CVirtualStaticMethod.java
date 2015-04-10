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

package de.tu_clausthal.in.mec.ui.web;


import com.github.drapostolos.typeparser.TypeParser;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;


/**
 * class to call object methods with an URL access
 */
public class CVirtualStaticMethod implements IVirtualLocation
{
    /**
     * URI reg expression for filter
     */
    private static final String c_uriallowchars = "[^a-zA-Z0-9_/]+";
    /**
     * mime type
     */
    private static final String c_mimetype = "application/json; charset=utf-8";
    /**
     * type parser
     */
    private static final TypeParser c_parser = TypeParser.newBuilder().build();
    /**
     * method handle *
     */
    private final MethodHandle m_method;
    /**
     * return-type of the method
     */
    private final Class<?> m_methodreturntype;
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
        m_uri = p_uri.startsWith( "/" ) ? p_uri.replaceAll( c_uriallowchars, "" ) : "/" + p_uri.replaceAll( c_uriallowchars, "" );
        m_method = p_method.getHandle();
        m_methodreturntype = p_method.getMethod().getReturnType();
        m_object = p_object;
        m_arguments = p_method.getMethod().getParameterCount();

        CLogger.info( p_uri );
    }

    @Override
    public final boolean match( final String p_uri )
    {
        return m_uri.equals( p_uri );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final NanoHTTPD.Response get( final NanoHTTPD.IHTTPSession p_session ) throws Throwable
    {
        // parse data - must be called otherwise an time-out exception is thrown
        if ( NanoHTTPD.Method.PUT.equals( p_session.getMethod() ) || NanoHTTPD.Method.POST.equals( p_session.getMethod() ) )
            p_session.parseBody( null );

        try
        {
            // invoke method
            final Object l_return;
            switch ( m_arguments )
            {
                case 0:
                    l_return = m_method.invoke( m_object );
                    break;
                case 1:
                    l_return = m_method.invoke( m_object, this.convertMap( p_session.getParms() ) );
                    break;
                case 2:
                    l_return = m_method.invoke( m_object, this.convertMap( p_session.getParms() ), p_session.getHeaders() );
                    break;
                default:
                    throw new IllegalArgumentException( CCommon.getResourceString( this, "argumentnumber", m_arguments ) );
            }

            return new NanoHTTPD.Response( NanoHTTPD.Response.Status.OK, c_mimetype, l_return == null ? "{}" : CCommon.toJson( l_return ) );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
            return new NanoHTTPD.Response( NanoHTTPD.Response.Status.INTERNAL_ERROR, c_mimetype, CCommon.toJson( CCommon.getMap( "error", l_exception.getMessage() ) ) );
        }
    }

    @Override
    public final CMarkdownRenderer getMarkDownRenderer()
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
            p_map.put( p_key[p_keyindex], CCommon.convertValue( p_value, new Class[]{Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class}, c_parser ) );
            return;
        }

        if ( !p_map.containsKey( p_key[p_keyindex] ) )
            p_map.put( p_key[p_keyindex], new HashMap<>() );

        this.splitKeyValues( p_key, p_keyindex + 1, p_value, (Map) p_map.get( p_key[p_keyindex] ) );
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
