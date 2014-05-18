/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenpraktikum.   #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
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

package de.tu_clausthal.in.winf.graph;

import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.objects.ICar;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * class for defining the cell sampling structure of an edge
 * with the car information
 */
public class CCellCarLinkage implements Comparable<CCellCarLinkage> {

    /**
     * edge ID *
     */
    private int m_edgeid = 0;
    /**
     * length of the edge (distance) *
     */
    private double m_edgelength = 0;
    /**
     * map with car-2-position in forward direction *
     */
    private Map<ICar, Integer> m_cars = new ConcurrentHashMap();
    /**
     * array with cells of the forward direction *
     */
    private ICar[] m_cells = null;
    /**
     * array with geopositions of the cell *
     */
    private GeoPosition[] m_cellgeoposition = null;

    /**
     * ctor create the samples
     *
     * @param p_id     edge ID
     * @param p_length length of the edge
     */
    public CCellCarLinkage(int p_id, double p_length) {
        m_edgeid = p_id;
        m_edgelength = p_length;
        this.sampling(CGraphHopper.getInstance().getEdgeIterator(m_edgeid));
    }


    /**
     * ctor create the samples
     *
     * @param p_edgestate
     */
    public CCellCarLinkage(EdgeIteratorState p_edgestate) {
        m_edgeid = p_edgestate.getEdge();
        m_edgelength = p_edgestate.getDistance();
        this.sampling(p_edgestate);
    }

    /**
     * creates the sampling of an edge
     *
     * @param p_edgestate edge state
     */
    private void sampling(EdgeIteratorState p_edgestate) {
        m_cells = new ICar[(int) Math.ceil(m_edgelength / CConfiguration.getInstance().get().CellSampling)];
        m_cellgeoposition = new GeoPosition[m_cells.length];

        // create a spline interpolation for cell sampling of the geoposition
        // get edge geoposition and convert data in arrays, run spline interpolation
        // catch number exceptions and calculate for each cell the geoposition
        PointListArray l_list = this.filterPointList(p_edgestate.fetchWayGeometry(2), 0.01);

        try {

            UnivariateInterpolator l_interpolator = l_list.size() < 3 ? new LinearInterpolator() : new SplineInterpolator();
            UnivariateFunction l_function = l_interpolator.interpolate(l_list.getX(), l_list.getY());
            double l_increment = (l_list.getX(l_list.size() - 1) - l_list.getX(0)) / m_cells.length;
            for (int i = 0; i < m_cells.length; i++)
                m_cellgeoposition[i] = new GeoPosition(l_list.getX(0) + i * l_increment, l_function.value(l_list.getX(0) + i * l_increment));

        } catch (NonMonotonicSequenceException l_exception) {

            double l_xincrement = (l_list.getX(l_list.size() - 1) - l_list.getX(0)) / m_cells.length;
            double l_yincrement = (l_list.getY(l_list.size() - 1) - l_list.getY(0)) / m_cells.length;
            for (int i = 0; i < m_cells.length; i++)
                m_cellgeoposition[i] = new GeoPosition(l_list.getX(0) + i * l_xincrement, l_list.getY(0) + i * l_yincrement);
        }

    }

    /**
     * filter point list to create a list of points which stores monoton increase points
     *
     * @param p_input   input point list
     * @param p_epsilon epsilon value of the monotonic increase
     * @return point list array
     */
    private PointListArray filterPointList(PointList p_input, double p_epsilon) {
        ArrayList<Double> l_x = new ArrayList();
        ArrayList<Double> l_y = new ArrayList();

        l_x.add(p_input.getLatitude(0));
        l_y.add(p_input.getLongitude(0));

        // convert point list to arrays and beware static increase
        for (int i = 1; i < p_input.size() - 1; i++)
            if ((Math.abs(l_x.get(l_x.size() - 1) - p_input.getLatitude(i)) >= p_epsilon) &&
                    (Math.abs(l_y.get(l_y.size() - 1) - p_input.getLongitude(i)) >= p_epsilon)) {
                l_x.add(p_input.getLatitude(i));
                l_y.add(p_input.getLongitude(i));
            }

        l_x.add(p_input.getLatitude(p_input.size() - 1));
        l_y.add(p_input.getLongitude(p_input.size() - 1));

        return new PointListArray(l_x, l_y);
    }

