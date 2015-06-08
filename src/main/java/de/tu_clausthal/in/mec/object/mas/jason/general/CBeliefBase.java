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
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.Map;
import java.util.Set;

/**
 * class for agent beliefbase
 */
public class CBeliefBase extends IDefaultBeliefBase<Literal>
{
    /**
     * ctor - default
     */
    public CBeliefBase()
    {
        super();
    }
    /**
     * ctor - just the top-level literals are specified
     *
     * @param p_literals top level literals
     */
    public CBeliefBase(final Set<ILiteral<Literal>> p_literals)
    {
        super(p_literals);
    }
    /**
     * ctor - top-level literals and inherited beliefbases are specified
     *
     * @param p_beliefbases inherited beliefbases
     * @param p_literals top level literals
     */
    public CBeliefBase(final Map<String, IBeliefBase<Literal>> p_beliefbases, final Set<ILiteral<Literal>> p_literals)
    {
        super(p_beliefbases, p_literals);
    }

    /**
     * collapse method to get an aggregated top-level-beliefbase,
     * which is the aggregation of the top-level literals and all
     * the inherited beliefbases' literals.
     *
     * @return beliefbase containing literals of all inherited beliefbases
     */
    @Override
    public IBeliefBase<Literal> collapseBeliefbase()
    {
        return new CBeliefBase( this.collapseLiterals() );
    }

    /**
     * method to update the beliefbase
     */
    @Override
    public void update()
    {
    }

    /**
     * adds a language specific literal to the top-level literals
     *
     * @param p_literal language specific literal
     */
    public void addLiteral( final Literal p_literal )
    {
        addLiteral( CCommon.convertGeneric( p_literal ) );
    }

    /**
     * removes a language specific literal from the top level literals
     *
     * @param p_literal language specific literal
     */
    public void removeLiteral( final Literal p_literal )
    {
        removeLiteral( CCommon.convertGeneric( p_literal ) );
    }

}
