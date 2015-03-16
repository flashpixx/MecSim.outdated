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

import de.tu_clausthal.in.mec.common.CCommon;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashSet;
import java.util.Set;


/**
 * create a plain browser component to show any URL
 *
 * @see http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
 */
public class CBrowser extends JFXPanel implements ComponentListener
{
    /**
     * listener *
     */
    protected final Set<IActionListener> m_listener = new HashSet<>();
    /**
     * webkit view
     */
    protected WebView m_webview = null;

    /**
     * ctor with instantiation a blank engine
     */
    public CBrowser()
    {
        Platform.runLater( () -> {
            this.initialize();
        } );
    }


    /**
     * ctor with instantiation the engine
     *
     * @param p_url string with URL
     */
    public CBrowser( final String p_url )
    {
        if ( ( p_url == null ) || ( p_url.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "urlempty" ) );

        Platform.runLater( () -> {
            this.initialize();
            m_webview.getEngine().load( p_url );
        } );
    }

    /**
     * adds a action listener to the browser content
     *
     * @param p_listener listen
     */
    public final void addContentActionListener( final IActionListener p_listener )
    {
        m_listener.add( p_listener );
    }

    /**
     * removes the browser content action listener
     */
    public final boolean removeContentActionListener( final IActionListener p_listener )
    {
        return m_listener.remove( p_listener );
    }

    /**
     * runs the initialization with listener
     */
    protected final void initialize()
    {
        this.addComponentListener( this );
        m_webview = new WebView();
        this.setScene( new Scene( m_webview ) );

        m_webview.getEngine().getLoadWorker().stateProperty().addListener( new ChangeListener<Worker.State>()
        {
            @Override
            public void changed( final ObservableValue<? extends Worker.State> p_value, final Worker.State p_oldstate, final Worker.State p_newstate )
            {
                if ( p_newstate != Worker.State.SUCCEEDED )
                    return;

                createHrefClickListener();
            }
        } );
    }

    /**
     * create listener of href-click-event
     */
    protected final void createHrefClickListener()
    {
        EventListener l_listener = new EventListener()
        {
            @Override
            public void handleEvent( final Event p_event )
            {
                if ( !"click".equalsIgnoreCase( p_event.getType() ) )
                    return;

                for ( IActionListener l_item : m_listener )
                    l_item.onHrefClick( m_webview.getEngine(), (Element) p_event.getTarget() );
            }
        };

        final NodeList l_nodes = m_webview.getEngine().getDocument().getElementsByTagName( "a" );
        for ( int i = 0; i < l_nodes.getLength(); i++ )
            ( (EventTarget) l_nodes.item( i ) ).addEventListener( "click", l_listener, false );
    }


    /**
     * loads a URL on the browser
     *
     * @param p_url string with URL
     */
    public final void load( final String p_url )
    {
        Platform.runLater( () -> {
            m_webview.getEngine().load( p_url );
        } );
    }

    /**
     * refresh the current URL
     */
    public final void reload()
    {
        Platform.runLater( () -> {
            m_webview.getEngine().reload();
        } );
    }

    /**
     * navigates one item back
     */
    public final void back()
    {
        if ( m_webview.getEngine().getHistory().getCurrentIndex() < 1 )
            return;

        Platform.runLater( () -> {
            m_webview.getEngine().getHistory().go( -1 );
        } );
    }

    /**
     * navigates on item forward
     */
    public final void forward()
    {
        if ( m_webview.getEngine().getHistory().getCurrentIndex() >= m_webview.getEngine().getHistory().getEntries().size() - 1 )
            return;

        Platform.runLater( () -> {
            m_webview.getEngine().getHistory().go( +1 );
        } );
    }

    @Override
    public void componentResized( final ComponentEvent p_event )
    {
        this.setPreferredSize( p_event.getComponent().getSize() );
    }

    @Override
    public void componentMoved( final ComponentEvent p_event )
    {

    }

    @Override
    public void componentShown( final ComponentEvent p_event )
    {
    }

    @Override
    public void componentHidden( final ComponentEvent p_event )
    {

    }

    /**
     * content action listener *
     */
    public static interface IActionListener
    {

        /**
         * click on a href tag
         *
         * @param p_web     engine
         * @param p_element full html element
         */
        public void onHrefClick( WebEngine p_web, Element p_element );

    }

}
