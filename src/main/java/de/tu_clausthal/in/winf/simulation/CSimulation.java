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
import de.tu_clausthal.in.winf.simulation.thread.CMainLoop;


/**
 * singleton object to run the simulation *
 * http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor.html
 * http://www.javacodegeeks.com/2013/03/my-custom-thread-worker-executor-in-java.html
 * http://www.coderanch.com/t/436108/threads/java/Pause-Resume-Thread
 * http://stackoverflow.com/questions/19894607/java-how-to-stop-thread
 * http://openbook.galileo-press.de/java7/1507_02_002.html#dodtp2e2b7bb6-7815-49a5-bd91-3a52aab74174
 * https://today.java.net/article/2011/06/14/method-reducing-contention-and-overhead-worker-queues-multithreaded-java-applications
 * https://www.igvita.com/2012/02/29/work-stealing-and-recursive-partitioning-with-fork-join/
 * http://www.heise.de/developer/artikel/Das-Fork-Join-Framework-in-Java-7-1755690.html
 * http://www.itcuties.com/java/executorservice/
 * http://www.javacodegeeks.com/2011/12/using-threadpoolexecutor-to-parallelize.html
 * http://www.javaworld.com/article/2078809/java-concurrency/java-101-the-next-generation-java-concurrency-without-the-pain-part-1.html?null
 * http://technicalmumbojumbo.wordpress.com/2011/05/16/java-util-concurrent-executors-thread-pools-cache-fixed-scheduled-executorcompletionservice-tutorial/
 * http://tutorials.jenkov.com/java-util-concurrent/executorservice.html
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

    private CMainLoop m_run = null;


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
        return m_run != null;
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
        CLogger.info( "simulation is started" );
        CBootstrap.BeforeSimulationStarts( this );

        m_run = new CMainLoop();
        new Thread( m_run ).start();

        /*
        if ( this.isRunning() )
            throw new IllegalStateException( "simulation is running" );

        for ( ILayer l_layer : m_world.getQueue() )
            if ( ( l_layer instanceof IMultiLayer ) && ( l_layer.isActive() ) && ( ( (IMultiLayer) l_layer ).size() == 0 ) )
                CLogger.warn( "layer [" + l_layer + "] is empty" );
        */
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
        /*
        this.shutdown();

        m_simulationcount.push( 0 );
        for ( ILayer l_layer : m_world.getQueue() )
            l_layer.resetData();
        CBootstrap.onSimulationReset( this );
        CLogger.info( "simulation reset" );
        */
    }


    /**
     * thread worker shutdown *
     */
    private void shutdown()
    {
        if ( !this.isRunning() )
            return;

        m_run.stop();
        m_run = null;
    }

}
