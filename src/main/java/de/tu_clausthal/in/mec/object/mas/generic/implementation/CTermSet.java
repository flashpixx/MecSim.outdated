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

package de.tu_clausthal.in.mec.object.mas.generic.implementation;

import de.tu_clausthal.in.mec.object.mas.generic.ITerm;
import de.tu_clausthal.in.mec.object.mas.generic.ITermCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * generic term set for agent literals
 *
 * @todo empty set initialization with static EMPTY_SET
 */
public class CTermSet extends HashSet<ITerm> implements ITermCollection
{
    /**
     * default ctor
     */
    public CTermSet()
    {
        super( 0 );
    }

    /**
     * ctor - with initial elements specified
     *
     * @param p_collection collection containing initial elements
     */
    public CTermSet( final Collection<ITerm> p_collection )
    {
        super( p_collection );
    }

    @Override
    public boolean instanceOf( final Class<?> p_class )
    {
        return Set.class.isAssignableFrom( p_class );
    }
}
