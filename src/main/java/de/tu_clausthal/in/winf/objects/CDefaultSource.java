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

package de.tu_clausthal.in.winf.objects;

import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;


/**
 * default source for generating cars with a visualization on the map *
 */
public class CDefaultSource implements ICarSourceFactory {

    /**
     * position of the source within the map *
     */
    private GeoPosition m_position = null;
    /**
     * integer values how many cars are generated in a step *
     */
    private int m_NumberCarsInStep = 1;
    /**
     * random interface *
     */
    private Random m_random = new Random();


    /**
     * ctor that sets the geo position of the source
     *
     * @param p_position geo position object
     */
    public CDefaultSource(GeoPosition p_position) {
        m_position = p_position;
    }


    /**
     * ctor which sets the geo position of the source and the number of cars on a creation step
     *
     * @param p_position geoposition
     * @param p_number   number of cars
     */
    public CDefaultSource(GeoPosition p_position, int p_number) {
        m_position = p_position;
        m_NumberCarsInStep = p_number;
        if (p_number < 1)
            throw new IllegalArgumentException("number must be greater than zero");
    }


    @Override
    public void setNumberOfCars(int p_number) {
        if (p_number < 1)
            throw new IllegalArgumentException("number must be greater than zero");

        m_NumberCarsInStep = p_number;
    }


    @Override
    public Collection<ICar> generate() {
        Collection<ICar> l_sources = new HashSet();

        // use random number on care creation, to avoid traffic jam on the source
        for (int i = 0; i < m_NumberCarsInStep; i++)
            if (m_random.nextDouble() <= 0.35)
                l_sources.add(new CDefaultCar(m_position));

        return l_sources;
    }


    @Override
    public Color getColor() {
        return Color.BLUE;
    }


    @Override
    public GeoPosition getPosition() {
        return m_position;
    }


}
