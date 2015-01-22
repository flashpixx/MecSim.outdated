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

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import de.tu_clausthal.in.mec.simulation.event.IReceiver;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;


/**
 * class of a Jason agent architecture
 *
 * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
 */
public class CAgentArchitecture<T extends IStepable> extends AgArch implements IVoidStepable, Painter, IReceiver
{

    /**
     * literal storage of all agents literals *
     */
    protected CLiteralStorage m_literals = new CLiteralStorage();
    /**
     * map of all actions which can be called by the agents *
     */
    protected Map<String, Pair<Method, Object>> m_action = new HashMap();
    /**
     * number of agents *
     */
    protected int m_agents = 0;

    /**
     * source object that is connect with the agents
     */
    protected T m_source = null;

    /*
        http://jason.sourceforge.net/api/jason/asSemantics/Agent.html
        http://jason.sourceforge.net/api/jason/asSemantics/TransitionSystem.html

        Agent x = new Agent();
        x.initDefaultFunctions();

       	addBel(Literal bel)
        addInitialBel(Literal b)
        addInitialBelsInBB()
        addInitialGoal(Literal g)
        addInitialGoalsInTS()

        Agent create(AgArch arch, java.lang.String agClass, ClassParameters bbPars, java.lang.String asSrc, Settings stts)


        x.parseAS( new File( CConfiguration.getInstance().getMASDir() + File.separator + "test.asl") );
     */


    /**
     * ctor
     *
     * @param p_source source object of the agent
     */
    public CAgentArchitecture( T p_source )
    {
        super();
        if ( p_source == null )
            throw new IllegalArgumentException( "source value need not to be null" );

        m_source = p_source;
    }


    /**
     * creates an agent with it's name / ASL file
     *
     * @param p_asl filename of the ASL
     */
    public void createAgent( String p_asl ) throws JasonException
    {

        Agent.create( this, Agent.class.getName(), null, CConfiguration.getInstance().getMASDir( p_asl + ".asl" ).toString(), null );
        m_agents++;
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

    /**
     * returns the number of agents
     *
     * @return agent count
     */
    public int getAgentNumber()
    {
        return m_agents;
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        // the reasoning cycle must be called within the transition system
        this.getTS().reasoningCycle();
    }

    @Override
    public void act( ActionExec action, List<ActionExec> feedback )
    {
        super.act( action, feedback );
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

    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {
    }
}
