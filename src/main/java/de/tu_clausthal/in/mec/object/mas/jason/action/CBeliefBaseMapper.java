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

package de.tu_clausthal.in.mec.object.mas.jason.action;

import de.tu_clausthal.in.mec.common.CPath;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

import java.util.Map;


/**
 * class for internal action for mapping literals to getBeliefbases
 * based on the literals functor
 */
public class CBeliefBaseMapper extends DefaultInternalAction
{
    /**
     * map which contains functor-path mapping
     * to locate literals in inherited getBeliefbases
     */
    final Map<String, CPath> m_mapping;

    /**
     * ctor with initial mapping
     *
     * @param p_mapping
     */
    public CBeliefBaseMapper( final Map<String, CPath> p_mapping )
    {
        m_mapping = p_mapping;
    }

    @Override
    public int getMinArgs()
    {
        return 2;
    }

    @Override
    public int getMaxArgs()
    {
        return 2;
    }

    @Override
    public Object execute( final TransitionSystem ts, final Unifier un, final Term[] args ) throws Exception
    {
        return m_mapping.put( args[0].toString(), new CPath( args[1].toString() ) );
    }
}
