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
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotEmpty;
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
            new Gson().toJson( m_data, l_writer );
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
            l_tmp = new Gson().fromJson( l_reader, Data.class );
            l_reader.close();
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
     *
     * @see http://metawidget.sourceforge.net/doc/reference/en/html/ch04s03.html
     */
    public class Data
    {

        /**
         * cell size for sampling
         */
        public int CellSampling = 2;
        /**
         * geo position object of the start viewpoint
         */
        private GeoPosition ViewPoint = new GeoPosition( 51.8089, 10.3412 );
        /**
         * zoom level of the viewpoint on the start point
         */
        private int Zoom = 4;
        /**
         * thread sleep time in miliseconds
         */
        private int ThreadSleepTime = 25;
        /**
         * window width
         */
        private int WindowWidth = 1684;
        /**
         * window height
         */
        private int WindowHeight = 1024;
        /**
         * geo map for graph
         */
        //@UiSection("Data Source / Download")
        //@UiLabel("")
        private RoutingMap RoutingMap = new RoutingMap();
        /**
         * graph algorithm: astar & astarbi (A* algorithm), dijkstra, dijkstrabi, dijkstraOneToMany (Dijkstra
         * algorithm)
         */
        private String RoutingAlgorithm = "astarbi";


        @UiSection("Traffic Graph")
        @UiLabel("Cell size of the graph sampling (in metre)")
        @Min(1)
        public int getCellsampling()
        {
            return CellSampling;
        }

        public void setCellsampling( int p_value )
        {
            CellSampling = p_value;
        }

        @UiComesAfter("Cellsampling")
        @UiLabel("Routing algorithm")
        @UiLookup({"astar", "astarbi", "dijkstra", "dijkstrabi", "dijkstraOneToMany"})
        @NotEmpty
        public String getRoutingalgorithm()
        {
            return RoutingAlgorithm;
        }

        public void setRoutingalgorithm( String p_value )
        {
            RoutingAlgorithm = p_value;
        }

        @UiComesAfter("Routingalgorithm")
        @UiLabel("")
        public RoutingMap getRoutingmap()
        {
            return RoutingMap;
        }

        public void setRoutingmap( RoutingMap p_value )
        {
            RoutingMap = p_value;
        }

        @UiHidden
        public GeoPosition getViewpoint()
        {
            return ViewPoint;
        }

        public void setViewpoint( GeoPosition p_value )
        {
            ViewPoint = p_value;
        }

        @UiHidden
        public int getZoom()
        {
            return Zoom;
        }

        public void setZoom( int p_value )
        {
            Zoom = p_value;
        }

        @UiHidden
        public int getThreadsleeptime()
        {
            return ThreadSleepTime;
        }

        public void setThreadsleeptime( int p_value )
        {
            ThreadSleepTime = p_value;
        }

        @UiHidden
        public int getWindowwidth()
        {
            return WindowWidth;
        }

        public void setWindowwidth( int p_value )
        {
            WindowWidth = p_value;
        }

        @UiHidden
        public int getWindowheight()
        {
            return WindowHeight;
        }

        public void setWindowheight( int p_value )
        {
            WindowHeight = p_value;
        }



        /**
         * object of the routing map
         */
        public class RoutingMap
        {
            /**
             * download URL
             */
            private String url = "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf";

            /**
             * name of the map
             */
            private String name = "europe/germany/lowersaxony";


            @UiLabel("Unique name of the OpenStreetMap data")
            public String getName()
            {
                return name;
            }

            public void setName( String p_value )
            {
                name = p_value;
            }

            @UiLabel("Download URL of the OpenStreetMap PBF file")
            public String getUrl()
            {
                return url;
            }

            public void setUrl( String p_value )
            {
                url = p_value;
            }
        }

    }

}