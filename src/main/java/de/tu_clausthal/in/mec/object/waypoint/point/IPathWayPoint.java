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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;


/**
 * waypoint class to describe a route
 * todo check if reflexive routes are a problem
 * todo do not allow editing chain while simulation is running
 */
public abstract class IPathWayPoint<T, P extends IFactory<T>, N extends IGenerator> extends IWayPointBase<T, P, N>
{

    /**
     * random interface
     */
    private final Random m_random = new Random();
    /**
     * makrov chain to calculate route
     */
    private final CMakrovChain<GeoPosition> m_makrovChain = new CMakrovChain<>();
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( IPathWayPoint.super.inspect() );
        }};


    /**
     * ctor
     *
     * @param p_position position
     * @param p_generator generator
     * @param p_factory factory
     */
    public IPathWayPoint( final GeoPosition p_position, final N p_generator, final P p_factory, final Color p_color, final String p_name )
    {
        super( p_position, p_generator, p_factory, p_color, p_name );
        m_inspect.put( CCommon.getResourceString( IRandomWayPoint.class, "radius" ), m_makrovChain );
    }

    @Override
    public Collection<Pair<GeoPosition, GeoPosition>> getPath()
    {
        final Collection<Pair<GeoPosition, GeoPosition>> l_path = new LinkedList<>();
        return l_path;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return super.inspect();
    }

    /**
     * generic class to create a markov chain
     * this class also provides a relative and absolute weighting
     *
     * @param <T>
     */
    public static class CMakrovChain<T> extends HashMap<T, Map<T, MutablePair<Double, Double>>>
    {
        /**
         * add node to makrov chain
         *
         * @param p_node
         */
        public final void addNode( final T p_node )
        {
            this.put( p_node, new HashMap<T, MutablePair<Double, Double>>() );
            updateRelativeWeighting();
        }

        /**
         * add edge to makrov chain
         *
         * @param p_start
         * @param p_end
         * @param p_value
         */
        public final void addEdge( final T p_start, final T p_end, final double p_value )
        {
            if ( p_value < 0 )
                return;

            //create node of does not exist
            if ( !this.containsKey( p_start ) )
                this.addNode( p_start );
            if ( !this.containsKey( p_end ) )
                this.addNode( p_end );

            this.get( p_start ).put( p_end, new MutablePair<>( p_value, p_value ) );
            this.updateRelativeWeighting();
        }

        /**
         * remove edge from makrov chain
         *
         * @param p_start
         * @param p_end
         */
        public final void removeEdge( final T p_start, final T p_end )
        {
            if ( this.containsKey( p_start ) )
                this.get( p_start ).remove( p_end );

            updateRelativeWeighting();
        }

        /**
         * remove all edges of a node
         *
         * @param p_node
         */
        public final void removeAllNodeEdge( final Object p_node )
        {
            for ( Map<T, MutablePair<Double, Double>> l_in : this.values() )
                if ( l_in.containsKey( p_node ) )
                    l_in.remove( p_node );

        }

        /**
         * update all relative weightings
         */
        public final void updateRelativeWeighting()
        {
            for ( Map<T, MutablePair<Double, Double>> l_in : this.values() )
                updateRelativeWeighting( l_in );
        }

        /**
         * update relative weighting
         *
         * @param p_edges
         */
        public final void updateRelativeWeighting( final Map<T, MutablePair<Double, Double>> p_edges )
        {
            //calculate the new sum
            double l_sum = 0.0;
            for ( MutablePair l_pair : p_edges.values() )
                l_sum += (double) l_pair.left;

            //update relative weighting
            for ( MutablePair l_pair : p_edges.values() )
                l_pair.right = (double) l_pair.left / l_sum;
        }

        @Override
        public String toString()
        {
            return this.toString();
        }

        @Override
        public Map<T, MutablePair<Double, Double>> put( final T p_key, final Map<T, MutablePair<Double, Double>> p_value )
        {
            Map<T, MutablePair<Double, Double>> l_result = super.put( p_key, p_value );
            updateRelativeWeighting();
            return l_result;
        }

        @Override
        public void putAll( final Map<? extends T, ? extends Map<T, MutablePair<Double, Double>>> p_map )
        {
            super.putAll( p_map );
            updateRelativeWeighting();
        }

        @Override
        public Map<T, MutablePair<Double, Double>> putIfAbsent( final T p_key, final Map<T, MutablePair<Double, Double>> p_value )
        {
            Map<T, MutablePair<Double, Double>> l_result = super.putIfAbsent( p_key, p_value );
            updateRelativeWeighting();
            return l_result;
        }

        @Override
        public Map<T, MutablePair<Double, Double>> remove( final Object p_key )
        {
            Map<T, MutablePair<Double, Double>> l_result = super.remove( p_key );
            removeAllNodeEdge( p_key );
            updateRelativeWeighting();
            return l_result;
        }

        @Override
        public boolean remove( final Object p_key, final Object p_value )
        {
            boolean l_result = super.remove( p_key, p_value );
            removeAllNodeEdge( p_key );
            updateRelativeWeighting();
            return l_result;
        }

        @Override
        public Map<T, MutablePair<Double, Double>> replace( final T p_key, final Map<T, MutablePair<Double, Double>> p_value )
        {
            Map<T, MutablePair<Double, Double>> l_result = super.replace( p_key, p_value );
            updateRelativeWeighting();
            return l_result;
        }

        @Override
        public boolean replace( final T p_key, final Map<T, MutablePair<Double, Double>> p_oldValue,
                final Map<T, MutablePair<Double, Double>> p_newValue )
        {
            boolean l_result = super.replace( p_key, p_oldValue, p_newValue );
            removeAllNodeEdge( p_key );
            updateRelativeWeighting();
            return l_result;
        }

        @Override
        public void replaceAll(
                final BiFunction<? super T, ? super Map<T, MutablePair<Double, Double>>, ? extends Map<T, MutablePair<Double, Double>>> p_function )
        {
            super.replaceAll( p_function );
            updateRelativeWeighting();
        }
    }

}
