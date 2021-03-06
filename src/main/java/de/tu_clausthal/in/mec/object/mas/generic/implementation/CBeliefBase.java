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
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.generic.ILiteral;
import de.tu_clausthal.in.mec.object.mas.generic.IStorage;

import java.util.Iterator;


/**
 * default beliefbase
 *
 * @tparam T literal type
 */
public class CBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * storage with data
     */
    protected final IStorage<ILiteral<T>, IBeliefBaseMask<T>> m_storage;


    /**
     * ctor - creates an root beliefbase
     */
    public CBeliefBase()
    {
        this( new CBeliefStorage<>() );
    }

    /**
     * ctor
     *
     * @param p_storage storage
     */
    public CBeliefBase( final IStorage<ILiteral<T>, IBeliefBaseMask<T>> p_storage )
    {
        if ( p_storage == null )
            throw new IllegalArgumentException( CCommon.getResourceString( CBeliefBase.class, "storageempty" ) );
        m_storage = p_storage;
    }

    @Override
    public final int hashCode()
    {
        return 23 * m_storage.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final void add( final ILiteral<T> p_literal )
    {
        m_storage.addMultiElement( p_literal.getFunctor().get(), p_literal );
    }

    @Override
    public final IBeliefBaseMask<T> add( final IBeliefBaseMask<T> p_mask )
    {
        m_storage.addSingleElement( p_mask.getName(), p_mask );
        return p_mask;
    }

    @Override
    public final void clear()
    {
        for ( final Iterator<IBeliefBaseMask<T>> l_iterator = m_storage.iteratorSingleElement(); l_iterator.hasNext(); )
            l_iterator.next().clear();
        m_storage.clear();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <E extends IBeliefBaseMask<T>> E createMask( final String p_name )
    {
        return (E) new CMask<T>( p_name, this );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final <L extends IStorage<ILiteral<T>, IBeliefBaseMask<T>>> L getStorage()
    {
        return (L) m_storage;
    }

    @Override
    public final boolean isEmpty()
    {
        return m_storage.isEmpty();
    }

    @Override
    public final boolean remove( final IBeliefBaseMask<T> p_mask )
    {
        return m_storage.removeSingleElement( p_mask.getName() );
    }

    @Override
    public final boolean remove( final ILiteral<T> p_literal )
    {
        return m_storage.removeMultiElement( p_literal.getFunctor().get(), p_literal );
    }

    @Override
    public boolean remove( final String p_name )
    {
        return m_storage.remove( p_name );
    }

    @Override
    public final void update()
    {
        m_storage.update();

        // iterate over all masks and call update (cascading)
        for ( final Iterator<IBeliefBaseMask<T>> l_iterator = m_storage.iteratorSingleElement(); l_iterator.hasNext(); )
            l_iterator.next().update();
    }

    @Override
    public int sizeMask()
    {
        int l_sum = m_storage.sizeSingleElement();
        for ( final Iterator<IBeliefBaseMask<T>> l_iterator = m_storage.iteratorSingleElement(); l_iterator.hasNext(); )
            l_sum += l_iterator.next().sizeMask();

        return l_sum;
    }

    @Override
    public int sizeLiteral()
    {
        int l_sum = m_storage.sizeMultiElement();
        for ( final Iterator<IBeliefBaseMask<T>> l_iterator = m_storage.iteratorSingleElement(); l_iterator.hasNext(); )
            l_sum += l_iterator.next().sizeLiteral();

        return l_sum;
    }

    @Override
    public int size()
    {
        return this.sizeMask() + this.sizeLiteral();
    }

    @Override
    public Iterator<ILiteral<T>> iteratorLiteral()
    {
        return m_storage.iteratorMultiElement();
    }

    @Override
    public Iterator<IBeliefBaseMask<T>> iteratorBeliefBaseMask()
    {
        return m_storage.iteratorSingleElement();
    }

}
