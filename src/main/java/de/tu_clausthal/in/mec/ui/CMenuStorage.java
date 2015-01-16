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


import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * structure to store all menu items
 */
public class CMenuStorage
{

    /**
     * stores path and menu / items *
     */
    private Map<Path, JComponent> m_elements = new HashMap();

    /**
     * returns the element
     *
     * @param p_path path
     * @return element or null
     */
    public JComponent get( String p_path )
    {
        return m_elements.get( new Path( p_path ) );
    }

    /**
     * returns the element
     *
     * @param p_path path
     * @return element or null
     */
    public JComponent get( Path p_path )
    {
        return m_elements.get( p_path );
    }

    /**
     * creates a full path and returns the latest element
     *
     * @param p_path full path (without item)
     * @return menu object
     */
    private JMenu GetOrCreatePath( Path p_path )
    {
        JComponent l_parent = null;
        for ( Path l_item : p_path )
        {
            JComponent l_current = m_elements.get( l_item );

            if ( l_current == null )
            {
                l_current = new JMenu( l_item.getSuffix() );
                if ( l_parent != null )
                    l_parent.add( l_current );
                m_elements.put( l_item, l_current );
            }

            if ( !( l_current instanceof JMenu ) )
                throw new IllegalStateException( "item on [" + l_item + "] is not a JMenu" );

            l_parent = l_current;
        }

        return (JMenu) l_parent;
    }

    /**
     * adds a new menu
     *
     * @param p_path full path without item
     */
    public void addMenu( String p_path )
    {
        this.GetOrCreatePath( new Path( p_path ) );
    }

    /**
     * adds a new menu item
     *
     * @param p_path   full path
     * @param p_listen action listener for the item
     */
    public void addItem( String p_path, ActionListener p_listen )
    {
        Path l_path = new Path( p_path );
        if ( m_elements.containsKey( l_path ) )
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

        m_elements.put( new Path( p_path ), l_item );
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
        Path l_path = new Path( p_path );
        if ( m_elements.containsKey( l_path ) )
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
                Path l_fullpath = new Path( p_path, l_name );
                if ( m_elements.containsKey( l_fullpath ) )
                    continue;

                JMenuItem l_item = new JMenuItem( l_name );
                l_item.addActionListener( p_listen );
                m_elements.put( l_fullpath, l_item );
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
        Path l_path = new Path( p_path );
        if ( m_elements.containsKey( l_path ) )
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

        m_elements.put( new Path( p_path ), l_slider );
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
        Path l_path = new Path( p_path );
        if ( m_elements.containsKey( l_path ) )
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
                Path l_fullpath = new Path( p_path, l_name );
                if ( m_elements.containsKey( l_fullpath ) )
                    continue;

                JRadioButtonMenuItem l_item = new JRadioButtonMenuItem( l_name );
                l_item.setSelected( false );
                l_item.addActionListener( p_listen );
                if ( p_group != null )
                    p_group.add( l_item );
                m_elements.put( l_fullpath, l_item );
                if ( l_menu != null )
                    l_menu.add( l_item );
            }
        }
    }


    /**
     * returns the full menu list
     *
     * @return JMenu list
     */
    public Collection<JComponent> getRoot()
    {
        List<JComponent> l_root = new ArrayList();

        for ( Map.Entry<Path, JComponent> l_item : m_elements.entrySet() )
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
        Path l_path = new Path( p_path );

        List<JComponent> l_elements = new ArrayList();

        for ( Map.Entry<Path, JComponent> l_item : m_elements.entrySet() )
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
    public Collection<Path> getPaths( String p_path )
    {
        Path l_path = new Path( p_path );
        List<Path> l_elements = new ArrayList();

        for ( Map.Entry<Path, JComponent> l_item : m_elements.entrySet() )
            if ( l_item.getKey().getPath().startsWith( l_path.getPath() ) )
                l_elements.add( l_item.getKey() );

        return l_elements;
    }


    /**
     * returns the full entry set
     *
     * @return set all entries
     */
    public Set<Map.Entry<Path, JComponent>> entrySet()
    {
        return m_elements.entrySet();
    }


    /**
     * returns the entry set of a path without the root element
     *
     * @param p_path path
     * @return set with elements
     */
    public Set<Map.Entry<Path, JComponent>> entrySet( String p_path )
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
    public Set<Map.Entry<Path, JComponent>> entrySet( String p_path, boolean p_withroot )
    {
        Path l_path = new Path( p_path );
        Map<Path, JComponent> l_set = new HashMap();

        for ( Map.Entry<Path, JComponent> l_item : m_elements.entrySet() )
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
     * path class to define a full menu path *
     */
    public static class Path implements Iterable<Path>
    {
        /**
         * seperator of the path elements *
         */
        private static final String s_seperator = "/";

        /**
         * list with path parts *
         */
        private List<String> m_path = new ArrayList();

        /**
         * ctor
         *
         * @param p_parent defines the parent
         * @param p_item   defines the last item on the path
         */
        public Path( String p_parent, String p_item )
        {
            this.initialize( p_parent + s_seperator + p_item );
        }

        /**
         * ctor
         *
         * @param p_value defines a full path
         */
        public Path( String p_value )
        {
            this.initialize( p_value );
        }

        /**
         * ctor
         *
         * @param p_value creates a path of an string array
         */
        public Path( String[] p_value )
        {
            m_path = new ArrayList( Arrays.asList( p_value ) );
            if ( m_path.size() == 0 )
                throw new IllegalArgumentException( "path is empty" );
        }

        /**
         * splits the string data
         *
         * @param p_fqn full path
         */
        private void initialize( String p_fqn )
        {
            for ( String l_item : p_fqn.split( s_seperator ) )
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
            return StringUtils.join( m_path, s_seperator );
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
        public Iterator<Path> iterator()
        {
            return new Iterator<Path>()
            {
                private int m_index = 0;

                @Override
                public boolean hasNext()
                {
                    return m_index < m_path.size();
                }

                @Override
                public Path next()
                {
                    String[] l_list = new String[m_index + 1];
                    m_path.subList( 0, ++m_index ).toArray( l_list );
                    return new Path( l_list );
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
            if ( ( p_object instanceof String ) || ( p_object instanceof Path ) )
                return this.hashCode() == p_object.hashCode();

            return false;
        }

        @Override
        public String toString()
        {
            return this.getPath();
        }

    }

}
