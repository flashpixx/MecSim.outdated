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

import de.tu_clausthal.in.mec.object.mas.general.IDefaultLiteral;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

/**
 * class for literals
 * @todo add deocumentation
 */
public class CLiteral extends IDefaultLiteral<Literal>
{
    public CLiteral(String p_functor, Literal p_literal)
    {
        super(p_functor, p_literal);

        for (final Term l_term : p_literal.getTerms())
            m_values.add(CCommon.convertGeneric(l_term));

        for (final Term l_term : p_literal.getAnnots())
            m_annotations.add(CCommon.convertGeneric(l_term));
    }
}
