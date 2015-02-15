/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.object.mas.jason.action;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.jason.CFieldFilter;
import jason.asSemantics.Agent;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * action to synchronize bind-object with agent value
 */
public class CFieldBind extends IAction
{

    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    protected Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();


    /**
     * ctor - default
     */
    public CFieldBind()
    {

    }


    /**
     * ctor bind an object
     *
     * @param p_name   name / annotation of the bind object
     * @param p_object bind object
     */
    public CFieldBind( String p_name, Object p_object )
    {
        this.push( p_name, p_object, null );
    }


    /**
     * ctor
     *
     * @param p_name           name / annotation of the bind object
     * @param p_object         bind object
     * @param p_forbiddennames set with forbidden field names of the object fields
     */
    public CFieldBind( String p_name, Object p_object, Set<String> p_forbiddennames )
    {
        this.push( p_name, p_object, p_forbiddennames );
    }


    /**
     * adds / binds an object
     *
     * @param p_name   name / annotation of the object
     * @param p_object object
     */
    public void push( String p_name, Object p_object )
    {
        this.push( p_name, p_object, null );
    }


    /**
     * adds a new bind object
     *
     * @param p_name           name / annotation of the object
     * @param p_object         object
     * @param p_forbiddennames set with forbidden names of the object fields
     */
    public void push( String p_name, Object p_object, Set<String> p_forbiddennames )
    {
        m_bind.put( p_name, new ImmutablePair<Object, Map<String, CReflection.CGetSet>>( p_object, CReflection.getClassFields( p_object.getClass(), new CFieldFilter( p_forbiddennames ) ) ) );
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


    @Override
    /**
     * @todo handle term list
     */
    public void act( Agent p_agent, Structure p_args )
    {

        // check number of argument first
        List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 3 )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "argument" ) );

        // first argument is the object name - try to get object
        String l_objectname = l_args.get( 0 ).toString();
        Pair<Object, Map<String, CReflection.CGetSet>> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "object", l_objectname ) );

        // second argument is the field name - try to get the field
        String l_fieldname = l_args.get( 1 ).toString();
        CReflection.CGetSet l_handle = l_object.getRight().get( l_fieldname );
        if ( l_handle == null )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "fieldnotfound", l_fieldname, l_objectname ) );


        try
        {
            // set value with the setter handle
            if ( l_args.get( 2 ).isNumeric() )
                l_handle.getSetter().invoke( l_object.getLeft(), (Number) ( (NumberTerm) l_args.get( 2 ) ).solve() );
            else
                l_handle.getSetter().invoke( l_object.getLeft(), l_args.get( 2 ) );
        }
        catch ( Throwable throwable )
        {
            throwable.printStackTrace();
        }
    }
}
