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

package de.tu_clausthal.in.winf.objects;

import com.graphhopper.GHRequest;
import com.graphhopper.routing.Path;
import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.winf.graph.CCellCarLinkage;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * class for a default car *
 */
public class CDefaultCar implements ICar {

    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * random interface *
     */
    private Random m_random = new Random();
    /**
     * geo position of the start *
     */
    private GeoPosition m_StartPosition = null;
    /**
     * geo position of the end *
     */
    private GeoPosition m_EndPosition = null;
    /**
     * current speed *
     */
    private int m_speed = 0;
    /**
     * maximum speed definition *
     */
    private int m_maxSpeed = 0;
    /**
     * linger probability value *
     */
    private double m_LingerProbability = 0;
    /**
     * edges of the route *
     */
    private List<EdgeIteratorState> m_routeedges = null;
    /**
     * edge counter for GHResponse  *
     *
     * @warn counter runs from [1,n-1) because the first and last edge are "virtual" and does not exists within the graph
     */
    private int m_routeindex = 1;


    /**
     * ctor to create the initial values
     *
     * @param p_StartPosition start positions (position of the source)
     */
    public CDefaultCar(GeoPosition p_StartPosition) {
        m_LingerProbability = m_random.nextDouble();
        m_StartPosition = p_StartPosition;
        while (m_maxSpeed < 75)
            m_maxSpeed = m_random.nextInt(100) + 20;
        while (m_speed < 25)
            m_speed = m_random.nextInt(m_maxSpeed);

        // we try to find a route within the geo data, so we get a random end position and try to calculate a
        // route between start and end position, so if an exception is cached, we create a new end position
        List<Path> l_route = null;
        while (true) {
            try {
                m_EndPosition = new GeoPosition(m_StartPosition.getLatitude() + m_random.nextDouble() - 0.5, m_StartPosition.getLongitude() + m_random.nextDouble() - 0.5);
                GHRequest l_request = CGraphHopper.getInstance().getRouteRequest(m_StartPosition, m_EndPosition);
                l_route = CGraphHopper.getInstance().getRoutePaths(l_request, CGraphHopper.getInstance().getRoute(l_request));
                if (l_route.size() > 0) {
                    m_routeedges = l_route.get(0).calcEdges();

                    // we must delete the first and last element, because the items are "virtual"
                    if (m_routeedges.size() < 3)
                        continue;
                    m_routeedges.remove(0);
                    m_routeedges.remove(m_routeedges.size() - 1);

                    break;
                }

            } catch (Exception l_exception) {
            }
        }
    }


    @Override
    public GeoPosition getStartPosition() {
        return m_StartPosition;
    }

    @Override
    public GeoPosition getEndPosition() {
        return m_EndPosition;
    }

    @Override
    public int getMaximumSpeed() {
        return m_maxSpeed;
    }


    @Override
    public int getCurrentSpeed() {
        return m_speed;
    }


    @Override
    public void setCurrentSpeed(int p_speed) {
        if ((p_speed < 0) || (p_speed > m_maxSpeed))
            throw new IllegalArgumentException("speed value must be equal or greater than zero and smaller than maximum speed");
        m_speed = p_speed;
    }


    @Override
    public double getLingerProbability() {
        return m_LingerProbability;
    }


    @Override
    public GeoPosition getCurrentPosition() {
        return CGraphHopper.getInstance().getEdge(this.getCurrentEdge()).getCarGeoposition(this);
    }


    @Override
    public boolean hasEndReached() {
        return (m_routeedges != null) && (m_routeindex >= m_routeedges.size());
    }


    @Override
    public void drive() {

        // store current speed for modifying the position on the edge
        int l_steps = this.getCurrentSpeed();

        for (int i = 0; true; i++) {

            // if the car is at the end
            if (this.hasEndReached())
                return;

            // avoid infinity loop for update
            if (i > 15) {
                m_routeindex = m_routeedges.size();
                return;
            }

            // get current edge of cars route
            CCellCarLinkage l_edge = CGraphHopper.getInstance().getEdge(this.getCurrentEdge());

            // car is not set on the current edge, so we try to find the first position
            if (!l_edge.contains(this)) {
                //System.out.println(this+" not contains");
                // check car can be set to the edge position
                if ((l_steps < l_edge.getEdgeCells()) && (!l_edge.isPositionSet(l_steps))) {
                    //System.out.println(this+" set");
                    l_edge.addCar2Edge(this, l_steps);
                    break;
                }

                // if car faster than edge length, so get the next edge
                if (l_steps >= l_edge.getEdgeCells()) {
                    l_steps -= l_edge.getEdgeCells();
                    m_routeindex++;
                    continue;
                }

                // car is stored on the edge, so try to update
            } else {

                // car can be updated to a new position (we did not check for empty,
                // because the driving model should update the current speed value for an empty cell
                if (l_edge.CarCanUpdated(this, l_steps)) {
                    try {
                        l_edge.updateCar(this, l_steps);
                    } catch (Exception l_exception) {
                    }
                    break;

                    // car can not be updated to a new position, so remove it and add the car to the next edge
                } else {
                    l_steps = l_edge.overlappingCells(this, l_steps);
                    l_edge.removeCarFromEdge(this);
                    m_routeindex++;
                    continue;
                }


            }

        }

    }


    @Override
    public HashMap<Integer, ICar> getPredecessor() {
        return null;
        //return CGraphHopper.getInstance().getEdge(this.getCurrentEdge()).getPredecessor(this);
    }


    @Override
    public EdgeIteratorState getCurrentEdge() {
        if (this.hasEndReached())
            return m_routeedges.get(m_routeedges.size() - 1);
        return m_routeedges.get(m_routeindex);
    }


    @Override
    public int getCurrentEdgeID() {
        return m_routeedges.get(m_routeindex).getEdge();
    }


    @Override
    public int getCurrentIndex() {
        return m_routeindex;
    }


    @Override
    public void paint(Graphics2D p_graphic, TileFactory p_factory, int p_zoom) {
        GeoPosition l_position = this.getCurrentPosition();
        if (l_position == null)
            return;

        int l_zoom = Math.max(9 - p_zoom, 2);

        Point2D l_point = p_factory.geoToPixel(l_position, p_zoom);
        p_graphic.setColor(Color.MAGENTA);
        p_graphic.fillOval((int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom);
    }


    //@Override
    public String toString() {
        return "speed [" + this.getCurrentSpeed() + " / " + this.getMaximumSpeed() + "]\tstart- / end- / current position [" + this.getStartPosition() + " / " + this.getEndPosition() + " / " + this.getCurrentPosition() + "]\tlinger prop [" + this.getLingerProbability() + "]\tedge [" + this.getCurrentEdgeID() + "]";
    }

}
