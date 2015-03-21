/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.ui.web;


import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;


/**
 * class to call object methods with an URL access
 */
public class CVirtualMethod implements IVirtualLocation
{

    /**
     * method handle *
     */
    private final MethodHandle m_method;
    /**
     * object to call *
     */
    private final Object m_object;
    /**
     * URI *
     */
    private final String m_uri;


    /**
     * ctor
     *
     * @param p_object object
     * @param p_method method
     * @param p_uri    calling URI
     */
    public CVirtualMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uri )
    {
        m_object = p_object;
        m_method = p_method.getHandle();
        m_uri = p_uri;
    }

    @Override
    public boolean match( final String p_uri )
    {
        return m_uri.equals( p_uri );
    }

    @Override
    public Map<Object, Object> get( final NanoHTTPD.IHTTPSession p_session ) throws Throwable
    {
        try
        {
            final Object l_return = m_method.invoke( m_object, null );

            return ( l_return instanceof Map ) ? (Map) l_return : new HashMap()
            {{
                    put( "data", l_return );
                }};
        }
        catch ( Throwable l_throwable )
        {
            return new HashMap()
            {{
                    put( "error", l_throwable.getMessage() );
                }};
        }
    }

    @Override
    public CMarkdownRenderer getMarkDownRenderer()
    {
        return null;
    }

    @Override
    public final int hashCode()
    {
        return m_uri.hashCode();
    }


    @Override
    public final boolean equals( final Object p_object )
    {
        if ( p_object instanceof CVirtualLocation )
            return this.hashCode() == p_object.hashCode();

        return false;
    }

}
