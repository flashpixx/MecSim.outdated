/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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
 **/

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IVoidAgent;
import de.tu_clausthal.in.mec.object.mas.jason.action.CInternalEmpty;
import de.tu_clausthal.in.mec.object.mas.jason.action.CMethodBind;
import de.tu_clausthal.in.mec.object.mas.jason.action.IAction;
import de.tu_clausthal.in.mec.object.mas.jason.belief.IBelief;
import de.tu_clausthal.in.mec.simulation.message.CParticipant;
import de.tu_clausthal.in.mec.simulation.message.IMessage;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.architecture.MindInspectorWeb;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.InternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.PlanLibrary;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;
import jason.bb.DefaultBeliefBase;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * class of a Jason agent architecture
 *
 * @tparam T typ of binding objects
 * @bug check agent name / structure
 */
public class CAgent<T> implements IVoidAgent
{

    /**
     * Jason interal agent architecture to run the reasoning cycle
     */
    protected CJasonArchitecture m_architecture = null;
    /**
     * Jason agent object
     */
    protected Agent m_agent = null;
    /**
     * cycle number of the agent - it need not to be equal to the simulation step (the cycle is the lifetime of the
     * agent)
     */
    protected int m_cycle = 0;
    /**
     * set with actions of this implementation
     */
    protected Map<String, IAction> m_action = new HashMap<>();
    /**
     * set with belief binds
     */
    protected Set<IBelief> m_beliefs = new HashSet<>();
    /**
     * name of the agent
     */
    protected CPath m_namepath = null;
    /**
     * participant object *
     */
    protected CParticipant m_participant = null;
    /**
     * set with received messages
     */
    protected Set<IMessage> m_receivedmessages = new HashSet<>();


    /**
     * ctor
     *
     * @param p_asl agent ASL file
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final String p_asl ) throws JasonException
    {
        this( null, p_asl, null );
    }
    

    /**
     * ctor
     *
     * @param p_namepath name of the agent (full path)
     * @param p_asl  agent ASL file
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final CPath p_namepath, final String p_asl ) throws JasonException
    {
        this( p_namepath, p_asl, null );
    }


    /**
     * ctor
     *
     * @param p_namepath name of the agent (full path)
     * @param p_asl  agent ASL file
     * @param p_bind object that should be bind with the agent
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final CPath p_namepath, final String p_asl, final T p_bind ) throws JasonException
    {
        m_namepath = p_namepath;
        if ( ( m_namepath == null ) || ( m_namepath.isEmpty() ) )
            m_namepath = new CPath( this.getClass().getSimpleName() + "@" + this.hashCode() );

        if ( p_bind != null )
        {
            m_action.put( "set", new de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind( "self", p_bind ) );
            m_action.put( "invoke", new CMethodBind( "self", p_bind ) );
            m_beliefs.add( new de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind( "self", p_bind ) );
        }

        // Jason code design error: the agent name is stored within the AgArch, but it can read if an AgArch has got an AgArch
        // successor (AgArchs are a linked list), so we insert a cyclic reference to the AgArch itself
        m_architecture = new CJasonArchitecture();
        m_architecture.insertAgArch( m_architecture );

        // build an own agent to handle manual internal actions
        m_agent = new CJasonAgent( IEnvironment.getAgentFile( p_asl ), m_architecture );

        // initialize message system
        m_participant = new CParticipant( this );
    }


    /**
     * ctor
     *
     * @param p_asl  agent ASL file
     * @param p_bind object that should be bind with the agent
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final String p_asl, final T p_bind ) throws JasonException
    {
        this( null, p_asl, p_bind );
    }


    /**
     * returns the set of actions
     *
     * @return action set
     */
    public final Map<String, IAction> getActions()
    {
        return m_action;
    }


    /**
     * returns a set of belief obejcts
     *
     * @return belief set
     */
    public final Set<IBelief> getBelief()
    {
        return m_beliefs;
    }


    @Override
    public final void release()
    {
        m_agent.stopAg();
        m_participant.release();
        MindInspectorWeb.get().removeAg( m_agent );
    }


    //@Override
    public final String getName()
    {
        return m_namepath.getPath();
    }

    @Override
    public final int getCycle()
    {
        return m_cycle;
    }

    @Override
    public final String getSource()
    {
        return new File( m_agent.getASLSrc() ).getName();
    }

