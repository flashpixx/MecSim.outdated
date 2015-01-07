/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.object.car.graph;

import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import de.tu_clausthal.in.winf.CConfiguration;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * class for defining the cell sampling structure of an edge
 * with the car information
 */
public class CCellObjectLinkage<N, T> implements Comparable<CCellObjectLinkage>
{

    /**
     * edge ID *
     */
    protected int m_edgeid = 0;
    /**
     * length of the edge (distance) *
     */
    protected double m_edgelength = 0;
    /**
     * map with car-2-position in forward direction *
     */
    protected Map<N, Integer> m_objects = new ConcurrentHashMap();
    /**
     * array with cells of the forward direction *
     */
    protected N[] m_cells = null;
    /**
     * array with geopositions of the cell *
     */
    protected GeoPosition[] m_cellgeoposition = null;
    /**
     * array with additional information *
     */
    protected T[] m_additionalinformation = null;


    /**
     * ctor create the samples
     *
     * @param p_edgestate
     */
    public CCellObjectLinkage( EdgeIteratorState p_edgestate )
    {
        m_edgeid = p_edgestate.getEdge();
        m_edgelength = p_edgestate.getDistance();
        this.sampling( p_edgestate );
    }

    /**
     * creates the sampling of an edge
     *
     * @param p_edgestate edge state
     */
    private void sampling( EdgeIteratorState p_edgestate )
    {
        ArrayList<N> l_initlist = new ArrayList();
        for ( int i = 0; i < (int) Math.ceil( m_edgelength / CConfiguration.getInstance().get().CellSampling ); i++ )
            l_initlist.add( null );
        m_cells = (N[]) l_initlist.toArray();
        m_additionalinformation = (T[]) l_initlist.toArray();
        m_cellgeoposition = new GeoPosition[m_cells.length];

        // create a spline interpolation for cell sampling of the geoposition
        // get edge geoposition and convert data in arrays, run spline interpolation
        // catch number exceptions and calculate for each cell the geoposition
        PointListArray l_list = this.filterPointList( p_edgestate.fetchWayGeometry( 2 ), 0.01 );

        try
        {

            UnivariateInterpolator l_interpolator = l_list.size() < 3 ? new LinearInterpolator() : new SplineInterpolator();
            UnivariateFunction l_function = l_interpolator.interpolate( l_list.getX(), l_list.getY() );
            double l_increment = ( l_list.getX( l_list.size() - 1 ) - l_list.getX( 0 ) ) / m_cells.length;
            for ( int i = 0; i < m_cells.length; i++ )
                m_cellgeoposition[i] = new GeoPosition( l_list.getX( 0 ) + i * l_increment, l_function.value( l_list.getX( 0 ) + i * l_increment ) );

        }
        catch ( NonMonotonicSequenceException l_exception )
        {

            double l_xincrement = ( l_list.getX( l_list.size() - 1 ) - l_list.getX( 0 ) ) / m_cells.length;
            double l_yincrement = ( l_list.getY( l_list.size() - 1 ) - l_list.getY( 0 ) ) / m_cells.length;
            for ( int i = 0; i < m_cells.length; i++ )
                m_cellgeoposition[i] = new GeoPosition( l_list.getX( 0 ) + i * l_xincrement, l_list.getY( 0 ) + i * l_yincrement );
        }
    }


