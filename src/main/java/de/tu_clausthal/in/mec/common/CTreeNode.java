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


import org.apache.commons.lang3.tuple.Pair;

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
    private final Map<String, CTreeNode<T>> m_childs = new HashMap<>();
    /**
     * data of the node *
     */
    private T m_data;
    /**
     * ID / name of the node *
     */
    private final String m_id;
    /**
     * reference to the parent node *
     */
    private final CTreeNode<T> m_parent;

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
        m_parent = null;
    }

    /**
     * ctor
     *
     * @param p_identifier name of the node
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
     * adds a list of path to the current node
     *
     * @param p_names collections of names
     */
    public final void addChild( final Collection<String> p_names )
    {
        for ( final String l_item : p_names )
            this.addChild( l_item );
    }

    /**
     * adds all childs of the path to the current node
     *
     * @param p_name name of the child
     * @return tree node
     */
    public final CTreeNode<T> addChild( final String p_name )
    {
        if ( m_childs.containsKey( p_name ) )
            return m_childs.get( p_name );

        return new CTreeNode<T>( p_name, this );
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
     * returns all child nodes
     *
     * @return collection of child nodes
     */
    public final Collection<CTreeNode<T>> getChilds()
    {
        return m_childs.values();
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
     * adds a collection of pair with path and data
     *
     * @param p_data collection of pair with path and data
     */
    public final void setData( final Collection<Pair<CPath, T>> p_data )
    {
        for ( final Pair<CPath, T> l_item : p_data )
            this.getNode( l_item.getKey() ).setData( l_item.getValue() );
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
        return this.getFQN( this );
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
     * returns the node
     *
     * @param p_path path of the node
     * @return node
     */
    public final CTreeNode<T> getNode( final String p_path )
    {
        return this.getNode( this, new CPath( p_path ), 0 );
    }

    /**
     * returns the node
     *
     * @param p_path path of the node
     * @return node
     */
    public final CTreeNode<T> getNode( final CPath p_path )
    {
        return this.getNode( this, p_path, 0 );
    }

    /**
     * returns a set of all sub nodes
     *
     * @return set of nodes
     */
    public final Set<CPath> getTree()
    {
        return this.getTree( true );
    }

    /**
     * returns a set of all sub nodes
     *
     * @param p_withroot with root node
     * @return set of nodes
     */
    public final Set<CPath> getTree( final boolean p_withroot )
    {
        final Set<CPath> l_list = new HashSet<>();

        final CPath l_path = p_withroot ? new CPath( m_id ) : new CPath();
        this.getTree( l_path, this, l_list );

        return l_list;
    }

    /**
     * gets all data of all subnodes
     *
     * @return list of data
     */
    public final List<T> getTreeData()
    {
        return this.getTreeData( true );
    }

    /**
     * gets all data of all subnodes
     *
     * @param p_withroot list with the root node
     * @return list of data
     */
    public final List<T> getTreeData( final boolean p_withroot )
    {
        final List<T> l_list = new LinkedList<>();

        if ( p_withroot )
            l_list.add( m_data );

        for ( final CTreeNode<T> l_child : m_childs.values() )
            l_list.addAll( l_child.getTreeData( true ) );

        return l_list;
    }

    /**
     * check if a parent exists
     *
     * @return bool for parent exists
     */
    public final boolean hasParent()
    {
        return m_parent != null;
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
     * checks the existing of a path
     *
     * @param p_path path
     * @return existing flag
     */
    public final boolean pathExist( final String p_path )
    {
        return this.pathExist( new CPath( p_path ) );
    }

    /**
     * checks the existing of a path
     *
     * @param p_path path
     * @return existing flag
     */
    public final boolean pathExist( final CPath p_path )
    {
        CTreeNode<T> l_node = this;

        for ( final CPath l_item : p_path )
        {
            if ( !l_node.m_childs.containsKey( l_item.getSuffix() ) )
                return false;

            l_node = l_node.m_childs.get( l_item.getSuffix() );
        }

        return true;
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
     * in-order traversing
     *
     * @return tree structure
     */
    public final String toString()
    {
        String l_return = this.getFQN() + "   [" + m_data + "]";
        for ( final CTreeNode<T> l_item : m_childs.values() )
            l_return += "\n" + l_item;
        return l_return;
    }

    /**
     * traverse up to root node
     *
     * @param p_node node
     * @return ID
     */
    protected final CPath getFQN( final CTreeNode<T> p_node )
    {
        final CPath l_path = new CPath( p_node.m_id );

        if ( p_node.hasParent() )
            l_path.pushfront( this.getFQN( p_node.m_parent ) );

        return l_path;
    }

    /**
     * returns the node
     *
     * @param p_node current node
     * @param p_path path
     * @param p_index current path index
     * @return node
     */
    protected final CTreeNode<T> getNode( final CTreeNode<T> p_node, final CPath p_path, final int p_index )
    {
        if ( p_index < p_path.size() )
            if ( p_node.m_childs.containsKey( p_path.get( p_index ) ) )
                return p_node.getNode( p_node.m_childs.get( p_path.get( p_index ) ), p_path, p_index + 1 );
            else
                return p_node.getNode( new CTreeNode<T>( p_path.get( p_index ), p_node ), p_path, p_index + 1 );

        return p_node;
    }

    /**
     * traversing of the nodes
     *
     * @param p_path start node path
     * @param p_node start node
     * @param p_set return set
     */
    protected final void getTree( final CPath p_path, final CTreeNode<T> p_node, final Set<CPath> p_set )
    {
        if ( !p_path.isEmpty() )
            p_set.add( p_path );

        for ( final Map.Entry<String, CTreeNode<T>> l_item : m_childs.entrySet() )
        {
            final CPath l_path = new CPath( p_path );
            l_path.pushback( l_item.getKey() );
            l_item.getValue().getTree( l_path, l_item.getValue(), p_set );
        }
    }

}
