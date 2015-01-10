/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
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

package de.tu_clausthal.in.mec.simulation.event;

import java.util.UUID;

/**
 * interface of a participant
 */
public interface IParticipant
{

    /**
     * get the unique name of the participant
     *
     * @return name
     */
    public String getEventName();

    /**
     * get an unique ID of the participant
     *
     * @return ID
     */
    public UUID getEventID();

    /**
     * creates a message
     *
     * @param p_name name of the message
     * @param p_data data
     * @param <T>    data type
     * @return message object
     */
    public <T> IMessage<T> createMessage( String p_name, T p_data );


    /**
     * creates a message
     *
     * @param p_data data
     * @param <T>    data type
     * @return message object
     */
    public <T> IMessage<T> createMessage( T p_data );

    /**
     * sends a message
     *
     * @param p_receiver receiver of the message
     * @param p_message  input message
     */
    public void sendMessage( CParticipant p_receiver, IMessage p_message );

}
