/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

package de.tu_clausthal.in.mec.ui.web;

import fi.iki.elonen.NanoHTTPD;

import java.util.HashMap;
import java.util.Map;


/**
 * class for the global name-based location collection
 */
public class CVirtualLocation
{

    /**
     * default name-based host *
     */
    private final CVirtualDirectory m_defaultlocation;
    /**
     * list with additional name-based location *
     */
    private final Map<Integer, IVirtualLocation> m_locations = new HashMap<>();

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
     * adds a new location
     */
    public final void add( final IVirtualLocation p_location )
    {
        m_locations.put( p_location.hashCode(), p_location );
    }

    /**
     * checks if a URI is exists (exact and start match)
     *
     * @param p_session session object
     * @return boolean of existence
     */
    public final boolean contains( final NanoHTTPD.IHTTPSession p_session )
    {
        if ( m_locations.containsKey( p_session.getUri().hashCode() ) )
            return true;

        for ( final IVirtualLocation l_item : m_locations.values() )
            if ( l_item.match( p_session.getUri() ) )
                return true;

        return false;
    }

    /**
     * checks of an exact match
     *
     * @param p_session session object
     * @return boolean of exact existance
     */
    public final boolean containsexact( final NanoHTTPD.IHTTPSession p_session )
    {
        return m_locations.containsKey( p_session.getUri().hashCode() );
    }

    /**
     * gets the name-based location matched by the URI
     *
     * @param p_session HTTP session
     * @return name-based location or default location or null
     */
    public final IVirtualLocation get( final NanoHTTPD.IHTTPSession p_session )
    {
        // try to find an exact match
        if ( m_locations.containsKey( p_session.getUri().hashCode() ) )
            return m_locations.get( p_session.getUri().hashCode() );


        // otherwise check each match
        for ( final IVirtualLocation l_item : m_locations.values() )
            if ( l_item.match( p_session.getUri() ) )
                return l_item;

        return m_defaultlocation;
    }

}
