/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.analysis.CEvaluationStore;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgentLayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.mas.EAgentLanguages;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CInconsistencyLayer;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CSymmetricDifferenceMetric;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.object.waypoint.CCarWayPointLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.benchmark.CSummary;
import de.tu_clausthal.in.mec.ui.CAgentEnvironment;
import de.tu_clausthal.in.mec.ui.CConsole;
import de.tu_clausthal.in.mec.ui.CInconsistencyEnvironment;
import de.tu_clausthal.in.mec.ui.CInspector;
import de.tu_clausthal.in.mec.ui.CLanguageEnvironment;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CTrafficEnvironment;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.CWaypointEnvironment;
import de.tu_clausthal.in.mec.ui.web.CMarkdownRenderer;
import de.tu_clausthal.in.mec.ui.web.CServer;
import de.tu_clausthal.in.mec.ui.web.CWorkspace;
import org.apache.commons.io.FileUtils;


/**
 * class to create the bootstrap of the program
 */
public final class CBootstrap
{

    /**
     * private ctor - avoid instantiation
     */
    private CBootstrap()
    {
    }

    /**
     * is called after the OSM viewer is initialize
     *
     * @param p_viewer viewer object
     */
    public static void afterOSMViewerInit( final COSMViewer p_viewer )
    {
        p_viewer.getCompoundPainter().addPainter( CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Car WayPoints" ) );
        p_viewer.getCompoundPainter().addPainter( CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Cars" ) );

        /*
        @bug

        p_viewer.getCompoundPainter().addPainter(
                CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).getGraph().getWeight(
                        CGraphHopper.EWeight.ForbiddenEdges
                )
        );
        */
    }

    /**
     * is called after the webserver is started
     *
     * @param p_server webserver
     */
    public static void afterServerInit( final CServer p_server )
    {
        // register default locations
        p_server.registerVirtualDirectory(
                "web/documentation/user/" + CConfiguration.getInstance().get().<String>get( "language/current" ), "index.md", "/userdoc/",
                new CMarkdownRenderer( CMarkdownRenderer.EHTMLType.Fragment, "/userdoc/" )
        );

        p_server.registerVirtualDirectory( CConfiguration.getInstance().getLocation( "www" ), "index.htm", "/local/" );

        p_server.registerVirtualDirectory( "web/documentation/developer", "index.htm", "/develdoc/" );


        // register objects
        p_server.registerObject( CConsole.getError( "error" ) );
        p_server.registerObject( CConsole.getOutput( "output" ) );
        p_server.registerObject( CSimulation.getInstance() );
        p_server.registerObject( CSimulation.getInstance().getMessageSystem() );
        p_server.registerObject( CConfiguration.getInstance() );
        p_server.registerObject( CLogger.getInstance() );
        p_server.registerObject( new CInspector() );

        p_server.registerObject( new CAgentEnvironment( EAgentLanguages.Jason ) );
        p_server.registerObject( new CTrafficEnvironment() );
        p_server.registerObject( new CLanguageEnvironment() );
        p_server.registerObject( new CWaypointEnvironment() );
        p_server.registerObject( CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent() );
        p_server.registerObject( CSimulation.getInstance().getWorld().get( "Car WayPoints" ) );
        p_server.registerObject( new CInconsistencyEnvironment( "Jason Car Inconsistency" ) );
    }

    /**
     * is called after the simulation is initialize
     *
     * @param p_simulation simulation
     */
    public static void afterSimulationInit( final CSimulation p_simulation )
    {
        p_simulation.getWorld().put( "Car WayPoints", new CCarWayPointLayer() );
        p_simulation.getWorld().put( "Cars", new CCarLayer() );

        // build layer first and set linkage between layer via ctor, because world is not initialized yet
        final CInconsistencyLayer l_inconsistency = new CInconsistencyLayer<CAgent>(
                CCommon.getResourceString( CInconsistencyLayer.class, "jasoncar" ),
                new CSymmetricDifferenceMetric<CAgent>()
        );
        p_simulation.getWorld().put( "Jason Car Inconsistency", l_inconsistency );
        p_simulation.getWorld().put( "Jason Car Agents", new CCarJasonAgentLayer( l_inconsistency ) );
    }

    /**
     * is called after the frame is initialize and before the UI configuration is load
     *
     * @param p_ui UI Object
     */
    public static void afterStageInit( final CUI p_ui )
    {
        p_ui.add( "OSM", new CSwingWrapper<COSMViewer>( new COSMViewer() ) );
        p_ui.add( "Main", new CWorkspace() );
    }

    /**
     * is called at UI shutdown
     *
     * @param p_ui
     */
    public static void beforeStageShutdown( final CUI p_ui )
    {
        CSimulation.getInstance().setConfiguration();
        p_ui.<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().setConfiguration();
    }

    /**
     * is called after configuration is loaded
     *
     * @param p_configuration configuration
     */
    public static void configurationIsLoaded( final CConfiguration p_configuration )
    {
        // layer with database connection must be created after the configuration is loaded,
        // because the configuration adds the driver Jars to the classpath
        CSimulation.getInstance().getWorld().put( "Statistic Evaluation", new CEvaluationStore() );
    }

    /**
     * is called on simulation reset
     *
     * @param p_simulation simulation object
     */
    public static void onSimulationReset( final CSimulation p_simulation )
    {
        if ( p_simulation.getStorage().exists() )
            p_simulation.getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().resetConfiguration();

    }

    /**
     * is called on application closed
     */
    public static void onApplicationClose()
    {
        CSummary.getInstance().store();

        // delete configuration directory
        if ( CConfiguration.getInstance().get().<Boolean>get( "deleteonshutdown" ) )
            FileUtils.deleteQuietly( CConfiguration.getInstance().getLocation( "root" ) );
    }

}
