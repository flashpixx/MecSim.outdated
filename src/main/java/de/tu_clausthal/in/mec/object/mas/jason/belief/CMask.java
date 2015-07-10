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


import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import jason.asSyntax.Literal;
import jason.bb.BeliefBase;


/**
 * beliefbase mask that represent the Jason beliefbase
 */
public class CMask extends de.tu_clausthal.in.mec.object.mas.general.implementation.CMask<Literal> implements BeliefBase
{

    /**
     * ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase )
    {
        super( p_name, p_beliefbase );
    }

    /**
     * private ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     * @param p_parent reference to the parent mask
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase,
            final IBeliefBaseMask<Literal> p_parent
    )
    {
        super( p_name, p_beliefbase, p_parent );
    }
}
