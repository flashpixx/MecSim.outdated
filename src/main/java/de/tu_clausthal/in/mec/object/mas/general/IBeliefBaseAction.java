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
 * interface for equal method names on masks and beliefbases
 */
public interface IBeliefBaseAction<T> extends Iterable<ILiteral<T>>
{

    /**
     * adds a literal in the current structure
     *
     * @param p_literal literal
     */
    public void add( final ILiteral<T> p_literal );

    /**
     * adds a mask into the current structure
     *
     * @param p_mask mask
     */
    public void add( final IBeliefBaseMask<T> p_mask );

    /**
     * clears all elements
     */
    public void clear();

    /**
     * returns a new mask of the belief base
     *
     * @param p_name name of the mask
     * @return mask
     */
    public IBeliefBaseMask<T> createMask( final String p_name );

    /**
     * returns a new mask of the belief base
     *
     * @param p_name name of the mask
     * @param p_parent parent mask
     * @return mask
     */
    public IBeliefBaseMask<T> createMask( final String p_name, final IBeliefBaseMask<T> p_parent );

    /**
     * returns the storage of the beliefbase
     *
     * @return storage
     *
     * @tparam L typecast
     */
    public <L extends IBeliefStorage<ILiteral<T>, IBeliefBaseMask<T>>> L getStorage();

    /**
     * checks if the structure empty
     *
     * @return empty boolean
     */
    public boolean isEmpty();

    /**
     * removes a mask in the current structure
     *
     * @param p_mask mask
     */
    public void remove( final IBeliefBaseMask<T> p_mask );

    /**
     * removes a literal in the current structure
     *
     * @param p_literal literal
     */
    public void remove( final ILiteral<T> p_literal );

    /**
     * updates all items
     */
    public void update();
}
