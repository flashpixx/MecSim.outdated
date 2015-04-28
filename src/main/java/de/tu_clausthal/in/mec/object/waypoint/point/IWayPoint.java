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

package de.tu_clausthal.in.mec.object.waypoint.point;


import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.Serializable;
import java.util.Collection;


/**
 * interface of a waypoint
 */
public interface IWayPoint<T> extends IReturnSteppable<T>, Painter<COSMViewer>, Serializable
{

    /**
     * checks if a generator and factory exists
     *
     * @return boolean flag of existance
     */
    public boolean hasFactoryGenerator();

    /**
     * returns the position
     *
     * @return geoposition of the source
     */
    public GeoPosition getPosition();


    /**
     * returns a list of geoposition, which represent a path from this waypoint
     *
     * @return geoposition list
     */
    public Collection<GeoPosition> getPath();


    /**
     * returns all neighbor of this node
     *
     * @return neighbors of this node
     */
    public Collection<GeoPosition> getNeighbor();
}
