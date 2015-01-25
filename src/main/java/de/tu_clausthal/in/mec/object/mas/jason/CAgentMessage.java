/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import jason.asSemantics.Message;


/**
 * class to encapsulate a Jason message for the event manager
 */
public class CAgentMessage implements IMessage<Message>
{

    /**
     * message data
     *
     * @note Jason message data
     */
    private Message m_data = null;
    /**
     * ttl of the message *
     */
    private int m_ttl = 10;


    /**
     * ctor
     *
     * @param p_message Jason message
     */
    public CAgentMessage( Message p_message )
    {
        if ( p_message == null )
            throw new IllegalArgumentException( "message data need not to be null" );

        m_data = p_message;
    }

    @Override
    public Message getData()
    {
        return m_data;
    }

    @Override
    public String getTitle()
    {
        return this.getClass().getName();
    }

    @Override
    public CPath getSource()
    {
        return new CPath( m_data.getSender() );
    }

    @Override
    public int ttl()
    {
        return m_ttl--;
    }
}
