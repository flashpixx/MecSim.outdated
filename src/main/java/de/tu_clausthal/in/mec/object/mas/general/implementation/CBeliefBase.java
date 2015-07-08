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
     * storage with data
     */
    protected final IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>> m_storage;


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
    public CBeliefBase( final IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>> p_storage )
    {
        m_storage = p_storage;
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
        private final IBeliefBase<P> m_beliefbase;

        /**
         * private ctpr
         *
         * @param p_name name of the mask
         * @param p_beliefbase reference to the beliefbase context
         */
        private CMask( final String p_name, final IBeliefBase<P> p_beliefbase )
        {
            this( p_name, p_beliefbase, null );
        }

        /**
         * private ctpr
         *
         * @param p_name name of the mask
         * @param p_beliefbase reference to the beliefbase context
         * @param p_parent reference to the parent mask
         */
        private CMask( final String p_name, final IBeliefBase<P> p_beliefbase, final IBeliefBaseMask<P> p_parent )
        {
            m_name = p_name;
            m_beliefbase = p_beliefbase;
            m_parent = p_parent;
        }

        @Override
        public void add( final CPath p_path, final ILiteral<P> p_literal )
        {
            this.add( p_path, p_literal, null );
        }

        @Override
        public void add( final CPath p_path, final IBeliefBaseMask<P> p_mask )
        {
            this.add( p_path, p_mask, null );
        }

        @Override
        public void add( final CPath p_path, final IBeliefBaseMask<P> p_mask, final IBeliefBaseMask.IGenerator<P> p_generator
        )
        {
            walk( p_path, this, p_generator ).add( p_mask );
        }

        @Override
        public void add( final CPath p_path, final ILiteral<P> p_literal, final IBeliefBaseMask.IGenerator<P> p_generator
        )
        {
            walk( p_path, this, p_generator ).add( p_literal );
        }

        @Override
        public IBeliefBaseMask<P> clone( final IBeliefBaseMask<P> p_parent )
        {
            return new CMask<>( m_name, m_beliefbase, p_parent );
        }

        @Override
        public Set<ILiteral<P>> get( final CPath p_path )
        {
            return walk( p_path, this, null ).get();
        }

        @Override
        public Set<ILiteral<P>> get()
        {
            return new HashSet<ILiteral<P>>()
            {{
                    for ( final ILiteral<P> l_item : m_beliefbase )
                        add( l_item );
                }};
        }

        @Override
        public CPath getFQNPath()
        {
            return getFQNPath( this, new CPath() );
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
        public boolean hasParent()
        {
            return m_parent != null;
        }

        @Override
        public int hashCode()
        {
            return 47 * m_name.hashCode() + 49 * m_beliefbase.hashCode();
        }

        @Override
        public void add( final ILiteral<P> p_literal )
        {
            m_beliefbase.add( p_literal );
        }

        @Override
        public Iterator<ILiteral<P>> iterator()
        {
            final CPath l_path = this.getFQNPath();
            final Set<ILiteral<P>> l_items = new HashSet<>();
            for ( final ILiteral<P> l_literal : m_beliefbase )
                l_items.add( l_literal.clone( l_path ) );

            return l_items.iterator();
        }

        /**
         * static method to generate FQN path
         *
         * @param p_mask curretn path
         * @param p_path current path
         * @return path
         * @tparam Q type of the beliefbase elements
         */
        private static <Q> CPath getFQNPath( final IBeliefBaseMask<Q> p_mask, final CPath p_path )
        {
            p_path.pushfront( p_mask.getName() );
            return !p_mask.hasParent() ? p_path : getFQNPath( p_mask.getParent(), p_path );
        }

        /**
         * returns a mask on the recursive descend
         *
         * @param p_path path
         * @param p_root start / root node
         * @param p_generator generator object for new masks
         * @return mask
         *
         * @note a path can contains ".." to use the parent object
         * @tparam Q literal type
         */
        private static <Q> IBeliefBaseMask<Q> walk( final CPath p_path, final IBeliefBaseMask<Q> p_root, final IBeliefBaseMask.IGenerator<Q> p_generator )
        {
            if ( ( p_path == null ) || ( p_path.isEmpty() ) )
                return p_root;

            // get the next mask (on ".." the parent is returned otherwise the child is used)
            IBeliefBaseMask<Q> l_mask = "..".equals( p_path.get( 0 ) ) ? p_root.getParent() : p_root.getStorage().getMask( p_path.get( 0 ) );

            // if a generator is exists and the mask is null, a new mask is created and added to the current
            if ( ( l_mask == null ) && ( p_generator != null ) && ( !( "..".equals( p_path.get( 0 ) ) ) ) )
            {
                l_mask = p_generator.create( p_path.get( 0 ) );
                p_root.add( l_mask );
            }

            // if mask null an exception is thrown
            if ( l_mask == null )
                throw new IllegalArgumentException( CCommon.getResourceString( CMask.class, "pathelementnotfound", p_path.get( 0 ), p_path ) );

            // recursive descend
            return walk( p_path.getSubPath( 1 ), l_mask, p_generator );
        }

        @Override
        public void add( final IBeliefBaseMask<P> p_mask )
        {
            m_beliefbase.add( p_mask.clone( this ) );
        }


        @Override
        public IBeliefBaseMask<P> createMask( final String p_name )
        {
            return m_beliefbase.createMask( p_name );
        }


        @Override
        public void remove( final ILiteral<P> p_literal )
        {
            m_beliefbase.remove( p_literal );
        }

        @Override
        public void remove( final IBeliefBaseMask<P> p_mask )
        {
            m_beliefbase.remove( p_mask );
        }

        @Override
        public boolean isEmpty()
        {
            return m_beliefbase.isEmpty();
        }


        @Override
        public void update()
        {
            m_beliefbase.update();
        }

        @Override
        public <L extends IBeliefStorage<ILiteral<P>, IBeliefBaseMask<P>>> L getStorage()
        {
            return m_beliefbase.getStorage();
        }

        @Override
        public void clear()
        {
            m_beliefbase.clear();
        }

        @Override
        public String toString()
        {
            return "{ name : " + m_name + ", parent " + ( m_parent == null ? "null" : m_parent.hashCode() ) + " # " + this.hashCode() + ", fqn : " +
                   this.getFQNPath() + ", storage : " + m_beliefbase
                           .getStorage() + " }";
        }
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

    @Override
    @SuppressWarnings( "unchecked" )
    public <L extends IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>>> L getStorage()
    {
        return (L) m_storage;
    }


    @Override
    public void add( final IBeliefBaseMask<T> p_mask )
    {
        m_storage.addMask( p_mask.getName(), p_mask );
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
        return new CMask<T>( p_name, this );
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

}
