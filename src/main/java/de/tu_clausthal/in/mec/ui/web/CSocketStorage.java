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
    public void clear()
    {
        m_communicator.clear();
    }


    /**
     * returns the number of objects
     *
     * @return
     */
    public int size()
    {
        return m_communicator.size();
    }


    /**
     * checks emptyness
     *
     * @return boolean of emptyness
     */
    public boolean isEmpty()
    {
        return m_communicator.isEmpty();
    }


    /**
     * checks if a key exists
     *
     * @param p_key ID of the websocket (hash code)
     * @return boolean of existance
     */
    public boolean contains( final Integer p_key )
    {
        return m_communicator.containsKey( p_key );
    }


    /**
     * checks if a websocket exists
     *
     * @param p_value websocket communicator
     * @return boolean of existance
     */
    public boolean contains( final CWebSocket.CCommunicator p_value )
    {
        return m_communicator.containsKey( p_value.getID() );
    }


    /**
     * returns the websocket communicator
     *
     * @param p_key ID of the socket (hash code)
     * @return communicator or null
     */
    public CWebSocket.CCommunicator get( final Integer p_key )
    {
        return m_communicator.get( p_key );
    }


    /**
     * adds a websocket communicator
     *
     * @param p_value communicator object
     */
    public void put( final CWebSocket.CCommunicator p_value )
    {
        m_communicator.put( p_value.getID(), p_value );
    }


    /**
     * removes a websocket communicator
     *
     * @param p_key ID of the socket communicator
     * @return removed communicator
     */
    public CWebSocket.CCommunicator remove( final Integer p_key )
    {
        return m_communicator.remove( p_key );
    }


    /**
     * removes a websocket communicator
     *
     * @param p_value communicator
     * @return removed communicator
     */
    public CWebSocket.CCommunicator remove( final CWebSocket.CCommunicator p_value )
    {
        return m_communicator.remove( p_value.getID() );
    }


    /**
     * runs the handshake
     *
     * @param p_action action
     * @param p_communicator communicator
     */
    public void handshake( final CWebSocket.EAction p_action, final CWebSocket.CCommunicator p_communicator )
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
     * sends the payload to all sockets
     *
     * @param p_payload string payload
     */
    public void send( final String p_payload )
    {
        for ( CWebSocket.CCommunicator l_item : m_communicator.values() )
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
    public void send( final byte[] p_payload )
    {
        for ( CWebSocket.CCommunicator l_item : m_communicator.values() )
            try
            {
                l_item.send( p_payload );
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
            }
    }
}