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

import de.tu_clausthal.in.winf.objects.IObject;

import javax.swing.*;


/**
 * inspector class to create a visual view of an object
 *
 * @see http://openbook.galileocomputing.de/javainsel9/javainsel_19_019.htm#mj4d8b3bc0b550304f97713fa1788ff0f6
 * @see http://wiki.byte-welt.net/wiki/JTable_(Tutorial)%C2%A9
 * @see http://wiki.byte-welt.net/wiki/JTable_%28Java_API%29
 */
public class CInspector extends JTable {

    /**
     * singleton instance *
     */
    private static CInspector s_inspector = new CInspector();

    /**
     * private ctor *
     */
    private CInspector() {
        super(new CInspectorModel());
    }

    /**
     * return singleton instance
     *
     * @return instance
     */
    public static CInspector getInstance() {
        return s_inspector;
    }

    /**
     * sets a new object
     *
     * @param p_object object
     */
    public void set(IObject p_object) {
        ((CInspectorModel) this.getModel()).set(p_object);
    }

}


