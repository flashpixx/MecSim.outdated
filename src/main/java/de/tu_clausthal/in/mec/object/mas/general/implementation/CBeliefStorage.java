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

package de.tu_clausthal.in.mec.object.mas.general.implementation;


import de.tu_clausthal.in.mec.object.mas.general.IBeliefStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * internal storage of the data
 *
 * @note not thread-safe
 * @tparam N element type
 * @tparam M mask type
 */
public class CBeliefStorage<N, M extends Iterable<N>> implements IBeliefStorage<N, M>
{
    /**
     * map with elements
     **/
    protected final Map<String, Set<N>> m_elements = new HashMap<>();
    /**
     * map with masks
     **/
    protected final Map<String, M> m_masks = new HashMap<>();

    @Override
    public final void addElement( final String p_key, final N p_element )
    {
        final Set<N> l_element;

        if ( m_elements.containsKey( p_key ) )
            l_element = m_elements.get( p_key );
        else
        {
            l_element = new HashSet<>();
            m_elements.put( p_key, l_element );
        }

        l_element.add( p_element );
    }

    @Override
    public void addMask( final String p_key, final M p_element )
    {
        m_masks.put( p_key, p_element );
    }

    @Override
    public void clear()
    {
        m_elements.clear();
        m_masks.clear();
    }

    @Override
    public final boolean contains( final String p_key )
    {
        return m_elements.containsKey( p_key ) || m_masks.containsKey( p_key );
    }

    @Override
    public final boolean containsElement( final String p_key )
    {
        final Set<N> l_elements = m_elements.get( p_key );
        if ( l_elements == null )
            return false;

        return !l_elements.isEmpty();
    }

    @Override
    public final boolean containsMask( String p_key )
    {
        return m_masks.containsKey( p_key );
    }

    @Override
    public final Set<N> getElement( final String p_key )
    {
        return m_elements.get( p_key );
    }

    @Override
    public final M getMask( final String p_key )
    {
        return m_masks.get( p_key );
    }

    @Override
    public final boolean isEmpty()
    {
        return m_elements.isEmpty() && m_masks.isEmpty();
    }

    @Override
    public boolean remove( final String p_key )
    {
        return ( m_masks.remove( p_key ) != null ) || ( m_elements.remove( p_key ) != null );
    }

    @Override
    public boolean removeElement( final String p_key, final N p_element )
    {
        final Set<N> l_element = m_elements.get( p_key );
        if ( l_element == null )
            return false;

        return l_element.remove( p_element );
    }

    @Override
    public boolean removeMask( final String p_key )
    {
        return m_masks.remove( p_key ) != null;
    }

    @Override
    public void update()
    {

    }

    @Override
    public final Iterator<N> iterator()
    {
        return new Iterator<N>()
        {
            private final Stack<Iterator<N>> m_stack = new Stack<Iterator<N>>()
            {{
                    for ( final Set<N> l_literals : m_elements.values() )
                        add( l_literals.iterator() );

                    for ( final M l_mask : m_masks.values() )
                        add( l_mask.iterator() );
                }};

            @Override
            public final boolean hasNext()
            {
                if ( m_stack.isEmpty() )
                    return false;

                if ( m_stack.peek().hasNext() )
                    return true;

                m_stack.pop();
                return this.hasNext();
            }

            @Override
            public final N next()
            {
                return m_stack.peek().next();
            }
        };
    }

}
