/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.object.car;


import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.ICycle;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import jason.JasonException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * agent car
 *
 * @bug refactor ctor (reduce parameter)
 */
public class CCarJasonAgent extends CDefaultCar implements ICycle
{

    /**
     * agent object *
     */
    @CFieldFilter.CAgent( bind = false )
    private final Set<de.tu_clausthal.in.mec.object.mas.jason.CAgent> m_agents = new HashSet<>();
    /**
     * inspector map
     */
    @CFieldFilter.CAgent( bind = false )
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CCarJasonAgent.super.inspect() );
        }};


    /**
     * ctor
     *
     * @param p_objectname name of the object within the simulation
     * @param p_agent ASL / agent name
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final String p_objectname, final String p_agent, final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed,
            final int p_maxspeed, final int p_acceleration, final int p_deceleration, final double p_lingerprobability
    ) throws JasonException

    {
        this(
                p_objectname, new HashSet<String>()
                {{
                        add( p_agent );
                    }}, p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability
        );
    }


    /**
     * @param p_objectname name of the object within the simulation
     * @param p_agent set with ASL / agent name
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final String p_objectname, final Set<String> p_agent, final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed,
            final int p_maxspeed, final int p_acceleration, final int p_deceleration, final double p_lingerprobability
    ) throws JasonException
    {
        super( p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability );
        final CPath l_path = new CPath( "traffic", "car", CSimulation.getInstance().generateObjectName( p_objectname, this ) );
        for ( final String l_item : p_agent )
            this.bind( l_path, l_item );
    }


    /**
     * binds an agent with the name
     *
     * @param p_objectname name of the object (for message system)
     * @param p_asl ASL / agent name
     * @throws JasonException throws on Jason error
     */
    @CMethodFilter.CAgent( bind = false )
    private void bind( final CPath p_objectname, final String p_asl ) throws JasonException
    {
        final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent = new de.tu_clausthal.in.mec.object.mas.jason.CAgent(
                p_objectname.append( p_asl ), p_asl, this
        );
        m_inspect.put( CCommon.getResourceString( this, "agent", l_agent.getName() ), l_agent.getSource() );
        l_agent.registerCycle( this );

        // add agent to layer and internal set
        CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).add( l_agent );
        m_agents.add( l_agent );

    }

    @Override
    public void cycle( final int p_currentstep, final IAgent p_agent )
    {

    }

    /**
     * returns the predeccor with meter values
     *
     * @param p_count number of predecessors
     * @return distance and predecessor
     *
     * @todo should be represent with a sensor definition
     */
    private final Map<Double, ICar> getPredecessorDistance( final int p_count )
    {
        return new HashMap<Double, ICar>()
        {{
                for ( final Map.Entry<Integer, ICar> l_item : getPredecessor( p_count ).entrySet() )
                    put( m_graph.getCellDistanceToMeter( l_item.getKey() ), l_item.getValue() );
            }};
    }

    /**
     * returns the predeccor with meter values
     *
     * @return distance and predecessor
     *
     * @todo should be represent with a sensor definition
     */
    private final Map<Double, ICar> getPredecessorDistance()
    {
        return this.getPredecessorDistance( 1 );
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final Map<String, Object> inspect()
    {
        return m_inspect;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final void release()
    {
        super.release();
        for ( final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent : m_agents )
        {
            l_agent.release();
            CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).remove( l_agent );
        }
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        // check speed, because agent can modify the speed value and the value should always in range
        // [0,max-speed] other values are declared as final member
        m_speed = Math.min( Math.max( 0, m_speed ), m_maxspeed );
        super.step( p_currentstep, p_layer );
    }
}