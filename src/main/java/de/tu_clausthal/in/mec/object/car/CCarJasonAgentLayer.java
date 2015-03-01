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

package de.tu_clausthal.in.mec.object.car;


import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import de.tu_clausthal.in.mec.ui.CFrame;


/**
 * layer for car agents
 */
public class CCarJasonAgentLayer extends IEnvironment<CDefaultCar>
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;

    /**
     * ctor of Jason structure
     */
    public CCarJasonAgentLayer()
    {
        super();
    }

    /**
     * ctor of Jason structure
     *
     * @param p_frame frame object set Jason mindinspector
     */
    public CCarJasonAgentLayer( final CFrame p_frame )
    {
        super( p_frame );
    }

}
