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
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


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


    /** ctor - adds the browser **/
    public CWebUI()
    {
        this.add( m_browser );
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
        protected File getRootFile( final String p_uri )
        {
            try
            {
                return new File( this.getClass().getClassLoader().getResource( "ui" + ( p_uri.startsWith( "/" ) ? p_uri : File.separator + p_uri ) ).getPath() );
            }
            catch ( NullPointerException l_exception )
            {

            }
            return null;
        }


        @Override
        public Response serve( IHTTPSession p_session )
        {
            try
            {
                final File l_file = p_session.getUri().equals( "/" ) ? this.getRootFile( "index.htm" ) : this.getRootFile( p_session.getUri() );
                if ( l_file != null )
                    return new Response( FileUtils.readFileToString(l_file) );
            }
            catch ( IOException l_exception )
            {
                CLogger.error( l_exception );
            }

            return super.serve( p_session );
        }
    }


}
