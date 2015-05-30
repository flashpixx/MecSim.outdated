/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.object.car.drivemodel;

import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;

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
     * defines the minimal speed in km/h
     */
    private static final int c_minimalspeed = 15;
    /**
     * random object for linger probability *
     */
    private final Random m_random = new Random();
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;

    /**
     * checks the acceleration and increment the speed
     *
     * @param p_layer car layer
     * @param p_car car object
     */
    protected final void checkAccelerationWithEdgeSpeed( final CCarLayer p_layer, final ICar p_car )
    {
        p_car.setCurrentSpeed(
                Math.min(
                        Math.min( p_car.getMaximumSpeed(), (int) p_layer.getGraph().getEdgeSpeed( p_car.getEdge() ) ),
                        p_car.getCurrentSpeed() + (int) p_layer.getUnitConvert().getAccelerationToSpeed( p_car.getAcceleration() )
                )
        );
    }

    /**
     * checks of a collision and reduce speed
     *
     * @param p_layer car layer
     * @param p_car car object
     */
    protected void checkCollision( final CCarLayer p_layer, final ICar p_car )
    {
        final Map<Double, ICar> l_predecessor = p_car.getPredecessor();
        if ( ( l_predecessor != null ) && ( l_predecessor.size() > 0 ) )
        {
            // get distance which can be drive in one step and distance to the predecessor
            final double l_speeddistance = p_layer.getUnitConvert().getSpeedToDistance( p_car.getCurrentSpeed() );
            final double l_distance = l_predecessor.entrySet().iterator().next().getKey();

            if ( l_distance < l_speeddistance )
                p_car.setCurrentSpeed( Math.max( 0, p_layer.getUnitConvert().getSpeedOfDistance( l_speeddistance - l_distance ) ) );
        }
    }

    /**
     * checks the linger probability and modify speed
     *
     * @param p_layer car layer
     * @param p_car car object
     */
    protected final void checkLinger( final CCarLayer p_layer, final ICar p_car )
    {
        if ( ( p_car.getCurrentSpeed() > 0 ) && ( m_random.nextDouble() <= p_car.getLingerProbability() ) )
            p_car.setCurrentSpeed(
                    Math.max(
                            c_minimalspeed, p_car.getCurrentSpeed() - (int) p_layer.getUnitConvert().getAccelerationToSpeed(
                            p_car.getDeceleration()
                    )
                    )
            );
    }

    @Override
    public void update( final int p_currentstep, final CCarLayer p_layer, final ICar p_car )
    {
        this.checkAccelerationWithEdgeSpeed( p_layer, p_car );
        this.checkCollision( p_layer, p_car );
        this.checkLinger( p_layer, p_car );
    }

}