    /**
     * returns the edge ID
     *
     * @return ID
     */
    public int getEdgeID() {
        return m_edgeid;
    }

    /**
     * returns the length of the edge
     *
     * @return length
     */
    public double getEdgeLength() {
        return m_edgelength;
    }

    /**
     * returns the number of samples of the edge
     *
     * @return sample
     */
    public int getEdgeCells() {
        return m_cells.length;
    }

    /**
     * returns the position of a car on the edge
     *
     * @param p_car car object
     * @return null or position
     */
    public Integer getCarPosition(ICar p_car) {
        return m_cars.get(p_car);
    }

    /**
     * returns the geoposition of a car
     *
     * @param p_car car object
     * @return geoposition or null
     */
    public GeoPosition getCarGeoposition(ICar p_car) {
        Integer l_position = m_cars.get(p_car);
        if (l_position == null)
            return null;

        return m_cellgeoposition[l_position.intValue()];
    }

    /**
     * checks if a care is set on the edge
     *
     * @param p_car car object
     * @return boolean car existence
     */
    public boolean contains(ICar p_car) {
        return m_cars.containsKey(p_car);
    }


    /**
     * checks if the position is set
     *
     * @param p_position position index
     * @return boolean for emptiness
     */
    public synchronized boolean isPositionSet(int p_position) {
        return m_cells[p_position] != null;
    }

    /**
     * adds a car to the edge
     *
     * @param p_car      car object
     * @param p_position position
     */
    public synchronized void addCar2Edge(ICar p_car, int p_position) {
        if (m_cells[p_position] != null)
            return;

        m_cells[p_position] = p_car;
        m_cars.put(p_car, p_position);
    }

    /**
     * remove the car from the forward edge
     *
     * @param p_car car object
     */
    public synchronized void removeCarFromEdge(ICar p_car) {
        Integer l_pos = m_cars.remove(p_car);
        if (l_pos != null)
            m_cells[l_pos.intValue()] = null;
    }

    /**
     * check if a car can be updated within the edge
     *
     * @param p_car  car object
     * @param p_move steps to move
     * @return boolean, true if the car can be updated
     * @warn does not check for an empty position
     */
    public synchronized boolean CarCanUpdated(ICar p_car, int p_move) {
        if (!m_cars.containsKey(p_car))
            return false;

        return m_cars.get(p_car).intValue() + Math.abs(p_move) < m_cells.length;
    }

    /**
     * returns the number of cell, that overlaps after update
     *
     * @param p_car  car object
     * @param p_move steps to move
     * @return number of cells that overlapps (positive value, cells overlapped, otherwise negative value)
     */
    public synchronized int overlappingCells(ICar p_car, int p_move) {
        if (!m_cars.containsKey(p_car))
            return -1;

        return (m_cars.get(p_car).intValue() + Math.abs(p_move)) - (m_cells.length - 1);
    }

    /**
     * updates the car position on the edge
     *
     * @param p_car  car object
     * @param p_move moving steps
     */
    public synchronized void updateCar(ICar p_car, int p_move) throws IllegalAccessException {
        Integer l_position = m_cars.get(p_car);
        if (l_position == null)
            throw new IllegalAccessException("car not found");

        int l_newposition = l_position.intValue() + Math.abs(p_move);
        if (l_newposition >= m_cells.length)
            throw new IllegalAccessException("new position value is not within the edge cells");

        for (int i = l_position.intValue() + 1; i < l_newposition; i++)
            if (m_cells[i] != null)
                throw new IllegalAccessException("path between old and new position is not empty");

        m_cells[l_position.intValue()] = null;
        m_cells[l_newposition] = p_car;
        m_cars.put(p_car, l_newposition);
    }

    /**
     * returns the predecessor of a car on the edge
     *
     * @param p_car   car object
     * @param p_count number of predecessors
     * @return null or map with position and car object
     */
    public Map<Integer, ICar> getPredecessor(ICar p_car, int p_count) {
        Integer l_pos = m_cars.get(p_car);
        if (l_pos == null)
            return null;

        return getPredecessor(l_pos.intValue() + 1, p_count);
    }


