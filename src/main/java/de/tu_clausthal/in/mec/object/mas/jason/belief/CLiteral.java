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

package de.tu_clausthal.in.mec.object.mas.jason.belief;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.generic.ILiteral;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;


/**
 * class for literals
 */
public class CLiteral extends de.tu_clausthal.in.mec.object.mas.generic.implementation.CLiteral<Literal>
{

    /**
     * ctor for Jason literals
     *
     * @param p_literal Jason literal
     */
    public CLiteral( final Literal p_literal )
    {
        super( p_literal.getFunctor(), p_literal, p_literal.negated() );

        if ( p_literal.hasTerm() )
            for ( final Term l_term : p_literal.getTerms() )
                m_values.add( CCommon.convertGeneric( l_term ) );

        if ( p_literal.hasAnnot() )
            for ( final Term l_term : p_literal.getAnnots() )
                m_annotations.add( CCommon.convertGeneric( l_term ) );
    }


    @Override
    public ILiteral<Literal> clone( final CPath p_prefix )
    {
        final Literal l_literal = ASSyntax.createLiteral( p_prefix.append( m_functor.get() ).toString() );
        l_literal.setAnnots( m_literal.getAnnots() );
        l_literal.setTerms( m_literal.getTerms() );
        l_literal.setNegated( m_literal.negated() );

        return new CLiteral( l_literal );
    }
}
