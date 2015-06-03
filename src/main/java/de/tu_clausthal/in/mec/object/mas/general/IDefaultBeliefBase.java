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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * generic default belief base for agents.
 * each beliefbase can contain further beliefbases.
 *
 * @todo DefaultBeliefBase als Interface und abstrakte Klasse (diese umbenennen)
 * @todo add documentation
 * @todo equals und Hashcode ueberladen
 */
public abstract class IDefaultBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * map of string/beliefbase
     * each entry represents an inherited beliefbase with its name
     */
    private Map<String, IDefaultBeliefBase<T>> m_beliefbases;
    /**
     * set of literals representing the top level beliefs,
     * i.e. it does not contain literals of inherited beliefbases
     */
    private Set<ILiteral<T>> m_literals;

    /**
     * returns the aggregated beliefbase which also contains
     * the literals of the inherited beliefbases
     */
    public Set<T> collapseLiterals()
    {
        // set for aggregation
        final Set<T> l_beliefbase = new HashSet<>();

        // top level start of recursion
        collapseLiterals(this, l_beliefbase);

        return l_beliefbase;
    }

    /**
     * static method for recursive traversation of beliefbases
     * to aggregate the literals. It prevents the instantiation of
     * an aggregation set in every recursion step.
     *
     * @param p_currentBeliefbase beliefbase to add
     * @param p_currentAggregatedSet the current aggregation
     */
    private static void collapseLiterals( final IDefaultBeliefBase<?> p_currentBeliefbase, final Set p_currentAggregatedSet  )
    {
        // add the current beliefbases' literals to aggregated set
        for(final ILiteral<?> l_literal : p_currentBeliefbase.m_literals )
            p_currentAggregatedSet.add(l_literal.getLiteral());

        // recursive method call for each inherited beliefbase
        for( final IDefaultBeliefBase<?> l_bb : p_currentBeliefbase.m_beliefbases.values() )
            collapseLiterals(l_bb, p_currentAggregatedSet);
    }

    /**
     * returns collapsed beliefbase
     *
     * @todo incomplete
     */
    public IBeliefBase<T> collapseBeliefbase()
    {
        return null;
    }

    /**
     * update method for beliefbase
     * @todo move to CFieldBeliefbase - Binding structure
     */
    public abstract void update();

    /**
     * removes all literals of each beliefbase
     * @todo move to CFieldBeliefbase - Binding structure
     */
    public void clearLiterals()
    {
        m_literals.clear();

        for (String l_name : m_beliefbases.keySet())
            m_beliefbases.get(l_name).clearLiterals();
    }

    public void clear()
    {
        m_beliefbases.clear();
        m_literals.clear();
    }

    public boolean addAll(final Set<ILiteral<T>> p_literals)
    {
        return true;
    };
}
