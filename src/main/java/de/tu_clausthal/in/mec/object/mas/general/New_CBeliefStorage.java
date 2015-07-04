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

package de.tu_clausthal.in.mec.object.mas.general;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * internal storage of the data
 *
 * @tparam N element type
 * @tparam M mask type
 */
public class New_CBeliefStorage<N,M> implements New_IBeliefStorage<N,M>
{
    /** map with elements **/
    private Map<String, Set<N>> m_elements = new HashMap<>();
    /** map with masks **/
    private Map<String, M> m_masks = new HashMap<>();


    @Override
    public boolean contains( final String p_key )
    {
        return m_elements.containsKey( p_key ) || m_masks.containsKey( p_key );
    }


    @Override
    public boolean containsElement( final String p_key )
    {
        final Set<N> l_elements = m_elements.get( p_key );
        if (l_elements == null)
            return false;

        return !l_elements.isEmpty();
    }


    @Override
    public void addElement( final String p_key, final N p_element )
    {
        final Set<N> l_element;

        if (m_elements.containsKey( p_key ))
            l_element = m_elements.get( p_key );
        else
        {
            l_element = new HashSet<>();
            m_elements.put( p_key, l_element );
        }

        l_element.add( p_element );
    }


    @Override
    public boolean removeElement( final String p_key, final N p_element )
    {
        final Set<N> l_element = m_elements.get( p_key );
        if (l_element == null)
            return false;

        return l_element.remove( p_element );
    }


    @Override
    public boolean containsMask( String p_key )
    {
        return m_masks.containsKey( p_key );
    }


    @Override
    public void addMask( final String p_key, final M p_element )
    {
        m_masks.put( p_key, p_element );
    }


    @Override
    public boolean removeMask( final String p_key )
    {
        return m_masks.remove( p_key ) != null;
    }


    @Override
    public boolean remove( final String p_key )
    {
        return (m_masks.remove( p_key ) != null) || (m_elements.remove( p_key ) != null);
    }

}
