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

import de.tu_clausthal.in.winf.objects.ICar;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import de.tu_clausthal.in.winf.util.CPainterQueue;
import de.tu_clausthal.in.winf.util.CQueue;
import de.tu_clausthal.in.winf.util.CWaypointQueue;

/**
 * data structure of the simulation and view
 */
public class CSimulationData {

    /**
     * singleton instance *
     */
    static CSimulationData s_instance = new CSimulationData();
    /**
     * queue for cars *
     */
    private CPainterQueue<ICar> m_cars = new CPainterQueue();
    /**
     * queue for sources *
     */
    private CWaypointQueue<ICarSourceFactory> m_sources = new CWaypointQueue(COSMViewer.getInstance());
    /**
     * queue for step listeners *
     */
    private CQueue<ISimulationStep> m_steplistener = new CQueue();


    /**
     * private ctor *
     */
    private CSimulationData() {
        //COSMViewer.getInstance().setOverlayPainter(m_cars);
        COSMViewer.getInstance().setOverlayPainter(m_sources);
    }


    /**
     * returns singleton instance
     *
     * @return instance
     */
    public static CSimulationData getInstance() {
        return s_instance;
    }


    /**
     * returns the care queue
     *
     * @return queue object
     */
    public CPainterQueue<ICar> getCarQueue() {
        return m_cars;
    }


    /**
     * returns the source queue
     *
     * @return queue
     */
    public CWaypointQueue<ICarSourceFactory> getSourceQueue() {
        return m_sources;
    }


    /**
     * returns the step listener queue
     *
     * @return queue
     */
    public CQueue<ISimulationStep> getStepListenerQueue() {
        return m_steplistener;
    }

}
