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

import de.tu_clausthal.in.winf.objects.ICar;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.simulation.ISimulationStep;

import javax.swing.*;


/**
 * class for step actions *
 *
 * @deprecated
 */
public class CSimulationStepPainter implements ISimulationStep {

    @Override
    public void before(int p_currentstep, ICarSourceFactory[] p_sources, ICar[] p_cars) {
    }

    @Override
    public void after(int p_currentstep, ICarSourceFactory[] p_sources, ICar[] p_cars) {
        SwingUtilities.invokeLater(new SwingPainter(p_cars));
    }

    /**
     * runnable class for Swing painter to avoid thread barrier exceptions *
     */
    private class SwingPainter implements Runnable {
        /**
         * list with cars *
         */
        private ICar[] m_cars = null;

        /**
         * ctor to create car painter *
         */
        public SwingPainter(ICar[] p_cars) {
            m_cars = p_cars;
        }

        @Override
        public void run() {

        }
    }

}
