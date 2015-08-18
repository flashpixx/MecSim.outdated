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
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import de.tu_clausthal.in.mec.runtime.message.IReceiver;
import org.jxmapviewer.painter.Painter;


/**
 * interface of an agent
 *
 * @tparam T literal type
 */
public interface IAgent<T> extends Painter, IReceiver
{

    /**
     * returns the root beliefmask to get access
     * of all beliefs
     *
     * @return root belief mask
     */
    public IBeliefBaseMask<T> getBeliefBase();

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
     * register an object for running actions
     *
     * @param p_name binding name of the action
     * @param p_object binding object
     */
    public void registerAction( final String p_name, final Object p_object );

    /**
     * register a beliefbase mask
     *
     * @param p_path path in which the mask is added
     * @param p_mask mask
     */
    public void registerMask( final CPath p_path, final IBeliefBaseMask<T> p_mask );

    /**
     * release agent call *
     */
    void release();

    /**
     * unregister an action
     *
     * @param p_name binding name of the action
     */
    public void unregisterAction( final String p_name );

    /**
     * unregister a beliefbase mask
     *
     * @param p_path path of the mask (last element is the mask name)
     */
    public void unregisterMask( final CPath p_path );

}
