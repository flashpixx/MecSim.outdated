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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.common.CPath;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * structure to store all menu items
 *
 * @todo create menu elements with different labels and path structure
 */
public class CMenuStorage
{

    /**
     * stores path and menu / items *
     */
    protected Map<CPath, JComponent> m_pathobject = new HashMap();
    /**
     * map between object and path
     */
    protected Map<JComponent, CPath> m_objectpath = new HashMap();


    /**
     * creates a full path and returns the latest element
     *
     * @param p_path full path (without item)
     * @return menu object
     */
    protected JMenu GetOrCreatePath( CPath p_path )
    {
        JComponent l_parent = null;
        for ( CPath l_item : p_path )
        {
            JComponent l_current = m_pathobject.get( l_item );

            if ( l_current == null )
            {
                l_current = new JMenu( l_item.getSuffix() );
                if ( l_parent != null )
                    l_parent.add( l_current );

                m_pathobject.put( l_item, l_current );
                m_objectpath.put( l_current, l_item );
            }

            if ( !( l_current instanceof JMenu ) )
                throw new IllegalStateException( "item on [" + l_item + "] is not a JMenu" );

            l_parent = l_current;
        }

        return (JMenu) l_parent;
    }


    // --- getter for data ---------------------------------------------------------------------------------------------

    /**
     * returns the full menu list
     *
     * @return JMenu list
     */
    public Collection<JComponent> getRoot()
    {
        List<JComponent> l_root = new ArrayList();

        for ( Map.Entry<CPath, JComponent> l_item : m_pathobject.entrySet() )
            if ( l_item.getKey().size() == 1 )
                l_root.add( l_item.getValue() );

        return l_root;
    }

    /**
     * returns all elements within a path
     *
     * @param p_path prefix path
     * @return list elements
     */
    public Collection<JComponent> getElements( String p_path )
    {
        CPath l_path = new CPath( p_path );

        List<JComponent> l_elements = new ArrayList();

        for ( Map.Entry<CPath, JComponent> l_item : m_pathobject.entrySet() )
            if ( l_item.getKey().getPath().startsWith( l_path.getPath() ) )
                l_elements.add( l_item.getValue() );

        return l_elements;
    }


    /**
     * returns all elements within a path
     *
     * @param p_path prefix path
     * @return list with names
     */
    public Collection<CPath> getPaths( String p_path )
    {
        CPath l_path = new CPath( p_path );
        List<CPath> l_elements = new ArrayList();

        for ( Map.Entry<CPath, JComponent> l_item : m_pathobject.entrySet() )
            if ( l_item.getKey().getPath().startsWith( l_path.getPath() ) )
                l_elements.add( l_item.getKey() );

        return l_elements;
    }


    /**
     * returns the full entry set
     *
     * @return set all entries
     */
    public Set<Map.Entry<CPath, JComponent>> entrySet()
    {
        return m_pathobject.entrySet();
    }


    /**
     * returns the entry set of a path without the root element
     *
     * @param p_path path
     * @return set with elements
     */
    public Set<Map.Entry<CPath, JComponent>> entrySet( String p_path )
    {
        return this.entrySet( p_path, false );
    }


    /**
     * returns the entry set of a path
     *
     * @param p_path     path
     * @param p_withroot enables / disables the addition of the root node
     * @return set with elements
     */
    public Set<Map.Entry<CPath, JComponent>> entrySet( String p_path, boolean p_withroot )
    {
        CPath l_path = new CPath( p_path );
        Map<CPath, JComponent> l_set = new HashMap();

        for ( Map.Entry<CPath, JComponent> l_item : m_pathobject.entrySet() )
            if ( p_withroot )
            {
                if ( l_item.getKey().getPath().startsWith( l_path.getPath() ) )
                    l_set.put( l_item.getKey(), l_item.getValue() );
            }
            else
            {
                if ( ( !l_item.getKey().getPath().equals( l_path.getPath() ) ) && ( l_item.getKey().getPath().startsWith( l_path.getPath() ) ) )
                    l_set.put( l_item.getKey(), l_item.getValue() );
            }

        return l_set.entrySet();
    }


    /**
     * returns the element
     *
     * @param p_path path
     * @return element or null
     */
    public JComponent get( String p_path )
    {
        return m_pathobject.get( new CPath( p_path ) );
    }

