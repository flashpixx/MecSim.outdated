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

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.runtime.CSimulation;

import java.util.HashMap;
import java.util.Map;


/**
 * encapsulating class for traffic UI components
 */
public class CTrafficEnvironment
{

    /**
     * UI method - lists all driving model
     *
     * @return list wir driving models
     */
    private final Map<String, Object> web_static_listdrivemodel()
    {
        final CCarLayer l_layer = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" );
        final Map<String, Object> l_models = new HashMap<>();
        for ( String l_item : l_layer.getDrivingModelList() )
            l_models.put( l_item, CCommon.getMap( "active", l_layer.getDrivingModel().equals( l_item ) ) );

        return l_models;
    }


    /**
     * UI method - sets the driving model
     *
     * @param p_data input data
     */
    private final void web_static_setdrivemodel( final Map<String, Object> p_data )
    {
        if ( CSimulation.getInstance().isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        if ( !p_data.containsKey( "id" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "nomodelname" ) );

        CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).setDriveModel( (String) p_data.get( "id" ) );
    }


    /**
     * UI method - list all graph weights
     *
     * @return graphweight list
     */
    private final Map<String, Object> web_static_listgraphweight()
    {
        final CCarLayer l_layer = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" );
        final Map<String, Object> l_return = new HashMap<>();

        for ( String l_item : l_layer.getGraphWeight() )
            l_return.put( l_item, CCommon.getMap( "active", l_layer.isActiveWeight( CGraphHopper.EWeight.valueOf( l_item ) ) ) );

        return l_return;
    }


    /**
     * UI method - enable graph weight
     *
     * @param p_data input data
     */
    private final void web_static_enabledisablegraphweight( final Map<String, Object> p_data )
    {
        if ( CSimulation.getInstance().isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
        if ( !p_data.containsKey( "name" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "noweightname" ) );

        CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).enableDisableGraphWeight(
                CGraphHopper.EWeight.valueOf(
                        (String) p_data.get(
                                "name"
                        )
                )
        );
    }

}
