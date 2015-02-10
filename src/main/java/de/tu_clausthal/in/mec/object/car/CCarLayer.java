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

package de.tu_clausthal.in.mec.object.car;

import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.car.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.mec.object.car.drivemodel.IDriveModel;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.IReturnStepableTarget;
import de.tu_clausthal.in.mec.ui.COSMViewer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;


/**
 * defines the layer for cars
 */
public class CCarLayer extends IMultiLayer<ICar> implements IReturnStepableTarget<ICar>
{

    /**
     * driving model list
     */
    protected static IDriveModel[] s_drivemodellist = {new CNagelSchreckenberg()};
    /**
     * driving model
     */
    protected IDriveModel m_drivemodel = s_drivemodellist[0];
    /**
     * graph
     */
    protected transient CGraphHopper m_graph = new CGraphHopper();
    /**
     * weight name
     */
    protected String m_weight = m_graph.getWeightingList()[0];
    /**
     * data structure - not serializable
     */
    protected transient List<ICar> m_data = new LinkedList<>();
    /**
     * map of analyse call
     */
    protected transient Map<String, Object> m_analyse = new HashMap<>();


    /**
     * returns the graph of the layer
     *
     * @return graph object
     */
    public CGraphHopper getGraph()
    {
        return m_graph;
    }

    /**
     * returns the current graph weight
     *
     * @return weight name
     */
    public String getGraphWeight()
    {
        return m_weight;
    }

    /**
     * sets the graph with a weight
     *
     * @param p_weight weight name
     */
    public void setGraphWeight( String p_weight )
    {
        m_weight = p_weight;
        m_graph = new CGraphHopper( p_weight );
    }

    /**
     * sets the drive model
     *
     * @param p_model model
     */
    public void setDriveModel( String p_model )
    {
        for ( IDriveModel l_model : s_drivemodellist )
            if ( p_model.equals( l_model.getName() ) )
                m_drivemodel = l_model;
    }


    /**
     * returns the name of the driving model
     *
     * @return name
     */
    public String getDrivingModel()
    {
        return m_drivemodel.getName();
    }

    /**
     * returns a list with all driving model names
     *
     * @return list
     */
    public String[] getDrivingModelList()
    {
        String[] l_list = new String[s_drivemodellist.length];
        int i = 0;
        for ( IDriveModel l_model : s_drivemodellist )
            l_list[i++] = l_model.getName();

        return l_list;
    }

    @Override
    public int getCalculationIndex()
    {
        return 1;
    }

    @Override
    public void beforeStepObject( int p_currentstep, ICar p_object )
    {
        m_drivemodel.update( p_currentstep, this.m_graph, p_object );
    }

    @Override
    public void afterStepObject( int p_currentstep, ICar p_object )
    {
        // if a car has reached its end, remove it
        if ( p_object.hasEndReached() )
        {
            super.remove( p_object );
            p_object.release();
        }

        // repaint the OSM viewer (supress flickering)
        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();
    }

    @Override
    public Map<String, Object> analyse()
    {
        m_analyse.put( "car count", this.getGraph().getNumberOfObjects() );
        return m_analyse;
    }

    @Override
    public void release()
    {
        super.clear();
        m_graph.clear();
    }

    @Override
    public void push( Collection<ICar> p_data )
    {
        super.addAll( p_data );
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     */
    private void readObject( ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();
        m_graph = new CGraphHopper( m_weight );
    }

}
