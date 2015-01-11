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

package de.tu_clausthal.in.mec.simulation.event;

import de.tu_clausthal.in.mec.object.IDataLayer;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.CSimulation;

import java.util.Set;
import java.util.UUID;


/**
 * participant of the event message system
 */
public class CParticipant implements IParticipant
{

    /**
     * defines the event UUID
     */
    protected UUID m_eventid = UUID.randomUUID();
    /**
     * defines the event name
     */
    protected String m_eventname = m_eventid.toString();
    /**
     * receiver object
     */
    protected IMessageReceiver m_receiver = null;


    /**
     * ctor to register the object on the event messager
     */
    public CParticipant( IMessageReceiver p_receiver )
    {
        if ( ( this instanceof IDataLayer ) || ( this instanceof ILayer ) )
            throw new IllegalStateException( "event handler cannot register for a layer" );

        m_receiver = p_receiver;
        CSimulation.getInstance().getEventManager().register( this );
    }


    @Override
    public String getEventName()
    {
        return m_eventname;
    }

    @Override
    public UUID getEventID()
    {
        return m_eventid;
    }

    @Override
    public <N> IMessage<N> createMessage( String p_name, N p_data )
    {
        return new CMessage( this, p_name, p_data );
    }


    @Override
    public <N> IMessage createMessage( N p_data )
    {
        return new CMessage( this, p_data );
    }


    @Override
    public void sendMessage( CParticipant p_receiver, IMessage p_message )
    {
        CSimulation.getInstance().getEventManager().pushMessage( p_receiver, p_message );
    }


    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {
        if ( m_receiver == null )
            return;
        m_receiver.receiveMessage( p_messages );
    }

}
