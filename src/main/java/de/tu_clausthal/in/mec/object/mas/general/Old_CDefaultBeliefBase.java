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

/**
 * generic default beliefbase for software agents, where
 * each beliefbase can contain further inherited getBeliefbases
 *
 * @param <T> language specific literal
 * @deprecated
 */
@Deprecated
public abstract class Old_CDefaultBeliefBase<T> implements Old_IBeliefBase<T>
{
    /*
    *//**
 * structure for beliefbase elements (i.e. literals and inherited getBeliefbases)
 *//*
    private final Map<String, Map<Class<?>, Set<Old_IBeliefBaseElement>>> m_elements = new HashMap<>();
    *//**
 * path to beliefbase
 *//*
    private CPath m_path;

    *//**
 * default ctor
 *//*
    public Old_CDefaultBeliefBase()
    {
        this( null, null, CPath.EMPTY );
    }

    *//**
 * ctor with path
 *
 * @param p_path
 *//*
    public Old_CDefaultBeliefBase( final CPath p_path )
    {
        this(null,null,p_path);
    }

    *//**
 * ctor - just the top-level literals are specified
 *
 * @param p_literals top level literals
 *//*
    public Old_CDefaultBeliefBase( final Set<ILiteral<T>> p_literals )
    {
        this( null, p_literals, CPath.EMPTY );
    }

    *//**
 * ctor - top-level literals and inherited getBeliefbases are specified
 *
 * @param p_inheritedBeliefbases inherited getBeliefbases with paths
 * @param p_literals top level literals
 *//*
    public Old_CDefaultBeliefBase( final Map<String, Old_IBeliefBase<T>> p_inheritedBeliefbases, final Set<ILiteral<T>> p_literals, final CPath p_path )
    {
        // generate map-entries for getBeliefbases
        if ( p_inheritedBeliefbases != null )
            for ( final Map.Entry<String, Old_IBeliefBase<T>> l_beliefbase : p_inheritedBeliefbases.entrySet() )
                this.add( new CPath( l_beliefbase.getKey() ), l_beliefbase.getValue() );

        // generate map-entries for literals
        if ( p_literals != null )
            for ( final ILiteral<T> l_literal : p_literals )
                this.add( l_literal );

        m_path = p_path;
    }

    *//**
 * fills up a stack with Iterator-objects on beliefbase elements
 *
 * @param p_current current beliefbase with top-level-literals to iterate over
 * @param p_stack current stack
 *//*
    private static <N> void collapseIterator( final Old_IBeliefBase<N> p_current, final CPath p_currentPath,
            final Stack<Pair<CPath, Iterator<ILiteral<N>>>> p_stack
    )
    {
        // push iterator object on top-level beliefbase elements
        for ( final Map<Class<?>, Set<Old_IBeliefBaseElement>> l_innerMap : p_current.getElements( CPath.EMPTY ).values() )
            if ( l_innerMap.containsKey( ILiteral.class ) )
            {
                final Set<ILiteral<N>> l_literals = new HashSet<ILiteral<N>>()
                {{
                        for ( final Old_IBeliefBaseElement l_item : l_innerMap.get( ILiteral.class ) )
                            add( (ILiteral<N>) l_item );
                    }};


                p_stack.push(
                        new ImmutablePair<CPath, Iterator<ILiteral<N>>>(
                                p_currentPath, l_literals.iterator()
                        )
                );
            }

        // recursive call for all inherited beliefbases
        final Map<String, Old_IBeliefBase<N>> l_inheritedBeliefbases = p_current.getBeliefbases( CPath.EMPTY );
        for ( final Map.Entry<String, Old_IBeliefBase<N>> l_beliefbase : l_inheritedBeliefbases.entrySet() )
            collapseIterator( l_beliefbase.getValue(), p_currentPath.append( l_beliefbase.getKey() ), p_stack );
    }

    @Override
    public final boolean add( final Old_IBeliefBaseElement p_element )
    {
        if ( p_element instanceof ILiteral )
            return this.add( (ILiteral) p_element );

        if ( p_element instanceof Old_IBeliefBase )
            return this.add( (Old_IBeliefBase) p_element );

        return false;
    }

    @Override
    public final void clear( final CPath... p_path )
    {
        // if nothing is specified, everything will be cleared
        if ( p_path == null || p_path.length == 0 )
        {
            m_elements.clear();
            return;
        }

        // clear beliefbases with specified paths
        for ( final CPath l_path : p_path )
        {
            if ( l_path.isEmpty() )
            {
                m_elements.clear();
                continue;
            }

            final Old_IBeliefBase l_beliefBase = this.get( l_path );
            if ( l_beliefBase != null )
                l_beliefBase.clear( CPath.EMPTY );
        }
    }

    @Override
    public final Old_IBeliefBase<T> get( final CPath p_path )
    {
        if ( p_path.isEmpty() )
            return this;

        // get map of beliefbase-elements by first path-element
        final Map<Class<?>, Set<Old_IBeliefBaseElement>> l_beliefBaseElements = m_elements.get( p_path.get( 0 ) );
        if ( l_beliefBaseElements == null )
            return null;

        // get beliefbase with name matching the specified first path-element
        final Set<Old_IBeliefBaseElement> l_beliefbase = l_beliefBaseElements.get( Old_IBeliefBase.class );
        if ( ( l_beliefbase == null ) || ( l_beliefbase.isEmpty() ) )
            return null;

        // recursive call in inherited beliefbase with shortened path
        return ( (Old_IBeliefBase) l_beliefbase.iterator().next() ).get( p_path.getSubPath( 1 ) );
    }

    @Override
    public final Map<String, Old_IBeliefBase<T>> getBeliefbases( final CPath... p_path )
    {
        if ( p_path == null )
            return Collections.EMPTY_MAP;

        // fill the resulting map with top-level beliefbases
        final Map<String, Old_IBeliefBase<T>> l_result = new HashMap<>();
        for ( final CPath l_path : p_path )
        {
            // get inherited beliefbase
            final Old_IBeliefBase<T> l_beliefbase = this.get( l_path );
            if ( l_beliefbase == null )
                continue;

            // iteration over beliefbase-elements
            for ( final Map.Entry<String, Map<Class<?>, Set<Old_IBeliefBaseElement>>> l_element : l_beliefbase.getElements( CPath.EMPTY ).entrySet() )
                if ( l_element.getValue().containsKey( Old_IBeliefBase.class ) )
                    l_result.put( l_element.getKey(), (Old_IBeliefBase<T>) l_element.getValue().get( Old_IBeliefBase.class ).iterator().next() );
        }

        return l_result;
    }

    *//**
 * returns set of beliefbase elements with specified keys
 *
 * @param p_name name of beliefbase elements
 * @param p_class class of beliefbase elements
 * @return set of beliefbase elements, or null if nothing was found
 *//*
    @Override
    public final Set<Old_IBeliefBaseElement> getElements( final CPath p_path, final String p_name, final Class<?> p_class )
    {
        final Old_IBeliefBase<T> l_beliefbase = this.get( p_path );
        if ( l_beliefbase == null )
            return Collections.EMPTY_SET;

        return (Set<Old_IBeliefBaseElement>) l_beliefbase.getElements( CPath.EMPTY )
                                                         .getOrDefault( p_name, Collections.EMPTY_MAP )
                                                         .getOrDefault( p_class, Collections.EMPTY_SET );
    }

    @Override
    public final Map<String, Map<Class<?>, Set<Old_IBeliefBaseElement>>> getElements( final CPath p_path )
    {
        if ( p_path.isEmpty() )
            return m_elements;

        final Old_IBeliefBase l_beliefbase = this.get( p_path );
        return l_beliefbase == null ? Collections.EMPTY_MAP : l_beliefbase.getElements( CPath.EMPTY );
    }

    @Override
    public final Set<ILiteral<T>> getLiterals( final CPath... p_path )
    {
        final Set<ILiteral<T>> l_literals = new HashSet<ILiteral<T>>();
        if ( p_path == null || p_path.length == 0 )
            for ( final ILiteral<T> l_literal : this )
                l_literals.add( l_literal );

        // step through path array and add top-level literals
        for ( int i = 0; i < p_path.length; ++i )
        {
            final Old_IBeliefBase<T> l_beliefBase = this.get( p_path[i] );
            if ( l_beliefBase == null )
                continue;

            for ( final ILiteral<T> l_item : l_beliefBase )
                l_literals.add( l_item );
        }

        return l_literals;
    }

    *//**
 * gets specified beliefbase if available, or default beliefbase if not available
 *
 * @param p_path path with name of the beliefbase as its last element
 * @param p_beliefbase default beliefbase, will be returned if the specified path cannot be found
 * @return specified beliefbase if path exists, otherwise the default beliefbase
 *//*
    public final Old_IBeliefBase<T> getOrDefault( final CPath p_path, final Old_IBeliefBase<T> p_beliefbase )
    {
        if ( p_path.isEmpty() )
            return this;

        final Old_IBeliefBase<T> l_return = this.get( p_path );
        return l_return == null ? p_beliefbase : l_return;
    }

    @Override
    public CPath getPath()
    {
        return m_path;
    }

    @Override
    public final void setPath( final CPath p_path )
    {
        m_path = p_path;
    }

    @Override
    public final boolean remove( final CPath p_path, final Old_IBeliefBaseElement p_element )
    {
        if ( p_element instanceof ILiteral )
            this.remove( p_path, (ILiteral) p_element );

        if ( p_element instanceof Old_IBeliefBase )
            this.remove( p_path, (Old_IBeliefBase) p_element );

        return false;
    }

    @Override
    public void update()
    {
        this.getBeliefbases().values().forEach( Old_IBeliefBase::update );
    }

    *//**
 * adds a literal to beliefbase with specified path. If path is non-existing
 * it will be constructed.
 *
 * @param p_literal literal to add
 * @return true, if addition was successful
 *//*
    private final boolean add( final ILiteral<T> p_literal )
    {
        if ( p_literal.getPath().isEmpty() )
        {
            final String l_key = p_literal.getFunctor().toString();

            // case 1: there are no beliefbase elements with specified key
            if ( !m_elements.containsKey( l_key ) )
                m_elements.put(
                        l_key, new HashMap<Class<?>, Set<Old_IBeliefBaseElement>>()
                        {{
                                put( ILiteral.class, new HashSet<Old_IBeliefBaseElement>() );
                            }}
                );

            // case 2: there are no literals with specified key
            final Map<Class<?>, Set<Old_IBeliefBaseElement>> l_innerMap = m_elements.get( l_key );
            if ( !l_innerMap.containsKey( ILiteral.class ) )
                l_innerMap.put( ILiteral.class, new HashSet<Old_IBeliefBaseElement>() );

            // case 3: there are already literals with the same key
            return l_innerMap.get( ILiteral.class ).add( p_literal );
        }

        // go down the hierarchy
        Old_IBeliefBase<T> l_current = this;
        for ( final CPath l_subPath : p_literal.getPath() )
        {
            // if non-existing, create new beliefbase
            if ( l_current.getElements( CPath.EMPTY, l_subPath.getSuffix(), Old_IBeliefBase.class ).size() != 1 )
                l_current.add( new Old_CDefaultBeliefBase<T>( new CPath( l_subPath.getSuffix() ) ) );

            l_current = l_current.get( new CPath( l_subPath.getSuffix() ) );
        }

        return l_current.add( p_literal );
    }

    *//**
 * Adds a beliefbase to specified path. The name of the new beliefbase
 * has to be the last element in variable path.
 *
 * @param p_path path with name as last element
 * @param p_beliefbase beliefbase to add
 * @return true if addition was successful
 *//*
    private final boolean add( final CPath p_path, final Old_IBeliefBase<T> p_beliefbase )
    {
        // a name (i.e. last element in path) must be specified to add a new beliefbase
        if ( p_path.isEmpty() )
            throw new IllegalArgumentException( CCommon.getResourceString( Old_CDefaultBeliefBase.class, "emptypath" ) );

        // if path just contains a single element, this is the name of the new beliefbase
        if ( p_path.size() == 1 )
        {
            final String l_key = p_path.getSuffix().toString();

            // add new map-entry if necessary
            if ( !m_elements.containsKey( l_key ) )
                m_elements.put(
                        l_key, new HashMap<Class<?>, Set<Old_IBeliefBaseElement>>()
                        {{
                                put( Old_IBeliefBase.class, new HashSet<Old_IBeliefBaseElement>() );
                            }}
                );

            // push beliefbase into elements (beliefbase with same key gets overwritten)
            m_elements.get( l_key ).put(
                    Old_IBeliefBase.class, new HashSet<Old_IBeliefBaseElement>()
                    {{
                            add( p_beliefbase );
                        }}
            );

            return true;
        }

        // if path size is greater than one, go down hierarchy and add new beliefbases if non-existing
        Old_IBeliefBase<T> l_current = this;
        for ( final CPath l_subpath : p_path.getSubPath( 0, p_path.size() - 1 ) )
        {
            // check if a beliefbase with same key is already existing
            final String l_key = l_subpath.getSuffix();
            if ( l_current.getElements( CPath.EMPTY, l_key, Old_IBeliefBase.class ).size() != 1 )
                l_current.add( new Old_CDefaultBeliefBase<>( new CPath( l_key ) ) );

            l_current = l_current.get( new CPath( l_key ) );
        }

        p_beliefbase.setPath( new CPath( p_path.getSuffix() ) );
        return l_current.add( p_beliefbase );
    }

    *//**
 * recursive method to fill up a stack with Iterator-objects
 *
 * @return iterator stack
 *//*
    private Stack<Pair<CPath, Iterator<ILiteral<T>>>> collapseIterator()
    {
        final Stack<Pair<CPath, Iterator<ILiteral<T>>>> l_stack = new Stack<>();
        collapseIterator( this, CPath.EMPTY, l_stack );
        return l_stack;
    }

    @Override
    public final int hashCode()
    {
        return m_elements.values().hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return m_elements.toString();
    }

    @Override
    public boolean isBeliefbase()
    {
        return true;
    }

    @Override
    public boolean isLiteral()
    {
        return false;
    }

    @Override
    public final Iterator<ILiteral<T>> iterator()
    {
        return new Iterator<ILiteral<T>>()
        {
            *//**
 * iterator stack
 *//*
            private final Stack<Pair<CPath, Iterator<ILiteral<T>>>> m_stack = collapseIterator();

            @Override
            public boolean hasNext()
            {
                // if the stack is empty, we have nothing to iterate over
                if ( m_stack.isEmpty() )
                    return false;

                // return true, if the top element of the stack has a next element
                if ( m_stack.peek().getRight().hasNext() )
                    return true;

                // the top element has no next element, so it can be removed
                m_stack.pop();

                // recursive call
                return this.hasNext();
            }

            *//**
 * returns next element of the top iterator
 *
 * @return successor element
 *//*
            @Override
            public ILiteral<T> next()
            {
                return new CDefaultLiteral<T>(
                        m_stack.peek().getLeft().toString(),
                        m_stack.peek().getRight().next().getLiteral() );
            }
        };
    }

    private final boolean remove( final CPath p_path, final Old_IBeliefBase p_beliefbase )
    {
        // if nothing is specified, nothing can be removed
        if ( p_path.isEmpty() )
            return false;

        // go down the hierarchy and get the last beliefbase
        final Old_IBeliefBase<T> l_beliefbase = this.get( p_path.getSubPath( 0, p_path.size() - 1 ) );
        if ( l_beliefbase == null )
            return false;

        return l_beliefbase.getElements( CPath.EMPTY, p_path.getSuffix(), Old_IBeliefBase.class ).remove( p_beliefbase );
    }

    private final boolean remove( final CPath p_path, final ILiteral<T> p_literal )
    {
        final Old_IBeliefBase<T> l_beliefbase = this.get( p_path );
        if ( l_beliefbase == null )
            return false;

        return l_beliefbase.getElements( CPath.EMPTY, p_literal.getFunctor().toString(), ILiteral.class ).remove( p_literal );
    }

    *//**
 * removes specified beliefbase elements from a beliefbase
 *
 * @param p_path path to beliefbase
 * @param p_key elements name as key
 * @param p_class class of elements
 * @return true, if removal was successful
 *//*
    public final boolean remove( final CPath p_path, final String p_key, final Class p_class )
    {
        final Set<Old_IBeliefBaseElement> l_elements = this.getElements( p_path, p_key, p_class );

        if ( l_elements.isEmpty() )
            return false;

        l_elements.clear();
        return true;
    }*/
}
