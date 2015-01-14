/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.analysis;

import de.tu_clausthal.in.mec.object.IEvaluateLayer;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.CFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;


/**
 * car count statistic
 */
public class CCarCount extends IEvaluateLayer
{

    /**
     * data push
     */
    DefaultCategoryDataset m_plotdata = new DefaultCategoryDataset();


    public CCarCount( CFrame p_frame )
    {
        this.initialize( p_frame );
    }

    /** initialize for setting up the frame
     *
     * @param p_frame frame
     */
    private void initialize( CFrame p_frame )
    {
        p_frame.addWidget( "Count Cars", new ChartPanel( ChartFactory.createLineChart( "Count Cars", "time", "number of cars", m_plotdata, PlotOrientation.VERTICAL, false, false, false ) ) );
    }

    @Override
    public void resetData()
    {
        m_plotdata.clear();
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        if ( !CSimulation.getInstance().hasUI() )
            return;

        m_plotdata.addValue( ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getGraph().getNumberOfObjects(), "number", String.valueOf( p_currentstep ) );
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     */
    private void readObject( ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        if ( CSimulation.getInstance().hasUI() )
            this.initialize( CSimulation.getInstance().getUI() );
    }
}
