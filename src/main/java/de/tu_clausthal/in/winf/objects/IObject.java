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

package de.tu_clausthal.in.winf.objects;

import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;


/**
 * global object of the simulation with mouse event handler
 */
public abstract class IObject extends JComponent implements MouseListener {

    /**
     * ctor to register component on the viewer *
     */
    public IObject() {
        COSMViewer.getInstance().addMouseListener(this);
    }

    /**
     * click method which is called by a click on the object
     *
     * @param e      mouse event
     * @param viewer viewer
     */
    public void onClick(MouseEvent e, JXMapViewer viewer) {
    }

    /**
     * returns a map to inspect current data of the car
     *
     * @return map with name and value
     */
    public Map<String, Object> inspect() {
        Map<String, Object> l_map = new HashMap();

        l_map.put("class name", this.getClass().getName());
        l_map.put("object id", this.hashCode());

        return l_map;
    }


    @Override
    public void mousePressed(MouseEvent e) {
        this.onClick(e, COSMViewer.getInstance());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

}
