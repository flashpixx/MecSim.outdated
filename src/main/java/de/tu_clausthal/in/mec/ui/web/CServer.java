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

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


/**
 * class for the HTTP server *
 */
public class CServer extends NanoHTTPD
{

    /**
     * markdown processor *
     */
    protected final PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );
    /**
     * list with binded object *
     */
    protected final Map<Object, Map<String, CReflection.CMethod>> m_bind = new HashMap<>();
    /**
     * virtual-locations
     */
    protected final CVirtualLocation m_virtuallocation;
    /**
     * bind URI - used for referer *
     */
    protected String m_binduri;


    /**
     * ctor - starts the HTTP server
     *
     * @param p_host bind hostname
     * @param p_port bind port
     * @param p_default default location
     */
    public CServer( final String p_host, final int p_port, final CVirtualDirectory p_default )
    {
        super( p_host, p_port );
        m_binduri = p_port != 80 ? "http://" + p_host + ":" + p_port : "http://" + p_host;
        m_virtuallocation = new CVirtualLocation( p_default );

        try
        {
            this.start();
        }
        catch ( IOException l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    @Override
    public final Response serve( final IHTTPSession p_session )
    {
/*
        System.out.println(p_session.getUri());
        for( Map.Entry<String, String> x : p_session.getHeaders().entrySet() )
            System.out.println(x.getKey() + "        " + x.getValue() );
        System.out.println("----------------------------------------------");
*/

        final String l_uri;
        if ( p_session.getHeaders().containsKey( "referer" ) )
            l_uri = p_session.getHeaders().get( "referer" ).replace( m_binduri, "" ) + p_session.getUri();
        else
            l_uri = p_session.getUri();


        System.out.println( l_uri + "     " + p_session.getUri() + "          " + p_session.getHeaders().get("referer"));

        Response l_response;
        try
        {
            // mime-type will be read by the file-extension
            final IVirtualLocation l_location = m_virtuallocation.get( p_session.getUri() );
            final URL l_physicalfile = l_location.getFile( p_session.getUri() );
            final String l_mimetype = URLConnection.guessContentTypeFromName( l_physicalfile.getFile() );

            switch ( FilenameUtils.getExtension( l_physicalfile.toString() ) )
            {
                case "htm":
                case "html":
                    l_response = new Response( Response.Status.OK, l_mimetype + "; charset=" + ( new InputStreamReader( l_physicalfile.openStream() ).getEncoding() ), l_physicalfile.openStream() );
                    break;

                case "md":
                    if ( l_location.getMarkDownRenderer() != null )
                        l_response = new Response( Response.Status.OK, "application/xhtml+xml; charset=utf-8", l_location.getMarkDownRenderer().getHTML( m_markdown, l_physicalfile ) );
                    else
                        l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
                    break;

                default:
                    l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
            }

        }
        catch ( IOException l_exception )
        {
            CLogger.error( l_exception );
            l_response = new Response( Response.Status.INTERNAL_ERROR, "text/plain", "ERROR 500\n" + l_exception );
        }

        l_response.addHeader( "Location", p_session.getUri() );
        l_response.addHeader( "Expires", "-1" );
        return l_response;
    }


    /**
     * returns the virtual-location object
     *
     * @return location
     */
    public CVirtualLocation getVirtualLocation()
    {
        return m_virtuallocation;
    }


    /**
     * register an object for the UI
     *
     * param p_object object, all methods with the name "ui_" are registered
     *
     public void addActionObject( final Object p_object )
     {
     for ( Object l_item : m_bind.keySet() )
     if ( l_item.getClass().equals( p_object.getClass() ) )
     throw new IllegalArgumentException();

     final Map<String, CReflection.CMethod> l_methods = CReflection.getClassMethods( p_object.getClass(), new CReflection.IMethodFilter()
     {
     Override public boolean filter( final java.lang.reflect.Method p_method )
     {
     return p_method.getName().startsWith( "ui_" );
     }
     } );
     }
     */

}
