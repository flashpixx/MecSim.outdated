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

package de.tu_clausthal.in.mec.object.waypoint.factory;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * class to create an car factory object
 */
public abstract class ICarFactory extends IInspectorDefault implements IFactory<ICar>
{
    /**
     * routing weight
     */
    protected final CGraphHopper.EWeight m_weight;

    /**
     * j
     * reference to the graph
     *
     * @bug reset on simulation loading
     */
    private final CGraphHopper m_graph = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).getGraph();

    /**
     * ctor
     */
    protected ICarFactory()
    {
        m_weight = CGraphHopper.EWeight.Default;
    }

    /**
     * ctor
     *
     * @param p_weight routing weight
     */
    protected ICarFactory( final CGraphHopper.EWeight p_weight )
    {
        m_weight = p_weight;
    }

    @Override
    public Set<ICar> generate( final Collection<Pair<GeoPosition, GeoPosition>> p_waypoints, final int p_count )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_cells = this.generateRouteCells( p_waypoints );
        return IntStream.range( 0, p_count )
                        .parallel()
                        .mapToObj( i -> this.getCar( l_cells ) )
                        .collect( Collectors.toCollection( () -> new HashSet<ICar>() ) );
    }

    /**
     * creates the route cells
     *
     * @param p_waypoints waypoint pair list
     * @return cell list
     *
     * @bug run in parallel with stream
     */
    protected final ArrayList<Pair<EdgeIteratorState, Integer>> generateRouteCells( final Collection<Pair<GeoPosition, GeoPosition>> p_waypoints )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_cells = new ArrayList<>();
        for ( final Pair<GeoPosition, GeoPosition> l_point : p_waypoints )
        {
            final List<List<EdgeIteratorState>> l_route = m_graph.getRoutes( l_point.getLeft(), l_point.getRight(), m_weight, 1 );
            if ( l_route.size() > 0 )
                l_cells.addAll( m_graph.getRouteCells( l_route.get( 0 ) ) );
        }

        return l_cells;
    }

    /**
     * generates one car
     *
     * @param p_cells cell list
     * @return car object
     */
    protected abstract ICar getCar( final ArrayList<Pair<EdgeIteratorState, Integer>> p_cells );

}
