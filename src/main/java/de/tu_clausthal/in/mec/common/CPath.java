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

package de.tu_clausthal.in.mec.common;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * class to create a path structure
 */
public class CPath implements Iterable<CPath>
{
    /**
     * empty path
     **/
    public static final CPath EMPTY = new CPath();

    public static final String DEFAULTSEPERATOR = "/";

    /**
     * list with path parts *
     */
    private List<String> m_path = new ArrayList<>();
    /**
     * separator of the path elements *
     */
    private String m_separator = DEFAULTSEPERATOR;

    /**
     * copy-ctor with arguments
     *
     * @param p_path path object
     * @param p_varargs string arguments
     */
    public CPath( final CPath p_path, final String... p_varargs )
    {
        this( p_path );
        m_path.addAll( Arrays.asList( p_varargs ) );
    }


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

    public static CPath createSplitPath( final String... p_varargs )
    {
        if ( p_varargs.length < 3 )
            throw new IllegalArgumentException( CCommon.getResourceString( CPath.class, "createpath" ) );

        final List<String> l_pathlist = new LinkedList<>();
        l_pathlist.add( p_varargs[ 1 ] );

        for ( int i = 2; i < p_varargs.length; ++i )
            l_pathlist.addAll( Arrays.asList( StringUtils.split( p_varargs[ i ], p_varargs[ 0 ] ) ) );

        return createPath( (String[]) l_pathlist.toArray() );
    }

    /**
     * appends a path at the current and returns a new object
     *
     * @param p_path path
     * @return new path
     */
    public CPath append( final CPath p_path )
    {
        final CPath l_path = new CPath( this );
        l_path.pushback( p_path );
        return l_path;
    }

    /**
     * appends a string at the current path and returns the new object
     *
     * @param p_path string with path
     * @return new path
     */
    public CPath append( final String p_path )
    {
        final CPath l_path = new CPath( this );
        l_path.pushback( p_path );
        return l_path;
    }

    /**
     * check of a path ends with another path
     *
     * @param p_path path
     * @return boolean
     */
    public final boolean endsWith( final CPath p_path )
    {
        if ( p_path.size() > m_path.size() )
            return false;

        for ( int i = m_path.size() - p_path.size(); i < m_path.size(); ++i )
            if ( m_path.get( i ) != p_path.m_path.get( i ) )
                return false;

        return true;
    }

    /**
     * returns an part of the path
     *
     * @param p_index index position
     * @return element
     */
    public final String get( final int p_index )
    {
        return m_path.get( p_index );
    }

    /**
     * returns the full path as string with an individual separator
     *
     * @param p_separator separator
     * @return string path
     */
    public final String getPath( final String p_separator )
    {
        return StringUtils.join( m_path, p_separator );
    }

    /**
     * returns the full path as string
     *
     * @return string path
     */
    public final String getPath()
    {
        return StringUtils.join( m_path, m_separator );
    }

    /**
     * returns the separator
     *
     * @return separator
     */
    public final String getSeparator()
    {
        return m_separator;
    }

    /**
     * sets the separator
     *
     * @param p_separator separator
     */
    public final void setSeparator( final String p_separator )
    {
        if ( ( p_separator == null ) || ( p_separator.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "separatornotempty" ) );

        m_separator = p_separator;
    }

    /**
     * creates a path of the start index until the end
     *
     * @param p_fromIndex start index
     * @return path
     */
    public final CPath getSubPath( final int p_fromIndex )
    {
        return this.getSubPath( p_fromIndex, this.size() );
    }

    /**
     * creates a path of the indices
     *
     * @param p_fromIndex start index
     * @param p_toIndex end index (exclusive)
     * @return path
     */
    public final CPath getSubPath( final int p_fromIndex, final int p_toIndex )
    {
        final CPath l_path = new CPath();
        l_path.m_separator = m_separator;
        l_path.m_path.addAll( m_path.subList( p_fromIndex, p_toIndex ) );
        return l_path;
    }

    /**
     * returns the last part of the path
     *
     * @return string
     */
    public final String getSuffix()
    {
        return m_path.get( m_path.size() == 0 ? 0 : m_path.size() - 1 );
    }

    @Override
    public final int hashCode()
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
    public final String toString()
    {
        return this.getPath();
    }

    /**
     * splits the string data
     *
     * @param p_fqn full path
     */
    private void initialize( final String p_fqn )
    {
        for ( final String l_item : p_fqn.split( m_separator ) )
            if ( !l_item.isEmpty() )
                m_path.add( l_item );

        if ( m_path.size() == 0 )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "pathempty" ) );
    }

    /**
     * check if the path is empty
     *
     * @return empty flag
     */
    public final boolean isEmpty()
    {
        return m_path.isEmpty();
    }

    @Override
    public final Iterator<CPath> iterator()
    {
        return new Iterator<CPath>()
        {
            private int m_index;

            @Override
            public boolean hasNext()
            {
                return m_index < m_path.size();
            }

            @Override
            public CPath next()
            {
                return new CPath( CCommon.convertCollectionToArray( String[].class, m_path.subList( 0, ++m_index ) ) );
            }
        };
    }

    /**
     * adds a path at the end
     *
     * @param p_path path
     */
    public final void pushback( final CPath p_path )
    {
        m_path.addAll( p_path.m_path );
    }

    /**
     * adds a path at the end
     *
     * @param p_path string path
     */
    public final void pushback( final String p_path )
    {
        this.pushback( new CPath( p_path ) );
    }

    /**
     * adds a path at the front
     *
     * @param p_path string path
     */
    public final void pushfront( final String p_path )
    {
        this.pushfront( new CPath( p_path ) );
    }

    /**
     * adds a path to the front of the path
     *
     * @param p_path path
     */
    public final void pushfront( final CPath p_path )
    {
        final ArrayList<String> l_path = new ArrayList<>( p_path.m_path );
        l_path.addAll( m_path );
        m_path = l_path;
    }

    /**
     * remove the suffix from the path
     *
     * @return last item of the path
     */
    public final String removeSuffix()
    {
        if ( this.isEmpty() )
            return null;

        final String l_suffix = this.getSuffix();
        if ( m_path.size() > 0 )
            m_path.remove( m_path.size() - 1 );
        return l_suffix;
    }

    /**
     * reverse path
     */
    public final void reverse()
    {
        Collections.reverse( m_path );
    }

    /**
     * returns the number of path elements
     *
     * @return size
     */
    public final int size()
    {
        return m_path.size();
    }

    /**
     * check of a path starts with another path
     *
     * @param p_path path
     * @return boolean
     */
    public final boolean startsWith( final CPath p_path )
    {
        if ( m_path.size() < p_path.size() )
            return false;

        for ( int i = 0; i < m_path.size(); ++i )
            if ( m_path.get( i ) != p_path.m_path.get( i ) )
                return false;

        return true;
    }

}
