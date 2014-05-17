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

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import de.tu_clausthal.in.winf.CConfiguration;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * class for create a graph structure of OSM data,
 * the class downloads the data and creates edges
 * and verticies
 *
 * @see http://graphhopper.com/
 */
public class CGraphHopper extends GraphHopper {


    /**
     * instance variable of singleton *
     */
    private static CGraphHopper s_instance = new CGraphHopper();
    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * map with edge-cell connection *
     */
    private Map<Integer, CCellCarLinkage> m_edgecell = new ConcurrentHashMap();


    /**
     * private ctor of the singleton structure
     * which runs the download / structure convert
     */
    private CGraphHopper() {
        // define graph location
        File l_graphlocation = new File(CConfiguration.getInstance().getConfigDir() + File.separator + "graphs" + File.separator +
                CConfiguration.getInstance().get().RoutingMap.replace('/', '_'));
        m_Logger.info("try to load graph from [" + l_graphlocation.getAbsolutePath() + "]");

        if (!this.load(l_graphlocation.getAbsolutePath())) {
            m_Logger.info("graph cannot be found");
            File l_osm = this.downloadOSMData();

            this.setGraphHopperLocation(l_graphlocation.getAbsolutePath());
            this.setOSMFile(l_osm.getAbsolutePath());
            this.setEncodingManager(new EncodingManager("CAR"));
            this.importOrLoad();

            l_osm.delete();
        }

        m_Logger.info("graph is loaded successfully");
    }

    /**
     * returns the instance
     *
     * @return instance of the class
     */
    public static CGraphHopper getInstance() {
        return s_instance;
    }

    /**
     * returns a route between two geo position
     *
     * @param p_start start geo position
     * @param p_end   end geo position
     * @return route response
     */
    public GHResponse getRoute(GeoPosition p_start, GeoPosition p_end) {
        GHRequest l_request = new GHRequest(p_start.getLatitude(), p_start.getLongitude(), p_end.getLatitude(), p_end.getLongitude());
        l_request.setAlgorithm(CConfiguration.getInstance().get().RoutingAlgorithm);
        GHResponse l_result = this.route(l_request);
        if (!l_result.getErrors().isEmpty()) {
            for (Throwable l_msg : l_result.getErrors())
                m_Logger.error(l_msg.getMessage());
            throw new IllegalArgumentException("graph error");
        }

        return l_result;
    }

    /**
     * returns the response of a route request
     *
     * @param p_request request object
     * @return response
     */
    public GHResponse getRoute(GHRequest p_request) {
        GHResponse l_result = this.route(p_request);
        if (!l_result.getErrors().isEmpty()) {
            for (Throwable l_msg : l_result.getErrors())
                m_Logger.error(l_msg.getMessage());
            throw new IllegalArgumentException("graph error");
        }

        return l_result;
    }

    /**
     * returns a route request between two geo position
     *
     * @param p_start start geo position
     * @param p_end   end geo position
     * @return request object
     */
    public GHRequest getRouteRequest(GeoPosition p_start, GeoPosition p_end) {
        GHRequest l_request = new GHRequest(p_start.getLatitude(), p_start.getLongitude(), p_end.getLatitude(), p_end.getLongitude());
        l_request.setAlgorithm(CConfiguration.getInstance().get().RoutingAlgorithm);
        return l_request;
    }

    /**
     * returns the path list of the request and response object
     *
     * @param p_request  route request
     * @param p_response route response
     * @return path list
     */
    public List<Path> getRoutePaths(GHRequest p_request, GHResponse p_response) {
        return this.getPaths(p_request, p_response);
    }

    /**
     * returns the closest edge(s) of a geo position
     *
     * @param p_position geo position
     * @return ID of the edge
     */
    public int getClosestEdge(GeoPosition p_position) {
        QueryResult l_result = this.getLocationIndex().findClosest(p_position.getLatitude(), p_position.getLongitude(), EdgeFilter.ALL_EDGES);
        return l_result.getClosestEdge().getEdge();
    }

    /**
     * returns the closest edge on a point list
     *
     * @param p_points point list
     * @param p_index  index of the list
     * @return edge ID
     */
    public int getClosestEdge(PointList p_points, int p_index) {
        GeoPosition l_geo = new GeoPosition(p_points.getLatitude(p_index), p_points.getLongitude(p_index));
        return this.getClosestEdge(l_geo);
    }

