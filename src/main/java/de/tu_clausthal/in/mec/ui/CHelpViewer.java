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
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import org.apache.commons.io.IOUtils;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.WikiLinkNode;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * help window
 *
 * @note help files are stored in Markdown syntax within the "help" directory for each language an own directory, links
 * with @code [[<Name>]] @endcode create a link to Wikipedia (with equal language), links with
 * @code ![<description>](<image file>)} @endcode create images to the file within the language help directory, links
 * without http prefix creates a link to a markdown file with equal name within the help directory
 */
public class CHelpViewer extends JDialog implements ActionListener
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * default language - safe structure because configuration can push a null value *
     */
    protected static String s_defaultlanguage = "en";
    /**
     * components
     */
    protected Map<JComponent, String> m_components = new HashMap<>();
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
     * ctor with window set (not modal)
     *
     * @param p_frame parent frame
     */
    public CHelpViewer( final Frame p_frame )
    {
        if ( p_frame != null )
            this.setLocationRelativeTo( p_frame );
        this.setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );

        final JToolBar l_toolbar = new JToolBar();
        l_toolbar.setFloatable( false );
        l_toolbar.setLayout( new FlowLayout( FlowLayout.CENTER ) );

        for ( String[] l_item : new String[][]{{"back", "helpbrowser_back.png"}, {"home", "helpbrowser_home.png"}, {"forward", "helpbrowser_forward.png"}} )

            try
            {
                final JButton l_button = new JButton( CCommon.getResouceString( this, l_item[0] ), new ImageIcon( ImageIO.read( this.getClass().getResource( "/images/" + l_item[1] ) ) ) );
                l_button.addActionListener( this );
                l_toolbar.add( l_button );
                m_components.put( l_button, l_item[0] );
            }
            catch ( Exception l_exception )
            {
                CLogger.error( l_exception );
            }

        final JPanel l_panel = new JPanel( new BorderLayout() );
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
    protected URL getFileURL( final String p_file )
    {
        return this.getClass().getClassLoader().getResource( "documentation" + File.separatorChar + this.getLanguage() + File.separator + p_file );
    }

    /**
     * returns the URL to the documentation directory
     *
     * @param p_file filename * @return full path
     */
    protected URL getDocumentationURL( final String p_file )
    {
        return this.getClass().getClassLoader().getResource( "documentation" + File.separator + p_file );
    }


    @Override
    public void actionPerformed( final ActionEvent p_event )
    {
        final String l_item = m_components.get( p_event.getSource() );
        if ( l_item == null )
            return;

        if ( "back".equalsIgnoreCase( l_item ) )
        {
            m_browser.back();
            return;
        }

        if ( "forward".equalsIgnoreCase( l_item ) )
        {
            m_browser.forward();
            return;
        }

        if ( "home".equalsIgnoreCase( l_item ) )
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
        public Rendering render( final ExpLinkNode p_node, final String p_text )
        {
            // check path for developer documentation (null value must be checked)
            if ( p_node.url.startsWith( "developer" ) )
            {
                final URL l_url = getDocumentationURL( p_node.url.toString() );
                if ( l_url != null )
                    return super.render( new ExpLinkNode( p_text, l_url.toString(), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), p_text );
            }

            return super.render( p_node, p_text );
        }

        @Override
        public Rendering render( final ExpImageNode p_node, final String p_text )
        {
            try
            {
                new URL( p_node.url );
            }
            catch ( MalformedURLException l_exception )
            {
                return super.render( new ExpImageNode( p_node.title, getFileURL( p_node.url ).toString(), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), p_text );
            }
            return super.render( p_node, p_text );
        }

        @Override
        public Rendering render( final WikiLinkNode p_node )
        {
            try
            {
                return super.render( new ExpLinkNode( p_node.getText(), "http://" + getLanguage() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( p_node.getText(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), p_node.getText() );
            }
            catch ( Exception l_exception )
            {
            }

            return super.render( p_node );
        }
    }


    /**
     * class to encapsulate browser component *
     */
    protected class CHelpBrowser extends CBrowser
    {
        /**
         * serialize version ID *
         */
        static final long serialVersionUID = 1L;
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
        public CHelpBrowser( final String p_resource )
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
        protected void processMarkdown( final WebEngine p_engine, final String p_source )
        {
            Platform.runLater( () -> {
                try (
                        BufferedReader l_reader = new BufferedReader( new InputStreamReader( getFileURL( p_source ).openStream() ) );
                )
                {
                    p_engine.loadContent( m_markdown.markdownToHtml( IOUtils.toString( l_reader ).toCharArray(), m_renderer ) );
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
        protected class CTagListener implements CBrowser.IActionListener
        {

            @Override
            public void onHrefClick( final WebEngine p_web, final Element p_element )
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

                // on internal links the href can be null, so ignore it
                if ( l_file == null )
                    return;

                // otherwise check markdown extension and call the markdown processor
                if ( !l_file.endsWith( ".md" ) )
                    l_file += ".md";

                processMarkdown( p_web, l_file );
            }

        }

    }

}
