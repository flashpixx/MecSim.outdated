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

package de.tu_clausthal.in.mec.object.car;


import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.generic.ILiteral;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.CBeliefBase;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.IOneTimeStorage;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CLiteral;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CMask;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import de.tu_clausthal.in.mec.runtime.message.IReceiver;
import jason.JasonException;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * agent car
 *
 * @note the agent gets the following traffic-beliefs during runtime:
 * @note predecessor that show the predecessor vehicles and can be manipulated with the belief seenpredecessors
 * @note start-/endposition defines the start and the end geo position of the route
 * @note currentposition defines the current geo position and the edge information e.g. name and length of the edge, and
 * the routesample shows the current route sampling information
 * @note inconsistency defines the inconsistency value of each agent
 * @note drived defines the history drived-time and -distance of the vehicle
 * @bug refactor ctor (reduce parameter)
 */
public class CCarJasonAgent extends CDefaultCar implements IReceiver
{
    /**
     * number of predecessors that can be seen by the agents
     */
    private int m_seenpredecessors = 2;
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
     * internal receiver path
     */
    @CFieldFilter.CAgent( bind = false )
    private final CPath m_objectpath;
    /**
     * agent traffic environment belief base
     */
    @CFieldFilter.CAgent( bind = false )
    private final IBeliefBase<Literal> m_trafficbeliefbase = new CBeliefBase<>( new CTrafficStorage() );
    /**
     * reference to the agent layer
     */
    @CFieldFilter.CAgent( bind = false )
    private final CCarJasonAgentLayer m_agentlayer = CSimulation.getInstance().getWorld().<CCarJasonAgentLayer>getTyped( "Jason Car Agents" );

    /**
     * ctor
     *
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @param p_objectname name of the object within the simulation
     * @param p_agent ASL / agent name     *
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed, final int p_maxspeed, final int p_acceleration,
            final int p_deceleration, final double p_lingerprobability, final String p_objectname, final String p_agent
    ) throws JasonException

    {
        this(
                p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability,
                p_objectname, new HashSet<String>()
                {{
                        add( p_agent );
                    }}
        );
    }

    /**
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @param p_objectname name of the object within the simulation
     * @param p_agent set with ASL / agent name
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed, final int p_maxspeed, final int p_acceleration,
            final int p_deceleration, final double p_lingerprobability, final String p_objectname, final Set<String> p_agent
    ) throws JasonException
    {
        super( p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability );
        m_objectpath = new CPath( "traffic", "car", CSimulation.getInstance().generateObjectName( p_objectname, this ) );
        for ( final String l_item : p_agent )
            this.bind( l_item );
    }

    @Override
    public CPath getReceiverPath()
    {
        return m_objectpath;
    }

    @Override
    public void receiveMessage( final Set<IMessage> p_messages )
    {
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
            m_agentlayer.remove( l_agent );
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

    /**
     * binds an agent with the name
     *
     * @param p_asl ASL / agent name
     * @throws JasonException throws on Jason error
     */
    @CMethodFilter.CAgent( bind = false )
    private void bind( final String p_asl ) throws JasonException
    {
        final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent = new de.tu_clausthal.in.mec.object.mas.jason.CAgent(
                m_objectpath.append( p_asl ), p_asl, this
        );
        m_inspect.put( CCommon.getResourceString( this, "agent", l_agent.getName() ), l_agent.getSource() );

        // add local beliefbase to the agent
        l_agent.getBeliefBase().add( new CMask( "mytraffic", m_trafficbeliefbase, "_" ) );

        // add agent to layer and internal set
        m_agentlayer.add( l_agent );
        m_agents.add( l_agent );
    }

    /**
     * returns the predecessor with name for communication
     *
     * @param p_count number of predecessors
     * @return map with distance and map with name and can communicate
     */
    private Map<Double, Map<String, Object>> getPredecessorWithName( final int p_count )
    {
        final Map<Double, Map<String, Object>> l_predecessor = new HashMap<>();

        for ( final Map.Entry<Double, ICar> l_item : this.getPredecessor( p_count ).entrySet() )
        {
            final ICar l_car = l_item.getValue();
            final boolean l_isagent = l_car instanceof CCarJasonAgent;

            l_predecessor.put(
                    l_item.getKey(),
                    CCommon.getMap(
                            "name", l_isagent ? ( (CCarJasonAgent) l_car ).getReceiverPath().toString() : l_car.toString(),
                            "isagent", l_isagent
                    )
            );
        }

        return l_predecessor;
    }