    /**
     * returns the max. speed of an edge
     *
     * @param p_edge edge ID
     * @return speed
     */
    public double getEdgeSpeed(int p_edge) {
        return this.getGraph().getEncodingManager().getEncoder("CAR").getSpeed(p_edge);
    }

    /**
     * returns the edge length
     *
     * @param p_edge edge ID
     * @return length
     */
    public double getEdgeLength(int p_edge) {
        return this.getGraph().getEdgeProps(p_edge, Integer.MIN_VALUE).getDistance();
    }


    /**
     * returns an iterator state of an edge
     *
     * @param p_edge edge ID
     * @return iterator
     */
    public EdgeIteratorState getEdgeIterator(int p_edge) {
        return this.getGraph().getEdgeProps(p_edge, Integer.MIN_VALUE);
    }


    /**
     * returns the ID of the closest node
     *
     * @param p_position geo position
     * @return ID of the node
     */
    public int getClosestNode(GeoPosition p_position) {
        QueryResult l_result = this.getLocationIndex().findClosest(p_position.getLatitude(), p_position.getLongitude(), EdgeFilter.ALL_EDGES);
        return l_result.getClosestNode();
    }

    /**
     * clears all edges
     */
    public synchronized void clear() {
        for (Map.Entry<Integer, CCellCarLinkage> l_item : m_edgecell.entrySet())
            l_item.getValue().clear();
    }

    /**
     * returns the linkage between edge and car
     *
     * @warn disable this method, because on an edge
     * ID the direction of an edge is undefined on
     * an edge iterator the direction is defined, so we
     * use the iterator
     * @param p_edge edge ID
     * @return linkage object
     *
    public synchronized CCellCarLinkage getEdge(int p_edge) {
    CCellCarLinkage l_edge = m_edgecell.get(p_edge);
    if (l_edge == null) {
    l_edge = new CCellCarLinkage(p_edge, this.getEdgeLength(p_edge));
    m_edgecell.put(l_edge.getEdgeID(), l_edge);
    }

    return l_edge;
    }
     */

    /**
     * returns the linkage between edge and car
     *
     * @param p_edgestate edge object
     * @return linkage object
     */
    public synchronized CCellCarLinkage getEdge(EdgeIteratorState p_edgestate) {
        CCellCarLinkage l_edge = m_edgecell.get(p_edgestate.getEdge());
        if (l_edge == null) {
            l_edge = new CCellCarLinkage(p_edgestate);
            m_edgecell.put(l_edge.getEdgeID(), l_edge);
        }

        return l_edge;
    }


    /**
     * downloads the OSM data from Geofabrik
     *
     * @return download file with full path
     * @see http://download.geofabrik.de/
     */
    private File downloadOSMData() {
        try {
            File l_output = File.createTempFile("tucosm", ".osm.pbf");
            URL l_url = new URL("http://download.geofabrik.de/" + CConfiguration.getInstance().get().RoutingMap + "-latest.osm.pbf");

            m_Logger.info("download OSM map from [" + l_url + "] to [" + l_output + "]");

            ReadableByteChannel l_channel = Channels.newChannel(l_url.openStream());
            FileOutputStream l_stream = new FileOutputStream(l_output);
            l_stream.getChannel().transferFrom(l_channel, 0, Long.MAX_VALUE);

            return l_output;
        } catch (Exception l_exception) {
            m_Logger.error(l_exception.getMessage());
        }
        return null;

    }

    /**
     * private inner edge classe
     *
     * @note is used for the hashmap, because the key must be a
     * full qualified object, but the comparable operation must
     * be overloaded for the primitive integer type
     */
    private class CEdgeInteger implements Comparable<CEdgeInteger> {

        /**
         * edge value *
         */
        private int m_value;

        /**
         * ctor to set the value
         *
         * @param p_value edge value
         */
        public CEdgeInteger(int p_value) {
            m_value = p_value;
        }

        /**
         * returning method
         *
         * @return value
         */
        public int get() {
            return m_value;
        }

        @Override
        public int compareTo(CEdgeInteger p_edgeInteger) {
            if (m_value > p_edgeInteger.m_value)
                return 1;
            if (m_value < p_edgeInteger.m_value)
                return -1;

            return 0;
        }

        @Override
        public boolean equals(Object p_object) {
            if ((p_object == null) || (!(p_object instanceof CEdgeInteger)))
                return false;

            return this.m_value == ((CEdgeInteger) p_object).m_value;
        }

    }

}
