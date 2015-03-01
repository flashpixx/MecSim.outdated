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

package de.tu_clausthal.in.mec.common;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * tree structure
 */
public class CTreeNode<T>
{

    /**
     * map with child names and nodes *
     */
    protected Map<String, CTreeNode<T>> m_childs = new HashMap<>();
    /**
     * reference to the parent node *
     */
    protected CTreeNode<T> m_parent = null;
    /**
     * data of the node *
     */
    protected T m_data = null;
    /**
     * ID / name of the node *
     */
    protected String m_id = null;


    /**
     * ctor
     *
     * @param p_identifier name of the node
     */
    public CTreeNode( final String p_identifier )
    {
        if ( ( p_identifier == null ) || ( p_identifier.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "idnotnull" ) );

        m_id = p_identifier;
    }


    /**
     * ctor
     *
     * @param p_identifier     name of the node
     * @param p_parent parent node
     */
    public CTreeNode( final String p_identifier, final CTreeNode<T> p_parent )
    {
        if ( ( p_identifier == null ) || ( p_identifier.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "idnotnull" ) );

        m_id = p_identifier;
        m_parent = p_parent;
        p_parent.m_childs.put( m_id, this );
    }


    /**
     * checks the existing of a path
     *
     * @param p_path path
     * @return existing flag
     */
    public final boolean pathexist( final CPath p_path )
    {
        CTreeNode<T> l_node = this;

        for ( CPath l_item : p_path )
        {
            if ( !l_node.m_childs.containsKey( l_item.getSuffix() ) )
                return false;

            l_node = l_node.m_childs.get( l_item.getSuffix() );
        }

        return true;
    }

    /**
     * returns the node
     *
     * @param p_path path of the node under the current
     * @return node
     */
    public final CTreeNode<T> traverseto( final CPath p_path )
    {
        return this.traverseto( p_path, true );
    }

    /**
     * returns the node
     *
     * @param p_path   path of the node under the current
     * @param p_create creates non-existing nodes
     * @return node
     */
    public final CTreeNode<T> traverseto( final CPath p_path, final boolean p_create )
    {
        CTreeNode<T> l_node = this;
        for ( CPath l_item : p_path )
        {
            if ( !l_node.m_childs.containsKey( l_item.getSuffix() ) )
                if ( !p_create )
                    return l_node;
                else
                    l_node.m_childs.put( l_item.getSuffix(), new CTreeNode<>( l_item.getSuffix() ) );

            l_node = l_node.m_childs.get( l_item.getSuffix() );
        }
        return l_node;
    }


    /**
     * returns a child under the current node
     *
     * @param p_name name
     * @return node
     */
    public final CTreeNode<T> getChild( final String p_name )
    {
        return m_childs.get( p_name );
    }


    /**
     * adds all childs of the path to the current node
     *
     * @param p_path path
     */
    public final void addChilds( final CPath p_path )
    {
        CTreeNode<T> l_sub = null;
        for ( CPath l_item : p_path )
            l_sub = new CTreeNode<T>( l_item.getSuffix(), l_sub );

        m_childs.put( p_path.getIndex( 0 ), l_sub );
    }


    /**
     * adds a list of path to the current node
     *
     * @param p_path collections of path
     */
    public final void addChilds( final Collection<CPath> p_path )
    {
        for ( CPath l_path : p_path )
            this.addChilds( l_path );
    }

    /**
     * remove a child node
     *
     * @param p_name ID
     * @return removed node
     */
    public final CTreeNode<T> removeChild( final String p_name )
    {
        return m_childs.remove( p_name );
    }

    /**
     * returns all child nodes
     *
     * @return collection of child nodes
     */
    public final Collection<CTreeNode<T>> getChilds()
    {
        return m_childs.values();
    }

    /**
     * gets all data of all subnodes
     *
     * @param p_withroot list with the root node
     * @return list of data
     */
    public final List<T> getSubData( final boolean p_withroot )
    {
        final List<T> l_list = new LinkedList<>();

        if ( p_withroot )
            l_list.add( m_data );

        for ( CTreeNode<T> l_child : m_childs.values() )
            l_list.addAll( l_child.getSubData( true ) );

        return l_list;
    }

    /**
     * gets all data of all subnodes
     *
     * @return list of data
     */
    public final List<T> getSubData()
    {
        return this.getSubData( true );
    }


    /**
     * checks if the data is null
     *
     * @return null flag
     */
    public final boolean isDataNull()
    {
        return m_data == null;
    }


    /**
     * returns the data of the current node
     *
     * @return data
     */
    public final T getData()
    {
        return m_data;
    }


    /**
     * sets the data to the current node
     *
     * @param p_data data
     */
    public final void setData( final T p_data )
    {
        m_data = p_data;
    }


    /**
     * get the full path to the node
     *
     * @return full path to this node
     */
    public final CPath getFQN()
    {
        return this.traverseUpToRoot( this );
    }


    /**
     * returns a set of all sub nodes
     *
     * @return set of nodes
     */
    public final Set<CPath> getSubTree()
    {
        return this.getSubTree( true );
    }

    /**
     * returns a set of all sub nodes
     *
     * @param p_withroot with root node
     * @return set of nodes
     */
    public final Set<CPath> getSubTree( final boolean p_withroot )
    {
        final Set<CPath> l_list = new HashSet<>();

        final CPath l_path = p_withroot ? new CPath( m_id ) : new CPath();
        this.getSubTreeRecursive( l_path, this, l_list );

        return l_list;

    }

    /**
     * traversing of the nodes
     *
     * @param p_path start node path
     * @param p_node start node
     * @param p_set  return set
     */
    protected final void getSubTreeRecursive( final CPath p_path, final CTreeNode<T> p_node, final Set<CPath> p_set )
    {
        if ( !p_path.isEmpty() )
            p_set.add( p_path );

        for ( Map.Entry<String, CTreeNode<T>> l_item : m_childs.entrySet() )
        {
            final CPath l_path = new CPath( p_path );
            l_path.pushback( l_item.getKey() );
            l_item.getValue().getSubTreeRecursive( l_path, l_item.getValue(), p_set );
        }
    }


    /**
     * get ID of the node
     *
     * @return ID
     */
    public final String getID()
    {
        return m_id;
    }


    /**
     * traverse up to root node
     *
     * @param p_node node
     * @return ID
     */
    protected final CPath traverseUpToRoot( final CTreeNode<T> p_node )
    {
        final CPath l_path = new CPath( p_node.m_id );

        if ( p_node.m_parent != null )
            l_path.pushfront( this.traverseUpToRoot( p_node.m_parent ) );

        return l_path;
    }

}