    @Override
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        m_architecture.cycle( p_currentstep );
    }


    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void paint( final Graphics2D p_graphic, final Object p_object, final int p_width, final int p_height )
    {

    }

    @Override
    public final void receiveMessage( final Set<IMessage> p_messages )
    {
        m_receivedmessages.clear();
        m_receivedmessages.addAll( p_messages );
    }

    @Override
    public final CPath getReceiverPath()
    {
        return m_namepath;
    }


    /**
     * class to create an own agent architecture to define the reasoning cycle one agent uses one agent architecture
     *
     * @note Jason needs on the Agent.create call an instance of AgArch and not AgArchTier, so we need an own class to
     * create an own cycle call
     * @warning An AgArch is a linked-list of AgArchs, the agent name can read if an AgArch has got a successor only
     * (Jason code design error)
     * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
     */
    protected class CJasonArchitecture extends AgArch
    {

        @Override
        public final void act( final ActionExec p_action, final List<ActionExec> p_feedback )
        {
            final IAction l_action = m_action.get( p_action.getActionTerm().getFunctor() );
            if ( l_action != null )
                try
                {
                    l_action.act( m_agent, p_action.getActionTerm() );
                    p_action.setResult( true );
                }
                catch ( Exception l_exception )
                {
                    p_action.setFailureReason( ASSyntax.createAtom( "exception" ), l_exception.getMessage() );
                    p_action.setResult( false );
                }

            p_feedback.add( p_action );
        }

        @Override
        public final boolean canSleep()
        {
            return false;
        }

        @Override
        public final String getAgName()
        {
            return m_namepath.getPath( "_" );
        }

        @Override
        public final void sendMsg( final Message p_message ) throws Exception
        {
            p_message.setSender( getReceiverPath().toString() );
            m_participant.sendMessage( new CPath( p_message.getReceiver() ), new CMessage( p_message ) );
        }

        @Override
        public final void broadcast( final Message p_message ) throws Exception
        {
            final CPath l_path = new CPath( m_namepath );
            if ( m_namepath.size() > 0 )
                l_path.removeSuffix();

            p_message.setSender( m_namepath.toString() );
            p_message.setReceiver( l_path.toString() );
            m_participant.sendMessage( l_path, new CMessage( p_message ) );
        }

        /**
         * manual call of the reasoning cycle
         *
         * @param p_currentstep current step
         */
        public final void cycle( final int p_currentstep )
        {
            // add the simulationstep belief with the new number and remove the old one
            try
            {
                m_agent.addBel( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep ) ) );
                m_agent.delBel( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep - 1 ) ) );
            }
            catch ( Exception l_exception )
            {
            }

            // run belief updates
            this.updateBindBeliefs();
            this.updateMessageBeliefs();

            // the reasoning cycle must be called within the transition system
            this.setCycleNumber( m_cycle++ );
            this.getTS().reasoningCycle();
        }

        /**
         * updates all beliefs that are read from the message queue
         */
        protected final void updateMessageBeliefs()
        {
            for ( IMessage l_msg : m_receivedmessages )
                try
                {

                    // if message is a message from Jason internal message system
                    if ( l_msg instanceof CMessage )
                    {
                        final Message l_jmsg = ( (Message) l_msg.getData() );
                        final Literal l_literal = (Literal) l_jmsg.getPropCont();
                        l_literal.addAnnot( ASSyntax.createLiteral( "source", ASSyntax.createAtom( l_jmsg.getSender() ) ) );

                        if ( l_jmsg.isTell() )
                            m_agent.addBel( l_literal );
                        if ( l_jmsg.isUnTell() )
                            m_agent.delBel( l_literal );
                        if ( l_jmsg.isKnownPerformative() )
                        {
                            l_literal.addAnnot( BeliefBase.TPercept );
                            this.getTS().getC().addEvent( new Event( new Trigger( Trigger.TEOperator.add, Trigger.TEType.belief, l_literal ), Intention.EmptyInt ) );
                        }

                        continue;
                    }

                    // otherwise message will direct converted
                    final Literal l_literal = CCommon.getLiteral( l_msg.getTitle(), l_msg.getData() );
                    l_literal.addAnnot( ASSyntax.createLiteral( "source", ASSyntax.createAtom( l_msg.getSource().toString() ) ) );
                    m_agent.addBel( l_literal );

                }
                catch ( Exception l_exception )
                {
                }
        }

        /**
         * updates all beliefs, that will read from the bind objects
         */
        protected final void updateBindBeliefs()
        {
            for ( IBelief l_item : m_beliefs )
            {
                // remove old belief within the agent
                for ( Literal l_literal : l_item.getLiterals() )
                    try
                    {
                        m_agent.delBel( l_literal );
                    }
                    catch ( Exception l_exception )
                    {
                    }

                // clear belief storage and update the entries
                l_item.clear();
                l_item.update();


                // set new belief into the agent
                for ( Literal l_literal : l_item.getLiterals() )
                    try
                    {
                        m_agent.addBel( l_literal );
                    }
                    catch ( Exception l_exception )
                    {
                    }
            }
        }

    }


    /**
     * class of an own Jason agent to handle Jason stdlib internal action includes
     *
     * @note we do the initialization process manually, because some internal actions are removed from the default
     * behaviour
     * @see http://jason.sourceforge.net/api/jason/asSemantics/TransitionSystem.html
     */
    protected class CJasonAgent extends Agent
    {

        /**
         * ctor - for building a "blank / empty" agent
         *
         * @param p_asl          ASL file
         * @param p_architecture architecture
         */
        public CJasonAgent( final File p_asl, final AgArch p_architecture ) throws JasonException
        {
            this.setTS( new TransitionSystem( this, null, null, p_architecture ) );
            this.setBB( new DefaultBeliefBase() );
            this.setPL( new PlanLibrary() );
            this.initDefaultFunctions();

            try
            {
                CReflection.getClassField( this.getClass(), "initialGoals" ).getSetter().invoke( this, new ArrayList<>() );
                CReflection.getClassField( this.getClass(), "initialBels" ).getSetter().invoke( this, new ArrayList<>() );

                // create internal actions map - reset the map and overwrite not useable actions with placeholder
                final Map<String, InternalAction> l_action = new HashMap<>();
                final CInternalEmpty l_empty13 = new CInternalEmpty( 1, 3 );

                l_action.put( "jason.stdlib.clone", new CInternalEmpty() );
                l_action.put( "jason.stdlib.wait", l_empty13 );
                l_action.put( "jason.stdlib.create_agent", l_empty13 );
                l_action.put( "jason.stdlib.kill_agent", new CInternalEmpty( 1, 1 ) );
                l_action.put( "jason.stdlib.stopMAS", new CInternalEmpty( 0, 0 ) );

                CReflection.getClassField( this.getClass(), "internalActions" ).getSetter().invoke( this, l_action );
            }
            catch ( Throwable l_throwable )
            {
                CLogger.error( l_throwable );
            }

            this.load( p_asl.toString() );
            MindInspectorWeb.get().registerAg( this );
        }

    }

}
