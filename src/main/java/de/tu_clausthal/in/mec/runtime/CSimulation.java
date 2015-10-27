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

package de.tu_clausthal.in.mec.runtime;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiEvaluateLayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.world.CWorld;
import de.tu_clausthal.in.mec.runtime.core.CMainLoop;
import de.tu_clausthal.in.mec.runtime.message.CMessageSystem;
import de.tu_clausthal.in.mec.ui.IViewableLayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


/**
 * singleton object to performtemplate the simulation
 */
@SuppressWarnings( "serial" )
public final class CSimulation
{

    /**
     * singleton instance
     */
    private static final CSimulation c_instance = new CSimulation();
    /**
     * main loop
     */
    private final CMainLoop m_mainloop = new CMainLoop( CConfiguration.getInstance().get().<Integer>get( "simulation/threadsleeptime" ) );
    /**
     * object of the thread loop *
     */
    private Thread m_mainloopthread;
    /**
     * event manager
     */
    private final CMessageSystem m_messagesystem = new CMessageSystem();
    /**
     * object increment value - thread-safe
     */
    private final AtomicLong m_objectcounter = new AtomicLong( 0 );
    /**
     * random object
     */
    private final Random m_random = new Random();
    /**
     * global object storage
     */
    private CStorage m_storage = new CStorage();
    /**
     * world of the simulation
     */
    private CWorld m_world = new CWorld();
    /**
     * performtemplate count - counts each start call
     */
    private int m_runs = 0;

    /**
     * private ctor
     */
    private CSimulation()
    {
        CBootstrap.afterSimulationInit( this );
    }

    /**
     * returns the singelton instance
     *
     * @return simulation object
     */
    public static CSimulation getInstance()
    {
        return c_instance;
    }

    /**
     * get a object name, depend on simulation data
     *
     * @param p_input input string
     * @param p_object object for object hash
     * @return string with name
     *
     * @note %hash% with the object hash or 0, %step% with the current simulation step, %rand% with a random integer value, %inc% increment value
     */
    public final String generateObjectName( final String p_input, final Object p_object )
    {
        String l_return = new String( p_input );

        l_return = l_return.replace( "%hash%", new Integer( p_object != null ? p_object.hashCode() : 0 ).toString() );
        l_return = l_return.replace( "%step%", new Integer( m_mainloop.getSimulationstep() ).toString() );
        if ( l_return.contains( "%rand%" ) )
            l_return = l_return.replace( "%rand%", new Integer( m_random.nextInt() ).toString() );
        if ( l_return.contains( "%inc%" ) )
            l_return = l_return.replace( "%inc%", new Long( m_objectcounter.getAndIncrement() ).toString() );

        return l_return;
    }

    /**
     * returns event manager
     */
    public CMessageSystem getMessageSystem()
    {
        return m_messagesystem;
    }

    /**
     * returns the storage
     */
    public CStorage getStorage()
    {
        return m_storage;
    }

    /**
     * returns the simulation world
     */
    public CWorld getWorld()
    {
        return m_world;
    }

    /**
     * checks the running state of the simulation
     *
     * @return state
     */
    public boolean isRunning()
    {
        return !m_mainloop.isPaused();
    }

