/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * binding belief storage
 */
public abstract class IBindStorage<N, M> extends IOneTimeStorage<N, M>
{
    /**
     * field filter
     */
    protected static final CFieldFilter c_filter = new CFieldFilter();
    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    protected final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();


    /**
     * default ctor
     */
    public IBindStorage()
    {
    }

    /**
     * ctor - bind an object
     *
     * @param p_name name / annotation of the bind object
     * @param p_object bind object
     */
    public IBindStorage( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object );
    }

    /**
     * adds / binds an object
     *
     * @param p_name name / annotation of the object
     * @param p_object object
     */
    public final void push( final String p_name, final Object p_object )
    {
        m_bind.put(
                p_name, new ImmutablePair<Object, Map<String, CReflection.CGetSet>>(
                        p_object, CReflection.getClassFields(
                        p_object.getClass(), c_filter
                )
                )
        );
    }

    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public final void removeBinding( final String p_name )
    {
        m_bind.remove( p_name );
    }

}
