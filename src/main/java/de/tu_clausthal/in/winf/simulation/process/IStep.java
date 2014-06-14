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

package de.tu_clausthal.in.winf.simulation.process;

import de.tu_clausthal.in.winf.object.car.ICar;
import de.tu_clausthal.in.winf.object.source.ICarSourceFactory;


/**
 * interface for getting messages before / after each call step call
 *
 * @note each object can be called on different threads, so attend synchronization
 * @deprecated
 */
public interface IStep {

    /**
     * is called on before each step
     *
     * @param p_currentstep step number
     * @param p_sources     list with all sources
     * @param p_cars        list with all cars
     */
    public void before(int p_currentstep, ICarSourceFactory[] p_sources, ICar[] p_cars);


    /**
     * is called after each step
     *
     * @param p_currentstep step number
     * @param p_sources     list with all sources
     * @param p_cars        list with all cars
     */
    public void after(int p_currentstep, ICarSourceFactory[] p_sources, ICar[] p_cars);


}
