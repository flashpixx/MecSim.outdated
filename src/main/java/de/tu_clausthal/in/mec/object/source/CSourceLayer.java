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
import de.tu_clausthal.in.mec.object.source.factory.CDefaultAgentCarFactory;
import de.tu_clausthal.in.mec.object.source.factory.CDefaultCarFactory;
import de.tu_clausthal.in.mec.object.source.generator.CTimeUniformDistribution;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CAtomTarget;
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
     * indicate the car type
     */
    private static ECarType m_carType = ECarType.DEFAULTCAR;
    /**
     * list of all Atom Targets
     */
    private final Vector<CAtomTarget> m_sourceTargets = new Vector<>();
    /**
     * variable which defines the asl program
     */
    private String m_selectedASL = null;


    @Override
    public final int getCalculationIndex()
    {
        return 2;
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

        //paint sources
        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        p_graphic.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( ISource l_source : this )
            l_source.paint( p_graphic, p_viewer, p_width, p_height );

        //paint targets
        for ( CAtomTarget l_target : m_sourceTargets )
            l_target.paint( p_graphic, p_viewer, p_width, p_height );

    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * creates a new source
     *
     * @param p_geoposition position of the Source
     * todo replace default generator
     */
    public final void createSource( final GeoPosition p_geoposition )
    {
        if ( p_geoposition == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgeoposition" ) );

        switch ( this.m_carType )
        {
            case DEFAULTCAR:
                this.add( new CCarSource( p_geoposition, new CTimeUniformDistribution( 1, 0, 10 ), new CDefaultCarFactory( p_geoposition ) ) );
                break;

            case DEFAULTAGENTCAR:
                if ( this.m_selectedASL == null )
                    break; //If no ASL is selected an empty source will be created
                this.add(
                        new CCarSource(
                                p_geoposition, new CTimeUniformDistribution( 1, 0, 10 ), new CDefaultAgentCarFactory(
                                p_geoposition, m_selectedASL
                        )
                        )
                );
                break;

            default:
                this.add( new CCarSource( p_geoposition, new CTimeUniformDistribution( 1, 0, 10 ), new CDefaultCarFactory( p_geoposition ) ) );
        }
    }

    /**
     * removes a source
     *
     * @param p_source source which should be removed
     */
    public final void removeSource( final ISource p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidsource" ) );

        p_source.release();
        this.remove( p_source );
    }

    /**
     * creates a new atom target
     *
     * @param p_geoposition position of the atom target
     */
    public final void createTarget( final GeoPosition p_geoposition )
    {
        if ( p_geoposition == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgeoposition" ) );

        final CAtomTarget l_newtarget = new CAtomTarget( p_geoposition );
        this.m_sourceTargets.add( l_newtarget );

        this.repaintOSM();
    }

    /**
     * after a target was created, OSM need to be repainted
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
     * removes an atom target from the source layer and all complex targets
     *
     * @param p_target Atom Target which should be removed
     */
    public final void removeTarget( final CAtomTarget p_target )
    {
        if ( p_target == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidtarget" ) );

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
    public final Vector<CAtomTarget> getTargets()
    {
        return this.m_sourceTargets;
    }

    /**
     * method to set a new car type
     *
     * @param p_data key:cartype, value:defaulcar/defaultagentcar
     */
    public final void web_static_setcartype( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "cartype" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidcartype" ) );

        String l_cartype = (String) p_data.get( "cartype" );

        if ( l_cartype.equals( "defaultcar" ) )
            this.m_carType = ECarType.DEFAULTCAR;

        if ( l_cartype.equals( "defaultagentcar" ) )
            this.m_carType = ECarType.DEFAULTAGENTCAR;
    }

    /**
     * method to set a new asl file
     *
     * @param p_data key:aslname, value:name of the asl file
     */
    public final void web_static_setasl( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "aslname" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidaslname" ) );

        this.m_selectedASL = (String) p_data.get( "aslname" );
    }

    /**
     * enum to indicate the car type
     */
    public enum ECarType
    {
        DEFAULTCAR,
        DEFAULTAGENTCAR
    }

}
