/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
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

package de.tu_clausthal.in.winf.object.car.drivemodel;

import de.tu_clausthal.in.winf.object.car.ICar;
import de.tu_clausthal.in.winf.object.car.graph.CGraphHopper;

import java.util.Map;
import java.util.Random;


/**
 * class of the Nagel-Schreckenberg drive model
 *
 * @note modifed model, the maximum speed is defined by the maximum edge speed and the
 * acceleration/ deceleration is defined on the car object (both create a little bit more realistic driving)
 * @see http://en.wikipedia.org/wiki/Nagel%E2%80%93Schreckenberg_model
 */
public class CNagelSchreckenberg implements IDriveModel
{

    /**
     * defines the minimal speed *
     */
    private static int s_minimalspeed = 5;

    /**
     * random object for linger probability *
     */
    private Random m_random = new Random();


    @Override
    public String getName()
    {
        return "Nagel-Schreckenberg";
    }

    @Override
    public void update( int p_currentstep, CGraphHopper p_graph, ICar p_car )
    {

        //check maximum speed on the current edge and modify speed
        int l_maxspeed = Math.min( p_car.getMaximumSpeed(), (int) p_graph.getEdgeSpeed( p_car.getEdge() ) );

        // increment speed
        p_car.setCurrentSpeed( Math.min( l_maxspeed, ( p_car.getCurrentSpeed() + p_car.getAcceleration() ) ) );

        // check collision with the predecessor car
        Map<Integer, ICar> l_predecessor = p_car.getPredecessor();
        if ( ( l_predecessor != null ) && ( l_predecessor.size() > 0 ) )
        {
            Map.Entry<Integer, ICar> l_item = l_predecessor.entrySet().iterator().next();
            if ( l_item.getKey().intValue() < p_car.getCurrentSpeed() )
                p_car.setCurrentSpeed( Math.max( s_minimalspeed, ( l_item.getKey().intValue() - p_car.getDeceleration() ) ) );
        }

        // decrement on linger random value
        if ( ( p_car.getCurrentSpeed() > 0 ) && ( m_random.nextDouble() <= p_car.getLingerProbability() ) )
            p_car.setCurrentSpeed( Math.max( s_minimalspeed, ( p_car.getCurrentSpeed() - p_car.getDeceleration() ) ) );

    }

}
