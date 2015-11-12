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

package de.tu_clausthal.in.mec.runtime.core;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiEvaluateLayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.ISingleEvaluateLayer;
import de.tu_clausthal.in.mec.object.ISingleLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.runtime.ISteppable;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import de.tu_clausthal.in.mec.runtime.benchmark.IBenchmark;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * main simulation thread
 */
public final class CMainLoop implements Runnable
{
    /**
     * boolean to pause/resume the thread
     */
    private boolean m_pause = true;
    /**
     * thread-pool for handling all objects
     */
    private final ExecutorService m_pool = Executors.newWorkStealingPool();
    /**
     * number of threads for running *
     */
    private int m_shutdownstep = Integer.MAX_VALUE;
    /**
     * simulation counter
     */
    private int m_simulationcount;
    /**
     * thread sleep time
     */
    private volatile int m_sleeptime;

    /**
     * ctor
     *
     * @param p_sleeptime thread-sleep time
     */
    public CMainLoop( final int p_sleeptime )
    {
        this.setSleepTime( p_sleeptime );
    }

    /**
     * returns the thread-sleep time
     *
     * @return thread-sleep time
     */
    public int getSleepTime()
    {
        return m_sleeptime;
    }

    /**
     * changes the thread-sleep time
     *
     * @param p_sleeptime thread-sleep time
     **/
    public void setSleepTime( final int p_sleeptime )
    {
        m_sleeptime = Math.abs( p_sleeptime );
    }

    /**
     * returns the simulation step
     *
     * @return step number
     */
    public final int getSimulationstep()
    {
        return m_simulationcount;
    }

    /**
     * checks if the thread is paused
     *
     * @return boolean for pause
     */
    public final boolean isPaused()
    {
        return m_pause;
    }

    /**
     * sets pause state
     */
    public final void pause()
    {
        m_pause = true;
    }

    /**
     * resets the thread
     */
    public final void reset()
    {
        if ( !m_pause )
            throw new IllegalStateException( CCommon.getResourceString( this, "pause" ) );

        CLogger.info( CCommon.getResourceString( this, "reset" ) );
        m_simulationcount = 0;
    }

    /**
     * resume state
     */
    public final void resume()
    {
        m_pause = false;
    }

    /**
     * resumes thread and shut down thread after
     *
     * @param p_steps number of steps which are performtemplate
     */
    public final void resume( final int p_steps )
    {
        if ( p_steps < 1 )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "stepnumber" ) );

        m_shutdownstep = m_simulationcount + p_steps;
        m_pause = false;
    }

    @Override
    public final void run()
    {
        CLogger.info( CCommon.getResourceString( this, "start" ) );

        // order of all layer - the order will be read only once
        // so the thread need not be startup on program initializing
        final List<ILayer> l_layerorder = CSimulation.getInstance().getWorld().getOrderedLayer();
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

                // performtemplate simulation objects
                this.processLayer( l_layerorder );
                this.processObjects( l_layerorder );

                m_simulationcount++;
                Thread.sleep( m_sleeptime );
            }
            catch ( final InterruptedException l_exception )
            {
                Thread.currentThread().interrupt();
                return;
            }
        }

        m_pool.shutdownNow();
        CLogger.info( CCommon.getResourceString( this, "stop" ) );
    }

    /**
     * thread is shut down
     */
    public final void stop()
    {
        Thread.currentThread().interrupt();
    }

    /**
     * invokes all defined data
     *
     * @param p_layer layer
     * @param p_tasksource invoking tasks
     * @throws InterruptedException is throws on task error
     */
    protected final void invokeTasks( final ILayer p_layer, final Collection<ISteppable> p_tasksource ) throws InterruptedException
    {
        m_pool.invokeAll(
                p_tasksource.parallelStream().map(
                        i -> createTask( m_simulationcount, i, p_layer )
                ).collect( Collectors.toCollection( () -> new LinkedList<>() ) )
        );
    }

    /**
     * returns a runnable object of the steppable input
     *
     * @param p_iteration iteration
     * @param p_object steppable object
     * @param p_layer layer
     * @return runnable object
     */
    @SuppressWarnings( "unchecked" )
    private static Callable<Object> createTask( final int p_iteration, final ISteppable p_object, final ILayer p_layer )
    {
        if ( p_object instanceof IVoidSteppable )
            return new CVoidSteppable( p_iteration, (IVoidSteppable) p_object, p_layer );

        if ( p_object instanceof IReturnSteppable )
            return new CReturnSteppable( p_iteration, (IReturnSteppable) p_object, p_layer );

        throw new IllegalArgumentException( CCommon.getResourceString( CMainLoop.class, "notsteppable" ) );
    }


    /**
     * process layer
     *
     * @param p_layer ordered layer list
     * @throws InterruptedException thrown on thread error
     */
    @IBenchmark
    private void processLayer( final List<ILayer> p_layer ) throws InterruptedException
    {
        final Collection<Callable<Object>> l_tasks = new LinkedList<>();

        l_tasks.add( new CVoidSteppable( m_simulationcount, CSimulation.getInstance().getMessageSystem(), null ) );
        p_layer.stream().filter( i -> i.isActive() ).map( i -> createTask( m_simulationcount, i, null ) ).forEachOrdered( l_tasks::add );

        m_pool.invokeAll( l_tasks );
    }

    /**
     * process layer object
     *
     * @param p_layer ordered layer list
     * @note only multi-, evaluate- & network layer can store other objects
     */
    @IBenchmark
    @SuppressWarnings( "unchecked" )
    private void processObjects( final List<ILayer> p_layer )
    {
        p_layer.stream().filter( i -> i.isActive() && ( !( i instanceof ISingleLayer ) ) && ( !( i instanceof ISingleEvaluateLayer ) ) ).forEachOrdered( i -> {

            try
            {
                if ( i instanceof IMultiLayer<?> )
                    this.invokeTasks( i, (IMultiLayer) i );

                if ( i instanceof IMultiEvaluateLayer<?> )
                    this.invokeTasks( i, (IMultiEvaluateLayer) i );
            }
            catch ( final InterruptedException l_exception )
            {
                CLogger.error( l_exception );
            }
        } );
    }

}
