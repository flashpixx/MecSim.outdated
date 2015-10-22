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

package de.tu_clausthal.in.mec.common;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;


/**
 * enum for distributions
 */
public enum EDistribution
{
    /**
     * normal distribution
     **/
    Normal(
            CCommon.getResourceString( EDistribution.class, "normaldistribution" ),
            CCommon.getResourceString( EDistribution.class, "normaldistributionleft" ),
            CCommon.getResourceString( EDistribution.class, "normaldistributionright" )
    ),
    /**
     * uniform distribution
     **/
    Uniform(
            CCommon.getResourceString( EDistribution.class, "uniformdistribution" ),
            CCommon.getResourceString( EDistribution.class, "uniformdistributionleft" ),
            CCommon.getResourceString( EDistribution.class, "uniformdistributionright" )
    ),
    /**
     * exponential distribution
     **/
    Exponential(
            CCommon.getResourceString( EDistribution.class, "exponentialdistribution" ),
            CCommon.getResourceString( EDistribution.class, "exponentialdistributionleft" )
    ),
    /**
     * beta distribution
     **/
    Beta(
            CCommon.getResourceString( EDistribution.class, "betadistribution" ),
            CCommon.getResourceString( EDistribution.class, "betadistributionleft" ),
            CCommon.getResourceString( EDistribution.class, "betadistributionright" )
    );


    /**
     * left / lower / first momentum description
     **/
    private final String m_firstmomentum;
    /**
     * right / higher / second momentum description
     **/
    private final String m_secondmomentum;
    /**
     * name of this distribution type
     */
    private final String m_text;

    /**
     * ctor
     *
     * @param p_text language depend name,
     * @param p_firstmomentum lower / first momentum language depend name
     */
    private EDistribution( final String p_text, final String p_firstmomentum )
    {
        this( p_text, p_firstmomentum, null );
    }

    /**
     * ctor
     *
     * @param p_text language depend name,
     * @param p_firstmomentum lower / first momentum language depend name
     * @param p_secondmomentum higher / second momentum language depend name
     */
    private EDistribution( final String p_text, final String p_firstmomentum, final String p_secondmomentum )
    {
        m_text = p_text;
        m_firstmomentum = p_firstmomentum;
        m_secondmomentum = p_secondmomentum;
    }

    /**
     * returns a distribution object
     *
     * @param p_firstmomentum lower / first momentum
     * @param p_secondmomentum higher / second momentum
     * @return distribution
     */
    public final AbstractRealDistribution get( final double p_firstmomentum, final double p_secondmomentum )
    {
        switch ( this )
        {
            case Uniform:
                return new UniformRealDistribution( p_firstmomentum, p_secondmomentum );

            case Normal:
                return new NormalDistribution( p_firstmomentum, p_secondmomentum );

            case Exponential:
                return new ExponentialDistribution( p_firstmomentum );

            case Beta:
                return new BetaDistribution( p_firstmomentum, p_secondmomentum );

            default:
                throw new IllegalStateException( CCommon.getResourceString( EDistribution.class, "unknowndistribution" ) );
        }
    }

    /**
     * returns the label of the first momentum
     *
     * @return label
     */
    public final String getFirstMomentum()
    {
        return m_firstmomentum;
    }

    /**
     * returns the label of the second momentum
     *
     * @return label
     */
    public final String getSecondMomentum()
    {
        return m_secondmomentum;
    }

    /**
     * returns bool value that a first momentum is used
     *
     * @return flag
     */
    public final boolean hasFirstMomentum()
    {
        return m_firstmomentum != null;
    }

    /**
     * returns bool value that a second momentum is used
     *
     * @return flag
     */
    public final boolean hasSecondMomentum()
    {
        return m_secondmomentum != null;
    }

    /**
     * returns the expection on a given distribution
     *
     * @param p_args numerical arguments
     * @return expectation
     */
    public final double getExpectation( final double... p_args )
    {
        switch ( this )
        {
            case Uniform:
                return ( p_args[0] + p_args[1] ) / 2;

            case Normal:
                return p_args[0];

            case Exponential:
                return 0;

            case Beta:
                return p_args[0] / ( p_args[0] + p_args[1] );

            default:
                throw new IllegalStateException( CCommon.getResourceString( EDistribution.class, "unknowndistribution" ) );

        }
    }

    @Override
    public String toString()
    {
        return m_text;
    }

}
