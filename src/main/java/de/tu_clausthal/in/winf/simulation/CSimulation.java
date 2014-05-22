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

import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.drivemodel.IDriveModel;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
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

        CSimulationData.getInstance().getSourceQueue().swap();
        if (CSimulationData.getInstance().getSourceQueue().isEmpty())
            m_Logger.warn("no sources exists");
        CSimulationData.getInstance().getCarQueue().swap();
        if (CSimulationData.getInstance().getCarQueue().isEmpty())
            m_Logger.warn("no cars exists");

        m_Logger.info("simulation is started");
        m_pool = Executors.newCachedThreadPool();
        for (int i = 0; i < CConfiguration.getInstance().get().MaxThreadNumber; i++)
            m_pool.submit(new CWorker(m_barrier, i == 0, m_currentstep, p_model));
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
     *
     * @bug reset
     */
    public void reset() {
        this.stop();
        m_currentstep.set(0);
        CGraphHopper.getInstance().clear();
        //COSMViewer.getInstance().clearOverlay("car");
        m_Logger.info("simulation reset");
    }

}
