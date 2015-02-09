/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CDefaultCar;
import de.tu_clausthal.in.mec.object.car.ICar;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.*;


/**
 * default source for generating cars with a visualization on the map * use a exponential distribution
 * (http://en.wikipedia.org/wiki/Exponential_distribution) to generate cars for avoiding traffic jam at the source
 *
 * @todo set mean & number of cars value with ctor
 */
public class CDefaultSourceFactory extends IDefaultSourceFactory
{

    /**
     * mean value of the distribution
     */
    protected double m_mean = 4;
    /**
     * random interface
     */
    protected ExponentialDistribution m_random = new ExponentialDistribution( 0.5 * m_mean );
    /**
     * integer values how many cars are generated in a step
     */
    protected int m_NumberCarsInStep = 1;


    /**
     * ctor that sets the geo position of the source
     *
     * @param p_position geo position object
     */
    public CDefaultSourceFactory( GeoPosition p_position )
    {
        super( p_position, Color.CYAN );
    }


    /**
     * ctor which sets the geo position of the source and the color
     *
     * @param p_position geoposition
     * @param p_color    color of the source
     */
    protected CDefaultSourceFactory( GeoPosition p_position, Color p_color )
    {
        super( p_position, p_color );
    }


    /**
     * ctor which sets the geo position of the source and the number of cars on a creation step
     *
     * @param p_position geoposition
     * @param p_number   number of cars
     * @param p_color    color of the source
     */
    protected CDefaultSourceFactory( GeoPosition p_position, int p_number, Color p_color )
    {
        super( p_position, p_color );
        m_NumberCarsInStep = p_number;
        if ( p_number < 1 )
            throw new IllegalArgumentException( "number must be greater than zero" );
    }


    /**
     * set the mean value
     *
     * @param p_mean mean value of exponential distribution
     */
    public void setMean( double p_mean )
    {
        m_mean = Math.abs( p_mean );
    }


    /**
     * set the number of cars that are generated on each step
     *
     * @param p_number number of cars
     */
    public void setCarNumber( int p_number )
    {
        m_NumberCarsInStep = Math.abs( p_number );
    }


    @Override
    public Collection<ICar> step( int p_currentstep, ILayer p_layer )
    {
        Collection<ICar> l_sources = new HashSet();
        if ( m_random.sample() < m_mean )
            return l_sources;

        for ( int i = 0; i < m_NumberCarsInStep; i++ )
            l_sources.add( new CDefaultCar( m_position ) );

        return l_sources;
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

}
