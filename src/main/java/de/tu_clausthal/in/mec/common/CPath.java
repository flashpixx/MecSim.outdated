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
 **/

package de.tu_clausthal.in.mec.common;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * class to create a path structure
 */
public class CPath implements Iterable<CPath>
{
    /**
     * separator of the path elements *
     */
    protected String m_separator = "/";

    /**
     * list with path parts *
     */
    private List<String> m_path = new ArrayList<>();


    /**
     * copy-ctor
     *
     * @param p_path path object
     */
    public CPath( final CPath p_path )
    {
        m_separator = p_path.m_separator;
        m_path.addAll( p_path.m_path );
    }

    /**
     * ctor
     *
     * @param p_varargs path component
     */
    public CPath( final String... p_varargs )
    {
        if ( ( p_varargs != null ) && ( p_varargs.length > 0 ) )
            this.initialize( StringUtils.join( p_varargs, m_separator ) );
    }


    /**
     * creates a path object from different items
     *
     * @param p_varargs list of strings (first element is the separator)
     * @return path object
     */
    public static CPath createPath( final String... p_varargs )
    {
        if ( p_varargs.length < 2 )
            throw new IllegalArgumentException( CCommon.getResourceString( CPath.class, "createpath" ) );

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
     * reverse path
     */
    public void reverse()
    {
        Collections.reverse( m_path );
    }


    /**
     * adds a path at the end
     *
     * @param p_path path
     */
    public void pushback( final CPath p_path )
    {
        m_path.addAll( p_path.m_path );
    }


    /**
     * adds a path at the end
     *
     * @param p_path string path
     */
    public void pushback( final String p_path )
    {
        this.pushback( new CPath( p_path ) );
    }


    /**
     * adds a path to the front of the path
     *
     * @param p_path path
     */
    public void pushfront( final CPath p_path )
    {
        final ArrayList<String> l_path = new ArrayList<>( p_path.m_path );
        l_path.addAll( m_path );
        m_path = l_path;
    }


    /**
     * adds a path at the front
     *
     * @param p_path string path
     */
    public void pushfront( final String p_path )
    {
        this.pushfront( new CPath( p_path ) );
    }


    /**
     * returns the separator
     *
     * @return separator
     */
    public String getSeparator()
    {
        return m_separator;
    }

    /**
     * sets the separator
     *
     * @param p_separator separator
     */
    public void setSeparator( final String p_separator )
    {
        if ( ( p_separator == null ) || ( p_separator.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "separatornotempty" ) );

        m_separator = p_separator;
    }

    /**
     * splits the string data
     *
     * @param p_fqn full path
     */
    private void initialize( final String p_fqn )
    {
        for ( String l_item : p_fqn.split( m_separator ) )
            if ( !l_item.isEmpty() ) m_path.add( l_item );

        if ( m_path.size() == 0 ) throw new IllegalArgumentException( CCommon.getResourceString( this, "pathempty" ) );
    }

    /**
     * returns the full path as string
     *
     * @return string path
     */
    public String getPath()
    {
        return StringUtils.join( m_path, m_separator );
    }


    /**
     * returns the full path as string with an individual separator
     *
     * @param p_separator separator
     * @return string path
     */
    public String getPath( final String p_separator )
    {
        return StringUtils.join( m_path, p_separator );
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
     * creates a path of the indices
     *
     * @param p_fromIndex start index
     * @param p_toIndex   end index (exclusive)
     * @return path
     */
    public CPath getSubPath( final int p_fromIndex, final int p_toIndex )
    {
        final CPath l_path = new CPath();
        l_path.m_separator = m_separator;
        l_path.m_path.addAll( m_path.subList( p_fromIndex, p_toIndex ) );
        return l_path;
    }


    /**
     * remove the suffix from the path
     *
     * @return last item of the path
     */
    public String removeSuffix()
    {
        final String l_suffix = this.getSuffix();
        if ( m_path.size() > 0 ) m_path.remove( m_path.size() - 1 );
        return l_suffix;
    }

    /**
     * returns an part of the path
     *
     * @param p_index index position
     * @return element
     */
    public String get( final int p_index )
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
                return new CPath( CCommon.CollectionToArray( String[].class, m_path.subList( 0, ++m_index ) ) );
            }
        };
    }

    @Override
    public int hashCode()
    {
        return this.getPath().hashCode();
    }


    @Override
    public final boolean equals( final Object p_object )
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
