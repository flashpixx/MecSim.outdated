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

package de.tu_clausthal.in.mec.object.mas.jason.action;


import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.IPropertyBind;
import jason.asSemantics.Agent;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * action to synchronize bind-object with agent value
 */
public final class CPropertyBind extends IPropertyBind<Agent, Structure>
{

    /**
     * ctor - default
     */
    public CPropertyBind()
    {
        super();
    }


    /**
     * ctor bind an object
     *
     * @param p_name name of the binding object
     * @param p_object object
     */
    public CPropertyBind( final String p_name, final Object p_object )
    {
        super( p_name, p_object );
    }


    /**
     * ctor
     *
     * @param p_name name / annotation of the bind object
     * @param p_object bind object
     * @param p_forbiddennames set with forbidden field names of the object fields
     */
    public CPropertyBind( final String p_name, final Object p_object, final Set<String> p_forbiddennames )
    {
        super( p_name, p_object, p_forbiddennames );
    }


    @Override
    public final void act( final Agent p_agent, final Structure p_args )
    {
        // check number of argument first
        final List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 3 )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "argument" ) );

        // first argument is the object name - try to get object (Jason has got a bug implemention, it pass the quotes to the toString() method, so the quotes must be removed)
        final String l_objectname = de.tu_clausthal.in.mec.object.mas.jason.CCommon.clearString( l_args.get( 0 ).toString() );
        final Pair<Object, Map<String, CReflection.CGetSet>> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "object", l_objectname ) );

        // second argument is the field name - try to get the field
        final String l_fieldname = de.tu_clausthal.in.mec.object.mas.jason.CCommon.clearString( l_args.get( 1 ).toString() );
        final CReflection.CGetSet l_handle = l_object.getRight().get( l_fieldname );
        if ( l_handle == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "fieldnotfound", l_fieldname, l_objectname ) );
        if ( Modifier.isFinal( l_handle.getField().getModifiers() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "fieldfinal", l_fieldname, l_objectname ) );


        try
        {
            l_handle.getSetter().invoke(
                    l_object.getLeft(), de.tu_clausthal.in.mec.object.mas.jason.CCommon.getJavaValue(
                            l_args.get( 2 ), l_handle.getField().getType()
                    )
            );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }

}
