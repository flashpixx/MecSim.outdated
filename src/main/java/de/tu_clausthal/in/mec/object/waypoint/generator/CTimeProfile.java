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


import java.util.Map;


/**
 * profile generator to define a fixed histogram on the time steps
 */
public class CTimeProfile implements IGenerator
{

    /**
     * profile histogram *
     */
    final int[] m_histogram;

    /**
     * ctor
     *
     * @param p_histogram array with histogram
     */
    public CTimeProfile( final int[] p_histogram )
    {
        m_histogram = p_histogram;
    }

    @Override
    public int getCount( final int p_currentStep )
    {
        return m_histogram[p_currentStep % m_histogram.length];
    }

    @Override
    public Map<String, Object> inspect()
    {
        return null;
    }
}
