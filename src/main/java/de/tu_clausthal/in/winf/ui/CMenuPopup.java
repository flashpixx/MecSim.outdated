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

import de.tu_clausthal.in.winf.util.CMenuFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


/**
 * popmenu
 */
public class CMenuPopup extends JPopupMenu implements ActionListener {

    /**
     * map with object for action listener
     */
    private Map<String, Object> m_reference = new HashMap();


    /**
     * ctor to create popup
     */
    public CMenuPopup() {

        String[] l_cars = {"default cars", "norm cars"};
        this.add(CMenuFactory.createRadioMenu("Sources", l_cars, this, m_reference));

        String[] l_norm = {"speed norm"};
        this.add(CMenuFactory.createRadioMenu("Norms", l_norm, this, m_reference));
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
