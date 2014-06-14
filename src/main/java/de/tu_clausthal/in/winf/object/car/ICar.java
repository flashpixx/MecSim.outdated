/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
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

package de.tu_clausthal.in.winf.object.car;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.winf.ui.IPainter;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Map;


/**
 * car interface for defining the car structure
 */
public interface ICar extends IPainter {

    /**
     * returns the geoposition of the start
     *
     * @return geoposition
     */
    public GeoPosition getStartPosition();


    /**
     * returns the geoposition of the end
     *
     * @return geoposition
     */
    public GeoPosition getEndPosition();


    /**
     * returns the maximum speed of the car
     *
     * @return speed value
     */
    public int getMaximumSpeed();


    /**
     * returns the current speed of the car
     *
     * @returns speed value
     */
    public int getCurrentSpeed();


    /**
     * sets the current speed of the care
     *
     * @param p_speed speed value
     */
    public void setCurrentSpeed(int p_speed) throws IllegalArgumentException;


    /**
     * returns a probability for lingering
     *
     * @return double value in [0,1]
     */
    public double getLingerProbability();


    /**
     * returns the current geo position of the car
     *
     * @return geoposition
     */
    public GeoPosition getCurrentPosition();


    /**
     * boolean method, that returns true, if the car has reached its end
     *
     * @return boolean for reaching the end
     */
    public boolean hasEndReached();


    /**
     * method to update the position on the graph *
     */
    public void drive();


    /**
     * returns the current predecessor of the car and the distance
     *
     * @return predecessor car object and its distance
     */
    public Map<Integer, ICar> getPredecessor();


    /**
     * get current edge on the graph
     *
     * @return edge ID
     */
    public int getCurrentEdgeID();


    /**
     * get current edge object on the graph
     *
     * @return edge
     */
    public EdgeIteratorState getCurrentEdge();


    /**
     * index of the current edge within the route
     *
     * @return index value
     */
    public int getCurrentIndex();


    /**
     * define an individual acceleration
     *
     * @return number (greater than zero)
     */
    public int getAcceleration();


    /**
     * define an individual deceleration
     *
     * @return number (greater than zero)
     */
    public int getDeceleration();

}
