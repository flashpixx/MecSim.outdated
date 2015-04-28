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

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * waypoint class to describe a route
 */
public abstract class IPathWayPoint<T, P extends IFactory<T>, N extends IGenerator> extends IWayPoint<T, P, N>
{

    /**
     * adjacency list *
     */
    private final CWeightMap m_adjacency = new CWeightMap();
    /**
     * random interface
     */
    private final Random m_random = new Random();


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

    /**
     * returns the adjacency map
     *
     * @return map
     */
    public Map<IPathWayPoint<T, P, N>, Double> getAdjacency()
    {
        return m_adjacency;
    }


    @Override
    public Collection<T> step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        final Collection<GeoPosition> l_route = new LinkedList<>();

        // calculate route points
        IPathWayPoint<T, P, N> l_node = this;
        while ( l_node != null )
        {
            l_route.add( l_node.getPosition() );
            l_node = l_node.m_adjacency.getNode( m_random.nextDouble() );
        }

        return m_factory.generate( l_route, m_generator.getCount( p_currentstep ) );
    }

    @Override
    public Map<String, Object> inspect()
    {
        return super.inspect();
    }


    /**
     * weight map with guarantee of normalized weights in [0,1]
     */
    public class CWeightMap implements Map<IPathWayPoint<T, P, N>, Double>
    {

        /**
         * map to resolve the buckets for checking *
         */
        final Map<ImmutablePair<Double, Double>, IPathWayPoint<T, P, N>> m_buckets = new HashMap<>();
        /**
         * max value *
         */
        private final double m_max;


        /**
         * ctor
         */
        public CWeightMap()
        {
            m_max = 1;
        }


        /**
         * ctor - set the maximum weight
         *
         * @param p_max weight
         */
        public CWeightMap( final double p_max )
        {
            m_max = p_max;
        }

        /**
         * returns the node depends on the weight
         *
         * @param p_value weight
         * @return node or null on empty list
         */
        public IPathWayPoint<T, P, N> getNode( final double p_value )
        {
            for ( Map.Entry<ImmutablePair<Double, Double>, IPathWayPoint<T, P, N>> l_item : m_buckets.entrySet() )
                if ( ( l_item.getKey().getLeft() <= p_value ) && ( p_value < l_item.getKey().getRight() ) )
                    return l_item.getValue();

            return null;
        }

        @Override
        public final int size()
        {
            return this.size();
        }

        @Override
        public final boolean isEmpty()
        {
            return this.isEmpty();
        }

        @Override
        public final boolean containsKey( final Object p_key )
        {
            return this.containsKey( p_key );
        }

        @Override
        public final boolean containsValue( final Object p_value )
        {
            return this.containsValue( p_value );
        }

        @Override
        public final Double get( final Object p_key )
        {
            return this.get( p_key );
        }

        @Override
        public final Double put( final IPathWayPoint<T, P, N> p_key, final Double p_value )
        {
            final double l_multiplier = m_max - p_value;
            for ( Map.Entry<IPathWayPoint<T, P, N>, Double> l_item : this.entrySet() )
                l_item.setValue( l_item.getValue() * l_multiplier );

            this.put( p_key, p_value );
            this.bucketupdate();

            return p_value;
        }

        @Override
        public Double remove( final Object p_key )
        {
            final double l_value = this.remove( p_key );

            final double l_multiplier = l_value / this.size();
            for ( Map.Entry<IPathWayPoint<T, P, N>, Double> l_item : this.entrySet() )
                l_item.setValue( l_item.getValue() + l_value );

            this.bucketupdate();
            return l_value;
        }

        @Override
        public void putAll( final Map<? extends IPathWayPoint<T, P, N>, ? extends Double> m )
        {
            this.putAll( m );
        }

        @Override
        public void clear()
        {
            this.clear();
        }

        @Override
        public Set<IPathWayPoint<T, P, N>> keySet()
        {
            return this.keySet();
        }

        @Override
        public Collection<Double> values()
        {
            return this.values();
        }

        @Override
        public Set<Entry<IPathWayPoint<T, P, N>, Double>> entrySet()
        {
            return this.entrySet();
        }

        /**
         * update the bucket map
         */
        private void bucketupdate()
        {
            double l_value = 0;
            m_buckets.clear();
            for ( Map.Entry<IPathWayPoint<T, P, N>, Double> l_item : this.entrySet() )
                m_buckets.put( new ImmutablePair<>( l_value, l_value += l_item.getValue() ), l_item.getKey() );
        }
    }

}
