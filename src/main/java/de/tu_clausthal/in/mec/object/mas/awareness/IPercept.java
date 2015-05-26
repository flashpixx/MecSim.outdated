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


import java.util.Map;


/**
 * perceptable data
 */
public interface IPercept<T>
{

    /**
     * returns the data of the perceptable object
     *
     * @return data (key-value pair)
     */
    public Map<String, Object> get();

    /**
     * returns the normalized intensity of the object
     * (e.g. to define radio communication)
     *
     * @return intensity value in [0,1] (0 = not receivable,
     * 1 = always reveivable [infinity intensity])
     */
    public double getIntensity();

    /**
     * returns the position of the perceptable object
     *
     * @return position
     */
    public T getPosition();

}
