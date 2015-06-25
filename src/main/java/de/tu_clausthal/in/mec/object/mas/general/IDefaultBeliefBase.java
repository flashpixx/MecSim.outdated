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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * generic default beliefbase for software agents, where
 * each beliefbase can contain further inherited beliefbases
 */
public abstract class IDefaultBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * structure for beliefbase elements (i.e. literals and inherited beliefbases)
     */
    protected final Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> m_elements = new HashMap<>();

    /**
     * default ctor
     */
    public IDefaultBeliefBase()
    {
        this( null, null );
    }

    /**
     * ctor - just the top-level literals are specified
     *
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase( final Set<ILiteral<T>> p_literals )
    {
        this( null, p_literals );
    }

    /**
     * ctor - top-level literals and inherited beliefbases are specified
     *
     * @param p_inheritedBeliefbases inherited beliefbases with paths
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase( final Map<String, IBeliefBase<T>> p_inheritedBeliefbases, final Set<ILiteral<T>> p_literals )
    {
        // generate map-entries for beliefbases
        if ( p_inheritedBeliefbases != null )
            for ( final Map.Entry<String, IBeliefBase<T>> l_beliefbase : p_inheritedBeliefbases.entrySet() )
                this.add( l_beliefbase.getKey(), l_beliefbase.getValue() );

        // generate map-entries for literals
        if ( p_literals != null )
            for ( final ILiteral<T> l_literal : p_literals )
                this.add( CPath.EMPTY, l_literal );
    }

    @Override
    public boolean add( final CPath p_path, final ILiteral<T> p_literal )
    {
        // get inherited beliefbase
        final IBeliefBase<T> l_beliefbase = this.getOrDefault(
                p_path, new IDefaultBeliefBase<T>()
        {
        }
        );

        // get beliefbase elements and inner-map with specified key
        final String l_key = p_literal.getFunctor().toString();
        final Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> l_elements = l_beliefbase.getElements();
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = l_elements.get( l_key );

        // if there is no inner-map with same key, generate new map-entry and put literal into it
        if ( l_innerMap == null )
            return l_elements.put(
                    l_key, new HashMap()
                    {{
                            put(
                                    ILiteral.class, new HashSet()
                                    {{
                                            add( p_literal );
                                        }}
                            );
                        }}
            ) == null;

        // get literals with same key from inner-map
        final Set<IBeliefBaseElement> l_innerLiterals = l_innerMap.get( ILiteral.class );

        // if there are no literals with same key, generate new set and put literal into it
        if ( l_innerLiterals == null )
            return l_innerMap.put(
                    ILiteral.class, new HashSet()
                    {{
                            add(p_literal);
                        }}
            ) == null;

        // if there are literals with same key, just add current literal to this set
        return l_innerLiterals.add(p_literal);

    }

    @Override
    public boolean add( final String p_path, final ILiteral<T> p_literal )
    {
        return this.add( new CPath( p_path ), p_literal );
    }

    @Override
    public Set<IBeliefBaseElement> add( final String p_path, final IBeliefBase<T> p_beliefbase )
    {
        return this.add(new CPath( p_path ), p_beliefbase);
    }

    @Override
    public Set<IBeliefBaseElement> add( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        // a name (i.e. last element in path) must be specified to add a new beliefbase
        if ( p_path.isEmpty() )
            throw new IllegalArgumentException( CCommon.getResourceString( IDefaultBeliefBase.class, "emptypath" ) );

        // get beliefbase or construct new ones
        final IBeliefBase<T> l_inherited = this.getOrDefault(
                p_path.getSubPath( 0, p_path.size() - 1 ),
                new IDefaultBeliefBase<T>()
                {
                }
        );

        // get inner map of beliefbase elements
        final Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> l_elements = l_inherited.getElements();
        if ( !l_elements.keySet().contains( p_path.getSuffix() ) )
            l_elements.put( p_path.getSuffix(), new HashMap<Class<?>, Set<IBeliefBaseElement>>() );
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = l_elements.get( p_path.getSuffix() );

        // push beliefbase into inner map
        return l_innerMap.put( IBeliefBase.class, new HashSet<IBeliefBaseElement>(){{ add( p_beliefbase ); }} );
    }

    @Override
    public boolean addAll( final CPath p_path, final Collection<ILiteral<T>> p_literals )
    {
        // get inherited beliefbase, construct a new one if path is unknown
        IBeliefBase<T> l_innerBeliefBase = this.getOrDefault(
                p_path, new IDefaultBeliefBase<T>()
        {
        }
        );

        // add every literal to beliefbase
        boolean l_success = true;
        for ( final ILiteral<T> l_literal : p_literals )
            l_success = l_success && l_innerBeliefBase.add( CPath.EMPTY, l_literal );

        return l_success;
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
        final Map<Class<?>, Set<IBeliefBaseElement>> l_beliefBaseElements = m_elements.get( p_path.get( 0 ) );
        if ( l_beliefBaseElements == null )
            return null;

        // get beliefbase with name matching the specified first path-element
        final Set<IBeliefBaseElement> l_beliefbase = l_beliefBaseElements.get( IBeliefBase.class );
        if ( ( l_beliefbase == null ) || ( l_beliefbase.isEmpty() ) )
            return null;

        // recursive call in inherited beliefbase with shortened path
        return ( (IBeliefBase) l_beliefbase.iterator().next() ).get(p_path.getSubPath(1));
    }

    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases( CPath p_path )
    {
        // get inherited beliefbase
        final IBeliefBase<T> l_beliefbase = this.get( p_path );

        // return top-level beliefbases if existing
        return l_beliefbase == null ?
                Collections.EMPTY_MAP :
                new HashMap<String, IBeliefBase<T>>(){{
                    for ( final Map.Entry<String, Map<Class<?>, Set<IBeliefBaseElement>>> l_element : m_elements.entrySet() )
                        if ( l_element.getValue().get( IBeliefBase.class ) != null )
                            put( l_element.getKey(), (IBeliefBase<T>) l_element.getValue().get( IBeliefBase.class ).iterator().next() );
                }};
    }

    @Override
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements()
    {
        return m_elements;
    }

    @Override
    public Set<ILiteral<T>> getLiterals()
    {
        return this.getLiterals( "" );
    }

    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases()
    {
        return this.getBeliefbases( CPath.EMPTY );
    }

    @Override
    public Set<ILiteral<T>> getLiterals( final String p_prefix )
    {
        // return top-level literals with prefix
        return new HashSet()
        {{
                for ( final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap : m_elements.values() )
                {
                    final Set<IBeliefBaseElement> l_set = l_innerMap.get( ILiteral.class );

                    if ( l_set != null )
                        for ( final IBeliefBaseElement l_element : l_set )
                        {
                            final ILiteral<T> l_literal = (ILiteral<T>) l_element;

                            add(new IDefaultLiteral<T>(p_prefix + l_literal.getFunctor().toString(), l_literal.getLiteral(),
                                    (CTermList) l_literal.getValues(), (CTermSet) l_literal.getAnnotation() ) {} );
                        }
                }
            }};
    }

    /**
     * @param p_path path with name of the beliefbase as last element
     * @param p_beliefbase default beliefbase
     * @return
     */
    public IBeliefBase<T> getOrDefault( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        if ( p_path.isEmpty() )
            return this;

        IDefaultBeliefBase<T> l_currentBeliefbase = this;

        for ( final CPath l_pathElement : p_path )
        {
            // get next inherited beliefbase
            final IDefaultBeliefBase<T> l_nextBeliefbase = (IDefaultBeliefBase) l_currentBeliefbase.getTopBeliefbase( l_pathElement.getSuffix() );

            // if no such beliefbase exists, create a new one
            if ( l_nextBeliefbase == null )
                l_currentBeliefbase.addTopElement(
                        l_pathElement.getSuffix(), IBeliefBase.class, new HashSet<IBeliefBaseElement>()
                        {{
                                add(
                                        l_pathElement.equals( p_path ) ?
                                                p_beliefbase :
                                                new IDefaultBeliefBase<T>()
                                                {
                                                }
                                );
                            }}
                );

            l_currentBeliefbase = (IDefaultBeliefBase) l_currentBeliefbase.getTopBeliefbase( l_pathElement.getSuffix() );
        }

        return l_currentBeliefbase;
    }

    @Override
    public Set<ILiteral<T>> getTopLiterals( final String p_name )
    {
        final Set<IBeliefBaseElement> l_beliefbaseElements = this.getElements( p_name, ILiteral.class );

        return l_beliefbaseElements == null ? null : new HashSet<ILiteral<T>>()
        {{
                for ( final IBeliefBaseElement l_beliefbaseElement : l_beliefbaseElements )
                    add( (ILiteral<T>) l_beliefbaseElement );
            }};
    }

    @Override
    public Set<ILiteral<T>> getTopLiterals()
    {
        final Set<ILiteral<T>> l_return = new HashSet<ILiteral<T>>();

        for ( final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap : m_elements.values() )
        {
            Set<IBeliefBaseElement> l_elements = l_innerMap.get( ILiteral.class );

            if ( l_elements != null )
                for ( final IBeliefBaseElement l_innerLiteral : l_elements )
                    l_return.add( (ILiteral) l_innerLiteral );
        }

        return l_return;
    }

    public Set<ILiteral<T>> getTopLiterals( final CPath p_path )
    {
        return this.get( p_path ).getTopLiterals();
    }

    @Override
    public boolean remove( final CPath p_path )
    {
        // if nothing is specified, nothing can be removed
        if ( p_path.isEmpty() )
            return false;

        // go down the hierarchy and get the last beliefbase
        final IBeliefBase<T> l_beliefbase = this.get( p_path.getSubPath( 0, p_path.size() - 1 ) );

        if ( l_beliefbase == null )
            return false;

        // try to get beliefbase elements with same key
        final Map<Class<?>, Set<IBeliefBaseElement>> l_beliefbaseElements =
                ( (IDefaultBeliefBase<T>) l_beliefbase ).getElements().get( p_path.getSuffix() );
        if ( l_beliefbaseElements == null )
            return false;

        // remove specified beliefbase
        return l_beliefbaseElements.remove( IBeliefBase.class ) != null;
    }

    @Override
    public boolean remove( final String p_path )
    {
        return this.remove(new CPath(p_path));
    }

    @Override
    public boolean remove( final CPath p_path, final ILiteral<T> p_literal )
    {
        return ( (IDefaultBeliefBase<T>) this.get( p_path ) ).getElements().get( p_literal.getFunctor() ).get( ILiteral.class ).remove(p_literal);
    }

    @Override
    public void update()
    {
        this.getBeliefbases( CPath.EMPTY ).values().forEach(de.tu_clausthal.in.mec.object.mas.general.IBeliefBase::update);
    }

    private void addTopElement( final String p_name, final Class<?> p_class, final Set<IBeliefBaseElement> p_element )
    {
        // get inner map by name, or create a new one
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = m_elements.getOrDefault(
                p_name, new HashMap<Class<?>, Set<IBeliefBaseElement>>()
        );
        m_elements.put( p_name, l_innerMap );

        // get beliefbase elements
        final Set<IBeliefBaseElement> l_data = l_innerMap.getOrDefault( p_class, new HashSet<IBeliefBaseElement>() );
        if ( IBeliefBase.class.equals( p_class ) )
            l_data.clear();

        l_data.addAll( p_element );
        l_innerMap.put( p_class, l_data );
    }

    /**
     * returns set of beliefbase elements with specified keys
     *
     * @param p_name name of beliefbase elements
     * @param p_class class of beliefbase elements
     * @return set of beliefbase elements, or null if nothing was found
     */
    private Set<IBeliefBaseElement> getElements( final String p_name, final Class<?> p_class )
    {
        // get possible beliefbase elements by name
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = m_elements.get( p_name );
        if ( l_innerMap == null )
            return null;

        // get specific elements by class
        final Set<IBeliefBaseElement> l_innerSet = l_innerMap.get(p_class);
        if ( ( l_innerSet == null ) || ( l_innerSet.isEmpty() ) )
            return null;

        return l_innerSet;
    }

    @Override
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements(String p_path)
    {
        return this.getElements( new CPath( p_path ) );
    }

    @Override
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements(CPath p_path)
    {
        final IBeliefBase l_beliefbase = this.get(p_path);

        return l_beliefbase == null ? null : l_beliefbase.getElements();
    }

    /**
     * returns beliefbase with specified key (i.e. name of the beliefbase)
     *
     * @param p_name name of beliefbase
     * @return beliefbase, or null if no such beliefbase was found
     */
    private IBeliefBase<T> getTopBeliefbase( final String p_name )
    {
        final Set<IBeliefBaseElement> l_bb = this.getElements( p_name, IBeliefBase.class );
        return l_bb == null ? null : (IBeliefBase<T>) l_bb.iterator().next();
    }

    @Override
    public int hashCode()
    {
        return m_elements.values().hashCode();
    }

    @Override
    public boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public String toString()
    {
        return m_elements.toString();
    }

    /**
     * fills up a stack with Iterator-objects on beliefbase elements
     *
     * @param p_current current beliefbase with top-level-literals to iterate over
     * @param p_stack current stack
     */
    private static <N> void collapseIterator( final IBeliefBase<N> p_current, final CPath p_currentPath, final Stack<Iterator<ILiteral<N>>> p_stack )
    {
        // push iterator object on top-level beliefbase elements
        p_stack.push( p_current.getLiterals( p_currentPath.toString() ).iterator() );

        final Map<String, IBeliefBase<N>> l_inheritedBeliefbases = p_current.getBeliefbases();

        // recursive call for all inherited beliefbases
        for ( final Map.Entry<String, IBeliefBase<N>> l_beliefbase : l_inheritedBeliefbases.entrySet() )
            collapseIterator( l_beliefbase.getValue(), p_currentPath.append(l_beliefbase.getKey()), p_stack );
    }

    /**
     * recursive method to fill up a stack with Iterator-objects
     *
     * @return iterator stack
     */
    private Stack<Iterator<ILiteral<T>>> collapseIterator()
    {
        final Stack<Iterator<ILiteral<T>>> l_stack = new Stack<>();
        collapseIterator(this, CPath.EMPTY, l_stack);
        return l_stack;
    }

    /**
     * iterator method over beliefbase elements
     *
     * @return
     */
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

    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases(String p_path)
    {
        return this.getBeliefbases(new CPath(p_path));
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
        for ( final ILiteral<T> l_literal : l_beliefbase.getLiterals() )
            if ( l_literal.getFunctor().equals( p_functor ) )
            {
                l_match = true;
                l_result = l_result && l_beliefbase.getLiterals().remove( l_literal );
            }

        return l_result && l_match;
    }

    public boolean remove( final String p_path, final ILiteral<T> p_literal )
    {
        return this.remove( new CPath( p_path ), p_literal );
    }
}
