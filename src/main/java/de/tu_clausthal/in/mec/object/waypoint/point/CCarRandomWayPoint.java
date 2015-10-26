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

package de.tu_clausthal.in.mec.object.waypoint.point;

import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.waypoint.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppableTarget;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;


/**
 * class with default source implementation for cars
 *
 * @todo symbol painter should be moved to an own structure
 */
public final class CCarRandomWayPoint extends IRandomWayPoint<ICar, ICarFactory, IGenerator>
{
    /**
     * map with targets
     */
    private transient Collection<IReturnSteppableTarget<ICar>> m_target = new HashSet()
    {{
        add( CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ) );
    }};

    /**
     * ctor
     *
     * @param p_position geo position
     * @param p_generator generator object
     * @param p_factory factory object
     * @param p_radius radius
     * @param p_color color
     * @param p_name name
     */
    public CCarRandomWayPoint( final GeoPosition p_position, final IGenerator p_generator, final ICarFactory p_factory, final double p_radius,
            final Color p_color, final String p_name
    )
    {
        super( p_position, p_generator, p_factory, p_radius, p_color, p_name );
    }

    @Override
    public Collection<IReturnSteppableTarget<ICar>> getTargets()
    {
        return m_target;
    }

}
