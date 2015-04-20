/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.object.source.CSourceLayer;
import de.tu_clausthal.in.mec.object.source.ISource;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CAtomTarget;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Map;


/**
 * mouse listener for JxViewer
 */
class COSMMouseListener extends MouseAdapter
{

    /**
     * size of the click range
     */
    private static final int c_rangesize = 10;
    /**
     * Flag for double click
     */
    private boolean m_doubleClick = false;
    /**
     * member variable which indicates the tool for the sourcelayer
     */
    private String m_sourceLayerTool = "sourcemode";

    /**
     * @bug incomplete - error messages
     * @bug mouse events should be run with http://docs.oracle.com/javase/8/docs/api/java/awt/event/MouseEvent.html
     */
    @Override
    public void mouseClicked( final MouseEvent p_event )
    {
        try
        {
            if ( CSimulation.getInstance().isRunning() )
            {
            }
            ;
            //throw new IllegalStateException( CCommon.getResourceString(this, "running") );

            // single left click
            if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 1 ) )
            {
                Thread.sleep( 200 );
                leftClick( p_event );
            }

            // double left click
            if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 2 ) )
            {
                m_doubleClick = true;
                doubleLeftClick( p_event );
                m_doubleClick = false;
            }

        }
        catch ( final Exception l_exception )
        {
            //JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
        }
    }

    /**
     * method for single left click
     *
     * @param p_event
     */
    private void leftClick( final MouseEvent p_event )
    {
        final COSMViewer l_viewer = (COSMViewer) p_event.getSource();
        final CSourceLayer l_sourcelayer = ( (CSourceLayer) CSimulation.getInstance().getWorld().get( "Sources" ) );
        final Point2D l_mousePosition = this.getMousePosition( p_event, l_viewer );

        //select a source in range
        for ( ISource l_source : l_sourcelayer )
        {
            if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_source.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
            {
                l_sourcelayer.setSelectedSource( l_source );
                return;
            }
        }
    }

    private void doubleLeftClick( final MouseEvent p_event )
    {
        final COSMViewer l_viewer = (COSMViewer) p_event.getSource();
        final CSourceLayer l_sourcelayer = ( (CSourceLayer) CSimulation.getInstance().getWorld().get( "Sources" ) );
        final Point2D l_mousePosition = this.getMousePosition( p_event, l_viewer );
        final GeoPosition l_geoPosition = this.getMouseGeoPosition( p_event, l_viewer );
        //final String l_selectedGenerator = ( (CMenuBar) CSimulation.getInstance().getUIServer().getJMenuBar() ).getSelectedSourceName();
        //final CCarJasonAgentLayer l_jasonlayer = ( (CCarJasonAgentLayer) CSimulation.getInstance().getWorld().get( "Jason Car Agents" ) );

        //place or remove source
        if ( m_sourceLayerTool.equals( "sourcemode" ) )
        {
            for ( ISource l_source : l_sourcelayer )
            {
                if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_source.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                {
                    l_sourcelayer.removeSource( l_source );
                    return;
                }
            }
            l_sourcelayer.createSource( l_geoPosition );
        }

        //place or remote generator
        if ( m_sourceLayerTool.equals( "generatormode" ) )
        {

            for ( ISource l_source : l_sourcelayer )
            {
                if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_source.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                {

                    if ( l_source.getGenerator() == null )
                    {
                        //l_sourcelayer.setGenerator( l_source, l_selectedGenerator, l_aslname );
                    }
                    else
                    {
                        // l_sourcelayer.removeGenerator( l_source );
                    }

                    return;
                }
            }
        }

        //place or remove target
        if ( m_sourceLayerTool.equals( "targetmode" ) )
        {

            for ( CAtomTarget l_target : l_sourcelayer.getTargets() )
            {
                if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_target.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                {
                    l_sourcelayer.removeTarget( l_target );
                    return;
                }
            }

            l_sourcelayer.createTarget( l_geoPosition );
        }

    }

    /**
     * returns the 2D position of a mouse position
     *
     * @param p_event mouse event
     * @param p_viewer OSM viewer
     * @return point
     */
    protected Point2D getMousePosition( final MouseEvent p_event, final COSMViewer p_viewer )
    {
        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        return new Point( l_viewportBounds.x + p_event.getPoint().x, l_viewportBounds.y + p_event.getPoint().y );
    }

    /**
     * checks if a point is in a box around another point
     *
     * @param p_checkposition point of the click
     * @param p_center point of the source
     * @param p_size rectangle size
     * @return boolean on existence
     */
    protected boolean inRange( final Point2D p_checkposition, final Point2D p_center, final int p_size )
    {
        if ( ( p_checkposition == null ) || ( p_center == null ) )
            return false;

        return ( ( p_checkposition.getX() - p_size / 2 ) <= p_center.getX() ) && ( ( p_checkposition.getX() + p_size / 2 ) >= p_center.getX() ) &&
               ( ( p_checkposition.getY() - p_size / 2 ) <= p_center.getY() ) && ( ( p_checkposition.getY() + p_size / 2 ) >= p_center.getY() );
    }

    /**
     * returns the geoposition of a mouse position
     *
     * @param p_event mouse event
     * @param p_viewer OSM viewer
     * @return geoposition
     */
    protected GeoPosition getMouseGeoPosition( final MouseEvent p_event, final COSMViewer p_viewer )
    {
        final Point2D l_position = this.getMousePosition( p_event, p_viewer );
        return p_viewer.getTileFactory().pixelToGeo( l_position, p_viewer.getZoom() );
    }

    private void web_static_setsourcelayertool(final Map<String, Object> p_data )
    {
        String l_tool = (String) p_data.get( "tool" );
        if(l_tool.equals( "sourcemode" ) || l_tool.equals( "generatormode" ) || l_tool.equals( "targetmode" ))
            this.m_sourceLayerTool = l_tool;

        System.out.println(this.m_sourceLayerTool);
    }

}
