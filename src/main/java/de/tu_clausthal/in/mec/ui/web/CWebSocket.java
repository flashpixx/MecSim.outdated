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

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.CLogger;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebSocket;
import fi.iki.elonen.WebSocketFrame;

import java.io.IOException;
import java.lang.invoke.MethodHandle;


/**
 * encapsulating class for websocket
 */
public class CWebSocket extends WebSocket
{
    /**
     * encapsulate socket
     */
    private final CConnection m_connection = new CConnection( this );
    /**
     * object
     */
    private final Object m_object;
    /**
     * method handle
     */
    private final MethodHandle m_method;


    /**
     * ctor
     *
     * @param p_handshakeRequest
     * @param p_object
     * @param p_method
     */
    public CWebSocket( final NanoHTTPD.IHTTPSession p_handshakeRequest, final Object p_object, final MethodHandle p_method )
    {
        super( p_handshakeRequest );

        m_object = p_object;
        m_method = p_method;
    }


    @Override
    protected void onPong( final WebSocketFrame p_frame )
    {
        try
        {
            this.ping( p_frame.getBinaryPayload() );
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    @Override
    protected void onMessage( final WebSocketFrame p_frame )
    {
    }

    @Override
    protected void onClose( final WebSocketFrame.CloseCode p_close, final String p_reason, final boolean p_initbyremote )
    {

    }

    @Override
    protected void onException( final IOException p_exception )
    {
        CLogger.error( p_exception );
    }


    /**
     * inner connection class to encapsulation send methods
     */
    public static class CConnection
    {

        /**
         * socket *
         */
        private final CWebSocket m_socket;


        /**
         * ctor
         *
         * @param p_socket websocket
         */
        public CConnection( final CWebSocket p_socket )
        {
            m_socket = p_socket;
        }


        /**
         * send method
         *
         * @param p_payload string data payload
         * @throws IOException throws on IO error
         */
        public final void send( final String p_payload ) throws IOException
        {
            m_socket.send( p_payload );
        }

        /**
         * send method
         *
         * @param p_payload binary data payload
         * @throws IOException throws on IO error
         */

        public final void send( final byte[] p_payload ) throws IOException
        {
            m_socket.send( p_payload );
        }

    }
}
