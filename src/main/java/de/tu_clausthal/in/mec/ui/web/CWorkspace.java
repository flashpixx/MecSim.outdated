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
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.simulation.CSimulation;


/**
 * main workspace of the web-menu structure (browser & server structure)
 */
public class CWorkspace extends CBrowser
{
    /**
     * HTTP server to handle websockets *
     */
    protected final CServer m_server = new CServer( "localhost", CConfiguration.getInstance().get().UIBindPort, new CVirtualDirectory( CCommon.getResourceURL( "web/root" ), "index.htm" ) );


    /**
     * ctor - adds the browser *
     */
    public CWorkspace()
    {
        super( EMenu.BackForward );
        this.load( "http://localhost:" + CConfiguration.getInstance().get().UIBindPort );

        // set via reflection the server
        try
        {
            if ( CSimulation.getInstance().getWebServer() == null )
                CReflection.getClassField( CSimulation.getInstance().getClass(), "m_webserver" ).getSetter().invoke( CSimulation.getInstance(), m_server );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }

}
