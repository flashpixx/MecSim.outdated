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

package de.tu_clausthal.in.winf.simulation.process;

import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.drivemodel.IDriveModel;
import de.tu_clausthal.in.winf.graph.CCellCarLinkage;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
import de.tu_clausthal.in.winf.mas.norm.IInstitution;
import de.tu_clausthal.in.winf.mas.norm.INormObject;
import de.tu_clausthal.in.winf.object.ICar;
import de.tu_clausthal.in.winf.object.ICarSourceFactory;
import de.tu_clausthal.in.winf.object.IObject;
import de.tu_clausthal.in.winf.simulation.data.CSimulationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * worker class for the simulation *
 *
 * @note Exception is catched within the run method,
 * because the method should be terminated correctly
 */
public class CWorker implements Runnable {

    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * drive model *
     */
    private IDriveModel m_drivemodel = null;
    /**
     * barrier object for synchronization *
     */
    private CyclicBarrier m_barrier = null;
    /**
     * rank defines the thread number *
     */
    private boolean m_increment = false;
    /**
     * current step value *
     */
    private AtomicInteger m_currentstep = null;

    /**
     * ctor to create a working process
     *
     * @param p_barrier     synchronized barrier
     * @param p_increment   rank ID of the process
     * @param p_currentstep current step object
     * @param p_drivemodel  driving model object
     */
    public CWorker(CyclicBarrier p_barrier, boolean p_increment, AtomicInteger p_currentstep, IDriveModel p_drivemodel) {
        m_barrier = p_barrier;
        m_increment = p_increment;
        m_currentstep = p_currentstep;
        m_drivemodel = p_drivemodel;
    }

    /**
     * run method - running simulation step *
     */
    public void run() {
        m_Logger.info("thread [" + Thread.currentThread().getId() + "] starts working");

        while (CSimulation.getInstance().isRunning()) {

            try {

                this.processListener(ListenerCall.Before);
                this.processCars();
                this.processSources();
                this.processListener(ListenerCall.After);

                if (m_increment)
                    m_currentstep.getAndIncrement();

                Thread.sleep(CConfiguration.getInstance().get().ThreadSleepTime);

            } catch (Exception l_exception) {
                LoggerFactory.getLogger(getClass()).error("thread [" + Thread.currentThread().getId() + "] is broken: ", l_exception);
            }
        }

        m_Logger.info("thread [" + Thread.currentThread().getId() + "] stops working");
    }

    /**
     * processes listener
     *
     * @throws InterruptedException
     */
    private void processListener(ListenerCall p_call) throws InterruptedException, BrokenBarrierException {

        IStep l_step = null;
        ICarSourceFactory[] l_sources = new ICarSourceFactory[CSimulationData.getInstance().getSourceQueue().unprocessedsize()];
        ICar[] l_cars = new ICar[CSimulationData.getInstance().getCarQueue().unprocessedsize()];

        CSimulationData.getInstance().getCarQueue().unprocessed2array(l_cars);
        CSimulationData.getInstance().getSourceQueue().unprocessed2array(l_sources);

        while ((l_step = CSimulationData.getInstance().getStepListenerQueue().pop()) != null) {
            try {

                switch (p_call) {
                    case Before:
                        l_step.before(m_currentstep.get(), l_sources, l_cars);
                        break;
                    case After:
                        l_step.after(m_currentstep.get(), l_sources, l_cars);
                        break;
                }

                CSimulationData.getInstance().getStepListenerQueue().push(l_step);

            } catch (Exception l_exception) {
                m_Logger.error("thread [" + Thread.currentThread().getId() + "] processListener: ", l_exception);
            }
        }

        m_barrier.await();
        CSimulationData.getInstance().getStepListenerQueue().reset();

    }

    /**
     * processes cars
     *
     * @throws InterruptedException
     */
    private void processCars() throws InterruptedException, BrokenBarrierException {

        ICar l_car = null;
        while ((l_car = CSimulationData.getInstance().getCarQueue().pop()) != null) {

            try {

                // car is at the end
                if (l_car.hasEndReached())
                    continue;

                // update car with drive model (and drive it)
                m_drivemodel.update(m_currentstep.get(), l_car);
                l_car.drive();

                // car is at the end
                if (!l_car.hasEndReached())
                    CSimulationData.getInstance().getCarQueue().push(l_car);
                else {

                    // remove car from the graph and from the mouse listener
                    CCellCarLinkage l_edge = CGraphHopper.getInstance().getEdge(l_car.getCurrentEdge());
                    if (l_edge != null)
                        l_edge.removeCarFromEdge(l_car);
                    ((IObject) l_car).release();
                }

                // call on each car all institutions
                if (l_car instanceof INormObject) {
                    ((INormObject) l_car).clearMatchedNorm();
                    for (IInstitution<INormObject> l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
                        l_item.check((INormObject) l_car);
                }

            } catch (Exception l_exception) {
                m_Logger.error("thread [" + Thread.currentThread().getId() + "] processCars: ", l_exception);
            }
        }

        m_barrier.await();
        CSimulationData.getInstance().getCarQueue().reset();

    }

    /**
     * processes sources
     *
     * @throws InterruptedException
     */
    private void processSources() throws InterruptedException, BrokenBarrierException {

        ICarSourceFactory l_source = null;
        while ((l_source = CSimulationData.getInstance().getSourceQueue().pop()) != null) {
            try {

                CSimulationData.getInstance().getCarQueue().add(l_source.generate(m_currentstep.get()));
                CSimulationData.getInstance().getSourceQueue().push(l_source);

            } catch (Exception l_exception) {
                LoggerFactory.getLogger(getClass()).error("thread [" + Thread.currentThread().getId() + "] processSources: ", l_exception);
            }

        }

        m_barrier.await();
        CSimulationData.getInstance().getSourceQueue().reset();

    }


    /**
     * enum listener call definition *
     */
    private enum ListenerCall {
        Before, After
    }

}
