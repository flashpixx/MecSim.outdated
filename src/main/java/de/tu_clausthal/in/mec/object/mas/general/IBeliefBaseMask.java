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

import java.util.Set;


/**
 * mask of the path
 */
public interface IBeliefBaseMask<T> extends IBeliefBaseAction<T>
{

    /**
     * adds a literal in the current structure
     *
     * @param p_path path
     * @param p_literal literal
     */
    void add( final CPath p_path, final ILiteral<T> p_literal );

    /**
     * adds a mask in the current structure
     *
     * @param p_path path
     * @param p_mask mask
     */
    void add( final CPath p_path, final IBeliefBaseMask<T> p_mask );

    /**
     * adds a mask in the current structure
     *
     * @param p_path path
     * @param p_mask mask
     * @param p_generator beliefbase generator if beliefbase not exists
     */
    void add( final CPath p_path, final IBeliefBaseMask<T> p_mask, final IGenerator<T> p_generator );

    /**
     * adds a literal in the current structure
     *
     * @param p_path path
     * @param p_literal literal
     * @param p_generator beliefbase generator if beliefbase not exists
     */
    void add( final CPath p_path, final ILiteral<T> p_literal, final IGenerator<T> p_generator );

    /**
     * clones the current mask
     *
     * @param p_parent new parent
     * @return new mask object
     */
    public IBeliefBaseMask<T> clone( final IBeliefBaseMask<T> p_parent );

    /**
     * gets a list of all literals
     * of the path
     *
     * @param p_path path
     * @return set with literal
     */
    public Set<ILiteral<T>> get( final CPath p_path );

    /**
     * gets a list of all literals
     *
     * @return set with literals
     */
    public Set<ILiteral<T>> get();

    /**
     * returns the full path
     *
     * @return path
     */
    public CPath getFQNPath();

    /**
     * returns only the element name
     *
     * @return name
     */
    public String getName();

    /**
     * returns the parent of the mask
     *
     * @return parent object or null
     */
    public IBeliefBaseMask<T> getParent();


    /**
     * interface for generating non-existing beliefbases
     */
    public interface IGenerator<Q>
    {
        public IBeliefBaseMask<Q> create( final String p_name );
    }
}
