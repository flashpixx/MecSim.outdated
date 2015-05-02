/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.object.car;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.car.drivemodel.CAgentNagelSchreckenberg;
import de.tu_clausthal.in.mec.object.car.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.mec.object.car.drivemodel.IDriveModel;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.object.car.graph.weights.IWeighting;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppableTarget;
import de.tu_clausthal.in.mec.runtime.ISerializable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import org.jxmapviewer.painter.Painter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * defines the layer for cars
 */
public class CCarLayer extends IMultiLayer<ICar> implements IReturnSteppableTarget<ICar>, ISerializable
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * data structure - not serializable
     */
    private final transient List<ICar> m_data = new LinkedList<>();
    /**
     * map of analyse call
     */
    private final transient Map<String, Object> m_analyse = new HashMap<>();
    /**
     * driving model
     */
    private EDrivingModel m_drivemodel = EDrivingModel.AgentNagelSchreckenberg;
    /**
     * graph
     */
    private transient CGraphHopper m_graph = new CGraphHopper();


    /**
     * enable / disable weight
     *
     * @param p_weight weight name
     */
    public final void enableDisableGraphWeight( final CGraphHopper.EWeight p_weight )
    {
        m_graph.enableDisableWeight( p_weight );
    }

    /**
     * checks if a weight is active
     *
     * @param p_weight weight name
     * @return bool active flag
     */
    public final boolean isActiveWeight( final CGraphHopper.EWeight p_weight )
    {
        return m_graph.isActiveWeight( p_weight );
    }

    /**
     * returns a graph weight
     *
     * @param p_weight weight name
     * @return weight object or null
     */
    public final <T extends IWeighting> T getGraphWeight( final CGraphHopper.EWeight p_weight )
    {
        return m_graph.<T>getWeight( p_weight );
    }

    /**
     * sets the drive model
     *
     * @param p_model model
     */
    public final void setDriveModel( final EDrivingModel p_model )
    {
        m_drivemodel = p_model;
    }

    /**
     * returns the name of the driving model
     *
     * @return name
     */
    public final EDrivingModel getDrivingModel()
    {
        return m_drivemodel;
    }

    @Override
    public final int getCalculationIndex()
    {
        return 1;
    }

    @Override
    public final void beforeStepObject( final int p_currentstep, final ICar p_object )
    {
        m_drivemodel.getModel().update( p_currentstep, this.m_graph, p_object );
    }

    @Override
    public final void afterStepObject( final int p_currentstep, final ICar p_object )
    {
        // if a car has reached its end, remove it
        if ( p_object.hasEndReached() )
        {
            super.remove( p_object );
            p_object.release();
        }

        // repaint the OSM viewer (supress flickering)
        if ( CSimulation.getInstance().getUIComponents().exists() )
            CSimulation.getInstance().getUIComponents().getUI().<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().repaint();
    }

    @Override
    public final void release()
    {
        super.clear();
        m_graph.clear();
    }

    @Override
    public final void onDeserializationInitialization()
    {
        if ( CSimulation.getInstance().getUIComponents().exists() )
            CSimulation.getInstance().getUIComponents().getUI().<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().getCompoundPainter().removePainter(
                    (Painter) this
            );
    }

    @Override
    public final void onDeserializationComplete()
    {
        if ( CSimulation.getInstance().getUIComponents().exists() )
            CSimulation.getInstance().getUIComponents().getUI().<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().getCompoundPainter().addPainter(
                    (Painter) this
            );
    }

    /**
     * returns the graph of the layer
     *
     * @return graph object
     */
    public final CGraphHopper getGraph()
    {
        return m_graph;
    }

    @Override
    public final void push( final Collection<ICar> p_data )
    {
        super.addAll( p_data );
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * write call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws the exception on loading data
     * @bug incomplete
     */
    private void writeObject( final ObjectOutputStream p_stream ) throws IOException
    {
        p_stream.defaultWriteObject();

        // write active graph weights
        p_stream.writeObject( m_graph.getActiveWeights() );
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     * @bug incomplete
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        m_graph = new CGraphHopper();
        //m_graph.disableWeight();
    }


    /**
     * enum for representating a driving model
     */
    public enum EDrivingModel
    {
        /**
         * default Nagel-Schreckenberg model *
         */
        NagelSchreckenberg( CCommon.getResourceString( EDrivingModel.class, "nagelschreckenberg" ), new CNagelSchreckenberg() ),
        /**
         * additional Nagel-Schreckenberg model for agents *
         */
        AgentNagelSchreckenberg( CCommon.getResourceString( EDrivingModel.class, "agentnagelschreckenberg" ), new CAgentNagelSchreckenberg() );

        /**
         * string representation *
         */
        private final String m_text;
        /**
         * driving model instance
         */
        private final IDriveModel m_model;

        /**
         * ctor
         *
         * @param p_text text representation
         * @param p_model model instance
         */
        private EDrivingModel( final String p_text, final IDriveModel p_model )
        {
            m_text = p_text;
            m_model = p_model;
        }

        /**
         * returns the driving model
         *
         * @return model instance
         */
        public IDriveModel getModel()
        {
            return m_model;
        }

        @Override
        public String toString()
        {
            return m_text;
        }
    }
}
