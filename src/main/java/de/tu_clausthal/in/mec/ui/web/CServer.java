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
import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.net.MalformedURLException;
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
     * link renderer
     */
    protected final LinkRenderer m_renderer = new CMarkdownLinkRenderer();
    /**
     * list with binded object *
     */
    protected final Map<Object, Map<String, CReflection.CMethod>> m_bind = new HashMap<>();
    /**
     * virtual-hosts
     */
    protected CVirtualHost m_vhost = null;


    /**
     * ctor - starts the HTTP server
     *
     * @note webservice is bind only to "localhost" with the configuration port
     */
    public CServer( final String p_host, final int p_port, final CVirtualHost.CName p_default )
    {
        super( p_host, p_port );
        m_vhost = new CVirtualHost( p_default );

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
        try
        {
            // mime-type will be read of the file-extension
            final CVirtualHost.CName l_host = m_vhost.get( p_session.getUri() );
            final URL l_url = this.getFileURL( l_host, p_session.getUri() );
            final String l_mimetype = URLConnection.guessContentTypeFromName( l_url.getFile() );

            // create response for markdown file
            if ( l_url.getPath().endsWith( ".md" ) )
                return new Response( Response.Status.OK, "application/xhtml+xml", getHTMLfromMarkdown( l_host, p_session.getUri() ) );

            // repsonse otherwise
            return new Response( Response.Status.OK, l_mimetype, l_url.openStream() );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
            return new Response( Response.Status.INTERNAL_ERROR, "text/plain", "ERROR 500\n" + l_exception );
        }
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

    /**
     * creates a full HTML document from a markdown document
     *
     * @param p_host vhost configuration
     * @param p_uri  URI part
     * @return full HTML document
     */
    protected final String getHTMLfromMarkdown( final CVirtualHost.CName p_host, final String p_uri ) throws IOException
    {
        return "<?xml version=\"1.0\" ?>" +
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                //( p_host.hasCSS() ? "" : "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"" + p_host.getCSS() + "\"></head>" ) +
                "<body>" +
                m_markdown.markdownToHtml( IOUtils.toString( getFileURL( p_host, p_uri ) ).toCharArray(), m_renderer ) +
                "</body></html>";
    }


    /**
     * returns the URL relative to the resource-root directory
     *
     * @param p_host name-based virtual host
     * @param p_uri  input URL
     * @return full URL to the file
     */
    protected URL getFileURL( final CVirtualHost.CName p_host, final String p_uri ) throws MalformedURLException
    {
        final String[] l_parts = p_uri.split( "/" );
        if ( ( l_parts.length == 0 ) || ( !l_parts[l_parts.length - 1].contains( "." ) ) )
            return this.getClass().getClassLoader().getResource( "web/" + p_host.getRoot() );

        return this.getClass().getClassLoader().getResource( "web/" + p_host.getFile( p_uri ) );
    }

}
