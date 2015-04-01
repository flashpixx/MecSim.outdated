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

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import de.tu_clausthal.in.mec.object.mas.jason.CFieldFilter;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * belief structure to bind object properties
 */
public class CFieldBind implements IBelief
{

    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    protected final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();
    /**
     * set with literals
     */
    protected final Set<Literal> m_literals = new HashSet<>();


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
    public CFieldBind( final String p_name, final Object p_object )
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
    public CFieldBind( final String p_name, final Object p_object, final Set<String> p_forbiddennames )
    {
        this.push( p_name, p_object, p_forbiddennames );
    }


    /**
     * adds / binds an object
     *
     * @param p_name   name / annotation of the object
     * @param p_object object
     */
    public final void push( final String p_name, final Object p_object )
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
    public final void push( final String p_name, final Object p_object, final Set<String> p_forbiddennames )
    {
        m_bind.put( p_name, new ImmutablePair<Object, Map<String, CReflection.CGetSet>>( p_object, CReflection.getClassFields( p_object.getClass(), new CFieldFilter( p_forbiddennames ) ) ) );
    }

    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public final void remove( final String p_name )
    {
        m_bind.remove( p_name );
    }


    @Override
    public final Set<Literal> getLiterals()
    {
        return m_literals;
    }


    @Override
    public final void update()
    {
        // iterate over all binded objects
        for ( Map.Entry<String, Pair<Object, Map<String, CReflection.CGetSet>>> l_item : m_bind.entrySet() )

            // iterate over all object fields
            for ( Map.Entry<String, CReflection.CGetSet> l_fieldref : l_item.getValue().getRight().entrySet() )
                try
                {
                    // invoke / call the getter of the object field - field name will be the belief name, return value
                    // of the getter invoke call is set for the belief value
                    final Literal l_literal = CCommon.getLiteral( l_fieldref.getKey(), l_fieldref.getValue().getGetter().invoke( l_item.getValue().getLeft() ) );

                    // add the annotation to the belief and push it to the main list for reading later (within the agent)
                    l_literal.addAnnot( ASSyntax.createLiteral( "source", ASSyntax.createAtom( l_item.getKey() ) ) );
                    m_literals.add( l_literal );

                }
                catch ( final Exception l_exception )
                {
                    CLogger.error( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "getter", l_item.getKey(), l_fieldref.getKey(), l_exception.getMessage() ) );
                }
                catch ( final Throwable l_throwable )
                {
                    CLogger.error( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "getter", l_item.getKey(), l_fieldref.getKey(), l_throwable.getMessage() ) );
                }
    }

    @Override
    public final void clear()
    {
        m_literals.clear();
    }
}
