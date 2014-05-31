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

package de.tu_clausthal.in.winf.objects;

import com.graphhopper.GHRequest;
import com.graphhopper.routing.Path;
import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.winf.graph.CCellCarLinkage;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * class for a default car *
 *
 * @note the paint method color the car depend on the current speed and need not to be call dispose
 */
public class CDefaultCar implements ICar {

    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * random interface *
     */
    protected Random m_random = new Random();
    /**
     * geo position of the start *
     */
    protected GeoPosition m_StartPosition = null;
    /**
     * geo position of the end *
     */
    protected GeoPosition m_EndPosition = null;
    /**
     * current speed *
     */
    protected int m_speed = 0;
    /**
     * maximum speed definition *
     */
    protected int m_maxSpeed = 200;
    /**
     * linger probability value *
     */
    protected double m_LingerProbability = 0;
    /**
     * edges of the route *
     */
    protected List<EdgeIteratorState> m_routeedges = null;
    /**
     * edge counter for GHResponse  *
     */
    protected int m_routeindex = 0;
    /**
     * individual acceleration
     */
    protected int m_acceleration = 1;
    /**
     * individual deceleration *
     */
    protected int m_deceleration = 1;


    /**
     * ctor to create the initial values
     *
     * @param p_StartPosition start positions (position of the source)
     */
    public CDefaultCar(GeoPosition p_StartPosition) {
        m_StartPosition = p_StartPosition;
        m_LingerProbability = m_random.nextDouble();
        while (m_speed < 50)
            m_speed = m_random.nextInt(m_maxSpeed);
        m_acceleration = m_random.nextInt(40) + 20;
        m_deceleration = m_random.nextInt(40) + 20;

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
        m_speed = Math.min(Math.max(p_speed, 15), m_maxSpeed);
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

                // check car can be set to the edge position
                if ((l_steps < l_edge.getEdgeCells()) && (!l_edge.isPositionSet(l_steps))) {
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
    public Map<Integer, ICar> getPredecessor() {
        int l_edgelength = 0;

        //iterate over the edges in the rozre
        for (int i = m_routeindex; i < m_routeedges.size(); i++) {
            CCellCarLinkage l_edge = CGraphHopper.getInstance().getEdge(m_routeedges.get(m_routeindex));

            if (l_edge == null)
                return null;

            // exists a predecessor on the current edge
            Map<Integer, ICar> l_predecessor = (i == m_routeindex) ? l_edge.getPredecessor(this) : l_edge.getPredecessor(0);
            if (l_predecessor != null) {
                Map<Integer, ICar> l_predecessordistance = new HashMap();
                for (Map.Entry<Integer, ICar> l_item : l_predecessor.entrySet())
                    l_predecessordistance.put(l_item.getKey().intValue() + l_edgelength, l_item.getValue());

                return l_predecessordistance;
            }

            l_edgelength += l_edge.getEdgeCells();
        }

        return null;

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
    public int getAcceleration() {
        return m_acceleration;
    }

    @Override
    public int getDeceleration() {
        return m_deceleration;
    }

    @Override
    public Map<String, Object> inspect() {
        Map<String, Object> l_map = new HashMap();

        l_map.put("current speed", m_speed);
        l_map.put("maximum speed", m_maxSpeed);
        l_map.put("acceleration", m_acceleration);
        l_map.put("deceleration", m_deceleration);
        l_map.put("start position", m_StartPosition);
        l_map.put("end position", m_EndPosition);
        l_map.put("current edge id", this.getCurrentEdgeID());
        l_map.put("current geoposition", this.getCurrentPosition());

        return l_map;
    }


    //@Override
    public String toString() {
        return "speed [" + this.getCurrentSpeed() + " / " + this.getMaximumSpeed() + "]\tstart- / end- / current position [" + this.getStartPosition() + " / " + this.getEndPosition() + " / " + this.getCurrentPosition() + "]\tlinger prop [" + this.getLingerProbability() + "]\tedge [" + this.getCurrentEdgeID() + "]";
    }


    @Override
    public void paint(Graphics2D graphics2D, JXMapViewer viewer) {
        GeoPosition l_position = this.getCurrentPosition();
        if (l_position == null)
            return;

        int l_zoom = Math.max(9 - viewer.getZoom(), 2);

        Point2D l_point = viewer.getTileFactory().geoToPixel(l_position, viewer.getZoom());

        // speed limit color defined with http://wiki.openstreetmap.org/wiki/File:Speed_limit_Germany.png
        graphics2D.setColor(Color.DARK_GRAY);
        if (m_speed >= 50)
            graphics2D.setColor(Color.MAGENTA);
        if (m_speed >= 60)
            graphics2D.setColor(Color.PINK);
        if (m_speed >= 80)
            graphics2D.setColor(Color.BLUE);
        if (m_speed >= 100)
            graphics2D.setColor(Color.CYAN);
        if (m_speed >= 130)
            graphics2D.setColor(Color.RED);

        graphics2D.fillOval((int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom);
    }
}
