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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * Generic default beliefbase for agents.
 * Each beliefbase can contain further inherited beliefbases.
 */
public abstract class IDefaultBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * map of string/beliefbase
     * each entry represents an inherited beliefbase with its name
     */
    protected final Map<String, IDefaultBeliefBase<T>> m_beliefbases;
    /**
     * set of literals representing the top level beliefs,
     * i.e. it does not contain literals of inherited beliefbases
     */
    protected final Set<ILiteral<T>> m_literals;

    /**
     * default ctor
     */
    protected IDefaultBeliefBase()
    {
        m_beliefbases = new HashMap<>();
        m_literals = new HashSet<>();
    }

    /**
     * ctor - just literals specified
     *
     * @param p_literals top level literals
     */
    protected IDefaultBeliefBase(Set<ILiteral<T>> p_literals)
    {
        m_beliefbases = new HashMap<>();
        m_literals = p_literals;
    }

    /**
     * ctor - literals and inherited beliefbases specified
     *
     * @param p_beliefbases inherited beliefbases
     * @param p_literals top level literals
     */
    protected IDefaultBeliefBase(Map<String, IDefaultBeliefBase<T>> p_beliefbases, Set<ILiteral<T>> p_literals)
    {
        m_beliefbases = p_beliefbases;
        m_literals = p_literals;
    }

    /**
     * returns the aggregated beliefbase which also contains
     * the literals of the inherited beliefbases
     */
    public Set<ILiteral<T>> collapseLiterals()
    {
        // set for aggregation
        final Set<ILiteral<T>> l_beliefbase = new HashSet<>();

        // start of recursion on top level
        collapseLiterals(this, l_beliefbase);

        return l_beliefbase;
    }

    /**
     * static method for recursive traversation of beliefbases
     * to aggregate literals. It prevents the instantiation of
     * an aggregation set in each recursion step.
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
     * returns a collapsed beliefbase
     */
    public abstract IBeliefBase<T> collapseBeliefbase();

    /**
     * getter for literal set
     */
    public Set<ILiteral<T>> getLiterals()
    {
        return m_literals;
    }

    /**
     * method for clearing the beliefbase
     */
    public abstract void clear();


    /**
     * hashcode function based on prime number linear combination
     *
     * @return hashcode calculated with prime numbers
     */
    @Override
    public int hashCode()
    {
        return  61 * m_beliefbases.hashCode() +
                79 * m_literals.hashCode();
    }

    /**
     * method for equivalence check
     *
     * @param p_object
     * @return true if beliefbase equals a given object
     */
    @Override
    public boolean equals(final Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }
}
