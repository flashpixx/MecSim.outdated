/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
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
import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.object.world.CWorld;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * singleton object to run the simulation *
 */
public class CSimulation {

    /**
     * singleton instance *
     */
    private static volatile CSimulation s_instance = new CSimulation();
    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * thread pool *
     */
    ThreadGroup m_runners = new ThreadGroup("Simulation");
    /**
     * current step of the simulation *
     */
    private AtomicInteger m_currentstep = new AtomicInteger(0);
    /**
     * barrier object to synchronize the threads *
     */
    private CyclicBarrier m_barrier = new CyclicBarrier(CConfiguration.getInstance().get().MaxThreadNumber);
    /**
     * latch to detect that all threads are stopped
     */
    private CountDownLatch m_threadcounter = null;
    /**
     * world of the simulation
     */
    private CWorld m_world = new CWorld();


    /**
     * private ctor *
     */
    private CSimulation() {
        CBootstrap.AfterSimulationInit(this);
    }

    /**
     * returns the singelton instance
     *
     * @return simulation object
     */
    public static CSimulation getInstance() {
        return s_instance;
    }


    /**
     * checks the running state of the simulation
     *
     * @return state
     */
    public synchronized boolean isRunning() {
        return m_runners.activeCount() > 0;
    }


    /**
     * returns the simulation world *
     */
    public CWorld getWorld() {
        return m_world;
    }


    /**
     * runs the simulation of the current step
     */
    public synchronized void start() {
        if (this.isRunning())
            throw new IllegalStateException("simulation is running");
        if (m_barrier.getParties() < 1)
            throw new IllegalStateException("one thread must be exists to start simulation");

        for (ILayer l_layer : m_world.getQueue())
            if ((l_layer instanceof IMultiLayer) && (l_layer.isActive()) && (((IMultiLayer) l_layer).size() == 0))
                m_Logger.warn("layer [" + l_layer + "] is empty");

        m_Logger.info("simulation is started");
        CBootstrap.BeforeSimulationStarts(this);
        m_threadcounter = new CountDownLatch(m_barrier.getParties());
        for (int i = 0; i < m_barrier.getParties(); i++)
            new Thread(m_runners, new CWorker(m_threadcounter, m_barrier, i == 0, m_world, m_currentstep)).start();

    }


    /**
     * stops the current simulation *
     */
    public synchronized void stop() {
        if (!this.isRunning())
            throw new IllegalStateException("simulation is not running");

        this.shutdown();
        CBootstrap.AfterSimulationStops(this);
        m_Logger.info("simulation is stopped");
    }


    /**
     * resets the simulation data *
     */
    public synchronized void reset() {
        this.shutdown();

        m_currentstep.set(0);
        for (ILayer l_layer : m_world.getQueue())
            l_layer.resetData();
        m_world.getQueue().reset();
        CBootstrap.onSimulationReset(this);
        m_Logger.info("simulation reset");
    }


    /**
     * thread pool shutdown *
     */
    private synchronized void shutdown() {
        if (!this.isRunning())
            return;

        m_runners.interrupt();
        try {

            m_threadcounter.await();

        } catch (InterruptedException l_exception) {
            m_Logger.error(l_exception.getMessage());
        }

    }

}
