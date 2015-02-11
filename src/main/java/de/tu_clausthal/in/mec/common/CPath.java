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


import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * class to create a path structure
 */
public class CPath implements Iterable<CPath>
{
    /**
     * seperator of the path elements *
     */
    protected String m_seperator = "/";

    /**
     * list with path parts *
     */
    private List<String> m_path = new ArrayList<>();


    /**
     * copy-ctor
     *
     * @param p_path path object
     */
    public CPath( CPath p_path )
    {
        m_seperator = p_path.m_seperator;
        m_path.addAll( p_path.m_path );
    }

    /**
     * ctor
     *
     * @param p_varargs path component
     */
    public CPath( String... p_varargs )
    {
        if ( ( p_varargs != null ) && ( p_varargs.length > 0 ) )
            this.initialize( StringUtils.join( p_varargs, m_seperator ) );
    }


    /**
     * creates a path object from different items
     *
     * @param p_varargs list of strings (first element is the seperator)
     * @return path object
     */
    public static CPath createPath( String... p_varargs )
    {
        if ( p_varargs.length < 2 )
            throw new IllegalArgumentException( CCommon.getResouceString( CPath.class, "createpath" ) );

        return new CPath( StringUtils.join( p_varargs[0], p_varargs, 1 ) );
    }

    /**
     * check if the path is empty
     *
     * @return empty flag
     */
    public boolean isEmpty()
    {
        return m_path.isEmpty();
    }


    /**
     * adds a path at the end
     *
     * @param p_path path
     */
    public void pushback( CPath p_path )
    {
        m_path.addAll( p_path.m_path );
    }


    /**
     * adds a path at the end
     *
     * @param p_path string path
     */
    public void pushback( String p_path )
    {
        this.pushback( new CPath( p_path ) );
    }


    /**
     * adds a path to the front of the path
     *
     * @param p_path path
     */
    public void pushfront( CPath p_path )
    {
        ArrayList<String> l_path = new ArrayList<>( p_path.m_path );
        l_path.addAll( m_path );
        m_path = l_path;
    }


    /**
     * adds a path at the front
     *
     * @param p_path string path
     */
    public void pushfront( String p_path )
    {
        this.pushfront( new CPath( p_path ) );
    }


    /**
     * returns the seperator
     *
     * @return seperator
     */
    public String getSeperator()
    {
        return m_seperator;
    }

    /**
     * sets the seperator
     *
     * @param p_seperator seperator
     */
    public void setSeperator( String p_seperator )
    {
        if ( ( p_seperator == null ) || ( p_seperator.isEmpty() ) )
            throw new IllegalArgumentException( "seperator need not to be empty" );

        m_seperator = p_seperator;
    }

    /**
     * splits the string data
     *
     * @param p_fqn full path
     */
    private void initialize( String p_fqn )
    {
        for ( String l_item : p_fqn.split( m_seperator ) )
            if ( !l_item.isEmpty() )
                m_path.add( l_item );

        if ( m_path.size() == 0 )
            throw new IllegalArgumentException( "path is empty" );
    }

    /**
     * returns the full path as string
     *
     * @return string path
     */
    public String getPath()
    {
        return StringUtils.join( m_path, m_seperator );
    }

    /**
     * returns the last part of the path
     *
     * @return string
     */
    public String getSuffix()
    {
        return m_path.get( m_path.size() == 0 ? 0 : m_path.size() - 1 );
    }


    /**
     * remove the suffix from the path
     *
     * @return last item of the path
     */
    public String removeSuffix()
    {

        String l_suffix = this.getSuffix();
        if ( m_path.size() > 0 )
            m_path.remove( m_path.size() - 1 );
        return l_suffix;
    }

    /**
     * returns an part of the path
     *
     * @param p_index index position
     * @return element
     */
    public String getIndex( int p_index )
    {
        return m_path.get( p_index );
    }

    /**
     * returns the number of path elements
     *
     * @return size
     */
    public int size()
    {
        return m_path.size();
    }

    @Override
    public Iterator<CPath> iterator()
    {
        return new Iterator<CPath>()
        {
            private int m_index = 0;

            @Override
            public boolean hasNext()
            {
                return m_index < m_path.size();
            }

            @Override
            public CPath next()
            {
                return new CPath( CCommon.ColletionToArray( String[].class, m_path.subList( 0, ++m_index ) ) );
            }
        };
    }

    @Override
    public int hashCode()
    {
        return this.getPath().hashCode();
    }

    @Override
    public boolean equals( Object p_object )
    {
        if ( ( p_object instanceof String ) || ( p_object instanceof CPath ) )
            return this.hashCode() == p_object.hashCode();

        return false;
    }

    @Override
    public String toString()
    {
        return this.getPath();
    }

}