    /**
     * filter point list to create a list of points which stores monoton increase points
     *
     * @param p_input   input point list
     * @param p_epsilon epsilon value of the monotonic increase
     * @return point list array
     */
    private PointListArray filterPointList( PointList p_input, double p_epsilon )
    {
        ArrayList<Double> l_x = new ArrayList();
        ArrayList<Double> l_y = new ArrayList();

        l_x.add( p_input.getLatitude( 0 ) );
        l_y.add( p_input.getLongitude( 0 ) );

        // convert point list to arrays and beware static increase
        for ( int i = 1; i < p_input.size() - 1; i++ )
            if ( ( Math.abs( l_x.get( l_x.size() - 1 ) - p_input.getLatitude( i ) ) >= p_epsilon ) &&
                    ( Math.abs( l_y.get( l_y.size() - 1 ) - p_input.getLongitude( i ) ) >= p_epsilon ) )
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
    public T getAdditionalInformation( int p_position )
    {
        return m_additionalinformation[p_position];
    }


    /**
     * sets an element into the additional array
     *
     * @param p_position position
     * @param p_object   object
     */
    public void setAdditionalInformation( int p_position, T p_object )
    {
        m_additionalinformation[p_position] = p_object;
    }


    /**
     * returns the edge ID
     *
     * @return ID
     */
    public int getEdgeID()
    {
        return m_edgeid;
    }


    /**
     * returns the number of samples of the edge
     *
     * @return sample
     */
    public int getEdgeCells()
    {
        return m_cells.length;
    }


    /**
     * returns the geoposition of an object
     *
     * @param p_object object
     * @return geoposition or null
     */
    public GeoPosition getGeoposition( N p_object )
    {
        Integer l_position = m_objects.get( p_object );
        if ( l_position == null )
            return null;

        return m_cellgeoposition[l_position.intValue()];
    }


    /**
     * returns the number of objects on the current edge
     *
     * @return object number
     */
    public int getNumberOfObjects()
    {
        return m_objects.size();
    }


    /**
     * checks if a position is empty
     *
     * @param p_position position index
     * @return empty for empty
     */
    public boolean isEmpty( int p_position )
    {
        return m_cells[p_position] == null;
    }


    /**
     * check if the edge is empty
     *
     * @return empty boolean
     */
    public boolean isEmpty()
    {
        return m_objects.isEmpty();
    }


    /**
     * returns the position of an object
     *
     * @param p_object object
     * @return position
     */
    public Integer getPosition( N p_object )
    {
        return m_objects.get( p_object );
    }


    /**
     * returns the object on the position
     *
     * @param p_position position index
     * @return object or null
     */
    public N getObject( int p_position )
    {
        return m_cells[p_position];
    }

    /**
     * sets an object on the edge position
     *
     * @param p_object   object
     * @param p_position position index
     * @throws IllegalAccessException
     */
    public synchronized void setObject( N p_object, int p_position ) throws IllegalAccessException
    {
        if ( !this.isEmpty( p_position ) )
            throw new IllegalAccessException( "position is not empty" );
        if ( m_objects.containsKey( p_object ) )
            throw new IllegalAccessException( "object exists" );

        m_cells[p_position] = p_object;
        m_objects.put( p_object, p_position );
    }


    /**
     * removes an object of the edge
     *
     * @param p_object object
     */
    public synchronized void removeObject( N p_object )
    {
        if ( !m_objects.containsKey( p_object ) )
            return;

        m_cells[m_objects.get( p_object )] = null;
    }


    /**
     * checks if an object is on the edge
     *
     * @param p_object object
     * @return contains boolean
     */

    public synchronized boolean contains( N p_object )
    {
        return m_objects.containsKey( p_object );
    }


    /**
     * clears the edge information
     */
    public synchronized void clear()
    {
        m_objects.clear();
        for ( int i = 0; i < m_cells.length; i++ )
            m_cells[i] = null;

    }


    @Override
    public int compareTo( CCellObjectLinkage p_edgelink )
    {
        if ( m_edgeid > p_edgelink.m_edgeid )
            return 1;
        if ( m_edgeid < p_edgelink.m_edgeid )
            return -1;

        return 0;
    }


    @Override
    public boolean equals( Object p_object )
    {
        if ( ( p_object == null ) || ( !( p_object instanceof CCellObjectLinkage ) ) )
            return false;

        return this.m_edgeid == ( (CCellObjectLinkage) p_object ).m_edgeid;
    }

    @Override
    public int hashCode()
    {
        return m_edgeid;
    }

    /**
     * class for storing the interpolation data *
     */
    private class PointListArray
    {

        /**
         * x values *
         */
        double[] m_x = null;
        /**
         * y values *
         */
        double[] m_y = null;


        /**
         * ctor creates from two arraylists the data structure
         *
         * @param p_x list with x values
         * @param p_y list with y values
         */
        public PointListArray( ArrayList<Double> p_x, ArrayList<Double> p_y ) throws IllegalArgumentException
        {
            if ( ( p_x.size() != p_y.size() ) || ( p_x.size() < 2 ) )
                throw new IllegalArgumentException( "point list need a least two elements and must have equal length" );

            m_x = new double[p_x.size()];
            m_y = new double[p_y.size()];
            for ( int i = 0; i < p_x.size(); i++ )
            {
                m_x[i] = p_x.get( i );
                m_y[i] = p_y.get( i );
            }
        }


        /**
         * returns a double array with x values
         *
         * @return array
         */
        public double[] getX()
        {
            return m_x;
        }


        /**
         * returns one x value
         *
         * @param p_index index of the value
         * @return value
         */
        public double getX( int p_index )
        {
            return m_x[p_index];
        }


        /**
         * returns a double array with x values
         *
         * @return array
         */
        public double[] getY()
        {
            return m_y;
        }


        /**
         * returns one x value
         *
         * @param p_index index of the value
         * @return value
         */
        public double getY( int p_index )
        {
            return m_y[p_index];
        }


        /**
         * number of elements
         *
         * @return number of elements
         */
        public int size()
        {
            return m_x.length;
        }

    }

}