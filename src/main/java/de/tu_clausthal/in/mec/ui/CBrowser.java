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
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Element;


/**
 * create a plain browser component to show any URL
 *
 * @see http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
 */
public class CBrowser extends JFXPanel
{
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
            m_webview = new WebView();
            this.setScene( new Scene( m_webview ) );
        } );
    }


    /**
     * ctor with instantiation the engine
     *
     * @param p_url string with URL
     */
    public CBrowser( final String p_url )
    {
        this();
        if ( ( p_url == null ) || ( p_url.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "urlempty" ) );

        Platform.runLater( () -> {
            m_webview.getEngine().load( p_url );
        } );
    }

    /**
     * adds a action listener to the browser content
     *
     * @param p_listener listen
     * @deprecated
     */
    public final void addContentActionListener( final IActionListener p_listener )
    {
    }

    /**
     * removes the browser content action listener
     * @deprecated
     */
    public final boolean removeContentActionListener( final IActionListener p_listener )
    {
        return true;
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

    /**
     * content action listener *
     * @deprecated
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
