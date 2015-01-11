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
import de.tu_clausthal.in.mec.object.world.CWorld;
import de.tu_clausthal.in.mec.simulation.event.CManager;
import de.tu_clausthal.in.mec.simulation.thread.CMainLoop;

import java.io.*;


/**
 * singleton object to run the simulation *
 */
public class CSimulation
{

    /**
     * singleton instance *
     */
    private static CSimulation s_instance = new CSimulation();

    /**
     * world of the simulation
     */
    private CWorld m_world = new CWorld();

    /**
     * main loop *
     */
    private CMainLoop m_mainloop = new CMainLoop();

    /**
     * event manager *
     */
    private CManager m_eventmanager = new CManager();


    /**
     * private ctor *
     */
    private CSimulation()
    {
        new Thread( m_mainloop ).start();
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
     * returns the simulation world *
     */
    public CWorld getWorld()
    {
        return m_world;
    }

    /**
     * returns event manager *
     */
    public CManager getEventManager()
    {
        return m_eventmanager;
    }


    /**
     * runs the simulation of the current step
     */
    public void start()
    {
        if ( this.isRunning() )
            throw new IllegalStateException( "simulation is running" );

        CLogger.info( "simulation is started" );
        CBootstrap.BeforeSimulationStarts( this );

        m_mainloop.resume();
    }


    /**
     * stops the current simulation *
     */
    public void stop()
    {
        if ( !this.isRunning() )
            throw new IllegalStateException( "simulation is not running" );

        m_mainloop.pause();
        CBootstrap.AfterSimulationStops( this );
        CLogger.info( "simulation is stopped" );
    }


    /**
     * resets the simulation data *
     */
    public void reset()
    {
        m_mainloop.pause();
        m_mainloop.reset();

        CBootstrap.onSimulationReset( this );
        CLogger.info( "simulation is reset" );
    }


    /**
     * stores the simulation in an output stream
     *
     * @param p_stream output stream
     */
    public void store( ObjectOutputStream p_stream ) throws IOException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( "simulation is running" );

        p_stream.writeObject( m_world );
        CLogger.info( "simulation is stored" );
    }


    /**
     * loads the simulation from an input stream
     *
     * @param p_stream input stream
     */
    public void load( ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        if ( this.isRunning() )
            throw new IllegalStateException( "simulation is running" );

        // @todo
        // remove all bindings between objects and UI
        // and detect connection between layers


        m_eventmanager.clear();
        m_world.clear();
        m_world = (CWorld) p_stream.readObject();
        this.reset();

        // @todo
        // recreate all bindings between new objects and UI
        // and reconstruct layer connections


        CLogger.info( "simulation is loaded" );
    }

}
