/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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
public class CDefaultCarFactory extends ICarFactory
{
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
     * max-speed distribution *
     */
    protected final AbstractRealDistribution m_maxspeed;
    /**
     * speed distribution
     */
    protected final Double m_speedfactor;
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CDefaultCarFactory.super.inspect() );
        }};

    /**
     * ctor
     *
     * @param p_speedfactor [0,1] initial speed factor of max speed
     * @param p_maxspeed distribution of max-speed
     * @param p_acceleration distribution of acceleration
     * @param p_deceleration distribution of deceleration
     * @param p_lingerdistribution distribution of linger-probability
     */
    public CDefaultCarFactory( final Double p_speedfactor, final AbstractRealDistribution p_maxspeed,
            final AbstractRealDistribution p_acceleration, final AbstractRealDistribution p_deceleration, final AbstractRealDistribution p_lingerdistribution
    )
    {
        super();

        if ( ( p_speedfactor <= 0 ) || ( p_speedfactor > 1 ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "speedfactorrange", p_speedfactor ) );

        m_speedfactor = p_speedfactor;
        m_maxspeed = p_maxspeed;
        m_acceleration = p_acceleration;
        m_deceleration = p_deceleration;
        m_lingerdistribution = p_lingerdistribution;

        m_inspect.put( CCommon.getResourceString( CDefaultCarFactory.class, "speed" ), m_speedfactor );
        m_inspect.put( CCommon.getResourceString( CDefaultCarFactory.class, "maxspeed" ), m_maxspeed );
        m_inspect.put( CCommon.getResourceString( CDefaultCarFactory.class, "acceleration" ), m_acceleration );
        m_inspect.put( CCommon.getResourceString( CDefaultCarFactory.class, "deceleration" ), m_deceleration );
        m_inspect.put( CCommon.getResourceString( CDefaultCarFactory.class, "linger" ), m_lingerdistribution );
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
            return new de.tu_clausthal.in.mec.object.car.CDefaultCar(
                    p_cells,
                    (int) ( l_maxspeed * m_speedfactor ),
                    l_maxspeed,
                    (int) m_acceleration.sample(),
                    (int) m_deceleration.sample(),
                    m_lingerdistribution.sample()
            );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
        return null;
    }
}
