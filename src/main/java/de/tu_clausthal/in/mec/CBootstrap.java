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

package de.tu_clausthal.in.mec;

import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.analysis.CDatabase;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgentLayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.source.CSourceLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.ui.CAgentEnvironment;
import de.tu_clausthal.in.mec.ui.CConsole;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CTrafficEnvironment;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.web.CMarkdownRenderer;
import de.tu_clausthal.in.mec.ui.web.CServer;
import de.tu_clausthal.in.mec.ui.web.CWorkspace;


/**
 * class to create the bootstrap of the program
 */
public class CBootstrap
{

    /**
     * private ctor - avoid instantiation
     */
    private CBootstrap()
    {
    }


    /**
     * is called after configuration is loaded
     *
     * @param p_configuration configuration
     */
    public static void configIsLoaded( final CConfiguration p_configuration )
    {
    }


    /**
     * is called at UI shutdown
     *
     * @param p_ui
     */
    public static void beforeStageShutdown( final CUI p_ui )
    {
        p_ui.<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().setConfiguration();
    }


    /*
     * is called after the frame is initialize and before the UI configuration is load
     *
     * @param UI
     */
    public static void afterStageInit( final CUI p_ui )
    {
        p_ui.add( "Main", new CWorkspace() );
        p_ui.add( "OSM", new CSwingWrapper<COSMViewer>( new COSMViewer() ) );
    }


    /**
     * is called after the webserver is started
     *
     * @param p_server webserver
     */
    public static void afterServerInit( final CServer p_server )
    {
        // registerObject default locations
        p_server.registerVirtualDirectory(
                "web/documentation/user/" + CConfiguration.getInstance().get().<String>getTraverse( "language/current" ), "index.md", "/userdoc/",
                new CMarkdownRenderer( CMarkdownRenderer.EHTMLType.Fragment, "/userdoc/" )
        );

        p_server.registerVirtualDirectory( CConfiguration.getInstance().getLocation( "www" ), "index.htm", "/local/" );

        p_server.registerVirtualDirectory( "web/documentation/developer", "index.htm", "/develdoc/" );


        // registerObject objects
        p_server.registerObject( CConsole.getError( "error" ) );
        p_server.registerObject( CConsole.getOutput( "output" ) );
        p_server.registerObject( CSimulation.getInstance() );
        p_server.registerObject( CConfiguration.getInstance() );
        p_server.registerObject( new CAgentEnvironment( CAgentEnvironment.EType.Jason ) );
        p_server.registerObject( new CTrafficEnvironment() );
    }


    /**
     * is called after the OSM viewer is initialize
     *
     * @param p_viewer viewer object
     */
    public static void afterOSMViewerInit( final COSMViewer p_viewer )
    {
        p_viewer.getCompoundPainter().addPainter( CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Sources" ) );
        p_viewer.getCompoundPainter().addPainter( CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Cars" ) );
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
     */
    public static void onSimulationReset( final CSimulation p_simulation )
    {
        if ( p_simulation.getUIComponents().hasUI() )
            p_simulation.getUIComponents().getUI().<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().resetConfiguration();

    }

}
