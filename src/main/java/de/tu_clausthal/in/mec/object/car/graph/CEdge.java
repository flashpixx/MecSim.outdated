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

package de.tu_clausthal.in.mec.object.car.graph;

import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * class for defining the cell sampling structure of an edge with the object information
 *
 * @tparam N type of the cell-object
 * @tparam T type of additional type
 */
public class CEdge<N, T> implements Comparable<CEdge>
{

    /**
     * map with object-2-position in forward direction
     */
    private final Map<N, Integer> m_objects = Collections.synchronizedMap( new HashMap<>() );
    /**
     * set with listener
     */
    private final Set<IAction<N, T>> m_listener = new HashSet<>();
    /**
     * edge ID
     */
    private final int m_edgeid;
    /**
     * length of the edge (distance)
     */
    private final double m_edgelength;
    /**
     * array with cells of the forward direction
     */
    private final N[] m_cells;
    /**
     * array with geopositions of the cell
     */
    private final GeoPosition[] m_cellgeoposition;
    /**
     * array with additional information
     */
    private final T[] m_additionalinformation;


    /**
     * ctor create the samples
     *
     * @param p_edgestate edge
     */
    public CEdge( final EdgeIteratorState p_edgestate )
    {
        m_edgeid = p_edgestate.getEdge();
        m_edgelength = p_edgestate.getDistance();


        final ArrayList<N> l_initlist = new ArrayList<>();
        for ( int i = 0; i < (int) Math.ceil(
                m_edgelength / CConfiguration.getInstance().get().<Integer>getTraverse(
                        "simulation/traffic/cellsampling"
                )
        ); i++
                )
            l_initlist.add( null );
        m_cells = (N[]) l_initlist.toArray();
        m_additionalinformation = (T[]) l_initlist.toArray();
        m_cellgeoposition = new GeoPosition[m_cells.length];

        // create a spline interpolation for cell sampling of the geoposition
        // get edge geoposition and convert data in arrays, run spline interpolation
        // catch number exceptions and calculate for each cell the geoposition
        final PointListArray l_list = this.filterPointList( p_edgestate.fetchWayGeometry( 2 ), 0.01 );

        try
        {

            final UnivariateInterpolator l_interpolator = l_list.size() < 3 ? new LinearInterpolator() : new SplineInterpolator();
            final UnivariateFunction l_function = l_interpolator.interpolate( l_list.getX(), l_list.getY() );
            final double l_increment = ( l_list.getX( l_list.size() - 1 ) - l_list.getX( 0 ) ) / m_cells.length;
            for ( int i = 0; i < m_cells.length; i++ )
                m_cellgeoposition[i] = new GeoPosition( l_list.getX( 0 ) + i * l_increment, l_function.value( l_list.getX( 0 ) + i * l_increment ) );

        }
        catch ( final NonMonotonicSequenceException l_exception )
        {

            final double l_xincrement = ( l_list.getX( l_list.size() - 1 ) - l_list.getX( 0 ) ) / m_cells.length;
            final double l_yincrement = ( l_list.getY( l_list.size() - 1 ) - l_list.getY( 0 ) ) / m_cells.length;
            for ( int i = 0; i < m_cells.length; i++ )
                m_cellgeoposition[i] = new GeoPosition( l_list.getX( 0 ) + i * l_xincrement, l_list.getY( 0 ) + i * l_yincrement );
        }
    }


    /**
     * filter point list to create a list of points which stores monoton increase points
     *
     * @param p_input input point list
     * @param p_epsilon epsilon value of the monotonic increase
     * @return point list array
     */
    private final PointListArray filterPointList( final PointList p_input, final double p_epsilon )
    {
        final ArrayList<Double> l_x = new ArrayList<>();
        final ArrayList<Double> l_y = new ArrayList<>();

        l_x.add( p_input.getLatitude( 0 ) );
        l_y.add( p_input.getLongitude( 0 ) );

        // convert point list to arrays and beware static increase
        for ( int i = 1; i < p_input.size() - 1; i++ )
            if ( ( Math.abs( l_x.get( l_x.size() - 1 ) - p_input.getLatitude( i ) ) >= p_epsilon ) && ( Math.abs(
                    l_y.get(
                            l_y.size() - 1
                    ) - p_input.getLongitude( i )
            ) >= p_epsilon ) )
            {
                l_x.add( p_input.getLatitude( i ) );
                l_y.add( p_input.getLongitude( i ) );
            }

        l_x.add( p_input.getLatitude( p_input.size() - 1 ) );
        l_y.add( p_input.getLongitude( p_input.size() - 1 ) );

        return new PointListArray( l_x, l_y );
    }


    /**
     * retuns an element of the additional array
     *
     * @return data element
     */
    public final T getAdditionalInformation( final int p_position )
    {
        return m_additionalinformation[p_position];
    }


    /**
     * sets an element into the additional array
     *
     * @param p_position position
     * @param p_object object
     */
    public final void setAdditionalInformation( final int p_position, final T p_object )
    {
        m_additionalinformation[p_position] = p_object;
    }


    /**
     * returns the edge ID
     *
     * @return ID
     */
    public final int getEdgeID()
    {
        return m_edgeid;
    }


    /**
     * returns the number of samples of the edge
     *
     * @return sample
     */
    public final int getEdgeCells()
    {
        return m_cells.length;
    }


    /**
     * returns the geoposition of an object
     *
     * @param p_object object
     * @return geoposition or null
     */
    public final GeoPosition getGeoposition( final N p_object )
    {
        final Integer l_position = m_objects.get( p_object );
        if ( l_position == null )
            return null;

        return m_cellgeoposition[l_position.intValue()];
    }


