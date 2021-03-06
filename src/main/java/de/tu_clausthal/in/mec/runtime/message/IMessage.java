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

package de.tu_clausthal.in.mec.runtime.message;

import de.tu_clausthal.in.mec.common.CPath;


/**
 * message interface to define a event message
 *
 * @tparam T type of the message data
 */
public interface IMessage<T>
{

    /**
     * the data of the message
     *
     * @return data
     */
    T getData();

    /**
     * returns the source of the message
     */
    CPath getSource();

    /**
     * a name if the message
     *
     * @return name
     */
    String getTitle();

    /**
     * decrements the time-to-live value and returns the decrement value and on zero the message is discarded
     *
     * @return current value
     */
    int ttl();

}
