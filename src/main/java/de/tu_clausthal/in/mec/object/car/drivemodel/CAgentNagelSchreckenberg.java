/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

import de.tu_clausthal.in.mec.object.car.CCarJasonAgent;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;


/**
 * class of the Nagel-Schreckenberg drive model which
 * uses full control of agent cars
 *
 * @see http://en.wikipedia.org/wiki/Nagel%E2%80%93Schreckenberg_model
 */
public class CAgentNagelSchreckenberg extends CNagelSchreckenberg
{

    @Override
    public final void update( final int p_currentstep, final CCarLayer p_layer, final ICar p_car )
    {
        // if car is an agent-car the agent gets full control over the car - we check only the precessor to avoid collisions
        if ( p_car instanceof CCarJasonAgent )
        {
            this.checkCollision( p_layer, p_car );
            return;
        }

        // otherwise call super method
        super.update( p_currentstep, p_layer, p_car );
    }
}
