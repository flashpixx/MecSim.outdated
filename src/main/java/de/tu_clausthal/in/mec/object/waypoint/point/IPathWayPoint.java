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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
     * default target
     */
    private static final GeoPosition m_defaultTarget = new GeoPosition( 51.80135377062704, 10.32871163482666 );

    /**
     * ctor
     *
     * @param p_position position
     * @param p_generator generator
     * @param p_factory factory
     * @param p_color color
     * @param p_name name
     */
    public IPathWayPoint( final GeoPosition p_position, final N p_generator, final P p_factory, final Color p_color, final String p_name )
    {
        super( p_position, p_generator, p_factory, p_color, p_name );
        m_makrovChain.addNode(this);
        m_inspect.put(CCommon.getResourceString(IRandomWayPoint.class, "radius"), m_makrovChain);
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
        HashSet<Pair<GeoPosition, GeoPosition>> l_path = new HashSet<>();
        l_path.add( new ImmutablePair<>( this.getPosition(), m_defaultTarget ) );

        /**
         * @todo will be refactored in a few commits

        //inital position
        Map<IWayPoint, MutablePair<Double, Double>> l_currentNode = m_makrovChain.get( this );
        GeoPosition l_currentPosition = this.getPosition();
        GeoPosition l_default = new GeoPosition( 51.80135377062704, 10.32871163482666 );
        GeoPosition l_newPosition;

        //check for circles and path length
        for ( int i = 1; i < m_makrovChain.size(); i++ )
        {
            double l_random = m_random.nextDouble();

            // current node has no outgoing edges add a path to default target and return
            if ( l_currentNode.size() <= 0 )
            {
                CLogger.out( l_path.size() );
                l_path.add( new ImmutablePair<>( l_currentPosition, l_default ) );
                return l_path;
            }

            //calculate new edge
            for ( final Map.Entry<IWayPoint, MutablePair<Double, Double>> l_node : l_currentNode.entrySet() )
            {
                if ( l_random >= l_node.getValue().getRight() )
                {
                    l_random = l_random - l_node.getValue().getRight();
                }
                else
                {
                    l_newPosition = l_node.getKey().getPosition();
                    l_path.add( new ImmutablePair<>( l_currentPosition, l_newPosition ) );
                    l_currentNode = m_makrovChain.get( l_node.getKey() );
                    l_currentPosition = l_newPosition;
                    break;
                }
            }
        }
        **/

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
    public static class CMakrovChain<T> extends HashMap<T, HashMap<T, double[]>>
    {
        @Override
        public HashMap<T, double[]> put( final T p_key, final HashMap<T, double[]> p_value )
        {
            final HashMap<T, double[]> l_result = super.put( p_key, p_value );
            this.updateRelativeWeighting();
            return l_result;
        }

        @Override
        public HashMap<T, double[]> remove( final Object p_key )
        {
            final HashMap<T, double[]> l_result = super.remove( p_key );
            this.updateRelativeWeighting();
            return l_result;
        }

        @Override
        public boolean remove( final Object p_key, final Object p_value )
        {
            final boolean l_result = super.remove( p_key, p_value );
            this.updateRelativeWeighting();
            return l_result;
        }

        /**
         * add node to makrov chain
         *
         * @param p_node
         */
        public final void addNode( final T p_node )
        {
            //only add node if node does not exist
            if( this.containsKey( p_node ) )
                return;

            this.put( p_node, new HashMap<T, double[]>() );
            this.updateRelativeWeighting();
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
            //create node if does not exist
            if ( !this.containsKey( p_start ) )
                this.addNode( p_start );
            if ( !this.containsKey( p_end ) )
                this.addNode( p_end );

            this.get( p_start ).put( p_end, new double[]{ p_value, p_value } );
            this.updateRelativeWeighting();
        }

        /**
         * remove node from makrov chain
         * also removes all connected edges
         *
         * @param p_node
         */
        public final void removeNode( final Object p_node )
        {
            //remove connected edges
            for ( final Map<T, double[]> l_edge : this.values() )
                if ( l_edge.containsKey( p_node ) )
                    l_edge.remove( p_node );

            //remove node
            if( this.containsKey( p_node ) )
                super.remove (p_node );

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
            if( this.containsKey( p_start ) )
                this.get( p_start ).remove( p_end );

            this.updateRelativeWeighting();
        }

        /**
         * update all relative weightings
         */
        private void updateRelativeWeighting()
        {

            for ( final Map<T, double[]> l_edge : this.values() )
            {
                //calculate sum
                double l_sum = 0.0;
                for ( final double[] l_weighting : l_edge.values() )
                        l_sum += l_weighting[0];

                //calculate relativ weighting
                for ( final double[] l_weighting : l_edge.values() )
                        l_weighting[1] = (double) l_weighting[0] / l_sum;
            }
        }
    }

}
