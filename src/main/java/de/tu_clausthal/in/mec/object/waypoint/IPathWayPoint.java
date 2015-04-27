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

package de.tu_clausthal.in.mec.object.waypoint;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * waypoint class to describe a route
 */
public abstract class IPathWayPoint<T, P extends IFactory<T>, N extends IGenerator> extends IWayPoint<T, P, N>
{

    /**
     * adjacency list *
     */
    private final Map<IPathWayPoint<T, P, N>, Double> m_adjacency = new HashMap<>();


    /**
     * ctor
     *
     * @param p_position position
     */
    public IPathWayPoint( final GeoPosition p_position )
    {
        super( p_position );
    }


    /**
     * ctor
     *
     * @param p_position position
     * @param p_generator generator
     * @param p_factory factory
     */
    public IPathWayPoint( final GeoPosition p_position, final N p_generator, final P p_factory )
    {
        super( p_position, p_generator, p_factory );
    }


    public void addWayPoint( final IPathWayPoint<T, P, N> p_waypoint, final double p_weight )
    {
        if ( ( p_weight < 0 ) || ( p_weight > 1 ) )
            throw new IllegalArgumentException( CCommon.getResourceString( IPathWayPoint.class, "weightrange" ) );

        // change defined weights
        final double l_multiplier = 1 - p_weight;
        for ( Map.Entry<IPathWayPoint<T, P, N>, Double> l_item : m_adjacency.entrySet() )
            l_item.setValue( l_item.getValue() * l_multiplier );
        m_adjacency.put( p_waypoint, p_weight );
    }

    public void removeWayPoint( final IPathWayPoint<T, P, N> p_waypoint )
    {
        if ( !m_adjacency.containsKey( p_waypoint ) )
            throw new IllegalArgumentException( CCommon.getResourceString( IPathWayPoint.class, "notexists" ) );

        final double l_value = m_adjacency.remove( p_waypoint ) / m_adjacency.size();
        for ( Map.Entry<IPathWayPoint<T, P, N>, Double> l_item : m_adjacency.entrySet() )
            l_item.setValue( l_item.getValue() + l_value );
    }

    @Override
    public Collection<T> step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        return null;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return super.inspect();
    }

}
