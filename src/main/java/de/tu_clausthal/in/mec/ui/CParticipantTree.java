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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.message.CMessageSystem;
import de.tu_clausthal.in.mec.simulation.message.IMessage;
import de.tu_clausthal.in.mec.simulation.message.IParticipant;
import prefuse.Display;
import prefuse.Visualization;


/**
 * graph of all message participants
 *
 * @see https://github.com/prefuse/Prefuse
 */
public class CParticipantTree extends Display implements CMessageSystem.IActionListener
{

    public CParticipantTree()
    {
        super( new Visualization() );
        CSimulation.getInstance().getMessageSystem().addActionListener( this );
    }


    @Override
    public final void onRegister( final CPath p_path, final IParticipant p_receiver )
    {

    }

    @Override
    public final void onUnregister( final CPath p_path, final IParticipant p_receiver )
    {

    }

    @Override
    public void onPushMessage( final CPath p_path, final IMessage<?> p_message )
    {

    }
}
