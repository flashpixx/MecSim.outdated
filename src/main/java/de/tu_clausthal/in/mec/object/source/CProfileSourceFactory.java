/**
 @cond
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
 @endcond
 **/

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CDefaultCar;
import de.tu_clausthal.in.mec.object.car.ICar;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.*;


/**
 * source which generates cars with a profile
 */
public class CProfileSourceFactory extends IDefaultSourceFactory
{

    /**
     * waypoint color *
     */
    protected Color m_color = Color.GREEN;
    /**
     * profile with car values *
     */
    protected int[] m_profile = null;


    /**
     * ctor to create a new profile source
     *
     * @param p_position geoposition
     * @param p_profile  profile definition
     */
    public CProfileSourceFactory( GeoPosition p_position, int[] p_profile )
    {
        super( p_position, Color.GREEN );
        this.checkSetProfile( p_profile );
    }


    /**
     * sets a new profile
     *
     * @param p_profile profile definition
     */
    public void setProfile( int[] p_profile )
    {
        this.checkSetProfile( p_profile );
    }


    /**
     * check the profile definition
     *
     * @param p_profile profile
     */
    private void checkSetProfile( int[] p_profile )
    {
        if ( p_profile == null )
            throw new IllegalArgumentException( "profile need not to be null" );

        for ( int i = 0; i < p_profile.length; i++ )
            if ( p_profile[i] < 0 )
                throw new IllegalArgumentException( "profile index [" + i + "] is less than zero" );

        m_profile = p_profile;
    }

    @Override
    public Collection<ICar> step( int p_currentstep, ILayer p_layer )
    {
        Collection<ICar> l_sources = new HashSet();
        for ( int i = 0; i < m_profile[p_currentstep % m_profile.length]; i++ )
            l_sources.add( new CDefaultCar( m_position ) );
        return l_sources;
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }
}
