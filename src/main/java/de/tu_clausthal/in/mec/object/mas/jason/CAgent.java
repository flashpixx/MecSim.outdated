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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IAgentTemplateFactory;
import de.tu_clausthal.in.mec.object.mas.IVoidAgent;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.generic.IWorldAction;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.CBeliefMaskStorage;
import de.tu_clausthal.in.mec.object.mas.jason.action.CMethodBind;
import de.tu_clausthal.in.mec.object.mas.jason.action.CPropertyBind;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CBeliefBase;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CBindingStorage;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CMessageStorage;
import de.tu_clausthal.in.mec.runtime.benchmark.IBenchmark;
import de.tu_clausthal.in.mec.runtime.message.CParticipant;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.architecture.MindInspectorWeb;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Message;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * class of a Jason agent architecture
 *
 * @tparam T typ of binding objects
 */
@SuppressWarnings( "serial" )
public class CAgent<T> implements IVoidAgent<Literal>, IAgentTemplateFactory.ITask<Agent>
{
    /**
     * path seperator of agent name
     */
    private static final String c_agentnameseparator = "::";
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
     * bind name of the initial object
     */
    private static final String c_bindname = "self";
    /**
     * property bind
     **/
    private final CPropertyBind m_propertybind = new CPropertyBind();
    /**
     * method bind
     */
    private final CMethodBind m_methodbind = new CMethodBind();
    /**
     * set with actions of this implementation
     */
    private final Map<String, IWorldAction> m_action = new HashMap<String, IWorldAction>()
    {{
            put( m_propertybind.getName(), m_propertybind );
            put( m_methodbind.getName(), m_methodbind );
        }};
    /**
     * agent object
     */
    private final Agent m_agent;
    /**
     * Jason interal agent architecture to performtemplate the reasoning cycle
     */
    private final CJasonArchitecture m_architecture;
    /**
     * root beliefbase
     */
    private final IBeliefBaseMask<Literal> m_beliefbaserootmask;
    /**
     * cycle number of the agent - it need not to be equal to the simulation step (the cycle is the lifetime of the
     * agent)
     */
    private int m_cycle;
    /**
     * name of the agent
     */
    private final CPath m_namepath;
    /**
     * participant object
     */
    private final CParticipant m_participant;


    /**
     * ctor
     *
     * @param p_asl agent ASL file
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final String p_asl ) throws Exception
    {
        this( null, p_asl, null );
    }


    /**
     * ctor
     *
     * @param p_namepath name of the agent (full path)
     * @param p_asl agent ASL file
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final CPath p_namepath, final String p_asl ) throws Exception
    {
        this( p_namepath, p_asl, null );
    }


    /**
     * ctor
     *
     * @param p_asl agent ASL file
     * @param p_bind object that should be bind with the agent
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final String p_asl, final T p_bind ) throws Exception
    {
        this( null, p_asl, p_bind );
    }


    /**
     * ctor
     *
     * @param p_namepath name of the agent (full path)
     * @param p_asl agent ASL file
     * @param p_bind object that should be bind with the agent
     * @throws JasonException throws an Jason exception
     * @note a default behaviour is defined: the name of the agent is the Java object information (class name and object hash)
     * and all properties and methods will be bind to the agent with the source "self"
     */
    public CAgent( final CPath p_namepath, final String p_asl, final T p_bind ) throws Exception
    {
        m_namepath = ( p_namepath == null ) || ( p_namepath.isEmpty() ) ? new CPath( this.getClass().getSimpleName() + "@" + this.hashCode() ).setSeparator(
                c_agentnameseparator
        ) : p_namepath.setSeparator( c_agentnameseparator );


        // --- create beliefbase and agent architecture
        // Jason code design error: the agent name is stored within the AgArch, but it can read if an AgArch has got an AgArch
        // successor (AgArchs are a linked list), so we insert a cyclic reference to the AgArch itself,
        // beware that beliefbase must exists before agent ctor is called !
        m_architecture = new CJasonArchitecture();
        m_architecture.insertAgArch( m_architecture );

        m_participant = new CParticipant( this );
        m_beliefbaserootmask = new CBeliefBase( new CBeliefMaskStorage<>(), c_agentbeliefseparator ).createMask( c_beliefbaseroot.getSuffix() );
        m_agent = IEnvironment.AGENTTEMPLATEFACTORY.instantiate( IEnvironment.getAgentFile( p_asl ), this, m_architecture, m_beliefbaserootmask );


        // --- create beliefbase structure with tree structure
        m_beliefbaserootmask.add(
                new CBeliefBase(
                        new CMessageStorage( m_agent.getTS(), c_agentnameseparator ), c_agentbeliefseparator
                ).<IBeliefBaseMask<Literal>>createMask( c_beliefbasemessage.getSuffix() )
        );
        m_beliefbaserootmask.add(
                new CBeliefBase(
                        // not the order of replacing arguments
                        new CBindingStorage( c_beliefbindprefixreplace, c_agentbeliefseparator ), c_agentbeliefseparator
                ).<IBeliefBaseMask<Literal>>createMask( c_beliefbasebind.getSuffix() )
        );

        if ( p_bind != null )
        {
            this.bind( c_bindname, p_bind );
            m_beliefbaserootmask.getMask( c_beliefbasebind ).<CBindingStorage>getStorage().push( c_bindname, p_bind );
        }


        MindInspectorWeb.get().registerAg( m_agent );
    }


