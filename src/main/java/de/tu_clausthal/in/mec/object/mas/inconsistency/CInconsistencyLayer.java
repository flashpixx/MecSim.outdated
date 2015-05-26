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

package de.tu_clausthal.in.mec.object.mas.inconsistency;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.runtime.ISteppable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.painter.Painter;

import java.awt.*;


/**
 * layer with inconsistence data
 */
public class CInconsistencyLayer<T extends IAgent> extends IMultiLayer<CInconsistencyLayer.CImmutablePair<T>>
{
    @Override
    public void afterStepObject( final int p_currentstep, final CImmutablePair<T> p_object )
    {

    }

    @Override
    public void beforeStepObject( final int p_currentstep, final CImmutablePair<T> p_object )
    {

    }

    @Override
    public int getCalculationIndex()
    {
        return 50;
    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer )
    {

    }

    @Override
    public String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * static class to represent a tuple of two elements
     *
     * @tparam <N>
     */
    public static class CImmutablePair<N> extends Pair<N, N> implements ISteppable, Painter
    {
        /**
         * pair value
         **/
        private final ImmutablePair<N, N> m_data;

        /**
         * ctor
         *
         * @param p_left left value
         * @param p_right right value
         */
        public CImmutablePair( final N p_left, final N p_right )
        {
            m_data = new ImmutablePair<>( p_left, p_right );
        }


        @Override
        public N getLeft()
        {
            return m_data.getLeft();
        }

        @Override
        public N getRight()
        {
            return m_data.getRight();
        }

        @Override
        public void paint( final Graphics2D p_graphics2D, final Object o, final int i, final int i1 )
        {
        }

        @Override
        public void release()
        {
        }

        @Override
        public N setValue( final N p_value )
        {
            return m_data.setValue( p_value );
        }

    }

    /**
     * class to create a bind between
     * agent and inconsistency value
     */
    public class CBind
    {
        @CFieldFilter.CAgent( bind = false )
        final CAgent<T> m_bind;


        /**
         * ctor
         *
         * @param p_agent sets the agent
         */
        public CBind( final CAgent<T> p_agent )
        {
            m_bind = p_agent;
        }


        /**
         * returns the agent inconsistency value
         *
         * @return value
         */
        public final Double getInconsistency()
        {
            return new Double( 0 );
        }

    }
}
