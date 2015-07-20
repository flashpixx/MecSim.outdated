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

/**
 * sensor interface to read data
 */
public interface ISensor<T>
{

    /**
     * returns the position of the sensor
     *
     * @return position
     */
    T getPosition();


    /**
     * check if an object is perceptable
     *
     * @param p_percept perceptable object
     * @return boolean
     */
    boolean isPerceptable( final IPercept<T> p_percept );


    /**
     * collects the perceptable data
     *
     * @param p_percept perception
     */
    void receive( final IPercept<T> p_percept );

}
