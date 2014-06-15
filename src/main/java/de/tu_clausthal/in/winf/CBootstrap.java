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

package de.tu_clausthal.in.winf;

import de.tu_clausthal.in.winf.object.world.CWorld;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.ui.CFrame;

/**
 * class to create the bootstrap of the program
 */
public class CBootstrap {

    /**
     * is called after configuration is loaded
     *
     * @param p_configuration configuration
     */
    public static void ConfigIsLoaded(CConfiguration p_configuration) {
    }

    ;


    /**
     * is called after the frame is initialize
     *
     * @param p_frame frame
     */
    public static void AfterFrameInit(CFrame p_frame) {
    }

    ;


    /**
     * is called after the simulation is initialize
     *
     * @param p_simulation simulation
     */
    public static void AfterSimulationInit(CSimulation p_simulation) {
    }

    ;


    /**
     * is called after the world is initialize
     *
     * @param p_world world
     */
    public static void AfterWorldInit(CWorld p_world ) {
    };

}
