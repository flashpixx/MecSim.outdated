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
import de.tu_clausthal.in.winf.ui.painter.CRectanglePainter;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;


/**
 * mouse listener for jxviewer *
 */
class COSMMouseListener implements MouseListener, MouseMotionListener {

    /**
     * popup *
     */
    private CMenuPopup m_popup = new CMenuPopup();
    private CRectanglePainter m_rectangle = null;


    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (m_rectangle == null)
        {
            m_rectangle = new CRectanglePainter(e.getPoint());
            COSMViewer.getInstance().getCompoundPainter().addPainter(m_rectangle);
        } else {
            m_rectangle.to(e.getPoint());
            m_rectangle = null;
        }


        //((JXMapViewer)e.getSource()).repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        try {
        // left double-click
        // http://stackoverflow.com/questions/4051659/identifying-double-click-in-java
        if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
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

        // right-click
        if ((e.getButton() == MouseEvent.BUTTON3) && (e.getClickCount() == 1)) {
            m_popup.update();
            m_popup.show(e.getComponent(), e.getX(), e.getY());
        }
        } catch (Exception l_exception) {
            JOptionPane.showMessageDialog(null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}


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