    /**
     * returns the number of objects on the current edge
     *
     * @return object number
     */
    public final int getNumberOfObjects()
    {
        return m_objects.size();
    }


    /**
     * checks if a position is empty
     *
     * @param p_position position index
     * @return empty for empty
     */
    public final boolean isEmpty( final int p_position )
    {
        return m_cells[p_position] == null;
    }


    /**
     * check if the edge is empty
     *
     * @return empty boolean
     */
    public final boolean isEmpty()
    {
        return m_objects.isEmpty();
    }


    /**
     * returns the position of an object
     *
     * @param p_object object
     * @return position
     */
    public final Integer getPosition( final N p_object )
    {
        return m_objects.get( p_object );
    }


    /**
     * returns the object on the position
     *
     * @param p_position position index
     * @return object or null
     */
    public final N getObject( final int p_position )
    {
        return m_cells[p_position];
    }


    /**
     * adds a set of edge listener
     *
     * @param p_listener listener collection
     */
    public final void addListener( final Collection<IAction<N, T>> p_listener )
    {
        m_listener.addAll( p_listener );
    }

    /**
     * adds an edge listener
     *
     * @param p_listener listener
     */
    public final void addListener( final IAction<N, T> p_listener )
    {
        m_listener.add( p_listener );
    }

    /**
     * removes an edge listener
     *
     * @param p_listener listener
     * @return remove bool flag
     */
    public final boolean removeListener( final IAction<N, T> p_listener )
    {
        return m_listener.remove( p_listener );
    }


    /**
     * removes a collection of edge listener
     *
     * @param p_listener listener collection
     * @return remove bool flag
     */
    public final boolean removeListener( final Collection<IAction<N, T>> p_listener )
    {
        return m_listener.removeAll( p_listener );
    }


    /**
     * sets an object on the edge position
     *
     * @param p_object object
     * @param p_position position index
     * @throws IllegalAccessException throws exception on emptyness
     */
    public final void setObject( final N p_object, final int p_position ) throws IllegalAccessException
    {
        if ( !this.isEmpty( p_position ) )
            throw new IllegalAccessException( CCommon.getResourceString( this, "emptyposition" ) );

        // if the object exists on the edge, it will be moved
        synchronized ( m_cells )
        {
            if ( m_objects.containsKey( p_object ) )
                m_cells[m_objects.get( p_object )] = null;

            m_cells[p_position] = p_object;
        }
        m_objects.put( p_object, p_position );

        // call listener
        for ( IAction l_action : m_listener )
            l_action.actionPerformed( this, p_position, p_object );
    }


    /**
     * removes an object of the edge
     *
     * @param p_object object
     */
    public final void removeObject( final N p_object )
    {
        if ( !m_objects.containsKey( p_object ) )
            return;

        synchronized ( m_cells )
        {
            m_cells[m_objects.remove( p_object )] = null;
        }
    }


    /**
     * checks if an object is on the edge
     *
     * @param p_object object
     * @return contains boolean
     */

    public final boolean contains( final N p_object )
    {
        return m_objects.containsKey( p_object );
    }


    /**
     * clears the edge information
     */
    public final void clear()
    {
        m_objects.clear();
        synchronized ( m_cells )
        {
            for ( int i = 0; i < m_cells.length; i++ )
                m_cells[i] = null;
        }
    }


    @Override
    public final int compareTo( final CEdge p_edgelink )
    {
        if ( m_edgeid > p_edgelink.m_edgeid )
            return 1;
        if ( m_edgeid < p_edgelink.m_edgeid )
            return -1;

        return 0;
    }

    @Override
    public final int hashCode()
    {
        return m_edgeid;
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        if ( p_object instanceof CEdge )
            return this.hashCode() == p_object.hashCode();

        return false;
    }

    /**
     * class for storing the interpolation data *
     */
    protected class PointListArray
    {

        /**
         * x values
         */
        private final double[] m_xpoints;
        /**
         * y values
         */
        private final double[] m_ypoints;


        /**
         * ctor creates from two arraylists the data structure
         *
         * @param p_xpoints list with x values
         * @param p_ypoints list with y values
         */
        public PointListArray( final ArrayList<Double> p_xpoints, final ArrayList<Double> p_ypoints ) throws IllegalArgumentException
        {

            if ( ( p_xpoints.size() != p_ypoints.size() ) || ( p_xpoints.size() < 2 ) )
                throw new IllegalArgumentException( CCommon.getResourceString( this, "pointerror" ) );

            m_xpoints = new double[p_xpoints.size()];
            m_ypoints = new double[p_ypoints.size()];
            for ( int i = 0; i < p_xpoints.size(); i++ )
            {
                m_xpoints[i] = p_xpoints.get( i );
                m_ypoints[i] = p_ypoints.get( i );
            }
        }


        /**
         * returns a double array with x values
         *
         * @return array
         */
        public final double[] getX()
        {
            return m_xpoints;
        }


        /**
         * returns one x value
         *
         * @param p_index index of the value
         * @return value
         */
        public final double getX( final int p_index )
        {
            return m_xpoints[p_index];
        }


        /**
         * returns a double array with x values
         *
         * @return array
         */
        public final double[] getY()
        {
            return m_ypoints;
        }


        /**
         * returns one x value
         *
         * @param p_index index of the value
         * @return value
         */
        public final double getY( final int p_index )
        {
            return m_ypoints[p_index];
        }


        /**
         * number of elements
         *
         * @return number of elements
         */
        public final int size()
        {
            return m_xpoints.length;
        }

    }

}
