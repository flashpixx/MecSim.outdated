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

package de.tu_clausthal.in.mec;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CNameHashMap;
import de.tu_clausthal.in.mec.common.CReflection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    private static final String c_filename = "config.json";
    /**
     * singleton instance variable
     */
    private static final CConfiguration c_instance = new CConfiguration();
    /**
     * location map
     */
    private final Map<String, File> m_location = new HashMap<String, File>()
    {{
            put( "root", new File( System.getProperty( "user.home" ) + File.separator + ".mecsim" ) );
        }};
    /**
     * configuration map
     */
    private final CNameHashMap.CImmutable m_configuration = new CNameHashMap.CImmutable()
    {{
            put( "reset", false );
            put( "version", 1 );


            // language data
            put( "language", new CNameHashMap.CImmutable()
            {{
                    put( "current", "en" );
                    put( "allow", new String[]{"en", "de"} );
                }} );


            // console data
            put( "console", new CNameHashMap.CImmutable()
            {{
                    put( "LineBuffer", 120 );
                    put( "LineNumber", 120 );
                }} );


            // ui data
            put( "ui", new CNameHashMap.CImmutable()
            {{
                    put( "geoposition", new GeoPosition( 51.8089, 10.3412 ) );
                    put( "windowheight", 1024.0 );
                    put( "windowwidth", 1280.0 );
                    put( "bindport", 9876 );
                    put( "zoom", 4 );
                }} );


            // main simulation data
            put( "simulation", new CNameHashMap.CImmutable()
            {{
                    put( "threadsleeptime", 25 );

                    put( "traffic", new CNameHashMap.CImmutable()
                    {{
                            put( "cellsampling", 2 );
                            put( "routing", new CNameHashMap.CImmutable()
                            {{
                                    put( "algorithm", "astarbi" );
                                    put( "allow", new String[]{"astar", "astarbi", "dijkstra", "dijkstrabi", "dijkstraOneToMany"} );
                                }} );
                            put( "map", new CNameHashMap.CImmutable()
                            {{
                                    put( "reimport", false );
                                    put( "name", "europe/germany/lowersaxony" );
                                    put( "url", "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf" );
                                }} );
                        }} );
                }} );


            // database data
            put( "database", new CNameHashMap.CImmutable()
            {{
                    put( "active", false );
                    put( "driver", null );
                    put( "url", null );
                    put( "tableprefix", null );
                    put( "username", null );
                    put( "password", null );
                }} );


            // manifest data
            put( "manifest", new CNameHashMap.CImmutable() );

        }};
    /**
     * UTF-8 property reader
     */
    private ResourceBundle.Control m_reader = new UTF8Control();


    /**
     * private Ctor to avoid manual instantiation with manifest reading
     */
    private CConfiguration()
    {
        try
        {
            final Manifest l_manifest = new JarFile( this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() ).getManifest();
            for ( Map.Entry<Object, Object> l_item : l_manifest.getMainAttributes().entrySet() )
                ( (Map) m_configuration.get( "manifest" ) ).put( l_item.getKey().toString().toLowerCase(), l_item.getValue().toString() );
        }
        catch ( final IOException l_exception )
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
        if ( ( p_varargs == null ) || ( p_varargs.length == 0 ) ) return m_location.get( p_name );

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
        switch ( m_configuration.<String>getTraverse( "language/current" ) )
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
            FileUtils.writeStringToFile( this.getLocation( "root", c_filename ), CCommon.toJson( m_configuration ) );

        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }
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
     * reads the configuration within the directory
     *
     * @todo check default values and base initialization
     */
    public void read()
    {

        // create the configuration directory
        try
        {
            this.createDirectories();
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        final File l_config = this.getLocation( "root", c_filename );
        CLogger.info( CCommon.getResourceString( this, "read", l_config ) );

        // read main configuration
        try (
                Reader l_reader = new InputStreamReader( new FileInputStream( l_config ), "UTF-8" );
        )
        {
            //l_tmp = new Gson().fromJson( l_reader, Data.class );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        // check the configuration values and set it
        /*
        if ( ( l_tmp == null ) || ( l_tmp.ResetConfig ) || ( l_tmp.Version < m_data.Version ) )
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
            /*
            if ( l_tmp.Console == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "consoledefault" ) );
                l_tmp.Console = m_data.Console;
            }

            m_data = l_tmp;

        }
        */

        // append all Jar files to the classpath of the system class loader
        try
        {
            URLClassLoader l_classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for ( String l_jar : m_location.get( "jar" ).list( new WildcardFileFilter( "*.jar" ) ) )
                CReflection.getClassMethod( l_classloader.getClass(), "addURL", new Class<?>[]{URL.class} ).getHandle().invoke( l_classloader, CCommon.getResourceURL( m_location.get( "jar" ) + File.separator + l_jar ) );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }

    /**
     * UI method to read configuration
     *
     * @return data
     */
    private CNameHashMap.CImmutable web_static_get()
    {
        return m_configuration;
    }


    /**
     * UI method to set data
     *
     * @param p_data   input data
     * @param p_header header data - configuration changeable only from localhost
     * @bug incomplete
     */
    private void web_static_set( final Map<String, Object> p_data, final Map<String, String> p_header )
    {
        if ( !( ( p_header.containsKey( "remote-addr" ) ) && ( p_header.get( "remote-addr" ).equals( "127.0.0.1" ) ) ) )
            throw new IllegalStateException( CCommon.getResourceString( this, "notallowed" ) );

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

            if ( !p_reload ) l_stream = p_loader.getResourceAsStream( l_resource );
            else
            {

                final URL l_url = p_loader.getResource( l_resource );
                if ( l_url == null ) return null;

                final URLConnection l_connection = l_url.openConnection();
                if ( l_connection == null ) return null;

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
