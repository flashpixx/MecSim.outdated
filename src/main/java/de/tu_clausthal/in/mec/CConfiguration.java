/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec;

import com.google.gson.Gson;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * singleton class for configuration with a Json file
 */
public class CConfiguration
{

    /**
     * name of the configuration file
     */
    private static final String c_ConfigFilename = "config.json";
    /**
     * singleton instance variable
     */
    private static final CConfiguration c_instance = new CConfiguration();
    private final Map<String, File> m_location = new HashMap()
    {{
            put( "root", new File( System.getProperty( "user.home" ) + File.separator + ".mecsim" ) );
        }};
    /**
     * property that stores the configuration data
     */
    private Data m_data = new Data();
    /**
     * UTF-8 property reader
     */
    private ResourceBundle.Control m_reader = new UTF8Control();
    /**
     * manifest data
     */
    private Map<String, String> m_manifest = new HashMap<>();


    /**
     * private Ctor to avoid manual instantiation with manifest reading
     */
    private CConfiguration()
    {
        try
        {
            Manifest l_manifest = new JarFile( this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() ).getManifest();
            for ( Map.Entry<Object, Object> l_item : l_manifest.getMainAttributes().entrySet() )
                m_manifest.put( l_item.getKey().toString(), l_item.getValue().toString() );
        }
        catch ( IOException l_exception )
        {
        }

        this.setDefaultDirectories();
    }


    /**
     * singleton get instance method
     *
     * @return configuration instance
     */
    public static CConfiguration getInstance()
    {
        return c_instance;
    }


