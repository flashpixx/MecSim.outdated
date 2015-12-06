/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import fi.iki.elonen.IWebSocketFactory;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebSocket;
import fi.iki.elonen.WebSocketResponseHandler;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * class of the HTTP server *
 */
public final class CServer extends NanoHTTPD implements IWebSocketFactory
{
    /**
     * seperator
     */
    private static final String c_seperator = "/";
    /**
     * web-dynamic prefix
     */
    private static final String c_webdynamic = "web_dynamic_";
    /**
     * web-static prefix
     */
    private static final String c_webstatic = "web_static_";
    /**
     * web-uribase name
     */
    private static final String c_weburibase = "web_uribase";
    /**
     * markdown processor
     */
    private final PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );
    /**
     * mimetype detector
     */
    private final MimeUtil2 m_mimetype = new MimeUtil2();
    /**
     * virtual-locations
     */
    private final CVirtualLocation m_virtuallocation;
    /**
     * websocket handler
     */
    private final WebSocketResponseHandler m_websockethandler = new WebSocketResponseHandler( this );
    /**
     * websocket heartbeat
     */
    private final int m_websocketheartbeat;

    /**
     * ctor - starts the HTTP server
     *
     * @param p_host bind hostname
     * @param p_port bind port
     * @param p_default default location
     * @param p_websocketheartbeat websocket heartbeat
     */
    public CServer( final String p_host, final int p_port, final CVirtualDirectory p_default, final int p_websocketheartbeat )
    {
        super( p_host, p_port );
        CLogger.info( CCommon.getResourceString( this, "bind", p_host, p_port ) );

        m_websocketheartbeat = p_websocketheartbeat;
        m_virtuallocation = new CVirtualLocation( p_default );
        for ( final String l_detector : new String[]{
                "eu.medsea.mimeutil.detector.MagicMimeMimeDetector", "eu.medsea.mimeutil.detector.ExtensionMimeDetector",
                "eu.medsea.mimeutil.detector.TextMimeDetector"
        } )
            m_mimetype.registerMimeDetector( l_detector );


        // create own thread-pool and start server
        this.setAsyncRunner( new CWorkerPool() );
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

    /**
     * returns the virtual-location object
     *
     * @return location
     */
    public final CVirtualLocation getVirtualLocation()
    {
        return m_virtuallocation;
    }

    @Override
    public final WebSocket openWebSocket( final IHTTPSession p_session )
    {
        try
        {
            return m_virtuallocation.get( p_session ).<WebSocket>get( p_session );
        }
        catch ( final Throwable l_throwable )
        {
        }

        return null;
    }

    /**
     * registerObject an object for the UI
     *
     * @param p_object object, all methods with the name "ui_" are registered
     * @note a class / object need to implemente methods with the prefix "web_static_" or "web_dynamic_", which is bind
     * to the server with the URL part "/classname/[basepart/]methodname" (without prefix). The methods "String
     * web_uribase"
     * returns the basepart of the URI
     */
    public final void registerObject( final Object p_object )
    {
        // URI begin
        final String l_uriclass = c_seperator + p_object.getClass().getSimpleName().toLowerCase() + c_seperator;

        // get methods
        for ( final Map.Entry<String, CReflection.CMethod> l_method : CReflection.getClassMethods(
                p_object.getClass(), new CReflection.IMethodFilter()
                {
                    @Override
                    public boolean filter( final java.lang.reflect.Method p_method )
                    {
                        return ( p_method.getName().toLowerCase().startsWith(
                                c_webstatic
                        )        || p_method.getName().toLowerCase().startsWith(
                                c_webdynamic
                        ) )    && ( !Modifier.isStatic( p_method.getModifiers() ) );
                    }
                }
        ).entrySet() )
        {
            this.bindStaticMethod( p_object, l_method.getValue(), l_uriclass );
            this.bindDynamicMethod( p_object, l_method.getValue(), l_uriclass );
        }
    }

    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source source file object
     * @param p_index index file
     * @param p_uri URI
     */
    public final void registerVirtualDirectory( final File p_source, final String p_index, final String p_uri )
    {
        this.registerVirtualDirectory( p_source, p_index, p_uri, null );
    }

    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source source file object
     * @param p_index index file
     * @param p_uri URI
     * @param p_markdown markdown renderer
     */
    public final void registerVirtualDirectory( final File p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
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

    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_index index file
     * @param p_uri URI
     */
    public final void registerVirtualDirectory( final String p_source, final String p_index, final String p_uri )
    {
        this.registerVirtualDirectory( p_source, p_index, p_uri, null );
    }

    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_index index file
     * @param p_uri URI
     * @param p_markdown markdown renderer
     */
    public final void registerVirtualDirectory( final String p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        this.registerVirtualDirectory( new File( p_source ), p_index, p_uri, p_markdown );
    }

    /**
     * adds a new virtual file to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_uri URI
     */
    public final void registerVirtualFile( final String p_source, final String p_uri )
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

    @Override
    public final Response serve( final IHTTPSession p_session )
    {
        // try to get the websocket first - try-catch avoid NPE because openWebSocket can create the exception
        try
        {
            final Response l_response = m_websockethandler.serve( p_session );
            if ( l_response != null )
                return l_response;
        }
        catch ( final NullPointerException l_exception )
        {
        }


        // no websocket
        try
        {
            // get location - and check if it is a websocket
            final IVirtualLocation l_location = m_virtuallocation.get( p_session );

            if ( l_location instanceof CVirtualDynamicMethod )
                throw new Exception();
            if ( l_location instanceof CVirtualStaticMethod )
                return this.setDefaultHeader( this.getVirtualStaticMethod( l_location, p_session ), p_session );

            return this.setDefaultHeader( this.getVirtualDirFile( l_location, p_session ), p_session );

        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
            return this.setDefaultHeader( new Response( Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "ERROR 500\n" + l_throwable ), p_session );
        }
    }

    /**
     * converts a Java string to the URL specification
     *
     * @param p_raw Java raw string
     * @return modfied string
     *
     * @warning only lower-case chars, number and underscore are allowed
     */
    private static String toURLFormat( final String p_raw )
    {
        return p_raw.toLowerCase().replaceAll( "[^a-z0-9_]", "" );
    }

    /**
     * dynamic method binding
     *
     * @param p_object bind object
     * @param p_method method object
     * @param p_uriclass class name
     */
    private void bindDynamicMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uriclass )
    {
        String l_methodname = p_method.getMethod().getName().toLowerCase();
        if ( !l_methodname.contains( c_webdynamic ) )
            return;
        l_methodname = l_methodname.replace( c_webdynamic, "" );
        if ( l_methodname.isEmpty() )
            return;

        m_virtuallocation.add(
                new CVirtualDynamicMethod(
                        m_websocketheartbeat, p_object, p_method, p_uriclass + this.getURIBase( p_object ) + toURLFormat(
                        l_methodname
                )
                )
        );
    }

    /**
     * static method binding
     *
     * @param p_object bind object
     * @param p_method method object
     * @param p_uriclass class name
     */
    private void bindStaticMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uriclass )
    {
        String l_methodname = p_method.getMethod().getName().toLowerCase();
        if ( !l_methodname.contains( c_webstatic ) )
            return;
        l_methodname = l_methodname.replace( c_webstatic, "" );
        if ( l_methodname.isEmpty() )
            return;

        m_virtuallocation.add( new CVirtualStaticMethod( p_object, p_method, p_uriclass + this.getURIBase( p_object ) + toURLFormat( l_methodname ) ) );
    }

    /**
     * reads the mime-type of an URL - first try to detect a mime-type which has no "application" prefix
     */
    @SuppressWarnings( "unchecked" )
    private String getMimeType( final URL p_url )
    {
        final Collection l_types = m_mimetype.getMimeTypes( p_url );
        if ( l_types.size() == 1 )
            return l_types.iterator().next().toString();

        for ( final Object l_item : l_types )
        {
            final MimeType l_type = (MimeType) l_item;
            if ( !l_type.toString().startsWith( "application/" ) )
                return l_type.toString();
        }
        return MimeUtil2.UNKNOWN_MIME_TYPE.toString();
    }

    /**
     * returns URI base if exists
     *
     * @param p_object object
     * @return empty string or URI
     */
    private String getURIBase( final Object p_object )
    {
        // try to read basepart
        String l_uribase = "";
        try
        {
            final Object l_data = CReflection.getClassMethod( p_object.getClass(), c_weburibase ).getMethod().invoke( p_object );
            if ( l_data instanceof String )
                l_uribase = toURLFormat( l_data.toString() );

            if ( ( l_uribase != null ) && ( !l_uribase.isEmpty() ) )
            {
                if ( l_uribase.startsWith( c_seperator ) )
                    l_uribase = l_uribase.substring( 1 );
                if ( !l_uribase.endsWith( c_seperator ) )
                    l_uribase += c_seperator;
            }
        }
        catch ( final IllegalArgumentException | IllegalAccessException | InvocationTargetException l_exception )
        {
        }

        return l_uribase;
    }

    /**
     * generates HTTP response of file & directory calls
     *
     * @param p_location location object
     * @param p_session session object
     * @return response
     *
     * @throws Throwable on error
     */
    private Response getVirtualDirFile( final IVirtualLocation p_location, final IHTTPSession p_session ) throws Throwable
    {
        final Response l_response;

        final URL l_physicalfile = p_location.<URL>get( p_session );
        final String l_mimetype = this.getMimeType( l_physicalfile );
        CLogger.info( p_session.getUri() + "   " + l_physicalfile + "   " + l_mimetype );

        switch ( FilenameUtils.getExtension( l_physicalfile.toString() ) )
        {
            case "htm":
            case "html":
                l_response = new Response(
                        Response.Status.OK, l_mimetype + "; charset=" + ( new InputStreamReader(
                        l_physicalfile.openStream()
                ).getEncoding() ), l_physicalfile.openStream()
                );
                break;

            case "md":
                if ( p_location.getMarkDownRenderer() != null )
                    l_response = new Response(
                            Response.Status.OK, p_location.getMarkDownRenderer().getMimeType(), p_location.getMarkDownRenderer().getHTML(
                            m_markdown, l_physicalfile
                    )
                    );
                else
                    l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
                break;

            default:
                l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
        }

        return l_response;
    }

    /**
     * generates HTTP response of a static method calls
     *
     * @param p_location location object
     * @param p_session session object
     * @return response
     *
     * @throws Throwable on error
     */
    private Response getVirtualStaticMethod( final IVirtualLocation p_location, final IHTTPSession p_session ) throws Throwable
    {
        CLogger.info( p_session.getUri() );
        return p_location.<Response>get( p_session );
    }

    /**
     * set default header items
     *
     * @param p_response response
     * @param p_session session
     * @return modified response
     */
    private Response setDefaultHeader( final Response p_response, final IHTTPSession p_session )
    {
        p_response.addHeader( "Location", p_session.getUri() );
        p_response.addHeader( "Expires", "-1" );

        // is needed for ajax request
        // @see http://stackoverflow.com/questions/10548883/request-header-field-authorization-is-not-allowed-error-tastypie
        p_response.addHeader( "Access-Control-Allow-Origin", "*" );
        p_response.addHeader( "Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT" );
        p_response.addHeader( "Access-Control-Allow-Headers", "Origin,Content-Type,Accept" );

        return p_response;
    }


    /**
     * runner with performance optimizing,
     * this runner uses a thread-stealing pool
     * to handle request
     */
    private static final class CWorkerPool implements AsyncRunner
    {
        /**
         * thread-pool for handling webserver request
         */
        private final ExecutorService m_pool = Executors.newCachedThreadPool();

        @Override
        public void closeAll()
        {
            m_pool.shutdownNow();
        }

        @Override
        public void closed( final ClientHandler p_clientHandler )
        {
        }

        @Override
        public void exec( final ClientHandler p_clientHandler )
        {
            m_pool.submit( p_clientHandler );
        }
    }
}
