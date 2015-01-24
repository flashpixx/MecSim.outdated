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

import de.tu_clausthal.in.mec.CLogger;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
     * menubar reference *
     */
    protected CMenuBar m_menubar = null;


    /**
     * sets the menubar
     *
     * @param p_menubar menubar
     */
    public void setMenubar( CMenuBar p_menubar )
    {
        m_menubar = p_menubar;
    }


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


            for ( String l_item : new String[]{"sourceeditor_save.png", "sourceeditor_delete.png", "sourceeditor_reload.png", "sourceeditor_close.png"} )
            {
                JButton l_button = new JButton( new ImageIcon( ImageIO.read( this.getClass().getResource( "/images/" + l_item ) ) ) );
                l_button.addActionListener( this );
                l_toolbar.add( l_button );

                m_actionobject.put( l_button, new ImmutablePair<String, File>( l_item, p_file ) );
            }

            this.readFile( l_editor, p_file );
            l_tab.add( new RTextScrollPane( l_editor ), BorderLayout.CENTER );

        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
            throw new IllegalStateException( "error on file [" + p_file + "] reading" );
        }
    }


    /**
     * reads data from file into the editor
     *
     * @param p_editor editor
     * @param p_file   input file
     */
    protected void readFile( RSyntaxTextArea p_editor, File p_file ) throws IOException
    {
        BufferedReader l_reader = new BufferedReader( new FileReader( p_file ) );
        p_editor.read( l_reader, null );
        l_reader.close();
    }

    /**
     * writes data from editor into file
     *
     * @param p_editor editor
     * @param p_file   output file
     */
    protected void writeFile( RSyntaxTextArea p_editor, File p_file ) throws IOException
    {
        FileWriter l_filewriter = new FileWriter( p_file );
        BufferedWriter l_writer = new BufferedWriter( l_filewriter );
        p_editor.write( l_writer );
        l_writer.close();
        l_filewriter.close();
    }


    /**
     * remove all data of a tab from the internal maps
     *
     * @param p_tab tab object
     */
    protected void removeTabData( JComponent p_tab )
    {
        for ( int i = 0; i < p_tab.getComponentCount(); i++ )
            m_actionobject.remove( p_tab.getComponent( i ) );
        this.remove( p_tab );
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        ImmutablePair<String, File> l_item = m_actionobject.get( e.getSource() );
        if ( l_item == null )
            return;

        ImmutablePair<JComponent, RSyntaxTextArea> l_component = m_tabs.get( l_item.getRight() );
        if ( l_component == null )
            return;

        try
        {

            if ( l_item.getLeft().equalsIgnoreCase( "sourceeditor_save.png" ) )
                this.writeFile( l_component.getRight(), l_item.getRight() );

            if ( l_item.getLeft().equalsIgnoreCase( "sourceeditor_reload.png" ) )
                this.readFile( l_component.getRight(), l_item.getRight() );

            if ( l_item.getLeft().equalsIgnoreCase( "sourceeditor_close.png" ) )
                this.removeTabData( m_tabs.get( l_item.getRight() ).getLeft() );

            if ( l_item.getLeft().equalsIgnoreCase( "sourceeditor_delete.png" ) )
            {
                if ( !l_item.getRight().delete() )
                    throw new IllegalStateException( "file [" + l_item.getRight().getName() + "] cannot be deleted" );

                this.removeTabData( m_tabs.get( l_item.getRight() ).getLeft() );

                if ( m_menubar != null )
                    m_menubar.refreshDynamicItems();
            }

        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION );
        }

    }
}
