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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
            put(
                    "language", new CNameHashMap.CImmutable()
                    {{
                            put( "current", "en" );
                            put(
                                    "allow", new ArrayList<String>()
                                    {{
                                            add( "en" );
                                            add( "de" );
                                        }}
                            );
                        }}
            );


            // console data
            put(
                    "console", new CNameHashMap.CImmutable()
                    {{
                            put( "LineBuffer", 120 );
                            put( "LineNumber", 120 );
                        }}
            );


            // ui data
            put(
                    "ui", new CNameHashMap.CImmutable()
                    {{
                            put( "geoposition", new GeoPosition( 51.8089, 10.3412 ) );
                            put( "windowheight", 1024.0 );
                            put( "windowwidth", 1280.0 );
                            put( "zoom", 4 );
                            put(
                                    "server", new CNameHashMap.CImmutable()
                                    {{
                                            put( "host", "localhost" );
                                            put( "port", 9876 );
                                            put( "websocketheartbeat", 3 );
                                        }}
                            );
                        }}
            );


            // main simulation data
            put(
                    "simulation", new CNameHashMap.CImmutable()
                    {{
                            put( "threadsleeptime", 25 );

                            put(
                                    "traffic", new CNameHashMap.CImmutable()
                                    {{
                                            put( "cellsampling", 2 );
                                            put(
                                                    "routing", new CNameHashMap.CImmutable()
                                                    {{
                                                            put( "algorithm", "astarbi" );
                                                            put(
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
                                                    "map", new CNameHashMap.CImmutable()
                                                    {{
                                                            put( "reimport", false );
                                                            put( "name", "europe/germany/lowersaxony" );
                                                            put( "url", "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf" );
                                                        }}
                                            );
                                        }}
                            );
                        }}
            );


            // database data
            put(
                    "database", new CNameHashMap.CImmutable()
                    {{
                            put( "active", false );
                            put( "driver", null );
                            put( "url", null );
                            put( "tableprefix", null );
                            put( "username", null );
                            put( "password", null );
                        }}
            );


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
     * creates the default directories relative to the root dir
     */
    private void setDefaultDirectories()
    {
        for ( String l_item : new String[]{"mas", "jar", "www"} )
            m_location.put( l_item, this.getBasePath( l_item ) );
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
     * singleton get instance method
     *
     * @return configuration instance
     */
    public static CConfiguration getInstance()
    {
        return c_instance;
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
        switch ( p_language == null || p_language.isEmpty() ? m_configuration.<String>getTraverse( "language/current" ) : p_language )
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
     * creates the configuration directories
     */
    private void createDirectories() throws IOException
    {
        for ( File l_dir : m_location.values() )
            if ( !l_dir.exists() && !l_dir.mkdirs() )
                throw new IOException( CCommon.getResourceString( this, "notcreate", l_dir.getAbsolutePath() ) );
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
     * reads the configuration within the directory
     */
    public void read()
    {
        final File l_config = this.getLocation( "root", c_filename );
        CLogger.info( CCommon.getResourceString( this, "read", l_config ) );

        try
        {
            // read Json
            this.createDirectories();
            final CNameHashMap l_input = new CNameHashMap();
            l_input.putAll( CCommon.fromJson( FileUtils.readFileToString( l_config, "utf-8" ) ) );

            if ( !l_input.<Boolean>getTypedValue( "reset" ) )
                this.setConfiguration( l_input );
        }
        catch ( final IOException | NullPointerException l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        // append all Jar files to the classpath of the system class loader
        try
        {
            final URLClassLoader l_classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for ( String l_jar : m_location.get( "jar" ).list( new WildcardFileFilter( "*.jar" ) ) )
                CReflection.getClassMethod( l_classloader.getClass(), "addURL", new Class<?>[]{URL.class} ).getHandle().invoke(
                        l_classloader, CCommon.getResourceURL(
                                m_location.get(
                                        "jar"
                                ) + File.separator + l_jar
                        )
                );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }


    /**
     * sets the configuration values with semantic check
     *
     * @param p_input input map
     * @todo check version
     * @todo do configuration convert on non-existing values
     */
    private void setConfiguration( final CNameHashMap p_input )
    {
        // convert read data
        final Map<String, Double> l_geodata = ( (LinkedHashMap) ( (Map) p_input.get( "ui" ) ).get( "geoposition" ) );
        p_input.<GeoPosition>setTraverse( "ui/geoposition", new GeoPosition( l_geodata.get( "latitude" ), l_geodata.get( "longitude" ) ) );

        // check allow values
        p_input.setTraverse(
                "ui/current", CCommon.getCheckedValue(
                        p_input.<String>getTraverse( "ui/current" ), m_configuration.<String>getTraverse(
                                "ui/current"
                        ), m_configuration.<ArrayList<String>>getTraverse( "language/allow" )
                )
        );
        p_input.setTraverse(
                "simulation/traffic/routing/algorithm", CCommon.getCheckedValue(
                        p_input.<String>getTraverse(
                                "simulation/traffic/routing/algorithm"
                        ), m_configuration.<String>getTraverse(
                                "simulation/traffic/routing/algorithm"
                        ), m_configuration.<ArrayList<String>>getTraverse(
                                "simulation/traffic/routing/allow"
                        )
                )
        );

        // set data into configuration
        for ( String l_key : new String[]{"console", "ui", "reset", "database", "language", "simulation"} )
            if ( p_input.containsKey( l_key ) )
                m_configuration.put( l_key, p_input.get( l_key ) );
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
    private void web_static_set( final Map<String, Object> p_data, final Map<String, String> p_header )
    {
        if ( !( ( p_header.containsKey( "remote-addr" ) ) && ( p_header.get( "remote-addr" ).equals( "127.0.0.1" ) ) ) )
            throw new IllegalStateException( CCommon.getResourceString( this, "notallowed" ) );

        try
        {
            this.setConfiguration( new CNameHashMap.CImmutable( p_data ) );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
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
