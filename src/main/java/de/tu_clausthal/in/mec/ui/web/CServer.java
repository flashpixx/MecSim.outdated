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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.MimeUtil2;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;


/**
 * class of the HTTP server *
 * @todo add http://www.html5rocks.com/de/tutorials/websockets/basics/
 */
public class CServer extends NanoHTTPD
{

    /**
     * markdown processor *
     */
    protected final PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );
    /**
     * virtual-locations
     */
    protected final CVirtualLocation m_virtuallocation;
    /**
     * Json module
     */
    protected final Gson m_json = new GsonBuilder().create();
    /**
     * Json type mapping
     */
    protected final Type m_jsontype = new TypeToken<Map<Object, Object>>()
    {
    }.getType();


    /**
     * ctor - starts the HTTP server
     *
     * @param p_host    bind hostname
     * @param p_port    bind port
     * @param p_default default location
     * @todo application/x-javascript extension mimetype
     */
    public CServer( final String p_host, final int p_port, final CVirtualDirectory p_default )
    {
        super( p_host, p_port );
        m_virtuallocation = new CVirtualLocation( p_default );

        for ( String l_detector : new String[]{"eu.medsea.mimeutil.detector.MagicMimeMimeDetector", "eu.medsea.mimeutil.detector.ExtensionMimeDetector", "eu.medsea.mimeutil.detector.OpendesktopMimeDetector", "eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector"} )
            MimeUtil.registerMimeDetector( l_detector );

        try
        {
            this.start();
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }

        CBootstrap.afterServerInit( this );
    }


    @Override
    public final Response serve( final IHTTPSession p_session )
    {
        Response l_response;
        try
        {
            // get location
            final IVirtualLocation l_location = m_virtuallocation.get( p_session );

            if ( l_location instanceof CVirtualMethod )
                l_response = this.getVirtualMethod( l_location, p_session );
            else
                l_response = this.getVirtualDirFile( l_location, p_session );

        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
            l_response = new Response( Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "ERROR 500\n" + l_throwable );
        }

        l_response.addHeader( "Location", p_session.getUri() );
        l_response.addHeader( "Expires", "-1" );

        // is needed for ajax request
        // @see http://stackoverflow.com/questions/10548883/request-header-field-authorization-is-not-allowed-error-tastypie
        l_response.addHeader( "Access-Control-Allow-Origin", "*" );
        l_response.addHeader( "Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT" );
        //l_response.addHeader( "Access-Control-Allow-Headers", "Origin,Content-Type,Accept,Authorization" );

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
     * reads the mime-type of an URL - first try to detect a mime-type which has no "application" prefix
     */
    private String getMimeType( final URL p_url )
    {
        for ( Object l_item : MimeUtil.getMimeTypes( p_url ) )
        {
            final MimeType l_type = (MimeType) l_item;
            if ( !l_type.toString().startsWith( "application/" ) )
                return l_type.toString();
        }

        return MimeUtil2.UNKNOWN_MIME_TYPE.toString();
    }


    /**
     * register an object for the UI
     *
     * param p_object object, all methods with the name "ui_" are registered
     *
     * @bug incomplete
     */
    public void register( final Object p_object )
    {
        for ( Map.Entry<String, CReflection.CMethod> l_method : CReflection.getClassMethods( p_object.getClass(), new CReflection.IMethodFilter()
        {
            @Override
            public boolean filter( final java.lang.reflect.Method p_method )
            {
                return ( p_method.getName().toLowerCase().startsWith( "web_static_" ) || p_method.getName().toLowerCase().startsWith( "web_dynamic_" ) ) && ( !Modifier.isStatic( p_method.getModifiers() ) );
            }
        } ).entrySet() )
        {
            final String l_methodname = l_method.getValue().getMethod().getName().toLowerCase().replace( "web_static_", "" );
            if ( !l_methodname.isEmpty() )
                m_virtuallocation.add( new CVirtualMethod( p_object, l_method.getValue(), "/" + p_object.getClass().getSimpleName().toLowerCase() + "/" + l_methodname ) );
        }

    }


    /**
     * generates HTTP response of method calls
     *
     * @param p_location location object
     * @param p_session  session object
     * @return response
     * @throws Throwable on error
     */
    protected final Response getVirtualMethod( final IVirtualLocation p_location, final IHTTPSession p_session ) throws Throwable
    {
        CLogger.info( p_session.getUri() );

        // http://stackoverflow.com/questions/14944419/gson-to-hashmap
        // http://stackoverflow.com/questions/2779251/how-can-i-convert-json-to-a-hashmap-using-gson
        return new Response( Response.Status.OK, "application/json; charset=utf-8", m_json.toJson( p_location.get( p_session ), m_jsontype ) );
    }


    /**
     * generates HTTP response of file & directory calls
     *
     * @param p_location location object
     * @param p_session  session object
     * @return response
     * @throws Throwable on error
     */
    protected final Response getVirtualDirFile( final IVirtualLocation p_location, final IHTTPSession p_session ) throws Throwable
    {
        final Response l_response;
        final URL l_physicalfile = p_location.get( p_session );
        final String l_mimetype = this.getMimeType( l_physicalfile );
        CLogger.info( p_session.getUri() + "   " + l_physicalfile + "   " + l_mimetype );

        switch ( FilenameUtils.getExtension( l_physicalfile.toString() ) )
        {
            case "htm":
            case "html":
                l_response = new Response( Response.Status.OK, l_mimetype + "; charset=" + ( new InputStreamReader( l_physicalfile.openStream() ).getEncoding() ), l_physicalfile.openStream() );
                break;

            case "md":
                if ( p_location.getMarkDownRenderer() != null )
                    l_response = new Response( Response.Status.OK, "text/html; charset=utf-8", p_location.getMarkDownRenderer().getHTML( m_markdown, l_physicalfile ) );
                else
                    l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
                break;

            default:
                l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
        }

        return l_response;
    }


    /**
     * adds a new virtual file to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_uri    URI
     */
    public final void addVirtualFile( final String p_source, final String p_uri )
    {
        try
        {
            final URL l_url = CCommon.getResourceURL( p_source );
            if ( l_url != null )
                m_virtuallocation.add( new CVirtualFile( l_url, p_uri ) );
        }
        catch ( final IllegalArgumentException l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_index  index file
     * @param p_uri    URI
     */
    public void addVirtualDirectory( final String p_source, final String p_index, final String p_uri )
    {
        this.addVirtualDirectory( p_source, p_index, p_uri, null );
    }


    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source   relative source path
     * @param p_index    index file
     * @param p_uri      URI
     * @param p_markdown markdown renderer
     */
    public void addVirtualDirectory( final String p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        this.addVirtualDirectory( new File( p_source ), p_index, p_uri, p_markdown );
    }


    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source source file object
     * @param p_index  index file
     * @param p_uri    URI
     */
    public void addVirtualDirectory( final File p_source, final String p_index, final String p_uri )
    {
        this.addVirtualDirectory( p_source, p_index, p_uri, null );
    }


    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source   source file object
     * @param p_index    index file
     * @param p_uri      URI
     * @param p_markdown markdown renderer
     */
    public void addVirtualDirectory( final File p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        try
        {
            if ( p_source != null )
                m_virtuallocation.add( new CVirtualDirectory( CCommon.getResourceURL( p_source ), p_index, p_uri, p_markdown ) );
        }
        catch ( final IllegalArgumentException l_exception )
        {
            CLogger.error( l_exception );
        }
    }
}
