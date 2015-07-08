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
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefStorage;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * default beliefbase
 *
 * @tparam T literal type
 */
public class CBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * reference to the parent
     */
    private final IBeliefBaseMask<T> m_parent;
    /**
     * storage with data
     */
    protected final IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>> m_storage;


    /**
     * ctor - creates an root beliefbase
     */
    public CBeliefBase()
    {
        this( null, new CBeliefStorage<>() );
    }

    /**
     * ctor
     *
     * @param p_parent parent element
     */
    public CBeliefBase( final IBeliefBaseMask<T> p_parent )
    {
        this( p_parent, new CBeliefStorage<>() );
    }

    /**
     * ctor
     *
     * @param p_storage storage
     */
    public CBeliefBase( final IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>> p_storage )
    {
        this( null, p_storage );
    }

    /**
     * ctor - creates a beliefbase and sets the parent
     *
     * @param p_parent
     * @param p_storage storage
     */
    public CBeliefBase( final IBeliefBaseMask<T> p_parent, final IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>> p_storage )
    {
        m_storage = p_storage;
        m_parent = p_parent;
    }


    @Override
    public void add( final ILiteral<T> p_literal )
    {
        m_storage.addElement( p_literal.getFunctor().get(), p_literal );
    }

    @Override
    public Iterator<ILiteral<T>> iterator()
    {
        return m_storage.iterator();
    }

    /**
     * mask of a beliefbase
     *
     * @tparam P type of the beliefbase element
     */
    private static class CMask<P> implements IBeliefBaseMask<P>
    {
        /**
         * mask name
         */
        private final String m_name;
        /**
         * parent name
         */
        private final IBeliefBaseMask<P> m_parent;
        /**
         * reference to the beliefbase context
         */
        private final IBeliefBase<P> m_self;


        /**
         * private ctpr
         *
         * @param p_name name of the mask
         * @param p_parent reference to the parent mask
         * @param p_self reference to the beliefbase context
         */
        private CMask( final String p_name, final IBeliefBaseMask<P> p_parent, final IBeliefBase<P> p_self )
        {
            m_name = p_name;
            m_parent = p_parent;
            m_self = p_self;
        }

        /**
         * static method to generate FQN path
         *
         * @param p_mask curretn path
         * @param p_path return path
         * @tparam Q type of the beliefbase elements
         */
        private static <Q> void getFQNPath( final IBeliefBaseMask<Q> p_mask, final CPath p_path )
        {
            if ( p_mask == null )
                return;

            p_path.pushfront( p_mask.getName() );
            getFQNPath( p_mask.getParent(), p_path );
        }


        @Override
        public IBeliefBaseMask<P> clone( final IBeliefBaseMask<P> p_parent )
        {
            return new CMask<>( m_name, p_parent, m_self );
        }

        @Override
        public CPath getFQNPath()
        {
            final CPath l_path = new CPath();
            getFQNPath( this, l_path );
            return l_path;
        }

        @Override
        public String getName()
        {
            return m_name;
        }

        @Override
        public IBeliefBaseMask<P> getParent()
        {
            return m_parent;
        }

        @Override
        public Set<ILiteral<P>> get( final CPath p_path )
        {
            return descend( p_path, this ).get();
        }

        /**
         * returns a mask on the recursive descend
         *
         * @param p_path path
         * @param p_root start / root node
         * @tparam Q literal type
         * @return mask
         */
        private static <Q> IBeliefBaseMask<Q> descend( final CPath p_path, final IBeliefBaseMask<Q> p_root )
        {
            if ((p_path == null) || (p_path.isEmpty()))
                return p_root;

            final IBeliefBaseMask<Q> l_mask = p_root.getStorage().getMask( p_path.get( 0 ) );
            if (l_mask == null)
                throw new IllegalStateException( CCommon.getResourceString( CMask.class, "pathelementnotfound", p_path.get(0), p_path ) );

            return l_mask;
        }


        @Override
        public Set<ILiteral<P>> get()
        {
             return new HashSet<ILiteral<P>>()
            {{
            for( final ILiteral<P> l_item : m_self )
                add( l_item );
                }};
        }

        @Override
        public IBeliefBaseMask<P> createMask( final String p_name )
        {
            return m_self.createMask( p_name );
        }

        @Override
        public void add( final ILiteral<P> p_literal )
        {
            m_self.add( p_literal );
        }

        @Override
        public Iterator<ILiteral<P>> iterator()
        {
            final CPath l_path = this.getFQNPath();
            final Set<ILiteral<P>> l_items = new HashSet<>();
            for ( final ILiteral<P> l_literal : m_self )
                l_items.add( l_literal.clone( l_path ) );

            return l_items.iterator();
        }

        @Override
        public void remove( final ILiteral<P> p_literal )
        {
            m_self.remove( p_literal );
        }

        @Override
        public void add( final IBeliefBaseMask<P> p_mask )
        {
            m_self.add( p_mask );
        }

        @Override
        public void remove( final IBeliefBaseMask<P> p_mask )
        {
            m_self.remove( p_mask );
        }

        @Override
        public boolean isEmpty()
        {
            return m_self.isEmpty();
        }

        @Override
        public void update()
        {
            m_self.update();
        }

        @Override
        public <L extends IBeliefStorage<ILiteral<P>, IBeliefBaseMask<P>>> L getStorage()
        {
            return m_self.getStorage();
        }

        @Override
        public void clear()
        {
            m_self.clear();
        }

    }    @Override
    @SuppressWarnings( "unchecked" )
    public <L extends IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>>> L getStorage()
    {
        return (L) m_storage;
    }




    @Override
    public void add( final IBeliefBaseMask<T> p_mask )
    {
        m_storage.addMask( p_mask.getName(), p_mask.clone( m_parent ) );
    }

    @Override
    public void remove( final ILiteral<T> p_literal )
    {
        m_storage.removeElement( p_literal.getFunctor().get(), p_literal );
    }

    @Override
    public void remove( final IBeliefBaseMask<T> p_mask )
    {
        m_storage.removeMask( p_mask.getName() );
    }

    @Override
    public final IBeliefBaseMask<T> createMask( final String p_name )
    {
        return new CMask<T>( p_name, m_parent, this );
    }


    @Override
    public boolean isEmpty()
    {
        return m_storage.isEmpty();
    }


    @Override
    public void update()
    {
        m_storage.update();
    }

    @Override
    public void clear()
    {
        m_storage.clear();
    }

}
