/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.simulation;

import de.tu_clausthal.in.winf.CBootstrap;
import de.tu_clausthal.in.winf.CLogger;
import de.tu_clausthal.in.winf.object.world.CWorld;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * singleton object to run the simulation *
 * http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor.html
 * http://www.javacodegeeks.com/2013/03/my-custom-thread-worker-executor-in-java.html
 * http://www.coderanch.com/t/436108/threads/java/Pause-Resume-Thread
 * http://stackoverflow.com/questions/19894607/java-how-to-stop-thread
 * http://openbook.galileo-press.de/java7/1507_02_002.html#dodtp2e2b7bb6-7815-49a5-bd91-3a52aab74174
 */
public class CSimulation
{

    /**
     * singleton instance *
     */
    private static CSimulation s_instance = new CSimulation();
    /**
     * thread worker *
     */
    ExecutorService m_pool = null;
    /**
     * world of the simulation
     */
    private CWorld m_world = new CWorld();
    /**
     * integer of simulation step *
     */
    private AtomicInteger m_simulationcount = new AtomicInteger();
    /**
     * barrier object to synchronize the threads *
     */
    // CConfiguration.getInstance().get().MaxThreadNumber
    private CyclicBarrier m_barrier = new CyclicBarrier( 1 );


    /**
     * private ctor *
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
        return m_pool != null;
    }


    /**
     * returns the simulation world *
     */
    public CWorld getWorld()
    {
        return m_world;
    }


    /**
     * runs the simulation of the current step
     */
    public void start()
    {
        if ( this.isRunning() )
            throw new IllegalStateException( "simulation is running" );

        for ( ILayer l_layer : m_world.getQueue() )
            if ( ( l_layer instanceof IMultiLayer ) && ( l_layer.isActive() ) && ( ( (IMultiLayer) l_layer ).size() == 0 ) )
                CLogger.warn( "layer [" + l_layer + "] is empty" );

        CLogger.info( "simulation is started" );
        CBootstrap.BeforeSimulationStarts( this );

        m_pool = Executors.newCachedThreadPool();
        for ( int i = 0; i < m_barrier.getParties(); i++ )
            m_pool.submit( new CWorker( m_barrier, i == 0, m_simulationcount ) );
    }


    /**
     * stops the current simulation *
     */
    public void stop()
    {
        if ( !this.isRunning() )
            throw new IllegalStateException( "simulation is not running" );

        this.shutdown();
        CBootstrap.AfterSimulationStops( this );
        CLogger.info( "simulation is stopped" );
    }


    /**
     * resets the simulation data *
     */
    public void reset()
    {
        this.shutdown();

        m_simulationcount.set( 0 );
        for ( ILayer l_layer : m_world.getQueue() )
            l_layer.resetData();
        CBootstrap.onSimulationReset( this );
        CLogger.info( "simulation reset" );
    }


    /**
     * thread worker shutdown *
     */
    private void shutdown()
    {
        if ( !this.isRunning() )
            return;

        m_pool.shutdown();
        try
        {
            m_pool.awaitTermination( 2, TimeUnit.SECONDS );
            m_pool = null;
        }
        catch ( InterruptedException l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }
    }

}
