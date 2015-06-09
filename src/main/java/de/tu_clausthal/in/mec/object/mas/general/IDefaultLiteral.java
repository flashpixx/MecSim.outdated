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
 * default generic literal class for agent beliefs
 * a literal consists of a functor, an optional list of values and
 * an optional set of annotations, e.g. speed(50)[source(self)]
 */
public abstract class IDefaultLiteral<T> implements ILiteral<T>
{
    /**
     * the literals functor
     */
    private final IAtom<String> m_functor;
    /**
     * the literal values
     */
    protected final ITermCollection m_values = new CTermList();
    /**
     * the literal annotations
     */
    protected final ITermCollection m_annotations = new CTermSet();
    /**
     * the original agent specific literal (i.e. Jason, Goal, 2APL)
     */
    private final T m_literal;

    /**
     * default ctor
     *
     * @param p_functor functor of the literal
     * @param p_literal the original literal
     */
    protected IDefaultLiteral(final String p_functor, final T p_literal)
    {
        m_functor = new CStringAtom(p_functor);
        m_literal = p_literal;
    }

    /**
     * getter method for annotation
     *
     * @return the literals annotation
     */
    @Override
    public ITermCollection getAnnotation()
    {
        return m_annotations;
    }

    /**
     * getter method for functor
     *
     * @return the literals functor
     */
    @Override
    public IAtom<?> getFunctor()
    {
        return m_functor;
    }

    /**
     * getter method for values
     *
     * @return the literals values
     */
    @Override
    public ITermCollection getValues()
    {
        return m_values;
    }

    /**
     * getter method for original literal
     *
     * @return original literal
     */
    @Override
    public T getLiteral()
    {
        return m_literal;
    }

    /**
     * check for literal class type
     *
     * @param p_class matching class
     * @return true if ILiteral is assignable from given class
     */
    @Override
    public boolean instanceOf(final Class<?> p_class)
    {
        return ILiteral.class.isAssignableFrom(p_class);
    }

    /**
     * hashcode method based on prime number linear combination
     *
     * @return literal hashcode
     */
    @Override
    public final int hashCode()
    {
        return 41 * m_functor.hashCode() +
                43 * m_values.hashCode() +
                59 * m_annotations.hashCode();
    }

    /**
     * method for equivalence check
     *
     * @param p_object
     * @return true if the literals equals a given object
     */
    @Override
    public final boolean equals(final Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }

    /**
     * get the literals string representation
     */
    @Override
    public String toString()
    {
        return m_functor.toString() + m_values.toString() + m_annotations.toString();
    }
}
