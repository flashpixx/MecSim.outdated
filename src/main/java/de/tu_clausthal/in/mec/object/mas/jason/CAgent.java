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
import de.tu_clausthal.in.mec.simulation.IStepable;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import de.tu_clausthal.in.mec.simulation.event.IReceiver;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.painter.Painter;

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
public class CAgent<T extends IStepable> implements IVoidStepable, Painter, IReceiver
{

    /**
     * literal storage of all agents literals *
     */
    protected CLiteralStorage m_literals = new CLiteralStorage();
    /**
     * map of all actions which can be called by the agents *
     */
    protected Map<String, Pair<Method, Field>> m_action = new HashMap();

    protected CAgentArchitecture m_agentarchitecture = new CAgentArchitecture();

    protected Agent m_agent = null;

    /**
     * source object that is connect with the agents
     */
    protected T m_bind = null;


    public CAgent( String p_name ) throws JasonException
    {
        m_agent = Agent.create( m_agentarchitecture, Agent.class.getName(), null, CEnvironment.getFilename( p_name ).toString(), null );
    }

    /**
     * ctor
     *
     * @param p_name agent name (ASL file)
     * @param p_bind object that should be bind with the agent
     */
    public CAgent( String p_name, T p_bind ) throws JasonException
    {
        m_bind = p_bind;
        m_agent = Agent.create( m_agentarchitecture, Agent.class.getName(), null, CEnvironment.getFilename( p_name ).toString(), null );
    }


    public Agent getAgent()
    {
        return m_agent;
    }


    /**
     * returns the literal object of the agent
     *
     * @return literal object
     */
    public CLiteralStorage getLiteralStorage()
    {
        return m_literals;
    }

    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {

    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        m_literals.addObjectFields( m_bind );
        for ( Literal l_literal : m_literals.get() )
        {
            System.out.println( l_literal );
            m_agent.addBel( l_literal );
        }


        //m_agent.addBel( ASSyntax.createLiteral( "blubnum", ASSyntax.createNumber( 5 ) ) );
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
     * @note Jason needs on the Agent.create call an instance of AgArch and not AgArchTier, so we need an own class
     * to create an own cycle call
     */
    private class CAgentArchitecture extends AgArch
    {

        public void cycle( int p_currentstep )
        {
            // the reasoning cycle must be called within the transition system
            this.setCycleNumber( p_currentstep );
            this.getTS().reasoningCycle();
        }

    }
}
