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
public class CPushBack extends IAction
{

    /**
     * bind object *
     */
    protected Map<String, Object> m_bind = new HashMap();
    /**
     * set with forbidden field names *
     */
    protected Map<String, Set<String>> m_forbidden = new HashMap();


    /**
     * ctor - default
     */
    public CPushBack()
    {

    }

    /**
     * ctor bind an object
     *
     * @param p_name name of the binding object
     * @param p_bind object
     */
    public CPushBack( String p_name, Object p_bind )
    {
        m_forbidden.put( p_name, new HashSet() );
        m_bind.put( p_name, p_bind );
    }


    /**
     * adds a new bind object
     *
     * @param p_name   name
     * @param p_object bind object
     */
    public void push( String p_name, Object p_object )
    {
        m_forbidden.put( p_name, new HashSet() );
        m_bind.put( p_name, p_object );
    }


    /**
     * removes an object from the bind
     *
     * @param p_name name
     * @return bind object
     */
    public Object remove( String p_name )
    {
        m_forbidden.remove( p_name );
        return m_bind.get( p_name );
    }

    /**
     * returns the forbidden set
     *
     * @return set with forbidden names
     */
    public Set<String> getForbidden( String p_name )
    {
        return m_forbidden.get( p_name );
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
        if ( l_data.size() < 3 )
            throw new IllegalArgumentException( "arguments are incorrect" );

        // first & second argument must changed to a string (cast calls are not needed, we use the object string call)
        String l_objectname = l_data.get( 0 ).toString();
        Object l_target = m_bind.get( l_objectname );
        if ( l_target == null )
            throw new IllegalArgumentException( "object key [" + l_objectname + "] not found" );

        String l_fieldname = l_data.get( 1 ).toString();
        try
        {

            for ( String l_name : m_forbidden.get( l_objectname ) )
                if ( l_name.equals( l_fieldname ) )
                    throw new IllegalAccessException( "field [" + l_fieldname + "] not accessible for the object [" + l_objectname + "]" );

            CCommon.getClassField( l_target.getClass(), l_fieldname ).set( l_target, 0 );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }

    }
}
