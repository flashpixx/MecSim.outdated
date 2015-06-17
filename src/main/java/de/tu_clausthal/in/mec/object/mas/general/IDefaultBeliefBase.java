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

import com.graphhopper.coll.MapEntry;
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
     * structure for beliefbase elements (i.e. literals and inherited beliefbases)
     */
    protected final Map<String, Map<Class<?>, Set<? super IBeliefBaseElement>>> m_elements = new HashMap<>();

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
    public IDefaultBeliefBase( final Map<String, IBeliefBase<T>> p_beliefbases, final Set<ILiteral<T>> p_literals )
    {
    }

    /**
     * fills up a stack with Iterator-objects on ILiterals
     *
     * @param p_current current beliefbase with top-level-literals to iterate over
     * @param p_stack current stack
     * @param <N> the literal type
     */
    private static <N> void collapseIterator( final IBeliefBase<N> p_current, final Stack<Iterator<ILiteral<N>>> p_stack )
    {
        // push iterator object on top-level-literals
        p_stack.push( p_current.getLiterals().iterator() );

        // recursive call for all inherited beliefbases
        for ( final IBeliefBase<N> l_beliefbase : p_current.getBeliefbases().values() )
            collapseIterator( l_beliefbase, p_stack );
    }

    @Override
    public boolean add( final CPath p_path, final ILiteral<T> p_literal )
    {
        // @todo traversierung
        this.get( p_path ).getOrDefault( ILiteral.class, new HashSet<>() ).add( p_literal );
    }

    @Override
    public boolean add( final String p_path, final ILiteral<T> p_literal )
    {
        return this.add( new CPath( p_path ), p_literal );
    }

    @Override
    public boolean add( final String p_path, final IBeliefBase<T> p_beliefbase )
    {
        return this.add(new CPath(p_path), p_beliefbase);
    }

    @Override
    public boolean add( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        if ( p_path.isEmpty() )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "emptypath" ) );

        return this.get( p_path.getSubPath( 0, p_path.size() - 1 ) ).getBeliefbases().put( p_path.getSuffix(), p_beliefbase ) != null;
    }

    @Override
    public boolean addAll( final CPath p_path, final Collection<ILiteral<T>> p_literals )
    {
        return this.get( p_path ).getLiterals().addAll(p_literals);
    }

    @Override
    public boolean addAll( String p_path, Collection<ILiteral<T>> p_literals )
    {
        return this.addAll( new CPath( p_path ), p_literals );
    }

    @Override
    public void clear()
    {
        m_elements.clear();
    }

    @Override
    public void clear( CPath p_path )
    {
        this.get( p_path ).clear();
    }

    @Override
    public void clear( String p_path )
    {
        this.clear( new CPath( p_path ) );
    }

    @Override
    public Set<ILiteral<T>> collapse()
    {
        return new HashSet<ILiteral<T>>()
        {
            {
                for ( final ILiteral<T> l_literal : IDefaultBeliefBase.this )
                    add( l_literal );
            }
        };
    }

    /**
     * map of string/beliefbase
     * each entry represents an inherited beliefbase associated with its name
     */
    //    protected final Map<String, IBeliefBase<T>> m_beliefbases;

    /**
     * set of literals representing the top-level beliefs,
     * this set does not contain literals of the inherited beliefbases
     */
    //    protected final Set<ILiteral<T>> m_literals;

    @Override
    public IBeliefBase get( final String p_path )
    {
        return this.get( new CPath( p_path ) );
    }

    /**
     * getter for specified beliefbase
     *
     * @param p_path path to beliefbase
     * @return specified beliefbase
     *
     * @todo new exception class PathNotFoundException which contains the last found beliefbase
     */
    @Override
    public IBeliefBase get( final CPath p_path )
    {
        if ( p_path.isEmpty() )
            return this;

        // get map of beliefbase elements via first elements in path
        // or return the empty set if nothing can be found
        final Map<Class<?>, Set<? super IBeliefBaseElement>> l_element = m_elements.get( p_path.get( 0 ) );
        if ( l_element == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "pathnotfound", p_path ) );

        // get beliefbase with name matching the specified first path-element
        // or return the empty set if no such beliefbase can be found
        final Set<? super IBeliefBaseElement> l_beliefbase = l_element.get( IBeliefBase.class );
        if( ( l_beliefbase == null ) || ( l_beliefbase.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "pathnotfound", p_path ) );

        // recursive call in inherited beliefbase with shortened path
        return ( ( IBeliefBase ) l_beliefbase.iterator().next() );
    }

    /**
     * get top-level literals from a specified beliefbase
     *
     * @param p_path path to an inherited beliefbase
     * @return top-level literals or empty set if beliefbase cannot be found
     */
    @Override
    public Collection<ILiteral<T>> getLiterals( final CPath p_path )
    {
        try
        {
            return this.get( p_path ).getLiterals();
        }
        catch ( IllegalArgumentException l_exception )
        {
            return Collections.emptySet();
        }
    }

    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases()
    {
        return new HashMap() {{
            for ( final Map.Entry<String, Map<Class<?>, Set<? super IBeliefBaseElement>>> l_item : m_elements.entrySet() )
                put( l_item.getKey(), l_item.getValue().get( IBeliefBase.class ).iterator().next() );
        }};
    }

    @Override
    public Collection<ILiteral<T>> getLiterals()
    {
        return new HashSet(){{
            for ( final Map<Class<?>, Set<? super IBeliefBaseElement>> l_value : m_elements.values() )
                addAll( l_value.get(ILiteral.class) );
        }};
    }

    @Override
    public boolean remove( CPath p_path )
    {
        if ( p_path.isEmpty() )
            return false;

        final IBeliefBase<T> l_beliefbase = this.get( p_path.getSubPath( 0, p_path.size() - 1 ) );
        final String l_suffix = p_path.getSuffix();
        if ( l_beliefbase.getBeliefbases().remove( l_suffix ) != null )
            return true;


        boolean l_result = true;
        boolean l_match = false;
        for ( final ILiteral<T> l_literal : l_beliefbase.getLiterals() )
            if ( l_literal.getFunctor().equals( l_suffix ) )
            {
                l_match = true;
                l_result = l_result && l_beliefbase.getLiterals().remove( l_literal );
            }


        return l_result && l_match;
    }

    @Override
    public boolean remove( String p_path )
    {
        return this.remove( new CPath( p_path ) );
    }

    @Override
    public void update()
    {
        for ( final IBeliefBase l_beliefbase : m_beliefbases.values() )
            l_beliefbase.update();
    }

    /**
     * recursive method to fill up a stack with Iterator-objects
     *
     * @return iterator stack
     */
    private Stack<Iterator<ILiteral<T>>> collapseIterator()
    {
        final Stack<Iterator<ILiteral<T>>> l_stack = new Stack<>();
        collapseIterator( this, l_stack );
        return l_stack;
    }

    @Override
    public int hashCode()
    {
        return m_elements.hashCode();
    }

    @Override
    public boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public Iterator<ILiteral<T>> iterator()
    {
        return new Iterator<ILiteral<T>>()
        {
            /**
             * iterator stack
             */
            private final Stack<Iterator<ILiteral<T>>> m_stack = collapseIterator();

            @Override
            public boolean hasNext()
            {
                // if the stack is empty, we have nothing to iterate over
                if ( m_stack.isEmpty() )
                    return false;

                // return true, if the top element of the stack has a next element
                if ( m_stack.peek().hasNext() )
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
            public ILiteral<T> next()
            {
                return m_stack.peek().next();
            }
        };
    }

    public boolean remove( final CPath p_path, final ILiteral<T> p_literal )
    {
        return this.get( p_path ).getLiterals().remove( p_literal );
    }
}
