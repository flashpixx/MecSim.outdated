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
import java.util.Timer;
import java.util.TimerTask;


/**
 * encapsulating class for websocket
 * the object represents one connection
 */
public class CWebSocket extends WebSocket
{
    /**
     * millisecond multiplier
     */
    private static final int c_millisecond = 1000;
    /**
     * communicator object
     */
    private final CCommunicator m_communicator;
    /**
     * bind Object
     */
    private final Object m_object;
    /**
     * bind method
     */
    private final MethodHandle m_method;
    /**
     * heartbeat timer
     */
    private final Timer m_heartbeat;


    /**
     * ctor
     *
     * @param p_handshakeRequest handshake object
     * @param p_heartbeat seconds in which the heartbeat is trasmittet
     * @param p_object bind object
     * @param p_method method handle
     */
    public CWebSocket( final NanoHTTPD.IHTTPSession p_handshakeRequest, final int p_heartbeat, final Object p_object, final MethodHandle p_method )
    {
        super( p_handshakeRequest );

        m_object = p_object;
        m_method = p_method;
        m_communicator = new CCommunicator();

        // create heartbeat
        if ( p_heartbeat < 1 )
            m_heartbeat = null;
        else
        {
            m_heartbeat = new Timer();
            m_heartbeat.schedule(
                    new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                CWebSocket.this.ping( "".getBytes() );
                            }
                            catch ( final IOException l_exception )
                            {
                                CLogger.error( l_exception );
                            }
                        }
                    }, p_heartbeat * c_millisecond
            );
        }

        this.invokeMethod( EAction.Open, null );
    }


    /**
     * invokes the method
     *
     * @param p_action action of the call
     * @param p_frame current frame or null
     */
    private void invokeMethod( final EAction p_action, final WebSocketFrame p_frame )
    {
        try
        {
            m_method.invoke( m_object, p_action, m_communicator, p_frame == null ? null : new CFrame( p_frame ) );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }


    @Override
    protected final void onPong( final WebSocketFrame p_webSocketFrame )
    {
        try
        {
            this.ping( p_webSocketFrame.getBinaryPayload() );
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    @Override
    protected final void onMessage( final WebSocketFrame p_frame )
    {
        p_frame.setUnmasked();
        this.invokeMethod( EAction.Message, p_frame );
    }

    @Override
    protected final void onClose( final WebSocketFrame.CloseCode p_close, final String p_reason, final boolean p_initbyremote )
    {
        this.invokeMethod( EAction.Close, null );
    }

    @Override
    protected final void onException( final IOException p_exception )
    {
        CLogger.error( p_exception );
        try
        {
            this.close( WebSocketFrame.CloseCode.InternalServerError, p_exception.getMessage() );
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * enum to define action
     */
    public enum EAction
    {
        /**
         * on websocket open *
         */
        Open,
        /**
         * on receiving message *
         */
        Message,
        /**
         * on closing *
         */
        Close
    }

    /**
     * connunicator object to handle all communication data
     */
    public class CCommunicator
    {

        /**
         * returns an unqiue ID of the socket
         *
         * @return ID
         */
        public final int getID()
        {
            return CWebSocket.this.hashCode();
        }

        /**
         * send string payload to the current socket
         *
         * @note line break at the end of the string to avoid an EOF error
         * @param p_payload string payload
         * @throws IOException throws on IO error
         */
        public final void send( final String p_payload ) throws IOException
        {
            CWebSocket.this.send( p_payload + "\n" );
        }

        /**
         * send byte payload to the current socket
         *
         * @param p_payload byte payload
         * @throws IOException throws on IO error
         */
        public final void send( final byte[] p_payload ) throws IOException
        {
            CWebSocket.this.send( p_payload );
        }

    }


    /**
     * data frame
     */
    public class CFrame
    {
        /**
         * origin data frame *
         */
        private final WebSocketFrame m_frame;


        /**
         * ctor
         *
         * @param p_frame set frame
         */
        public CFrame( final WebSocketFrame p_frame )
        {
            m_frame = p_frame;
        }

        /**
         * returns an unqiue ID of the socket
         *
         * @return ID
         */
        public final int getID()
        {
            return CWebSocket.this.hashCode();
        }

        /**
         * returns the text payload
         *
         * @return string with text payload
         */
        public final String getTextPayload()
        {
            return m_frame.getTextPayload();
        }

        /**
         * returns the binary payload
         *
         * @return byte payload
         */
        public final byte[] getBinaryPayload()
        {
            return m_frame.getBinaryPayload();
        }

    }

}
