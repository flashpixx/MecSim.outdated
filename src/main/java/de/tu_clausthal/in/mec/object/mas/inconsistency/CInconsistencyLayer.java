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

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;


/**
 * layer with inconsistence data
 */
public class CInconsistencyLayer extends IMultiLayer<IMeasurement<?>>
{

    @Override
    @CMethodFilter.CAgent( bind = false )
    public void afterStepObject( final int p_currentstep, final IMeasurement<?> p_object )
    {

    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public void beforeStepObject( final int p_currentstep, final IMeasurement<?> p_object )
    {

    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public void step( final int p_currentstep, final ILayer p_layer )
    {
    }


    /**
     * class to create a bind between
     * agent and inconsistency
     */
    public class CBind
    {
        @CFieldFilter.CAgent( bind = false )
        final CAgent<?> m_bind;


        /**
         * ctor
         *
         * @param p_agent sets the agent
         */
        public CBind( final CAgent<?> p_agent )
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
