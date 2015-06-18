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
import de.tu_clausthal.in.mec.object.mas.jason.belief.IBelief;

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
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase(final Map<String, IBeliefBase<T>> p_beliefbases, final Set<ILiteral<T>> p_literals)
    {
        // generate map-entries for beliefbases
        for ( final String l_key : p_beliefbases.keySet() )
            m_elements.put( l_key, new HashMap() {{
                put(IBeliefBase.class, new HashSet() {{
                    add(p_beliefbases.get(l_key));
                }});
            }} );

        // generate map-entries for literals
        for ( final ILiteral<T> l_literal : p_literals )
            this.add(l_literal);

    }

    @Override
    public IBeliefBase<T> get( final String p_path )
    {
        return this.get( new CPath( p_path ) );
    }

    @Override
    public IBeliefBase<T> get( final CPath p_path )
    {
        if ( p_path.isEmpty() )
            return this;

        // get map of beliefbase-elements by first path-element
        final Map<Class<?>, Set<? super IBeliefBaseElement>> l_beliefBaseElements = m_elements.get( p_path.get( 0 ) );
        if ( l_beliefBaseElements == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "pathnotfound", p_path ) );

        // get beliefbase with name matching the specified first path-element
        final Set<? super IBeliefBaseElement> l_beliefbase = l_beliefBaseElements.get( IBeliefBase.class );
        if( ( l_beliefbase == null ) || ( l_beliefbase.isEmpty() ) )
            throw new IllegalArgumentException( p_path.toString() );

        // recursive call in inherited beliefbase with shortened path
        return ( ( IBeliefBase ) l_beliefbase.iterator().next() ).get( p_path.getSubPath( 1 ) );
    }

    /**
     *
     * @param p_path path with name of the beliefbase as last element
     * @param p_beliefbase default beliefbase
     * @return
     *
     * @warning untested
     */
    public IBeliefBase<T> getOrDefault( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        if ( p_path.isEmpty() )
            return this;

        // if there are no beliefbase-elements with specified key
        if ( m_elements.get( p_path.get( 0 ) ) == null )

            // generate new map-entry and put a new beliefbase into it
            m_elements.put( p_path.get( 0 ), new HashMap() {{
                put( IBeliefBase.class, new HashSet() {{
                    add( p_path.size() == 1 ?
                            p_beliefbase : new IDefaultBeliefBase<T>(){} );
                }});
            }});

        // get map of beliefbase-elements by first path-element
        final Map<Class<?>, Set<? super IBeliefBaseElement>> l_beliefBaseElements = m_elements.get( p_path.get( 0 ) );

        // if there is no beliefbase in beliefbase-elements
        if ( l_beliefBaseElements.get( IBeliefBase.class ) == null )

            // add new beliefbase into beliefbase-elements
            l_beliefBaseElements.put( IBeliefBase.class, new HashSet() {{
                add( p_path.size() == 1 ?
                        p_beliefbase : new IDefaultBeliefBase<T>(){} );
            }} );

        // get beliefbase with name matching the specified first path-element
        final Set<? super IBeliefBaseElement> l_beliefbase = l_beliefBaseElements.get( IBeliefBase.class );

        // step down one level and do recursion
        return ( ( IBeliefBase ) l_beliefbase.iterator().next() ).getOrDefault( p_path.getSubPath( 1 ), p_beliefbase );
    }

    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases( CPath p_path )
    {
        // if path is empty, return all the top-level beliefbases
        if ( p_path.isEmpty() )
            return new HashMap() {{
                for ( final Map.Entry<String, Map<Class<?>, Set<? super IBeliefBaseElement>>> l_item : m_elements.entrySet() )
                    put( l_item.getKey(), l_item.getValue().get( IBeliefBase.class ).iterator().next() );
            }};

        // else go down the hierarchy and do self call
        return this.get( p_path ).getBeliefbases( CPath.EMPTY );
    }

    public Collection<ILiteral<T>> getLiterals()
    {
        return new HashSet(){{
            for ( final Map<Class<?>, Set<? super IBeliefBaseElement>> l_value : m_elements.values() )
                addAll( l_value.get(ILiteral.class) );
        }};
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
            return this.get( p_path ).getLiterals( CPath.EMPTY );
        }
        catch ( IllegalArgumentException l_exception )
        {
            return Collections.emptySet();
        }
    }

    /**
     * add literal to current beliefbase
     *
     * @param p_literal literal to add
     * @return true if addition was successful
     */
    @Override
    public boolean add( final ILiteral<T> p_literal )
    {
        // set literals functor as key
        final String l_key = p_literal.getFunctor().toString();

        // get inner-map with same key
        final Map<Class<?>, Set<? super IBeliefBaseElement>> l_innerMap = m_elements.get( l_key );

        // if there is no inner-map with same key, generate new map-entry and put literal into it
        if( l_innerMap == null )
            return m_elements.put( l_key, new HashMap() {{
                        put( ILiteral.class, new HashSet() {{
                            add( p_literal );
                        }} );
                    }} ) == null;

        // get literals with same key from inner-map
        final Set<? super IBeliefBaseElement> l_innerLiterals = l_innerMap.get( ILiteral.class );

        // if there are no literals with same key, generate new set and put literal into it
        if( l_innerLiterals == null )
            return l_innerMap.put( ILiteral.class, new HashSet() {{ add( p_literal ); }} ) == null;

        // if there are literals with same key, just add current literal to this set
        return l_innerLiterals.add( p_literal );
    }

    /**
     *
     * @param p_path path to specific beliefbase
     * @param p_literal literal to add
     * @return
     *
     * @todo construct new beliefbases when path is unknown
     */
    @Override
    public boolean add( final CPath p_path, final ILiteral<T> p_literal )
    {
        return this.get( p_path ).add( p_literal );
    }

    @Override
    public boolean add( final String p_path, final ILiteral<T> p_literal )
    {
        return this.add( new CPath( p_path ), p_literal );
    }

    /**
     *
     * @param p_path path to a specific beliefbase with name of new beliefbase as last element
     * @param p_beliefbase beliefbase to add
     *
     * @todo construct new beliefbases when path is unknown
     */
    @Override
    public void add( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        // in order to add a beliefbase, a name (i.e. last element in path) must be specified
        if( p_path.isEmpty() )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "emptypath" ) );

        // if path contains a single element, this element has to be the name of the new beliefbase
        if ( p_path.size() == 1 )
        {
            m_elements.put( p_path.get(0), new HashMap()
            {{
                    put(IBeliefBase.class, new HashSet()
                    {{
                            add(p_beliefbase);
                        }});
                }});
            return;
        }

        // if the path contains more than one element, go down the hierarchy and add the beliefbase
        this.get( p_path.getSubPath( 0, p_path.size() - 1 ) ).add( p_path.getSuffix(), p_beliefbase );
    }

    @Override
    public void add( final String p_path, final IBeliefBase<T> p_beliefbase )
    {
        this.add(new CPath(p_path), p_beliefbase);
    }

    @Override
    public boolean remove( final CPath p_path )
    {
        // if nothing is specified, nothing can be removed
        if ( p_path.isEmpty() )
            return false;

        // go down the hierarchy and get the last beliefbase
        final IBeliefBase<T> l_beliefbase = this.get( p_path.getSubPath( 0, p_path.size() - 1 ) );

        // try to remove the beliefbase which name equals the suffix of the path
        final String l_suffix = p_path.getSuffix();
        if ( l_beliefbase.getBeliefbases( CPath.EMPTY ).remove( l_suffix ) != null )
            return true;

        return false;
    }

    public boolean remove( final CPath p_path, final String p_functor )
    {
        // go down the hierarchy and get last beliefbase
        final IBeliefBase<T> l_beliefbase = this.get( p_path );

        // result becomes false, if there the removal fails
        boolean l_result = true;

        // match becomes true, if there is at least one literal that should be removed
        boolean l_match = false;

        // remove literals which functor equals the methods argument p_functor
        for ( final ILiteral<T> l_literal : l_beliefbase.getLiterals( CPath.EMPTY ) )
            if ( l_literal.getFunctor().equals( p_functor ) )
            {
                l_match = true;
                l_result = l_result && l_beliefbase.getLiterals( CPath.EMPTY ).remove( l_literal );
            }

        return l_result && l_match;
    }

    @Override
    public boolean remove( String p_path )
    {
        return this.remove( new CPath( p_path ) );
    }

    public boolean remove( final CPath p_path, final ILiteral<T> p_literal )
    {
        return this.get( p_path ).getLiterals( CPath.EMPTY ).remove( p_literal );
    }

    public boolean remove( final String p_path, final ILiteral<T> p_literal )
    {
        return this.remove( new CPath( p_path ), p_literal );
    }

    @Override
    public void update()
    {
        this.getBeliefbases( CPath.EMPTY ).values().forEach(de.tu_clausthal.in.mec.object.mas.general.IBeliefBase::update);
    }

    @Override
    public void clear()
    {
        m_elements.clear();
    }

    @Override
    public void clear( CPath p_path )
    {
        this.get(p_path).clear();
    }

    @Override
    public void clear( String p_path )
    {
        this.clear( new CPath( p_path ) );
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
        p_stack.push( p_current.getLiterals( CPath.EMPTY ).iterator() );

        // recursive call for all inherited beliefbases
        for ( final IBeliefBase<N> l_beliefbase : p_current.getBeliefbases( CPath.EMPTY ).values() )
            collapseIterator( l_beliefbase, p_stack );
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
    public boolean addAll( final CPath p_path, final Collection<ILiteral<T>> p_literals )
    {
        return this.get( p_path ).getLiterals( CPath.EMPTY ).addAll(p_literals);
    }

    @Override
    public boolean addAll( String p_path, Collection<ILiteral<T>> p_literals )
    {
        return this.addAll( new CPath( p_path ), p_literals );
    }

    /**
     *
     * @return
     *
     * @todo concatenate path for unification
     */
    @Override
    public Set<ILiteral<T>> collapse( final CPath p_path )
    {
        // if path is empty, do collapse on inherited beliefbases
        if ( p_path.isEmpty() )
            return new HashSet<ILiteral<T>>() {{
                for ( final ILiteral<T> l_literal : IDefaultBeliefBase.this )
                    add( l_literal );
            }};

        // go down the hierarchy and do collapse
        return this.get( p_path ).collapse( CPath.EMPTY );
    }
}
