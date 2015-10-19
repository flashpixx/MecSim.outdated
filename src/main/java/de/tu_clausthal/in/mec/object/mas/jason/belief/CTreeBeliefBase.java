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

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.CBeliefStorage;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.bb.BeliefBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.Set;


/**
 * beliefbase which wraps all Jason structures
 */
public final class CTreeBeliefBase implements BeliefBase, IBeliefBaseMask.IGenerator<Literal>
{

    /**
     * path separator of agent belief
     */
    private static final String c_agentbeliefseparator = "_";
    /**
     * binding replacing prefix - the prefix will be removed from the binding belief name
     */
    private static final String c_beliefbindprefixreplace = "m_";
    /**
     * name of the root beliefbase and its mask
     */
    private static final CPath c_beliefbaseroot = new CPath( "root" );
    /**
     * name of the binding beliefbase and its mask
     */
    private static final CPath c_beliefbasebind = new CPath( "bind" );
    /**
     * name of the message beliefbase and its mask
     */
    private static final CPath c_beliefbasemessage = new CPath( "message" );
    /**
     * root beliefbase
     */
    private final CMask m_beliefbaserootmask;


    /**
     * ctor
     *
     * @param p_agent agent which will be connected with the beliefbase
     * @param p_agentnameseparator agent name seperator used by the message beliefbase
     */
    public CTreeBeliefBase( final Agent p_agent, final String p_agentnameseparator )
    {
        m_beliefbaserootmask = new CBeliefBase( new CBeliefStorage<>(), c_agentbeliefseparator ).createMask( c_beliefbaseroot.getSuffix() );
        m_beliefbaserootmask.setPrefixRemove( c_beliefbaseroot );

        // --- create beliefbase structure with tree structure
        m_beliefbaserootmask.add(
                new CBeliefBase(
                        new CMessageStorage( p_agent.getTS(), p_agentnameseparator ), c_agentbeliefseparator
                ).<IBeliefBaseMask<Literal>>createMask( c_beliefbasemessage.getSuffix() )
        );
        m_beliefbaserootmask.add(
                new CBeliefBase(
                        // not the order of replacing arguments
                        new CBindingStorage( c_beliefbindprefixreplace, c_agentbeliefseparator ), c_agentbeliefseparator
                ).<IBeliefBaseMask<Literal>>createMask( c_beliefbasebind.getSuffix() )
        );

    }

    /**
     * bind an object to the binding-beliefbase
     *
     * @param p_bindname bind name
     * @param p_bind bind object
     */
    public void bind( final String p_bindname, final Object p_bind )
    {
        m_beliefbaserootmask.getMask( c_beliefbasebind ).<CBindingStorage>getStorage().push( p_bindname, p_bind );
    }

    /**
     * unbind an object from the binding-beliefbase
     *
     * @param p_bindname bind name
     */
    public void unbind( final String p_bindname )
    {
        m_beliefbaserootmask.getMask( c_beliefbasebind ).<CBindingStorage>getStorage().removeBinding( p_bindname );
    }

    /**
     * returns the root mask
     *
     * @return root mask
     */
    public IBeliefBaseMask<Literal> getRootMask()
    {
        return m_beliefbaserootmask;
    }


    /**
     * removes an item from the bliefbase
     *
     * @param p_path item path
     */
    public void remove( final CPath p_path )
    {
        m_beliefbaserootmask.remove( p_path );
    }

    /**
     * adds a mask to the beliefbase
     */
    public void add( final CPath p_path, final IBeliefBaseMask<Literal> p_mask )
    {
        m_beliefbaserootmask.add( p_mask );
    }


    /**
     * sets the messages to the message-beliefbase
     *
     * @param p_messages set of messages
     */
    public void setMessages( final Set<IMessage> p_messages )
    {
        m_beliefbaserootmask.getMask( c_beliefbasemessage ).<CMessageStorage>getStorage().receiveMessage( p_messages );
    }


    /**
     * updates all internal data structures,
     * should be called on the agent cycle
     */
    public void update()
    {
        m_beliefbaserootmask.update();
    }


    /**
     * adds a literal and its belief structure to the agent beliefbase
     *
     * @param p_literal literal with prefix
     * @param p_addliteral adds the literal to the created structure
     */
    public void setBeliefbaseStructure( final Literal p_literal, final boolean p_addliteral )
    {
        // split path of the literal functor
        final CPath l_path = CPath.createSplitPath( c_agentbeliefseparator, p_literal.getFunctor() );

        // remove if exists root beliefbase name
        if ( l_path.startsWith( c_beliefbaseroot ) )
            l_path.remove( 0, c_beliefbaseroot.size() );

        // add the belief to the structure
        if ( p_addliteral )
            m_beliefbaserootmask.add( l_path.getSubPath( 0, -1 ), new CLiteral( l_path.getSuffix(), p_literal ), this );
        else
            m_beliefbaserootmask.add( l_path.getSubPath( 0, -1 ), this );
    }

    @Override
    public IBeliefBaseMask<Literal> createBeliefbase( final String p_name )
    {
        return new CBeliefBase( new CBeliefStorage<>(), c_agentbeliefseparator ).createMask( p_name );
    }

    @Override
    public void init( final Agent ag, final String[] args )
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public void clear()
    {

    }

    @Override
    public boolean add( final Literal l )
    {
        return false;
    }

    @Override
    public boolean add( final int index, final Literal l )
    {
        return false;
    }

    @Override
    public Iterator<Literal> iterator()
    {
        return null;
    }

    @Override
    public Iterator<Literal> getAll()
    {
        return null;
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final PredicateIndicator pi )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final Literal l, final Unifier u )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getRelevant( final Literal l )
    {
        return null;
    }

    @Override
    public Literal contains( final Literal l )
    {
        return null;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public Iterator<Literal> getPercepts()
    {
        return null;
    }

    @Override
    public boolean remove( final Literal l )
    {
        return false;
    }

    @Override
    public boolean abolish( final PredicateIndicator pi )
    {
        return false;
    }

    @Override
    public Element getAsDOM( final Document document )
    {
        return null;
    }

    @Override
    public BeliefBase clone()
    {
        return this;
    }
}
