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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;


/**
 * waypoint class to describe a route
 * todo check if reflexive routes are a problem
 * todo do not allow editing chain while simulation is running
 * todo implement getPath correctly
 */
public abstract class IPathWayPoint<T, P extends IFactory<T>, N extends IGenerator> extends IWayPointBase<T, P, N>
{

    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( IPathWayPoint.super.inspect() );
        }};
    /**
     * makrov chain to calculate route
     */
    private final CMakrovChain<IWayPoint> m_makrovChain = new CMakrovChain<>();
    /**
     * random interface
     */
    private final Random m_random = new Random();


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
        m_makrovChain.addNode( this );
        m_inspect.put( CCommon.getResourceString( IRandomWayPoint.class, "radius" ), m_makrovChain );
    }

    /**
     * get the makrov chain
     *
     * @return
     */
    public final CMakrovChain<IWayPoint> getMakrovChain()
    {
        return this.m_makrovChain;
    }

    @Override
    public Collection<Pair<GeoPosition, GeoPosition>> getPath()
    {
        return new HashSet<Pair<GeoPosition, GeoPosition>>()
        {{
              add( new ImmutablePair<GeoPosition, GeoPosition>( getPosition(), new GeoPosition( 51.80135377062704, 10.32871163482666 ) ) );
            }};
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

            //create node if does not exist
            if ( !this.containsKey( p_start ) )
                this.addNode( p_start );
            if ( !this.containsKey( p_end ) )
                this.addNode( p_end );

            this.get( p_start ).put( p_end, new MutablePair<>( p_value, p_value ) );
            this.updateRelativeWeighting();
        }

        /**
         * add node to makrov chain
         *
         * @param p_node
         */
        public final void addNode( final T p_node )
        {
            this.put( p_node, new HashMap<T, MutablePair<Double, Double>>() );
            this.updateRelativeWeighting();
        }

        @Override
        public Map<T, MutablePair<Double, Double>> put( final T p_key, final Map<T, MutablePair<Double, Double>> p_value )
        {
            final Map<T, MutablePair<Double, Double>> l_result = super.put( p_key, p_value );
            this.updateRelativeWeighting();
            return l_result;
        }

        @Override
        public Map<T, MutablePair<Double, Double>> remove( final Object p_key )
        {
            final Map<T, MutablePair<Double, Double>> l_result = super.remove( p_key );
            this.removeAllNodeEdge( p_key );
            this.updateRelativeWeighting();
            return l_result;
        }

        @Override
        public boolean remove( final Object p_key, final Object p_value )
        {
            final boolean l_result = super.remove( p_key, p_value );
            this.removeAllNodeEdge( p_key );
            this.updateRelativeWeighting();
            return l_result;
        }

        /**
         * remove all edges of a node
         *
         * @param p_node
         */
        public final void removeAllNodeEdge( final Object p_node )
        {
            for ( final Map<T, MutablePair<Double, Double>> l_in : this.values() )
                if ( l_in.containsKey( p_node ) )
                    l_in.remove( p_node );

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

            this.updateRelativeWeighting();
        }


        /**
         * update all relative weightings
         */
        private void updateRelativeWeighting()
        {
            for ( final Map<T, MutablePair<Double, Double>> l_edge : this.values() )
            {
                //calculate the new sum
                double l_sum = 0.0;
                for ( final MutablePair l_pair : l_edge.values() )
                    l_sum += (double) l_pair.left;

                //update relative weighting
                for ( final MutablePair l_pair : l_edge.values() )
                    l_pair.right = (double) l_pair.left / l_sum;
            }
        }

    }

}
