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

package de.tu_clausthal.in.mec.object;

import de.tu_clausthal.in.mec.simulation.ISteppable;
import de.tu_clausthal.in.mec.simulation.IVoidSteppable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * layer for any calculation without visibility
 */
public abstract class IEvaluateLayer<T extends ISteppable> implements ILayer, IVoidSteppable, Collection<T>
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;

    /**
     * list of data items
     */
    protected final Queue<T> m_data = new ConcurrentLinkedQueue<>();
    /**
     * flag for activity
     */
    protected boolean m_active = true;

    @Override
    public final boolean isActive()
    {
        return m_active;
    }

    @Override
    public final void setActive( final boolean p_active )
    {
        m_active = p_active;
    }

    @Override
    public int getCalculationIndex()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer )
    {
    }

    @Override
    public final int size()
    {
        return m_data.size();
    }

    @Override
    public final boolean isEmpty()
    {
        return m_data.isEmpty();
    }

    @Override
    public final boolean contains( final Object p_object )
    {
        return m_data.contains( p_object );
    }

    @Override
    public final Iterator<T> iterator()
    {
        return m_data.iterator();
    }

    @Override
    public final Object[] toArray()
    {
        return m_data.toArray();
    }

    @Override
    public final <S> S[] toArray( final S[] p_value )
    {
        return m_data.toArray( p_value );
    }

    @Override
    public final boolean add( final T p_value )
    {
        return m_data.add( p_value );
    }

    @Override
    public final boolean remove( final Object p_object )
    {
        return m_data.remove( p_object );
    }

    @Override
    public final boolean containsAll( final Collection<?> p_collection )
    {
        return m_data.containsAll( p_collection );
    }

    @Override
    public final boolean addAll( final Collection<? extends T> p_collection )
    {
        return m_data.addAll( p_collection );
    }

    @Override
    public final boolean removeAll( final Collection<?> p_collection )
    {
        return m_data.removeAll( p_collection );
    }

    @Override
    public final boolean retainAll( final Collection<?> p_collection )
    {
        return m_data.retainAll( p_collection );
    }

    @Override
    public final void clear()
    {
        m_data.clear();
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void release()
    {
        for ( ISteppable l_item : m_data )
            l_item.release();
    }

}
