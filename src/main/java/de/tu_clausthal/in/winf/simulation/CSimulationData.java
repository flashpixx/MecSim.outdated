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

import de.tu_clausthal.in.winf.object.car.ICar;
import de.tu_clausthal.in.winf.object.norm.INormObject;
import de.tu_clausthal.in.winf.object.norm.institution.IInstitution;
import de.tu_clausthal.in.winf.object.source.ICarSourceFactory;
import de.tu_clausthal.in.winf.simulation.process.IStep;
import de.tu_clausthal.in.winf.simulation.world.CWorld;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import de.tu_clausthal.in.winf.util.CPainterQueue;
import de.tu_clausthal.in.winf.util.CQueue;
import de.tu_clausthal.in.winf.util.CWaypointQueue;

/**
 * data structure of the simulation and view
 *
 * @deprecated
 */
public class CSimulationData {

    /**
     * singleton instance *
     */
    private static volatile CSimulationData s_instance = new CSimulationData();
    /**
     * queue for sources *
     */
    private CWaypointQueue<ICarSourceFactory> m_sources = new CWaypointQueue(COSMViewer.getInstance());
    /**
     * queue for cars *
     */
    private CPainterQueue<ICar> m_cars = new CPainterQueue(COSMViewer.getInstance());
    /**
     * queue for step listeners *
     */
    private CQueue<IStep> m_steplistener = new CQueue();
    /**
     * car institution *
     */
    private CPainterQueue<IInstitution<INormObject>> m_carinstitution = new CPainterQueue(COSMViewer.getInstance());
    /**
     * world layer structure
     */
    private CWorld m_world = new CWorld(COSMViewer.getInstance());


    /**
     * private ctor *
     */
    private CSimulationData() {
    }


    /**
     * returns singleton instance
     *
     * @return instance
     */
    public static CSimulationData getInstance() {
        return s_instance;
    }


    public CWorld getWorld() {
        return m_world;
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
    public CQueue<IStep> getStepListenerQueue() {
        return m_steplistener;
    }


    /**
     * returns the car institution
     *
     * @return car institution
     */
    public CPainterQueue<IInstitution<INormObject>> getCarInstitutionQueue() {
        return m_carinstitution;
    }

}
