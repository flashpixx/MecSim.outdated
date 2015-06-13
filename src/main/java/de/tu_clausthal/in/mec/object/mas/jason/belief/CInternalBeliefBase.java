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

package de.tu_clausthal.in.mec.object.mas.jason.belief;

import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.jason.general.CBeliefBase;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;

import java.util.Set;

/**
 * beliefbase for internal beliefs, i.e. the initial beliefs
 * and beliefs that arise from deduction rules
 */
public class CInternalBeliefBase extends CBeliefBase
{
    /**
     * agent object
     */
    private Agent m_agent;

    /**
     * default ctor
     */
    public CInternalBeliefBase() { super(); }

    /**
     * ctor with initial literals specified
     *
     * @param p_literals
     */
    public CInternalBeliefBase( Set<ILiteral<Literal>> p_literals ) {super(p_literals);}

    @Override
    public void update()
    {
        // clear old literals
        super.clearLiterals();

        // get actual literals
        for( final Literal l_literal : m_agent.getBB() )
            super.addLiteral( l_literal );
    }
}
