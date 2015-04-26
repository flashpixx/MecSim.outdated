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

package de.tu_clausthal.in.mec.object.source.generator;


import org.apache.commons.math3.distribution.UniformRealDistribution;


/**
 * creates a time-uniform distribution generator
 */
public class CTimeUniformDistribution extends ITimeDistribution
{
    /**
     * ctor
     *
     * @param p_count number of objects
     * @param p_lower lower-bound value
     * @param p_upper upper-bound value
     */
    public CTimeUniformDistribution( final int p_count, final double p_lower, final double p_upper )
    {
        super( new UniformRealDistribution( p_lower, p_upper ), p_count );
    }
}
