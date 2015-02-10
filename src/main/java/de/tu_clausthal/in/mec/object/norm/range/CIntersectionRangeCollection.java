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

package de.tu_clausthal.in.mec.object.norm.range;

import java.util.*;


/**
 * disjoint range
 */
public class CIntersectionRangeCollection<T> implements IRangeCollection<T>
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * list of ranges
     */
    private Set<IRange<T>> m_ranges = new HashSet<>();


    @Override
    public boolean check( T p_object )
    {
        if ( m_ranges.isEmpty() )
            return false;

        for ( IRange<T> l_item : m_ranges )
            if ( !l_item.check( p_object ) )
                return false;

        return true;
    }

    @Override
    public void release()
    {
        for ( IRange<T> l_item : m_ranges )
            l_item.release();
    }

    @Override
    public int size()
    {
        return m_ranges.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_ranges.isEmpty();
    }

    @Override
    public boolean contains( Object o )
    {
        return m_ranges.contains( o );
    }

    @Override
    public Iterator<IRange<T>> iterator()
    {
        return m_ranges.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return m_ranges.toArray();
    }

    @Override
    public <T> T[] toArray( T[] a )
    {
        return m_ranges.toArray( a );
    }

    @Override
    public boolean add( IRange<T> iRange )
    {
        return m_ranges.add( iRange );
    }

    @Override
    public boolean remove( Object o )
    {
        return m_ranges.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c )
    {
        return m_ranges.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends IRange<T>> c )
    {
        return m_ranges.addAll( c );
    }

    @Override
    public boolean removeAll( Collection<?> c )
    {
        return m_ranges.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return m_ranges.retainAll( c );
    }

    @Override
    public void clear()
    {
        m_ranges.clear();
    }
}
