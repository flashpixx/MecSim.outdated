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
import de.tu_clausthal.in.mec.common.CCommon;
import fi.iki.elonen.NanoHTTPD;

import java.net.URL;


/**
 * virtual file with full path
 */
public class CVirtualFile implements IVirtualLocation
{
    /**
     * seperator
     */
    private static final String c_seperator = "/";
    /**
     * URI reg expression for filter
     */
    private static final String c_allowchar = "[^a-zA-Z0-9_/]+";
    /**
     * file
     */
    private final URL m_file;
    /**
     * url match *
     */
    private final String m_uri;


    /**
     * ctor
     *
     * @param p_file file
     * @param p_uri regular expression of the URI
     */
    public CVirtualFile( final URL p_file, final String p_uri )
    {
        if ( ( p_uri == null ) || ( p_uri.isEmpty() ) || ( p_uri.endsWith( c_seperator ) ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "trailingslashempty", p_uri ) );
        if ( p_file == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "fileisnull", p_uri ) );

        m_file = p_file;
        m_uri = p_uri.startsWith( c_seperator ) ? p_uri.replaceAll( c_allowchar, "" ) : c_seperator + p_uri.replaceAll( c_allowchar, "" );

        CLogger.info( p_uri );
    }

    @Override
    public final boolean match( final String p_uri )
    {
        return p_uri.equals( m_uri );
    }

    @Override
    public final URL get( final NanoHTTPD.IHTTPSession p_session )
    {
        return m_file;
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
