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

package de.tu_clausthal.in.winf;

import de.tu_clausthal.in.winf.analysis.CStatisticMap;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.ui.CFrame;
import de.tu_clausthal.in.winf.ui.CSimulationStepPainter;

import javax.swing.*;
import java.io.File;


/**
 * main class of the application
 *
 * @note Jar must be started with option "-Xmx2g", because we need memory to create graph structure
 */
public class CMain {

    /**
     * main program
     *
     * @param p_args commandline arguments
     */
    public static void main(String[] p_args) throws Exception {
        // read the configuration directory (default ~/.tucwinf)
        File l_config = new File(System.getProperty("user.home") + File.separator + ".tucwinf");
        if (p_args.length > 0)
            l_config = new File(p_args[0]);

        // set configuration directory and read the Json configuration file‚‚‚‚‚
        CConfiguration.getInstance().setConfigDir(l_config);
        CConfiguration.getInstance().read();

        // call Graphhopper instance (and import data if needed)
        CGraphHopper.getInstance();

        // add step runner objects to the simulation
        CSimulation.getInstance().registerStep(new CSimulationStepPainter());
        CSimulation.getInstance().registerStep(CStatisticMap.getInstance());


        // create the UI within an invoke thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CFrame l_frame = new CFrame();
                l_frame.setVisible(true);
            }
        });
    }

}
