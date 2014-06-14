/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
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
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;


/**
 * source for norm cars
 */
public class CNormSource extends CDefaultSource {

    /**
     * ctor set the position
     *
     * @param p_position geo position
     */
    public CNormSource(GeoPosition p_position) {
        super(p_position);
    }


    /**
     * ctor set positition and generate number
     *
     * @param p_position position
     * @param p_number   number of cars
     */
    public CNormSource(GeoPosition p_position, int p_number) {
        super(p_position, p_number);
    }


    @Override
    public Collection<ICar> generate(int p_currentstep) {
        Collection<ICar> l_sources = new HashSet();

        // use random number on care creation, to avoid traffic jam on the source
        for (int i = 0; i < super.m_NumberCarsInStep; i++)
            if (super.m_random.nextDouble() > 0.15)
                l_sources.add(new CNormCar(super.m_position));

        return l_sources;
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }
}
