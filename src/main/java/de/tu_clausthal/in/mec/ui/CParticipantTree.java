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
import de.tu_clausthal.in.mec.simulation.message.IParticipant;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * tree of all message participants
 */
public class CParticipantTree extends JPanel implements CMessageSystem.IActionListener
{

    protected final JTree m_root = new JTree( new DefaultMutableTreeNode( "Message Participant" ) );


    public CParticipantTree()
    {
        CSimulation.getInstance().getMessageSystem().addActionListener( this );
        this.add( m_root );
    }


    @Override
    public void onRegister( CPath p_path, IParticipant p_receiver )
    {

    }

    @Override
    public void onUnregister( CPath p_path, IParticipant p_receiver )
    {

    }
}
