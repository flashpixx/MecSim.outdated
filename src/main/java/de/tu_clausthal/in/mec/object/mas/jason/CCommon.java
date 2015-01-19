/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.mas.jason;

import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;

import java.util.*;


/**
 * Jason common functions
 */
public class CCommon
{

    /**
     * converts a map into Jason literals
     *
     * @param p_data map with data
     * @return literal list
     */
    public static List<Literal> getLiteralList( Map<String, Object> p_data )
    {
        List<Literal> l_literals = new LinkedList();
        if ( p_data == null )
            return l_literals;

        for ( Map.Entry<String, Object> l_data : p_data.entrySet() )
        {
            // null value into atom
            if ( l_data.getValue() == null )
            {
                l_literals.add( ASSyntax.createAtom( l_data.getKey() ) );
                continue;
            }

            // number value into number
            if ( l_data.getValue() instanceof Number )
            {
                l_literals.add( ASSyntax.createLiteral( l_data.getKey(), ASSyntax.createNumber( ( (Number) l_data.getValue() ).doubleValue() ) ) );
                continue;
            }

            // collection into term list
            if ( l_data.getValue() instanceof Collection )
            {
                l_literals.add( ASSyntax.createLiteral( l_data.getKey(), ASSyntax.createList( (Collection) l_data.getValue() ) ) );
                continue;
            }

            // otherwise into string
            l_literals.add( ASSyntax.createLiteral( l_data.getKey(), ASSyntax.createString( l_data.getValue().toString() ) ) );

        }

        return l_literals;
    }

}
