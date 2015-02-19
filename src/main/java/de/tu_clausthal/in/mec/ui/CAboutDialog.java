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


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * about dialog to show current version data
 */
public class CAboutDialog extends JDialog
{

    /**
     * ctor
     */
    public CAboutDialog()
    {
        this( null );
    }


    /**
     * ctor
     *
     * @param p_frame parent frame
     */
    public CAboutDialog( final Frame p_frame )
    {
        super( p_frame );
        if ( p_frame != null )
            this.setLocationRelativeTo( p_frame );

        this.setAlwaysOnTop( true );
        this.setResizable( false );
        this.setModalityType( ModalityType.TOOLKIT_MODAL );

        final JPanel l_panel = new JPanel();
        l_panel.setLayout( new BorderLayout( 8, 8 ) );
        this.add( l_panel );

        l_panel.add( this.getHeader(), BorderLayout.PAGE_START );
        l_panel.add( this.getBuildData(), BorderLayout.CENTER );

        this.pack();
        this.setSize( 450, 200 );
    }

    /**
     * returns a panel with header information
     *
     * @return panel
     */
    protected JPanel getHeader()
    {
        final JPanel l_panel = new JPanel();
        l_panel.setLayout( new GridBagLayout() );
        final GridBagConstraints l_layout = new GridBagConstraints();
        l_layout.fill = GridBagConstraints.HORIZONTAL;

        final JLabel l_title = new CLinkLabel( CConfiguration.getInstance().getManifest().get( "Project-Name" ), CConfiguration.getInstance().getManifest().get( "Project-URL" ), JLabel.CENTER );
        l_title.setFont( new Font( l_title.getFont().getName(), Font.BOLD, (int) ( l_title.getFont().getSize() * 1.5 ) ) );
        l_layout.gridx = 1;
        l_layout.gridy = 0;
        l_panel.add( l_title, l_layout );

        final JLabel l_url = new CLinkLabel( CConfiguration.getInstance().getManifest().get( "Project-URL" ), JLabel.CENTER );
        l_layout.gridx = 1;
        l_layout.gridy = 1;
        l_panel.add( l_url, l_layout );

        return l_panel;
    }

    /**
     * returns a planel with build data
     *
     * @return panel
     */
    protected JPanel getBuildData()
    {
        final JPanel l_panel = new JPanel();
        l_panel.setLayout( new GridBagLayout() );
        final GridBagConstraints l_layout = new GridBagConstraints();
        l_layout.fill = GridBagConstraints.HORIZONTAL;

        l_layout.gridx = 0;
        l_layout.gridy = 0;
        l_panel.add( new JLabel( "Build-Version", JLabel.RIGHT ), l_layout );
        l_layout.gridx = 1;
        l_panel.add( Box.createHorizontalStrut( 20 ) );
        l_layout.gridx = 2;
        l_panel.add( new JLabel( CConfiguration.getInstance().getManifest().get( "Build-Version" ) ), l_layout );

        l_layout.gridx = 0;
        l_layout.gridy = 1;
        l_panel.add( new JLabel( CCommon.getResouceString( this, "buildnumber" ), JLabel.RIGHT ), l_layout );
        l_layout.gridx = 1;
        l_panel.add( Box.createHorizontalStrut( 20 ) );
        l_layout.gridx = 2;
        l_panel.add( new JLabel( CConfiguration.getInstance().getManifest().get( "Build-Number" ) ), l_layout );

        l_layout.gridx = 0;
        l_layout.gridy = 2;
        l_panel.add( new JLabel( CCommon.getResouceString( this, "buildcommit" ), JLabel.RIGHT ), l_layout );
        l_layout.gridx = 1;
        l_panel.add( Box.createHorizontalStrut( 20 ) );
        l_layout.gridx = 2;
        String l_commit = CConfiguration.getInstance().getManifest().get( "Build-Commit" );
        if ( l_commit != null )
            l_commit = l_commit.substring( 0, Math.min( 9, l_commit.length() ) );
        l_panel.add( new JLabel( l_commit ), l_layout );

        l_layout.gridx = 0;
        l_layout.gridy = 3;
        l_panel.add( new JLabel( CCommon.getResouceString( this, "license" ), JLabel.RIGHT ), l_layout );
        l_layout.gridx = 1;
        l_panel.add( Box.createHorizontalStrut( 20 ) );
        l_layout.gridx = 2;
        l_panel.add( new CLinkLabel( CConfiguration.getInstance().getManifest().get( "License" ), CConfiguration.getInstance().getManifest().get( "License-URL" ) ), l_layout );

        return l_panel;
    }


    /**
     * create a label with clickable hypelink
     */
    protected class CLinkLabel extends JLabel
    {
        /**
         * URI *
         */
        private URI m_uri = null;


        /**
         * ctor
         *
         * @param p_uri uri
         */
        public CLinkLabel( final String p_uri )
        {
            this( p_uri, p_uri, 0 );
        }

        /**
         * ctor
         *
         * @param p_uri                 uri
         * @param p_horizontalalignment horizontal alignment
         */
        public CLinkLabel( final String p_uri, final int p_horizontalalignment )
        {
            this( p_uri, p_uri, p_horizontalalignment );
        }

        /**
         * ctor
         *
         * @param p_text text
         * @param p_uri  uri
         */
        public CLinkLabel( final String p_text, final String p_uri )
        {
            this( p_text, p_uri, 0 );
        }

        /**
         * ctor
         *
         * @param p_text text of the label
         * @param p_uri  link
         * @param p_horizontalalignment horizontal alignment
         */
        public CLinkLabel( final String p_text, final String p_uri, final int p_horizontalalignment )
        {
            super();
            this.setText( p_text );
            this.setHorizontalAlignment( p_horizontalalignment );

            if ( p_uri != null )
                try
                {
                    m_uri = new URI( p_uri );
                }
                catch ( URISyntaxException l_exception )
                {
                }

            this.addMouseListener(
                    new MouseAdapter()
                    {
                        public void mouseClicked( MouseEvent p_event )
                        {
                            open();
                        }
                    } );
        }

        /**
         * open with default browser *
         */
        private void open()
        {
            if ( ( !Desktop.isDesktopSupported() ) || ( m_uri == null ) )
                return;

            try
            {
                Desktop.getDesktop().browse( m_uri );
            }
            catch ( IOException l_exception )
            {
                CLogger.error( l_exception );
            }
        }
    }

}
