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
import java.net.URL;


/**
 * server class of the UI
 */
public class CWebUI extends JPanel
{

    protected CServer m_server = new CServer();
    protected CBrowser m_browser = new CBrowser( "http://localhost:" + CConfiguration.getInstance().get().getUibindport() );



    public CWebUI()
    {
        this.add( m_browser );
    }



    private class CServer extends NanoHTTPD
    {
        protected final File c_home = new File( this.getClass().getClassLoader().getResource( "ui" + File.separator + "index.htm" ).getPath() );


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


        @Override
        public Response serve( IHTTPSession p_session )
        {
            try
            {
                return new Response( FileUtils.readFileToString( c_home ) );
            }
            catch ( IOException l_exception )
            {
                CLogger.error( l_exception );
            }

            return super.serve( p_session );
        }
    }


}
