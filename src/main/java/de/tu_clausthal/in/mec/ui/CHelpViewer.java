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
import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * help window
 *
 * @note help files are stored in Markdown syntax within the "help" directory
 */
public class CHelpViewer extends CBrowser
{

    /**
     * markdown processor *
     */
    protected PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );


    public CHelpViewer()
    {
        try
        {
            BufferedReader l_reader = new BufferedReader( new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream( "help/" + CConfiguration.getInstance().get().getLanguage() + "/index.md" ) ) );
            m_webview.getEngine().loadContent( m_markdown.markdownToHtml( IOUtils.toString( l_reader ).toCharArray() ) );
            l_reader.close();
        }
        catch ( Exception l_exception )
        {
        }
    }

}
