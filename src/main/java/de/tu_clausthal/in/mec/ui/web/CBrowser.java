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

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.common.CCommon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;


/**
 * create a plain browser component to show any URL
 *
 * @see http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
 */
public class CBrowser extends GridPane
{

    /**
     * webkit view
     */
    protected final WebView m_webview = new WebView();

    /**
     * ctor with instantiation a blank engine
     */
    public CBrowser()
    {
        this( EMenu.None );
    }


    /**
     * ctor with instantiation a blank engine
     */
    public CBrowser( final EMenu p_menu )
    {
        super();
        this.setMenuBar( p_menu );
        this.getChildren().add( m_webview );

        GridPane.setConstraints( m_webview, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS );
        this.getColumnConstraints().addAll(
                new ColumnConstraints( 100, 100, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true ),
                new ColumnConstraints( 40, 40, 40, Priority.NEVER, HPos.CENTER, true )
        );
    }

    /**
     * ctor with instantiation the engine
     *
     * @param p_url string with URL
     */
    public CBrowser( final EMenu p_menu, final String p_url )
    {
        this( p_menu );
        if ( ( p_url == null ) || ( p_url.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( CBrowser.class, "urlempty" ) );

        m_webview.getEngine().load( p_url );
    }

    /**
     * sets the mennubar
     *
     * @param p_menu menu options
     */
    private void setMenuBar( final EMenu p_menu )
    {
        if ( p_menu == EMenu.None )
            return;

        this.setVgap( 5 );
        this.setHgap( 5 );

        switch ( p_menu )
        {
            case BackForward:
                this.setBackForward();
                break;

            case URL:
                this.setURLMenu();
                break;

            case Full:
                this.setURLMenu();
                this.setBackForward();
                break;

            default:
        }
    }

    /**
     * create the URL input box
     */
    private void setURLMenu()
    {
        final TextField l_url = new TextField();
        l_url.setMaxHeight( Double.MAX_VALUE );


        final Button l_button = new Button( CCommon.getResourceString( CBrowser.class, "load" ) );
        l_button.setDefaultButton( true );

        final EventHandler<ActionEvent> l_buttonaction = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( final ActionEvent p_event )
            {
                CBrowser.this.load( l_url.getText().startsWith( "http://" ) ? l_url.getText() : "http://" + l_url.getText() );
            }
        };
        l_button.setOnAction( l_buttonaction );
        l_url.setOnAction( l_buttonaction );

        m_webview.getEngine().locationProperty().addListener( new ChangeListener<String>()
        {
            @Override
            public void changed( final ObservableValue<? extends String> p_observable, final String p_oldValue, final String p_newValue )
            {
                l_url.setText( p_newValue );
            }
        } );

        GridPane.setConstraints( l_url, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.SOMETIMES );
        GridPane.setConstraints( l_button, 1, 0 );

        this.getChildren().add( l_url );
        this.getChildren().add( l_button );
    }

    /**
     * creates the back- and forward button
     */
    private void setBackForward()
    {

    }

    /**
     * loads a URL on the browser
     *
     * @param p_url string with URL
     */
    public final void load( final String p_url )
    {
        m_webview.getEngine().load( p_url );
    }

    /**
     * refresh the current URL
     */
    public final void reload()
    {
        m_webview.getEngine().reload();
    }

    /**
     * navigates one item back
     */
    public final void back()
    {
        if ( m_webview.getEngine().getHistory().getCurrentIndex() < 1 )
            return;

        m_webview.getEngine().getHistory().go( -1 );
    }

    /**
     * navigates on item forward
     */
    public final void forward()
    {
        if ( m_webview.getEngine().getHistory().getCurrentIndex() >= m_webview.getEngine().getHistory().getEntries().size() - 1 )
            return;

        m_webview.getEngine().getHistory().go( +1 );
    }

    /**
     * returns the current URL
     *
     * @return string URL
     */
    public final String getURL()
    {
        return m_webview.getEngine().locationProperty().getValue();
    }

    /**
     * enum to define browser menubar *
     */
    public enum EMenu
    {
        None,
        BackForward,
        URL,
        Full
    }

}
