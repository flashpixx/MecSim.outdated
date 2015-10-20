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
import de.tu_clausthal.in.mec.object.mas.generic.ILiteral;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.CBeliefStorage;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.bb.BeliefBase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
    private final CMask m_mask = new CBeliefBase( new CBeliefStorage<>(), c_agentbeliefseparator ).createMask( c_beliefbaseroot.getSuffix() );


    /**
     * ctor
     */
    public CTreeBeliefBase()
    {
        m_mask.setPathSeparator( c_agentbeliefseparator );

        // set binding-beliefbase
        m_mask.add(
                new CBeliefBase(
                        // not the order of replacing arguments
                        new CBindingStorage( c_beliefbindprefixreplace, c_agentbeliefseparator ), c_agentbeliefseparator
                ).<IBeliefBaseMask<Literal>>createMask( c_beliefbasebind.getSuffix() )
        );
    }

    /**
     * set agent to the beliefbase for message system, can be set once
     *
     * @param p_agent agent which will be connected with the beliefbase
     * @param p_agentnameseparator agent name seperator used by the message beliefbase
     */
    public void setMessageBeliefbase( final Agent p_agent, final String p_agentnameseparator )
    {
        if ( m_mask.containsMask( c_beliefbasemessage ) )
            return;

        m_mask.add(
                new CBeliefBase(
                        new CMessageStorage( p_agent.getTS(), p_agentnameseparator ), c_agentbeliefseparator
                ).<IBeliefBaseMask<Literal>>createMask( c_beliefbasemessage.getSuffix() )
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
        m_mask.getMask( c_beliefbasebind ).<CBindingStorage>getStorage().push( p_bindname, p_bind );
    }

    /**
     * unbind an object from the binding-beliefbase
     *
     * @param p_bindname bind name
     */
    public void unbind( final String p_bindname )
    {
        m_mask.getMask( c_beliefbasebind ).<CBindingStorage>getStorage().removeBinding( p_bindname );
    }

    /**
     * returns the root mask
     *
     * @return root mask
     */
    public IBeliefBaseMask<Literal> getRootMask()
    {
        return m_mask;
    }


    /**
     * removes an item from the bliefbase
     *
     * @param p_path item path
     */
    public void remove( final CPath p_path )
    {
        m_mask.remove( p_path );
    }

    /**
     * adds a mask to the beliefbase
     */
    public void add( final CPath p_path, final IBeliefBaseMask<Literal> p_mask )
    {
        m_mask.add( p_mask );
    }


    /**
     * sets the messages to the message-beliefbase
     *
     * @param p_messages set of messages
     */
    public void setMessages( final Set<IMessage> p_messages )
    {
        if ( !m_mask.containsMask( c_beliefbasemessage ) )
            return;

        m_mask.getMask( c_beliefbasemessage ).<CMessageStorage>getStorage().receiveMessage( p_messages );
    }


    /**
     * updates all internal data structures,
     * should be called on the agent cycle
     */
    public void update()
    {
        m_mask.update();
    }


    /**
     * adds a literal and its belief structure to the agent beliefbase
     *
     * @param p_literal literal with prefix
     * @param p_addliteral adds the literal to the created structure
     */
    public void setBeliefbaseStructure( final Literal p_literal, final boolean p_addliteral )
    {
        final Pair<CPath, CLiteral> l_pathliteral = this.createPathLiteral( p_literal );

        // add the belief to the structure
        if ( p_addliteral )
            m_mask.add( l_pathliteral.getLeft(), l_pathliteral.getRight(), this );
        else
            m_mask.add( l_pathliteral.getLeft(), this );
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
        m_mask.clear();
    }

    @Override
    public boolean add( final Literal p_literal )
    {
        return this.add( 0, p_literal );
    }

    @Override
    public boolean add( final int p_index, final Literal p_literal )
    {
        final Pair<CPath, CLiteral> l_pathliteral = this.createPathLiteral( p_literal );
        m_mask.add( l_pathliteral.getLeft(), l_pathliteral.getRight() );
        return true;
    }

    @Override
    public Iterator<Literal> iterator()
    {
        return this.getLiteralIterator( m_mask.iteratorLiteral() );
    }

    @Override
    public Iterator<Literal> getAll()
    {
        // deprecated
        return this.iterator();
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final PredicateIndicator p_predict )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final Literal p_literal, final Unifier p_unify )
    {
        return null;
    }

    @Override
    public Iterator<Literal> getRelevant( final Literal p_literal )
    {
        // deprecated
        return this.getCandidateBeliefs( p_literal, null );
    }

    @Override
    public Literal contains( final Literal p_literal )
    {
        final Pair<CPath, CLiteral> l_pathliteral = this.createPathLiteral( p_literal );

        // Jason does not implement contains correct, so contains returns the literals if it is found or null if it is not found
        if ( m_mask.containsLiteral( l_pathliteral.getLeft().append( l_pathliteral.getRight().getFunctor().get() ) ) )
            return p_literal;
        return null;
    }

    @Override
    public int size()
    {
        return m_mask.sizeLiteral();
    }

    @Override
    public Iterator<Literal> getPercepts()
    {
        return this.iterator();
    }

    @Override
    public boolean remove( final Literal p_literal )
    {
        final Pair<CPath, CLiteral> l_pathliteral = this.createPathLiteral( p_literal );
        return m_mask.remove( l_pathliteral.getLeft(), l_pathliteral.getValue() );
    }

    @Override
    public boolean abolish( final PredicateIndicator p_predict )
    {
        return false;
    }

    @Override
    public Element getAsDOM( final Document p_document )
    {
        final Element l_beliefs = (Element) p_document.createElement( "beliefs" );
        for ( final Literal l_item : this )
            l_beliefs.appendChild( l_item.getAsDOM( p_document ) );

        return l_beliefs;
    }

    /**
     * @todo define a full deep-copy
     */
    @Override
    public BeliefBase clone()
    {
        return this;
    }

    /**
     * creates a generic literal with path
     *
     * @param p_literal input literal
     * @return pair of literal path and generic literal
     */
    private Pair<CPath, CLiteral> createPathLiteral( final Literal p_literal )
    {
        // split path from functor and remove if exists the root name
        final CPath l_path = CPath.createSplitPath( c_agentbeliefseparator, p_literal.getFunctor() );
        if ( l_path.startsWith( c_beliefbaseroot ) )
            l_path.remove( 0, c_beliefbaseroot.size() );

        // build literal
        final Literal l_literal = ASSyntax.createLiteral( !p_literal.negated(), l_path.getSuffix() );
        l_literal.addAnnot( p_literal.getAnnots() );
        l_literal.addTerms( p_literal.getTerms() );

        // create pair of path and literal
        return new ImmutablePair<>( l_path.getSubPath( 0, -1 ), new CLiteral( l_literal ) );
    }

    /**
     * get literal iterator
     *
     * @return iterator
     */
    private Iterator<Literal> getLiteralIterator( final Iterator<ILiteral<Literal>> p_iterator )
    {
        return new Iterator<Literal>()
        {
            @Override
            public boolean hasNext()
            {
                return p_iterator.hasNext();
            }

            @Override
            public Literal next()
            {
                // literal with path
                return p_iterator.next().getLiteral();
            }
        };
    }

}
