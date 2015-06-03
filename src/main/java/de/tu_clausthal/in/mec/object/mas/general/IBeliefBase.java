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

import java.util.Map;
import java.util.Set;

/**
 * agent belief base
 */
public abstract class IBeliefBase<T>
{
    /**
     * map of string/beliefbase
     */
    Map<String,IBeliefBase<T>> m_beliefbases;
    /**
     * set of literals
     */
    Set<ILiteral<T>> m_literals;

    /**
     * returns the aggregated beliefbase which contains
     * every nested literal
     */
    public abstract IBeliefBase<T> collapse();

    /**
     * update method for beliefbase
     */
    public abstract void update();

    /**
     * removes all literals of each beliefbase
     */
    public void clearLiterals()
    {
        m_literals.clear();

        for( String l_name : m_beliefbases.keySet() )
            m_beliefbases.get( l_name ).clearLiterals();
    }

    public void clear()
    {
        m_beliefbases.clear();
        m_literals.clear();
    }

    public boolean addAll( final Set<ILiteral<T>> p_literals )
}
