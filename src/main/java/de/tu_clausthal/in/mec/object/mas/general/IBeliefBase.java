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
 * The beliefbase interface for generic getBeliefbases. A single beliefbase contains beliefs
 * as literals (the top-level literals) and further inherited getBeliefbases.
 */
public interface IBeliefBase<T> extends IBeliefBaseElement, Iterable<ILiteral<T>>
{
    /**
     * adds a beliefbase-element to specified path (i.e. the path to an inherited beliefbase)
     * If the path is non-existing, it will be constructed.
     *
     * @param p_path    path for addition
     * @param p_element element to add
     * @return true if addition was successful
     */
    public boolean add(final CPath p_path, final IBeliefBaseElement p_element);

    /**
     * get beliefbase-elements
     *
     * @param p_path
     * @param p_name
     * @param p_class
     * @return
     */
    public Set<IBeliefBaseElement> getElements(final CPath p_path, final String p_name, final Class<?> p_class);

    /**
     * adds a collection of literals into an inherited beliefbase specified by a path
     *
     * @param p_path     path to beliefbase
     * @param p_literals literals to add
     */
    public boolean addAll(final CPath p_path, final Collection<ILiteral<T>> p_literals);

    /**
     * get a map of all inherited getBeliefbases with names from specified beliefbase
     *
     * @param p_path path to beliefbase
     * @return map of getBeliefbases
     */
    public Map<String, IBeliefBase<T>> getBeliefbases(final CPath... p_path);

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited getBeliefbases
     */
    public void clear(final CPath... p_path);

    /**
     * gets a beliefbase with position and name specified in path
     *
     * @param p_path path with name of the beliefbase as last element
     * @return specified beliefbase or null, if it was not found
     */
    public IBeliefBase get(final CPath p_path);

    /**
     * getter for beliefbase elements with path specified
     */
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements(final CPath p_path);


    /**
     * gets a beliefbase with position and name specified in path
     * if there is no beliefbase or the path is unknown, the path
     * will be constructed with a default beliefbase
     *
     * @param p_path       path with name of the beliefbase as last element
     * @param p_beliefbase default beliefbase
     * @return specified or default beliefbase
     */
    public IBeliefBase getOrDefault(final CPath p_path, final IBeliefBase<T> p_beliefbase);


    /**
     * returns top level literals
     *
     * @return
     */
    public Set<ILiteral<T>> getLiterals(final CPath... p_path);

    /**
     * removes specified beliefbase-element
     *
     * @param p_path    path beliefbase-element
     * @param p_element element to remove
     */
    public boolean remove(final CPath p_path, final IBeliefBaseElement p_element);

    /**
     * updates the beliefbase
     */
    public void update();
}
