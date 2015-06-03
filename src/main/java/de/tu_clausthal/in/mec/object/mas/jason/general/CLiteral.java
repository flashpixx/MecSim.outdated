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

package de.tu_clausthal.in.mec.object.mas.jason.general;

import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.general.*;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class for literals
 */
public class CLiteral implements ILiteral
{
    private final String m_functor;
    private final ITermList m_values = new CTermList();
    private final ITermSet m_annotations = new CTermSet();

    /**
     * bind objects - map uses a name / annotation as key value and a pair of object and the map of fields and getter /
     * setter handles, so each bind can configurate individual
     */
    private final Map<String, Pair<Object, Map<String, CReflection.CGetSet>>> m_bind = new HashMap<>();

    /**
     * default ctor - a literal at least contains a functor
     *
     * @param p_functor
     */
    public CLiteral(final String p_functor)
    {
        m_functor = p_functor;
    }

    /**
     * add a list of terms to the values
     *
     * @param p_listTerm
     */
    public void addValue(final ListTerm p_listTerm)
    {
        for (final Term l_term : p_listTerm)
            m_values.add(CCommon.convertGeneric(l_term));
    }

    /**
     * add a list of terms to the values
     *
     * @param p_listTerm
     */
    public void addValue(final List<Term> p_listTerm)
    {
        m_values.addAll(CCommon.convertGeneric(p_listTerm));
    }

    /**
     * add a single term to the values
     *
     * @param p_term
     */
    public void addValue(final Term p_term)
    {
        m_values.add(CCommon.convertGeneric(p_term));
    }

    public void addAnnotation(final ListTerm p_listTerm)
    {
        for (final Term l_term : p_listTerm)
            m_annotations.add(CCommon.convertGeneric(l_term));
    }

    /**
     * add a term to annotations
     *
     * @param p_term
     */
    public void addAnnotation(final Term p_term)
    {
        m_annotations.add(CCommon.convertGeneric(p_term));
    }

    /**
     * getter for annotation set
     *
     * @return annotation set
     */
    @Override
    public ITermSet getAnnotation()
    {
        return m_annotations;
    }

    /**
     * getter for literal functor
     *
     * @return functor atom
     */
    @Override
    public IAtom<?> getFunctor()
    {
        return new IAtom<String>()
        {
            /**
             * checks if String is assignable from given class
             *
             * @param p_class matching class
             * @return true if String is assignable from matching class
             */
            @Override
            public boolean instanceOf(Class<?> p_class)
            {
                return String.class.isAssignableFrom(p_class);
            }

            /**
             * getter for functor
             *
             * @return string representation for functor
             */
            @Override
            public String get()
            {
                return m_functor;
            }
        };
    }

    /**
     * getter for values
     *
     * @return literal values
     */
    @Override
    public ITermList getValues()
    {
        return m_values;
    }

    /**
     * clears values and annotations
     */
    @Override
    public void clear()
    {
        m_annotations.clear();
        m_values.clear();
    }

    @Override
    public void update()
    {

    }

    /**
     * checks if ILiteral is assignable from given class
     *
     * @param p_class matching class
     * @return true if ILiteral is assignable from matching class
     */
    @Override
    public boolean instanceOf(Class<?> p_class)
    {
        return ILiteral.class.isAssignableFrom( p_class );
    }
}
