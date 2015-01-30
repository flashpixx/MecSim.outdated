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

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;


/**
 * belief structure to bind object properties
 */
public class CFieldBind implements IBelief
{
    /**
     * bind object *
     */
    protected Map<String, ImmutablePair<Object, Set<String>>> m_bind = new HashMap();


    /**
     * ctor - default
     */
    public CFieldBind()
    {

    }

    /**
     * ctor bind an object
     *
     * @param p_name   name of the binding object
     * @param p_object object
     */
    public CFieldBind( String p_name, Object p_object )
    {
        m_bind.put( p_name, new ImmutablePair<Object, Set<String>>( p_object, new HashSet() ) );
    }


    /**
     * adds a new bind object
     *
     * @param p_name   name
     * @param p_object bind object
     */
    public void push( String p_name, Object p_object )
    {
        m_bind.put( p_name, new ImmutablePair<Object, Set<String>>( p_object, new HashSet() ) );
    }


    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public void remove( String p_name )
    {
        m_bind.remove( p_name );
    }

    /**
     * returns the forbidden set
     *
     * @return set with forbidden names
     */
    public Set<String> getForbidden( String p_name )
    {
        ImmutablePair<Object, Set<String>> l_object = m_bind.get( p_name );
        if ( l_object == null )
            return null;

        return l_object.getRight();
    }


    @Override
    public Set<Literal> getAtoms()
    {
        return null;
    }

    @Override
    public Set<Literal> getLiterals()
    {
        return null;
    }
}