    /**
     * private class for definined a belief storage to use "traffic content information"
     */
    private class CTrafficStorage extends IOneTimeStorage<ILiteral<Literal>, IBeliefBaseMask<Literal>>
    {
        /**
         * label of the inconsistency belief
         **/
        private static final String c_label_inconsistency = "inconsistency";
        /**
         * label of the predecessor belief
         **/
        private static final String c_label_predecessor = "predecessor";
        /**
         * label of the current position belief
         */
        private static final String c_label_currentposition = "currentposition";
        /**
         * label of the start position belief
         */
        private static final String c_label_startposition = "startposition";
        /**
         * label of the end position belief
         */
        private static final String c_label_endposition = "endposition";
        /**
         * label of the drived-history
         */
        private static final String c_label_drivedhistory = "drived";
        /**
         * label of the drived-distance
         */
        private static final String c_label_driveddistance = "distance";
        /**
         * label of the drived-time
         */
        private static final String c_label_drivedtime = "time";
        /**
         * label of source annotation
         */
        private static final String c_label_source = "source";
        /**
         * label of route sampling information
         */
        private static final String c_label_route = "routesample";
        /**
         * label of route index belief
         */
        private static final String c_label_routeindex = "index";
        /**
         * label of route cell number / count
         */
        private static final String c_label_routesize = "size";
        /**
         * label of the allowed-speed belief
         */
        private static final String c_label_allowedspeed = "allowedspeed";
        /**
         * start position as literal
         */
        private final Literal m_startposition = this.getLiteralGeoposition( c_label_startposition, 0 ).addAnnots(
                de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION
        );

        @Override
        protected void updating()
        {
            // --- traffic values ---

            // predecessor
            this.add(
                    de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                            c_label_predecessor,
                            CCarJasonAgent.this.getPredecessorWithName( CCarJasonAgent.this.m_seenpredecessors )
                    )
            ).addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION );

            // start- / end-position
            this.add( m_startposition );
            this.add(
                    this.getLiteralGeoposition( c_label_endposition, m_route.size() - 1 ).addAnnots(
                            de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION
                    )
            );

            // current allowed speed
            this.add(
                    de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                            c_label_allowedspeed,
                            CCarJasonAgent.this.m_layer.getGraph().getEdgeSpeed( CCarJasonAgent.this.getEdge() )
                    )
            ).addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION );

            // current position
            this.add(
                    de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                            c_label_currentposition,
                            CCarJasonAgent.this.getGeoposition(),
                            CCarJasonAgent.this.getEdge(),
                            de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                                    c_label_route,
                                    de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( c_label_routeindex, CCarJasonAgent.this.m_routeindex ),
                                    de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( c_label_routesize, CCarJasonAgent.this.m_route.size() )
                            )
                    )
            ).addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION );

            // drived-history
            this.add(
                    de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                            c_label_drivedhistory,
                            de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                                    c_label_driveddistance,
                                    CCarJasonAgent.this.m_layer.getUnitConvert().getCellToKiloMeter( CCarJasonAgent.this.m_routeindex )
                            ),
                            de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                                    c_label_drivedtime,
                                    CCarJasonAgent.this.m_layer.getUnitConvert().getTimeInMinutes( m_agents.iterator().next().getCycle() )
                            )
                    )
            ).addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION );


            // --- agent inconsistency value ---
            // each agent knows the inconsistency value of the other agents within the same car (all binded agents within this object),
            // the name of the agent is get by the receiver and not bei the getName() call, because the getName() returns the full path of the agent
            for ( final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent : m_agents )
            {
                final Literal l_literal = this.add(
                        de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                                c_label_inconsistency, m_agentlayer.getInconsistencyValue( l_agent )
                        )
                );
                l_literal.addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.DEFAULTANNOTATION );
                l_literal.addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( c_label_source, l_agent.getReceiverPath().getSuffix() ) );
            }
        }

        /**
         * returns the geo position as literal
         *
         * @param p_name literal name
         * @param p_routeindex route index
         * @return literal
         */
        private Literal getLiteralGeoposition( final String p_name, final int p_routeindex )
        {
            final Pair<EdgeIteratorState, Integer> l_cell = CCarJasonAgent.this.m_route.get( p_routeindex );
            return de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                    p_name, CCarJasonAgent.this.m_layer.getGraph().getEdge( l_cell.getLeft() ).getGeoPositions( l_cell.getRight() )
            );
        }

        /**
         * adds an element to the storage
         *
         * @param p_literal literal
         * @return input literal
         */
        private Literal add( final Literal p_literal )
        {
            final Set<ILiteral<Literal>> l_elements = m_multielements.getOrDefault( p_literal.getFunctor(), new HashSet<>() );
            l_elements.add( new CLiteral( p_literal ) );
            m_multielements.put( p_literal.getFunctor(), l_elements );
            return p_literal;
        }

    }
}