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

package de.tu_clausthal.in.mec.simulation;

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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private static CSimulation s_instance = new CSimulation();

    /**
     * world of the simulation
     */
    private CWorld m_world = new CWorld();

    /**
     * main loop
     */
    private CMainLoop m_mainloop = new CMainLoop();

    /**
     * event manager
     */
    private CMessageSystem m_messagesystem = new CMessageSystem();

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
        CBootstrap.AfterSimulationInit( this );
    }

    /**
     * returns the singelton instance
     *
     * @return simulation object
     */
    public static CSimulation getInstance()
    {
        return s_instance;
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
    public void setUI( CFrame p_frame )
    {
        if ( m_ui != null )
            throw new IllegalStateException( CCommon.getResouceString( this, "uiframeset" ) );
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
     */
    public void start( int p_steps ) throws InterruptedException, IllegalArgumentException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResouceString( this, "running" ) );
        this.threadStartUp();

        CLogger.info( CCommon.getResouceString( this, "startsteps", p_steps ) );
        CBootstrap.BeforeSimulationStarts( this );

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
            throw new IllegalStateException( CCommon.getResouceString( this, "running" ) );
        this.threadStartUp();

        CLogger.info( CCommon.getResouceString( this, "start" ) );
        CBootstrap.BeforeSimulationStarts( this );

        m_mainloop.resume();
    }


    /**
     * stops the current simulation
     */
    public void stop()
    {
        if ( !this.isRunning() )
            throw new IllegalStateException( CCommon.getResouceString( this, "notrunning" ) );

        m_mainloop.pause();
        CBootstrap.AfterSimulationStops( this );
        CLogger.info( CCommon.getResouceString( this, "stop" ) );
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
        CLogger.info( CCommon.getResouceString( this, "reset" ) );
    }


    /**
     * stores the simulation in an output stream
     *
     * @param p_stream output stream
     * @todo store MAS agent files also within the file (name and file content are needed)
     */
    public void store( ObjectOutputStream p_stream ) throws Exception
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResouceString( this, "running" ) );

        // read all painter object and store the list
        List<String> l_osmpainter = new ArrayList<>();
        for ( Map.Entry<String, ILayer> l_item : m_world.entrySet() )
            if ( COSMViewer.getSimulationOSM().getCompoundPainter().getPainters().contains( l_item.getValue() ) )
                l_osmpainter.add( l_item.getKey() );

        // store data (layers, OSM painter layers)
        p_stream.writeObject( m_world );
        p_stream.writeObject( l_osmpainter );


        CLogger.info( CCommon.getResouceString( this, "store" ) );
    }


    /**
     * loads the simulation from an input stream
     *
     * @param p_stream input stream
     * @todo on restore MAS agent content existing file should overwrite, if the hash of the file and stored content are
     * equal overwrite the file, otherwise rename existing file and create a new one with the store content
     */
    public void load( ObjectInputStream p_stream ) throws Exception
    {
        if ( this.isRunning() )
            throw new IllegalStateException( CCommon.getResouceString( this, "running" ) );

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

        CLogger.info( CCommon.getResouceString( this, "load" ) );
    }

}
