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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * generic term list for agent literals
 */
public class CTermList extends LinkedList<ITerm> implements ITermCollection
{
    /**
     * default ctor
     */
    public CTermList()
    {
        super();
    }
    /**
     * ctor - with initial elements specified
     *
     * @param p_collection collection containing initial elements
     */
    public CTermList( final Collection<ITerm> p_collection )
    {
        super( p_collection );
    }

    /**
     * check for the termlist's class type
     *
     * @param p_class matching class
     * @return true if List is assignable from matching class
     */
    @Override
    public boolean instanceOf(final Class<?> p_class)
    {
        return List.class.isAssignableFrom(p_class);
    }

}