    /**
     * returns the predeccors of a position
     *
     * @param p_position index
     * @param p_count    number of elements
     * @return null or map with position and car object
     */
    public Map<Integer, ICar> getPredecessor(int p_position, int p_count) {
        HashMap<Integer, ICar> l_items = new HashMap();
        for (int i = p_position; i < m_cells.length; i++) {
            if (m_cells[i] != null)
                l_items.put(i - p_position, m_cells[i]);

            if (l_items.size() >= p_count)
                break;
        }

        return l_items;
    }

    public int getNumberOfCars() {
        return m_cars.size();
    }

    /**
     * returns the next predecessor of a car on the edge
     *
     * @param p_car car object
     * @return null or map with position and car object
     */
    public Map<Integer, ICar> getPredecessor(ICar p_car) {
        return this.getPredecessor(p_car, 1);
    }

    /**
     * returns the next predecessor of a position on the egde
     *
     * @param p_position index
     * @return null or map with position and car object
     */
    public Map<Integer, ICar> getPredecessor(int p_position) {
        return this.getPredecessor(p_position, 1);
    }

    /**
     * clears the edge information
     */
    public synchronized void clear() {
        m_cars.clear();
        for (int i = 0; i < m_cells.length; i++)
            m_cells[i] = null;

    }

    /**
     * checks if the forward edge is empty
     *
     * @return boolean for empty
     */
    public synchronized boolean isEmpty() {
        for (ICar l_car : m_cells)
            if (l_car != null)
                return false;

        return true;
    }

    /**
     * checks if a cell is empty
     *
     * @param p_position position
     * @return empty boolean
     */
    public synchronized boolean isEmpty(int p_position) {
        return m_cells[p_position] == null;
    }

    @Override
    public String toString() {
        String l_out = "edge [" + m_edgeid + "]\tcells [" + m_cells.length + "]\tcars [" + m_cars.size() + "]\tset [";
        for (int i = 0; i < m_cells.length; i++)
            if (m_cells[i] != null)
                l_out += " " + i;
        l_out += " ]";

        return l_out;
    }

    @Override
    public int compareTo(CCellCarLinkage p_edgelink) {
        if (m_edgeid > p_edgelink.m_edgeid)
            return 1;
        if (m_edgeid < p_edgelink.m_edgeid)
            return -1;

        return 0;
    }

    @Override
    public boolean equals(Object p_object) {
        if ((p_object == null) || (!(p_object instanceof CCellCarLinkage)))
            return false;

        return this.m_edgeid == ((CCellCarLinkage) p_object).m_edgeid;
    }

    @Override
    public int hashCode() {
        return m_edgeid;
    }

    /**
     * class for storing the interpolation data *
     */
    private class PointListArray {

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
        public PointListArray(ArrayList<Double> p_x, ArrayList<Double> p_y) throws IllegalArgumentException {
            if ((p_x.size() != p_y.size()) || (p_x.size() < 2))
                throw new IllegalArgumentException("point list need a least two elements and must have equal length");

            m_x = new double[p_x.size()];
            m_y = new double[p_y.size()];
            for (int i = 0; i < p_x.size(); i++) {
                m_x[i] = p_x.get(i);
                m_y[i] = p_y.get(i);
            }
        }


        /**
         * returns a double array with x values
         *
         * @return array
         */
        public double[] getX() {
            return m_x;
        }


        /**
         * returns one x value
         *
         * @param p_index index of the value
         * @return value
         */
        public double getX(int p_index) {
            return m_x[p_index];
        }


        /**
         * returns a double array with x values
         *
         * @return array
         */
        public double[] getY() {
            return m_y;
        }


        /**
         * returns one x value
         *
         * @param p_index index of the value
         * @return value
         */
        public double getY(int p_index) {
            return m_y[p_index];
        }


        /**
         * number of elements
         *
         * @return number of elements
         */
        public int size() {
            return m_x.length;
        }

    }

}