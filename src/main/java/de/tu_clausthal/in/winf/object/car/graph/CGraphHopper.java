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

package de.tu_clausthal.in.winf.object.car.graph;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.object.car.graph.weights.CSpeedUp;
import de.tu_clausthal.in.winf.object.car.graph.weights.CSpeedUpTrafficJam;
import org.jxmapviewer.viewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
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
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * map with edge-cell connection *
     */
    private Map<Integer, CCellCarLinkage> m_edgecell = new ConcurrentHashMap();


    /**
     * private ctor of the singleton structure
     *
     * @param p_location path of data
     */
    public CGraphHopper(String p_location) {
        this.initialize(p_location);
    }

    /**
     * private ctor do add different weights for routing *
     *
     * @param p_location path of data
     * @param p_weights  weight name
     * @note weight reloading can be create only be reinitialize the graph
     * @see https://github.com/graphhopper/graphhopper/issues/111
     */
    public CGraphHopper(String p_location, String p_weights) {
        this.setCHShortcuts(p_weights);
        this.initialize(p_location);
    }


    /**
     * run graph initialize process with OSM data convert *
     *
     * @param p_location path of data
     */
    private void initialize(String p_location) {
        // define graph location
        File l_graphlocation = new File(p_location);
        System.out.println("try to load graph from [" + l_graphlocation.getAbsolutePath() + "]");

        // convert OSM or load the graph
        if (!this.load(l_graphlocation.getAbsolutePath())) {
            m_Logger.info("graph cannot be found");
            File l_osm = this.downloadOSMData();

            this.setGraphHopperLocation(l_graphlocation.getAbsolutePath());
            this.setOSMFile(l_osm.getAbsolutePath());
            this.setEncodingManager(new EncodingManager("CAR"));
            this.importOrLoad();

            l_osm.delete();
        }

        System.out.println("graph is loaded successfully");
    }


    /**
     * creates a list of list of edge between two geopositions
     *
     * @param p_start start geoposition
     * @param p_end   end geoposition
     * @return list of list of edges
     */
    public List<List<EdgeIteratorState>> getRoutes(GeoPosition p_start, GeoPosition p_end) {
        return this.getRoutes(p_start, p_end, Integer.MAX_VALUE);
    }


    /**
     * creates a list of list of edge between two geopositions
     *
     * @param p_start     start geoposition
     * @param p_end       end geoposition
     * @param p_maxroutes max. number of paths
     * @return list of list of edges
     */
    public List<List<EdgeIteratorState>> getRoutes(GeoPosition p_start, GeoPosition p_end, int p_maxroutes) {
        // calculate routes
        GHRequest l_request = new GHRequest(p_start.getLatitude(), p_start.getLongitude(), p_end.getLatitude(), p_end.getLongitude());
        l_request.setAlgorithm(CConfiguration.getInstance().get().RoutingAlgorithm);

        GHResponse l_result = this.route(l_request);
        if (!l_result.getErrors().isEmpty()) {
            for (Throwable l_msg : l_result.getErrors())
                m_Logger.error(l_msg.getMessage());
            throw new IllegalArgumentException("graph error");
        }


        // get all pathes
        List<Path> l_routes = this.getPaths(l_request, l_result);
        if (l_routes.size() == 0)
            return null;

        // create edge list with routes
        List<List<EdgeIteratorState>> l_pathes = new ArrayList();
        for (Path l_path : l_routes) {
            if (l_pathes.size() >= p_maxroutes)
                return l_pathes;

            // we must delete the first and last element, because the items are "virtual"
            List<EdgeIteratorState> l_edgelist = l_path.calcEdges();
            if (l_edgelist.size() < 3)
                continue;

            l_edgelist.remove(0);
            l_edgelist.remove(l_edgelist.size() - 1);

            l_pathes.add(l_edgelist);
        }

        return l_pathes;
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
    public double getEdgeSpeed(EdgeIteratorState p_edge) {
        return this.getGraph().getEncodingManager().getEncoder("CAR").getSpeed(p_edge.getFlags());
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

            System.out.println("download OSM map from [" + l_url + "] to [" + l_output + "]");

            ReadableByteChannel l_channel = Channels.newChannel(l_url.openStream());
            FileOutputStream l_stream = new FileOutputStream(l_output);
            l_stream.getChannel().transferFrom(l_channel, 0, Long.MAX_VALUE);

            return l_output;
        } catch (Exception l_exception) {
            m_Logger.error(l_exception.getMessage());
        }
        return null;

    }

    @Override
    public Weighting createWeighting(String p_weighting, FlagEncoder p_encoder) {
        if ("TrafficJam + SpeedUp".equalsIgnoreCase(p_weighting))
            return new CSpeedUpTrafficJam(this, p_encoder);

        if ("SpeedUp".equalsIgnoreCase(p_weighting))
            return new CSpeedUp(p_encoder);

        if ("TrafficJam".equalsIgnoreCase(p_weighting))
            return null;

        return super.createWeighting(p_weighting, p_encoder);
    }

}
