/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

package de.tu_clausthal.in.mec;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CNameHashMap;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * singleton class for configuration with a Json file
 */
@SuppressWarnings( "serial" )
public class CConfiguration
{
    /**
     * name of the configuration file
     */
    private static final String c_filename = "config.json";
    /**
     * singleton instance variable
     */
    private static final CConfiguration c_instance = new CConfiguration();
    /**
     * name of the main package
     */
    private static final String c_mainpackage = "de.tu_clausthal.in.mec";
    /**
     * list of MAS files for extracting
     */
    private static final String[] c_masfiles = new String[]{"carDefault.asl", "carEmergency.asl", "carScramble.asl"};
    /**
     * list if external files for extracting
     */
    private static final String[] c_externalfiles = new String[]{"benchmark.r", "inconsistency.r"};
    /**
     * configuration map
     */
    private final CNameHashMap.CImmutable m_configuration = new CNameHashMap.CImmutable()
    {{
            // on shutdown delete full configuration folder
            put( "deleteonshutdown", false );

            // flag for resetting the full configuration - accessible within the UI
            put( "reset", false );

            // extract all MAS files from the Jar to the home path (should be run once) - accessible within the UI
            put( "extractmasexamples", true );

            // UUID / unique instance name - used by the database structure
            put( "uuid", UUID.randomUUID().toString() );


            // language data - accessible within the UI
            put(
                    "language", new CNameHashMap.CImmutable()
                    {{
                            // current language
                            put( "current", "en" );
                            put(
                                    // allowed language strings
                                    "allow", new ArrayList<String>()
                                    {{
                                            add( "en" );
                                            add( "de" );
                                        }}
                            );
                        }}
            );


            // UI data
            put(
                    "ui", new CNameHashMap.CImmutable()
                    {{
                            // initial geoposition of the map
                            put( "geoposition", new GeoPosition( 51.8089, 10.3412 ) );
                            // window height
                            put( "windowheight", 1024.0 );
                            // window width
                            put( "windowwidth", 1280.0 );
                            // zoom value
                            put( "zoom", 4 );
                            // route painter opacity delay - accessible within the UI
                            put( "routepainterdelay", 60 );
                            put(
                                    // server bind information - accessible within the UI
                                    "server", new CNameHashMap.CImmutable()
                                    {{
                                            // address / hostname bind name
                                            put( "host", "localhost" );
                                            // bind port
                                            put( "port", 9876 );
                                            // heartbeat of the websocket to avoid disconnecting
                                            put( "websocketheartbeat", 5 );
                                        }}
                            );
                        }}
            );


            // main simulation data
            put(
                    "simulation", new CNameHashMap.CImmutable()
                    {{
                            // sleep time of the thread
                            put( "threadsleeptime", 25 );

                            put(
                                    // traffic simulation components - accessible within the UI
                                    "traffic", new CNameHashMap.CImmutable()
                                    {{
                                            // cell sampling of the graph data in meter
                                            put( "cellsampling", 2 );
                                            // time sampling in seconds - each simulation step is equal to this time
                                            put( "timesampling", 10 );
                                            put(
                                                    "routing", new CNameHashMap.CImmutable()
                                                    {{
                                                            // current routing algorithms
                                                            put( "algorithm", "astarbi" );
                                                            put(
                                                                    // allowed strings of the routing algoritm
                                                                    "allow", new ArrayList<String>()
                                                                    {{
                                                                            add( "astar" );
                                                                            add( "astarbi" );
                                                                            add( "dijkstra" );
                                                                            add( "dijkstrabi" );
                                                                            add( "dijkstraOneToMany" );
                                                                        }}
                                                            );
                                                        }}
                                            );
                                            put(
                                                    // map information - accessible within the UI
                                                    "map", new CNameHashMap.CImmutable()
                                                    {{
                                                            // graph should be reimported
                                                            put( "reimport", false );
                                                            // active element on the graph map
                                                            put( "current", "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf" );
                                                            // list with download URL
                                                            put(
                                                                    "graphs", new ArrayList<String>()
                                                                    {{
                                                                            add( "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf" );
                                                                            add( "http://download.geofabrik.de/north-america/us/south-dakota-latest.osm.pbf" );
                                                                        }}
                                                            );
                                                        }}
                                            );
                                        }}
                            );
                        }}
            );


            // database data - accessible within the UI
            put(
                    "database", new CNameHashMap.CImmutable()
                    {{
                            // enables / disables the database connection - JDBC driver is needed
                            put( "active", false );
                            // driver name
                            put( "driver", null );
                            // connection URL
                            put( "url", null );
                            // table prefix - different simulation-types can be stored within the same database
                            put( "tableprefix", null );
                            // connection username
                            put( "username", null );
                            // connection password (decrypted)
                            put( "password", null );
                        }}
            );

            // manifest data
            put( "manifest", new CNameHashMap.CImmutable() );

        }};
    /**
     * map with path and check-item for the value
     */
    private Map<String, List<ICheck>> m_configurationchecks = new HashMap<String, List<ICheck>>()
    {{
            put(
                    "uuid", new LinkedList<ICheck>()
                    {{
                            add( new CStringNotEmpty() );
                        }}
            );
            put(
                    "language/current", new LinkedList<ICheck>()
                    {{
                            add( new CContains<String>( m_configuration.<List<String>>get( "language/allow" ) ) );
                        }}
            );

            put(
                    "ui/windowheight", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CGreater( 150 ) );
                        }}
            );
            put(
                    "ui/windowwidth", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CGreater( 150 ) );
                        }}
            );
            put(
                    "ui/zoom", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CGreater( 0 ) );
                        }}
            );
            put(
                    "ui/routepainterdelay", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CInRange( 1, 200 ) );
                        }}
            );
            put(
                    "ui/server/host", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                            add( new CStringNotEmpty() );
                        }}
            );
            put(
                    "ui/server/port", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CGreater( 1024 ) );
                        }}
            );
            put(
                    "ui/server/websocketheartbeat", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CGreater( 0 ) );
                        }}
            );

            put(
                    "simulation/threadsleeptime", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CInRange( 1, 150 ) );
                        }}
            );

            put(
                    "simulation/traffic/cellsampling", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CInRange( 1, 25 ) );
                        }}
            );
            put(
                    "simulation/traffic/timesampling", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Number.class ) );
                            add( new CInRange( 1, 360 ) );
                        }}
            );

            put(
                    "simulation/traffic/routing/algorithm", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                            add( new CContains<String>( m_configuration.<List<String>>get( "simulation/traffic/routing/allow" ) ) );
                        }}
            );

            put(
                    "simulation/traffic/map/reimport", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Boolean.class ) );
                        }}
            );
            put(
                    "simulation/traffic/map/current", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                            add( new CContains<String>( m_configuration.<List<String>>get( "simulation/traffic/map/graphs" ) ) );
                        }}
            );

            put(
                    "database/active", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( Boolean.class ) );
                        }}
            );
            put(
                    "database/driver", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                        }}
            );
            put(
                    "database/url", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                        }}
            );
            put(
                    "database/tableprefix", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                        }}
            );
            put(
                    "database/username", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                        }}
            );
            put(
                    "database/password", new LinkedList<ICheck>()
                    {{
                            add( new CClassType( String.class ) );
                        }}
            );
        }};
    /**
     * location map
     */
    private final Map<String, File> m_location = new HashMap<String, File>()
    {{
            put( "root", new File( System.getProperty( "user.home" ) + File.separator + ".mecsim" ) );
        }};
    /**
     * UTF-8 property reader
     */
    private ResourceBundle.Control m_reader = new UTF8Control();
    /**
     * UUID of the current process
     */
    private final BigInteger m_processid = new BigInteger( UUID.randomUUID().toString().replace( "-", "" ), 16 );

    /**
     * private Ctor to avoid manual instantiation with manifest reading
     *
     * @note IO-Exception is thrown on non-existing jar / NPE is thrown on Benchmarking
     */
    private CConfiguration()
    {
        try
        {
            final Manifest l_manifest = new JarFile( this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() ).getManifest();
            for ( final Map.Entry<Object, Object> l_item : l_manifest.getMainAttributes().entrySet() )
                m_configuration.set( "manifest/" + l_item.getKey().toString().toLowerCase(), l_item.getValue().toString() );
        }
        catch ( final IOException | NullPointerException l_exception )
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
     * returns the main package name
     */
    public static String getPackage()
    {
        return c_mainpackage;
    }

    /**
     * returns the current process id (a signed big integer value)
     */
    public BigInteger getProcessID()
    {
        return m_processid;
    }

    /**
     * returns the data items of the configuration
     *
     * @return configuration map
     */
    public CNameHashMap.CImmutable get()
    {
        return m_configuration;
    }

    /**
     * returns the location of a directory
     *
     * @param p_name name of the location
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
     * returns the property bundle
     *
     * @return resource bundle
     */
    public ResourceBundle getResourceBundle()
    {
        return getResourceBundle( null );
    }

    /**
     * returns the property bundle
     *
     * @param p_language language code
     * @return resource bundle
     */
    public ResourceBundle getResourceBundle( final String p_language )
    {
        final Locale l_locale;
        switch ( p_language == null || p_language.isEmpty() ? m_configuration.<String>get( "language/current" ) : p_language )
        {
            case "de":
                l_locale = Locale.GERMANY;
                break;
            default:
                l_locale = Locale.ENGLISH;
        }

        Locale.setDefault( l_locale );
        return ResourceBundle.getBundle( "language.locals", l_locale, m_reader );
    }

    /**
     * reads the configuration within the directory
     *
     * @return boolean on correct read
     */
    public boolean read()
    {
        final File l_config = this.getLocation( "root", c_filename );
        CLogger.info( CCommon.getResourceString( this, "read", l_config ) );

        try
        {
            // read Json
            this.createDirectories();
            final CNameHashMap l_input = new CNameHashMap( CCommon.fromJson( FileUtils.readFileToString( l_config, "utf-8" ) ) );
            if ( !l_input.<Boolean>get( "reset" ) )
                this.setConfiguration( l_input );
        }
        catch ( final IllegalArgumentException | ClassNotFoundException | NullPointerException l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            return false;
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }

        // append all Jar files to the classpath of the system class loader
        try
        {
            final URLClassLoader l_classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for ( final String l_jar : m_location.get( "jar" ).list( new WildcardFileFilter( "*.jar" ) ) )
            {
                final URL l_fqnjar = CCommon.getResourceURL( m_location.get( "jar" ) + File.separator + l_jar );
                CReflection.getClassMethod( l_classloader.getClass(), "addURL", new Class<?>[]{URL.class} ).getHandle().invoke(
                        l_classloader, l_fqnjar
                );
                CLogger.info( CCommon.getResourceString( this, "jarload", l_fqnjar ) );
            }
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
            return false;
        }

        // extract MAS files to the home directory
        if ( m_configuration.get( "extractmasexamples" ) )
            for ( final String l_mas : c_masfiles )
                try
                {
                    FileUtils.copyURLToFile( CCommon.getResourceURL( "mas/" + l_mas ), new File( m_location.get( "mas" ) + File.separator + l_mas ) );
                }
                catch ( final IOException l_exception )
                {
                    CLogger.error( l_exception );
                    return false;
                }
        m_configuration.set( "extractmasexamples", false );

        // extract external files
        for ( final String l_file : c_externalfiles )
            try
            {
                FileUtils.copyURLToFile( CCommon.getResourceURL( "external/" + l_file ), new File( m_location.get( "external" ) + File.separator + l_file ) );
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
                return false;
            }

        return true;
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
        catch ( final Exception l_exception )
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

            // remove manifest from config
            final Map<String, Object> l_output = m_configuration.toHashMap();
            l_output.remove( "manifest" );

            FileUtils.writeStringToFile( this.getLocation( "root", c_filename ), CCommon.toJson( l_output ) );
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    /**
     * deletes the configuration filename
     */
    public void delete()
    {
        FileUtils.deleteQuietly( this.getLocation( "root", c_filename ) );
    }

    /**
     * creates the configuration directories
     */
    private void createDirectories() throws IOException
    {
        for ( final File l_dir : m_location.values() )
            if ( !l_dir.exists() && !l_dir.mkdirs() )
                throw new IOException( CCommon.getResourceString( this, "notcreate", l_dir.getAbsolutePath() ) );
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
     * sets the configuration values with semantic check
     *
     * @param p_input input map
     */
    private void setConfiguration( final CNameHashMap p_input ) throws IOException, ClassNotFoundException, IllegalArgumentException
    {
        // convert special data into individual types
        if ( p_input.traverseContainsKey( "ui/geoposition" ) )
            p_input.set(
                    "ui/geoposition", new GeoPosition(
                            p_input.<Double>get( "ui/geoposition/latitude" ), p_input.<Double>get( "ui/geoposition/longitude" )
                    )
            );

        // check allowed values - traverse default map and transfer values if type is equal - need a local copy of the map for traversing
        final Set<String> l_errors = new HashSet<>();
        for ( final Map.Entry<CPath, Object> l_item : m_configuration )
            if ( p_input.traverseContainsKey( l_item.getKey() ) )
            {
                final Object l_data = p_input.get( l_item.getKey() );
                if ( l_data == null )
                {
                    m_configuration.set( l_item.getKey(), null );
                    continue;
                }

                // run value checks
                boolean l_valid = true;
                if ( m_configurationchecks.containsKey( l_item.getKey().toString() ) )
                    for ( final ICheck l_check : m_configurationchecks.get( l_item.getKey().toString() ) )
                        if ( l_check.isWrong( l_data ) )
                        {
                            l_errors.add( CCommon.getResourceString( this, "valuecheck", l_item.getKey(), l_data, l_check ) );
                            l_valid = false;
                            break;
                        }


                if ( l_valid )
                    m_configuration.set( l_item.getKey(), l_data );
            }


        if ( !l_errors.isEmpty() )
            throw new IllegalArgumentException( StringUtils.join( l_errors, "\n" ) );
    }


    /**
     * creates the default directories relative to the root dir
     */
    private void setDefaultDirectories()
    {
        for ( final String l_item : new String[]{"mas", "jar", "www", "external"} )
            m_location.put( l_item, this.getBasePath( l_item ) );
    }

    /**
     * UI method - read configuration
     *
     * @return data
     */
    private CNameHashMap.CImmutable web_static_get()
    {
        return m_configuration;
    }

    /**
     * UI method - set data
     *
     * @param p_data input data
     * @param p_header header data - configuration changeable only from localhost
     */
    private void web_static_set( final Map<String, Object> p_data, final Map<String, String> p_header ) throws IOException, ClassNotFoundException
    {
        if ( !( ( p_header.containsKey( "remote-addr" ) ) && ( p_header.get( "remote-addr" ).equals( "127.0.0.1" ) ) ) )
            throw new IllegalStateException( CCommon.getResourceString( this, "notallowed" ) );

        this.setConfiguration( new CNameHashMap.CImmutable( p_data ) );
    }

    /**
     * UI method - set data
     *
     * @param p_data input data
     **/
    private void web_static_deletegraph( final Map<String, Object> p_data )
    {
        if ( p_data.containsKey( "url" ) )
            CGraphHopper.deleteGraph( (String) p_data.get( "url" ) );
    }

    /**
     * UI method - run startup checks
     *
     * @return error / warning messages
     **/
    private Map<String, Object> web_static_startupchecks()
    {
        return CCommon.getMap( "messages", CCommon.startupchecks() );
    }

    /**
     * class type check
     */
    protected class CClassType extends ICheck<Object>
    {
        /**
         * class type *
         */
        private final Class<?> m_class;

        /**
         * ctor
         *
         * @param p_class class type
         */
        public CClassType( final Class<?> p_class )
        {
            m_class = p_class;
        }

        @Override
        public boolean isCorrect( final Object p_value )
        {
            if ( p_value == null )
                return true;

            return m_class.isInstance( p_value );
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text", m_class );
        }
    }

    /**
     * collection empty check
     */
    protected class CCollectionNotEmpty extends ICheck<Collection>
    {
        @Override
        public boolean isCorrect( final Collection p_value )
        {
            return ( p_value != null ) & ( !p_value.isEmpty() );
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text" );
        }
    }

    /**
     * collection contains check
     *
     * @tparam T type of the collection values
     */
    protected class CContains<T> extends ICheck<T>
    {
        /**
         * collection *
         */
        private final Collection<T> m_collection;

        /**
         * ctor - set the collection
         *
         * @param p_collection collection
         */
        public CContains( final Collection<T> p_collection )
        {
            m_collection = p_collection;
        }

        @Override
        public boolean isCorrect( final T p_value )
        {
            return m_collection.contains( p_value );
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text", m_collection );
        }
    }

    /**
     * numeric greater check
     */
    protected class CGreater extends ICheck<Number>
    {
        /**
         * bound
         */
        private final Number m_bound;

        /**
         * ctor - set the bound
         *
         * @param p_bound
         */
        public CGreater( final Number p_bound )
        {
            m_bound = p_bound;
        }

        @Override
        public boolean isCorrect( final Number p_value )
        {
            return p_value.doubleValue() > m_bound.doubleValue();
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text", m_bound );
        }
    }

    /**
     * numeric in range check
     */
    protected class CInRange extends ICheck<Number>
    {
        /**
         * lower bound *
         */
        private final Number m_lower;
        /**
         * upper bound *
         */
        private final Number m_upper;

        /**
         * ctor - set upper & lower bound
         *
         * @param p_lower lower bound
         * @param p_upper upper bound
         */
        public CInRange( final Number p_lower, final Number p_upper )
        {
            m_upper = p_upper;
            m_lower = p_lower;
        }

        @Override
        public boolean isCorrect( final Number p_value )
        {
            return ( m_lower.doubleValue() <= p_value.doubleValue() ) && ( p_value.doubleValue() <= m_upper.doubleValue() );
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text", m_lower, m_upper );
        }
    }

    /**
     * string not-allowed char check
     */
    protected class CStringNotContains extends ICheck<String>
    {
        /**
         * string with non-allowed chars
         **/
        private final char[] m_notallowed;

        /**
         * String with not allowed chars
         *
         * @param p_notallowed not allowed chars
         */
        public CStringNotContains( final String p_notallowed )
        {
            m_notallowed = p_notallowed.toCharArray();
        }

        @Override
        public boolean isCorrect( final String p_value )
        {
            for ( final Character l_char : m_notallowed )
                if ( p_value.indexOf( l_char ) > -1 )
                    return false;

            return true;
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text", m_notallowed );
        }
    }

    /**
     * string empty check
     */
    protected class CStringNotEmpty extends ICheck<String>
    {
        @Override
        public boolean isCorrect( final String p_value )
        {
            return ( p_value != null ) & ( !p_value.isEmpty() );
        }

        @Override
        public String toString()
        {
            return CCommon.getResourceString( this, "text" );
        }
    }

    /**
     * check class
     *
     * @tparam T parameter of the check
     */
    protected abstract class ICheck<T>
    {
        /**
         * check of a correct value
         *
         * @param p_value value
         * @return boolean flag
         */
        public boolean isCorrect( final T p_value )
        {
            return true;
        }

        /**
         * complement of ccorrect check
         *
         * @param p_value check value
         * @return boolean
         */
        public boolean isWrong( final T p_value )
        {
            return !this.isCorrect( p_value );
        }
    }

    /**
     * class to read UTF-8 encoded property file
     *
     * @note Java default encoding for property files is ISO-Latin-1
     */
    protected class UTF8Control extends ResourceBundle.Control
    {

        public final ResourceBundle newBundle( final String p_basename, final Locale p_locale, final String p_format, final ClassLoader p_loader,
                final boolean p_reload
        ) throws IllegalAccessException, InstantiationException, IOException
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
            catch ( final Exception l_exception )
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
