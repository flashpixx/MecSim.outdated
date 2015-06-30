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

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.general.CDefaultLiteral;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;


/**
 * class for literals
 *
 * @todo move class to belief package - after moving CBeliefBase
 */
public class CLiteral extends CDefaultLiteral<Literal>
{
    /**
     * ctor
     *
     * @param p_literal
     */
    public CLiteral(final Literal p_literal)
    {
        this(p_literal, CPath.EMPTY);
    }

    /**
     * ctor with path specified
     */
    public CLiteral(final Literal p_literal, final CPath p_path)
    {
        super(p_path.append(p_literal.getFunctor()).toString(), p_literal);

        if (p_literal.hasTerm())
            for (final Term l_term : p_literal.getTerms())
                m_values.add(CCommon.convertGeneric(l_term));

        if (p_literal.hasAnnot())
            for (final Term l_term : p_literal.getAnnots())
                m_annotations.add(CCommon.convertGeneric(l_term));
    }
}
