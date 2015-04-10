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

package de.tu_clausthal.in.mec.object.car.graph.weights;


import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;


/**
 * wrapper class to encapsulate a GraphHipper weighting object with the IWeighting interface
 */
public class CWeightingWrapper<T extends Weighting> implements IWeighting
{

    /**
     * GraphHopper weight object *
     */
    private final T m_weight;
    /**
     * active flag *
     */
    private boolean m_active = false;


    /**
     * ctor
     *
     * @param p_weight weight object
     */
    public CWeightingWrapper( final T p_weight )
    {
        m_weight = p_weight;
    }


    /**
     * ctor
     *
     * @param p_weight weight object
     * @param p_active active flag
     */
    public CWeightingWrapper( final T p_weight, final boolean p_active )
    {
        m_weight = p_weight;
        m_active = p_active;
    }


    @Override
    public final boolean isActive()
    {
        return m_active;
    }

    @Override
    public final void setActive( final boolean p_value )
    {
        m_active = p_value;
    }

    @Override
    public final double getMinWeight( final double p_weight )
    {
        return m_weight.getMinWeight( p_weight );
    }

    @Override
    public final double calcWeight( final EdgeIteratorState p_edge, final boolean p_reverse )
    {
        return m_weight.calcWeight( p_edge, p_reverse );
    }
}
