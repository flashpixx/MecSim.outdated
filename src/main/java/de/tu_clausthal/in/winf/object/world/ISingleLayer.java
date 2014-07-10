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

package de.tu_clausthal.in.winf.object.world;

import de.tu_clausthal.in.winf.simulation.IVoidStepable;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import de.tu_clausthal.in.winf.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Map;


/**
 * single layer to create a single information structure
 */
public abstract class ISingleLayer implements Painter<COSMViewer>, IViewableLayer, IDataLayer, IVoidStepable, ILayer {

    /**
     * flag for visibility *
     */
    protected boolean m_visible = true;
    /**
     * flag for activity *
     */
    protected boolean m_active = true;

    @Override
    public boolean isActive() {
        return m_active;
    }

    @Override
    public void setActive(boolean p_active) {
        m_active = p_active;
    }

    @Override
    public boolean isVisible() {
        return m_visible;
    }

    @Override
    public void setVisible(boolean p_visible) {
        m_visible = p_visible;
    }

    @Override
    public void step(int p_currentstep, ILayer p_layer) {
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

    @Override
    public void paint(Graphics2D graphics2D, COSMViewer object, int i, int i2) {
    }

    @Override
    public void resetData() {

    }
}
