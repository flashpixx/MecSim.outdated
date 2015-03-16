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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.source.generator.CDefaultCarGenerator;
import de.tu_clausthal.in.mec.object.source.generator.CJasonCarGenerator;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Vector;


/**
 * layer with all sources
 */
public class CSourceLayer extends IMultiLayer<ISource>
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Member Variable to save a Source which was selected (null if no Source is selected)
     */
    private static ISource m_selectedSource = null;

    /**
     * List of all Destinations which might be set in some Target Objects for special Sourcess
     */
    private Vector<GeoPosition> m_destinations = new Vector<>();


    /**
     * Define the Calculation Index (When this Layer should be executes)
     */
    @Override
    public final int getCalculationIndex()
    {
        return 2;
    }

    /**
     * release overwrite, because all sources will be removed of the reset is called
     */
    @Override
    public void release()
    {

    }

    /**
     * Override paint Method from Multilayer to paint Destinations
     * Save Destinations in an own Layer is an Alternative (but maybe there is no further need for an own Layer)
     * @param p_graphic
     * @param p_viewer
     * @param p_width
     * @param p_height
     */
    @Override
    public void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        //Paint Sources
        if ( !m_visible )
            return;

        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        p_graphic.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( ISource l_source : this )
            l_source.paint( p_graphic, p_viewer, p_width, p_height );

        //Paint Destinations
        for ( GeoPosition l_destinations : m_destinations){
            final int l_zoom = Math.max(15 - p_viewer.getZoom(), 3);
            final Point2D l_point = p_viewer.getTileFactory().geoToPixel( l_destinations, p_viewer.getZoom() );
            p_graphic.setColor( Color.BLACK );
            p_graphic.fillRect( (int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom );
        }
    }

    /**
     * returns a list of source names
     *
     * @return source names
     */
    public final String[] getSourceNamesList()
    {
        return new String[]{"Default", "Jason Agent"};
    }

    /**
     * Method to create a Source
     */
    public void createSource( GeoPosition p_geoPosition, String p_defaultGenerator, String p_aslname )
    {
        CLogger.out( CCommon.getResourceString( this, "sourcecreated" ) );
        ISource l_newSource = new CSource( p_geoPosition );
        this.add( l_newSource );

        //Set Default Generator (Selected Generator)
        if ( ( p_defaultGenerator == null ) || ( p_defaultGenerator.contains( "Jason" ) && p_aslname == null ) )
            return;

        this.setGenerator( l_newSource, p_defaultGenerator, p_aslname );
    }

    /**
     * Method to Remove a Source
     */
    public void removeSource( ISource p_source )
    {
        CLogger.out( CCommon.getResourceString( this, "sourceremoved" ) );
        if ( this.isSelectedSource( p_source ) )
        {
            m_selectedSource = null;
        }

        p_source.release();
        this.remove( p_source );
    }

    /**
     * Method to create a Generator from a specific Source
     */
    public void setGenerator( ISource p_source, String p_selectedGenerator, String p_aslname )
    {
        CLogger.out( CCommon.getResourceString( this, "generatorcreated" ) );

        if ( p_selectedGenerator.equals( "Default" ) )
            p_source.setGenerator( new CDefaultCarGenerator( p_source.getPosition() ) );
        if ( p_selectedGenerator.equals( "Jason Agent" ) )
            p_source.setGenerator( new CJasonCarGenerator( p_source.getPosition(), p_aslname ) );
    }

    /**
     * Method to remove a Generator from a specific Source
     */
    public void removeGenerator( ISource p_source )
    {
        CLogger.out( CCommon.getResourceString( this, "generatorremoved" ) );
        p_source.removeGenerator();
    }

    /**
     * Method to create a Destination
     */
    public void createDestination( GeoPosition p_geoPosition )
    {
        CLogger.out( CCommon.getResourceString( this, "destinationcreated" ) );

        this.m_destinations.add(p_geoPosition);
        if ( this.m_selectedSource != null ){

        }
        CLogger.out(m_destinations.size());
        try
        {
            COSMViewer.getSimulationOSM().repaint();
        }
        catch ( Exception l_exception )
        {
        }
    }

    /**
     * Method to remove a Destination
     */
    public void removeDestination(GeoPosition p_position)
    {
        CLogger.out( CCommon.getResourceString( this, "destinationremoved" ) );
        this.m_destinations.remove(p_position);
        CLogger.out(m_destinations.size());
        try
        {
            COSMViewer.getSimulationOSM().repaint();
        }
        catch ( Exception l_exception )
        {
        }
    }

    /**
     * Getter for the selected Source
     */
    public ISource getSelectedSource()
    {
        return m_selectedSource;
    }

    /**
     * Method to select a Source
     */
    public void setSelectedSource( ISource p_source )
    {
        CLogger.out( CCommon.getResourceString( this, "sourceselected" ) );
        if ( m_selectedSource != null )
        {
            m_selectedSource.setColor( m_selectedSource.getGenerator() == null ? Color.BLACK : m_selectedSource.getGenerator().getColor() );
        }
        if ( p_source != null )
        {
            p_source.setColor( Color.WHITE );
        }
        m_selectedSource = p_source;
    }

    /**
     * Check if SOurce is selected
     */
    public boolean isSelectedSource( ISource p_source )
    {
        if ( p_source != null )
            return p_source.equals( m_selectedSource );

        return false;
    }

    public Vector<GeoPosition> getDestinations(){
        return this.m_destinations;
    }

}
