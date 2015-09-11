/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * storage class to store websockets communicator thread-safe
 */
public class CSocketStorage
{
    /**
     * thread-safe map with all active sockets *
     */
    private final Map<Integer, CWebSocket.CCommunicator> m_communicator = new ConcurrentHashMap<>();


    /**
     * clear all data
     */
    public final void clear()
    {
        m_communicator.clear();
    }

    /**
     * checks if a key exists
     *
     * @param p_key ID of the websocket (hash code)
     * @return boolean of existance
     */
    public final boolean contains( final Integer p_key )
    {
        return m_communicator.containsKey( p_key );
    }

    /**
     * checks if a websocket exists
     *
     * @param p_value websocket communicator
     * @return boolean of existance
     */
    public final boolean contains( final CWebSocket.CCommunicator p_value )
    {
        return m_communicator.containsKey( p_value.getID() );
    }

    /**
     * returns the websocket communicator
     *
     * @param p_key ID of the socket (hash code)
     * @return communicator or null
     */
    public final CWebSocket.CCommunicator get( final Integer p_key )
    {
        return m_communicator.get( p_key );
    }

    /**
     * runs the handshake
     *
     * @param p_action action
     * @param p_communicator communicator
     */
    public final void handshake( final CWebSocket.EAction p_action, final CWebSocket.CCommunicator p_communicator )
    {
        switch ( p_action )
        {
            case Open:
                m_communicator.put( p_communicator.getID(), p_communicator );
                break;

            case Close:
                m_communicator.remove( p_communicator.getID() );
                break;

            default:
        }
    }

    /**
     * checks emptyness
     *
     * @return boolean of emptyness
     */
    public final boolean isEmpty()
    {
        return m_communicator.isEmpty();
    }

    /**
     * adds a websocket communicator
     *
     * @param p_value communicator object
     */
    public final void put( final CWebSocket.CCommunicator p_value )
    {
        m_communicator.put( p_value.getID(), p_value );
    }


    /**
     * removes a websocket communicator
     *
     * @param p_key ID of the socket communicator
     * @return removed communicator
     */
    public final CWebSocket.CCommunicator remove( final Integer p_key )
    {
        return m_communicator.remove( p_key );
    }


    /**
     * removes a websocket communicator
     *
     * @param p_value communicator
     * @return removed communicator
     */
    public final CWebSocket.CCommunicator remove( final CWebSocket.CCommunicator p_value )
    {
        return m_communicator.remove( p_value.getID() );
    }

    /**
     * sends the payload to all sockets
     *
     * @param p_payload string payload
     */
    public final void send( final String p_payload )
    {
        for ( final CWebSocket.CCommunicator l_item : m_communicator.values() )
            try
            {
                l_item.send( p_payload );
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
            }
    }

    /**
     * sends the byte payload to all sockets
     *
     * @param p_payload byte payload
     */
    public final void send( final byte[] p_payload )
    {
        for ( final CWebSocket.CCommunicator l_item : m_communicator.values() )
            try
            {
                l_item.send( p_payload );
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
            }
    }

    /**
     * returns the number of objects
     *
     * @return
     */
    public final int size()
    {
        return m_communicator.size();
    }
}
