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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * JavaFX application
 *
 * @see http://stackoverflow.com/questions/17673292/internal-frames-in-javafx
 * @see https://blog.idrsolutions.com/2014/03/create-stacked-menus-in-javafx/
 * @see https://blog.idrsolutions.com/2014/02/tutorial-create-border-glow-effect-javafx/
 * @see http://alvinalexander.com/java/java-mac-osx-about-preferences-quit-application-adapter
 */
public class CUI extends Application
{
    /**
     * tab list *
     */
    private final TabPane m_tabs = new TabPane();


    public static void main( final String[] p_args )
    {
        launch( p_args );
    }


    @Override
    public void start( final Stage p_stage ) throws Exception
    {
        p_stage.setTitle( CConfiguration.getInstance().getManifest().get( "Project-Name" ) );

        p_stage.setOnCloseRequest( new EventHandler<WindowEvent>()
        {
            public void handle( final WindowEvent p_event )
            {
                CConfiguration.getInstance().get().WindowWidth = (int) ( (Stage) p_event.getSource() ).getScene().getWidth();
                CConfiguration.getInstance().get().WindowHeight = (int) ( (Stage) p_event.getSource() ).getScene().getHeight();

                CConfiguration.getInstance().write();
            }
        } );


        // border pane for with menu
        final BorderPane l_root = new BorderPane();


        final MenuBar l_menubar = new MenuBar();
        l_menubar.setUseSystemMenuBar( true );
        l_root.setTop( l_menubar );


        // tab pane for content
        l_root.setCenter( m_tabs );
        CBootstrap.afterStageInit( this );

        // set stage
        p_stage.setScene( new Scene( l_root, CConfiguration.getInstance().get().WindowWidth, CConfiguration.getInstance().get().WindowHeight ) );
        p_stage.sizeToScene();
        p_stage.show();


        // set via reflection the UI
        try
        {
            if ( CSimulation.getInstance().getUI() == null )
                CReflection.getClassField( CSimulation.getInstance().getClass(), "m_ui" ).getSetter().invoke( CSimulation.getInstance(), this );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }


    /**
     * adds a ne tab to the UI
     *
     * @param p_tab tab
     */
    public void add( final Tab... p_tab )
    {
        m_tabs.getTabs().addAll( p_tab );
    }

    /**
     * returns a tab / node
     *
     * @param p_name name of the tab
     * @return node or null
     * @tparam T node type
     */
    @SuppressWarnings("unchecked")
    public <T extends Node> T getTab( final String p_name )
    {
        for ( Tab l_tab : m_tabs.getTabs() )
            if ( l_tab.getId().equals( p_name ) )
                return (T) l_tab.getContent();

        return null;
    }

}
