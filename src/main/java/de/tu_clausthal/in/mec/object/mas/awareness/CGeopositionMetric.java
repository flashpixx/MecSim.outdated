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

package de.tu_clausthal.in.mec.object.mas.awareness;

import org.jxmapviewer.viewer.GeoPosition;


/**
 * distance between geoposition in meter
 *
 * @see http://www.movable-type.co.uk/scripts/latlong.html
 */
public class CGeopositionMetric implements IMetric<GeoPosition>
{
    /**
     * earth radius
     */
    private static final double c_earthradius = 6378.1370;

    /**
     * radiant factor
     */
    private static final double c_radiant = Math.PI / 180;


    @Override
    public double getDistance( final ISensor<GeoPosition> p_sensor, final IPercept<GeoPosition> p_perceptable )
    {
        final double l_alpha = Math.pow( Math.sin( ( p_sensor.getPosition().getLatitude() - p_perceptable.getPosition().getLatitude() ) * c_radiant / 2 ), 2 ) +
                               Math.pow(
                                       Math.sin( ( p_sensor.getPosition().getLongitude() - p_perceptable.getPosition().getLongitude() ) * c_radiant / 2 ), 2
                               ) *
                               Math.cos( p_sensor.getPosition().getLatitude() ) * Math.cos( p_perceptable.getPosition().getLatitude() );

        return 1000 * c_earthradius * 2 * Math.atan2( Math.sqrt( l_alpha ), Math.sqrt( l_alpha ) );
    }
}
