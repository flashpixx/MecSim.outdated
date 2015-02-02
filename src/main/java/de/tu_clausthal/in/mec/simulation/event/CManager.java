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

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.*;
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
    protected CNode<Pair<Set<IParticipant>, Set<IMessage>>> m_root = new CNode( this.toString() );


    /**
     * register a new participant
     *
     * @param p_path     path of the receiver
     * @param p_receiver participant
     */
    public synchronized void register( CPath p_path, IParticipant p_receiver )
    {
        if ( ( p_path == null ) || ( p_path.isEmpty() ) || ( p_receiver == null ) || ( p_receiver == null ) )
        {
            CLogger.error( CCommon.getResouceString( this, "register", p_receiver, p_path ) );
            return;
        }

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
        if ( ( p_path == null ) || ( p_path.isEmpty() ) || ( p_receiver == null ) || ( !m_root.pathexist( p_path ) ) )
        {
            CLogger.error( CCommon.getResouceString( this, "unregister", p_receiver, p_path ) );
            return;
        }

        CNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.traverseto( p_path, false );
        l_node.getData().getLeft().remove( p_receiver );
    }


    /**
     * pushs a message to the queue
     *
     * @param p_path    receiver
     * @param p_message message
     */
    public synchronized void pushMessage( CPath p_path, IMessage p_message )
    {
        if ( ( p_path == null ) || ( p_message == null ) || ( p_path.isEmpty() ) || ( !m_root.pathexist( p_path ) ) )
            return;

        if ( ( p_message.getSource() == null ) || ( p_message.getSource().isEmpty() ) || ( ( p_message.getSource().getPath().contains( p_path.getPath() ) ) && ( !p_message.getSource().getPath().equals( p_path ) ) ) )
        {
            CLogger.error( CCommon.getResouceString( this, "push", p_message, p_path ) );
            return;
        }

        // check time to live value
        if ( p_message.ttl() < 0 )
        {
            CLogger.info( CCommon.getResouceString( this, "ttl", p_message ) );
            return;
        }

        for ( Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.traverseto( p_path, false ).getSubData() )
            l_item.getRight().add( p_message );
    }


    @Override
    /**
     * @bug creates a null pointer exception
     */
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        /*
        for ( Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.getSubData() )
        {
            for ( IParticipant l_receiver : l_item.getLeft() )
                l_receiver.receiveMessage( l_item.getRight() );

            // clear all messages
            l_item.getRight().clear();
        }
        */
    }


    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void release()
    {

    }
}
