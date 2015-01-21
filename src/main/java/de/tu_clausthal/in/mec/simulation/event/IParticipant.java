/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.simulation.event;

import de.tu_clausthal.in.mec.common.CPath;


/**
 * interface of a participant of the event system
 */
public interface IParticipant extends IMessageReceiver
{

    /**
     * creates a message
     *
     * @param p_name name of the message
     * @param p_data data
     * @return message object
     */
    public <T> IMessage<T> createMessage( String p_name, T p_data );


    /**
     * creates a message
     *
     * @param p_data data
     * @return message object
     */
    public <T> IMessage<T> createMessage( T p_data );

    /**
     * sends a message
     *
     * @param p_path    message receiver path
     * @param p_message input message
     */
    public void sendMessage( CPath p_path, IMessage p_message );

}
