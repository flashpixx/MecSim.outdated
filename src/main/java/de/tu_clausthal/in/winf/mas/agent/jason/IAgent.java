/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.mas.agent.jason;


import jason.JasonException;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.TransitionSystem;
import jason.runtime.Settings;

import java.util.List;


/**
 * @see http://jason.sourceforge.net/api/jason/asSemantics/Agent.html
 */
public abstract class IAgent<T> extends Agent {


    public IAgent( String p_asl, IArchitecture p_architecture ) throws JasonException {

        new TransitionSystem(this, new Circumstance(), new Settings(), p_architecture);
        this.initAg(p_asl);

    }


    public List<T> perceive() { return null; }

    public void act(ActionExec p_action, List<ActionExec> p_feedback) {}

    public void update() {}

}
