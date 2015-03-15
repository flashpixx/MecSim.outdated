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

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


/**
 * server class of the UI
 */
public class CWebUI extends JPanel
{
    /**
     * HTTP server to handle websockets *
     */
    protected CServer m_server = new CServer();
    /** browser component for viewing **/
    protected CBrowser m_browser = new CBrowser( "http://localhost:" + CConfiguration.getInstance().get().getUibindport() );
    /**
     * list with binded object *
     */
    protected Map<Object, Map<String, CReflection.CMethod>> m_bind = new HashMap<>();


    /** ctor - adds the browser **/
    public CWebUI()
    {
        this.add( m_browser );
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
    private class CServer extends NanoHTTPD
    {


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
         * @return file or null
         */
        protected final File getRootFile( final String p_uri )
        {
            try
            {
                return new File( this.getClass().getClassLoader().getResource( "ui" + ( p_uri.startsWith( "/" ) ? p_uri : File.separator + p_uri ) ).getPath() );
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
            final File l_file = p_session.getUri().equals( "/" ) ? this.getRootFile( "index.htm" ) : this.getRootFile( p_session.getUri() );
            if ( l_file != null )
                try (
                        // file data is read by an input stream
                        FileInputStream l_stream = new FileInputStream( l_file );
                )
                {
                    // mime-type is read by the file extension
                    return new Response( Response.Status.OK, URLConnection.guessContentTypeFromName( l_file.getName() ), new FileInputStream( l_file ) );
                }
                catch ( IOException l_exception )
                {
                    CLogger.error( l_exception );
                    return new Response( Response.Status.INTERNAL_ERROR, "text/plain", "ERROR 500\n" + l_exception );
                }


            return new Response( Response.Status.NOT_FOUND, "text/plain", "ERROR 404\nfile [ " + p_session.getUri() + " ] not found" );
        }
    }

}
