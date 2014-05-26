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

import de.tu_clausthal.in.winf.CConfiguration;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * openstreetmap viewer class - must be a singleton because
 * we would to use only one instance with a global access
 *
 * @see https://github.com/msteiger/jxmapviewer2
 * @see https://today.java.net/pub/a/today/2007/10/30/building-maps-into-swing-app-with-jxmapviewer.html
 */
public class COSMViewer extends JXMapViewer {

    /**
     * singleton instance *
     */
    private static volatile COSMViewer s_instance = new COSMViewer();
    /**
     * compounend painter
     */
    private CompoundPainter m_painter = new CompoundPainter();


    /**
     * private ctor with loading configuration defaults
     * and listener definition
     */
    private COSMViewer() {
        super();

        TileFactoryInfo l_info = new OSMTileFactoryInfo();
        DefaultTileFactory l_tileFactory = new DefaultTileFactory(l_info);
        l_tileFactory.setThreadPoolSize(CConfiguration.getInstance().get().MaxThreadNumber);

        LocalResponseCache.installResponseCache(l_info.getBaseURL(), CConfiguration.getInstance().getConfigDir(), false);
        this.setTileFactory(l_tileFactory);
        this.setZoom(CConfiguration.getInstance().get().Zoom);
        this.setCenterPosition(CConfiguration.getInstance().get().ViewPoint);
        this.setAddressLocation(CConfiguration.getInstance().get().ViewPoint);

        this.setOverlayPainter(m_painter);

        MouseListener l_mouse = new COSMMouseListener();

        this.addMouseListener(new PanMouseInputListener(this));
        this.addMouseListener(l_mouse);
        //this.addMouseListener(new CenterMapListener(this));
        //this.addMouseMotionListener(new PanMouseInputListener(this));
        this.addMouseMotionListener( (MouseMotionListener)l_mouse );
        this.addMouseWheelListener(new ZoomMouseWheelListenerCenter(this));
        this.addKeyListener(new PanKeyListener(this));
    }


    /**
     * return instance
     *
     * @return viewer instance
     */
    public static COSMViewer getInstance() {
        return s_instance;
    }


    /**
     * returns the compounend painter
     *
     * @return painter
     */
    public CompoundPainter getCompoundPainter() {
        return m_painter;
    }

}
