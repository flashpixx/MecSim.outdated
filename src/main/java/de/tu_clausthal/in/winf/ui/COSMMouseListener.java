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
import de.tu_clausthal.in.winf.objects.norms.CNormSource;
import de.tu_clausthal.in.winf.simulation.CSimulation;
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

    private Point2D m_beginning = null;


    /**
     * popup *
     */
    private CMenuPopup m_popup = new CMenuPopup();


    @Override
    public void mousePressed(MouseEvent e) {
        // set the start position for dragging
        m_beginning = (e.getButton() == MouseEvent.BUTTON1) ? e.getPoint() : null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {

            // left click (without any mouse movement)
            if ((e.getButton() == MouseEvent.BUTTON1) && (this.inRange(e.getPoint(), m_beginning, 2))) {
                if (CSimulation.getInstance().isRunning())
                    throw new IllegalStateException("simulation is running");

                COSMViewer l_viewer = (COSMViewer) e.getSource();
                Rectangle l_viewportBounds = l_viewer.getViewportBounds();
                Point2D l_position = new Point(l_viewportBounds.x + e.getPoint().x, l_viewportBounds.y + e.getPoint().y);

                boolean l_remove = false;
                for (ICarSourceFactory l_source : CSimulationData.getInstance().getSourceQueue().getAll())
                    if (this.inRange(l_position, l_viewer.getTileFactory().geoToPixel(l_source.getPosition(), l_viewer.getZoom()), 10)) {
                        CSimulationData.getInstance().getSourceQueue().remove(l_source);
                        l_remove = true;
                        break;
                    }

                if (!l_remove) {
                    if (m_popup.getSourceSelection().equals("default cars"))
                        CSimulationData.getInstance().getSourceQueue().add(new CDefaultSource(l_viewer.getTileFactory().pixelToGeo(l_position, l_viewer.getZoom())));
                    if (m_popup.getSourceSelection().equals("norm cars"))
                        CSimulationData.getInstance().getSourceQueue().add(new CNormSource(l_viewer.getTileFactory().pixelToGeo(l_position, l_viewer.getZoom())));
                }
            }

            // left click (with mouse movement)
            if ((e.getButton() == MouseEvent.BUTTON1) && (!this.inRange(e.getPoint(), m_beginning, 2))) {
                System.out.println("xxx");
            }


            // right click
            if (e.getButton() == MouseEvent.BUTTON3) {
                m_popup.update();
                m_popup.show(e.getComponent(), e.getX(), e.getY());
            }


        } catch (Exception l_exception) {
            JOptionPane.showMessageDialog(null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION);
        }
    }


    /**
     * checks if a point is in a box around another point
     *
     * @param p_checkposition point of the click
     * @param p_center        point of the source
     * @param p_size          rectangle size
     * @return boolean on existence
     */
    private boolean inRange(Point2D p_checkposition, Point2D p_center, int p_size) {
        if ((p_checkposition == null) || (p_center == null))
            return true;

        return ((p_checkposition.getX() - p_size / 2) <= p_center.getX()) && ((p_checkposition.getX() + p_size / 2) >= p_center.getX()) && ((p_checkposition.getY() - p_size / 2) <= p_center.getY()) && ((p_checkposition.getY() + p_size / 2) >= p_center.getY());
    }

}
