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

package de.tu_clausthal.in.mec.object.source.factory;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.ICar;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * class to generate default cars
 */
public class CDefaultCarFactory extends ICarFactory
{
    /**
     * position where the cars should be created
     */
    private GeoPosition m_geoPosition;

    /**
     * ctor
     *
     * @param p_position position where the cars should be created
     */
    public CDefaultCarFactory( final GeoPosition p_position )
    {
        if ( p_position == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgeoposition" ) );

        this.m_geoPosition = p_position;
    }

    @Override
    public Color getColor()
    {
        return Color.CYAN;
    }

    @Override
    public Set<ICar> generate( final int p_count )
    {
        final Set<ICar> l_set = new HashSet<>();
        for ( int i = 0; i < p_count; i++ )
            l_set.add( new de.tu_clausthal.in.mec.object.car.CDefaultCar( m_geoPosition ) );
        return l_set;
    }

}
