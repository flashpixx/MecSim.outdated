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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.source.generator.CDefaultCarGenerator;
import de.tu_clausthal.in.mec.object.source.generator.CJasonCarGenerator;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CAtomTarget;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CComplexTarget;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Random;
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
     * List of all Atom Targets
     */
    private Vector<CAtomTarget> m_sourceTargets = new Vector<>();


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
     * Override paint Method from Multilayer to paint Targets
     * It is possible to save Targets in an own Layer, but this might be not necessary because Targets do not need
     * to be triggered and so on
     *
     * @param p_graphic
     * @param p_viewer
     * @param p_width
     * @param p_height
     */
    @Override
    public void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_visible )
            return;

        //Paint Sources
        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        p_graphic.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( ISource l_source : this )
            l_source.paint( p_graphic, p_viewer, p_width, p_height );

        //Paint Targets
        for ( CAtomTarget l_target : m_sourceTargets )
            l_target.paint( p_graphic, p_viewer, p_width, p_height );

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
     * creates a new source
     *
     * @param p_geoposition      position of the Source
     * @param p_defaultGenerator default Generator for this Source
     * @param p_aslname          ASL Programm for the Generator
     */
    public void createSource( final GeoPosition p_geoposition, final String p_defaultGenerator, final String p_aslname )
    {
        CLogger.out( CCommon.getResourceString( this, "sourcecreated" ) );
        ISource l_newSource = new CSource( p_geoposition );
        this.add( l_newSource );

        //Set Default Generator (Selected Generator)
        if ( ( p_defaultGenerator == null ) || ( p_defaultGenerator.contains( "Jason" ) && p_aslname == null ) )
            return;
        this.setGenerator( l_newSource, p_defaultGenerator, p_aslname );

        //Set Default Target (CChoice Target)
        CComplexTarget l_complexTarget = new CComplexTarget();
        l_newSource.setComplexTarget( l_complexTarget );
    }

    /**
     * removes a source
     *
     * @param p_source source which should be removed
     */
    public void removeSource( final ISource p_source )
    {
        CLogger.out( CCommon.getResourceString( this, "sourceremoved" ) );
        if ( this.isSelectedSource( p_source ) )
            m_selectedSource = null;

        p_source.release();
        this.remove( p_source );
    }

    /**
     * return the selected source
     *
     * @return selected Source or null (if no source is selected)
     */
    public ISource getSelectedSource()
    {
        return m_selectedSource;
    }

    /**
     * set the selected Source
     *
     * @param p_source source which should be selected
     */
    public void setSelectedSource( final ISource p_source )
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
     * checker if a source is selected
     *
     * @param p_source source which should be checked
     * @return true if the source is selected otherwise false
     */
    public boolean isSelectedSource( final ISource p_source )
    {
        if ( p_source != null )
            return p_source.equals( m_selectedSource );

        return false;
    }

    /**
     * set a generator object for a source
     *
     * @param p_source            source where the generator should be placed
     * @param p_selectedgenerator new generator
     * @param p_aslname           ASL Programm for the Generator
     */
    public void setGenerator( final ISource p_source, final String p_selectedgenerator, final String p_aslname )
    {
        CLogger.out( CCommon.getResourceString( this, "generatorcreated" ) );

        if ( p_selectedgenerator.equals( "Default" ) )
            p_source.setGenerator( new CDefaultCarGenerator( p_source.getPosition() ) );
        if ( p_selectedgenerator.equals( "Jason Agent" ) )
            p_source.setGenerator( new CJasonCarGenerator( p_source.getPosition(), p_aslname ) );
    }

    /**
     * removes the generator object for a source
     *
     * @param p_source source where the generator should be removed
     */
    public void removeGenerator( final ISource p_source )
    {
        CLogger.out( CCommon.getResourceString( this, "generatorremoved" ) );
        p_source.removeGenerator();
    }

    /**
     * creates a new Atom Target
     * if a Source is selected, the Atom Target will be passed
     * in the Complex Target of this Source
     *
     * @param p_geoposition position of the Atom Target
     */
    public void createTarget( final GeoPosition p_geoposition )
    {
        CLogger.out( CCommon.getResourceString( this, "targetcreated" ) );

        if ( p_geoposition == null )
            return;

        CAtomTarget l_newTarget = new CAtomTarget( p_geoposition );
        this.m_sourceTargets.add( l_newTarget );

        //If a Source is Selected the Target also should be passed in the ComplexTarget of this Source
        Random l_random = new Random();
        if ( this.m_selectedSource != null )
            this.m_selectedSource.getComplexTarget().addTarget( l_newTarget, l_random.nextDouble() );

        this.repaintOSM();
    }

    /**
     * removes an Atom Target from the sourcelayer and all Complex Targets
     *
     * @param p_target Atom Target which should be removed
     */
    public void removeTarget( final CAtomTarget p_target )
    {
        CLogger.out( CCommon.getResourceString( this, "targetremoved" ) );

        if ( p_target == null )
            return;

        this.m_sourceTargets.remove( p_target );

        for ( ISource l_source : this )
            l_source.getComplexTarget().removeTarget( p_target );

        this.repaintOSM();
    }

    /**
     * return the full list of Atom Targets
     *
     * @return all Atom Targets
     */
    public Vector<CAtomTarget> getTargets()
    {
        return this.m_sourceTargets;
    }

    /**
     * after a target was created, OSM need to be repainted
     *
     * @bug must be fixed
     */
    public void repaintOSM()
    {
        /*
        try
        {
            COSMViewer.getSimulationOSM().repaint();
        }
        catch ( Exception l_exception )
        {
        }
        m_selectedSource = p_source;
        */
    }

}
