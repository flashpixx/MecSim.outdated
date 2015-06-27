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

import de.tu_clausthal.in.mec.common.CPath;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * The beliefbase interface for generic beliefbases. A single beliefbase contains beliefs
 * as literals (the top-level literals) and further inherited beliefbases.
 */
public interface IBeliefBase<T> extends IBeliefBaseElement, Iterable<ILiteral<T>>
{
    /**
     * adds a literal to specified path (i.e. the path to an inherited beliefbase)
     * If the path is unknown, it will be constructed.
     *
     * @param p_path path to specific beliefbase
     * @param p_literal literal to add
     * @return true if addition was successful
     */
    public boolean add( final CPath p_path, final ILiteral<T> p_literal );

    /**
     * adds generic literal to specified path (i.e. the path to an inherited beliefbase)
     *
     * @param p_path path to specific beliefbase
     * @param p_literal literal to add
     * @return true if addition was successful
     */
    public boolean add( final String p_path, final ILiteral<T> p_literal );

    /**
     * adds a literal, the path can be part of the literals functor
     *
     * @param p_literal literal to add with path in functor
     * @return true if addition was successful
     */
    public boolean add( final ILiteral<T> p_literal );

    /**
     * Adds a new beliefbase into specified path. The last element in path has to be the name
     * of the new beliefbase. Beliefbases with the same name will be overwritten.
     *
     * @param p_path path to a specific beliefbase with name of new beliefbase as last element
     * @param p_beliefbase beliefbase to add
     */
    public Set<IBeliefBaseElement> add( final String p_path, final IBeliefBase<T> p_beliefbase );

    /**
     * Adds a new beliefbase into specified path. The last element in path has to be the name
     * of the new beliefbase. Beliefbases with the same name will be overwritten.
     *
     * @param p_path path to a specific beliefbase with name of new beliefbase as last element
     * @param p_beliefbase beliefbase to add
     */
    public Set<IBeliefBaseElement> add( final CPath p_path, final IBeliefBase<T> p_beliefbase );

    /**
     * adds a collection of literals into an inherited beliefbase specified by a path
     *
     * @param p_path path to beliefbase
     * @param p_literals literals to add
     */
    public boolean addAll( final CPath p_path, final Collection<ILiteral<T>> p_literals );

    /**
     * adds a collection of literals into an inherited beliefbase specified by a path
     *
     * @param p_path path to beliefbase
     * @param p_literals literals to add
     */
    public boolean addAll( final String p_path, final Collection<ILiteral<T>> p_literals );

    /**
     * get a map of all inherited beliefbases with names
     *
     * @return map of beliefbases
     */
    public Map<String, IBeliefBase<T>> beliefbases();

    /**
     * get a map of all inherited beliefbases with names from specified beliefbase
     *
     * @param p_path path to beliefbase
     * @return map of beliefbases
     */
    public Map<String, IBeliefBase<T>> beliefbases( final CPath p_path );

    /**
     * get a map of all inherited beliefbases with names from specified beliefbase
     *
     * @param p_path path to beliefbase
     * @return map of beliefbases
     */
    public Map<String, IBeliefBase<T>> beliefbases( final String p_path );

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited beliefbases
     */
    void clear();

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited beliefbases
     */
    public void clear( final CPath p_path );

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited beliefbases
     */
    public void clear( final String p_path );

    /**
     * gets a beliefbase with position and name specified in path
     *
     * @param p_path path with name of the beliefbase as last element
     * @return specified beliefbase or null, if it was not found
     */
    public IBeliefBase get( final String p_path );

    /**
     * gets a beliefbase with position and name specified in path
     *
     * @param p_path path with name of the beliefbase as last element
     * @return specified beliefbase or null, if it was not found
     */
    public IBeliefBase get( final CPath p_path );

    /**
     * getter for beliefbase elements
     *
     * @param p_path path to beliefbase with beliefbase elements
     * @param p_key key of beliefbase elements
     * @param p_class class of beliefbase elements
     * @return
     */
    public Set<IBeliefBaseElement> get( final CPath p_path, final String p_key, final Class p_class );

    /**
     * getter for beliefbase elements
     */
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements();

    /**
     * getter for beliefbase elements with path specified
     */
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements( final String p_path );

    /**
     * getter for beliefbase elements with path specified
     */
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements( final CPath p_path );

    /**
     * get beliefbase elements by name and class
     *
     * @param p_name
     * @param p_class
     * @return
     */
    public Set<IBeliefBaseElement> getElements( final String p_name, final Class<?> p_class );

    /**
     * gets a beliefbase with position and name specified in path
     * if there is no beliefbase or the path is unknown, the path
     * will be constructed with a default beliefbase
     *
     * @param p_path path with name of the beliefbase as last element
     * @param p_beliefbase default beliefbase
     * @return specified or default beliefbase
     */
    public IBeliefBase getOrDefault( final CPath p_path, final IBeliefBase<T> p_beliefbase );

    /**
     * returns top level literals with specified key
     *
     * @param p_name
     * @return
     */
    public Set<ILiteral<T>> literals( final String p_name );

    /**
     * returns top level literals
     *
     * @return
     */
    public Set<ILiteral<T>> literals();

    /**
     * returns top level literals
     *
     * @return
     */
    public Set<ILiteral<T>> literals( final CPath p_path );

    /**
     * get inherited literals of specified beliefbase
     *
     * @param p_prefix literals prefix (e.g. the path)
     * @return collection of all inherited literals
     */
    public Set<ILiteral<T>> prefixedLiterals( final String p_prefix );

    /**
     * removes an inherited beliefbase
     *
     * @param p_path path to beliefbase with name as last element
     */
    public boolean remove( final CPath p_path );

    /**
     * removes an inherited beliefbase
     *
     * @param p_path path to beliefbase with name as last element
     */
    public boolean remove( final String p_path );

    /**
     * remove method for specific literal
     *
     * @param p_path path to beliefbase
     * @param p_literal literal to remove
     * @return true, if removal was successful
     */
    public boolean remove( final CPath p_path, final ILiteral<T> p_literal );

    /**
     * updates the beliefbase
     */
    void update();
}
