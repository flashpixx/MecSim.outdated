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

import de.tu_clausthal.in.mec.object.car.CDefaultCar;
import de.tu_clausthal.in.mec.object.car.ICar;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

public class CDefaultCarGenerator implements  IGenerator
{

    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     *Position of this Generator
     */
    protected GeoPosition m_position = null;
    /**
     * mean value of the distribution
     */
    protected double m_mean = 4;
    /**
     * random interface
     */
    protected ExponentialDistribution m_random = new ExponentialDistribution( 3 );
    /**
     * integer values how many cars are generated in a step
     */
    protected int m_NumberCarsInStep = 1;


    public CDefaultCarGenerator(GeoPosition p_position)
    {
        this.m_position = p_position;
    }

    @Override
    public String getName()
    {
        return "Default Car Generator";
    }

    @Override
    public Color getColor()
    {
        return Color.CYAN;
    }

    @Override
    public Collection<ICar> generate()
    {
        final Collection<ICar> l_sources = new HashSet<>();
        if ( m_random.sample() < m_mean )
            return l_sources;

        for ( int i = 0; i < m_NumberCarsInStep; i++ )
            l_sources.add( new CDefaultCar( m_position ) );

        return l_sources;
    }

}
