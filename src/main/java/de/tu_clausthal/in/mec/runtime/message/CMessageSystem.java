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

package de.tu_clausthal.in.mec.runtime.message;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CAdjacencyMatrix;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.common.CTreeNode;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import de.tu_clausthal.in.mec.ui.web.CSocketStorage;
import de.tu_clausthal.in.mec.ui.web.CWebSocket;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;


/**
 * message handler class
 */
public final class CMessageSystem implements IVoidSteppable
{
    /**
     * set with all communicators
     */
    private final CSocketStorage m_communicator = new CSocketStorage();
    /**
     * event listener *
     */
    private final Set<IActionListener> m_listener = new HashSet<>();
    /**
     * message counter
     */
    private final CAdjacencyMatrix<CPath, Integer> m_messageflow = new CAdjacencyMatrix<>( "source", "target" );
    /**
     * tree structure of all objects (root-node is equal to this object)
     */
    private final CTreeNode<Pair<Set<IParticipant>, Set<IMessage>>> m_root = new CTreeNode( this.toString() );

    /**
     * adds a listener
     *
     * @param p_listener listener
     */
    public final void addActionListener( final IActionListener p_listener )
    {
        m_listener.add( p_listener );
    }

    /**
     * pushs a message to the queue
     *
     * @param p_receiverpath receiver
     * @param p_message message
     */
    public final synchronized void pushMessage( final CPath p_receiverpath, final IMessage<?> p_message )
    {
        if ( ( p_receiverpath == null ) || ( p_message == null ) || ( p_receiverpath.isEmpty() ) )
            return;

        if ( !m_root.pathExist( p_receiverpath ) )
        {
            CLogger.error( CCommon.getResourceString( this, "messagefail", p_message.getData(), p_receiverpath ) );
            return;
        }

        if ( ( p_message.getSource() == null ) || ( p_message.getSource().isEmpty() ) || ( p_message.getSource().getPath().equals( p_receiverpath ) ) )
        {
            CLogger.error( CCommon.getResourceString( this, "push", p_message, p_receiverpath ) );
            return;
        }

        // check time to live value
        if ( p_message.ttl() < 0 )
        {
            CLogger.info( CCommon.getResourceString( this, "ttl", p_message ) );
            return;
        }

        // receive message / push message to the receiver - increment main receiver of the message
        this.incrementMessageFlow( p_message.getSource(), p_receiverpath );
        for ( final Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.getNode( p_receiverpath ).getTreeData() )
        {
            // if item equal null skip
            if ( l_item == null )
                continue;

            l_item.getRight().add( p_message );

            // all subnodes of the receiver gets the message from the receiver the root node get the receiver from the message sender
            for ( final IParticipant l_subreceiver : l_item.getLeft() )
                this.incrementMessageFlow( p_receiverpath, l_subreceiver.getReceiverPath() );
        }

        // call listener with receiver and message
        for ( final IActionListener l_item : m_listener )
            l_item.onPushMessage( p_receiverpath, p_message );
    }

    /**
     * register a new participant
     *
     * @param p_path path of the receiver
     * @param p_receiver participant
     */
    public final synchronized void register( final CPath p_path, final IParticipant p_receiver )
    {
        if ( ( p_path == null ) || ( p_path.isEmpty() ) || ( p_receiver == null ) || ( p_receiver == null ) )
        {
            CLogger.error( CCommon.getResourceString( this, "register", p_receiver, p_path ) );
            return;
        }

        final CTreeNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.getNode( p_path );
        if ( l_node.isDataNull() )
            l_node.setData( new ImmutablePair<>( new HashSet<>(), new HashSet<>() ) );
        l_node.getData().getLeft().add( p_receiver );

        CLogger.info( CCommon.getResourceString( this, "registered", p_receiver, p_path ) );

        for ( final IActionListener l_item : m_listener )
            l_item.onRegister( p_path, p_receiver );
    }

    @Override
    public final void release()
    {
        m_messageflow.clear();
    }

    /**
     * removes a listener
     *
     * @param p_listener listener
     */
    public final void removeListener( final IActionListener p_listener )
    {
        m_listener.remove( p_listener );
    }

    @Override
    public final void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        for ( final Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.getTreeData( false ) )
        {
            // data element within the tree can be used null values, so this items will be skipped
            // the item is also skipped, if there does not exists messages
            if ( ( l_item == null ) || ( l_item.getRight() == null ) || ( l_item.getRight().isEmpty() ) )
                continue;

            for ( final IParticipant l_receiver : l_item.getLeft() )
                l_receiver.receiveMessage( l_item.getRight() );

            // clear all messages, that are received
            l_item.getRight().clear();
        }

        m_communicator.send( CCommon.toJson( m_messageflow ) );
    }

    /**
     * unregister a participant
     *
     * @param p_path path of the receiver
     * @param p_receiver participant
     */
    public final synchronized void unregister( final CPath p_path, final IParticipant p_receiver )
    {
        if ( ( p_path == null ) || ( p_path.isEmpty() ) || ( p_receiver == null ) || ( !m_root.pathExist( p_path ) ) )
        {
            CLogger.error( CCommon.getResourceString( this, "unregister", p_receiver, p_path ) );
            return;
        }

        final CTreeNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.getNode( p_path );
        l_node.getData().getLeft().remove( p_receiver );

        CLogger.info( CCommon.getResourceString( this, "unregistered", p_receiver, p_path ) );

        for ( final IActionListener l_item : m_listener )
            l_item.onUnregister( p_path, p_receiver );
    }

    /**
     * increments the message flow
     *
     * @param p_row row value of the adjacency matrix
     * @param p_colum column value of the adjacency matrix
     */
    private void incrementMessageFlow( final CPath p_row, final CPath p_colum )
    {
        m_messageflow.set(
                p_row, p_colum, ( m_messageflow.exist( p_row, p_colum ) ? m_messageflow.get(
                        p_row, p_colum
                ) : new Integer( -1 ) ) + 1
        );
    }

    /**
     * UI method - message counting information
     */
    private void web_dynamic_flow( final CWebSocket.EAction p_action, final CWebSocket.CCommunicator p_communicator, final CWebSocket.CFrame p_frame )
    {
        m_communicator.handshake( p_action, p_communicator );
    }

    /**
     * observer interface *
     */
    public interface IActionListener
    {

        /**
         * is called on a message push
         */
        void onPushMessage( final CPath p_pathreceiver, final IMessage<?> p_message );

        /**
         * is called on register
         *
         * @param p_path path of the object
         * @param p_receiver receiver
         */
        void onRegister( final CPath p_path, final IParticipant p_receiver );

        /**
         * is called on unregister
         *
         * @param p_path path of the object
         * @param p_receiver receiver
         */
        void onUnregister( final CPath p_path, final IParticipant p_receiver );

    }
}
