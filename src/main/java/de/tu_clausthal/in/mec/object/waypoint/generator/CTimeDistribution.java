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

package de.tu_clausthal.in.mec.object.waypoint.generator;


import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.HashMap;
import java.util.Map;


/**
 * generator of a static number of objects with a time-exponential function
 */
public class CTimeDistribution extends IInspectorDefault implements IGenerator
{
    /**
     * number of objects *
     */
    private final int m_count;
    /**
     * distribution *
     */
    private final AbstractRealDistribution m_distribution;
    /**
     * inspect data
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CTimeDistribution.super.inspect() );
        }};

    /**
     * ctor
     *
     * @param p_distribution distribution object
     * @param p_count number of objects
     */
    public CTimeDistribution( final AbstractRealDistribution p_distribution, final int p_count )
    {
        m_count = p_count;
        m_distribution = p_distribution;
    }

    @Override
    public int getCount( final int p_currentStep )
    {
        if ( m_distribution == null )
            return 0;
        return m_distribution.sample() <= m_distribution.getNumericalMean() ? 0 : m_count;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }
}
