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


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;

import java.io.File;


/**
 * main workspace of the web-menu structure
 */
public class CWorkspace extends CBrowser
{
    /**
     * HTTP server to handle websockets *
     */
    protected final CServer m_server = new CServer( "localhost", CConfiguration.getInstance().get().getUibindport(), new CVirtualDirectory( CCommon.getResource( "web/root" ), "index.htm" ) );


    /**
     * ctor - adds the browser *
     */
    public CWorkspace()
    {
        super( EMenu.Full );

        try
        {
            this.addVirtualFile( "web/documentation/user/layout.css", "/userdoc/layout.css" );
            this.addVirtualDirectory( "web/documentation/user/" + CConfiguration.getInstance().get().getLanguage(), "index.md", "/userdoc/", new CMarkdownRenderer( "layout.css" ) );

            this.addVirtualDirectory( CConfiguration.getInstance().getLocation( "www" ), "index.htm", "/local/", null );

            this.addVirtualDirectory( "web/documentation/developer", "index.htm", "/develdoc/", null );
        }
        catch ( IllegalArgumentException l_exception )
        {
            CLogger.error( l_exception );
        }

        this.load( "http://localhost:" + CConfiguration.getInstance().get().getUibindport() );
    }


    /**
     * adds a new virtual file to the server
     *
     * @param p_source relative source path
     * @param p_uri    URI
     */
    protected final void addVirtualFile( final String p_source, final String p_uri )
    {
        final File l_file = CCommon.getResource( p_source );
        if ( l_file != null )
            m_server.getVirtualLocation().getLocations().add( new CVirtualFile( l_file, p_uri ) );
    }


    /**
     * adds a new virtual directory to the server
     *
     * @param p_source   relative source path
     * @param p_index    index file
     * @param p_uri      URI
     * @param p_markdown markdown renderer
     */
    protected void addVirtualDirectory( final String p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        this.addVirtualDirectory( CCommon.getResource( p_source ), p_index, p_uri, p_markdown );
    }


    /**
     * adds a new virtual directory to the server
     *
     * @param p_source   source file object
     * @param p_index    index file
     * @param p_uri      URI
     * @param p_markdown markdown renderer
     */
    protected void addVirtualDirectory( final File p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        if ( p_source != null )
            m_server.getVirtualLocation().getLocations().add( new CVirtualDirectory( p_source, p_index, p_uri, p_markdown ) );
    }

}
