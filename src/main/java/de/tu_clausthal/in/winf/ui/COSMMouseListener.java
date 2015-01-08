/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.ui;

import de.tu_clausthal.in.winf.object.norm.INormObject;
import de.tu_clausthal.in.winf.object.norm.institution.IInstitution;
import de.tu_clausthal.in.winf.object.norm.range.CRangeGPS;
import de.tu_clausthal.in.winf.object.source.CDefaultSourceFactory;
import de.tu_clausthal.in.winf.object.source.CSourceFactoryLayer;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.ui.painter.CRectanglePainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;


/**
 * mouse listener for jxviewer *
 */
class COSMMouseListener extends MouseAdapter
{

    /**
     * popup *
     */
    private CMenuPopup m_popup = new CMenuPopup();
    /**
     * rectangle painter to push norm ranges *
     */
    private CRectanglePainter m_rectangle = null;
    /**
     * flag to detect dragging *
     */
    private boolean m_drag = false;

    @Override
    public void mouseClicked( MouseEvent e )
    {

        try
        {
            // left double-click
            if ( ( SwingUtilities.isLeftMouseButton( e ) ) && ( e.getClickCount() == 2 ) )
            {
                if ( CSimulation.getInstance().isRunning() )
                    throw new IllegalStateException( "simulation is running" );

                COSMViewer l_viewer = (COSMViewer) e.getSource();
                Rectangle l_viewportBounds = l_viewer.getViewportBounds();
                Point2D l_position = new Point( l_viewportBounds.x + e.getPoint().x, l_viewportBounds.y + e.getPoint().y );
                GeoPosition l_geoposition = l_viewer.getTileFactory().pixelToGeo( l_position, l_viewer.getZoom() );

                ( (CSourceFactoryLayer) CSimulation.getInstance().getWorld().get( "Source" ) ).add( new CDefaultSourceFactory( l_geoposition ) );

/*
                boolean l_remove = false;
                for (ICarSourceFactory l_source : CSimulationData.getInstance().getSourceQueue().getAll())
                    if (this.inRange(l_position, l_viewer.getTileFactory().geoToPixel(l_source.getPosition(), l_viewer.getZoom()), 10)) {
                        CSimulationData.getInstance().getSourceQueue().remove(l_source);
                        l_remove = true;
                        break;
                    }

                if (!l_remove) {
                    if (m_popup.getSourceSelection().equals("default cars"))
                        CSimulationData.getInstance().getSourceQueue().add(new CDefaultSource(l_viewer.getTileFactory().pixelToGeo(l_position, l_viewer.getZoom())));
                    if (m_popup.getSourceSelection().equals("norm cars"))
                        CSimulationData.getInstance().getSourceQueue().add(new CNormSource(l_viewer.getTileFactory().pixelToGeo(l_position, l_viewer.getZoom())));
                }*/
            }

            // right-click
            if ( ( SwingUtilities.isRightMouseButton( e ) ) && ( e.getClickCount() == 1 ) )
            {
                m_popup.update();
                m_popup.show( e.getComponent(), e.getX(), e.getY() );
            }
        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION );
        }
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
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
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
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
    }

    @Override
    public void mouseDragged( MouseEvent e )
    {
        if ( !m_drag )
            return;
        m_rectangle.to( e.getPoint() );
    }

    /**
     * returns the popup listener
     *
     * @return popup listener
     */
    public CMenuPopup getPopupListener()
    {
        return m_popup;
    }


    /**
     * checks if a point is in a box around another point
     *
     * @param p_checkposition point of the click
     * @param p_center        point of the source
     * @param p_size          rectangle size
     * @return boolean on existence
     */
    private boolean inRange( Point2D p_checkposition, Point2D p_center, int p_size )
    {
        if ( ( p_checkposition == null ) || ( p_center == null ) )
            return true;

        return ( ( p_checkposition.getX() - p_size / 2 ) <= p_center.getX() ) && ( ( p_checkposition.getX() + p_size / 2 ) >= p_center.getX() ) && ( ( p_checkposition.getY() - p_size / 2 ) <= p_center.getY() ) && ( ( p_checkposition.getY() + p_size / 2 ) >= p_center.getY() );
    }


    /**
     * returns the selected institution
     *
     * @return institution
     */
    private IInstitution<INormObject> getSelectedInstitution()
    {
        if ( ( m_popup.getInstitutionSelection() == null ) || ( m_popup.getInstitutionSelection().isEmpty() ) )
            return null;
/*
        for (IInstitution<INormObject> l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
            if (l_item.getName().equals(m_popup.getInstitutionSelection()))
                return l_item;
*/
        return null;
    }

}
