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

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IVoidAgent;
import de.tu_clausthal.in.mec.object.mas.jason.actions.CPushBack;
import de.tu_clausthal.in.mec.object.mas.jason.actions.IAction;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;

import java.awt.*;
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
    protected CArchitecture m_architecture = null;
    /**
     * Jason agent object
     */
    protected Agent m_agent = null;
    /**
     * set with build-in actions of this implementation *
     */
    protected Set<IAction> m_buildinaction = new HashSet();
    /**
     * external user actions e.g. environment based *
     */
    protected Set<IAction> m_useraction = new HashSet();
    /**
     * name of the agent *
     */
    protected String m_name = null;


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
            m_buildinaction.add( new CPushBack<T>( p_bind ) );


        // Jason code design error: the agent name is stored within the AgArch, but it can read if an AgArch has got an AgArch
        // successor (AgArchs are a linked list), so we insert a cyclic reference to the AgArch itself
        m_architecture = new CArchitecture();
        m_architecture.insertAgArch( m_architecture );

        // @todo build agent self, beause we would like to modify the internal actions
        m_agent = Agent.create( m_architecture, Agent.class.getName(), null, CEnvironment.getFilename( p_asl ).toString(), null );
    }


    @Override
    public void release()
    {
        // remove agent from the mindinspector
        m_agent.stopAg();
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
     * @warn An AgArch is a linked list of AgArchs, the agent name can read if an AgArch has got a successor only (Jason
     * code design error)
     */
    private class CArchitecture extends AgArch
    {

        @Override
        public void act( ActionExec action, List<ActionExec> feedback )
        {
            if ( this.runActions( m_buildinaction, action, feedback ) )
                return;

            if ( this.runActions( m_useraction, action, feedback ) )
                return;
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

        /**
         * runs a set of actions
         *
         * @param p_actions action set
         * @param action    action that should be run
         * @param feedback  feedback
         * @return action is run
         */
        protected boolean runActions( Set<IAction> p_actions, ActionExec action, List<ActionExec> feedback )
        {
            for ( IAction l_action : p_actions )
                if ( l_action.getName().equals( action.getActionTerm().getFunctor() ) )
                    try
                    {
                        l_action.act( action );
                        feedback.add( action );
                        return true;
                    }
                    catch ( Exception l_exception )
                    {
                        // @todo create error
                    }
            return false;
        }

        /**
         * manual call of the reasoning cycle
         *
         * @param p_currentstep current step
         */
        public void cycle( int p_currentstep )
        {
            // the reasoning cycle must be called within the transition system
            this.setCycleNumber( p_currentstep );
            this.getTS().reasoningCycle();
        }

    }

}
