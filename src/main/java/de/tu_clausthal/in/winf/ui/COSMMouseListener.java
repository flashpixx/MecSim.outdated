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

import de.tu_clausthal.in.winf.objects.CDefaultSource;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.simulation.CSimulationData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;


/**
 * mouse listener for jxviewer *
 */
class COSMMouseListener extends MouseAdapter {

    /**
     * size of the box around a source to remove it *
     */
    static final int s_removeBoxEpsilon = 10;


    /**
     * click method to add / remove sources *
     *
     * @param p_event mouse event
     */
    public void mouseClicked(MouseEvent p_event) {
        try {

            // left click
            if (p_event.getButton() == MouseEvent.BUTTON1) {
                COSMViewer l_viewer = (COSMViewer) p_event.getSource();
                Rectangle l_viewportBounds = l_viewer.getViewportBounds();
                Point2D l_position = new Point(l_viewportBounds.x + p_event.getPoint().x, l_viewportBounds.y + p_event.getPoint().y);

                boolean l_remove = false;
                for (ICarSourceFactory l_source : CSimulationData.getInstance().getSourceQueue().getAll()) {
                    Point2D l_sourceposition = l_viewer.getTileFactory().geoToPixel(l_source.getPosition(), l_viewer.getZoom());
                    if ((l_position.getX() >= l_sourceposition.getX() - s_removeBoxEpsilon) && (l_position.getX() <= l_sourceposition.getX() + s_removeBoxEpsilon) &&
                            (l_position.getY() >= l_sourceposition.getY() - s_removeBoxEpsilon) && (l_position.getY() <= l_sourceposition.getY() + s_removeBoxEpsilon)) {
                        CSimulationData.getInstance().getSourceQueue().remove(l_source);
                        l_remove = true;
                        break;
                    }
                }

                if (!l_remove)
                    CSimulationData.getInstance().getSourceQueue().add(new CDefaultSource(l_viewer.getTileFactory().pixelToGeo(l_position, l_viewer.getZoom())));

                COSMViewer.getInstance().repaint();
            }


            // right click
            if (p_event.getButton() == MouseEvent.BUTTON3) {
                CMenuPopup l_menu = new CMenuPopup();
                l_menu.show(p_event.getComponent(), p_event.getX(), p_event.getY());
            }


        } catch (Exception l_exception) {
            JOptionPane.showMessageDialog(null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION);
        }
    }

}
