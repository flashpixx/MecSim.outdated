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

package de.tu_clausthal.in.mec.ui.web;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * class for name-based location
 */
public class CVirtualLocation
{

    /**
     * default name-based host *
     */
    protected final CVirtualDirectory m_defaultlocation;
    /**
     * list with additional name-based location *
     */
    protected final Set<IVirtualLocation> m_locations = new LinkedHashSet<>();


    /**
     * ctor
     *
     * @param p_defaultlocation default / fallback location
     */
    public CVirtualLocation( final CVirtualDirectory p_defaultlocation )
    {
        m_defaultlocation = p_defaultlocation;
    }


    /**
     * returns the location set
     */
    public final Set<IVirtualLocation> getLocations()
    {
        return m_locations;
    }


    /**
     * gets the name-based location matched by the URI
     *
     * @param p_uri URI
     * @return name-based location or default location
     */
    public IVirtualLocation get( final String p_uri )
    {
        for ( IVirtualLocation l_item : m_locations )
            if ( l_item.match( p_uri ) )
                return l_item;

        return m_defaultlocation;
    }

}