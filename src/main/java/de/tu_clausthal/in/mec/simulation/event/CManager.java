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

import de.tu_clausthal.in.mec.common.CNode;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


/**
 * eventmanager class
 */
public class CManager implements IVoidStepable
{

    /**
     * tree structure of all objects (root-node is equal to this object)
     */
    private CNode<Pair<Set<IParticipant>, Set<IMessage>>> m_root = new CNode( this.toString() );


    /**
     * register a new participant
     *
     * @param p_path     path of the receiver
     * @param p_receiver participant
     */
    public synchronized void register( CPath p_path, IParticipant p_receiver )
    {
        CNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.traverseto( p_path );

        if ( l_node.isDataNull() )
            l_node.setData( new ImmutablePair( new HashSet(), new HashSet() ) );

        l_node.getData().getLeft().add( p_receiver );
    }


    /**
     * unregister a participant
     *
     * @param p_path     path of the receiver
     * @param p_receiver participant
     */
    public synchronized void unregister( CPath p_path, IParticipant p_receiver )
    {
        if ( !m_root.pathexist( p_path ) )
            return;

        CNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.traverseto( p_path, false );
        l_node.getData().getLeft().remove( p_receiver );
    }


    /**
     * pushs a message to the queue
     *
     * @param p_path    of objects
     * @param p_message message
     */
    public void pushMessage( CPath p_path, IMessage p_message )
    {
        if ( !m_root.pathexist( p_path ) )
            return;

        // check time to live value
        if ( p_message.ttl() < 0 )
            return;

        for ( Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.traverseto( p_path, false ).getSubData() )
            l_item.getRight().add( p_message );
    }


    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        for ( Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.getSubData() )
        {
            for ( IParticipant l_receiver : l_item.getLeft() )
                l_receiver.receiveMessage( l_item.getRight() );

            // clear all messages
            l_item.getRight().clear();
        }
    }


    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }
}
