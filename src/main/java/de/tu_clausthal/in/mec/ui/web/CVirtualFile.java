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


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * virtual file with full path
 */
public class CVirtualFile implements IVirtualLocation
{
    /**
     * file *
     */
    private final File m_file;
    /**
     * url match *
     */
    private final String m_uri;


    /**
     * ctor
     *
     * @param p_file file
     * @param p_uri  regular expression of the URI
     */
    public CVirtualFile( final File p_file, final String p_uri )
    {
        m_file = p_file;
        m_uri = p_uri;

    }

    @Override
    public boolean match( String p_uri )
    {
        return p_uri.equals( m_uri );
    }

    @Override
    public URL getFile( String p_uri ) throws MalformedURLException
    {
        return m_file.toURI().toURL();
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
