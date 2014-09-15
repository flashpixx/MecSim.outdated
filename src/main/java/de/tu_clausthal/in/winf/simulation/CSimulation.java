/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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
import de.tu_clausthal.in.winf.CLogger;
import de.tu_clausthal.in.winf.object.world.CWorld;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;

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
     * world of the simulation
     */
    private CWorld m_world = new CWorld();
    /**
     * pool of worker threads *
     */
    private CWorkerPool m_worker = null;
    /**
     * integer of simulation step *
     */
    private AtomicInteger m_simulationcount = new AtomicInteger();
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
    public boolean isRunning() {
        return m_worker != null;
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
    public void start() {
        if (this.isRunning())
            throw new IllegalStateException("simulation is running");

        for (ILayer l_layer : m_world.getQueue())
            if ((l_layer instanceof IMultiLayer) && (l_layer.isActive()) && (((IMultiLayer) l_layer).size() == 0))
                CLogger.warn("layer [" + l_layer + "] is empty");

        CLogger.info("simulation is started");
        CBootstrap.BeforeSimulationStarts(this);
        m_worker = new CWorkerPool(CConfiguration.getInstance().get().MaxThreadNumber, m_simulationcount);
    }


    /**
     * stops the current simulation *
     */
    public void stop() {
        if (!this.isRunning())
            throw new IllegalStateException("simulation is not running");

        this.shutdown();
        CBootstrap.AfterSimulationStops(this);
        CLogger.info("simulation is stopped");
    }


    /**
     * resets the simulation data *
     */
    public void reset() {
        this.shutdown();

        m_simulationcount.set(0);
        for (ILayer l_layer : m_world.getQueue())
            l_layer.resetData();
        CBootstrap.onSimulationReset(this);
        CLogger.info("simulation reset");
    }


    /**
     * thread pool shutdown *
     */
    private void shutdown() {
        if (!this.isRunning())
            return;

        try {
            m_worker.stop();
        } catch (InterruptedException l_exception) {
            CLogger.error(l_exception.getMessage());
        }

        m_worker = null;
    }

}
