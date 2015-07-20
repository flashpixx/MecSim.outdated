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
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.implementation.IBindStorage;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * beliefbase structure to bind object properties
 */
public class CBindingStorage extends IBindStorage<ILiteral<Literal>, IBeliefBaseMask<Literal>>
{
    /**
     * string that is removed from a literal name
     */
    private final String m_literalreplace;

    /**
     * default ctor
     *
     * @param p_replace replace literal string
     */
    public CBindingStorage( final String p_replace )
    {
        super();
        m_literalreplace = p_replace;
    }

    /**
     * ctor bind an object
     *
     * @param p_replace replace literal string
     * @param p_name name / annotation of the bind object
     * @param p_object bind object
     */
    public CBindingStorage( final String p_replace, final String p_name, final Object p_object )
    {
        super( p_name, p_object );
        m_literalreplace = p_replace;
    }

    @Override
    protected void updating()
    {
        // iterate over all binded objects
        for ( final Map.Entry<String, Pair<Object, Map<String, CReflection.CGetSet>>> l_item : m_bind.entrySet() )

            // iterate over all object fields
            for ( final Map.Entry<String, CReflection.CGetSet> l_fieldref : l_item.getValue().getRight().entrySet() )
                try
                {
                    // invoke / call the getter of the object field - field name will be the belief name, return value
                    // of the getter invoke call is set for the belief value
                    // replace separator to avoid path splitting
                    final Literal l_literal = CCommon.getLiteral(
                            l_fieldref.getKey().replace( m_literalreplace, "" ), l_fieldref.getValue().getGetter().invoke(
                                    l_item.getValue().getLeft()
                            )
                    );

                    // add the annotation to the belief and push it to the main list for reading later (within the agent)
                    l_literal.addAnnot( ASSyntax.createLiteral( "source", ASSyntax.createAtom( l_item.getKey() ) ) );

                    final Set<ILiteral<Literal>> l_elements = m_multielements.getOrDefault( l_literal.getFunctor(), new HashSet<>() );
                    l_elements.add(
                            new CLiteral(
                                    l_literal
                            )
                    );
                    m_multielements.put( l_literal.getFunctor(), l_elements );
                }
                catch ( final Exception l_exception )
                {
                    CLogger.error(
                            de.tu_clausthal.in.mec.common.CCommon.getResourceString(
                                    this, "getter", l_item.getKey(), l_fieldref.getKey(), l_exception.getMessage()
                            )
                    );
                }
                catch ( final Throwable l_throwable )
                {
                    CLogger.error(
                            de.tu_clausthal.in.mec.common.CCommon.getResourceString(
                                    this, "getter", l_item.getKey(), l_fieldref.getKey(), l_throwable.getMessage()
                            )
                    );
                }
    }

}
