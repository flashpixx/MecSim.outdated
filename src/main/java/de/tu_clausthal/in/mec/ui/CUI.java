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
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
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
 */
public class CUI extends Application
{

    public static void main( final String[] p_args )
    {
        launch( p_args );
    }

    @Override
    public void start( final Stage p_stage ) throws Exception
    {
        p_stage.setTitle( CConfiguration.getInstance().getManifest().get( "Project-Name" ) );
        p_stage.setHeight( CConfiguration.getInstance().get().getWindowheight() );
        p_stage.setWidth( CConfiguration.getInstance().get().getWindowwidth() );


        p_stage.setOnCloseRequest( new EventHandler<WindowEvent>()
        {
            public void handle( final WindowEvent p_event )
            {
                CConfiguration.getInstance().get().setWindowwidth( (int) ( (Stage) p_event.getSource() ).getWidth() );
                CConfiguration.getInstance().get().setWindowheight( (int) ( (Stage) p_event.getSource() ).getHeight() );

                CConfiguration.getInstance().write();
            }
        } );


        // border pane for with menu
        final BorderPane l_root = new BorderPane();


        final MenuBar l_menubar = new MenuBar();
        l_menubar.getMenus().addAll();
        l_menubar.setUseSystemMenuBar( true );
        l_root.setTop( l_menubar );


        // tab pane for content
        final TabPane l_tabs = new TabPane();
        l_root.setCenter( l_tabs );
        CBootstrap.afterStageInit( l_tabs );

        // set stage
        p_stage.setScene( new Scene( l_root ) );
        p_stage.sizeToScene();
        p_stage.show();
    }

}
