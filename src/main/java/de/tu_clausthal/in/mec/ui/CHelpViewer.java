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
import de.tu_clausthal.in.mec.common.CCommon;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import org.apache.commons.io.IOUtils;
import org.pegdown.*;
import org.pegdown.ast.*;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


/**
 * help window
 *
 * @note help files are stored in Markdown syntax within the "help" directory
 */
public class CHelpViewer extends JDialog implements ActionListener
{

    /**
     * default language - safe structure because configuration can push a null value *
     */
    protected static String s_defaultlanguage = "en";
    /**
     * components
     */
    protected Map<JComponent, String> m_components = new HashMap();
    /**
     * browser
     */
    protected CHelpBrowser m_browser = new CHelpBrowser( "index.md" );


    /**
     * ctor
     */
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


        JToolBar l_toolbar = new JToolBar();
        l_toolbar.setFloatable( false );
        l_toolbar.setLayout( new FlowLayout( FlowLayout.CENTER ) );

        for ( String[] l_item : new String[][]{{"back", "helpbrowser_back.png"}, {"home", "helpbrowser_home.png"}, {"forward", "helpbrowser_forward.png"}} )

            try
            {
                JButton l_button = new JButton( CCommon.getResouceString( this, l_item[0] ), new ImageIcon( ImageIO.read( this.getClass().getResource( "/images/" + l_item[1] ) ) ) );
                l_button.addActionListener( this );
                l_toolbar.add( l_button );
                m_components.put( l_button, l_item[0] );
            }
            catch ( Exception l_exception )
            {
                CLogger.error( l_exception );
            }

        JPanel l_panel = new JPanel( new BorderLayout() );
        l_panel.add( l_toolbar, BorderLayout.NORTH );
        l_panel.add( m_browser, BorderLayout.CENTER );

        this.add( l_panel );
        this.pack();
    }


    /**
     * returns the language code
     *
     * @return language code
     */
    protected String getLanguage()
    {
        return CConfiguration.getInstance().get().getLanguage() == null ? s_defaultlanguage : CConfiguration.getInstance().get().getLanguage();
    }

    /**
     * returns a full path of a file within the help directory
     *
     * @param p_file file name
     * @return full path
     */
    protected URL getFileURL( String p_file )
    {
        return this.getClass().getClassLoader().getResource( "help" + File.separatorChar + this.getLanguage() + File.separator + p_file );
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        String l_item = m_components.get( e.getSource() );
        if ( l_item == null )
            return;

        if ( l_item.equalsIgnoreCase( "back" ) )
        {
            m_browser.back();
            return;
        }

        if ( l_item.equalsIgnoreCase( "forward" ) )
        {
            m_browser.forward();
            return;
        }

        if ( l_item.equalsIgnoreCase( "home" ) )
        {
            m_browser.home();
            return;
        }
    }


    /**
     * link renderer to redefine link names *
     */
    protected class CLinkRenderer extends LinkRenderer
    {

        @Override
        public Rendering render( ExpImageNode node, String text )
        {
            try
            {
                new URL( node.url );
            }
            catch ( MalformedURLException l_exception )
            {
                return super.render( new ExpImageNode( node.title, getFileURL( node.url ).toString(), ( node.getChildren() == null ) || ( node.getChildren().isEmpty() ) ? null : node.getChildren().get( 0 ) ), text );
            }
            return super.render( node, text );
        }

        @Override
        public Rendering render( WikiLinkNode node )
        {
            try
            {
                return super.render( new ExpLinkNode( node.getText(), "http://" + getLanguage() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( node.getText(), "UTF-8" ), ( node.getChildren() == null ) || ( node.getChildren().isEmpty() ) ? null : node.getChildren().get( 0 ) ), node.getText() );
            }
            catch ( Exception l_exception )
            {
            }

            return super.render( node );
        }
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
        /**
         * link renderer
         */
        protected LinkRenderer m_renderer = new CLinkRenderer();
        /**
         * home source *
         */
        protected String m_home = null;

        /**
         * ctor
         *
         * @param p_resource resource markdown file
         */
        public CHelpBrowser( String p_resource )
        {
            super();

            m_home = p_resource;

            this.addContentActionListener( new CTagListener() );
            this.home();
        }

        /**
         * read the start page *
         */
        public void home()
        {
            this.processMarkdown( m_webview.getEngine(), m_home );
        }

        /**
         * process the markdown transformation
         *
         * @param p_engine web engine
         * @param p_source path of the markdown file
         */
        protected void processMarkdown( WebEngine p_engine, String p_source )
        {
            Platform.runLater( () -> {
                try
                {
                    BufferedReader l_reader = new BufferedReader( new InputStreamReader( getFileURL( p_source ).openStream() ) );
                    p_engine.loadContent( m_markdown.markdownToHtml( IOUtils.toString( l_reader ).toCharArray(), m_renderer ) );
                    l_reader.close();
                }
                catch ( Exception l_exception )
                {
                    CLogger.error( l_exception );
                }
            } );
        }


        /**
         * action listener to read markdown files from the Jar help directory
         */
        protected class CTagListener extends CBrowser.IActionListener
        {

            @Override
            public void onHrefClick( WebEngine p_web, Element p_element )
            {
                try
                {
                    new URL( p_element.getAttribute( "href" ) );
                    return;
                }
                catch ( MalformedURLException l_exception )
                {
                }

                String l_file = p_element.getAttribute( "href" );
                if ( !l_file.endsWith( ".md" ) )
                    l_file += ".md";
                processMarkdown( p_web, l_file );
            }

        }

    }

}