    /**
     * returns the set of actions
     *
     * @return action set
     */
    public final Map<String, IWorldAction> getActions()
    {
        return m_action;
    }


    @Override
    public IBeliefBaseMask<Literal> getBeliefBase()
    {
        return m_beliefbaserootmask;
    }


    @Override
    public final int getCycle()
    {
        return m_cycle;
    }


    @Override
    public final String getName()
    {
        return m_namepath.getPath();
    }


    @Override
    public final String getSource()
    {
        return new File( m_agent.getASLSrc() ).getName();
    }


    @Override
    public void bind( final String p_name, final Object p_object )
    {
        m_propertybind.push( p_name, p_object );
        m_methodbind.push( p_name, p_object );
    }


    @Override
    public void addBeliefBase( final CPath p_path, final IBeliefBaseMask<Literal> p_mask )
    {
        m_beliefbaserootmask.add( p_mask );
    }


    @Override
    public final void release()
    {
        m_agent.stopAg();
        m_participant.release();
        MindInspectorWeb.get().removeAg( m_agent );
    }


    @Override
    public void unbind( final String p_name )
    {
        m_propertybind.remove( p_name );
        m_methodbind.remove( p_name );
    }


    @Override
    public void removeBeliefBase( final CPath p_path )
    {
        m_beliefbaserootmask.remove( p_path );
    }


    @Override
    public final CPath getReceiverPath()
    {
        return m_namepath;
    }


    /**
     * pass messages to message containing beliefbase
     *
     * @param p_messages set of messages
     */
    @Override
    public final void receiveMessage( final Set<IMessage> p_messages )
    {
        m_beliefbaserootmask.getMask( c_beliefbasemessage ).<CMessageStorage>getStorage().receiveMessage( p_messages );
    }


    @Override
    public void paint( final Graphics2D p_graphic, final Object p_object, final int p_width, final int p_height )
    {

    }


    @Override
    @IBenchmark
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        m_architecture.cycle( p_currentstep );
    }


    @Override
    public void performtemplate( final Agent p_agent )
    {
        // the initial beliefs are stored within the beliefbase, so iterate over all
        // beliefs and create beliefbase tree structure (split functor, build beliefbase and masks)
        for ( final Literal l_literal : p_agent.getBB() )
        {
            //System.out.println( CPath.createSplitPath( c_agentbeliefseparator, l_literal.getFunctor() ));
        }
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
            final IWorldAction l_action = m_action.get( p_action.getActionTerm().getFunctor() );
            if ( l_action != null )
                try
                {
                    l_action.act( m_agent, p_action.getActionTerm() );
                    p_action.setResult( true );
                }
                catch ( final Exception l_exception )
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
            return m_namepath.getPath( c_agentnameseparator );
        }

        @Override
        public final void sendMsg( final Message p_message ) throws Exception
        {
            p_message.setSender( getReceiverPath().toString() );
            m_participant.sendMessage( new CPath( p_message.getReceiver().split( c_agentnameseparator ) ), new CMessage( p_message ) );
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
            m_beliefbaserootmask.update();

            // performtemplate agent reasoning cycle for deducing new beliefs
            // the reasoning cycle must be called within the transition system
            this.setCycleNumber( m_cycle++ );
            this.getTS().reasoningCycle();
        }

    }

}
