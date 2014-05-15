/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenpraktikum.   #
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
 @endcond
 **/

package de.tu_clausthal.in.winf.ui;

import javax.swing.*;


/**
 * class for create the menubar *
 */
public class CMenuBar extends JMenuBar {

    /**
     * ctor with menu items *
     */
    public CMenuBar() {
        super();

        CMenuListener l_listener = new CMenuListener();
        JMenu l_menu = new JMenu("Action");
        JMenuItem l_item = null;


        l_item = new JMenuItem("Start");
        l_listener.add(l_item.getText(), l_item);
        l_item.addActionListener(l_listener);
        l_menu.add(l_item);

        l_item = new JMenuItem("Stop");
        l_listener.add(l_item.getText(), l_item);
        l_item.addActionListener(l_listener);
        l_menu.add(l_item);

        l_menu.addSeparator();

        l_item = new JMenuItem("Reset");
        l_listener.add(l_item.getText(), l_item);
        l_item.addActionListener(l_listener);
        l_menu.add(l_item);

        this.add(l_menu);
    }

}
