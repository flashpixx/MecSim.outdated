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


/**
 * default beliefbase
 * @tparam T literal type
 */
public abstract class New_IDefaultBeliefBase<T> implements New_IBeliefBase<ILiteral<T>>
{
    /**
     * storage with data
     */
    protected final New_CBeliefStorage<ILiteral<T>, New_IPathMask> m_storage = new New_CBeliefStorage<>();

    @Override
    public final New_IPathMask getPathElement( final String p_name )
    {
        return new CMask<>( p_name, this );
    }

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

    /**
     * mask of a beliefbase
     * @tparam P type of the beliefbase element
     */
    private class CMask<P> implements New_IPathMask, Cloneable
    {
        /** name of the mask **/
        private final String m_name;
        /** reference of the beliefbase **/
        private final New_IBeliefBase<P> m_beliefbase;
        /** reference to the parent **/
        private final New_IPathMask m_parent;


        /**
         * ctor
         *
         * @param p_name       name of the mask
         * @param p_beliefbase reference to a beliefbase
         */
        public CMask( final String p_name, final New_IBeliefBase<P> p_beliefbase )
        {
            this( null, p_name, p_beliefbase );
        }

        /**
         * private ctor
         *
         * @param p_parent parent of the mask
         * @param p_name name of the mask
         * @param p_beliefbase reference to a beliefbase
         */
        private CMask( final New_IPathMask p_parent, final String p_name, final New_IBeliefBase<P> p_beliefbase )
        {
            m_name = p_name;
            m_beliefbase = p_beliefbase;
            m_parent = p_parent;
        }

        @Override
        public CMask clone() throws CloneNotSupportedException
        {
            return new CMask<>( m_parent, m_name, m_beliefbase );
        }

        /**
         * clone with parameter
         *
         * @param p_parent parent
         * @return mask
         * @throws CloneNotSupportedException
         */
        public CMask clone( final New_IPathMask p_parent ) throws CloneNotSupportedException
        {
            return new CMask<>( p_parent, m_name, m_beliefbase );
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

}
