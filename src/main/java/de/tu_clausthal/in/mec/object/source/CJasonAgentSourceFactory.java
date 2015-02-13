/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.source;


import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgent;
import de.tu_clausthal.in.mec.object.car.ICar;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.*;


/**
 * source to create a Jason car agent
 */
public class CJasonAgentSourceFactory extends CDefaultSourceFactory
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * ASL file *
     */
    protected String m_asl = null;


    /**
     * ctor
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     * @param p_mean     mean value
     * @param p_distmean distribution mean
     */
    public CJasonAgentSourceFactory( String p_asl, GeoPosition p_position, double p_mean, double p_distmean )
    {
        super( p_position, Color.red, p_mean, p_distmean );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file must not be null" );
        m_asl = p_asl;
    }


    /**
     * ctor
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     * @param p_mean     mean value
     * @param p_distmean distribution mean
     * @param p_number   creating car number
     */
    public CJasonAgentSourceFactory( String p_asl, GeoPosition p_position, double p_mean, double p_distmean, int p_number )
    {
        super( p_position, Color.red, p_mean, p_distmean, p_number );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file must not be null" );
        m_asl = p_asl;
    }


    /**
     * ctor
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     * @param p_number   creating car number
     */
    public CJasonAgentSourceFactory( String p_asl, GeoPosition p_position, int p_number )
    {
        super( p_position, Color.red, p_number );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file must not be null" );
        m_asl = p_asl;
    }

    /**
     * ctor that sets the geo position of the source
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     */
    public CJasonAgentSourceFactory( String p_asl, GeoPosition p_position )
    {
        super( p_position, Color.red );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file must not be null" );
        m_asl = p_asl;
    }


    @Override
    public Collection<ICar> step( int p_currentstep, ILayer p_layer )
    {
        Collection<ICar> l_sources = new HashSet<>();
        if ( m_random.sample() < m_mean )
            return l_sources;

        for ( int i = 0; i < m_NumberCarsInStep; i++ )
            l_sources.add( new CCarJasonAgent( m_asl, m_position ) );

        return l_sources;
    }


    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }
}
