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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IVoidAgent;
import de.tu_clausthal.in.mec.object.mas.jason.actions.*;
import de.tu_clausthal.in.mec.object.mas.jason.belief.IBelief;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.event.CParticipant;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.architecture.MindInspectorWeb;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.bb.DefaultBeliefBase;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


/**
 * class of a Jason agent architecture
 *
 * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
 * @see http://jason.sourceforge.net/api/jason/asSemantics/TransitionSystem.html
 * @see http://jason.sourceforge.net/api/jason/stdlib/package-summary.html
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
     * set with actions of this implementation *
     */
    protected Set<IAction> m_action = new HashSet();
    /**
     * set with belief binds
     */
    protected Set<IBelief> m_beliefs = new HashSet();
    /**
     * name of the agent *
     */
    protected String m_name = null;
    /**
     * participant object *
     */
    protected CParticipant m_participant = new CParticipant( this );


    /**
     * ctor
     *
     * @param p_name name of the agent
     * @param p_asl  agent ASL file
     */
    public CAgent( String p_name, String p_asl ) throws JasonException
    {
        this.initialize( p_name, p_asl, null );
    }


    /**
     * ctor
     *
     * @param p_name name of the agent
     * @param p_asl  agent ASL file
     * @param p_bind object that should be bind with the agent
     */
    public CAgent( String p_name, String p_asl, T p_bind ) throws JasonException
    {
        this.initialize( p_name, p_asl, p_bind );
    }

    /**
     * ctor
     *
     * @param p_asl agent ASL file
     */
    public CAgent( String p_asl ) throws JasonException
    {
        this.initialize( this.createName(), p_asl, null );
    }

    /**
     * ctor
     *
     * @param p_asl  agent ASL file
     * @param p_bind object that should be bind with the agent
     */
    public CAgent( String p_asl, T p_bind ) throws JasonException
    {
        this.initialize( this.createName(), p_asl, p_bind );
    }


    /**
     * returns an unique agent name
     *
     * @return string with name
     */
    protected String createName()
    {
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    /**
     * initialize the object data
     *
     * @param p_name agent name (ASL file)
     * @param p_bind object that should be bind with the agent
     */
    protected void initialize( String p_name, String p_asl, T p_bind ) throws JasonException
    {
        if ( ( p_name == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( "agent name need not to be empty" );

        m_name = p_name;
        if ( p_bind != null )
        {
            m_action.add( new de.tu_clausthal.in.mec.object.mas.jason.actions.CFieldBind( "self", p_bind ) );
            m_action.add( new CMethodBind( "self", p_bind ) );
            m_beliefs.add( new de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind( "self", p_bind ) );
        }

        CSimulation.getInstance().getEventManager().register( new CPath( m_name ), m_participant );

        // Jason code design error: the agent name is stored within the AgArch, but it can read if an AgArch has got an AgArch
        // successor (AgArchs are a linked list), so we insert a cyclic reference to the AgArch itself
        m_architecture = new CJasonArchitecture();
        m_architecture.insertAgArch( m_architecture );

        // build an own agent to handle manual internal actions
        m_agent = new CJasonAgent( IEnvironment.getAgentFile( p_asl ), m_architecture );
    }


    /**
     * returns the set of actions
     *
     * @return action set
     */
    public Set<IAction> getActions()
    {
        return m_action;
    }


    /**
     * returns a set of belief obejcts
     *
     * @return belief set
     */
    public Set<IBelief> getBelief()
    {
        return m_beliefs;
    }


    @Override
    public void release()
    {
        m_agent.stopAg();
        MindInspectorWeb.get().removeAg( m_agent );
        CSimulation.getInstance().getEventManager().unregister( new CPath( m_name ), m_participant );
    }


    @Override
    public String getName()
    {
        return m_name;
    }


    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {

    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        m_architecture.cycle( p_currentstep );
    }


    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void paint( Graphics2D graphics2D, Object o, int i, int i1 )
    {

    }


    /**
     * class to create an own agent architecture to define the reasoning cycle one agent uses one agent architecture
     *
     * @note Jason needs on the Agent.create call an instance of AgArch and not AgArchTier, so we need an own class to
     * create an own cycle call
     * @warn An AgArch is a linked-list of AgArchs, the agent name can read if an AgArch has got a successor only (Jason
     * code design error)
     */
    private class CJasonArchitecture extends AgArch
    {
        /**
         * cycle number *
         */
        protected int m_cycle = 0;

        @Override
        public void act( ActionExec action, List<ActionExec> feedback )
        {
            for ( IAction l_action : m_action )
                if ( ( l_action.getName() != null ) && ( l_action.getName().equals( action.getActionTerm().getFunctor() ) ) )
                {
                    try
                    {
                        l_action.act( action.getActionTerm() );
                        action.setResult( true );
                    }
                    catch ( Exception l_exception )
                    {
                        action.setFailureReason( ASSyntax.createAtom( "exception" ), l_exception.getMessage() );
                        action.setResult( false );
                    }

                    feedback.add( action );
                    return;
                }
        }

        @Override
        public boolean canSleep()
        {
            return false;
        }

        @Override
        public String getAgName()
        {
            return m_name;
        }

        @Override
        public void sendMsg( Message m ) throws Exception
        {
            m_participant.sendMessage( new CPath( m.getReceiver() ), new CMessage( m ) );
        }

        @Override
        /**
         * @todo bind to event messangener
         */
        public void broadcast( Message m ) throws Exception
        {
            super.broadcast( m );
        }

        /**
         * manual call of the reasoning cycle
         *
         * @param p_currentstep current step
         */
        public void cycle( int p_currentstep )
        {
            // update all beliefs
            m_agent.getBB().clear();
            try
            {
                m_agent.addBel( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep ) ) );
            }
            catch ( Exception l_exception )
            {
            }

            for ( IBelief l_item : m_beliefs )
            {
                l_item.clear();
                l_item.update();
                for ( Literal l_literal : l_item.getLiterals() )
                    try
                    {
                        m_agent.addBel( l_literal );
                    }
                    catch ( Exception l_exception )
                    {
                    }
            }

            // the reasoning cycle must be called within the transition system
            this.setCycleNumber( m_cycle++ );
            this.getTS().reasoningCycle();
        }

    }


    /**
     * class of an own Jason agent to handle Jason stdlib internal action includes
     *
     * @note we do the initialization process manually, because some internal actions are removed from the default
     * behaviour
     */
    private class CJasonAgent extends Agent
    {

        /**
         * ctor - for building a "blank / empty" agent
         *
         * @param p_asl          ASL file
         * @param p_architecture architecture
         */
        public CJasonAgent( File p_asl, AgArch p_architecture ) throws JasonException
        {
            this.setTS( new TransitionSystem( this, null, null, p_architecture ) );
            this.setBB( new DefaultBeliefBase() );
            this.setPL( new PlanLibrary() );
            this.initDefaultFunctions();

            try
            {
                CCommon.getClassField( super.getClass().getSuperclass(), "initialGoals" ).set( this, new ArrayList() );
                CCommon.getClassField( super.getClass().getSuperclass(), "initialBels" ).set( this, new ArrayList() );

                // create internal actions map - reset the map and overwrite not useable actions with placeholder
                Map<String, InternalAction> l_action = new HashMap();

                l_action.put( "jason.stdlib.clone", new CInternalEmpty() );
                l_action.put( "jason.stdlib.wait", new CInternalEmpty( 1, 3 ) );
                l_action.put( "jason.stdlib.create_agent", new CInternalEmpty( 1, 3 ) );
                l_action.put( "jason.stdlib.kill_agent", new CInternalEmpty( 1, 1 ) );
                l_action.put( "jason.stdlib.stopMAS", new CInternalEmpty( 0, 0 ) );

                CCommon.getClassField( super.getClass().getSuperclass(), "internalActions" ).set( this, l_action );

            }
            catch ( Exception l_exception )
            {
                CLogger.error( l_exception );
            }

            this.load( p_asl.toString() );
            MindInspectorWeb.get().registerAg( this );
        }

    }

}
