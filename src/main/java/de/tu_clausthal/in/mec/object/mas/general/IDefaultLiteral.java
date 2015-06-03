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

package de.tu_clausthal.in.mec.object.mas.general;

/**
 * Created by marcel on 03.06.15.
 * @todo add documentation + license
 */
public abstract class IDefaultLiteral<T> implements ILiteral<T>
{
    protected final ITermCollection m_values = new CTermList();
    protected final ITermCollection m_annotations = new CTermSet();
    private final T m_literal;
    private final IAtom<String> m_functor;

    protected IDefaultLiteral(final String p_functor, final T p_literal)
    {
        m_functor = new CAtom<>(p_functor);
        m_literal = p_literal;
    }

    @Override
    public ITermCollection getAnnotation()
    {
        return m_annotations;
    }

    @Override
    public IAtom<?> getFunctor()
    {
        return m_functor;
    }

    @Override
    public ITermCollection getValues()
    {
        return m_values;
    }

    @Override
    public T getLiteral()
    {
        return m_literal;
    }

    @Override
    public boolean instanceOf(final Class<?> p_class)
    {
        return ILiteral.class.isAssignableFrom(p_class);
    }

    @Override
    public final int hashCode()
    {
        return 41 * m_functor.hashCode() +
                43 * m_values.hashCode() +
                59 * m_annotations.hashCode();
    }

    @Override
    public final boolean equals(Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }
}
