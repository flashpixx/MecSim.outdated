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

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import de.tu_clausthal.in.mec.object.mas.jason.general.CBeliefBase;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;

/**
 * beliefbase for internal beliefs, i.e. the initial beliefs
 * and beliefs that arise from deduction rules
 *
 * @todo in update-method: add just literals which are not in other beliefbases
 */
public class CInternalBeliefBase extends CBeliefBase
{
    /**
     * agent object
     */
    private final Agent m_agent;

    /**
     * ctor with agent specified
     * reads in the initial beliefs from the agent
     *
     * @param p_agent
     */
    public CInternalBeliefBase( final Agent p_agent )
    {
        super( CCommon.convertGeneric( p_agent.getInitialBels() ) );
        m_agent = p_agent;
    }

    @Override
    public void update()
    {
        super.update();

        // clear old literals
        this.clear();

        // push agent beliefs into set of top-level literals
        addAll(CPath.EMPTY, CCommon.convertGeneric( m_agent.getBB() ) );
    }
}
