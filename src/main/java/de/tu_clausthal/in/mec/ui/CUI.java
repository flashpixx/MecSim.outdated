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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * JavaFX application
 *
 * @see http://stackoverflow.com/questions/17673292/internal-frames-in-javafx
 * @see https://blog.idrsolutions.com/2014/03/create-stacked-menus-in-javafx/
 * @see https://blog.idrsolutions.com/2014/02/tutorial-create-border-glow-effect-javafx/
 * @see http://alvinalexander.com/java/java-mac-osx-about-preferences-quit-application-adapter
 * @see http://docs.oracle.com/javafx/2/layout/style_css.htm#CHDHIGCA
 * @todo create docking structure https://gist.github.com/jewelsea/9579047 / https://arnaudnouard.wordpress.com/2013/02/02/undecorator-add-a-better-look-to-your-javafx-stages-part-i/ / https://community.oracle.com/thread/2417144
 */
public class CUI extends Application
{
    /**
     * tab list *
     */
    private final TabPane m_tabpane = new TabPane();
    /**
     * map with nodes (of tab content) and pair of dockables and tab
     */
    private final Map<Node, Pair<CDockable, Tab>> m_widget = new HashMap<>();
    /**
     * map with name and content
     */
    private final Map<String, Node> m_content = new HashMap<>();
    /**
     * main stage *
     */
    private Stage m_stage;
    /**
     * tab close event
     */
    private final EventHandler<Event> m_tabcloseevent = new EventHandler<Event>()
    {
        @Override
        public void handle( final Event p_event )
        {
            final Tab l_tab = ( (Tab) p_event.getSource() );
            m_tabpane.getTabs().remove( l_tab );

            m_widget.get( l_tab.getContent() ).getKey().show( m_stage );
            p_event.consume();
        }
    };


    /** main-wrapper method
     *
     * @param p_args start arguments
     */
    public static void main( final String[] p_args )
    {
        launch( p_args );
    }


    @Override
    public void start( final Stage p_stage ) throws Exception
    {
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



        this.setUserAgentStylesheet( null );
        if ( m_stage == null )
            m_stage = p_stage;


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

        // create tab pane for content and scene
        l_root.setCenter( m_tabpane );
        p_stage.setScene( new Scene( l_root, CConfiguration.getInstance().get().WindowWidth, CConfiguration.getInstance().get().WindowHeight ) );
        p_stage.sizeToScene();
        p_stage.show();

        CBootstrap.afterStageInit( this );
    }


    /**
     * adds a new tab to the UI
     *
     * @param p_title name of the node
     * @param p_node node
     */
    public void add( final String p_title, final Node p_node )
    {
        if ( ( m_widget.containsKey( p_node ) ) || ( m_content.containsKey( p_title ) ) )
            return;

        m_content.put( p_title, p_node );
        m_widget.put( p_node, new ImmutablePair<>( new CDockable( p_title, p_node ), this.addTab( p_title, p_node ) ) );
    }

    /**
     * creates a new tab
     *
     * @param p_title title
     * @param p_node  node
     */
    private Tab addTab( final String p_title, final Node p_node )
    {
        final Tab l_tab = new Tab();
        l_tab.setId( p_title );
        l_tab.setText( p_title );
        l_tab.setContent( p_node );
        l_tab.setClosable( true );
        l_tab.setOnCloseRequest( m_tabcloseevent );
        m_tabpane.getTabs().add( l_tab );
        return l_tab;
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
        return (T) m_content.get( p_name );
    }

    private static class CDeltaPosition
    {
        double x, y;
    }

    /**
     * class to create from a tab a dockable
     */
    private class CDockable extends Popup
    {

        public CDockable( final String p_title, final Node p_node )
        {
            final BorderPane l_pane = new BorderPane();
            l_pane.setPrefSize( 800, 800 );
            l_pane.setStyle( "-fx-background-color: lightgrey; -fx-border-width: 1; -fx-border-color: black" );
            l_pane.setTop( this.getTitleBar( p_title ) );
            l_pane.setCenter( p_node );
            this.getContent().add( l_pane );
        }


        private Node getTitleBar( final String p_title )
        {
            final BorderPane l_pane = new BorderPane();
            l_pane.setStyle( "-fx-padding: 5" );

            final CDeltaPosition l_position = new CDeltaPosition();
            l_pane.setOnMousePressed( mouseEvent -> {
                l_position.x = getX() - mouseEvent.getScreenX();
                l_position.y = getY() - mouseEvent.getScreenY();
            } );
            l_pane.setOnMouseDragged( mouseEvent -> {
                setX( mouseEvent.getScreenX() + l_position.x );
                setY( mouseEvent.getScreenY() + l_position.y );
            } );

            l_pane.setLeft( new Label( p_title ) );
            final Button l_close = new Button( "X" );
            l_pane.setRight( l_close );
            l_close.setOnAction( actionEvent -> hide() );

            return l_pane;
        }

    }

}