    /**
     * loads the simulation from an input stream
     *
     * @param p_input input file
     * @throws IOException throws the exception on file reading error
     * @throws ClassNotFoundException throws the exception on deserialization
     * @bug incomplete routingmap
     */
    public void load( final File p_input ) throws IOException, ClassNotFoundException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );

        try (
                FileInputStream l_stream = new FileInputStream( p_input );
                ObjectInputStream l_input = new ObjectInputStream( l_stream );
        )
        {

            for ( final ILayer l_layer : m_world.values() )
            {
                if ( l_layer instanceof ISerializable )
                    ( (ISerializable) l_layer ).onDeserializationInitialization();

                if ( ( l_layer instanceof IMultiLayer ) || ( l_layer instanceof IMultiEvaluateLayer ) )
                    for ( final ISteppable l_item : ( (Collection<ISteppable>) l_layer ) )
                        if ( l_item instanceof ISerializable )
                            ( (ISerializable) l_item ).onDeserializationInitialization();
            }

            //CConfiguration.getInstance().get().RoutingMap = (CConfiguration.Data.RoutingMap) l_input.readObject();
            m_world.clear();
            m_world = (CWorld) l_input.readObject();

            for ( final ILayer l_layer : m_world.values() )
            {
                if ( l_layer instanceof ISerializable )
                    ( (ISerializable) l_layer ).onDeserializationComplete();

                if ( ( l_layer instanceof IMultiLayer ) || ( l_layer instanceof IMultiEvaluateLayer ) )
                    for ( final ISteppable l_item : ( (Collection<ISteppable>) l_layer ) )
                        if ( l_item instanceof ISerializable )
                            ( (ISerializable) l_item ).onDeserializationComplete();
            }

            // reset all layer
            this.reset();

            CLogger.info( CCommon.getResourceString( this, "load", p_input ) );

        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( l_exception.getMessage() );
        }
    }

    /**
     * resets the simulation data
     */
    public void reset()
    {
        this.threadStartUp();
        m_mainloop.pause();
        m_mainloop.reset();
        m_objectcounter.set( 0 );
        this.callLayerReset();

        CBootstrap.onSimulationReset( this );
        CLogger.info( CCommon.getResourceString( this, "reset" ) );
    }

    /**
     * runs the simulation for n steps
     *
     * @param p_steps number of steps
     * @throws InterruptedException throws the exception on thread startup
     */
    public void start( final int p_steps ) throws InterruptedException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        this.threadStartUp();

        CLogger.info( CCommon.getResourceString( this, "startsteps", p_steps ) );

        // performtemplate thread and wait until thread is finished
        this.callLayerStart();
        m_mainloop.resume( p_steps );
        m_mainloopthread.join();

        m_mainloopthread = null;
        m_runs++;
    }

    /**
     * runs the simulation of the current step
     */
    public void start()
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        this.threadStartUp();

        CLogger.info( CCommon.getResourceString( this, "start" ) );

        this.callLayerStart();
        m_mainloop.resume();
        m_runs++;
    }

    /**
     * returns the number of runs
     *
     * @return performtemplate number
     */
    public int getNumberOfRuns()
    {
        return m_runs;
    }

    /**
     * stops the current simulation
     */
    public void stop()
    {
        if ( !this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "notrunning" ) );

        m_mainloop.pause();
        this.callLayerStop();
        CLogger.info( CCommon.getResourceString( this, "stop" ) );
    }

    /**
     * stores the simulation in an output stream
     *
     * @param p_output output file
     * @throws IOException throws the exception on file writing
     * @bug incomplete routing map
     */
    public void store( final File p_output ) throws IOException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );


        try (
                FileOutputStream l_stream = new FileOutputStream( p_output );
                ObjectOutputStream l_output = new ObjectOutputStream( l_stream );
        )
        {
            //l_output.writeObject( CConfiguration.getInstance().get().RoutingMap );
            l_output.writeObject( m_world );

            CLogger.info( CCommon.getResourceString( this, "store", p_output ) );
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( l_exception.getMessage() );
        }
    }

    /**
     * stores the current settings within the configuration
     */
    public final void setConfiguration()
    {
        CConfiguration.getInstance().get().set( "simulation/threadsleeptime", m_mainloop.getSleepTime() );
    }

    /**
     * calls on simulation start on each layer
     */
    private void callLayerStart()
    {
        for ( final ILayer l_item : m_world.values() )
            l_item.onSimulationStart();
    }

    /**
     * calls on simulation stop on each layer
     */
    private void callLayerStop()
    {
        for ( final ILayer l_item : m_world.values() )
            l_item.onSimulationStop();
    }

    /**
     * call on simulation reset on each layer
     */
    private void callLayerReset()
    {
        for ( final ILayer l_item : m_world.values() )
            l_item.onSimulationReset();
    }

    /**
     * UI method - gets the layer name from the map
     *
     * @param p_data input data
     * @return layer name
     */
    private String getLayerName( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "id" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "nolayername" ) );

        final String l_id = (String) p_data.get( "id" );
        if ( !m_world.containsKey( l_id ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "layernotexists", l_id ) );

        return l_id;
    }

    /**
     * starts the thread worker *
     */
    private void threadStartUp()
    {
        if ( m_mainloopthread != null )
            return;

        m_mainloopthread = new Thread( m_mainloop );
        m_mainloopthread.setDaemon( true );
        m_mainloopthread.setName( "simulation" );
        m_mainloopthread.start();
    }

    /**
     * UI method - disables / enables a layer
     *
     * @param p_data input data
     */
    private void web_static_disableenablelayer( final Map<String, Object> p_data )
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        if ( !p_data.containsKey( "state" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "state" ) );

        m_world.get( this.getLayerName( p_data ) ).setActive( (boolean) p_data.get( "state" ) );
    }

    /**
     * UI method - hide / show a layer
     *
     * @param p_data input data
     */
    private void web_static_hideshowlayer( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "state" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "state" ) );

        final Object l_layer = m_world.get( this.getLayerName( p_data ) );
        if ( l_layer instanceof IViewableLayer )
            ( (IViewableLayer) l_layer ).setVisible( (boolean) p_data.get( "state" ) );
    }

    /**
     * UI method - returns a list with available layers
     *
     * @return data map
     */
    private Map<String, Map<String, Object>> web_static_listlayer()
    {
        final Map<String, Map<String, Object>> l_return = new HashMap<>();
        for ( final Map.Entry<String, ILayer> l_item : m_world.entrySet() )
            l_return.put(
                    l_item.getValue().toString(), new HashMap<String, Object>()
                    {{
                        put( "id", l_item.getKey() );
                        put( "active", l_item.getValue().isActive() );
                        put( "isviewable", l_item.getValue() instanceof IViewableLayer );
                        put( "visible", l_item.getValue() instanceof IViewableLayer ? ( (IViewableLayer) l_item.getValue() ).isVisible() : false );
                    }}
            );


        return l_return;
    }

    /**
     * UI method - reset the simulation
     */
    private void web_static_reset()
    {
        this.reset();
    }

    /**
     * UI method - start the simulation
     */
    private void web_static_start()
    {
        this.start();
    }

    /**
     * UI method - stop the simulation
     */
    private void web_static_stop()
    {
        this.stop();
    }

    /**
     * UI method - set thread-sleep time
     *
     * @param p_data input data
     */
    private void web_static_setthreadsleep( final Map<String, Object> p_data )
    {
        m_mainloop.setSleepTime( ( (Number) p_data.getOrDefault( "time", m_mainloop.getSleepTime() ) ).intValue() );
    }

    /**
     * UI method - set thread-sleep time
     *
     * @return data map
     */
    private Map<String, Object> web_static_getthreadsleep()
    {
        return CCommon.getMap( "time", m_mainloop.getSleepTime() );
    }

    /**
     * peristent storage for any objects
     */
    public class CStorage
    {
        /**
         * map with (lower-case) names and elements
         */
        private final Map<String, Object> m_elements = new HashMap<>();

        /**
         * adds an element
         *
         * @param p_name name of the object
         * @param p_object object
         * @warning existing elements are not allowed and throws an exception
         */
        public final void add( final String p_name, final Object p_object )
        {
            if ( m_elements.containsKey( p_name.toLowerCase() ) )
                throw new IllegalArgumentException( CCommon.getResourceString( this, "exists", p_name ) );

            m_elements.put( p_name.toLowerCase(), p_object );
        }

        /**
         * check the map has existing elements
         *
         * @return existence flag
         */
        public final boolean exists()
        {
            return !m_elements.isEmpty();
        }

        /**
         * check if an object exists
         *
         * @param p_name name of the object
         * @return existence flag
         */
        public final boolean exists( final String p_name )
        {
            return m_elements.containsKey( p_name.toLowerCase() );
        }

        /**
         * returns the type based object
         *
         * @param p_name name of the object
         * @return object
         *
         * @tparam T type of the object
         */
        public final <T> T get( final String p_name )
        {
            return (T) m_elements.get( p_name.toLowerCase() );
        }
    }
}
