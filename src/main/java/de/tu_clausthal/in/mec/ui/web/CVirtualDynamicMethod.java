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

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebSocket;

import java.lang.invoke.MethodHandle;


/**
 * class to call methods continuously with websockets
 *
 * @see http://www.html5rocks.com/de/tutorials/websockets/basics/
 * @see https://github.com/NanoHttpd/nanohttpd/issues/74
 * @see https://github.com/NanoHttpd/nanohttpd/blob/master/websocket/src/main/java/fi/iki/elonen/NanoWebSocketServer.java
 */
public class CVirtualDynamicMethod implements IVirtualLocation
{
    /**
     * seperator
     */
    private static final String c_seperator = "/";
    /**
     * URI reg expression for filter
     */
    private static final String c_uriallowchars = "[^a-zA-Z0-9_/]+";
    /**
     * object
     */
    private final Object m_object;
    /**
     * method handle
     */
    private final MethodHandle m_method;
    /**
     * URI
     */
    private final String m_uri;


    /**
     * ctor
     *
     * @param p_object object
     * @param p_method method
     * @param p_uri calling URI
     */
    public CVirtualDynamicMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uri )
    {
        m_uri = p_uri.startsWith( c_seperator ) ? p_uri.replaceAll( c_uriallowchars, "" ) : c_seperator + p_uri.replaceAll( c_uriallowchars, "" );
        m_method = p_method.getHandle();
        m_object = p_object;

        CLogger.info( p_uri );
    }


    @Override
    public final boolean match( final String p_uri )
    {
        return m_uri.equals( p_uri );
    }

    @Override
    public final WebSocket get( final NanoHTTPD.IHTTPSession p_session ) throws Throwable
    {
        return new CWebSocket( p_session, m_object, m_method );
    }

    @Override
    public final CMarkdownRenderer getMarkDownRenderer()
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
