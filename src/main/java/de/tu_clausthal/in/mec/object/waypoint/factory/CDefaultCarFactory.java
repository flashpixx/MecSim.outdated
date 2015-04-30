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

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * class to generate default cars
 */
public class CDefaultCarFactory extends ICarFactory
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * inspect data
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            put( CCommon.getResourceString( IFactory.class, "factoryname" ), this );
        }};
    /**
     * reference to the graph
     */
    private final CGraphHopper m_graph = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).getGraph();

    @Override
    public Set<ICar> generate( final Collection<Pair<GeoPosition, GeoPosition>> p_waypoints, final int p_count )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_cells = this.generateRouteCells( p_waypoints );
        final Set<ICar> l_set = new HashSet<>();
        for ( int i = 0; i < p_count; i++ )
            try
            {
                l_set.add( new de.tu_clausthal.in.mec.object.car.CDefaultCar( l_cells ) );
            }
            catch ( final Exception l_exception )
            {
                CLogger.error( l_exception );
            }
        return l_set;
    }

    /**
     * creates the route cells
     *
     * @param p_waypoints waypoint pair list
     * @return cell list
     */
    protected final ArrayList<Pair<EdgeIteratorState, Integer>> generateRouteCells( final Collection<Pair<GeoPosition, GeoPosition>> p_waypoints )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_cells = new ArrayList<>();
        for ( Pair<GeoPosition, GeoPosition> l_point : p_waypoints )
        {
            final List<List<EdgeIteratorState>> l_route = m_graph.getRoutes( l_point.getLeft(), l_point.getRight(), 1 );
            if ( ( l_route != null ) && ( l_route.size() > 0 ) )
                l_cells.addAll( m_graph.getRouteCells( l_route.get( 0 ) ) );
        }

        return l_cells;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }
}
