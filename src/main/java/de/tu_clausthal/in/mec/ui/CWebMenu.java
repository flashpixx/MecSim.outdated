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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.WikiLinkNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * server class of the UI
 */
public class CWebMenu extends CBrowser
{
    /**
     * HTTP server to handle websockets *
     */
    protected CServer m_server = new CServer();
    /**
     * list with binded object *
     */
    protected Map<Object, Map<String, CReflection.CMethod>> m_bind = new HashMap<>();


    /** ctor - adds the browser **/
    public CWebMenu()
    {
        super();
        this.load( "http://localhost:" + CConfiguration.getInstance().get().getUibindport() );
    }


    /**
     * register an object for the UI
     *
     * @param p_object object, all methods with the name "ui_" are registered
     */
    public void addActionObject( final Object p_object )
    {
        for ( Object l_item : m_bind.keySet() )
            if ( l_item.getClass().equals( p_object.getClass() ) )
                throw new IllegalArgumentException();

        final Map<String, CReflection.CMethod> l_methods = CReflection.getClassMethods( p_object.getClass(), new CReflection.IMethodFilter()
        {
            @Override
            public boolean filter( final Method p_method )
            {
                return p_method.getName().startsWith( "ui_" );
            }
        } );



    }



    /** private class for the HTTP server **/
    protected class CServer extends NanoHTTPD
    {

        /**
         * markdown processor *
         */
        protected final PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );
        /**
         * link renderer
         */
        protected final LinkRenderer m_renderer = new CMarkdownLinkRenderer();


        /** ctor - starts the HTTP server
         * @note webservice is bind only to "localhost" with the configuration port
         */
        public CServer()
        {
            super( "localhost", CConfiguration.getInstance().get().getUibindport() );
            try
            {
                this.start();
            }
            catch ( IOException l_exception )
            {
                CLogger.error( l_exception );
            }
        }

        /**
         * gets a file from the root directory
         *
         * @param p_uri URI
         * @return URL or null
         */
        protected final URL getRootFile( final String p_uri )
        {
            try
            {
                return this.getClass().getClassLoader().getResource( "web" + ( p_uri.startsWith( "/" ) ? p_uri : File.separator + p_uri ) );
            }
            catch ( NullPointerException l_exception )
            {
                CLogger.error( l_exception );
            }
            return null;
        }


        @Override
        public final Response serve( final IHTTPSession p_session )
        {
            // check default document
            final URL l_url = p_session.getUri().equals( "/" ) ? this.getRootFile( "index.htm" ) : this.getRootFile( p_session.getUri() );

            if ( l_url != null )
                try
                {
                    // if the file a markdown file, the renderer is called
                    if ( l_url.getPath().endsWith( ".md" ) )
                        return new Response( Response.Status.OK, "application/xhtml+xml", getHTMLfromMarkdown( l_url, null ) );

                    // mime-type is read by the file extension
                    return new Response( Response.Status.OK, URLConnection.guessContentTypeFromName( l_url.getFile() ), l_url.openStream() );
                }
                catch ( IOException l_exception )
                {
                    CLogger.error( l_exception );
                    return new Response( Response.Status.INTERNAL_ERROR, "text/plain", "ERROR 500\n" + l_exception );
                }


            return new Response( Response.Status.NOT_FOUND, "text/plain", "ERROR 404\nfile [ " + p_session.getUri() + " ] not found" );
        }


        /**
         * creates a full HTML document
         *
         * @param p_input HTML snipplet
         * @return full HTML document
         */
        protected final String getHTMLfromMarkdown( final URL p_input, final URL p_css ) throws IOException
        {
            return "<?xml version=\"1.0\" ?>" +
                    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                    ( p_css == null ? "" : "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"" + p_css.toString() + "\"></head>" ) +
                    "<body>" +
                    m_markdown.markdownToHtml( IOUtils.toString( p_input ).toCharArray(), m_renderer ) +
                    "</body></html>";
        }
    }


    /**
     * link renderer to redefine link names *
     */
    protected class CMarkdownLinkRenderer extends LinkRenderer
    {

        @Override
        public final Rendering render( final ExpImageNode p_node, final String p_text )
        {
            try
            {
                new URL( p_node.url );
            }
            catch ( MalformedURLException l_exception )
            {
                return super.render( new ExpImageNode( p_node.title, m_server.getRootFile( p_node.url ).toString(), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), p_text );
            }
            return super.render( p_node, p_text );
        }

        @Override
        public final Rendering render( final WikiLinkNode p_node )
        {
            try
            {
                // split node text into this definition
                // [[item]] -> item will be searched at Wikipedia in the current language
                // [[search|item]] first part will be search at Wikipedia and second will be shown within the help
                // [[language|search|item]] first part is the language at Wikipedia, search the searched item and item will be sown

                final String[] l_parts = StringUtils.split( p_node.getText(), "|" );
                if ( l_parts.length == 1 )
                    return super.render( new ExpLinkNode( l_parts[0], "http://" + this.getLanguage() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[0].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[0] );
                if ( l_parts.length == 2 )
                    return super.render( new ExpLinkNode( l_parts[1], "http://" + this.getLanguage() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[0].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[1] );
                if ( l_parts.length == 3 )
                    return super.render( new ExpLinkNode( l_parts[2], "http://" + l_parts[0].trim() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[1].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[2] );

            }
            catch ( Exception l_exception )
            {
            }

            return super.render( p_node );
        }


        /**
         * returns the language code
         *
         * @return language code
         */
        protected final String getLanguage()
        {
            return CConfiguration.getInstance().get().getLanguage() == null ? "en" : CConfiguration.getInstance().get().getLanguage();
        }
    }

}
