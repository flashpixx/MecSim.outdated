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

import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;
import de.tu_clausthal.in.mec.object.mas.generic.IWorldAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * action to call a method from an
 * agent on any object
 */
public abstract class IMethodBind<N, M> implements IWorldAction<N, M>
{
    /**
     * bind object *
     */
    protected final Map<String, CReflection.CMethodCache<Object>> m_bind = new HashMap<>();
    /**
     * method filter
     */
    private static final CMethodFilter c_filter = new CMethodFilter();


    /**
     * ctor - default
     */
    public IMethodBind()
    {
    }

    /**
     * ctor bind an object
     *
     * @param p_name name of the binding object
     * @param p_object object
     */
    public IMethodBind( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object );
    }

    /**
     * adds a new bind object
     *
     * @param p_name name
     * @param p_object bind object
     */
    public final void push( final String p_name, final Object p_object )
    {
        m_bind.put( p_name, new CReflection.CMethodCache( p_object, c_filter ) );
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
        return "mecsim_invokemethod";
    }

    /**
     * invokes a method
     *
     * @param p_object invoking object
     * @param p_methodname method name
     * @param p_returntype return type
     * @param p_argumenttypes argument type list
     * @param p_argumentdata argument data list
     * @return return value
     *
     * @throws Throwable on invoking error
     */
    protected final Object invokeMethod( final CReflection.CMethodCache<Object> p_object, final String p_methodname, final Class<?> p_returntype,
            final List<Class<?>> p_argumenttypes, final List<Object> p_argumentdata
    ) throws Throwable
    {
        // method has got any arguments
        if ( ( !p_argumentdata.isEmpty() ) && ( !p_argumenttypes.isEmpty() ) )
        {
            // convert argumenttypes to array
            final Class<?>[] l_argumenttypes = new Class[p_argumenttypes.size()];
            p_argumenttypes.toArray( l_argumenttypes );

            // add to invoker-argument list and call invoker
            return p_returntype.cast(
                    p_object.get( p_methodname, l_argumenttypes ).getHandle().invokeWithArguments(
                            new LinkedList<Object>()
                            {{
                                    add( p_object.getObject() );
                                    addAll( p_argumentdata );
                                }}
                    )
            );
        }

        // call invoker
        return p_returntype.cast( p_object.get( p_methodname ).getHandle().invoke( p_object.getObject() ) );
    }

}
