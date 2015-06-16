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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;

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
        this(new HashMap<>(), new HashSet<>());
    }

    /**
     * ctor - just the top-level literals are specified
     *
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase(final Set<ILiteral<T>> p_literals)
    {
        this(new HashMap<>(), p_literals);
    }

    /**
     * ctor - top-level literals and inherited beliefbases are specified
     *
     * @param p_beliefbases inherited beliefbases
     * @param p_literals    top level literals
     */
    public IDefaultBeliefBase(final Map<String, IBeliefBase<T>> p_beliefbases, final Set<ILiteral<T>> p_literals)
    {
        m_literals = p_literals;
        m_beliefbases = p_beliefbases;
    }

    @Override
    public boolean add(final ILiteral p_literal)
    {
        return m_literals.add(p_literal);
    }

    @Override
    public boolean add(final CPath p_path, final ILiteral<T> p_literal)
    {
        return this.get( p_path ).add( p_literal );
    }

    @Override
    public boolean add(final String p_path, final ILiteral<T> p_literal)
    {
        return this.add( new CPath( p_path ), p_literal );
    }

    @Override
    public boolean add(final String p_name, final IBeliefBase<T> p_beliefbase)
    {
        return m_beliefbases.put( p_name, p_beliefbase ) == null;
    }

    @Override
    public boolean remove(final ILiteral<T> p_literal)
    {
        return m_literals.remove( p_literal );
    }

    @Override
    public boolean remove(final CPath p_path, final ILiteral<T> p_literal)
    {
        return this.get(p_path).remove(p_literal);
    }

    @Override
    public boolean remove(final String p_path, final ILiteral<T> p_literal)
    {
        return this.get(p_path).remove(p_literal);
    }

    @Override
    public boolean removeAll(final Collection<ILiteral<T>> p_literals)
    {
        return m_literals.removeAll( p_literals );
    }

    @Override
    public boolean removeAll(final CPath p_path, final Collection<ILiteral<T>> p_literals)
    {
        return this.get(p_path).removeAll(p_literals);
    }

    @Override
    public boolean removeAll(String p_path, Collection<ILiteral<T>> p_literals)
    {
        return this.get(p_path).removeAll(p_literals);
    }

    @Override
    public boolean remove(String p_name)
    {
        return m_beliefbases.remove(p_name) != null;
    }

    @Override
    public boolean remove(CPath p_path, String p_name)
    {
        return this.get(p_path).remove(p_name);
    }

    @Override
    public boolean remove(String p_path, String p_name)
    {
        return this.get(p_path).remove(p_name);
    }

    @Override
    public IBeliefBase get(final String p_name)
    {
        return m_beliefbases.get( p_name );
    }

    @Override
    public IBeliefBase get( final CPath p_path )
    {
        // if depth of path equals 1, return inherited beliefbase
        if( p_path.size() == 1 )
        {
            final IBeliefBase l_beliefbase = m_beliefbases.get( p_path.get(0) );

            // throw exception if beliefbase was not found
            if( l_beliefbase == null )
                throw new IllegalArgumentException(CCommon.getResourceString(this, "pathnotfound", p_path));

            return l_beliefbase;
        }

        // otherwise start recursion with inherited beliefbase
        return this.get( p_path.get( 0 ) ).get(p_path.getSubPath(1));
    }

    @Override
    public boolean add(final CPath p_path, final String p_name, final IBeliefBase<T> p_beliefbase)
    {
        return this.get( p_path ).getInherited().put( p_name, p_beliefbase ) == null;
    }

    @Override
    public boolean add(String p_path, String p_name, IBeliefBase<T> p_beliefbase)
    {
        return this.get( new CPath( p_path ) ).add( p_name, p_beliefbase );
    }

    @Override
    public boolean addAll(final Collection<ILiteral<T>> p_literals)
    {
        return m_literals.addAll(p_literals);
    }

    @Override
    public boolean addAll(String p_path, Collection<ILiteral<T>> p_literals)
    {
        return this.get( new CPath( p_path ) ).addAll(p_literals);
    }

    @Override
    public boolean addAll(CPath p_path, Collection<ILiteral<T>> p_literals)
    {
        return this.get( p_path ).addAll( p_literals );
    }

    @Override
    public Set<ILiteral<T>> getTopLevelLiterals()
    {
        return m_literals;
    }

    @Override
    public Map<String, IBeliefBase<T>> getInherited()
    {
        return m_beliefbases;
    }

    @Override
    public void clear()
    {
        m_literals.clear();

        for (String l_name : m_beliefbases.keySet())
            m_beliefbases.get(l_name).clear();
    }

    @Override
    public Set<ILiteral<T>> collapse()
    {
        // set for aggregation
        final Set<ILiteral<T>> l_beliefbase = new HashSet<>();

        // start of recursion on top level
        collapse(this, l_beliefbase);

        return l_beliefbase;
    }

    /**
     * static method for recursive traversation of beliefbases
     * to aggregate literals. It prevents the instantiation of
     * an aggregation set in each recursion step.
     *
     * @param p_current    beliefbase to add
     * @param p_aggregated the current aggregated set
     */
    private static void collapse(final IBeliefBase<?> p_current, final Set p_aggregated)
    {
        // add the current beliefbases' literals to aggregated set
        for (final ILiteral<?> l_literal : p_current.getTopLevelLiterals())
            p_aggregated.add(l_literal.getLiteral());

        // recursive method call for each inherited beliefbase
        for (final IBeliefBase<?> l_bb : p_current.getInherited().values())
            collapse(l_bb, p_aggregated);
    }

    /**
     * fills up a stack with Iterator-objects on ILiterals
     *
     * @param p_current current beliefbase with top-level-literals to iterate over
     * @param p_stack current stack
     * @param <N> the literal type
     */
    private static <N> void collapseIterator(final IBeliefBase<N> p_current, final Stack<Iterator<ILiteral<N>>> p_stack)
    {
        // push iterator object on top-level-literals
        p_stack.push(p_current.getTopLevelLiterals().iterator());

        // recursive call for all inherited beliefbases
        for( final IBeliefBase<N> l_beliefbase : p_current.getInherited().values() )
            collapseIterator( l_beliefbase, p_stack);
    }

    /**
     * recursive method to fill up a stack with Iterator-objects
     *
     * @return iterator stack
     */
    private Stack<Iterator<ILiteral<T>>> collapseIterator()
    {
        final Stack<Iterator<ILiteral<T>>> l_stack = new Stack<>();
        collapseIterator(this, l_stack);
        return l_stack;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            /**
             * iterator stack
             */
            private final Stack<Iterator<ILiteral<T>>> m_stack = collapseIterator();

            @Override
            public boolean hasNext()
            {
                // if the stack is empty, we have nothing to iterate over
                if( m_stack.isEmpty() )
                    return false;

                // return true, if the top element of the stack has a next element
                if( m_stack.peek().hasNext() )
                    return true;

                // the top element has no next element, so it can be removed
                m_stack.pop();

                // recursive call
                return this.hasNext();
            }

            /**
             * returns next element of the top iterator
             *
             * @return successor element
             */
            @Override
            public T next()
            {
                return m_stack.peek().next().getLiteral();
            }
        };
    }

    @Override
    public int hashCode()
    {
        return 61 * m_beliefbases.hashCode() +
                79 * m_literals.hashCode();
    }

    @Override
    public boolean equals(final Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }
}
