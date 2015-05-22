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

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;


/**
 * splits up literals to access the functor or
 * single terms into a belief
 */
public class CGetFunctor extends DefaultInternalAction
{
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
    public Object execute( final TransitionSystem p_transitionsystem, final Unifier p_unifier, final Term[] p_args ) throws Exception
    {
        if ( ( p_args[0].isLiteral() ) && ( p_args[1].isLiteral() ) )
        {
            p_transitionsystem.getAg().addBel(
                    ASSyntax.createLiteral(
                            ( (Literal) p_args[1] ).getFunctor(), ASSyntax.createLiteral(
                                    ( (Literal) p_args[0] ).getFunctor()
                            )
                    )
            );
            return true;
        }

        return false;
    }
}