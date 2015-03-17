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

package de.tu_clausthal.in.mec;

import de.tu_clausthal.in.mec.common.CCommonUI;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.analysis.CDatabase;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgentLayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.source.CSourceLayer;
import de.tu_clausthal.in.mec.object.world.CWorld;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.CConsole;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CWebMenu;
import javafx.scene.control.TabPane;


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
    public static void configIsLoaded( final CConfiguration p_configuration )
    {
    }


    /**
     * is called after the frame is initialize and before the UI configuration is load
     *
     * @deprecated
     */
    public static void afterFrameInit()
    {
        // ( (CCarJasonAgentLayer) CSimulation.getInstance().getWorld().get( "Jason Car Agents" ) ).setFrame( p_frame );

        //p_frame.addWidget( "Inspector", new CInspector(), CFrame.Position.RIGHT, 0.2 );
        //p_frame.addWidget( "OSM", new COSMViewer(), CFrame.Position.BOTTOM, 0.7 );
        //p_frame.addWidget( "Main", new CWebUI(), CFrame.Position.TOP, 0.3);
        //p_frame.addWidget( "Editor", new CSourceEditor() );
        //p_frame.addWidget( "Console", new CConsole() );
    }


    /*
     * is called after the frame is initialize and before the UI configuration is load
     *
     * @param p_root root pane
     */
    public static void afterStageInit( final TabPane p_root )
    {
        p_root.getTabs().add( CCommonUI.createTab( "Main", new CWebMenu() ) );
        p_root.getTabs().add( CCommonUI.createTab( "OSM", new CSwingWrapper<COSMViewer>( new COSMViewer() ) ) );
    }


    /**
     * is called after the OSM viewer is initialize
     *
     * @param p_viewer viewer object
     */
    public static void afterOSMViewerInit( final COSMViewer p_viewer )
    {
        p_viewer.getCompoundPainter().addPainter( (IMultiLayer) CSimulation.getInstance().getWorld().get( "Sources" ) );
        p_viewer.getCompoundPainter().addPainter( (IMultiLayer) CSimulation.getInstance().getWorld().get( "Cars" ) );
    }


    /**
     * is called after the simulation is initialize
     *
     * @param p_simulation simulation
     */
    public static void afterSimulationInit( final CSimulation p_simulation )
    {
        p_simulation.getWorld().put( "Database", new CDatabase() );
        p_simulation.getWorld().put( "Sources", new CSourceLayer() );
        p_simulation.getWorld().put( "Cars", new CCarLayer() );
        p_simulation.getWorld().put( "Jason Car Agents", new CCarJasonAgentLayer() );
    }


    /**
     * is called after the world is initialize
     *
     * @param p_world world
     */
    public static void afterWorldInit( final CWorld p_world )
    {

    }


    /**
     * is called before the simulation is run
     *
     * @param p_simulation simulation object
     */
    public static void beforeSimulationStarts( final CSimulation p_simulation )
    {

    }


    /**
     * is called after the simulation is finished
     *
     * @param p_simulation simiulation object
     */
    public static void afterSimulationStops( final CSimulation p_simulation )
    {

    }


    /**
     * is called on simulation reset
     *
     * @param p_simulation simulation object
     */
    public static void onSimulationReset( final CSimulation p_simulation )
    {

        // clear UI elements
        if ( CSimulation.getInstance().hasUI() )
        {

            final COSMViewer l_osm = COSMViewer.getSimulationOSM();
            if ( l_osm != null )
                l_osm.reset();

            ( (CConsole) CSimulation.getInstance().getUI().getWidget( "Console" ) ).clear();
        }

    }

}
