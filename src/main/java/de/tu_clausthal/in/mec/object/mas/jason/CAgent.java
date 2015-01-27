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
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.architecture.MindInspectorWeb;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


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
     * literal storage of all agents literals *
     */
    protected CLiteralStorage m_literals = new CLiteralStorage();
    /**
     * map of all actions which can be called by the agents *
     */
    protected Map<String, Pair<Method, Object>> m_methods = new HashMap();
    /**
     * field list of object fields that converted to belief-base data
     */
    protected Map<String, Pair<Field, Object>> m_fields = new HashMap();
    /**
     * Jason interal agent architecture to run the reasoning cycle
     */
    protected CAgentArchitecture m_agentarchitecture = null;
    /**
     * Jason agent object
     */
    protected Agent m_agent = null;


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
    private String createName()
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

        this.addObjectFields( p_bind );
        //this.addObjectMethods( p_bind );

        // Jason code design error: the agent name is stored within the AgArch, but it can read if an AgArch has got an AgArch
        // successor (AgArchs are a linked list), so we insert a cyclic reference to the AgArch itself
        m_agentarchitecture = new CAgentArchitecture( p_name );
        m_agent = Agent.create( m_agentarchitecture, Agent.class.getName(), null, CEnvironment.getFilename( p_asl ).toString(), null );
        m_agentarchitecture.insertAgArch( m_agentarchitecture );

        // register the agent on the web mindinspector (DoS threat)
        MindInspectorWeb.get().registerAg( m_agent );
    }


    @Override
    public void release()
    {
        MindInspectorWeb.get().removeAg( m_agent );
    }


    @Override
    public void addObjectFields( Object p_object )
    {
        m_fields.putAll( m_literals.addObjectFields( p_object ) );
    }

    @Override
    public void addObjectMethods( Object p_object )
    {
        m_methods.putAll( m_literals.addObjectMethods( p_object ) );
    }

    @Override
    public String getName()
    {
        return m_agentarchitecture.getAgName();
    }


    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {

    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        for ( Literal l_literal : m_literals.get() )
            m_agent.addBel( l_literal );

        m_agentarchitecture.cycle( p_currentstep );
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
    private class CAgentArchitecture extends AgArch
    {
        /**
         * agent name *
         */
        protected String m_agentname = null;

        /**
         * ctor for setting agent name
         *
         * @param p_name name
         */
        public CAgentArchitecture( String p_name )
        {
            m_agentname = p_name;
        }

        @Override
        public boolean canSleep()
        {
            return false;
        }

        @Override
        public String getAgName()
        {
            return m_agentname;
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
