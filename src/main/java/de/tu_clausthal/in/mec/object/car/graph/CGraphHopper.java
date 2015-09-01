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
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.weights.CCombine;
import de.tu_clausthal.in.mec.object.car.graph.weights.CForbiddenEdges;
import de.tu_clausthal.in.mec.object.car.graph.weights.CSpeedUp;
import de.tu_clausthal.in.mec.object.car.graph.weights.CTrafficJam;
import de.tu_clausthal.in.mec.object.car.graph.weights.CWeightingWrapper;
import de.tu_clausthal.in.mec.object.car.graph.weights.IWeighting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * class for create a graph structure of OSM data, the class downloads the data and creates edges and verticies
 *
 * @see http://graphhopper.com/
 */
public class CGraphHopper extends GraphHopper
{
    /**
     * cell size for sampling
     */
    private final int m_cellsize;
    /**
     * map with edge-cell connection
     */
    private final Map<Integer, CEdge<ICar, ?>> m_edgecell = new ConcurrentHashMap<>();
    /**
     * set with listerner of the edges
     */
    private final Set<IAction<ICar, ?>> m_edgelister = new HashSet<>();
    /**
     * default weight
     */
    private CCombine<EWeight> m_weight;

    /**
     * ctor
     *
     * @param p_cellsize cellsize in meter for sampling
     */
    public CGraphHopper( final int p_cellsize )
    {
        this( "CAR", p_cellsize );
    }

    /**
     * ctor
     *
     * @param p_encoding flag encoder name
     * @param p_cellsize cellsize in meter for sampling
     * @see https://github.com/graphhopper/graphhopper/blob/master/core/src/main/java/com/graphhopper/routing/util/EncodingManager.java
     */
    public CGraphHopper( final String p_encoding, final int p_cellsize )
    {
        // set the default settings
        m_cellsize = p_cellsize;
        this.setCHShortcuts( "default" );

        // define graph location (use configuration)
        final Pair<String, String> l_current = getCurrentGraph();

        final File l_graphlocation = CConfiguration.getInstance().getLocation(
                "root", "graphs", CConfiguration.getInstance().get().<String>get(
                        "simulation/traffic/map/name"
                ).replace( '/', '_' )
        );
        CLogger.out( CCommon.getResourceString( this, "path", l_graphlocation.getAbsolutePath() ) );

        // if reimported is set, delete graph directory
        if ( CConfiguration.getInstance().get().<Boolean>get( "simulation/traffic/map/reimport" ) )
            FileUtils.deleteQuietly( l_graphlocation );

        // convert OSM or load the graph
        CConfiguration.getInstance().get().set( "simulation/traffic/map/reimport", false );
        if ( !this.load( l_graphlocation.getAbsolutePath() ) )
        {
            CLogger.info( CCommon.getResourceString( this, "notloaded" ) );
            final File l_osm = this.downloadOSMData( CConfiguration.getInstance().get().<String>get( "simulation/traffic/map/url" ) );

            this.setGraphHopperLocation( l_graphlocation.getAbsolutePath() );
            this.setOSMFile( l_osm.getAbsolutePath() );
            this.setEncodingManager( new EncodingManager( p_encoding ) );
            this.importOrLoad();

            l_osm.delete();
        }

        CLogger.out( CCommon.getResourceString( this, "loaded" ) );
    }

    /**
     * adds an edge listener
     *
     * @note listener object will be set at the edge instantiation process
     */
    public final synchronized void addEdgeListener( final IAction<ICar, ?> p_listener )
    {
        m_edgelister.add( p_listener );
    }

    /**
     * clears all edges
     */
    public final synchronized void clear()
    {
        for ( final Map.Entry<Integer, CEdge<ICar, ?>> l_item : m_edgecell.entrySet() )
            l_item.getValue().clear();
    }

    @Override
    public final Weighting createWeighting( final String p_weighting, final FlagEncoder p_encoder )
    {
        // method creates on the first call all weights and store it within the
        // combined-weight object and returns it like a singleton
        if ( m_weight == null )
        {
            m_weight = new CCombine();

            m_weight.put( EWeight.Default, new CWeightingWrapper<Weighting>( super.createWeighting( p_weighting, p_encoder ), true ) );
            m_weight.put( EWeight.TrafficJam, new CTrafficJam( this ) );
            m_weight.put( EWeight.SpeedUp, new CSpeedUp( p_encoder ) );
            m_weight.put( EWeight.ForbiddenEdges, new CForbiddenEdges( this ) );
        }

        return m_weight;
    }

