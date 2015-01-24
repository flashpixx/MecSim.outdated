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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.util.Pair;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * source code editor primary for MAS editing based on the RSyntaxTextArea
 *
 * @see http://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
 * @see https://github.com/bobbylight/RSyntaxTextArea
 */
public class CSourceEditor extends JTabbedPane implements ActionListener
{

    /**
     * map to store reference file, tab and editor
     */
    protected Map<File, ImmutablePair<JComponent, RSyntaxTextArea>> m_tabs = new HashMap();
    /**
     * map with components (buttons), button name, file object
     */
    protected Map<JComponent, ImmutablePair<String, File>> m_actionobject = new HashMap();



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
            JComponent l_tab = new JPanel();
            this.add( p_file.getName(), l_tab );
            l_tab.setLayout( new BorderLayout() );

            // create editor
            RSyntaxTextArea l_editor = new RSyntaxTextArea();
            l_editor.setAutoIndentEnabled( true );
            m_tabs.put( p_file, new ImmutablePair<JComponent, RSyntaxTextArea>( l_tab, l_editor ) );

            // tab toolbar
            JToolBar l_toolbar = new JToolBar();
            l_toolbar.setFloatable( false );
            l_tab.add( l_toolbar, BorderLayout.NORTH );

            for( String l_item : new String[]{"save.png", "delete.png", "reload.png"})
            {
                JButton l_button = new JButton( new ImageIcon( ImageIO.read( this.getClass().getResource( "../../../../../icons/" + l_item ) ) ) );
                l_button.addActionListener( this );
                l_toolbar.add( l_button );

                m_actionobject.put( l_button, new ImmutablePair<String, File>( l_item, p_file ) );
            }


            // read file into editor
            BufferedReader l_reader = new BufferedReader( new FileReader( p_file ) );
            l_editor.read( l_reader, null );
            l_reader.close();
            l_tab.add( new RTextScrollPane( l_editor ), BorderLayout.CENTER );

        }
        catch ( Exception l_exception )
        {
            throw new IllegalStateException( "error on file [" + p_file + "] reading" );
        }
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        ImmutablePair<String, File> l_item = m_actionobject.get( e.getSource() );
        if (l_item == null)
            return;

        ImmutablePair<JComponent,RSyntaxTextArea> l_component = m_tabs.get( l_item.getRight() );
        if (l_component == null)
            return;


        try
        {

            if ( l_item.getLeft().equalsIgnoreCase( "save.png" ) )
            {
                FileWriter l_filewriter = new FileWriter( l_item.getRight() );
                BufferedWriter l_writer = new BufferedWriter( l_filewriter );
                l_component.getRight().write( l_writer );
                l_writer.close();
                l_filewriter.close();
            }

            if ( l_item.getLeft().equalsIgnoreCase( "delete.png" ) )
            {
                if ( !l_item.getRight().delete() )
                    throw new IllegalStateException( "file [" + l_item.getRight().getName() + "] cannot be deleted" );

                JComponent l_tab = m_tabs.get( l_item.getRight() ).getLeft();
                for(int i=0; i < l_tab.getComponentCount(); i++)
                    m_actionobject.remove( l_tab.getComponent( i ) );
                this.remove( l_tab );
            }

        } catch (Exception l_exception) {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION );
        }

    }
}
