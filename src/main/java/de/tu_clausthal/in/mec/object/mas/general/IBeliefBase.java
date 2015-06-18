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
 * beliefbase interface
 * <p>
 * a beliefbase contains beliefs as literals (i.e. the top-level literals)
 * and further inherited beliefbases.
 */
public interface IBeliefBase<T> extends IBeliefBaseElement, Iterable<ILiteral<T>>
{
    /**
     * adds generic literal to beliefbase
     *
     * @param p_literal literal to add
     * @return true if addition was successful
     */
    public boolean add( final ILiteral<T> p_literal );

    /**
     * adds generic literal to specified path (i.e. the path to an inherited beliefbase)
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
     * adds a beliefbase to the set of inherited beliefbases
     *
     * @param p_path path to beliefbase with name of the new beliefbase as last element
     * @param p_beliefbase beliefbase to add
     * @return true if no beliefbase was overwritten
     */
    public boolean add( final String p_path, final IBeliefBase<T> p_beliefbase );

    /**
     * adds generic beliefbase into a specified beliefbase
     *
     * @param p_path path to specific beliefbase with name of new beliefbase as last element
     * @param p_beliefbase beliefbase to add
     * @return true if addition was successful
     */
    public boolean add( final CPath p_path, final IBeliefBase<T> p_beliefbase );

    /**
     * adds a collection of generic literals to a specified inherited beliefbase
     *
     * @param p_path path to specific beliefbase
     * @param p_literals literals to add
     */
    public boolean addAll( final CPath p_path, final Collection<ILiteral<T>> p_literals );

    /**
     * adds a collection of generic literals to a specified inherited beliefbase
     *
     * @param p_path path to specific beliefbase
     * @param p_literals literals to add
     */
    public boolean addAll( final String p_path, final Collection<ILiteral<T>> p_literals );

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited beliefbases will be removed
     */
    public void clear();

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited beliefbases will be removed
     */
    public void clear( final CPath p_path );

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited beliefbases will be removed
     */
    public void clear( final String p_path );

    /**
     * collapse method to get a set of literals containing the top-level
     * and all inherited beliefbases' literals
     *
     * @return collapsed set of top-level and inherited literals
     */
    public Set<ILiteral<T>> collapse();

    /**
     * get beliefbase with specified name
     *
     * @param p_path path with name of the beliefbase as last element
     * @return specified beliefbase or null, if it was not found
     */
    public IBeliefBase get( final String p_path );

    /**
     * get beliefbase with specified path
     *
     * @param p_path path to beliefbase
     * @return specified beliefbase or null
     */
    public IBeliefBase get( final CPath p_path );

    public Map<String, IBeliefBase<T>> getBeliefbases();

    public Collection<ILiteral<T>> getLiterals();

    public Collection<ILiteral<T>> getLiterals( final CPath p_path );

    /**
     * removes a generic literal from a specified beliefbase
     *
     * @param p_path path to specific beliefbase
     */
    public boolean remove( final CPath p_path );

    /**
     * removes a generic literal from a specified beliefbase
     *
     * @param p_path path to specific beliefbase
     */
    public boolean remove( final String p_path );

    /**
     * method to update the beliefbase
     */
    public void update();
}
