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

package de.tu_clausthal.in.winf.simulation.data;


import de.tu_clausthal.in.winf.ui.COSMViewer;
import org.jxmapviewer.painter.CompoundPainter;

import java.util.HashMap;
import java.util.Map;

/**
 * world layer collection
 */
public class CWorld<T> extends CompoundPainter<T> implements Map<String, ILayer> {

    /**
     * viewer *
     */
    protected COSMViewer m_viewer = null;
    /**
     * map with layer *
     */
    protected Map<String, ILayer> m_layer = new HashMap();


    /**
     * ctor
     *
     * @param p_viewer viewer
     */
    public CWorld(COSMViewer p_viewer) {
        m_viewer = p_viewer;
        m_viewer.getCompoundPainter().addPainter(this);
    }

}