    /**
     * gets a path of a component
     *
     * @param p_object object
     * @return path
     */
    public CPath get( JComponent p_object )
    {
        return m_objectpath.get( p_object );
    }

    /**
     * returns the element
     *
     * @param p_path path
     * @return element or null
     */
    public JComponent get( CPath p_path )
    {
        return m_pathobject.get( p_path );
    }


    // --- add / remove menu elements ----------------------------------------------------------------------------------

    /**
     * adds a new menu
     *
     * @param p_path full path without item
     */
    public void addMenu( String p_path )
    {
        this.GetOrCreatePath( new CPath( p_path ) );
    }


    /**
     * removes all items with their subitems
     *
     * @param p_path       path of the item
     * @param p_deleteroot bool flag to remove the root item
     * @return root item
     */
    public JComponent removeItems( String p_path, boolean p_deleteroot )
    {
        CPath l_path = new CPath( p_path );
        JComponent l_root = m_pathobject.get( l_path );
        if ( l_root == null )
            return l_root;

        List<Pair<CPath, JComponent>> l_remove = new LinkedList();
        if ( p_deleteroot )
        {
            for ( Map.Entry<CPath, JComponent> l_item : m_pathobject.entrySet() )
                if ( l_item.getKey().getPath().startsWith( l_path.getPath() ) )
                    l_remove.add( new ImmutablePair<CPath, JComponent>( l_item.getKey(), l_item.getValue() ) );
        }
        else
        {
            for ( Map.Entry<CPath, JComponent> l_item : m_pathobject.entrySet() )
                if ( ( l_item.getKey().getPath().startsWith( l_path.getPath() ) ) && ( !l_item.getKey().equals( l_path ) ) )
                    l_remove.add( new ImmutablePair<CPath, JComponent>( l_item.getKey(), l_item.getValue() ) );
        }

        for ( Pair<CPath, JComponent> l_item : l_remove )
        {
            m_pathobject.remove( l_item.getLeft() );
            m_objectpath.remove( l_item.getRight() );
        }

        return l_root;
    }

    /**
     * removes all items with their subitems
     *
     * @param p_path path of the item
     * @return root item
     */
    public JComponent removeItems( String p_path )
    {
        return this.removeItems( p_path, true );
    }


    /**
     * adds a new menu item
     *
     * @param p_path   full path
     * @param p_listen action listener for the item
     */
    public void addItem( String p_path, ActionListener p_listen )
    {
        CPath l_path = new CPath( p_path );
        if ( m_pathobject.containsKey( l_path ) )
            throw new IllegalArgumentException( "item exists and cannot be replaced" );

        // get parent
        JMenu l_menu = null;
        String l_label = l_path.removeSuffix();

        if ( l_path.size() > 0 )
            l_menu = this.GetOrCreatePath( l_path );

        // create menu entry
        JMenuItem l_item = new JMenuItem( l_label );
        l_item.addActionListener( p_listen );
        if ( l_menu != null )
            l_menu.add( l_item );

        l_path = new CPath( p_path );
        m_pathobject.put( l_path, l_item );
        m_objectpath.put( l_item, l_path );
    }


    /**
     * adds a list of items
     *
     * @param p_path     parent path
     * @param p_elements array with item names (null adds a seperator)
     * @param p_listen   action listener for the item
     */
    public void addItem( String p_path, String[] p_elements, ActionListener p_listen )
    {
        CPath l_path = new CPath( p_path );
        if ( m_pathobject.containsKey( l_path ) )
            throw new IllegalArgumentException( "item exists and cannot be replaced" );

        // get parent
        JMenu l_menu = null;
        if ( l_path.size() > 0 )
            l_menu = this.GetOrCreatePath( l_path );

        // add all elements
        for ( String l_name : p_elements )
        {
            if ( ( l_name == null ) || ( l_name.isEmpty() ) )
            {
                if ( l_menu != null )
                    l_menu.addSeparator();
            }
            else
            {
                CPath l_fullpath = new CPath( p_path, l_name );
                if ( m_pathobject.containsKey( l_fullpath ) )
                    continue;

                JMenuItem l_item = new JMenuItem( l_name );
                l_item.addActionListener( p_listen );

                m_pathobject.put( l_fullpath, l_item );
                m_objectpath.put( l_item, l_fullpath );
                if ( l_menu != null )
                    l_menu.add( l_item );
            }
        }
    }


