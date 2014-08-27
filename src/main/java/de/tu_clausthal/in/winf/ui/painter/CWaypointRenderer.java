/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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

package de.tu_clausthal.in.winf.ui.painter;

import de.tu_clausthal.in.winf.CLogger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.Waypoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


/**
 * own waypoint renderer to handle all waypoints,
 * can be defined with a singleton because the object
 * is used global
 *
 * @deprecated is moved to waypointer class
 */
public class CWaypointRenderer extends DefaultWaypointRenderer {

    /**
     * singleton instance
     */
    private static volatile CWaypointRenderer s_instance = new CWaypointRenderer();
    /**
     * image object (read from JXMapViewer
     */
    protected BufferedImage m_image = null;
    /**
     * image map for faster access *
     */
    protected Map<Color, BufferedImage> m_images = new HashMap();


    /**
     * ctor to construct the basic image from the JXMapViewer package
     */
    private CWaypointRenderer() {
        try {
            m_image = ImageIO.read(DefaultWaypointRenderer.class.getResource("/images/standard_waypoint.png"));
        } catch (Exception l_exception) {
            CLogger.warn("could not read standard_waypoint.png" + l_exception.toString());
        }

    }

    /**
     * returns the singleton instance
     *
     * @return instance
     */
    public static CWaypointRenderer getInstance() {
        return s_instance;
    }

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint w) {
        /*
        if (m_image == null)
            return;

        // modify blue value to the color of the waypoint
        BufferedImage l_image = m_image;
        if (w instanceof ISourceFactory) {
            l_image = m_images.get(((ISourceFactory) w).getColor());
            if (l_image == null) {
                l_image = new BufferedImage(m_image.getColorModel(), m_image.copyData(null), m_image.isAlphaPremultiplied(), null);
                for (int i = 0; i < l_image.getHeight(); i++)
                    for (int j = 0; j < l_image.getWidth(); j++) {
                        Color l_color = new Color(l_image.getRGB(j, i));
                        if (l_color.getBlue() > 0)
                            l_image.setRGB(j, i, ((ISourceFactory) w).getColor().getRGB());

                    }
            }

        }

        // set image
        Point2D point = map.getTileFactory().geoToPixel(w.getPosition(), map.getZoom());
        g.drawImage(l_image, (int) point.getX() - l_image.getWidth() / 2, (int) point.getY() - l_image.getHeight(), null);
        */
    }

}
