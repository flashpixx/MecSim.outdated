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

package de.tu_clausthal.in.mec.object.waypoint.factory;

import de.tu_clausthal.in.mec.ui.IInspector;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;


/**
 * interface which defines the generic structure of every factory (for sources)
 *
 * @tparam T any object type
 */
public interface IFactory<T> extends IInspector, Serializable
{

    /**
     * factory method to create objects
     *
     * @param p_positions geoposition list for routing calls
     * @param p_count number of objects
     * @return set with objects
     */
    public Set<T> generate( final Collection<GeoPosition> p_positions, final int p_count );

}
