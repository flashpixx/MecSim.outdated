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

import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * generic default beliefbase for software agents, where
 * each beliefbase can contain further inherited beliefbases
 */
public abstract class IDefaultBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * map of string/beliefbase
     * each entry represents an inherited beliefbase associated with its name
     */
    protected final Map<String, IBeliefBase<T>> m_beliefbases;

    /**
     * set of literals representing the top-level beliefs,
     * this set does not contain literals of the inherited beliefbases
     */
    protected final Set<ILiteral<T>> m_literals;

    /**
     * default ctor
     * creates an empty set of top-level literals and
     * an empty map of inherited beliefbases
     */
    public IDefaultBeliefBase()
    {
        this( new HashMap<>(), new HashSet<>() );
    }

    /**
     * ctor - just the top-level literals are specified
     *
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase( final Set<ILiteral<T>> p_literals )
    {
        this(new HashMap<>(), p_literals);
    }

    /**
     * ctor - top-level literals and inherited beliefbases are specified
     *
     * @param p_beliefbases inherited beliefbases
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase(final Map<String, IBeliefBase<T>> p_beliefbases, final Set<ILiteral<T>> p_literals)
    {
        m_literals = p_literals;
        m_beliefbases = p_beliefbases;
    }

    /**
     * collapse method to get a set of literals containing the top-level
     * and all inherited beliefbases' literals
     *
     * @return collapsed set of top-level and inherited literals
     */
    @Override
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
     * @param p_currentAggregatedSet the current aggregated set
     */
    private static void collapseLiterals( final IBeliefBase<?> p_currentBeliefbase, final Set p_currentAggregatedSet  )
    {
        // add the current beliefbases' literals to aggregated set
        for(final ILiteral<?> l_literal : p_currentBeliefbase.getLiterals() )
            p_currentAggregatedSet.add(l_literal.getLiteral());

        // recursive method call for each inherited beliefbase
        for( final IBeliefBase<?> l_bb : p_currentBeliefbase.getBeliefbases().values() )
            collapseLiterals( l_bb, p_currentAggregatedSet );
    }

    /**
     * getter for top-level literal set
     *
     * @return set of top-level literals
     */
    @Override
    public Set<ILiteral<T>> getLiterals()
    {
        return m_literals;
    }

    /**
     * getter for the inherited beliefbases
     *
     * @return map of inherited beliefbases and their names
     */
    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases()
    {
        return m_beliefbases;
    }

    /**
     * adds a generic literal to the top-level literals
     *
     * @param p_literal generic literal
     */
    @Override
    public void addLiteral( final ILiteral p_literal )
    {
        m_literals.add( p_literal );
    }

    /**
     * adds a collection of generic literals
     */
    @Override
    public void addAllLiterals( final Collection<ILiteral<T>> p_literals )
    {
        m_literals.addAll(p_literals);
    }

    /**
     * removes a language specific literal from the top level literals
     *
     * @param p_literal language specific literal
     */
    @Override
    public void removeLiteral( final ILiteral p_literal )
    {
        m_literals.remove( p_literal );
    }

    /**
     * removes a collection of generic literals
     */
    @Override
    public void removeAllLiterals( final Collection<ILiteral<T>> p_literals )
    {
        m_literals.remove( p_literals );
    }

    /**
     * removes a beliefbase recursively from inherited beliefbases
     *
     * @param p_name beliefbase to remove
     */
    @Override
    public void removeBeliefbase( final String p_name )
    {
        // removes specified beliefbase if its inherited
        m_beliefbases.remove( p_name );

        // recursive call
        for( IBeliefBase<T> l_beliefbase : m_beliefbases.values() )
            l_beliefbase.removeBeliefbase( p_name );
    }

    /**
     * method to clear top-level literals and optionally the inherited literals
     *
     * @param p_recursive set true to clear all inherited literals
     */
    public void clearLiterals( final boolean p_recursive )
    {
        m_literals.clear();

        if( p_recursive )
            for (String l_name : m_beliefbases.keySet())
                m_beliefbases.get(l_name).clearLiterals();
    }

    /**
     * removes the top-level and the inherited beliefbases' literals
     * i.e. all the inherited beliefbases stay preserved empty
     */
    @Override
    public void clearLiterals()
    {
        clearLiterals( true );
    }

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the inherited beliefbases will be removed
     */
    @Override
    public void clear()
    {
        m_literals.clear();
        m_beliefbases.clear();
    }

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

    /**
     * method for adding a beliefbase
     * the prefix gets concatenated by the beliefbases' hashcode
     *
     * @param p_name beliefbase name
     * @param p_beliefbase beliefbase to add
     */
    @Override
    public void addBeliefbase(final String p_name, final IBeliefBase<T> p_beliefbase)
    {
        m_beliefbases.put( p_name, p_beliefbase);
    }
}
