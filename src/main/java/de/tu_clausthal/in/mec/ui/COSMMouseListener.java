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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgentLayer;
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
     * @bug UI frame
     */
    @Override
    public void mouseClicked( final MouseEvent p_event )
    {
        try
        {
            final COSMViewer l_viewer = (COSMViewer) p_event.getSource();

            // left single-click
            if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 1 ) )
            {

                Thread.sleep( 200 );

                final CSourceLayer l_sourcelayer = ( (CSourceLayer) CSimulation.getInstance().getWorld().get( "Sources" ) );
                final Point2D l_mousePosition = this.getMousePosition( p_event, l_viewer );

                //Check for Sources in Range
                for ( ISource l_source : l_sourcelayer )
                {
                    if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_source.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                    {
                        l_sourcelayer.setSelectedSource( l_source );
                        return;
                    }
                }
            }

            // left double-click
            if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 2 ) )
            {

                if ( CSimulation.getInstance().isRunning() )
                    throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );

                m_doubleClick = true;

                //Get some Data
                final CSourceLayer l_sourcelayer = ( (CSourceLayer) CSimulation.getInstance().getWorld().get( "Sources" ) );
                final CCarJasonAgentLayer l_jasonlayer = ( (CCarJasonAgentLayer) CSimulation.getInstance().getWorld().get( "Jason Car Agents" ) );
                final Point2D l_mousePosition = this.getMousePosition( p_event, l_viewer );
                final GeoPosition l_geoPosition = this.getMouseGeoPosition( p_event, l_viewer );
                //final String l_selectedGenerator = ( (CMenuBar) CSimulation.getInstance().getUIServer().getJMenuBar() ).getSelectedSourceName();
                final COSMKeyListener l_keyListener = l_viewer.getKeyListener();

                //If no Shortcut is pressed (Place or Remove Source)
                if ( !l_keyListener.isAnyKeyPressed() )
                {

                    //Check for Sources in Range
                    for ( ISource l_source : l_sourcelayer )
                    {
                        if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_source.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                        {
                            l_sourcelayer.removeSource( l_source );
                            return;
                        }
                    }

                    //final String l_aslname = l_selectedGenerator.contains( "Jason" ) ? CCommonUI.openGroupSelectDialog( l_jasonlayer.getAgentFiles(), CCommon.getResourceString( this, "chooseasl" ), CCommon.getResourceString( this, "chooseasldescription" ) ) : null;
                    //l_sourcelayer.createSource( l_geoPosition, l_selectedGenerator, l_aslname );
                }

                //Strg Key is pressed (Place or Remove Generator)
                if ( l_keyListener.isStrgPressed() && l_keyListener.getKeyPressedCount() < 2 )
                {

                    //Check for Sources in Range
                    for ( ISource l_source : l_sourcelayer )
                    {
                        if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_source.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                        {

                            if ( l_source.getGenerator() == null )
                            {
                                //final String l_aslname = l_selectedGenerator.contains( "Jason" ) ? CCommonUI.openGroupSelectDialog( l_jasonlayer.getAgentFiles(), CCommon.getResourceString( this, "chooseasl" ), CCommon.getResourceString( this, "chooseasldescription" ) ) : null;
                                //l_sourcelayer.setGenerator( l_source, l_selectedGenerator, l_aslname );
                            }
                            else
                            {
                                l_sourcelayer.removeGenerator( l_source );
                            }

                            return;
                        }
                    }
                }

                //Shift Key is pressed (Place or Remove  Target)
                if ( l_keyListener.isShiftPressed() && l_keyListener.getKeyPressedCount() < 2 )
                {

                    for ( CAtomTarget l_target : l_sourcelayer.getTargets() )
                    {
                        //Check for Target in Range and remove it
                        if ( this.inRange( l_mousePosition, l_viewer.getTileFactory().geoToPixel( l_target.getPosition(), l_viewer.getZoom() ), c_rangesize ) )
                        {
                            l_sourcelayer.removeTarget( l_target );
                            return;
                        }
                    }

                    //If not add Target
                    l_sourcelayer.createTarget( l_geoPosition );
                }

                m_doubleClick = false;
            }
        }
        catch ( final Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
        }

    }

    /**
     * returns the geoposition of a mouse position
     *
     * @param p_event  mouse event
     * @param p_viewer OSM viewer
     * @return geoposition
     */
    protected GeoPosition getMouseGeoPosition( final MouseEvent p_event, final COSMViewer p_viewer )
    {
        final Point2D l_position = this.getMousePosition( p_event, p_viewer );
        return p_viewer.getTileFactory().pixelToGeo( l_position, p_viewer.getZoom() );
    }

    /**
     * returns the 2D position of a mouse position
     *
     * @param p_event  mouse event
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
     * @param p_center        point of the source
     * @param p_size          rectangle size
     * @return boolean on existence
     */
    protected boolean inRange( final Point2D p_checkposition, final Point2D p_center, final int p_size )
    {
        if ( ( p_checkposition == null ) || ( p_center == null ) ) return false;

        return ( ( p_checkposition.getX() - p_size / 2 ) <= p_center.getX() ) && ( ( p_checkposition.getX() + p_size / 2 ) >= p_center.getX() ) && ( ( p_checkposition.getY() - p_size / 2 ) <= p_center.getY() ) && ( ( p_checkposition.getY() + p_size / 2 ) >= p_center.getY() );
    }

}
