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
import de.tu_clausthal.in.mec.simulation.IVoidStepable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * eventmanager class
 */
public class CManager implements IVoidStepable
{

    /**
     * list of messages *
     */
    private Map<IParticipant, Set<IMessage>> m_data = new ConcurrentHashMap();


    /**
     * register a new participant
     *
     * @param p_receiver participant
     */
    public void register( IParticipant p_receiver )
    {
        m_data.put( p_receiver, Collections.synchronizedSet( new HashSet() ) );
    }


    /**
     * unregister a participant
     *
     * @param p_receiver participant
     */
    public void unregister( IParticipant p_receiver )
    {
        m_data.remove( p_receiver );
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
