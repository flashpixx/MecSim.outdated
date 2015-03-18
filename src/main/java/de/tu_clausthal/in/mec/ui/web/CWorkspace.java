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
import de.tu_clausthal.in.mec.ui.CBrowser;


/**
 * main workspace
 */
public class CWorkspace extends CBrowser
{
    /**
     * HTTP server to handle websockets *
     */
    protected CServer m_server = new CServer( "localhost", CConfiguration.getInstance().get().getUibindport(), new CVirtualLocation.CLocation( "root", "index.htm" ) );


    /**
     * ctor - adds the browser *
     */
    public CWorkspace()
    {
        super();
        m_server.getVirtualLocation().add( new CVirtualLocation.CLocation( "documentation/user/" + CConfiguration.getInstance().get().getLanguage(), "index.md", "/userdoc*" ) );
        m_server.getVirtualLocation().add( new CVirtualLocation.CLocation( "documentation/developer", "index.htm", "/develdoc*" ) );
        //m_server.getVirtualLocation().add( new CVirtualLocation.CLocation( "<mecsim>/www",                                                   "index.htm",    "/local*" ) );

        this.load( "http://localhost:" + CConfiguration.getInstance().get().getUibindport() );
    }

}
