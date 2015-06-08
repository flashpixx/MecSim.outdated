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
 * beliefbase interface
 *
 * a beliefbase contains beliefs as literals (i.e. the top-level literals)
 * and further inherited beliefbases.
 */
public interface IBeliefBase<T>
{
    /**
     * collapse method to get an aggregated top-level-beliefbase,
     * which is the aggregation of the top-level literals and all
     * the inherited beliefbases' literals.
     *
     * @return beliefbase containing literals of all inherited beliefbases
     */
    public IBeliefBase<T> collapseBeliefbase();

    /**
     * collapse method to get a set of literals containing the top-level
     * and all inherited beliefbases' literals
     */
    public Set<ILiteral<T>> collapseLiterals();

    /**
     * getter for top-level literal set
     */
    public Set<ILiteral<T>> getLiterals();

    /**
     * getter for the inherited beliefbases
     */
    public Map<String, IBeliefBase<T>> getBeliefbases();

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the inherited beliefbases will be removed
     */
    public void clear();

    /**
     * adds a language specific literal to the top-level literals
     *
     * @param p_literal
     */
    public abstract void addLiteral( ILiteral p_literal );

    /**
     * removes a language specific literal from the top level literals
     *
     * @param p_literal
     */
    public abstract void removeLiteral( ILiteral p_literal );
}
