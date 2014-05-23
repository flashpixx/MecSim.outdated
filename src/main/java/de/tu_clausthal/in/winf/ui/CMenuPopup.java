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

package de.tu_clausthal.in.winf.ui;

import javax.swing.*;


/**
 * popmenu
 */
public class CMenuPopup extends JPopupMenu {

    public CMenuPopup() {

        ButtonGroup l_group = new ButtonGroup();
        JMenu l_menu = new JMenu("Sources");

        JRadioButtonMenuItem l_radio = new JRadioButtonMenuItem("default cars");
        l_radio.setSelected(true);
        l_group.add(l_radio);
        l_menu.add(l_radio);

        l_radio = new JRadioButtonMenuItem("norm cars");
        l_group.add(l_radio);
        l_menu.add(l_radio);

        this.add(l_menu);


/*
        l_menu = new JMenu("Norms");
        l_item = new JMenuItem("speed norm");
        l_menu.add(l_item);
        this.add(l_menu);*/

    }


}
