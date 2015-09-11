/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.Iterator;


/**
 * internal action for deleting beliefs directly,
 * based on their functor without event generation
 */
public class CBeliefRemove extends DefaultInternalAction
{

    @Override
    public int getMinArgs()
    {
        return 1;
    }

    @Override
    public int getMaxArgs()
    {
        return 1;
    }

    @Override
    public Object execute( final TransitionSystem p_transitionsystem, final Unifier p_unifier, final Term[] p_args ) throws Exception
    {
        if ( !p_args[0].isLiteral() )
            return false;

        for ( final Iterator<Literal> l_iterator = p_transitionsystem.getAg().getBB().getCandidateBeliefs( (Literal) p_args[0], p_unifier );
              l_iterator.hasNext(); )
        {
            final Literal l_literal = l_iterator.next();
            if ( !l_literal.isRule() )
                p_transitionsystem.getAg().delBel( l_literal );
        }

        return true;
    }

}
