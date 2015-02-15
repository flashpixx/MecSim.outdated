/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.object.norm;

import de.tu_clausthal.in.mec.object.norm.institution.IInstitution;

import java.io.Serializable;


/**
 * defines a message to transfer a norm
 */
public interface INormMessage<T> extends Serializable
{


    /**
     * returns the norm
     *
     * @return norm object
     */
    public INorm<T> getNorm();


    /**
     * get owner institution
     *
     * @return institution object
     */
    public IInstitution<T> getInstitution();


    /**
     * returns the message type
     *
     * @return message type
     */
    public ENormMessageType getType();


    /**
     * hops to avoid infinity loops on update process (time-to-live)
     *
     * @return hop value (less or equal than zero equal ignore / remove norm)
     */
    public int getTTL();


    /**
     * TTL decrement (time-to-live)
     */
    public void decrementTTL();

}
