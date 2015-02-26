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

package de.tu_clausthal.in.mec.simulation.thread;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;


/**
 * wrapper class to reset a layer
 */
public class CLayerReset extends IRunnable<ILayer>
{

    /**
     * ctor for setting the object
     *
     * @param p_object performing object
     */
    public CLayerReset( final ILayer p_object )
    {
        super( p_object );
    }


    /**
     * run method to perform the action on runnable and callable interface
     */
    protected final void perform()
    {
        try
        {
            m_object.release();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( CCommon.getResourceString( this, "error", m_object.toString(), l_exception.toString() ) );
        }
    }

}
