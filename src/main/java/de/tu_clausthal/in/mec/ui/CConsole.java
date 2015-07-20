/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.ui.web.CSocketStorage;
import de.tu_clausthal.in.mec.ui.web.CWebSocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * stream to create stream access
 *
 * @note log messages can be written with System.out or System.err
 */
public class CConsole extends ByteArrayOutputStream
{
    /**
     * base URI *
     */
    private final String m_baseuri;
    /**
     * set with all communicators
     */
    private final CSocketStorage m_communicator = new CSocketStorage();

    /**
     * ctor
     */
    private CConsole( final String p_baseuri )
    {
        m_baseuri = p_baseuri;
    }

    /**
     * factor method to set error stream
     *
     * @param p_baseuri base uri
     * @return console stream
     */
    public static CConsole getError( final String p_baseuri )
    {
        final CConsole l_stream = new CConsole( p_baseuri );
        System.setErr( new PrintStream( l_stream, true ) );
        return l_stream;
    }

    /**
     * factor method to set output stream
     *
     * @param p_baseuri base uri
     * @return console stream
     */
    public static CConsole getOutput( final String p_baseuri )
    {
        final CConsole l_stream = new CConsole( p_baseuri );
        System.setOut( new PrintStream( l_stream, true ) );
        return l_stream;
    }

    @Override
    public void flush() throws IOException
    {
        final String l_data = this.toString();
        if ( l_data.isEmpty() )
            return;
        this.reset();

        m_communicator.send( l_data );
    }

    /**
     * UI method - show log
     */
    private void web_dynamic_log( final CWebSocket.EAction p_action, final CWebSocket.CCommunicator p_communicator, final CWebSocket.CFrame p_frame )
    {
        m_communicator.handshake( p_action, p_communicator );
    }

    /**
     * UI method - register sockets
     *
     * @return base URI
     */
    private String web_uribase()
    {
        return m_baseuri;
    }

}