    /**
     * reads the manifest data
     *
     * @return map with string key and value
     */
    public Map<String, String> getManifest()
    {
        return m_manifest;
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
     * returns a path relative to the root directory
     *
     * @param p_dir directories
     * @return full file
     */
    private File getBasePath( final String... p_dir )
    {
        if ( !m_location.containsKey( "root" ) )
            throw new IllegalStateException( CCommon.getResourceString( this, "rootnotfound" ) );

        return new File( m_location.get( "root" ) + File.separator + StringUtils.join( p_dir, File.separator ) );
    }


    /**
     * returns the location of a directory
     *
     * @param p_name    name of the location
     * @param p_varargs path components after the directory
     * @return full directory
     */
    public File getLocation( final String p_name, final String... p_varargs )
    {
        if ( ( p_varargs == null ) || ( p_varargs.length == 0 ) )
            return m_location.get( p_name );

        return new File( m_location.get( p_name ) + File.separator + StringUtils.join( p_varargs, File.separator ) );
    }


    /**
     * creates the default directories relative to the root dir
     */
    private void setDefaultDirectories()
    {
        for ( String l_item : new String[]{"mas", "jar", "www"} )
            m_location.put( l_item, this.getBasePath( l_item ) );
    }


    /**
     * creates the configuration directories
     */
    private void createDirectories() throws IOException
    {
        for ( File l_dir : m_location.values() )
            if ( !l_dir.exists() && !l_dir.mkdirs() )
                throw new IOException( CCommon.getResourceString( this, "notcreate", l_dir.getAbsolutePath() ) );
    }


    /**
     * returns the property bundle
     *
     * @return resource bundle
     */
    public ResourceBundle getResourceBundle()
    {
        if ( m_data.Language != null )
            switch ( m_data.Language )
            {
                case "en":
                    Locale.setDefault( Locale.ENGLISH );
                    break;
                case "de":
                    Locale.setDefault( Locale.GERMANY );
                    break;
            }

        return ResourceBundle.getBundle( "language.locals", m_reader );
    }


    /**
     * sets the config dir
     *
     * @param p_dir directory
     */
    public void setConfigDir( final File p_dir )
    {
        m_location.put( "root", p_dir );
        this.setDefaultDirectories();
        try
        {
            this.createDirectories();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }
    }


    /**
     * write method of the configuration
     */
    public void write()
    {
        try
        {
            this.createDirectories();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }


        try (
                Writer l_writer = new OutputStreamWriter( new FileOutputStream( this.getLocation( "root", c_ConfigFilename ) ), "UTF-8" );
        )
        {
            new Gson().toJson( m_data, l_writer );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * reads the configuration within the directory
     */
    public void read()
    {

        // create the configuration directory
        Data l_tmp = null;
        try
        {
            this.createDirectories();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        final File l_config = this.getLocation( "root", c_ConfigFilename );
        CLogger.info( CCommon.getResourceString( this, "read", l_config ) );

        // read main configuration
        try (
                Reader l_reader = new InputStreamReader( new FileInputStream( l_config ), "UTF-8" );
        )
        {
            l_tmp = new Gson().fromJson( l_reader, Data.class );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        // check the configuration values and set it
        if ( ( l_tmp == null ) || ( l_tmp.ResetConfig ) )
            CLogger.warn( CCommon.getResourceString( this, "default" ) );
        else
        {
            if ( l_tmp.UIBindPort < 1 )
            {
                CLogger.warn( CCommon.getResourceString( this, "uibindportdefault" ) );
                l_tmp.UIBindPort = m_data.UIBindPort;
            }
            if ( l_tmp.ViewPoint == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "viewpointdefault" ) );
                l_tmp.ViewPoint = m_data.ViewPoint;
            }
            if ( l_tmp.WindowHeight < 100 )
            {
                CLogger.warn( CCommon.getResourceString( this, "heightdefault" ) );
                l_tmp.WindowHeight = m_data.WindowHeight;
            }
            if ( l_tmp.WindowWidth < 100 )
            {
                CLogger.warn( CCommon.getResourceString( this, "widthdefault" ) );
                l_tmp.WindowWidth = m_data.WindowWidth;
            }
            if ( ( l_tmp.RoutingAlgorithm == null ) || ( l_tmp.RoutingAlgorithm.isEmpty() ) )
            {
                CLogger.warn( CCommon.getResourceString( this, "routingdefault" ) );
                l_tmp.RoutingAlgorithm = m_data.RoutingAlgorithm;
            }
            if ( l_tmp.CellSampling < 1 )
            {
                CLogger.warn( CCommon.getResourceString( this, "cellsamplingdefault" ) );
                l_tmp.CellSampling = m_data.CellSampling;
            }
            if ( l_tmp.ThreadSleepTime < 0 )
            {
                CLogger.warn( CCommon.getResourceString( this, "threadsleepdefault" ) );
                l_tmp.ThreadSleepTime = m_data.ThreadSleepTime;
            }
            if ( l_tmp.RoutingMap == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "routingmapdefault" ) );
                l_tmp.RoutingMap = m_data.RoutingMap;
            }
            if ( l_tmp.Database == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "databasedefault" ) );
                l_tmp.Database = m_data.Database;
            }
            if ( l_tmp.Console == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "consoledefault" ) );
                l_tmp.Console = m_data.Console;
            }

            m_data = l_tmp;
        }

        // append all Jar files to the classpath of the system class loader
        try
        {
            URLClassLoader l_classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for ( String l_jar : m_location.get( "jar" ).list( new WildcardFileFilter( "*.jar" ) ) )
                CReflection.getClassMethod( l_classloader.getClass(), "addURL", new Class<?>[]{URL.class} ).getHandle().invoke( l_classloader, CCommon.getResource( m_location.get( "jar" ) + File.separator + l_jar ) );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
        catch ( Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }

    /**
     * get configuration definition
     *
     * @return data
     */
    private Map<String, Object> web_static_get()
    {
        final Map<String, Object> l_map = new HashMap<>();

        l_map.put( "cellsampling", m_data.CellSampling );
        l_map.put( "resetconfig", m_data.ResetConfig );
        l_map.put( "threadsleeptime", m_data.ThreadSleepTime );
        l_map.put( "routingalgorithm", m_data.RoutingAlgorithm );
        l_map.put( "language", m_data.Language );

        l_map.put( "console_linebuffer", m_data.getConsole().LineBuffer );
        l_map.put( "console_linenumber", m_data.getConsole().LineNumber );

        l_map.put( "routingmap_name", m_data.RoutingMap.Name );
        l_map.put( "routingmap_url", m_data.RoutingMap.URL );
        l_map.put( "routingmap_reimport", m_data.RoutingMap.Reimport );

        l_map.put( "database_active", m_data.Database.Active );
        l_map.put( "database_driver", m_data.Database.Driver );
        l_map.put( "database_url", m_data.Database.URL );
        l_map.put( "database_tableprefix", m_data.Database.TablePrefix );
        l_map.put( "database_username", m_data.Database.Username );
        l_map.put( "database_password", m_data.Database.Password );

        return l_map;
    }


    /**
     * set UI definition
     *
     * @param p_data   input data
     * @param p_header header data - configuration changeable only from localhost
     */
    private void web_static_set( final Map<String, String> p_data, final Map<String, String> p_header )
    {
        if ( !( ( p_header.containsKey( "remote-addr" ) ) && ( p_header.get( "remote-addr" ).equals( "127.0.0.1" ) ) ) )
            throw new IllegalStateException( CCommon.getResourceString( this, "notallowed" ) );

        m_data.CellSampling = Math.max( 1, Integer.parseInt( p_data.get( "cellsampling" ) ) );
        m_data.ResetConfig = Boolean.parseBoolean( p_data.get( "resetconfig" ) );
        m_data.ThreadSleepTime = Math.max( 0, Integer.parseInt( p_data.get( "threadsleeptime" ) ) );
        m_data.RoutingAlgorithm = CCommon.getCheckedValue( p_data.get( "routingalgorithm" ), m_data.RoutingAlgorithm, new String[]{"astar", "astarbi", "dijkstra", "dijkstrabi", "dijkstraOneToMany"} );
        m_data.Language = CCommon.getCheckedValue( p_data.get( "language" ), "en", new String[]{"en", "de"} );

        m_data.getConsole().LineBuffer = Math.max( 1, Integer.parseInt( p_data.get( "console_linebuffer" ) ) );
        m_data.getConsole().LineNumber = Math.max( 1, Integer.parseInt( p_data.get( "console_linenumber" ) ) );

        m_data.RoutingMap.Name = p_data.get( "routingmap_name" );
        m_data.RoutingMap.URL = p_data.get( "routingmap_url" );
        m_data.RoutingMap.Reimport = Boolean.parseBoolean( p_data.get( "routingmap_reimport" ) );

        m_data.Database.Active = Boolean.parseBoolean( p_data.get( "database_active" ) );
        m_data.Database.Driver = p_data.get( "database_driver" );
        m_data.Database.URL = p_data.get( "database_url" );
        m_data.Database.TablePrefix = p_data.get( "database_tableprefix" );
        m_data.Database.Username = p_data.get( "database_username" );
        m_data.Database.Password = p_data.get( "database_password" );
    }


    /**
     * class for storing the configuration
     */
    public class Data
    {
        /**
         * bind port of the web UI
         */
        public int UIBindPort = 9876;
        /**
         * cell size for sampling
         */
        public int CellSampling = 2;
        /**
         * flag to reset the configuration
         */
        public boolean ResetConfig = false;
        /**
         * geo position object of the start viewpoint
         */
        public GeoPosition ViewPoint = new GeoPosition( 51.8089, 10.3412 );
        /**
         * zoom level of the viewpoint on the start point
         */
        public int Zoom = 4;
        /**
         * thread sleep time in milliseconds
         */
        public int ThreadSleepTime = 25;
        /**
         * window width
         */
        public int WindowWidth = 1684;
        /**
         * window height
         */
        public int WindowHeight = 1024;
        /**
         * geo map for graph
         */
        public RoutingMap RoutingMap = new RoutingMap();
        /**
         * graph algorithm: astar & astarbi (A* algorithm), dijkstra, dijkstrabi, dijkstraOneToMany (Dijkstra
         * algorithm)
         */
        public String RoutingAlgorithm = "astarbi";
        /**
         * language code
         */
        public String Language = null;
        /**
         * database driver (optional)
         */
        private DatabaseDriver Database = new DatabaseDriver();
        /**
         * console definition
         */
        private ConsoleData Console = new ConsoleData();


        /**
         * returns the console data
         *
         * @return console data object
         */
        public ConsoleData getConsole()
        {
            return Console;
        }

        /**
         * returns database driver
         *
         * @return driver object
         */
        public DatabaseDriver getDatabase()
        {
            return Database;
        }


        /**
         * class of the console configuration
         */
        public class ConsoleData
        {
            /**
             * maximum char number on each line *
             */
            public int LineBuffer = 120;
            /**
             * maximum line numbers *
             */
            public int LineNumber = 120;
        }


        /**
         * class of the routing map
         */
        public class RoutingMap implements Serializable
        {
            /**
             * serialize version ID *
             */
            private static final long serialVersionUID = 1L;
            /**
             * download URL
             */
            public String URL = "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf";
            /**
             * flag for reimport
             */
            public boolean Reimport = false;
            /**
             * name of the map
             */
            public String Name = "europe/germany/lowersaxony";
        }


        /**
         * class of the database driver
         */
        public class DatabaseDriver
        {
            /**
             * enable / disable without removing settings *
             */
            public boolean Active = false;
            /**
             * driver *
             */
            public String Driver = null;
            /**
             * server url *
             */
            public String URL = null;
            /**
             * login user name *
             */
            public String Username = null;
            /**
             * login password *
             */
            public String Password = null;
            /**
             * table prefix
             */
            public String TablePrefix = null;

            /**
             * checks if the server can connect
             *
             * @return boolean flag
             */
            public boolean isConnectable()
            {
                return ( Active ) && ( Driver != null ) && ( !Driver.isEmpty() ) && ( URL != null ) && ( !URL.isEmpty() );
            }
        }

    }


    /**
     * class to read UTF-8 encoded property file
     *
     * @note Java default encoding for property files is ISO-Latin-1
     */
    protected class UTF8Control extends ResourceBundle.Control
    {

        public ResourceBundle newBundle( final String p_basename, final Locale p_locale, final String p_format, final ClassLoader p_loader, final boolean p_reload ) throws IllegalAccessException, InstantiationException, IOException
        {
            InputStream l_stream = null;
            final String l_resource = this.toResourceName( this.toBundleName( p_basename, p_locale ), "properties" );

            if ( !p_reload )
                l_stream = p_loader.getResourceAsStream( l_resource );
            else
            {

                final URL l_url = p_loader.getResource( l_resource );
                if ( l_url == null )
                    return null;

                final URLConnection l_connection = l_url.openConnection();
                if ( l_connection == null )
                    return null;

                l_connection.setUseCaches( false );
                l_stream = l_connection.getInputStream();
            }

            try
            {
                return new PropertyResourceBundle( new InputStreamReader( l_stream, "UTF-8" ) );

            }
            catch ( Exception l_exception )
            {
            }
            finally
            {
                l_stream.close();
            }

            return null;
        }
    }

}
