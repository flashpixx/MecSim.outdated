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
import de.tu_clausthal.in.mec.object.mas.generic.IWorldAction;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * action to set a property value
 * of an object from an agent
 */
public abstract class IPropertyBind<N, M> implements IWorldAction<N, M>
{
    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    protected final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();
    /**
     * add field filter *
     */
    private static final CFieldFilter c_filter = new CFieldFilter();


    /**
     * ctor - default
     */
    public IPropertyBind()
    {
    }

    /**
     * ctor bind an object
     *
     * @param p_name name / annotation of the bind object
     * @param p_object bind object
     */
    public IPropertyBind( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object, null );
    }


    /**
     * ctor
     *
     * @param p_name name / annotation of the bind object
     * @param p_object bind object
     * @param p_forbiddennames set with forbidden field names of the object fields
     */
    public IPropertyBind( final String p_name, final Object p_object, final Set<String> p_forbiddennames )
    {
        this.push( p_name, p_object, p_forbiddennames );
    }


    /**
     * adds / binds an object
     *
     * @param p_name name / annotation of the object
     * @param p_object object
     */
    public final void push( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object, null );
    }

    /**
     * adds a new bind object
     *
     * @param p_name name / annotation of the object
     * @param p_object object
     * @param p_forbiddennames set with forbidden names of the object fields
     */
    public final void push( final String p_name, final Object p_object, final Set<String> p_forbiddennames )
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
    public final void remove( final String p_name )
    {
        m_bind.remove( p_name );
    }


    @Override
    public final String getName()
    {
        return "mecsim_propertyset";
    }

}
