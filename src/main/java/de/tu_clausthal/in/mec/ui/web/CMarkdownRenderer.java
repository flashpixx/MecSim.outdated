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


import de.tu_clausthal.in.mec.CConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.WikiLinkNode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * class of the link and layout markdown renderer
 */
public class CMarkdownRenderer extends LinkRenderer
{
    /**
     * string with optional CSS URI *
     */
    protected final String m_cssuri;
    /**
     * string with base URI
     */
    protected final String m_baseuri;
    /**
     * rendering enum
     */
    protected final EHTMLType m_htmltype;



    /**
     * ctor
     */
    public CMarkdownRenderer()
    {
        this( EHTMLType.Fragment, "", "" );
    }

    /**
     * ctor
     *
     * @param p_htmltype type of the HTML result
     */
    public CMarkdownRenderer( final EHTMLType p_htmltype )
    {
        this( p_htmltype, "", "" );
    }


    /**
     * ctor
     *
     * @param p_htmltype type of the HTML result
     * @param p_baseuri  base URI
     */
    public CMarkdownRenderer( final EHTMLType p_htmltype, final String p_baseuri )
    {
        this( p_htmltype, p_baseuri, "" );
    }


    /**
     * ctor
     *
     * @param p_htmltype type of the HTML result
     * @param p_baseuri base URI
     * @param p_cssuri CSS URI
     */
    public CMarkdownRenderer( final EHTMLType p_htmltype, final String p_baseuri, final String p_cssuri )
    {
        m_htmltype = p_htmltype;
        m_baseuri = p_baseuri;
        m_cssuri = p_cssuri;
    }


    private String getURL( final String p_url )
    {
        return ( ( p_url == null ) || ( p_url.isEmpty() ) ) ? p_url : m_baseuri + p_url;
    }

    @Override
    public final Rendering render( final ExpLinkNode p_node, final String p_text )
    {
        return super.render( new ExpLinkNode( p_text, this.getURL( p_node.url ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), p_text );
    }

    @Override
    public final Rendering render( final ExpImageNode p_node, final String p_text )
    {
        try
        {
            new URL( p_node.url );
        }
        catch ( final MalformedURLException l_exception )
        {
            return super.render( new ExpImageNode( p_node.title, this.getURL( p_node.url ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), p_text );
        }

        return super.render( p_node, p_text );
    }

    @Override
    public final Rendering render( final WikiLinkNode p_node )
    {
        try
        {
            // split node text into this definition
            // [[item]] -> item will be searched at Wikipedia in the current language
            // [[search|item]] first part will be search at Wikipedia and second will be shown within the help
            // [[language|search|item]] first part is the language at Wikipedia, search the searched item and item will be sown

            final String[] l_parts = StringUtils.split( p_node.getText(), "|" );
            if ( l_parts.length == 1 )
                return super.render( new ExpLinkNode( l_parts[0], "http://" + CConfiguration.getInstance().get().Language + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[0].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[0] );
            if ( l_parts.length == 2 )
                return super.render( new ExpLinkNode( l_parts[1], "http://" + CConfiguration.getInstance().get().Language + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[0].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[1] );
            if ( l_parts.length == 3 )
                return super.render( new ExpLinkNode( l_parts[2], "http://" + l_parts[0].trim() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[1].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[2] );

        }
        catch ( final Exception l_exception )
        {
        }

        return super.render( p_node );
    }


    /**
     * creates HTML content from a markdown document
     *
     * @param p_processor markdown processor
     * @param p_file      URL of the file
     * @return HTML content
     */
    public final String getHTML( final PegDownProcessor p_processor, final URL p_file ) throws IOException
    {
        switch ( m_htmltype )
        {
            case Document:
                return this.getHTMLDocument( p_processor, p_file );
            case Fragment:
                return this.getHTMLFragment( p_processor, p_file );
            default:
        }

        return null;
    }


    /**
     * creates a full HTML document from a markdown document
     *
     * @param p_processor markdown processor
     * @param p_file      URL of the file
     * @return full HTML document
     */
    private String getHTMLDocument( final PegDownProcessor p_processor, final URL p_file ) throws IOException
    {
        return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                ( m_cssuri.isEmpty() ? "" : "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"" + m_cssuri + "\"/></head>\n" ) +
                "<body>\n" + this.getHTMLFragment( p_processor, p_file ) + "\n</body></html>";
    }


    /**
     * creates a HTML fragment from a markdown document
     *
     * @param p_processor markdown processor
     * @param p_file      URL of the file
     * @return HTML fragment
     */
    private String getHTMLFragment( final PegDownProcessor p_processor, final URL p_file ) throws IOException
    {
        return p_processor.markdownToHtml( IOUtils.toString( p_file ).toCharArray(), this );
    }


    /**
     * enum to define return type of the HTML document *
     */
    public enum EHTMLType
    {
        Fragment, Document
    }

}
