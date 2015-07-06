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

import java.util.Map;
import java.util.Set;


/**
 * The beliefbase interface for generic getBeliefbases. A single beliefbase contains beliefs
 * as literals (the top-level literals) and further inherited getBeliefbases.
 *
 * @param <T> language specific literal
 */
public interface Old_IBeliefBase<T> extends Old_IBeliefBaseElement, Iterable<ILiteral<T>>
{
    /**
     * adds a beliefbase-element to specified path (i.e. the path to an inherited beliefbase)
     * If the path is non-existing, it will be constructed.
     *
     * @param p_element element to add
     * @return true if addition was successful
     */
    public boolean add( final Old_IBeliefBaseElement p_element );

    /**
     * empties the whole beliefbase, i.e. the top-level literals
     * and all the literals in inherited getBeliefbases
     */
    public void clear( final CPath... p_path );

    /**
     * gets a beliefbase with position and name specified in path
     *
     * @param p_path path with name of the beliefbase as last element
     * @return specified beliefbase or null, if it was not found
     */
    public Old_IBeliefBase get( final CPath p_path );

    /**
     * get a map of all inherited getBeliefbases with names from specified beliefbase
     *
     * @param p_path path to beliefbase
     * @return map of getBeliefbases
     */
    public Map<String, Old_IBeliefBase<T>> getBeliefbases( final CPath... p_path );

    /**
     * get beliefbase-elements
     *
     * @param p_path path to inherited beliefbase
     * @param p_name key of beliefbase-elements (i.e. literal functor or beliefbase name)
     * @param p_class class of beliefbase-elements (e.g. ILiteral or IBeliefbase)
     * @return set of beliefbase-elements with specified attributes
     */
    public Set<Old_IBeliefBaseElement> getElements( final CPath p_path, final String p_name, final Class<?> p_class );

    /**
     * getter for beliefbase elements with path specified
     *
     * @param p_path path to inherited beliefbase
     * @return beliefbase-elements
     */
    public Map<String, Map<Class<?>, Set<Old_IBeliefBaseElement>>> getElements( final CPath p_path );

    /**
     * returns top level literals
     *
     * @return top-level literals
     */
    public Set<ILiteral<T>> getLiterals( final CPath... p_path );

    /**
     * gets a beliefbase with position and name specified in path
     * if there is no beliefbase or the path is unknown, the path
     * will be constructed with a default beliefbase
     *
     * @param p_path path with name of the beliefbase as last element
     * @param p_beliefbase default beliefbase
     * @return specified or default beliefbase
     */
    public Old_IBeliefBase getOrDefault( final CPath p_path, final Old_IBeliefBase<T> p_beliefbase );

    /**
     * getter for path
     */
    public CPath getPath();

    /**
     * setter for path
     *
     * @param p_path
     */
    public void setPath( final CPath p_path );

    /**
     * removes specified beliefbase-element
     *
     * @param p_path path beliefbase-element
     * @param p_element element to remove
     */
    public boolean remove( final CPath p_path, final Old_IBeliefBaseElement p_element );

    /**
     * updates the beliefbase
     */
    public void update();

}
