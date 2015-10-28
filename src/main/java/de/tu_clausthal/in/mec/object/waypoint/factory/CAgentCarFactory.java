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

package de.tu_clausthal.in.mec.object.waypoint.factory;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.object.mas.EAgentLanguages;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * class to generate Jason cars with a distribution of the attributes
 */
public final class CAgentCarFactory extends CDefaultCarFactory
{
    /**
     * name of the agent
     */
    private final String m_agent;
    /**
     * agent type
     */
    private final EAgentLanguages m_agenttype;
    /**
     * inspect data
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
        putAll( CAgentCarFactory.super.inspect() );
    }};

    /**
     * ctor
     *
     * @param p_speedfactor [0,1] initial speed factor of max speed
     * @param p_maxspeed distribution of max-speed
     * @param p_acceleration distribution of acceleration
     * @param p_deceleration distribution of deceleration
     * @param p_lingerdistribution distribution of linger-probability
     * @param p_weight routing weight
     * @param p_agent agent name
     * @param p_agenttype agent type definition
     */
    public CAgentCarFactory( final Double p_speedfactor, final AbstractRealDistribution p_maxspeed,
            final AbstractRealDistribution p_acceleration, final AbstractRealDistribution p_deceleration, final AbstractRealDistribution p_lingerdistribution,
            final CGraphHopper.EWeight p_weight,
            final String p_agent, final EAgentLanguages p_agenttype
    )
    {
        super( p_speedfactor, p_maxspeed, p_acceleration, p_deceleration, p_lingerdistribution, p_weight );

        if ( ( p_agent == null ) || ( p_agent.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "agentnotnull" ) );
        m_agent = p_agent;
        m_agenttype = p_agenttype;
        m_inspect.put( CCommon.getResourceString( CAgentCarFactory.class, "factoryagent" ), m_agent );
        m_inspect.put( CCommon.getResourceString( CAgentCarFactory.class, "factoryagenttype" ), m_agenttype );
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

    @Override
    protected ICar getCar( final ArrayList<Pair<EdgeIteratorState, Integer>> p_cells )
    {
        try
        {
            final int l_maxspeed = (int) m_maxspeed.sample();

            switch ( m_agenttype )
            {
                case Jason:

                    return new de.tu_clausthal.in.mec.object.car.CCarJasonAgent(
                            p_cells,
                            (int) ( l_maxspeed * m_speedfactor ),
                            l_maxspeed,
                            (int) m_acceleration.sample(),
                            (int) m_deceleration.sample(),
                            m_lingerdistribution.sample(),
                            "agentcar %inc%",
                            m_agent
                    );

                default:
            }
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
        return null;
    }

}
