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
        super();
        setHgrow( m_webview, Priority.ALWAYS );
        setVgrow( m_webview, Priority.ALWAYS );
        this.getChildren().add( m_webview );
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

        m_webview.getEngine().load( p_url );
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

}
