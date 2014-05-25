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

package de.tu_clausthal.in.winf.analysis;

import de.tu_clausthal.in.winf.objects.ICar;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.simulation.ISimulationStep;
import de.tu_clausthal.in.winf.ui.CFrame;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.concurrent.ConcurrentHashMap;


/**
 * statistic map for creating plots of any data *
 * * @see http://commons.apache.org/proper/commons-math/userguide/stat.html
 */
public class CStatisticMap implements ISimulationStep {

    /**
     * singleton instance of the class *
     */
    private static volatile CStatisticMap s_instance = new CStatisticMap();
    /**
     * thread-safe static structure *
     */
    private ConcurrentHashMap<String, SynchronizedDescriptiveStatistics> m_statisticdata = new ConcurrentHashMap();

    /**
     * thread-safe map with plot objects *
     */
    private ConcurrentHashMap<String, DefaultCategoryDataset> m_plotdata = new ConcurrentHashMap();

    /**
     * frame to add the charts
     */
    private CFrame m_frame = null;


    /**
     * returns the instance
     *
     * @return map instance
     */
    static public CStatisticMap getInstance() {
        return s_instance;
    }


    /**
     * adds the frame to the map for painting
     *
     * @param p_frame frame objct
     */
    public void setFrame(CFrame p_frame) {
        if (m_frame == null)
            m_frame = p_frame;
    }


    /**
     * adds a new value to the statistic map, if the map does not exist, the object will be created
     *
     * @param p_name  a free-defined unique name of the statistic
     * @param p_value a value for accumulation
     */
    private synchronized void addStatisticValue(String p_name, double p_value) {
        if (!m_statisticdata.containsKey(p_name))
            m_statisticdata.put(p_name, new SynchronizedDescriptiveStatistics());

        m_statisticdata.get(p_name).addValue(p_value);
    }


    /**
     * adds a new line chart to the window
     *
     * @param p_name   name
     * @param p_xaxis  x axis name
     * @param p_yaxis  y axis name
     * @param p_series series object
     */
    private synchronized void addLinePlot(String p_name, String p_xaxis, String p_yaxis, String p_series) {
        if (m_plotdata.containsKey(p_name))
            return;

        m_plotdata.put(p_name, new DefaultCategoryDataset());
        m_frame.addChart(p_name, new ChartPanel(ChartFactory.createLineChart(p_name, p_xaxis, p_yaxis, m_plotdata.get(p_name), PlotOrientation.VERTICAL, false, false, false)));
    }


    /**
     * add value to a series
     *
     * @param p_name      plot name
     * @param p_series    series name
     * @param p_valuename value name
     * @param p_value     value
     */
    private synchronized void addPlotValue(String p_name, String p_series, String p_valuename, double p_value) {
        if (!m_plotdata.containsKey(p_name))
            m_plotdata.put(p_name, new DefaultCategoryDataset());

        m_plotdata.get(p_name).addValue(p_value, p_series, p_valuename);
    }


    @Override
    public void before(int p_currentstep, ICarSourceFactory[] p_sources, ICar[] p_cars) {
    }


    @Override
    public void after(int p_currentstep, ICarSourceFactory[] p_sources, ICar[] p_cars) {
        if (p_currentstep == 0)
            this.addLinePlot("Car Count", "time", "number of cars", "number");

        this.addPlotValue("Car Count", "number", String.valueOf(p_currentstep), p_cars.length);
    }


}
