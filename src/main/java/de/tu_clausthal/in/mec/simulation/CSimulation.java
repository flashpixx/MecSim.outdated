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

package de.tu_clausthal.in.mec.simulation;

import com.google.gson.Gson;
import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.world.CWorld;
import de.tu_clausthal.in.mec.simulation.message.CMessageSystem;
import de.tu_clausthal.in.mec.simulation.thread.CMainLoop;
import de.tu_clausthal.in.mec.ui.CFrame;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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
    private Thread m_mainloopthread = null;

    /**
     * frame object
     */
    private CFrame m_ui = null;


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
     * returns the UI frame
     *
     * @return null or frame
     */
    public CFrame getUI()
    {
        return m_ui;
    }

    /**
     * sets the UI frame
     *
     * @param p_frame frame object
     */
    public void setUI( final CFrame p_frame )
    {
        if ( m_ui != null )
            throw new IllegalStateException( CCommon.getResourceString(this, "uiframeset") );
        m_ui = p_frame;
    }

    /**
     * returns a boolean for existing UI
     *
     * @return UI exists
     */
    public boolean hasUI()
    {
        return m_ui != null;
    }


    /**
     * starts the thread worker *
     */
    private void threadStartUp()
    {
        if ( m_mainloopthread != null )
            return;

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
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString(this, "running") );
        this.threadStartUp();

        CLogger.info( CCommon.getResourceString(this, "startsteps", p_steps) );
        CBootstrap.beforeSimulationStarts( this );

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
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString(this, "running") );
        this.threadStartUp();

        CLogger.info( CCommon.getResourceString(this, "start") );
        CBootstrap.beforeSimulationStarts( this );

        m_mainloop.resume();
    }


    /**
     * stops the current simulation
     */
    public void stop()
    {
        if ( !this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString(this, "notrunning") );

        m_mainloop.pause();
        CBootstrap.afterSimulationStops( this );
        CLogger.info( CCommon.getResourceString(this, "stop") );
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
        CLogger.info( CCommon.getResourceString(this, "reset") );
    }


    /**
     * stores the simulation in an output stream
     *
     * @param p_output output stream
     * @throws IOException throws the exception on file writing
     * @todo store MAS agent files also within the file (name and file content are needed)
     */
    public void store( final File p_output ) throws IOException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString(this, "running") );

        // read all painter object and store the list
        final List<String> l_osmpainter = new ArrayList<>();
        for ( Map.Entry<String, ILayer> l_item : m_world.entrySet() )
            if ( COSMViewer.getSimulationOSM().getCompoundPainter().getPainters().contains( l_item.getValue() ) )
                l_osmpainter.add( l_item.getKey() );

        // store data (layers, OSM painter layers)
        Writer l_writer = new OutputStreamWriter( new FileOutputStream( p_output ), "UTF-8" );
        new Gson().toJson( new CStorage( m_world, l_osmpainter ), l_writer );
        l_writer.close();

        CLogger.info( CCommon.getResourceString(this, "store") );
    }


    /**
     * loads the simulation from an input stream
     *
     * @param p_stream input stream
     * @throws IOException            throws the exception on file reading error
     * @throws ClassNotFoundException throws the exception on deserialization
     * @todo on restore MAS agent content existing file should overwrite, if the hash of the file and stored content are
     * equal overwrite the file, otherwise rename existing file and create a new one with the store content
     */
    public void load( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResourceString(this, "running") );

        if ( this.hasUI() )
            for ( Map.Entry<String, ILayer> l_item : m_world.entrySet() )
            {
                // check if layer is a JXMapViewer Painter
                if ( l_item.getValue() instanceof Painter )
                    COSMViewer.getSimulationOSM().getCompoundPainter().removePainter( (Painter) l_item.getValue() );
                m_ui.removeWidget( l_item.getKey() );
            }

        // clear and load world
        m_world.clear();
        m_world = (CWorld) p_stream.readObject();

        // restore OSM painter layers
        if ( this.hasUI() )
            for ( String l_item : (ArrayList<String>) p_stream.readObject() )
                COSMViewer.getSimulationOSM().getCompoundPainter().addPainter( (Painter) m_world.get( l_item ) );


        // reset all layer
        this.reset();

        CLogger.info( CCommon.getResourceString(this, "load") );
    }


    protected static class CStorage
    {
        public CWorld m_world = null;

        public List<String> m_painter = null;

        public CStorage( CWorld p_world, List<String> p_painter )
        {
            m_world = p_world;
            m_painter = p_painter;
        }

    }

}
