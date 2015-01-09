/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.mec.simulation.event;

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.CSimulation;

import java.util.Set;
import java.util.UUID;


/**
 * participant for message system
 */
public abstract class IParticipant
{

    /**
     * defines the event UUID *
     */
    protected UUID m_eventid = UUID.randomUUID();
    /**
     * defines the event name *
     */
    protected String m_eventname = m_eventid.toString();


    /**
     * ctor to register the object on the event messager
     */
    protected IParticipant()
    {
        if ( this instanceof ILayer )
            throw new IllegalStateException( "event handler cannot register for a layer" );
        CSimulation.getInstance().getEventManager().register( this );
    }


    /**
     * get the unique name of the participant
     *
     * @return name
     */
    protected String getName()
    {
        return m_eventname;
    }

    /**
     * get an unique ID of the participant
     *
     * @return ID
     */
    protected UUID getID()
    {
        return m_eventid;
    }

    /**
     * creates a message
     *
     * @param p_name name of the message
     * @param p_data data
     * @param <T>    data type
     * @return message object
     */
    protected <T> IMessage<T> createMessage( String p_name, T p_data )
    {
        return new CDefaultMessage( this, p_name, p_data );
    }


    /**
     * creates a message
     *
     * @param p_data data
     * @param <T>    data type
     * @return message object
     */
    protected <T> IMessage<T> createMessage( T p_data )
    {
        return new CDefaultMessage( this, p_data );
    }

    /**
     * sends a message
     *
     * @param p_receiver receiver of the message
     * @param p_message  input message
     */
    protected void sendMessage( IParticipant p_receiver, IMessage p_message )
    {
        CSimulation.getInstance().getEventManager().pushMessage( p_receiver, p_message );
    }


    /**
     * receives all messages, each message is unique
     *
     * @param p_messages set of messages
     */
    protected void receiveMessage( Set<IMessage> p_messages )
    {
    }
}
