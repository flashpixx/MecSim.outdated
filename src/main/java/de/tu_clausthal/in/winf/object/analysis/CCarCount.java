/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
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

package de.tu_clausthal.in.winf.object.analysis;

import de.tu_clausthal.in.winf.object.car.CCarLayer;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.ISingleLayer;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.ui.CFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.Map;


/**
 * car count statistic
 */
public class CCarCount extends ISingleLayer {

    /**
     * data set *
     */
    DefaultCategoryDataset m_plotdata = new DefaultCategoryDataset();

    /**
     * ctor
     *
     * @param p_frame frame objct
     */
    public CCarCount(CFrame p_frame) {
        p_frame.addUIComponent("Car Count", new ChartPanel(ChartFactory.createLineChart("Car Count", "time", "number of cars", m_plotdata, PlotOrientation.VERTICAL, false, false, false)));
    }


    @Override
    public void step(int p_currentstep, ILayer p_layer) {
        m_plotdata.addValue(((CCarLayer) CSimulation.getInstance().getWorld().getMap().get("Car")).getGraph().getNumberOfCars(), "number", String.valueOf(p_currentstep));
    }

    @Override
    public void resetData() {
        m_plotdata.clear();
    }

    @Override
    public Map<String, Object> analyse() {
        return null;
    }
}
