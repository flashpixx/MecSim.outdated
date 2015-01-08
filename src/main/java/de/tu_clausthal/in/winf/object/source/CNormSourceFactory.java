/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.object.source;

import de.tu_clausthal.in.winf.object.car.CNormCar;
import de.tu_clausthal.in.winf.object.car.ICar;
import de.tu_clausthal.in.winf.object.world.ILayer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;


/**
 * source for norm cars
 */
public class CNormSourceFactory extends CDefaultSourceFactory
{

    /**
     * ctor push the position
     *
     * @param p_position geo position
     */
    public CNormSourceFactory( GeoPosition p_position )
    {
        super( p_position );
    }


    /**
     * ctor push positition and generate number
     *
     * @param p_position position
     * @param p_number   number of cars
     */
    public CNormSourceFactory( GeoPosition p_position, int p_number )
    {
        super( p_position, p_number, Color.YELLOW );
    }


    @Override
    public Collection<ICar> step( int p_currentstep, ILayer p_layer )
    {
        Collection<ICar> l_sources = new HashSet();
        if ( m_random.sample() >= s_mean )
            return l_sources;

        for ( int i = 0; i < m_NumberCarsInStep; i++ )
            l_sources.add( new CNormCar( super.m_position ) );
        return l_sources;
    }

}
