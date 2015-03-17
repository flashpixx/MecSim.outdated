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

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * class for name-based virtual-hosts
 */
public class CVirtualHost
{

    /**
     * default name-based host *
     */
    protected CName m_default = null;
    /**
     * list with additional name-based hosts *
     */
    protected Set<CName> m_names = new HashSet<>();


    /**
     * ctor
     *
     * @param p_default default / fallback host
     */
    public CVirtualHost( final CName p_default )
    {
        m_default = p_default;
    }

    /**
     * adds a new name-based host
     *
     * @param p_name name object
     */
    public void add( final CName p_name )
    {
        m_names.add( p_name );
    }


    /**
     * removes a name-based host
     *
     * @param p_name name object
     * @return boolean for correct remove
     */
    public boolean remove( final CName p_name )
    {
        return m_names.remove( p_name );
    }


    /**
     * gets the name-based host matched by the URI
     *
     * @param p_uri URI
     * @return name-based host or default host
     */
    public CName get( final String p_uri )
    {
        for ( CName l_item : m_names )
            if ( l_item.match( p_uri ) )
                return l_item;

        return m_default;
    }


    /**
     * definition of virtual-host *
     */
    static public class CName
    {

        /**
         * URI pattern *
         */
        private Pattern m_uri = Pattern.compile( "/*" );
        /**
         * vhost-directory *
         */
        private String m_directory = "";
        /**
         * index file *
         */
        private String m_index = "";


        /**
         * ctor
         *
         * @param p_directory base directory of the host
         * @param p_index     index file
         */
        public CName( final String p_directory, final String p_index )
        {
            m_index = p_index;
            m_directory = p_directory;
        }

        /**
         * ctor
         *
         * @param p_directory base directory of the host
         * @param p_index     index file
         * @param p_uri       regular expression of the URI
         */
        public CName( final String p_directory, final String p_index, final String p_uri )
        {
            this( p_directory, p_index );
            m_uri = Pattern.compile( p_uri );
        }


        /**
         * checks an URI if it matches the named-based host
         *
         * @param p_uri input URI
         * @return machting boolean
         */
        public boolean match( final String p_uri )
        {
            return m_uri.matcher( p_uri ).find();
        }


        /**
         * returns the path of the directory with the input file
         *
         * @param p_uri input URI
         * @return file path
         */
        public String getFile( final String p_uri ) throws MalformedURLException
        {
            return m_directory + "/" + m_uri.matcher( p_uri ).replaceAll( "" );
        }


        /**
         * returns the root file
         *
         * @return root file
         */
        public String getRoot() throws MalformedURLException
        {
            return m_directory + "/" + m_index;
        }


        @Override
        public final int hashCode()
        {
            return m_uri.hashCode();
        }


        @Override
        public final boolean equals( final Object p_object )
        {
            if ( p_object instanceof CVirtualHost )
                return this.hashCode() == p_object.hashCode();

            return false;
        }
    }

}