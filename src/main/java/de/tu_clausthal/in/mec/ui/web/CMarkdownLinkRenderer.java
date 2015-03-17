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
import org.apache.commons.lang3.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.WikiLinkNode;

import java.net.URLEncoder;


/**
 * link renderer to redefine link names *
 */
public class CMarkdownLinkRenderer extends LinkRenderer
{

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
                return super.render( new ExpLinkNode( l_parts[0], "http://" + CConfiguration.getInstance().get().getLanguage() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[0].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[0] );
            if ( l_parts.length == 2 )
                return super.render( new ExpLinkNode( l_parts[1], "http://" + CConfiguration.getInstance().get().getLanguage() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[0].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[1] );
            if ( l_parts.length == 3 )
                return super.render( new ExpLinkNode( l_parts[2], "http://" + l_parts[0].trim() + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode( l_parts[1].trim(), "UTF-8" ), ( p_node.getChildren() == null ) || ( p_node.getChildren().isEmpty() ) ? null : p_node.getChildren().get( 0 ) ), l_parts[2] );

        }
        catch ( Exception l_exception )
        {
        }

        return super.render( p_node );
    }

}
