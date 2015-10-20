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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


/**
 * generates a fixed number of cars
 */
public final class CConstant extends IInspectorDefault implements IGenerator
{
    /**
     * start step value
     */
    private final int m_startstep;
    /**
     * end step value
     */
    private final int m_endstep;
    /**
     * number of objects at each step
     */
    private final int m_stepobjects;
    /**
     * inspect data
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CConstant.super.inspect() );
        }};

    /**
     * ctor
     *
     * @param p_startstep start step
     * @param p_endstep end step
     * @param p_maxobjects maximum number of created objects
     */
    public CConstant( final int p_startstep, final int p_endstep, final int p_maxobjects )
    {
        if ( p_startstep >= p_endstep )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "startbegin " ) );

        final int l_duration = p_endstep - p_startstep;

        m_startstep = p_startstep;
        m_endstep = p_endstep;
        m_stepobjects = p_maxobjects / BigInteger.valueOf( Math.abs( p_maxobjects ) ).gcd( BigInteger.valueOf( l_duration ) ).intValue();

        if ( m_stepobjects * l_duration != p_maxobjects )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "notpossible" ) );

        m_inspect.put( CCommon.getResourceString( this, "startstep" ), m_startstep );
        m_inspect.put( CCommon.getResourceString( this, "endstep" ), m_endstep );
        m_inspect.put( CCommon.getResourceString( this, "objectstep" ), m_stepobjects );
    }


    @Override
    public int getCount( final int p_currentStep )
    {
        if ( ( p_currentStep >= m_startstep ) && ( p_currentStep <= m_endstep ) )
            return m_stepobjects;
        return 0;
    }


    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }
}
