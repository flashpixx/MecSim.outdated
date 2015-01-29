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


import de.tu_clausthal.in.mec.common.CCommon;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.*;


/**
 * creates a action to synchronize bind-object with agent
 */
public class CPushBack<T> extends IAction
{

    /**
     * bind object *
     */
    protected T m_bind = null;
    /**
     * set with forbidden field names *
     */
    protected Set<String> m_forbidden = new HashSet();

    /** ctor - with bind object
     *
     * @param p_bind bind object
     */
    public CPushBack( T p_bind )
    {
        if ( p_bind == null )
            throw new IllegalArgumentException( "bind object need not to be null" );

        m_bind = p_bind;
    }

    /**
     * returns the forbidden set
     *
     * @return set with forbidden names
     */
    public Set<String> getForbidden()
    {
        return m_forbidden;
    }


    @Override
    public String getName()
    {
        return "pushback";
    }

    @Override
    public void act( Structure p_args )
    {

        // check number of argument first
        List<Term> l_data = p_args.getTerms();
        if ( l_data.size() < 2 )
            throw new IllegalArgumentException( "arguments are incorrect" );

        // first argument must changed to a string (cast calls are not needed, we use the object string call)
        String l_fieldname = l_data.get( 0 ).toString();

        System.out.println();
        try
        {

            for ( String l_name : m_forbidden )
                if ( l_name.equals( l_fieldname ) )
                    throw new IllegalAccessException( "field [" + l_fieldname + "] not accessible" );

            CCommon.getClassField( m_bind.getClass(), l_fieldname ).set( m_bind, 0 );

        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( "field name [" + l_fieldname + "] not found" );
        }

    }
}
