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

package de.tu_clausthal.in.mec.object.mas.generic.implementation;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBase;

import java.util.HashMap;
import java.util.Map;


/**
 * storage of beliefbases to address different beliefbases with a name
 */
public final class CBeliefBaseStorage<T>
{
    /**
     * map with case-insensitive name and a beliefbase
     **/
    private final Map<String, IBeliefBase<T>> m_beliefbases = new HashMap<>();


    /**
     * adds a new beliefbase
     *
     * @param p_name key name
     * @param p_base beliefbase
     */
    public final void add( final String p_name, final IBeliefBase<T> p_base )
    {
        if ( m_beliefbases.containsKey( p_name.toLowerCase() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "exists", p_name.toLowerCase() ) );

        m_beliefbases.put( p_name.toLowerCase(), p_base );
    }

    /**
     * returns a beliefbase
     *
     * @param p_name key name
     * @return null or beliefbase
     */
    public final IBeliefBase<T> get( final String p_name )
    {
        return m_beliefbases.get( p_name.toLowerCase() );
    }

    /**
     * removes a beliefbase
     *
     * @param p_name key name
     */
    public final void remove( final String p_name )
    {
        m_beliefbases.remove( p_name.toLowerCase() );
    }

}
