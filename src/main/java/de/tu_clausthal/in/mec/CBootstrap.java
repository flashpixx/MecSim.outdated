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

package de.tu_clausthal.in.mec;

import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.analysis.CCarCount;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.mas.jason.CEnvironment;
import de.tu_clausthal.in.mec.object.source.CSourceFactoryLayer;
import de.tu_clausthal.in.mec.object.world.CWorld;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.*;
import de.tu_clausthal.in.mec.ui.inspector.CInspector;


/**
 * class to create the bootstrap of the program
 */
public class CBootstrap
{

    /**
     * is called after configuration is loaded
     *
     * @param p_configuration configuration
     */
    public static void ConfigIsLoaded( CConfiguration p_configuration )
    {
    }


    /**
     * is called after the frame is initialize and before the UI configuration is load
     *
     * @param p_frame frame
     */
    public static void AfterFrameInit( CFrame p_frame )
    {
        CSimulation.getInstance().getWorld().put( "Count Cars", new CCarCount( p_frame ) );

        p_frame.addWidget( "Inspector", new CInspector(), CFrame.Position.EAST, 0.2 );
        p_frame.addWidget( "OSM", new COSMViewer(), CFrame.Position.WEST, 0.8 );
        p_frame.addWidget( "Jason Mindinspector", new CBrowser( "http://localhost:3272" ) );
    }


    /**
     * is called after the OSM viewer is initialize
     *
     * @param p_viewer viewer object
     */
    public static void AfterOSMViewerInit( COSMViewer p_viewer )
    {

        p_viewer.getCompoundPainter().addPainter( (IMultiLayer) CSimulation.getInstance().getWorld().get( "Sources" ) );
        p_viewer.getCompoundPainter().addPainter( (IMultiLayer) CSimulation.getInstance().getWorld().get( "Cars" ) );

    }


    /**
     * is called after the simulation is initialize
     *
     * @param p_simulation simulation
     */
    public static void AfterSimulationInit( CSimulation p_simulation )
    {

        p_simulation.getWorld().put( "Sources", new CSourceFactoryLayer() );
        p_simulation.getWorld().put( "Cars", new CCarLayer() );
        p_simulation.getWorld().put( "Jason Car Agents", new CEnvironment<ICar>() );

    }


    /**
     * is called after the world is initialize
     *
     * @param p_world world
     */
    public static void AfterWorldInit( CWorld p_world )
    {

    }


    /**
     * is called before the simulation is run
     *
     * @param p_simulation simulation object
     */
    public static void BeforeSimulationStarts( CSimulation p_simulation )
    {

    }


    /**
     * is called after the simulation is finished
     *
     * @param p_simulation simiulation object
     */
    public static void AfterSimulationStops( CSimulation p_simulation )
    {

    }


    /**
     * is called on simulation reset
     *
     * @param p_simulation simulation object
     */
    public static void onSimulationReset( CSimulation p_simulation )
    {

        COSMViewer l_osm = COSMViewer.getSimulationOSM();
        if ( l_osm == null )
            return;

        l_osm.setZoom( CConfiguration.getInstance().get().getZoom() );
        l_osm.setCenterPosition( CConfiguration.getInstance().get().getViewpoint() );
        l_osm.setAddressLocation( CConfiguration.getInstance().get().getViewpoint() );

    }

}
