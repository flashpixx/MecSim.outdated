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

package de.tu_clausthal.in.mec.object;

import de.tu_clausthal.in.mec.runtime.IVoidSteppable;


/**
 * layer for any calculation atomic task without visibility
 */
@SuppressWarnings( "serial" )
public abstract class ISingleEvaluateLayer implements ILayer, IVoidSteppable
{
    /**
     * flag for activity
     */
    protected boolean m_active = true;

    @Override
    public int getCalculationIndex()
    {
        return Integer.MAX_VALUE;
    }

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
    public void onSimulationStart()
    {
    }

    @Override
    public void onSimulationStop()
    {
    }

    @Override
    public void onSimulationReset()
    {
    }

    @Override
    public final void release()
    {

    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer )
    {
    }
}
