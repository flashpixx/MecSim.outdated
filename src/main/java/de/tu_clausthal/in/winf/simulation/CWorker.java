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
import de.tu_clausthal.in.winf.object.world.CWorld;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
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
     * barrier object for synchronization *
     */
    private CyclicBarrier m_barrier = null;
    /**
     * rank defines the thread number *
     */
    private boolean m_isFirst = false;
    /**
     * current step value *
     */
    private AtomicInteger m_currentstep = null;
    /**
     * reference of the world
     */
    private CWorld m_world = null;


    /**
     * ctor to create a working process
     *
     * @param p_barrier     synchronized barrier
     * @param p_isFirst     rank ID of the process
     * @param p_world       world object
     * @param p_currentstep current step object
     */
    public CWorker(CyclicBarrier p_barrier, boolean p_isFirst, CWorld p_world, AtomicInteger p_currentstep) {
        m_barrier = p_barrier;
        m_isFirst = p_isFirst;
        m_world = p_world;
        m_currentstep = p_currentstep;
    }

    /**
     * run method - running simulation step *
     */
    public void run() {
        m_Logger.info("thread [" + Thread.currentThread().getId() + "] starts working");

        while (CSimulation.getInstance().isRunning()) {

            try {

                this.processLayer();
                this.processMultiLayerObject();

                if (m_isFirst)
                    m_currentstep.getAndIncrement();

                Thread.sleep(CConfiguration.getInstance().get().ThreadSleepTime);

            } catch (Exception l_exception) {
                LoggerFactory.getLogger(getClass()).error("thread [" + Thread.currentThread().getId() + "] is broken: ", l_exception);
            }
        }

        m_Logger.info("thread [" + Thread.currentThread().getId() + "] stops working");
    }


    /**
     * run process of each layer *
     */
    private void processLayer() throws BrokenBarrierException, InterruptedException {

        m_world.getQueue().reset();
        m_barrier.await();

        for (ILayer l_layer = null; (l_layer = m_world.getQueue().poll()) != null; m_world.getQueue().add(l_layer)) {

            if (!l_layer.isActive())
                continue;
            this.processObject(l_layer, null);

        }

    }


    /**
     * run process on each object on the multilayer *
     */
    private void processMultiLayerObject() throws BrokenBarrierException, InterruptedException {

        m_world.getQueue().reset();
        m_barrier.await();

        // all threads get the layer (so we does not remove it on getter)
        for (ILayer l_layer = null; (l_layer = m_world.getQueue().peek()) != null; m_barrier.await()) {

            // only the first remove the layer (and push it back to the queue)
            m_barrier.await();
            if (m_isFirst)
                m_world.getQueue().add( m_world.getQueue().poll() );

            if ((!l_layer.isActive()) || (!(l_layer instanceof IMultiLayer)))
                continue;

            // resets the layer and process objects of the layer
            ((IMultiLayer) l_layer).reset();
            m_barrier.await();

            for (IStepable l_object = null; (l_object = ((IMultiLayer) l_layer).poll()) != null; ((IMultiLayer) l_layer).offer(l_object)) {

                ((IMultiLayer) l_layer).beforeStepObject(m_currentstep.get(), l_object);
                this.processObject(l_object, l_layer);
                ((IMultiLayer) l_layer).afterStepObject(m_currentstep.get(), l_object);

            }

            m_barrier.await();
            if (m_isFirst)
                System.out.println("----------------------------------------------------------------------------------");
        }

    }


    /**
     * process an object
     *
     * @param p_object object which should be processed
     * @param p_layer  layer of the object or null
     */
    private void processObject(IStepable p_object, ILayer p_layer) {

        if (p_object instanceof IVoidStepable) {
            ((IVoidStepable) p_object).step(m_currentstep.get(), p_layer);
            return;
        }

        if (p_object instanceof IReturnStepable) {
            Collection l_data = ((IReturnStepable) p_object).step(m_currentstep.get(), p_layer);
            Collection<IReturnStepableTarget> l_targets = ((IReturnStepable) p_object).getTargets();
            if ((l_data != null) && (l_targets != null))
                for (IReturnStepableTarget l_target : l_targets)
                    l_target.set(l_data);
        }

    }

}
