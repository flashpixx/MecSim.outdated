/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

package de.tu_clausthal.in.mec.runtime;

import de.tu_clausthal.in.mec.object.ILayer;

import java.util.Collection;


/**
 * interface for a step call with a return argument
 */
public interface IReturnSteppable<T> extends ISteppable
{

    /**
     * returns the object which gets the data
     *
     * @return objects which gets the data
     */
    Collection<IReturnSteppableTarget<T>> getTargets();

    /**
     * step method with return argument
     *
     * @param p_currentstep current step value
     * @param p_layer layer on which is the object push or null
     * @return collection with step values
     */
    Collection<T> step( final int p_currentstep, final ILayer p_layer ) throws Exception;

}
