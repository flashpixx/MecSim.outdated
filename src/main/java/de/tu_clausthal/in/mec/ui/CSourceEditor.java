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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * source code editor primary for MAS editing based on the RSyntaxTextArea
 *
 * @see http://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
 * @see https://github.com/bobbylight/RSyntaxTextArea
 */
public class CSourceEditor extends JTabbedPane
{

    /**
     * map to store reference file and tab *
     */
    protected Map<File, JComponent> m_tabs = new HashMap();


    /**
     * open a new editor tab
     *
     * @param p_file input file
     */
    public void open( File p_file )
    {
        if ( m_tabs.containsKey( p_file ) )
            return;

        if ( ( !p_file.exists() ) || ( !p_file.canRead() ) || ( !p_file.canWrite() ) )
            throw new IllegalStateException( "file [" + p_file + "] does not exist or cannot be read / written" );

        try
        {
            JComponent l_tab = this.createEditor( p_file );
            m_tabs.put( p_file, l_tab );
            this.add( p_file.toString(), l_tab );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalStateException( "error on file [" + p_file + "] reading" );
        }
    }


    /**
     * creates an editor component
     *
     * @param p_file input file
     * @return component
     */
    protected JComponent createEditor( File p_file ) throws IOException
    {
        RSyntaxTextArea l_editor = new RSyntaxTextArea();

        BufferedReader l_reader = new BufferedReader( new FileReader( p_file ) );
        l_editor.read( l_reader, null );
        l_reader.close();

        return new RTextScrollPane( l_editor );
    }

}