    /**
     * adds a slider
     *
     * @param p_path     path of the element
     * @param p_value    initialization value
     * @param p_labelmin label of the minimum (null removes the label)
     * @param p_min      minimum value
     * @param p_labelmax label of the maximum (null removes the label)
     * @param p_max      maximum value
     * @param p_listen   change listener
     */
    public void addSlider( String p_path, int p_value, String p_labelmin, int p_min, String p_labelmax, int p_max, ChangeListener p_listen )
    {
        CPath l_path = new CPath( p_path );
        if ( m_pathobject.containsKey( l_path ) )
            throw new IllegalArgumentException( "item exists and cannot be replaced" );

        // get parent
        JMenu l_menu = null;
        String l_label = l_path.removeSuffix();
        if ( l_path.size() > 0 )
            l_menu = this.GetOrCreatePath( l_path );

        // create menu entry
        JSlider l_slider = new JSlider( p_min, p_max, p_value );
        l_slider.addChangeListener( p_listen );

        if ( ( p_labelmax != null ) && ( p_labelmin != null ) )
        {
            Hashtable<Integer, JLabel> l_sliderlabel = new Hashtable();
            l_sliderlabel.put( l_slider.getMinimum(), new JLabel( p_labelmin ) );
            l_sliderlabel.put( l_slider.getMaximum(), new JLabel( p_labelmax ) );
            l_sliderlabel.put( ( l_slider.getMaximum() - l_slider.getMinimum() ) / 2, new JLabel( l_label ) );

            l_slider.setLabelTable( l_sliderlabel );
            l_slider.setPaintLabels( true );
        }

        if ( l_menu != null )
            l_menu.add( l_slider );

        l_path = new CPath( p_path );
        m_pathobject.put( l_path, l_slider );
        m_objectpath.put( l_slider, l_path );
    }

    /**
     * adds single radio items
     *
     * @param p_path     parent path
     * @param p_elements array with item names (null adds a seperator)
     * @param p_listen   action listener for the item
     */
    public void addRadioItems( String p_path, String[] p_elements, ActionListener p_listen )
    {
        this.addRadioGroup( p_path, p_elements, null, p_listen );
    }

    /**
     * adds a radio group
     *
     * @param p_path     parent path
     * @param p_elements array with item names (null adds a seperator)
     * @param p_listen   action listener for the item
     */
    public void addRadioGroup( String p_path, String[] p_elements, ActionListener p_listen )
    {
        this.addRadioGroup( p_path, p_elements, new ButtonGroup(), p_listen );
    }


    /**
     * adds a radio group
     *
     * @param p_path     parent path
     * @param p_elements array with item names (null adds a seperator)
     * @param p_group    button group or null
     * @param p_listen   action listener for the item
     */
    private void addRadioGroup( String p_path, String[] p_elements, ButtonGroup p_group, ActionListener p_listen )
    {
        CPath l_path = new CPath( p_path );
        if ( m_pathobject.containsKey( l_path ) )
            throw new IllegalArgumentException( "item exists and cannot be replaced" );

        // get parent
        JMenu l_menu = null;
        if ( l_path.size() > 0 )
            l_menu = this.GetOrCreatePath( l_path );

        // add all elements
        for ( String l_name : p_elements )
        {
            if ( ( l_name == null ) || ( l_name.isEmpty() ) )
            {
                if ( l_menu != null )
                    l_menu.addSeparator();
            }
            else
            {
                CPath l_fullpath = new CPath( p_path, l_name );
                if ( m_pathobject.containsKey( l_fullpath ) )
                    continue;

                JRadioButtonMenuItem l_item = new JRadioButtonMenuItem( l_name );
                l_item.setSelected( false );
                l_item.addActionListener( p_listen );
                if ( p_group != null )
                    p_group.add( l_item );

                m_pathobject.put( l_fullpath, l_item );
                m_objectpath.put( l_item, l_fullpath );
                if ( l_menu != null )
                    l_menu.add( l_item );
            }
        }
    }

}
