/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.util;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Map;


/**
 * factory class to create UI menus
 */
public class CMenuFactory {


    /**
     * creates a menu
     *
     * @param p_label     menu label
     * @param p_list      string with items
     * @param p_listener  listener
     * @param p_reference reference map with object for action listener
     * @return menu
     */
    public static JMenu createMenu(String p_label, String[] p_list, ActionListener p_listener, Map<String, Object> p_reference) {
        if (p_label == null)
            return null;
        if ((p_list == null) || (p_listener == null) || (p_reference == null))
            return new JMenu(p_label);

        JMenu l_menu = new JMenu(p_label);
        for (String l_item : p_list)
            if ((l_item == null) || (l_item.isEmpty()))
                l_menu.addSeparator();
            else
                createMenuItem(l_item, l_menu, p_listener, p_reference);

        return l_menu;
    }

    /**
     * creates a menu
     *
     * @param p_label     menu label
     * @param p_list      string with items
     * @param p_listener  listener
     * @param p_reference reference map with object for action listener
     * @return menu
     */
    public static JMenu createRadioMenu(String p_label, String[] p_list, ActionListener p_listener, Map<String, Object> p_reference) {
        if (p_label == null)
            return null;
        if ((p_list == null) || (p_listener == null) || (p_reference == null))
            return new JMenu(p_label);

        JMenu l_menu = new JMenu(p_label);
        ButtonGroup l_group = new ButtonGroup();

        for (int i = 0; i < p_list.length; i++)
            if ((p_list[i] == null) || (p_list[i].isEmpty()))
                l_menu.addSeparator();
            else
                createRadioMenuItem(p_list[i], l_menu, p_listener, p_reference, l_group, i == 0);

        return l_menu;
    }


    /**
     * creates a menu item
     *
     * @param p_label     label
     * @param p_menu      menu
     * @param p_listener  listener
     * @param p_reference reference map with object for action listener
     */
    private static void createMenuItem(String p_label, JMenu p_menu, ActionListener p_listener, Map<String, Object> p_reference) {
        JMenuItem l_item = new JMenuItem(p_label);
        p_reference.put(l_item.getText(), l_item);
        l_item.addActionListener(p_listener);
        p_menu.add(l_item);
    }


    /**
     * creates a radio menu item
     *
     * @param p_label     label
     * @param p_menu      menu
     * @param p_listener  listener
     * @param p_reference reference map with object for action listener
     * @param p_group     group
     * @param p_select    item default selected
     */
    private static void createRadioMenuItem(String p_label, JMenu p_menu, ActionListener p_listener, Map<String, Object> p_reference, ButtonGroup p_group, boolean p_select) {
        JRadioButtonMenuItem l_item = new JRadioButtonMenuItem(p_label);
        p_reference.put(l_item.getText(), l_item);
        l_item.setSelected(p_select);
        l_item.addActionListener(p_listener);
        p_group.add(l_item);
        p_menu.add(l_item);
    }

}
