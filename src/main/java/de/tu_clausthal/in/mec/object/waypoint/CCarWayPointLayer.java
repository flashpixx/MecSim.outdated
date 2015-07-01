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

package de.tu_clausthal.in.mec.object.waypoint;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPoint;


/**
 * layer with all sources
 */
public class CCarWayPointLayer extends IMultiLayer<IWayPoint<ICar>>
{

    @Override
    public final void afterStepObject( final int p_currentstep, final IWayPoint<ICar> p_object )
    {

    }

    @Override
    public final void beforeStepObject( final int p_currentstep, final IWayPoint<ICar> p_object )
    {

    }

    @Override
    public final int getCalculationIndex()
    {
        return 300;
    }

    @Override
    public final void release()
    {
        for ( final IWayPoint<ICar> l_item : m_data )
            l_item.release();
    }

    @Override
    public final void step( final int p_currentstep, final ILayer p_layer )
    {

    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

}
