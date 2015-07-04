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


import de.tu_clausthal.in.mec.common.CPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * default beliefbase
 * @tparam T literal type
 */
public abstract class New_IDefaultBeliefBase<T> implements New_IBeliefBase<ILiteral<T>>
{
    /**
     * storage with data
     */
    protected final CStorage<ILiteral<T>, New_IPathMask> m_storage = new CStorage<>();


    @Override
    public void add( final ILiteral<T> p_literal )
    {
        m_storage.addElement( p_literal.getFunctor().get(), p_literal );
    }


    @Override
    public void add( final New_IPathMask p_mask )
    {
        m_storage.addMask( p_mask.getName(), p_mask );
    }


    @Override
    public final New_IPathMask getPathElement( final String p_name )
    {
        return new CMask<>( p_name, this );
    }


    /**
     * mask of a beliefbase
     * @tparam P type of the beliefbase element
     */
    private class CMask<P> implements New_IPathMask
    {
        /** name of the mask **/
        private final String m_name;
        /** reference of the beliefbase **/
        private final New_IBeliefBase<P> m_beliefbase;

        /**
         * ctor
         *
         * @param p_name name of the mask
         */
        public CMask( final String p_name, final New_IBeliefBase<P> p_beliefbase )
        {
            m_name = p_name;
            m_beliefbase = p_beliefbase;
        }

        @Override
        public CPath getFQNPath()
        {
            return null;
        }

        @Override
        public String getName()
        {
            return null;
        }
    }


    /**
     * internal storage of the data
     *
     * @tparam N element type
     * @tparam M mask type
     */
    private class CStorage<N,M>
    {
        /** map with elements **/
        private Map<String, Set<N>> m_elements = new HashMap<>();
        /** map with masks **/
        private Map<String, M> m_masks = new HashMap<>();


        /**
         * checks any element exists
         *
         * @param p_key key name
         * @return exist boolean
         */
        public boolean contains( final String p_key )
        {
            return m_elements.containsKey( p_key ) || m_masks.containsKey( p_key );
        }

        /**
         * check if an element exists
         *
         * @param p_key key name
         * @return exist boolean
         */
        public boolean containsElement( final String p_key )
        {
            final Set<N> l_elements = m_elements.get( p_key );
            if (l_elements == null)
                return false;

            return !l_elements.isEmpty();
        }


        /**
         * adds an element
         *
         * @param p_key key name
         * @param p_element element
         */
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


        /**
         * removes an element
         *
         * @param p_key key name
         * @param p_element element
         * @return boolean flag, that the element is removed
         */
        public boolean removeElement( final String p_key, final N p_element )
        {
            final Set<N> l_element = m_elements.get( p_key );

            if (l_element == null)
                return false;

            return l_element.remove( p_element );
        }


        /**
         * checks if a mask exists
         *
         * @param p_key key name
         * @return exist boolean
         */
        public boolean containsMask( String p_key )
        {
            return m_masks.containsKey( p_key );
        }


        /**
         * adds a new mask
         *
         * @param p_key key name
         * @param p_element mask element
         */
        public void addMask( final String p_key, final M p_element )
        {
            m_masks.put( p_key, p_element );
        }


        /**
         * removes a mask
         *
         * @param p_key key name
         * @return boolean flag the element is removed
         */
        public boolean removeMask( final String p_key )
        {
            return m_masks.remove( p_key ) != null;
        }


        /**
         * removes all elements by its name
         *
         * @param p_key key name
         * @return boolean flag that elements could be removed
         */
        public boolean remove( final String p_key )
        {
            return (m_masks.remove( p_key ) != null) || (m_elements.remove( p_key ) != null);
        }

    }

}
