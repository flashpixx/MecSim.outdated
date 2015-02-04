/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import javafx.application.Platform;
import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.*;


/**
 * help window
 *
 * @note help files are stored in Markdown syntax within the "help" directory
 * @todo add back / forward button with action listener
 */
public class CHelpViewer extends JDialog
{

    /**
     * default language - safe structure because configuration can push a null value *
     */
    protected static String s_defaultlanguage = "en";

    public CHelpViewer()
    {
        this( null );
    }

    /**
     * ctor with window set (not modal) *
     */
    public CHelpViewer( Frame p_frame )
    {
        if ( p_frame != null )
            this.setLocationRelativeTo( p_frame );

        this.setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
        this.add( new CHelpBrowser( "help" + File.separatorChar + ( CConfiguration.getInstance().get().getLanguage() == null ? s_defaultlanguage : CConfiguration.getInstance().get().getLanguage() ) + File.separator + "index.md" ) );
        this.pack();
    }


    /**
     * class to encapsulate browser component *
     */
    protected class CHelpBrowser extends CBrowser
    {

        /**
         * markdown processor *
         */
        protected PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );

        /** ctor
         *
         * @param p_resource resource markdown file
         */
        public CHelpBrowser( String p_resource )
        {
            super();
            Platform.runLater( () -> {
                try
                {
                    BufferedReader l_reader = new BufferedReader( new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream( p_resource ) ) );
                    m_webview.getEngine().loadContent( m_markdown.markdownToHtml( IOUtils.toString( l_reader ).toCharArray() ) );
                    l_reader.close();
                }
                catch ( Exception l_exception )
                {
                    CLogger.error( l_exception );
                }
            } );
        }
    }

}
