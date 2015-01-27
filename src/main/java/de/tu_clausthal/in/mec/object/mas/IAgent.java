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

package de.tu_clausthal.in.mec.object.mas;


import de.tu_clausthal.in.mec.simulation.event.IReceiver;
import org.jxmapviewer.painter.Painter;


/**
 * interface of an agent
 */
public interface IAgent extends Painter, IReceiver
{

    /**
     * release agent call *
     */
    public void release();

    /**
     * adds all object fields to the agent, fields are converted on their data type
     *
     * @param p_object object
     */
    public void addObjectFields( Object p_object );


    /**
     * adds all object methods to the agent, methods are converted to internal call
     *
     * @param p_object object
     */
    public void addObjectMethods( Object p_object );


    /**
     * returns the name of the agent
     *
     * @return name of the agent
     */
    public String getName();

}
