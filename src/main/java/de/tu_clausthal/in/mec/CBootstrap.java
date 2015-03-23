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
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.CAbout;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.web.CServer;
import de.tu_clausthal.in.mec.ui.web.CWorkspace;


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


    /*
     * is called after the frame is initialize and before the UI configuration is load
     *
     * @param UI
     */
    public static void afterStageInit( final CUI p_ui )
    {
        p_ui.add( CCommonUI.createTab( "Main", new CWorkspace() ) );
        p_ui.add( CCommonUI.createTab( "OSM", new CSwingWrapper<COSMViewer>( new COSMViewer() ) ) );
    }


    /**
     * is called after the webserver is started
     *
     * @param p_server webserver
     */
    public static void afterServerInit( final CServer p_server )
    {
        p_server.register( CSimulation.getInstance() );
        p_server.register( new CAbout() );
        p_server.register( CConfiguration.getInstance() );
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
     * is called on simulation reset
     *
     * @param p_simulation simulation object
     * @bug update for JavaFX
     */
    public static void onSimulationReset( final CSimulation p_simulation )
    {

        // clear UI elements
        if ( CSimulation.getInstance().hasUI() )
        {

            final COSMViewer l_osm = COSMViewer.getSimulationOSM();
            if ( l_osm != null )
                l_osm.reset();

            //( (CConsole) CSimulation.getInstance().getUIServer().getWidget( "Console" ) ).clear();
        }

    }

}
