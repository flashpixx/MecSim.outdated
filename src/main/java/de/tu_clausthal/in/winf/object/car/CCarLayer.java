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

package de.tu_clausthal.in.winf.object.car;

import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.object.car.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.winf.object.car.drivemodel.IDriveModel;
import de.tu_clausthal.in.winf.object.car.graph.CCellCarLinkage;
import de.tu_clausthal.in.winf.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;
import de.tu_clausthal.in.winf.simulation.IReturnStepableTarget;

import java.io.File;
import java.util.Collection;


/**
 * defines the layer for cars
 */
public class CCarLayer extends IMultiLayer<ICar> implements IReturnStepableTarget<ICar> {

    /**
     * drive model *
     */
    private IDriveModel m_drivemodel = new CNagelSchreckenberg();
    /**
     * graph *
     */
    private CGraphHopper m_graph = new CGraphHopper(CConfiguration.getInstance().getConfigDir() + File.separator + "graphs" + File.separator + CConfiguration.getInstance().get().RoutingMap.replace('/', '_'));


    /**
     * returns the graph of the layer
     *
     * @return graph object
     */
    public CGraphHopper getGraph() {
        return m_graph;
    }


    /**
     * sets the graph with a weight
     *
     * @param p_weight weight name
     */
    public void setGraphWeight(String p_weight) {
        m_graph = new CGraphHopper(CConfiguration.getInstance().getConfigDir() + File.separator + "graphs" + File.separator + CConfiguration.getInstance().get().RoutingMap.replace('/', '_'), p_weight);
    }


    /**
     * sets the drive model
     *
     * @param p_model model
     */
    public void setDriveModel(IDriveModel p_model) {
        if (p_model == null)
            throw new IllegalArgumentException("drive model need not be null");
        m_drivemodel = p_model;
    }


    @Override
    public void beforeStepObject(int p_currentstep, ICar p_object) {
        m_drivemodel.update(p_currentstep, p_object);
    }

    @Override
    public void afterStepObject(int p_currentstep, ICar p_object) {

        if (p_object.hasEndReached()) {
            super.remove(p_object);
            CCellCarLinkage l_edge = m_graph.getEdge(p_object.getCurrentEdge());
            if (l_edge != null)
                l_edge.removeCarFromEdge(p_object);
        }

    }

    @Override
    public void set(Collection<ICar> p_data) {
        super.addAll(p_data);
    }
}