    /**
     * disable all weights *
     */
    public final void disableWeights()
    {
        for ( final IWeighting l_item : m_weight.values() )
            l_item.setActive( false );
    }

    /**
     * enable / disable a weighing
     *
     * @param p_weight weightning name
     * @see https://github.com/graphhopper/graphhopper/issues/111
     */
    public final void enableDisableWeight( final EWeight p_weight )
    {
        if ( !m_weight.containsKey( p_weight ) )
            return;

        m_weight.get( p_weight ).setActive( !m_weight.get( p_weight ).isActive() );
    }

    /**
     * returns a list of the active weights
     *
     * @return String list
     */
    public final EWeight[] getActiveWeights()
    {
        final List<EWeight> l_active = new LinkedList<>();
        for ( final Map.Entry<EWeight, IWeighting> l_item : m_weight.entrySet() )
            if ( l_item.getValue().isActive() )
                l_active.add( l_item.getKey() );

        return CCommon.convertCollectionToArray( EWeight[].class, l_active );
    }

    /**
     * returns the closest edge(s) of a geo position
     *
     * @param p_position geo position
     * @return ID of the edge
     */
    public final int getClosestEdge( final GeoPosition p_position )
    {
        final QueryResult l_result = this.getLocationIndex().findClosest( p_position.getLatitude(), p_position.getLongitude(), EdgeFilter.ALL_EDGES );
        return l_result.getClosestEdge().getEdge();
    }

    /**
     * returns the ID of the closest node
     *
     * @param p_position geo position
     * @return ID of the node
     */
    public final int getClosestNode( final GeoPosition p_position )
    {
        final QueryResult l_result = this.getLocationIndex().findClosest( p_position.getLatitude(), p_position.getLongitude(), EdgeFilter.ALL_EDGES );
        return l_result.getClosestNode();
    }

    /**
     * returns the linkage between edge and car
     *
     * @param p_edgestate edge object
     * @return linkage object
     *
     * @note listener object will be set at the edge instantiation process
     * @todo check generic
     */
    public final synchronized CEdge<ICar, ?> getEdge( final EdgeIteratorState p_edgestate )
    {
        CEdge l_edge = m_edgecell.get( p_edgestate.getEdge() );
        if ( l_edge == null )
        {
            l_edge = new CEdge<>( p_edgestate, m_cellsize );
            l_edge.addListener( m_edgelister );
            m_edgecell.put( l_edge.getEdgeID(), l_edge );
        }

        return l_edge;
    }

    /**
     * returns an iterator state of an edge
     *
     * @param p_edgeid edge ID
     * @return iterator
     */
    public final EdgeIteratorState getEdgeIterator( final int p_edgeid )
    {
        return this.getGraph().getEdgeProps( p_edgeid, Integer.MIN_VALUE );
    }

    /**
     * returns the max. speed of an edge
     *
     * @param p_edge edge ID
     * @return speed
     */
    public final double getEdgeSpeed( final EdgeIteratorState p_edge )
    {
        if ( p_edge == null )
            return Double.POSITIVE_INFINITY;

        return this.getGraph().getEncodingManager().getEncoder( "CAR" ).getSpeed( p_edge.getFlags() );
    }

    /**
     * number of cars
     *
     * @return number of cars on the graph
     */
    public final synchronized int getNumberOfObjects()
    {
        int l_count = 0;
        for ( final CEdge<ICar, ?> l_item : m_edgecell.values() )
            l_count += l_item.getNumberOfObjects();
        return l_count;
    }

    /**
     * creates the full path of cells with the edge value
     *
     * @param p_route edge list
     * @return list with pair of edge and cell position
     */
    public final ArrayList<Pair<EdgeIteratorState, Integer>> getRouteCells( final List<EdgeIteratorState> p_route )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_list = new ArrayList<>();
        if ( p_route.isEmpty() )
            return l_list;

        for ( final EdgeIteratorState l_edge : p_route )
            for ( int i = 0; i < this.getEdge( l_edge ).getEdgeCells(); i++ )
                l_list.add( new ImmutablePair<>( l_edge, i ) );

