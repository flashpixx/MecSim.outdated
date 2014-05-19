/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenpraktikum.   #
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
 @endcond
 **/

package de.tu_clausthal.in.winf.simulation;

import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.drivemodel.IDriveModel;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
import de.tu_clausthal.in.winf.objects.ICar;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * singleton object to run the simulation *
 */
public class CSimulation {

    /**
     * singleton instance *
     */
    private static CSimulation s_instance = new CSimulation();
    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * thread pool *
     */
    ExecutorService m_pool = null;
    /**
     * queue of sources *
     */
    private CQueue<ICarSourceFactory> m_sources = new CQueue();
    /**
     * queue with cars *
     */
    private CQueue<ICar> m_cars = new CQueue();
    /**
     * queue if step-interfaces *
     */
    private CQueue<ISimulationStep> m_steps = new CQueue();
    /**
     * current step of the simulation *
     */
    private AtomicInteger m_currentstep = new AtomicInteger(0);
    /**
     * barrier object to synchronize the threads *
     */
    private CyclicBarrier m_barrier = new CyclicBarrier(CConfiguration.getInstance().get().MaxThreadNumber);


    /**
     * returns the singelton instance
     *
     * @return simulation object
     */
    public static CSimulation getInstance() {
        return s_instance;
    }


    /**
     * adds a new source to the simulation
     *
     * @param p_source source object
     */
    public void addSource(ICarSourceFactory p_source) throws IllegalAccessException {
        if (this.isRunning())
            throw new IllegalAccessException("simulation is running");

        m_sources.add(p_source);
    }

    /**
     * removes a source from the simulation
     *
     * @param p_source source object
     */
    public void removeSource(ICarSourceFactory p_source) throws IllegalAccessException {
        if (this.isRunning())
            throw new IllegalAccessException("simulation is running");

        m_sources.remove(p_source);
    }


    /**
     * adds a step interface
     *
     * @param p_step item
     */
    public void registerStep(ISimulationStep p_step) {
        if (!m_steps.contains(p_step))
            m_steps.add(p_step);
    }

    /**
     * removes a step interface
     *
     * @param p_step item
     */
    public void unregisterStep(ISimulationStep p_step) {
        m_steps.remove(p_step);
    }


    /**
     * checks the running state of the simulation
     *
     * @return state
     */
    public synchronized boolean isRunning() {
        return m_pool != null;
    }


    /**
     * returns the current step
     *
     * @return current step
     */
    public int getCurrentStep() {
        return m_currentstep.intValue();
    }


    /**
     * runs the simulation of the current step
     *
     * @param p_model traffic model
     * @throws IllegalAccessException
     * @note thread pool is generated (pause structures can be created with a reentrant lock @see http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor.html )
     */
    public synchronized void start(IDriveModel p_model) throws IllegalAccessException, IllegalStateException {
        if (this.isRunning())
            throw new IllegalStateException("simulation is running");

        if (CConfiguration.getInstance().get().MaxThreadNumber < 1)
            throw new IllegalAccessException("one thread must be exists to start simulation");

        m_sources.swap();
        if (m_sources.isEmpty())
            m_Logger.warn("no sources exists");
        m_cars.swap();
        if (m_cars.isEmpty())
            m_Logger.warn("no cars exists");

        m_Logger.info("simulation is started");
        m_pool = Executors.newCachedThreadPool();
        for (int i = 0; i < CConfiguration.getInstance().get().MaxThreadNumber; i++)
            m_pool.submit(new CWorker(m_barrier, i == 0, m_currentstep, p_model, m_cars, m_sources, m_steps));
    }


    /**
     * stops the current simulation *
     */
    public synchronized void stop() throws IllegalStateException {
        if (!this.isRunning())
            throw new IllegalStateException("simulation is not running");

        m_pool.shutdown();
        try {
            m_pool.awaitTermination(2, TimeUnit.SECONDS);
            m_pool = null;
        } catch (InterruptedException l_exception) {
            m_Logger.error(l_exception.getMessage());
        }

        m_Logger.info("simulation is stopped");
    }


    /**
     * resets the simulation data *
     */
    public void reset() {
        this.stop();
        m_currentstep.set(0);
        m_cars.clear();
        CGraphHopper.getInstance().clear();
        COSMViewer.getInstance().getCarRenderer().clear();
        m_Logger.info("simulation reset");
    }

}
