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


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.IStorage;

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
    public int hashCode()
    {
        return 23 * m_storage.hashCode();
    }

    @Override
    public boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public void add( final ILiteral<T> p_literal )
    {
        m_storage.addMultiElement( p_literal.getFunctor().get(), p_literal );
    }

    @Override
    public IBeliefBaseMask<T> add( final IBeliefBaseMask<T> p_mask )
    {
        m_storage.addSingleElement( p_mask.getName(), p_mask );
        return p_mask;
    }

    @Override
    public void clear()
    {
        m_storage.clear();
    }

    @Override
    public final IBeliefBaseMask<T> createMask( final String p_name )
    {
        return new CMask<T>( p_name, this );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <L extends IStorage<ILiteral<T>, IBeliefBaseMask<T>>> L getStorage()
    {
        return (L) m_storage;
    }

    @Override
    public boolean isEmpty()
    {
        return m_storage.isEmpty();
    }

    @Override
    public void remove( final IBeliefBaseMask<T> p_mask )
    {
        m_storage.removeSingleElement( p_mask.getName() );
    }

    @Override
    public void remove( final ILiteral<T> p_literal )
    {
        m_storage.removeMultiElement( p_literal.getFunctor().get(), p_literal );
    }

    @Override
    public void update()
    {
        m_storage.update();
    }

    @Override
    public Iterator<ILiteral<T>> iterator()
    {
        return m_storage.iterator();
    }





}
