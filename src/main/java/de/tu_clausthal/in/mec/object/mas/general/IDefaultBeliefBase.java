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
public abstract class IDefaultBeliefBase<T> implements IBeliefBase<ILiteral<T>>
{
    /**
     * storage with data
     */
    protected final CBeliefStorage<ILiteral<T>, IPathMask> m_storage = new CBeliefStorage<>();


    @Override
    public void add( final ILiteral<T> p_literal )
    {
        m_storage.addElement( p_literal.getFunctor().get(), p_literal );
    }


    @Override
    public void add( final IPathMask p_mask )
    {
        m_storage.addMask( p_mask.getName(), p_mask );
    }


    @Override
    public final IPathMask getPathElement( final String p_name )
    {
        return new CMask<>( p_name, this );
    }


    /**
     * mask of a beliefbase
     * @tparam P type of the beliefbase element
     */
    private class CMask<P> implements IPathMask
    {
        /**
         * reference of the beliefbase
         **/
        private final IBeliefBase<P> m_beliefbase;
        /** name of the mask **/
        private final String m_name;

        /**
         * ctor
         *
         * @param p_name name of the mask
         */
        public CMask( final String p_name, final IBeliefBase<P> p_beliefbase )
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

}
