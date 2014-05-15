/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenpraktikum.   #
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
 @endcond
 **/

package de.tu_clausthal.in.winf.ui;


import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.analysis.CStatisticMap;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * create the main frame of the simulation
 * dockable component @see http://www.javalobby.org/java/forums/t52990.html
 */
public class CFrame extends JFrame {

    /**
     * dock object *
     */
    private DefaultDockingPort m_dock = new DefaultDockingPort();


    /**
     * ctor with frame initialization
     */
    public CFrame() {
        super();

        CStatisticMap.getInstance().setFrame(this);
        this.setLayout(new BorderLayout());
        this.setJMenuBar(new CMenuBar());


        this.setSize(CConfiguration.getInstance().get().WindowWidth, CConfiguration.getInstance().get().WindowHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent l_event) {
                CConfiguration.getInstance().get().ViewPoint = COSMViewer.getInstance().getCenterPosition();
                CConfiguration.getInstance().get().Zoom = COSMViewer.getInstance().getZoom();
                CConfiguration.getInstance().get().WindowHeight = l_event.getWindow().getHeight();
                CConfiguration.getInstance().get().WindowWidth = l_event.getWindow().getWidth();
                CConfiguration.getInstance().write();
                l_event.getWindow().dispose();
            }
        });


        m_dock.setSingleTabAllowed(true);
        m_dock.setPreferredSize(new Dimension(CConfiguration.getInstance().get().WindowWidth, CConfiguration.getInstance().get().WindowHeight));

        // register first component with name and add it to docking component
        DockingManager.registerDockable(COSMViewer.getInstance(), "OSM");
        DockingManager.dock(COSMViewer.getInstance(), (DockingPort) m_dock);

        // add docking component to frame
        this.add(m_dock);

    }


    /**
     * add a chart to the frame
     *
     * @param p_name  dockname
     * @param p_panel chart object
     */
    public void addChart(String p_name, ChartPanel p_panel) {
        DockingManager.registerDockable(p_panel, p_name);
        DockingManager.dock(p_panel, (DockingPort) m_dock);
        this.pack();
    }

}
