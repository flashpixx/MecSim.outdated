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
import de.tu_clausthal.in.mec.common.CCommonUI;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgentLayer;
import de.tu_clausthal.in.mec.object.norm.INormObject;
import de.tu_clausthal.in.mec.object.norm.institution.IInstitution;
import de.tu_clausthal.in.mec.object.source.CSourceLayer;
import de.tu_clausthal.in.mec.object.source.ISource;
import de.tu_clausthal.in.mec.simulation.CSimulation;
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
     * flag to detect dragging
     */
    private boolean m_drag = false;


    @Override
    public void mouseClicked( final MouseEvent p_event )
    {
        try {
            final COSMViewer l_viewer = (COSMViewer) p_event.getSource();

            // left single-click
            if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 1 ) ){
                final CSourceLayer l_sourcelayer = ((CSourceLayer) CSimulation.getInstance().getWorld().get("Sources"));
                final Point2D l_mousePosition = this.getMousePosition(p_event, l_viewer);

                //Check for Sources in Range
                for ( ISource l_source : l_sourcelayer ){
                    if (this.inRange(l_mousePosition, l_viewer.getTileFactory().geoToPixel(l_source.getPosition(), l_viewer.getZoom()), c_rangesize)){
                        CLogger.out("Source selected");
                        //l_sourcelayer.setSelectedSource(l_source);
                        return ;
                    }
                }
            }

            // left double-click
            if ( ( SwingUtilities.isLeftMouseButton( p_event ) ) && ( p_event.getClickCount() == 2 ) ) {

                if ( CSimulation.getInstance().isRunning() )
                    throw new IllegalStateException( CCommon.getResourceString(this, "running") );

                //Get some Data
                final CSourceLayer l_sourcelayer = ((CSourceLayer) CSimulation.getInstance().getWorld().get("Sources"));
                final CCarJasonAgentLayer l_jasonlayer = ((CCarJasonAgentLayer) CSimulation.getInstance().getWorld().get("Jason Car Agents"));
                final Point2D l_mousePosition = this.getMousePosition(p_event, l_viewer);
                final GeoPosition l_geoPosition = this.getMouseGeoPosition(p_event, l_viewer);
                final String l_selectedGenerator = ((CMenuBar) CSimulation.getInstance().getUI().getJMenuBar()).getSelectedSourceName();
                final COSMKeyListener l_keyListener = l_viewer.getKeyListener();

                //If no Shortcut is pressed (Place or Remove Source)
                if(!l_keyListener.isAnyKeyPressed()){

                    //Check for Sources in Range
                    for ( ISource l_source : l_sourcelayer ){
                        if (this.inRange(l_mousePosition, l_viewer.getTileFactory().geoToPixel(l_source.getPosition(), l_viewer.getZoom()), c_rangesize)){
                            CLogger.out("Source removed");
                            //l_sourcelayer.removeSource(l_source);
                            l_source.release();
                            l_sourcelayer.remove( l_source );
                            return ;
                        }
                    }
                    CLogger.out("Source created");
                    //l_sourcelayer.createSource(l_geoPosition);
                    final String l_aslname = l_selectedGenerator.contains("Jason") ? CCommonUI.openGroupSelectDialog( l_jasonlayer.getAgentFiles(), CCommon.getResourceString(this, "chooseasl"), CCommon.getResourceString(this, "chooseasldescription") ) : null;
                    l_sourcelayer.add(
                            l_sourcelayer.getSource(
                                    l_selectedGenerator,
                                    l_geoPosition,
                                    l_aslname
                            )
                    );
                }

                //Strg Key is pressed (Place or Remove Generator)
                if(l_keyListener.isStrgPressed() && l_keyListener.getKeyPressedCount()<2){

                    //Check for Sources in Range
                    for ( ISource l_source : l_sourcelayer ){
                        if (this.inRange(l_mousePosition, l_viewer.getTileFactory().geoToPixel(l_source.getPosition(), l_viewer.getZoom()), c_rangesize)){
                            final String l_aslname = l_selectedGenerator.contains("Jason") ? CCommonUI.openGroupSelectDialog( l_jasonlayer.getAgentFiles(), CCommon.getResourceString(this, "chooseasl"), CCommon.getResourceString(this, "chooseasldescription") ) : null;
                            CLogger.out("Get or Set Generator");
                            //l_sourcelayer.getOrSetGenerator(l_source, l_selectedGenerator, l_aslname);
                            return ;
                        }
                    }
                }

                //Shift Key is pressed (Place or Remove  Destination)
                if(l_keyListener.isShiftPressed() && l_keyListener.getKeyPressedCount()<2){

                    //Check for Destinations in Range
                    CLogger.out("Destination removed");
                    //l_sourcelayer.removeDestination();

                    //If not add Destination
                    CLogger.out("Destination created");
                    //l_sourcelayer.createDestination(l_geoPosition);
                }

            }
        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString(this, "warning"), JOptionPane.CANCEL_OPTION );
        }

    }

    @Override
    public void mousePressed( final MouseEvent p_event )
    {
        /*
        if ( ( SwingUtilities.isLeftMouseButton( e ) ) && ( !m_drag ) )
            return;

        // create painter on the first action, because in the ctor the OSM Viewer is not fully instantiate
        // and would create an exception, so we do the initialization process here
        if ( m_rectangle == null )
        {
            m_rectangle = new CRectanglePainter();
            COSMViewer.getInstance().getCompoundPainter().addPainter( m_rectangle );
        }

        m_drag = true;
        m_rectangle.from( e.getPoint() );
        */
    }

    @Override
    public void mouseReleased( final MouseEvent p_event )
    {
        /*
        if ( !m_drag )
            return;

        m_drag = false;
        m_rectangle.to( e.getPoint() );

        IInstitution<INormObject> l_institution = this.getSelectedInstitution();
        if ( ( m_rectangle.getRectangle() == null ) || ( l_institution == null ) )
        {
            m_rectangle.clear();
            return;
        }

        // add range to the institution
        l_institution.getRange().add( new CRangeGPS( l_institution, ( (JXMapViewer) e.getSource() ).convertPointToGeoPosition( m_rectangle.getFrom() ), ( (JXMapViewer) e.getSource() ).convertPointToGeoPosition( m_rectangle.getTo() ) ) );
        m_rectangle.clear();
        */
    }

    @Override
    public void mouseDragged( final MouseEvent p_event )
    {
        /*
        if ( !m_drag )
            return;
        m_rectangle.to( e.getPoint() );
        */
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
        if ( ( p_checkposition == null ) || ( p_center == null ) )
            return false;

        return ( ( p_checkposition.getX() - p_size / 2 ) <= p_center.getX() ) && ( ( p_checkposition.getX() + p_size / 2 ) >= p_center.getX() ) && ( ( p_checkposition.getY() - p_size / 2 ) <= p_center.getY() ) && ( ( p_checkposition.getY() + p_size / 2 ) >= p_center.getY() );
    }

    /**
     * returns the selected institution
     *
     * @return institution
     */
    private IInstitution<INormObject> getSelectedInstitution()
    {
/*
        if ( ( m_popup.getInstitutionSelection() == null ) || ( m_popup.getInstitutionSelection().isEmpty() ) )
            return null;

        for (IInstitution<INormObject> l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
            if (l_item.getName().equals(m_popup.getInstitutionSelection()))
                return l_item;
*/
        return null;
    }

}