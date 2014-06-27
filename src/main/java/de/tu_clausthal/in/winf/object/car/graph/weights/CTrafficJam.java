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

package de.tu_clausthal.in.winf.object.car.graph.weights;

import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.winf.object.car.graph.CGraphHopper;


/**
 * class to create edge weights of the current traffic occurrence
 *
 * @see https://github.com/graphhopper/graphhopper/blob/master/docs/core/weighting.md
 */
public class CTrafficJam implements Weighting {

    /**
     * graph instance *
     */
    private CGraphHopper m_graph = null;


    /**
     * ctor
     *
     * @param p_graph graph object
     */
    public CTrafficJam(CGraphHopper p_graph) {
        m_graph = p_graph;
    }


    @Override
    public double getMinWeight(double p_weight) {
        return 0;
    }


    @Override
    public double calcWeight(EdgeIteratorState p_edge, boolean p_reverse) {
        return m_graph.getEdge(p_edge).getNumberOfCars();
    }

}
