/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.common;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.lang.invoke.MethodHandle;
import java.util.*;


/**
 * cache class of method invoker structure *
 */
public class CMethodCache<T>
{

    /**
     * object reference *
     */
    protected T m_object = null;
    /**
     * forbidden names *
     */
    protected Set<String> m_forbidden = new HashSet();
    /**
     * cache map *
     */
    protected Map<ImmutablePair<String, Class[]>, MethodHandle> m_cache = new HashMap();


    /**
     * ctor
     *
     * @param p_bind bind object
     */
    public CMethodCache( T p_bind )
    {
        m_object = p_bind;
    }

    /**
     * object getter
     *
     * @return bind object
     */
    public T getObject()
    {
        return m_object;
    }


    /**
     * get forbidden set
     *
     * @return set with names
     */
    public Set<String> getForbidden()
    {
        return m_forbidden;
    }


    /**
     * get the method handle
     *
     * @param p_methodname method name
     * @return handle
     */
    public MethodHandle getHandle( String p_methodname ) throws IllegalAccessException
    {
        return getHandle( p_methodname, null );
    }


    /**
     * get the method handle
     *
     * @param p_methodname method name
     * @param p_arguments  method arguments
     * @return handle
     */
    public MethodHandle getHandle( String p_methodname, Class[] p_arguments ) throws IllegalAccessException
    {
        ImmutablePair<String, Class[]> l_method = new ImmutablePair<String, Class[]>( p_methodname, p_arguments );
        if ( m_cache.containsKey( l_method ) )
            return m_cache.get( l_method );

        for ( String l_item : m_forbidden )
            if ( p_methodname.equals( l_item ) )
                throw new IllegalAccessException( CCommon.getResouceString( this, "access", p_methodname ) );

        MethodHandle l_handle = CCommon.getClassMethod( m_object.getClass(), p_methodname, p_arguments );
        m_cache.put( l_method, l_handle );
        return l_handle;
    }

}
