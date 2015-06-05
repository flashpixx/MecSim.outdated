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

package de.tu_clausthal.in.mec.object.mas.jason.general;

import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IDefaultBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import jason.asSyntax.Literal;

import java.util.Map;
import java.util.Set;

/**
 * class for agent beliefbase
 */
public class CBeliefBase extends IDefaultBeliefBase<Literal>
{
    protected CBeliefBase(Set<ILiteral<Literal>> p_literals)
    {
        super(p_literals);
    }

    protected CBeliefBase(Map<String, IDefaultBeliefBase<Literal>> p_beliefbases, Set<ILiteral<Literal>> p_literals)
    {
        super(p_beliefbases, p_literals);
    }

    /**
     * returns a single beliefbase containing
     * the top level and all inherited literals.
     *
     * @return aggregated beliefbase
     */
    @Override
    public IBeliefBase<Literal> collapseBeliefbase()
    {
        return new CBeliefBase( this.collapseLiterals() );
    }

    /**
     * method for updating the agents beliefbase,
     * i.e. read and store the values of binded objects
     */
    @Override
    public void update()
    {

    }
}
