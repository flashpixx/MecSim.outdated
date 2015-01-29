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

package de.tu_clausthal.in.mec.object.mas.jason.actions;


import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.List;


/**
 * creates a action to synchronize bind-object with agent
 */
public class CPushBack<T> extends IAction
{
    protected T m_bind = null;


    public CPushBack( T p_bind )
    {
        if ( p_bind == null )
            throw new IllegalArgumentException( "bind object need not to be null" );

        m_bind = p_bind;
    }


    @Override
    public String getName()
    {
        return "pushback";
    }

    @Override
    public void act( Structure p_args )
    {
        List<Term> l_data = p_args.getTerms();
        if ( l_data.size() < 2 )
            throw new IllegalArgumentException( "arguments are incorrect" );

        //m_bind.getClass().getDeclaredField(  )
    }
}
