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

package de.tu_clausthal.in.mec.simulation.thread;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.simulation.*;

import java.util.*;
import java.util.concurrent.*;


/**
 * main simulation thread
 */
public class CMainLoop implements Runnable
{
    /**
     * thread-pool for handling all objects
     */
    private ExecutorService m_pool = Executors.newWorkStealingPool();

    /**
     * task list with objects
     */
    private Collection<Callable<Object>> m_tasks = new LinkedList();

    /**
     * simulation counter
     */
    private int m_simulationcount = 0;

    /**
     * boolean to pause/resume the thread
     */
    private boolean m_pause = true;

    /**
     * number of threads for running *
     */
    private int m_shutdownstep = Integer.MAX_VALUE;


    /**
     * returns a runnable object of the stepable input
     *
     * @param p_iteration iteration
     * @param p_object    stepable object
     * @param p_layer     layer
     * @return runnable object
     */
    private static Callable createTask( int p_iteration, IStepable p_object, ILayer p_layer )
    {
        if ( p_object instanceof IVoidStepable )
            return new CVoidStepable( p_iteration, (IVoidStepable) p_object, p_layer );

        if ( p_object instanceof IReturnStepable )
            return new CReturnStepable( p_iteration, (IReturnStepable) p_object, p_layer );

        throw new IllegalArgumentException( "stepable object need not be null" );
    }


    @Override
    public void run()
    {
        CLogger.info( "thread starts working" );

        // order of all layer - the order will be read only once
        // so the thread need not be startup on program initializing
        List<ILayer> l_layerorder = CSimulation.getInstance().getWorld().getOrderedLayer();
        CLogger.info( l_layerorder );

        while ( !Thread.currentThread().isInterrupted() )
        {

            try
            {
                // if thread is paused
                if ( m_pause )
                {
                    Thread.yield();
                    continue;
                }

                // shutdown
                if ( m_simulationcount >= m_shutdownstep )
                    break;

                // if thread is not paused perform objects
                m_tasks.clear();
                m_tasks.add( new CVoidStepable( 0, CSimulation.getInstance().getEventManager(), null ) );
                for ( ILayer l_layer : l_layerorder )
                    if ( l_layer.isActive() )
                        m_tasks.add( createTask( m_simulationcount, l_layer, null ) );
                m_pool.invokeAll( m_tasks );


                m_tasks.clear();
                for ( ILayer l_layer : l_layerorder )
                    if ( ( l_layer.isActive() ) && ( l_layer instanceof IMultiLayer ) )
                    {
                        for ( Object l_object : ( (IMultiLayer) l_layer ) )
                            m_tasks.add( createTask( m_simulationcount, (IStepable) l_object, l_layer ) );
                        m_pool.invokeAll( m_tasks );
                    }


                m_simulationcount++;
                Thread.sleep( CConfiguration.getInstance().get().getThreadsleeptime() );
            }
            catch ( InterruptedException l_exception )
            {
                Thread.currentThread().interrupt();
                return;
            }
        }

        m_pause = true;
        CLogger.info( "thread stops working" );
    }


    /**
     * thread is shut down
     */
    public void stop()
    {
        Thread.currentThread().interrupt();
    }

    /**
     * checks if the thread is paused
     *
     * @return boolean for pause
     */
    public boolean isPaused()
    {
        return m_pause;
    }

    /**
     * sets pause state
     */
    public void pause()
    {
        m_pause = true;
    }

    /**
     * resume state
     */
    public void resume()
    {
        m_pause = false;
    }

    /**
     * resumes thread and shut down thread after
     */
    public void resume( int p_steps )
    {
        if ( p_steps < 1 )
            throw new IllegalArgumentException( "step number must be greater than zero" );

        m_shutdownstep = m_simulationcount + p_steps;
        m_pause = false;
    }

    /**
     * resets the thread
     */
    public void reset()
    {
        if ( !m_pause )
            throw new IllegalStateException( "simulation reset can run only on pause" );

        CLogger.info( "thread reset" );

        try
        {
            m_tasks.clear();
            m_simulationcount = 0;
            for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
                m_tasks.add( new CLayerReset( l_layer ) );
            m_pool.invokeAll( m_tasks );

        }
        catch ( InterruptedException l_exception )
        {
        }

    }

}
