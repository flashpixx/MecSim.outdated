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


import java.util.*;


/**
 * tree structure
 */
public class CNode<T>
{

    /**
     * map with child names and nodes *
     */
    protected Map<String, CNode<T>> m_childs = new HashMap();
    /**
     * reference to the parent node *
     */
    protected CNode<T> m_parent = null;
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
     * @param p_id name of the node
     */
    public CNode( String p_id )
    {
        if ( ( p_id == null ) || ( p_id.isEmpty() ) )
            throw new IllegalArgumentException( "ID need not to be null" );

        m_id = p_id;
    }


    /**
     * ctor
     *
     * @param p_id     name of the node
     * @param p_parent parent node
     */
    public CNode( String p_id, CNode<T> p_parent )
    {
        if ( ( p_id == null ) || ( p_id.isEmpty() ) )
            throw new IllegalArgumentException( "ID need not to be null" );

        m_id = p_id;
        m_parent = p_parent;
    }


    /**
     * checks the existing of a path
     *
     * @param p_path path
     * @return existing flag
     */
    public boolean pathexist( CPath p_path )
    {
        CNode<T> l_node = this;

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
    public CNode<T> traverseto( CPath p_path )
    {
        return traverseto( p_path, true );
    }

    /**
     * returns the node
     *
     * @param p_path   path of the node under the current
     * @param p_create creates non-existing nodes
     * @return node
     */
    public CNode<T> traverseto( CPath p_path, boolean p_create )
    {
        CNode<T> l_node = this;
        for ( CPath l_item : p_path )
        {
            if ( !l_node.m_childs.containsKey( l_item.getSuffix() ) )
                if ( p_create )
                    m_childs.put( l_item.getSuffix(), new CNode<T>( l_item.getSuffix() ) );
                else
                    return l_node;

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
    public CNode<T> getChild( String p_name )
    {
        return m_childs.get( p_name );
    }


    /**
     * adds all childs of the path to the current node
     *
     * @param p_path path
     */
    public void addChilds( CPath p_path )
    {
        CNode<T> l_sub = null;
        for ( CPath l_item : p_path )
            l_sub = new CNode<T>( l_item.getSuffix(), l_sub );

        m_childs.put( p_path.getIndex( 0 ), l_sub );
    }


    /**
     * adds a list of path to the current node
     *
     * @param p_path collections of path
     */
    public void addChilds( Collection<CPath> p_path )
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
    public CNode<T> removeChild( String p_name )
    {
        return m_childs.remove( p_name );
    }

    /**
     * returns all child nodes
     *
     * @return collection of child nodes
     */
    public Collection<CNode<T>> getChilds()
    {
        return m_childs.values();
    }

    /**
     * gets all data of all subnodes
     *
     * @param p_withroot list with the root node
     * @return list of data
     */
    public List<T> getSubData( boolean p_withroot )
    {
        List<T> l_list = new LinkedList();

        if ( p_withroot )
            l_list.add( this.getData() );

        for ( CNode<T> l_child : m_childs.values() )
            l_list.addAll( this.getSubData( true ) );

        return l_list;
    }

    /**
     * gets all data of all subnodes
     *
     * @return list of data
     */
    public List<T> getSubData()
    {
        return this.getSubData( true );
    }


    /**
     * checks if the data is null
     *
     * @return null flag
     */
    public boolean isDataNull()
    {
        return m_data == null;
    }


    /**
     * returns the data of the current node
     *
     * @return data
     */
    public T getData()
    {
        return m_data;
    }


    /**
     * sets the data to the current node
     *
     * @param p_data data
     */
    public void setData( T p_data )
    {
        m_data = p_data;
    }


    /**
     * get the full path to the node
     *
     * @return full path to this node
     */
    public CPath getFQN()
    {
        return traverseToRoot( this );
    }

    public Set<CPath> getSubTree( boolean p_withroot )
    {
        Set<CPath> l_list = new HashSet();

        CPath l_path = p_withroot ? new CPath( m_id ) : new CPath();
        this.getSubTreeRecursive( l_path, this, l_list );

        return l_list;

    }

    protected void getSubTreeRecursive( CPath p_path, CNode<T> p_node, Set<CPath> p_set )
    {
        if ( !p_path.isEmpty() )
            p_set.add( p_path );

        for ( Map.Entry<String, CNode<T>> l_item : m_childs.entrySet() )
        {
            CPath l_path = new CPath( p_path );
            l_path.pushback( l_item.getKey() );
            this.getSubTreeRecursive( l_path, l_item.getValue(), p_set );
        }
    }



    /**
     * get ID of the node
     *
     * @return ID
     */
    public String getID()
    {
        return m_id;
    }


    /**
     * traverse up to root node
     *
     * @param p_node node
     * @return ID
     */
    protected CPath traverseToRoot( CNode<T> p_node )
    {
        if ( m_parent != null )
        {
            CPath l_path = new CPath( m_id );
            l_path.pushfront( traverseToRoot( m_parent ) );
            return l_path;
        }


        return new CPath( m_id );
    }

}
