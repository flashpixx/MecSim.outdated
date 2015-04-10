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

package de.tu_clausthal.in.mec.simulation;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IEvaluateLayer;
import de.tu_clausthal.in.mec.object.IFeedForwardLayer;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.world.CWorld;
import de.tu_clausthal.in.mec.simulation.message.CMessageSystem;
import de.tu_clausthal.in.mec.simulation.thread.CMainLoop;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.IViewableLayer;
import de.tu_clausthal.in.mec.ui.web.CServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * singleton object to run the simulation
 */
public class CSimulation
{

    /**
     * singleton instance
     */
    private static final CSimulation c_instance = new CSimulation();
    /**
     * main loop
     */
    private final CMainLoop m_mainloop = new CMainLoop();
    /**
     * event manager
     */
    private final CMessageSystem m_messagesystem = new CMessageSystem();
    /**
     * world of the simulation
     */
    private CWorld m_world = new CWorld();
    /**
     * object of the thread loop *
     */
    private Thread m_mainloopthread;
    /**
     * HTTP server
     */
    private CServer m_webserver;
    /**
     * UI
     */
    private CUI m_ui;


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
     * checks the running state of the simulation
     *
     * @return state
     */
    public boolean isRunning()
    {
        return !m_mainloop.isPaused();
    }


    /**
     * returns the simulation world
     */
    public CWorld getWorld()
    {
        return m_world;
    }

    /**
     * returns event manager
     */
    public CMessageSystem getMessageSystem()
    {
        return m_messagesystem;
    }

    /**
     * returns the HTTP server
     *
     * @return null or server
     */
    public CServer getWebServer()
    {
        return m_webserver;
    }

    /**
     * returns the UI
     *
     * @return UI
     */
    public CUI getUI()
    {
        return m_ui;
    }


    /**
     * returns a boolean for existing UI
     *
     * @return UI exists
     */
    public boolean hasUI()
    {
        return ( m_ui != null ) && ( m_webserver != null );
    }


    /**
     * starts the thread worker *
     */
    private void threadStartUp()
    {
        if ( m_mainloopthread != null ) return;

        m_mainloopthread = new Thread( m_mainloop );
        m_mainloopthread.start();
    }

    /**
     * runs the simulation for n steps
     *
     * @param p_steps number of steps
     * @throws InterruptedException throws the exception on thread startup
     */
    public void start( final int p_steps ) throws InterruptedException
    {
        if ( this.isRunning() ) throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        this.threadStartUp();

        CLogger.info( CCommon.getResourceString( this, "startsteps", p_steps ) );

        // run thread and wait until thread is finished
        m_mainloop.resume( p_steps );
        m_mainloopthread.join();

        m_mainloopthread = null;
    }

    /**
     * runs the simulation of the current step
     */
    public void start()
    {
        if ( this.isRunning() ) throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        this.threadStartUp();

        CLogger.info( CCommon.getResourceString( this, "start" ) );

        m_mainloop.resume();
    }


    /**
     * stops the current simulation
     */
    public void stop()
    {
        if ( !this.isRunning() ) throw new IllegalStateException( CCommon.getResourceString( this, "notrunning" ) );

        m_mainloop.pause();
        CLogger.info( CCommon.getResourceString( this, "stop" ) );
    }


    /**
     * resets the simulation data
     */
    public void reset()
    {
        this.threadStartUp();
        m_mainloop.pause();
        m_mainloop.reset();

        CBootstrap.onSimulationReset( this );
        CLogger.info( CCommon.getResourceString( this, "reset" ) );
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
        if ( this.isRunning() ) throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );


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
     * loads the simulation from an input stream
     *
     * @param p_input input file
     * @throws IOException            throws the exception on file reading error
     * @throws ClassNotFoundException throws the exception on deserialization
     * @bug incomplete routingmap
     */
    public void load( final File p_input ) throws IOException, ClassNotFoundException
    {
        if ( this.isRunning() ) throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );

        try (
                FileInputStream l_stream = new FileInputStream( p_input );
                ObjectInputStream l_input = new ObjectInputStream( l_stream );
        )
        {

            for ( ILayer l_layer : m_world.values() )
            {
                if ( l_layer instanceof ISerializable ) ( (ISerializable) l_layer ).onDeserializationInitialization();

                if ( ( l_layer instanceof IMultiLayer ) || ( l_layer instanceof IEvaluateLayer ) || ( l_layer instanceof IFeedForwardLayer ) )
                    for ( ISteppable l_item : ( (Collection<ISteppable>) l_layer ) )
                        if ( l_item instanceof ISerializable )
                            ( (ISerializable) l_item ).onDeserializationInitialization();
            }

            //CConfiguration.getInstance().get().RoutingMap = (CConfiguration.Data.RoutingMap) l_input.readObject();
            m_world.clear();
            m_world = (CWorld) l_input.readObject();

            for ( ILayer l_layer : m_world.values() )
            {
                if ( l_layer instanceof ISerializable ) ( (ISerializable) l_layer ).onDeserializationComplete();

                if ( ( l_layer instanceof IMultiLayer ) || ( l_layer instanceof IEvaluateLayer ) || ( l_layer instanceof IFeedForwardLayer ) )
                    for ( ISteppable l_item : ( (Collection<ISteppable>) l_layer ) )
                        if ( l_item instanceof ISerializable ) ( (ISerializable) l_item ).onDeserializationComplete();
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
     * UI method - reset the simulation
     */
    private void web_static_reset()
    {
        this.reset();
    }


    /**
     * UI method - returns a list with available layers
     *
     * @return
     */
    private Map<String, Map<String, Object>> web_static_listlayer()
    {
        final Map<String, Map<String, Object>> l_return = new HashMap<>();
        for ( Map.Entry<String, ILayer> l_item : m_world.entrySet() )
            l_return.put( l_item.getKey(), new HashMap()
            {{
                    put( "active", l_item.getValue().isActive() );
                    put( "visible", l_item.getValue() instanceof IViewableLayer ? ( (IViewableLayer) l_item.getValue() ).isVisible() : false );
                }} );


        return l_return;
    }


    /**
     * UI method - enables a layer
     */
    private void web_static_enablelayer( final Map<String, Object> p_data )
    {
        ( (ILayer) m_world.get( this.getLayerName( p_data ) ) ).setActive( true );
    }


    /**
     * UI method - disables a layer
     */
    private void web_static_disablelayer( final Map<String, Object> p_data )
    {
        ( (ILayer) m_world.get( this.getLayerName( p_data ) ) ).setActive( false );
    }


    /**
     * UI method - view a layer
     */
    private void web_static_showlayer( final Map<String, Object> p_data )
    {
        final Object l_layer = m_world.get( this.getLayerName( p_data ) );
        if ( l_layer instanceof IViewableLayer )
            ( (IViewableLayer) l_layer ).setVisible( true );
    }


    /**
     * UI method - hide a layer
     */
    private void web_static_hidelayer( final Map<String, Object> p_data )
    {
        final Object l_layer = m_world.get( this.getLayerName( p_data ) );
        if ( l_layer instanceof IViewableLayer )
            ( (IViewableLayer) l_layer ).setVisible( false );
    }


    /**
     * UI method - gets the layer name from the map
     *
     * @param p_data input data
     * @return layer name
     */
    private String getLayerName( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "name" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "nolayername" ) );

        final String l_name = (String) p_data.get( "name" );

        if ( !m_world.containsKey( l_name ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "layernotexists", l_name ) );

        return l_name;
    }
}
