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

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.analysis.CStatisticMap;
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

    /** control of the dock component **/
    private CControl m_control = new CControl( this );
    /** grid of the windows **/
    private CGrid m_grid = new CGrid(m_control);


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
                m_control.destroy();
            }
        });

        this.add( m_control.getContentArea() );
        m_grid.add(0,0,1,1, this.createDockable("OSM", COSMViewer.getInstance(), false));
        m_control.getContentArea().deploy(m_grid);

    }


    /**
     * add a chart to the frame
     *
     * @param p_name  dockname
     * @param p_panel chart object
     */
    public void addChart(String p_name, ChartPanel p_panel) {
        m_grid.add(0,0,1,1, this.createDockable(p_name, p_panel, false));
    }


    private SingleCDockable createDockable( String p_title, Component p_panel, boolean p_close )
    {
        DefaultSingleCDockable l_dock = new DefaultSingleCDockable( p_title, p_title );
        l_dock.setTitleText(p_title);
        l_dock.setCloseable(p_close);
        l_dock.add(p_panel);
        return l_dock;
    }

}
