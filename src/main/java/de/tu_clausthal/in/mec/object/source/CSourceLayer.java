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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.source.generator.CDefaultCarGenerator;
import de.tu_clausthal.in.mec.object.source.generator.CJasonCarGenerator;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CAtomTarget;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CComplexTarget;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Map;
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
     * List of all Atom Targets
     */
    private final Vector<CAtomTarget> m_sourceTargets = new Vector<>();
    /**
     * member variable to indicate the car type for new sources/generators
     */
    private static ECarType m_carType = ECarType.DEFAULTCAR;
    /**
     * variable which defines the asl programm
     */
    private String m_selectedASL = null;


    @Override
    public final int getCalculationIndex()
    {
        return 2;
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    @Override
    public final void release()
    {

    }

    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
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
     * creates a new source
     *
     * @param p_geoposition position of the Source
     */
    public final void createSource( final GeoPosition p_geoposition )
    {
        final ISource l_newsource = new CSource( p_geoposition );
        this.add( l_newsource );

        this.setGenerator( l_newsource );
        l_newsource.setComplexTarget( new CComplexTarget() );
    }

    /**
     * removes a source
     *
     * @param p_source source which should be removed
     */
    public final void removeSource( final ISource p_source )
    {
        p_source.release();
        this.remove( p_source );
    }

    /**
     * set a generator object for a source
     *
     * @param p_source source where the generator should be placed
     */
    public final void setGenerator( final ISource p_source)
    {
        switch(this.m_carType){
            case DEFAULTCAR:
                p_source.setGenerator( new CDefaultCarGenerator( p_source.getPosition() ) );
                break;

            case DEFAULTAGENTCAR:
                if(this.m_selectedASL==null)
                    break; //If no ASL is selected an empty source will be created
                p_source.setGenerator( new CJasonCarGenerator( p_source.getPosition(), this.m_selectedASL ) );
                break;

            default:
                p_source.setGenerator( new CDefaultCarGenerator( p_source.getPosition() ) );
        }
    }

    /**
     * removes the generator object for a source
     *
     * @param p_source source where the generator should be removed
     */
    public final void removeGenerator( final ISource p_source )
    {
        p_source.removeGenerator();
    }

    /**
     * creates a new Atom Target
     * if a Source is selected, the Atom Target will be passed
     * in the Complex Target of this Source
     *
     * @param p_geoposition position of the Atom Target
     */
    public final void createTarget( final GeoPosition p_geoposition )
    {
        if ( p_geoposition == null )
            return;

        final CAtomTarget l_newtarget = new CAtomTarget( p_geoposition );
        this.m_sourceTargets.add( l_newtarget );

        this.repaintOSM();
    }

    /**
     * removes an Atom Target from the sourcelayer and all Complex Targets
     *
     * @param p_target Atom Target which should be removed
     */
    public final void removeTarget( final CAtomTarget p_target )
    {
        if ( p_target == null )
            return;

        this.m_sourceTargets.remove( p_target );

        for ( ISource l_source : this )
            l_source.getComplexTarget().removeTarget( p_target );

        this.repaintOSM();
    }

    /**
     * after a target was created, OSM need to be repainted
     *
     * @bug must be fixed
     */
    private final void repaintOSM()
    {
        try
        {
            CSimulation.getInstance().getUIComponents().getUI().<CSwingWrapper<COSMViewer>>getTyped( "OSM" ).getComponent().repaint();
        }
        catch ( Exception l_exception )
        {
        }
    }

    /**
     * return the full list of Atom Targets
     *
     * @return all Atom Targets
     */
    public final Vector<CAtomTarget> getTargets()
    {
        return this.m_sourceTargets;
    }

    /**
     * method to set a new car type
     * @param p_data key:cartype, value:defaulcar/defaultagentcar
     */
    public void web_static_setcartype(final Map<String, Object> p_data)
    {
        if ( !p_data.containsKey( "cartype" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidcartype" ) );

        String l_cartype = (String) p_data.get( "cartype" );

        if(l_cartype.equals( "defaultcar" ))
            this.m_carType=ECarType.DEFAULTCAR;

        if(l_cartype.equals( "defaultagentcar" ))
            this.m_carType=ECarType.DEFAULTAGENTCAR;
    }

    /**
     * method to set a new asl file
     * @param p_data key:aslname, value:name of the asl file
     */
    public void web_static_setasl(final Map<String, Object> p_data)
    {
        if ( !p_data.containsKey( "aslname" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidaslname" ) );

        this.m_selectedASL= (String) p_data.get( "aslname" );
    }

    /**
     * enum to indicate the car type
     */
    public enum ECarType{
        DEFAULTCAR,
        DEFAULTAGENTCAR
    }

}
