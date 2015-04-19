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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.ui.web.CWebSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * inspector class to create a visual view of an object
 */
public class CInspector
{
    /**
     * set with all communicators
     */
    private final Set<CWebSocket.CCommunicator> m_communicator = Collections.synchronizedSet( new HashSet<>() );


    public CInspector()
    {
        // set via reflection the inspector
        try
        {
            if ( CSimulation.getInstance().getUIComponents().getInspector() == null )
                CReflection.getClassField( CSimulation.getInstance().getUIComponents().getClass(), "m_inspector" ).getSetter().invoke(
                        CSimulation.getInstance().getUIComponents(), this
                );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }


    /**
     * sets a new object
     *
     * @param p_object object
     */
    public final void set( final IInspector p_object )
    {
        final Map<String, Object> l_data = p_object.inspect();
        if ( ( l_data == null ) || ( l_data.isEmpty() ) )
            return;

        for ( CWebSocket.CCommunicator l_item : m_communicator )
            try
            {
                l_item.send( CCommon.toJson( l_data ) );
            }
            catch ( IOException e )
            {
            }
    }


    /**
     * UI method - register sockets
     */
    private void web_dynamic_show( final CWebSocket.EAction p_action, final CWebSocket.CCommunicator p_communicator, final CWebSocket.CFrame p_frame )
    {
        switch ( p_action )
        {
            case Open:
                m_communicator.add( p_communicator );
                break;

            case Close:
                m_communicator.remove( p_communicator );
                break;

            default:
        }
    }

}


