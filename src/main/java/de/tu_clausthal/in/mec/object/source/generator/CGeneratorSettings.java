/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.object.source.generator;

import org.apache.commons.math3.distribution.*;

/**
 * Class which handles the Settings for the Generator
 */
public class CGeneratorSettings {

    /**
     * Member Variable which holds the specific Distribution which defines how many Cars should be processed
     */
    private AbstractRealDistribution m_distribution = new UniformRealDistribution(0, 10);


    /**
     * Set a Constant Distribution
     * @param p_const
     */
    public void setConstant(double p_const){
        m_distribution = new ConstantRealDistribution(p_const);
    }

    /**
     * Set a Uniform Distribution
     * @param p_lowerBound
     * @param p_upperBound
     */
    public void setUniform(double p_lowerBound, double p_upperBound){
        m_distribution = new UniformRealDistribution(p_lowerBound, p_upperBound);
    }

    /**
     * Set a Normal Distribution
     * @param p_mean
     * @param p_deviation
     */
    public void setNormal(double p_mean, double p_deviation){
        m_distribution = new NormalDistribution(p_mean, p_deviation);
    }

    /**
     * Set a Exponential Distribution
     * @param p_mean
     * @param p_deviation
     */
    public void setExpo(double p_mean, double p_deviation){
        m_distribution = new ExponentialDistribution(p_mean, p_deviation);
    }

    /**
     * Get the Number of Cars which should be generated
     * @return
     */
    public int getSample(){
        double l_value = m_distribution.sample();
        return (int) l_value;
    }

}
