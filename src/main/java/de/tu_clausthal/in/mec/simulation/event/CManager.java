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

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * eventmanager class
 */
public class CManager implements IVoidStepable
{

    /**
     * list of messages
     */
    private Map<IParticipant, Set<IMessage>> m_data = new ConcurrentHashMap();
    /**
     * list of UUID -> participant
     */
    private Map<UUID, IParticipant> m_uuidparticipant = new ConcurrentHashMap();
    /**
     * list of name -> participant
     */
    private Map<String, IParticipant> m_nameparticipant = new ConcurrentHashMap();


    /**
     * register a new participant
     *
     * @param p_receiver participant
     */
    public void register( IParticipant p_receiver )
    {
        m_data.put( p_receiver, Collections.synchronizedSet( new HashSet() ) );
        m_uuidparticipant.put( p_receiver.getEventID(), p_receiver );
        m_nameparticipant.put( p_receiver.getEventName(), p_receiver );
    }


    /**
     * unregister a participant
     *
     * @param p_receiver participant
     */
    public void unregister( IParticipant p_receiver )
    {
        m_data.remove( p_receiver );
        m_uuidparticipant.remove( p_receiver.getEventID() );
        m_nameparticipant.remove( p_receiver.getEventName() );
    }


    /**
     * pushs a message to the queue
     *
     * @param p_receiver receiver of the message
     * @param p_message  message
     */
    public void pushMessage( IParticipant p_receiver, IMessage p_message )
    {
        Set<IMessage> l_messages = m_data.get( p_receiver );
        l_messages.add( p_message );
    }


    /**
     * returns a set of all current participants
     *
     * @return participant set
     */
    public Set<IParticipant> getParticipant()
    {
        return m_data.keySet();
    }

    /**
     * returns a participant on the name
     *
     * @param p_name name
     * @return participant or null
     */
    public IParticipant getParticipant( String p_name )
    {
        return m_nameparticipant.get( p_name );
    }


    /**
     * returns a participant on the UUID
     *
     * @param p_id UUID
     * @return participant or null
     */
    public IParticipant getParticipant( UUID p_id )
    {
        return m_uuidparticipant.get( p_id );
    }

    /**
     * clears all messages
     */
    public void clear()
    {
        m_data.clear();
        m_uuidparticipant.clear();
        m_nameparticipant.clear();
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        for ( Map.Entry<IParticipant, Set<IMessage>> l_item : m_data.entrySet() )
        {
            l_item.getKey().receiveMessage( l_item.getValue() );
            l_item.getValue().clear();
        }
    }


    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }
}
