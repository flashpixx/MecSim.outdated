/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Marcel Spitzer (marcel.spitzer@tu-clausthal.de)             #
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * class for generating cars with a distribution of the attributes
 */
public class CDistributionDefaultCarFactory extends ICarFactory
{
    /**
     * speed distribution
     */
    protected final AbstractRealDistribution m_speed;
    /**
     * max-speed distribution *
     */
    protected final AbstractRealDistribution m_maxspeed;
    /**
     * acceleration *
     */
    protected final AbstractRealDistribution m_acceleration;
    /**
     * deceleration *
     */
    protected final AbstractRealDistribution m_deceleration;
    /**
     * max-speed distribution *
     */
    protected final AbstractRealDistribution m_lingerdistribution;
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CDistributionDefaultCarFactory.super.inspect() );
        }};


    /**
     * ctor
     *
     * @param p_speed distribution of speed
     * @param p_maxspeed distribution of max-speed
     * @param p_acceleration distribution of acceleration
     * @param p_deceleration distribution of deceleration
     * @param p_lingerdistribution distribution of linger-probability
     */
    public CDistributionDefaultCarFactory( final AbstractRealDistribution p_speed, final AbstractRealDistribution p_maxspeed,
            final AbstractRealDistribution p_acceleration, final AbstractRealDistribution p_deceleration, final AbstractRealDistribution p_lingerdistribution
    )
    {
        super();

        m_speed = p_speed;
        m_maxspeed = p_maxspeed;
        m_acceleration = p_acceleration;
        m_deceleration = p_deceleration;
        m_lingerdistribution = p_lingerdistribution;

        m_inspect.put( CCommon.getResourceString( CDistributionDefaultCarFactory.class, "speed" ), m_speed );
        m_inspect.put( CCommon.getResourceString( CDistributionDefaultCarFactory.class, "maxspeed" ), m_maxspeed );
        m_inspect.put( CCommon.getResourceString( CDistributionDefaultCarFactory.class, "acceleration" ), m_acceleration );
        m_inspect.put( CCommon.getResourceString( CDistributionDefaultCarFactory.class, "deceleration" ), m_deceleration );
        m_inspect.put( CCommon.getResourceString( CDistributionDefaultCarFactory.class, "linger" ), m_lingerdistribution );
    }

    @Override
    protected ICar getCar( final ArrayList<Pair<EdgeIteratorState, Integer>> p_cells )
    {
        try
        {
            return new de.tu_clausthal.in.mec.object.car.CDefaultCar(
                    p_cells, (int) m_speed.sample(), (int) m_maxspeed.sample(), (int) m_acceleration.sample(), (int) m_deceleration.sample(),
                    m_lingerdistribution.sample()
            );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
        return null;
    }


    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }
}
