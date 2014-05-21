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

import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;


import java.util.HashSet;
import java.util.Set;


/**
 * overlay renderer for waypoints, especially sources
 *
 * @note after adding / removing waypoints the JxMapViewer must be repainted,
 * otherwise the waypoint is not shown
 */
public class CSourceOverlay extends WaypointPainter {

    /**
     * reference to the viewer *
     */
    private JXMapViewer m_viewer = null;


    /**
     * ctor to set the viewer
     *
     * @param p_viewer reference to viewer
     */
    public CSourceOverlay(JXMapViewer p_viewer) {
        m_viewer = p_viewer;
    }


    /**
     * adds new sources to the map
     *
     * @param p_src source object
     * @note we need a manual call of the repaint, because the waypoints
     * are not shown until the map is repainted
     */
    public void addSources(ICarSourceFactory p_src) {
        HashSet<ICarSourceFactory> l_sources = new HashSet<ICarSourceFactory>(this.getWaypoints());
        l_sources.add(p_src);
        this.setWaypoints(l_sources);
        m_viewer.repaint();
    }


    /**
     * removes a source from the map
     *
     * @param p_src source object
     * @note we need a manual call of the repaint, because the waypoints
     * are not shown until the map is repainted
     */
    public void removeSource(ICarSourceFactory p_src) {
        HashSet<ICarSourceFactory> l_sources = new HashSet<ICarSourceFactory>(this.getWaypoints());
        l_sources.remove(p_src);
        this.setWaypoints(l_sources);
        m_viewer.repaint();
    }


    /**
     * returns a immutable set of all sources
     *
     * @return set with sources
     */
    public Set<ICarSourceFactory> getSources() {
        return this.getWaypoints();
    }

}
