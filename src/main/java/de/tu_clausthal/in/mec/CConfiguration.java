/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec;

import com.google.gson.Gson;
import org.jxmapviewer.viewer.GeoPosition;
import org.metawidget.inspector.annotation.*;

import java.io.*;


/**
 * singleton class for configuration with a Json file
 */
public class CConfiguration
{

    /**
     * name of the configuration file
     */
    private static final String s_ConfigFilename = "config.json";
    /**
     * singleton instance variable
     */
    private static CConfiguration s_instance = new CConfiguration();
    /**
     * property that stores the configuration data
     */
    private Data m_data = new Data();
    /**
     * directory of the configuration file
     */
    private File m_dir = new File( System.getProperty( "user.home" ) + File.separator + ".mecsim" );

    /**
     * private Ctor to avoid manual instantiation
     */
    private CConfiguration()
    {
    }

    /**
     * singleton get instance method
     *
     * @return configuration instance
     */
    public static CConfiguration getInstance()
    {
        return s_instance;
    }

    /**
     * write method of the configuration
     */
    public void write()
    {
        try
        {
            if ( !m_dir.exists() && !m_dir.mkdirs() )
                throw new IOException( "unable to create " + m_dir.getAbsolutePath() );

            Writer l_writer = new OutputStreamWriter( new FileOutputStream( m_dir + File.separator + s_ConfigFilename ), "UTF-8" );

            Gson l_gson = new Gson();
            l_gson.toJson( m_data, l_writer );

            l_writer.close();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }
    }

    /**
     * reads the configuration within the directory
     */
    public void read()
    {
        Data l_tmp = null;
        try
        {
            String l_config = m_dir + File.separator + s_ConfigFilename;
            CLogger.info( "read configuration from [" + l_config + "]" );

            Reader l_reader = new InputStreamReader( new FileInputStream( l_config ), "UTF-8" );

            Gson l_gson = new Gson();
            l_tmp = l_gson.fromJson( l_reader, Data.class );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        if ( l_tmp == null )
            CLogger.warn( "configuration is null, use default configuration" );
        else
        {
            if ( l_tmp.ViewPoint == null )
            {
                CLogger.warn( "view point uses default value" );
                l_tmp.ViewPoint = m_data.ViewPoint;
            }
            if ( l_tmp.WindowHeight < 100 )
            {
                CLogger.warn( "window height uses default value" );
                l_tmp.WindowHeight = m_data.WindowHeight;
            }
            if ( l_tmp.WindowWidth < 100 )
            {
                CLogger.warn( "window width uses default value" );
                l_tmp.WindowWidth = m_data.WindowWidth;
            }
            if ( ( l_tmp.RoutingAlgorithm == null ) || ( l_tmp.RoutingAlgorithm.isEmpty() ) )
            {
                CLogger.warn( "routing algorithm uses default value" );
                l_tmp.RoutingAlgorithm = m_data.RoutingAlgorithm;
            }
            if ( l_tmp.CellSampling < 1 )
            {
                CLogger.warn( "cell sampling uses default value" );
                l_tmp.CellSampling = m_data.CellSampling;
            }
            if ( l_tmp.ThreadSleepTime < 0 )
            {
                CLogger.warn( "thread sleep time uses default value" );
                l_tmp.ThreadSleepTime = m_data.ThreadSleepTime;
            }

            m_data = l_tmp;
        }
    }

    /**
     * returns the config dir
     *
     * @return path to config dir
     */
    public File getConfigDir()
    {
        return m_dir;
    }

    /**
     * sets the config dir
     *
     * @param p_dir directory
     */
    public void setConfigDir( File p_dir )
    {
        m_dir = p_dir;
    }

    /**
     * returns the configuration data
     *
     * @return returns the configuration data
     */
    public Data get()
    {
        return m_data;
    }

    /**
     * private class for storing the configuration
     */
    public class Data
    {

        /**
         * geo position object of the start viewpoint
         */
        @UiHidden
        public GeoPosition ViewPoint = new GeoPosition( 51.8089, 10.3412 );
        /**
         * zoom level of the viewpoint on the start point
         */
        @UiHidden
        public int Zoom = 4;
        /**
         * window width
         */
        @UiLabel("Main Window Width")
        public int WindowWidth = 1684;
        /**
         * window height
         */
        @UiLabel("Main Window Height")
        public int WindowHeight = 1024;
        /**
         * cell size for sampling
         */
        @UiLabel("Cell size of the graph sampling (in metre)")
        public int CellSampling = 2;
        /**
         * thread sleep time in miliseconds
         */
        @UiHidden
        public int ThreadSleepTime = 25;
        /**
         * geo map for graph
         */
        public RoutingMap RoutingMap = new RoutingMap();
        /**
         * graph algorithm: astar & astarbi (A* algorithm), dijkstra, dijkstrabi, dijkstraOneToMany (Dijkstra algorithm)
         */
        @UiLabel("Routing algorithm")
        @UiLookup({"astar", "astarbi", "dijkstra", "dijkstrabi", "dijkstraOneToMany"})
        public String RoutingAlgorithm = "astarbi";

        /**
         * object of the routing map
         */
        public class RoutingMap
        {
            /**
             * download URL
             */
            @UiLabel("Download URL of the OpenStreetMap PBF file")
            public String url = "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf";

            /**
             * name of the map
             */
            @UiLabel("Unique name of the OpenStreetMap data")
            public String name = "europe/germany/lowersaxony";
        }

    }

}
