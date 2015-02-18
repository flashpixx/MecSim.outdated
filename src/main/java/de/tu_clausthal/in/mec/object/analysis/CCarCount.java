/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.analysis;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IEvaluateLayer;
import de.tu_clausthal.in.mec.object.ILayer;
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
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * data push
     */
    DefaultCategoryDataset m_plotdata = new DefaultCategoryDataset();


    /**
     * ctor with frame initialization
     *
     * @param p_frame frame object
     */
    public CCarCount( final CFrame p_frame )
    {
        m_active = false;
        this.initialize( p_frame );
    }

    /**
     * initialize for setting up the frame
     *
     * @param p_frame frame
     */
    private void initialize( final CFrame p_frame )
    {
        p_frame.addWidget( "Count Cars", new ChartPanel( ChartFactory.createLineChart( CCommon.getResouceString( this, "charttitle" ), CCommon.getResouceString( this, "xaxis" ), CCommon.getResouceString( this, "yaxis" ), m_plotdata, PlotOrientation.VERTICAL, false, false, false ) ) );
    }

    @Override
    public int getCalculationIndex()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer )
    {
        if ( !CSimulation.getInstance().hasUI() )
            return;

        m_plotdata.addValue( Integer.parseInt( CSimulation.getInstance().getWorld().get( "Cars" ).analyse().get( "car count" ).toString() ), "number", String.valueOf( p_currentstep ) );
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void release()
    {
        if ( m_plotdata != null )
            m_plotdata.clear();
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws exception on reading
     * @throws ClassNotFoundException throws on deserialization
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        if ( CSimulation.getInstance().hasUI() )
            this.initialize( CSimulation.getInstance().getUI() );
    }
}
