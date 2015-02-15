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

package de.tu_clausthal.in.mec.simulation.message;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.simulation.CSimulation;

import java.util.Set;


/**
 * participant class for event messager
 */
public class CParticipant implements IParticipant
{

    /**
     * owner object *
     */
    protected IReceiver m_owner = null;


    /**
     * ctor to register an object *
     */
    public CParticipant( IReceiver p_owner )
    {
        if ( p_owner == null )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "ownernull" ) );

        m_owner = p_owner;
        CSimulation.getInstance().getMessageSystem().register( this.getReceiverPath(), this );
    }

    /**
     * release *
     */
    public void release()
    {
        CSimulation.getInstance().getMessageSystem().unregister( m_owner.getReceiverPath(), this );
    }

    @Override
    public void sendMessage( CPath p_path, IMessage p_message )
    {
        CSimulation.getInstance().getMessageSystem().pushMessage( p_path, p_message );
    }

    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {
        m_owner.receiveMessage( p_messages );
    }

    @Override
    public CPath getReceiverPath()
    {
        return m_owner.getReceiverPath();
    }
}