        return l_list;
    }

    /**
     * creates a list of list of edge between two geopositions
     *
     * @param p_start start geoposition
     * @param p_end end geoposition
     * @return list of list of edges
     */
    public final List<List<EdgeIteratorState>> getRoutes( final GeoPosition p_start, final GeoPosition p_end )
    {
        return this.getRoutes( p_start, p_end, Integer.MAX_VALUE );
    }

    /**
     * creates a list of list of edge between two geopositions
     *
     * @param p_start start geoposition
     * @param p_end end geoposition
     * @param p_maxroutes max. number of paths
     * @return list of list of edges
     */
    public final List<List<EdgeIteratorState>> getRoutes( final GeoPosition p_start, final GeoPosition p_end, final int p_maxroutes )
    {
        // calculate routes
        final GHRequest l_request = new GHRequest( p_start.getLatitude(), p_start.getLongitude(), p_end.getLatitude(), p_end.getLongitude() );
        l_request.setAlgorithm( CConfiguration.getInstance().get().<String>get( "simulation/traffic/routing/algorithm" ) );

        final GHResponse l_result = this.route( l_request );
        if ( !l_result.getErrors().isEmpty() )
        {
            for ( final Throwable l_msg : l_result.getErrors() )
                CLogger.error( l_msg.getMessage() );
            throw new IllegalArgumentException( CCommon.getResourceString( this, "grapherror" ) );
        }


        // get all paths and create routes
        final List<List<EdgeIteratorState>> l_paths = new ArrayList<>();
        final List<Path> l_routes = this.getPaths( l_request, l_result );
        if ( l_routes.size() == 0 )
            return l_paths;

        for ( final Path l_path : l_routes )
        {
            if ( l_paths.size() >= p_maxroutes )
                return l_paths;

            // we must delete the first and last element, because the items are "virtual"
            final List<EdgeIteratorState> l_edgelist = l_path.calcEdges();
            if ( l_edgelist.size() < 3 )
                continue;

            l_edgelist.remove( 0 );
            l_edgelist.remove( l_edgelist.size() - 1 );

            l_paths.add( l_edgelist );
        }

        return l_paths;
    }

    /**
     * returns the weight object
     *
     * @return weight object or null
     */
    @SuppressWarnings( "unchecked" )
    public final <T extends IWeighting> T getWeight( final EWeight p_weight )
    {
        return (T) m_weight.get( p_weight );
    }

    /**
     * checks if a weight is active
     *
     * @param p_weight weight name
     * @return bool flag if weight is active
     */
    public final boolean isActiveWeight( final EWeight p_weight )
    {
        return m_weight.containsKey( p_weight ) && ( m_weight.get( p_weight ).isActive() );
    }

    /**
     * gets the current graph with its download URL from
     * the configuration
     *
     * @return pair of graph name and download URL
     */
    private static Pair<String, String> getCurrentGraph()
    {
        final String l_current = CConfiguration.getInstance().get().<String>get( "simulation/traffic/map/current" );
        return new ImmutablePair<>( l_current, CConfiguration.getInstance().get().<String>get( "simulation/traffic/map/graphs/" + l_current ) );
    }

    /**
     * downloads the OSM data
     *
     * @param p_url URL for downloading as string
     * @return download file with full path
     */
    private final File downloadOSMData( final String p_url )
    {
        try
        {
            final File l_output = File.createTempFile( "mecsim", ".osm.pbf" );
            final URL l_url = new URL( p_url );

            CLogger.out( CCommon.getResourceString( this, "download", l_url, l_output ) );

            final ReadableByteChannel l_channel = Channels.newChannel( l_url.openStream() );
            final FileOutputStream l_stream = new FileOutputStream( l_output );
            l_stream.getChannel().transferFrom( l_channel, 0, Long.MAX_VALUE );

            return l_output;
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }
        return null;

    }

    /**
     * weight names
     */
    public enum EWeight
    {
        /**
         * default weight *
         */
        Default( CCommon.getResourceString( EWeight.class, "default" ) ),
        /**
         * traffic jam weight *
         */
        TrafficJam( CCommon.getResourceString( EWeight.class, "trafficjam" ) ),
        /**
         * speed-up weight *
         */
        SpeedUp( CCommon.getResourceString( EWeight.class, "speedup" ) ),
        /**
         * forbidden edges weight *
         */
        ForbiddenEdges( CCommon.getResourceString( EWeight.class, "forbiddenedges" ) );


        /**
         * string representation *
         */
        private final String m_text;

        /**
         * ctor
         *
         * @param p_text text representation
         */
        EWeight( final String p_text )
        {
            m_text = p_text;
        }

        @Override
        public String toString()
        {
            return m_text;
        }
    }

}
