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

package de.tu_clausthal.in.mec.object.mas;


import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.runtime.message.IReceiver;
import org.jxmapviewer.painter.Painter;

import java.awt.peer.CanvasPeer;


/**
 * interface of an agent
 */
public interface IAgent extends Painter, IReceiver
{

    /**
     * adds a new belief to specified beliefbase
     *
     * @param p_path path of beliefbase with literals name as last element
     * @param p_data belief data
     */
    public void addLiteral(final String p_path, final Object p_data);


    /**
     * adds a new belief to specified beliefbase
     *
     * @param p_path path of beliefbase with literals name as last element
     * @param p_data belief data
     */
<<<<<<< HEAD
    void addLiteral(final String p_name, final Object p_data);
=======
    public void addLiteral(final CPath p_path, final Object p_data);
>>>>>>> temp-branch

    /**
     * returns the current cycle
     *
     * @return cycle number
     */
    int getCycle();

    /**
     * returns the name of the agent
     *
     * @return name of the agent
     */
    String getName();

    /**
     * returns the source / file of the agent
     *
     * @return source
     */
    String getSource();

    /**
     * register a cycle object
     *
     * @param p_cycle cycle object
     */
    void registerCycle(final ICycle p_cycle);

    /**
     * release agent call *
     */
    void release();

    /**
     * removes a belief from specified beliefbase
     *
     * @param p_path path to beliefbase with literals name as last element
     * @param p_data belief data
     */
    public void removeLiteral(final String p_path, final Object p_data);

    /**
     * removes a belief from specified beliefbase
     *
     * @param p_path path to beliefbase with literals name as last element
     * @param p_data belief data
     */
<<<<<<< HEAD
    void removeLiteral(final String p_name, final Object p_data);
=======
    public void removeLiteral(final CPath p_path, final Object p_data);
>>>>>>> temp-branch

    /**
     * unregister a cycle object
     *
     * @param p_cycle cycle object
     */
    void unregisterCycle(final ICycle p_cycle);

}
