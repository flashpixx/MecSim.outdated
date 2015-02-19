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

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * source code editor primary for MAS editing based on the RSyntaxTextArea
 *
 * @see http://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
 * @see https://github.com/bobbylight/RSyntaxTextArea
 */
public class CSourceEditor extends JTabbedPane implements ActionListener
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * map to store reference file, tab and editor
     */
    protected Map<File, Pair<JComponent, RSyntaxTextArea>> m_tabs = new HashMap<>();
    /**
     * map with components (buttons), button name, file object
     */
    protected Map<JComponent, Pair<String, File>> m_actionobject = new HashMap<>();
    /**
     * set of action listener that will call on editor action
     */
    protected Set<ActionListener> m_listener = new HashSet<>();


    /**
     * adds an action listener
     *
     * @param p_listener action listener
     */
    public void addActionListener( final ActionListener p_listener )
    {
        m_listener.add( p_listener );
    }

    /**
     * removes an action listener
     *
     * @param p_listener action listener
     */
    public void removeActionListener( final ActionListener p_listener )
    {
        m_listener.remove( p_listener );
    }


    /**
     * open a new editor tab
     *
     * @param p_file input file
     */
    public void open( final File p_file )
    {
        if ( m_tabs.containsKey( p_file ) )
            return;

        if ( ( !p_file.exists() ) || ( !p_file.canRead() ) || ( !p_file.canWrite() ) )
            throw new IllegalStateException( CCommon.getResouceString( this, "readwrite", p_file ) );

        try
        {
            final JComponent l_tab = new JPanel();
            this.add( p_file.getName(), l_tab );
            l_tab.setLayout( new BorderLayout() );

            // create editor
            final RSyntaxTextArea l_editor = new RSyntaxTextArea();
            l_editor.setAutoIndentEnabled( true );
            m_tabs.put( p_file, new ImmutablePair<JComponent, RSyntaxTextArea>( l_tab, l_editor ) );

            // tab toolbar
            final JToolBar l_toolbar = new JToolBar();
            l_toolbar.setFloatable( false );
            l_tab.add( l_toolbar, BorderLayout.PAGE_START );


            for ( String[] l_item : new String[][]{{"save", "sourceeditor_save.png"}, {"delete", "sourceeditor_delete.png"}, {"reload", "sourceeditor_reload.png"}, {"close", "sourceeditor_close.png"}} )
            {
                final JButton l_button = new JButton( CCommon.getResouceString( this, l_item[0] ), new ImageIcon( ImageIO.read( this.getClass().getResource( "/images/" + l_item[1] ) ) ) );
                l_button.addActionListener( this );
                l_toolbar.add( l_button );

                m_actionobject.put( l_button, new ImmutablePair<String, File>( l_item[0], p_file ) );
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
    protected void readFile( final RSyntaxTextArea p_editor, final File p_file ) throws IOException
    {
        final BufferedReader l_reader = new BufferedReader( new FileReader( p_file ) );
        p_editor.read( l_reader, null );
        l_reader.close();
    }

    /**
     * writes data from editor into file
     *
     * @param p_editor editor
     * @param p_file   output file
     */
    protected void writeFile( final RSyntaxTextArea p_editor, final File p_file ) throws IOException
    {
        final FileWriter l_filewriter = new FileWriter( p_file );
        final BufferedWriter l_writer = new BufferedWriter( l_filewriter );
        p_editor.write( l_writer );
        l_writer.close();
        l_filewriter.close();
    }


    /**
     * remove all data of a tab from the internal maps
     *
     * @param p_tab  tab object
     * @param p_file file object
     */
    protected void removeTabData( final JComponent p_tab, final File p_file )
    {
        for ( int i = 0; i < p_tab.getComponentCount(); i++ )
            m_actionobject.remove( p_tab.getComponent( i ) );

        this.remove( p_tab );
        m_tabs.remove( p_file );
    }


    @Override
    public void actionPerformed( final ActionEvent p_event )
    {
        final Pair<String, File> l_item = m_actionobject.get( p_event.getSource() );
        if ( l_item == null )
            return;

        final Pair<JComponent, RSyntaxTextArea> l_component = m_tabs.get( l_item.getRight() );
        if ( l_component == null )
            return;

        try
        {

            if ( "save".equalsIgnoreCase( l_item.getLeft() ) )
                this.writeFile( l_component.getRight(), l_item.getRight() );

            if ( "reload".equalsIgnoreCase( l_item.getLeft() ) )
                this.readFile( l_component.getRight(), l_item.getRight() );

            if ( "close".equalsIgnoreCase( l_item.getLeft() ) )
                this.removeTabData( m_tabs.get( l_item.getRight() ).getLeft(), l_item.getRight() );

            if ( "delete".equalsIgnoreCase( l_item.getLeft() ) )
            {
                if ( !l_item.getRight().delete() )
                    throw new IllegalStateException( CCommon.getResouceString( this, "filedelete", l_item.getRight().getName() ) );

                this.removeTabData( m_tabs.get( l_item.getRight() ).getLeft(), l_item.getRight() );
            }

        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResouceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
        }

        for ( ActionListener l_action : m_listener )
            l_action.actionPerformed( p_event );

    }
}
