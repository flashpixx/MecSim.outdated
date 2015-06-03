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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * class for beliefbase
 */
public class CBeliefBase implements IBeliefBase
{
    @Override
    public int size()
    {
        return size();
    }

    @Override
    public boolean isEmpty()
    {
        return isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return contains( o );
    }

    @Override
    public Iterator<ILiteral> iterator()
    {
        return iterator();
    }

    @Override
    public Object[] toArray()
    {
        return toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return toArray( a );
    }

    @Override
    public boolean add(ILiteral p_literal)
    {
        return add( p_literal );
    }

    @Override
    public boolean remove(Object o)
    {
        return remove( o );
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return containsAll( c );
    }

    @Override
    public boolean addAll(Collection<? extends ILiteral> c)
    {
        return addAll( c );
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return retainAll( c );
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return removeAll( c );
    }

    @Override
    public void clear()
    {
        clear();
    }
}
