/**
 @cond
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
 @endcond
 **/

package de.tu_clausthal.in.mec.object.car;

import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.car.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.mec.object.car.drivemodel.IDriveModel;
import de.tu_clausthal.in.mec.object.car.graph.CCellObjectLinkage;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.simulation.IReturnStepableTarget;

import java.util.*;


/**
 * defines the layer for cars
 */
public class CCarLayer extends IMultiLayer<ICar> implements IReturnStepableTarget<ICar>
{

    /**
     * driving model list
     */
    protected transient IDriveModel[] m_drivemodellist = {new CNagelSchreckenberg()};
    /**
     * driving model
     */
    protected IDriveModel m_drivemodel = m_drivemodellist[0];

    /**
     * graph *
     */
    protected transient CGraphHopper m_graph = new CGraphHopper();

    /**
     * data structure - not serializable
     *
     * @Overload
     */
    protected transient List<ICar> m_data = new LinkedList();

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
     * sets the graph with a weight
     *
     * @param p_weight weight name
     */
    public void setGraphWeight( String p_weight )
    {
        m_graph = new CGraphHopper( p_weight );
    }


    /**
     * sets the drive model
     *
     * @param p_model model
     */
    public void setDriveModel( String p_model )
    {
        for ( IDriveModel l_model : m_drivemodellist )
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
        String[] l_list = new String[m_drivemodellist.length];
        int i = 0;
        for ( IDriveModel l_model : m_drivemodellist )
            l_list[i] = l_model.getName();
        return l_list;
    }

    @Override
    public void resetData()
    {
        super.clear();
        m_graph.clear();
    }

    @Override
    public void beforeStepObject( int p_currentstep, ICar p_object )
    {
        m_drivemodel.update( p_currentstep, this.m_graph, p_object );
    }

    @Override
    public void afterStepObject( int p_currentstep, ICar p_object )
    {

        if ( p_object.hasEndReached() )
        {
            super.remove( p_object );
            CCellObjectLinkage l_edge = m_graph.getEdge( p_object.getEdge() );
            if ( l_edge != null )
            {
                l_edge.removeObject( p_object );
            }
        }

    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public synchronized void push( Collection<ICar> p_data )
    {
        super.addAll( p_data );
    }

}
