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
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.general.*;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import de.tu_clausthal.in.mec.object.mas.jason.general.CBeliefBase;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * belief structure to bind object properties
 */
public class CFieldBind extends CBeliefBase
{
    /**
     * field filter
     */
    private static final CFieldFilter c_filter = new CFieldFilter();
    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    private final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();

    /**
     * ctor bind an object
     *
     * @param p_name   name / annotation of the bind object
     * @param p_object bind object
     */
    public CFieldBind(final String p_name, final Object p_object)
    {
        this.push(p_name, p_object);
    }

    /**
     * clears top level and inherited literals and bindings
     */
    @Override
    public void clear()
    {
        m_literals.clear();
        m_bind.clear();

        for (String l_name : m_beliefbases.keySet())
            m_beliefbases.get(l_name).clear();
    }

    /**
     * update beliefs by calling getter functions of binded objects
     */
    public final void update()
    {
        // iterate over all binded objects
        for (final Map.Entry<String, Pair<Object, Map<String, CReflection.CGetSet>>> l_item : m_bind.entrySet())

            // iterate over all object fields
            for (final Map.Entry<String, CReflection.CGetSet> l_fieldref : l_item.getValue().getRight().entrySet())
                try
                {
                    // invoke / call the getter of the object field - field name will be the belief name, return value
                    // of the getter invoke call is set for the belief value
                    final Literal l_literal = CCommon.getLiteral(
                            l_fieldref.getKey(), l_fieldref.getValue().getGetter().invoke(
                                    l_item.getValue().getLeft()
                            )
                    );

                    // add the annotation to the belief and push it to the main list for reading later (within the agent)
                    l_literal.addAnnot(ASSyntax.createLiteral("source", ASSyntax.createAtom(l_item.getKey())));
                    m_literals.add( (ILiteral) CCommon.convertGeneric( l_literal ) );

                } catch (final Exception l_exception)
                {
                    CLogger.error(
                            de.tu_clausthal.in.mec.common.CCommon.getResourceString(
                                    this, "getter", l_item.getKey(), l_fieldref.getKey(), l_exception.getMessage()
                            )
                    );
                } catch (final Throwable l_throwable)
                {
                    CLogger.error(
                            de.tu_clausthal.in.mec.common.CCommon.getResourceString(
                                    this, "getter", l_item.getKey(), l_fieldref.getKey(), l_throwable.getMessage()
                            )
                    );
                }
    }

    /**
     * adds / binds an object
     *
     * @param p_name   name / annotation of the object
     * @param p_object object
     */
    public final void push(final String p_name, final Object p_object)
    {
        m_bind.put(
                p_name, new ImmutablePair<Object, Map<String, CReflection.CGetSet>>(
                        p_object, CReflection.getClassFields(
                        p_object.getClass(), c_filter
                )
                )
        );
    }

    /**
     * removes an object from the bind
     *
     * @param p_name name
     */
    public final void remove(final String p_name)
    {
        m_bind.remove(p_name);
    }

    public ITermCollection getAnnotation()
    {
        return null;
    }

    public IAtom<?> getFunctor()
    {
        return null;
    }

    public ITermCollection getValues()
    {
        return null;
    }

    public boolean instanceOf(Class<?> p_class)
    {
        return false;
    }
}
