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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.ui.web.CSocketStorage;
import de.tu_clausthal.in.mec.ui.web.CWebSocket;

import java.util.Map;


/**
 * inspector class to create a visual view of an object
 */
public final class CInspector
{
    /**
     * set with all communicators
     */
    private final CSocketStorage m_communicator = new CSocketStorage();

    public CInspector()
    {
        CSimulation.getInstance().getStorage().add( "inspector", this );
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

        m_communicator.send( CCommon.toJson( l_data ) );
    }


    /**
     * UI method - register sockets
     */
    private void web_dynamic_show( final CWebSocket.EAction p_action, final CWebSocket.CCommunicator p_communicator, final CWebSocket.CFrame p_frame )
    {
        m_communicator.handshake( p_action, p_communicator );
    }

}


