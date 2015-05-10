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
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.object.mas.jason.action.CMethodBind;
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
 * @bug check agent name / structure
 * @bug refactor ctor (reduce parameter)
 */
public class CCarJasonAgent extends CDefaultCar
{
    /**
     * bind name
     */
    private static final String s_bindname = "self";
    /**
     * list of forbidden names *
     */
    private static final Set<String> c_forbidden = new HashSet()
    {{
            add( "m_agents" );
            add( "m_graph" );
            add( "m_route" );
            add( "m_inspect" );
            add( "m_endreached" );
            add( "m_routeindex" );

            add( "release" );
            add( "paint" );
            add( "step" );
            add( "inspect" );
            add( "onClick" );
        }};
    /**
     * agent object *
     */
    private final Set<CAgent<CDefaultCar>> m_agents = new HashSet<>();
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CCarJasonAgent.super.inspect() );
        }};


    /**
     * ctor
     *
     * @param p_objectname name of the object within the simulation
     * @param p_agent ASL name
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
                p_objectname, new HashMap<String, String>()
        {{
                        put( "main", p_agent );
                    }}, p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability
        );
    }


    /**
     * @param p_objectname name of the object within the simulation
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @param p_agent map with bind and ASL name
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final String p_objectname, final Map<String, String> p_agent, final ArrayList<Pair<EdgeIteratorState, Integer>> p_route,
                           final int p_speed, final int p_maxspeed, final int p_acceleration, final int p_deceleration, final double p_lingerprobability
    ) throws JasonException
    {
        super( p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability );
        final CPath l_path = new CPath( "traffic", "car", CSimulation.getInstance().generateObjectName( p_objectname, this ) );
        for ( Map.Entry<String, String> l_item : p_agent.entrySet() )
            this.bind( l_path, l_item.getKey(), l_item.getValue() );
    }


    /**
     * binds an agent with the name
     *
     * @param p_objectname name of the object (for message system)
     * @param p_bind bind name
     * @param p_asl ASL name
     * @throws JasonException
     */
    private void bind( final CPath p_objectname, final String p_bind, final String p_asl ) throws JasonException
    {

        final CAgent<CDefaultCar> l_agent = new CAgent<>( p_objectname, p_asl );

        // add the belief bind to the agent
        final de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind l_belief = new de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind(
                s_bindname, this, c_forbidden
        );
        l_agent.getBelief().add( l_belief );

        // add the method bind to the agent
        final CMethodBind l_method = new CMethodBind( s_bindname, this );
        l_method.getForbidden( s_bindname ).addAll( c_forbidden );
        l_agent.getActions().put( "invoke", l_method );

        // add the set bind to the agent
        final de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind l_set = new de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind(
                s_bindname, this, c_forbidden
        );
        l_agent.getActions().put( "set", l_set );

        m_inspect.put( CCommon.getResourceString( this, "asl", p_bind ), l_agent.getSource() );
        m_inspect.put( CCommon.getResourceString( this, "agent", p_bind ), l_agent.getName() );

        // add agent to layer and internal set
        CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).add( l_agent );
        m_agents.add( l_agent );

    }

    @Override
    public final void release()
    {
        super.release();
        for ( CAgent<CDefaultCar> l_agent : m_agents )
        {
            l_agent.release();
            CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).remove( l_agent );
        }
    }

    @Override
    public final Map<String, Object> inspect()
    {
        return m_inspect;
    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        // check speed, because agent can modify the speed value and the value should always in range
        // [0,max-speed] other values are declared as final member
        m_speed = Math.min( Math.max( 0, m_speed ), m_maxspeed );
        super.step( p_currentstep, p_layer );
    }
}