/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.car.drivemodel;

import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;

import java.util.Map;
import java.util.Random;


/**
 * class of the Nagel-Schreckenberg drive model
 *
 * @see http://en.wikipedia.org/wiki/Nagel%E2%80%93Schreckenberg_model
 */
public class CNagelSchreckenberg implements IDriveModel
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * defines the minimal speed *
     */
    protected int m_minimalspeed = 15;

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
    public void update( final int p_currentstep, final CGraphHopper p_graph, final ICar p_car )
    {

        //check maximum speed on the current edge and modify speed
        int l_maxspeed = Math.min( p_car.getMaximumSpeed(), (int) p_graph.getEdgeSpeed( p_car.getEdge() ) );

        // increment speed
        p_car.setCurrentSpeed( Math.min( l_maxspeed, p_car.getCurrentSpeed() + p_car.getAcceleration() ) );

        // check collision with the predecessor car
        Map<Integer, ICar> l_predecessor = p_car.getPredecessor();
        if ( ( l_predecessor != null ) && ( l_predecessor.size() > 0 ) )
        {
            Map.Entry<Integer, ICar> l_item = l_predecessor.entrySet().iterator().next();
            if ( l_item.getKey().intValue() < p_car.getCurrentSpeed() )
                p_car.setCurrentSpeed( Math.max( 0, l_item.getKey().intValue() - 1 ) );
        }

        // decrement on linger random value
        if ( ( p_car.getCurrentSpeed() > 0 ) && ( m_random.nextDouble() <= p_car.getLingerProbability() ) )
            p_car.setCurrentSpeed( Math.max( m_minimalspeed, p_car.getCurrentSpeed() - p_car.getDeceleration() ) );

    }

}
