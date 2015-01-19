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
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.*;
import jason.asSyntax.Literal;
import jason.runtime.Settings;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * class of a Jason agent
 *
 * @note encapsulate the Jason AgentArch structure to run more than one agent
 * @see http://jason.sourceforge.net/
 * http://jason.sourceforge.net/faq/faq.html#SECTION00030000000000000000
 */
public class CAgent<T extends IStepable> extends AgArch implements IVoidStepable, Painter
{

    /**
     * source object that is connect with the agents *
     */
    protected T m_source = null;
    protected Circumstance m_jsoncircumstance = new Circumstance();
    protected Settings m_jsonsettings = new Settings();
    protected Map<String, Agent> m_agents = new HashMap();


    /**
     * ctor
     *
     * @param p_source source object of the agent
     */
    public CAgent( T p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( "source value need not to be null" );

        m_source = p_source;
    }


    /**
     * adds a new agent to the architecture
     *
     * @param p_name    name of the agent
     * @param p_aslfile ASL file
     */
    public void createAgent( String p_name, String p_aslfile )
    {
        if ( m_agents.containsKey( p_name ) )
            throw new IllegalArgumentException( "agent [" + p_name + "] exists" );

        try
        {
            m_agents.put( p_name, new CJasonAgentWrapper( p_aslfile ) );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * removes an agent from the architecture
     *
     * @param p_name name
     */
    public void removeAgent( String p_name )
    {
        this.
        m_agents.remove( p_name );
    }


    @Override
    public Map<String, Object> analyse()
    {
        return m_source.analyse();
    }

    @Override
    public void paint( Graphics2D graphics2D, Object o, int i, int i1 )
    {

    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        List<Literal> l_localPercepts = new LinkedList();

        //l_localPercepts.add( ASSyntax.createLiteral( "layer", ASSyntax.crea ) ) );


        this.getTS().reasoningCycle();
    }

    @Override
    public boolean canSleep()
    {
        return false;
    }

    @Override
    public boolean isRunning()
    {
        return true;
    }


    /**
     * a class of a Jason agent *
     */
    protected class CJasonAgentWrapper extends Agent
    {

        public CJasonAgentWrapper( String p_aslfile ) throws JasonException
        {
            new TransitionSystem( this, m_jsoncircumstance, m_jsonsettings, CAgent.this );
            this.initAg( p_aslfile );
        }
    }

}
