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

import org.apache.commons.collections4.map.LinkedMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * agent belief base
 */
public abstract class IBeliefBase<T> extends HashMap<String, IBeliefBase<T>>
{
    /**
     * set of literals
     */
    Set<ILiteral<T>> m_literals;

    /**
     * returns the aggregated beliefbase which contains
     * every nested literal
     */
    public abstract IBeliefBase collapse();

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

        for( String l_name : this.keySet() )
            this.get(l_name).clear();
    }
}
