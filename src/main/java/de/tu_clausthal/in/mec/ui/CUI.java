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


import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.controlsfx.control.PopOver;

import java.util.HashMap;
import java.util.Map;


/**
 * JavaFX application
 *
 * @see https://arnaudnouard.wordpress.com/2013/02/02/undecorator-add-a-better-look-to-your-javafx-stages-part-i/
 * @see http://docs.oracle.com/javafx/2/layout/style_css.htm#CHDHIGCA
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
    private final Map<Node, Pair<PopOver, Tab>> m_widget = new HashMap<>();
    /**
     * map with name and content
     */
    private final Map<String, Node> m_content = new HashMap<>();
    /**
     * hidden event handler
     */
    private final EventHandler<WindowEvent> m_popuphiddenevent = new EventHandler<WindowEvent>()
    {
        @Override
        public void handle( final WindowEvent p_event )
        {
            p_event.consume();

            final Node l_node = ( (PopOver) p_event.getSource() ).getContentNode();
            if ( !m_widget.containsKey( l_node ) )
                return;

            m_tabpane.getTabs().add( m_widget.get( l_node ).getValue() );
        }
    };
    /**
     * main stage *
     */
    private Stage m_stage;
    /**
     * tab close handler
     */
    private final EventHandler<Event> m_tabcloseevent = new EventHandler<Event>()
    {
        @Override
        public void handle( final Event p_event )
        {
            p_event.consume();

            final Tab l_tab = (Tab) p_event.getSource();
            if ( !m_widget.containsKey( l_tab.getContent() ) )
                return;

            m_widget.get( l_tab.getContent() ).getKey().show( m_stage );
        }
    };

    /**
     * main-wrapper method
     *
     * @param p_args start arguments
     */
    public static void main( final String[] p_args )
    {
        launch( p_args );
    }


    @Override
    public final void start( final Stage p_stage ) throws Exception
    {
        // set via reflection the UI
        try
        {
            if ( CSimulation.getInstance().getUIComponents().getUI() == null )
                CReflection.getClassField( CSimulation.getInstance().getUIComponents().getClass(), "m_ui" ).getSetter().invoke(
                        CSimulation.getInstance().getUIComponents(), this
                );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }

        this.stageInitialization( p_stage );
        this.stageLayoutInitialization( p_stage );
        CBootstrap.afterStageInit( this );
    }


    /**
     * state initialization
     *
     * @param p_stage main stage
     */
    private void stageInitialization( final Stage p_stage )
    {
        this.setUserAgentStylesheet( null );
        if ( m_stage == null )
            m_stage = p_stage;

        p_stage.setTitle( CConfiguration.getInstance().get().<String>get( "manifest/project-name" ) );
        p_stage.setOnCloseRequest(
                new EventHandler<WindowEvent>()
                {
                    @Override
                    public void handle( final WindowEvent p_event )
                    {
                        CBootstrap.beforeStageShutdown( CUI.this );

                        CConfiguration.getInstance().get().set(
                                "ui/windowwidth", ( (Stage) p_event.getSource() ).getScene().getWidth()
                        );
                        CConfiguration.getInstance().get().set(
                                "ui/windowheight", ( (Stage) p_event.getSource() ).getScene().getHeight()
                        );

                        CConfiguration.getInstance().write();
                    }
                }
        );
    }


    /**
     * stage layout initialization
     *
     * @param p_stage main stage
     */
    private void stageLayoutInitialization( final Stage p_stage )
    {
        // border pane for with menu
        final BorderPane l_root = new BorderPane();

        final MenuBar l_menubar = new MenuBar();
        l_menubar.setUseSystemMenuBar( true );
        l_root.setTop( l_menubar );

        // create tab pane for content and scene (screen size depends on the configuration or the current screen size)
        l_root.setCenter( m_tabpane );
        p_stage.setScene(
                new Scene(
                        l_root, Math.min(
                        Screen.getPrimary().getVisualBounds().getWidth() * 0.9, CConfiguration.getInstance().get().<Double>get( "ui/windowwidth" )
                ), Math.min(
                        Screen.getPrimary().getVisualBounds().getHeight() * 0.9, CConfiguration.getInstance().get().<Double>get( "ui/windowheight" )
                )
                )
        );
        p_stage.sizeToScene();
        p_stage.show();
    }


    /**
     * adds a new tab to the UI
     *
     * @param p_title name of the node
     * @param p_node node
     */
    public final void add( final String p_title, final Node p_node )
    {
        if ( ( m_widget.containsKey( p_node ) ) || ( m_content.containsKey( p_title ) ) )
            return;

        m_content.put( p_title, p_node );
        m_widget.put( p_node, new ImmutablePair<>( this.createPopOver( p_title, p_node ), this.createTab( p_title, p_node ) ) );
    }

    /**
     * creates a new pop window
     *
     * @param p_title title
     * @param p_node node
     * @return popup
     */
    private PopOver createPopOver( final String p_title, final Node p_node )
    {
        final PopOver l_popover = new PopOver( p_node );

        l_popover.setDetached( true );
        l_popover.setDetachedTitle( p_title );
        l_popover.setHideOnEscape( true );
        l_popover.setAutoFix( true );
        l_popover.setOnHidden( m_popuphiddenevent );

        return l_popover;
    }

    /**
     * creates a new tab - adds the tab to the global tab pane
     *
     * @param p_title title
     * @param p_node node
     * @return tab
     */
    private Tab createTab( final String p_title, final Node p_node )
    {
        final Tab l_tab = new Tab();

        l_tab.setId( p_title );
        l_tab.setText( p_title );
        l_tab.setContent( p_node );
        l_tab.setClosable( true );
        l_tab.setOnClosed( m_tabcloseevent );
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
    @SuppressWarnings( "unchecked" )
    public final <T extends Node> T getTyped( final String p_name )
    {
        return (T) m_content.get( p_name );
    }

}
