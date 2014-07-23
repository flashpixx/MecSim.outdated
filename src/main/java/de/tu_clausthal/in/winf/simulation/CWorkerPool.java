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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * thread pool structure to encapsulate dynamic simualtion thread group
 */
public class CWorkerPool {

    /**
     * thread group to bundle all working threads *
     */
    private ThreadGroup m_runners = new ThreadGroup(this.toString());

    /**
     * barrier object to synchronize the threads *
     */
    private CyclicBarrier m_barrier = null;

    /**
     * latch to detect thread finishing *
     */
    private CountDownLatch m_threadcounter = null;


    /**
     * ctor to initialize the thread group
     *
     * @param p_size            number of threads
     * @param p_simulationcount atomic counter of simulation step
     */
    public CWorkerPool(int p_size, AtomicInteger p_simulationcount) {
        if (p_size < 1)
            throw new IllegalArgumentException("one thread must be exists to start simulation");
        if (p_simulationcount == null)
            throw new IllegalArgumentException("simulation counter need not to be null");

        m_barrier = new CyclicBarrier(p_size);
        m_threadcounter = new CountDownLatch(p_size);

        for (int i = 0; i < p_size; i++)
            new Thread(m_runners, new CWorker(m_threadcounter, m_barrier, i == 0, p_simulationcount)).start();
    }

    /**
     * flag to detect a running process
     *
     * @return boolean for activity
     */
    public boolean isRunning() {
        return m_runners.activeCount() > 0;
    }


    /**
     * stops the current pool
     *
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        if (!this.isRunning())
            return;

        m_runners.interrupt();
        m_threadcounter.await();
    }

}
