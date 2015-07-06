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

import de.tu_clausthal.in.mec.common.CPath;

import java.util.List;
import java.util.Set;


/**
 * default generic literal class for agent beliefs
 * a literal consists of a functor, an optional list of values and
 * an optional set of annotations, e.g. speed(50)[source(self)]
 *
 * @todo define constructors for TermSets and TermLists
 */
public class CDefaultLiteral<T> implements ILiteral<T>
{
    /**
     * the literal annotations
     */
    protected final ITermCollection m_annotations;
    /**
     * the literals functor
     */
    private IAtom<String> m_functor;
    /**
     * the original agent specific literal (i.e. Jason, Goal, 2APL)
     */
    protected final T m_literal;
    /**
     * the literal values
     */
    protected final ITermCollection m_values;


    /**
     * ctor
     *
     * @param p_functor functor
     * @param p_literal literal
     */
    public CDefaultLiteral( final String p_functor, final T p_literal )
    {
        this( p_functor, p_literal, new CTermList(), new CTermSet() );
    }

    /**
     * ctor
     *
     * @param p_functor functor of the literal
     * @param p_literal the original literal
     * @param p_values initial list of values
     * @param p_annotations initial set of annotations
     */
    public CDefaultLiteral( final String p_functor, final T p_literal, final List<ITerm> p_values, final Set<ITerm> p_annotations )
    {
        this( new CStringAtom( p_functor ), p_literal, new CTermList( p_values ), new CTermSet( p_annotations ) );
    }

    /**
     * ctor
     *
     * @param p_functor functor of the literal
     * @param p_literal the original literal
     * @param p_values initial list of values
     * @param p_annotations initial set of annotations
     */
    public CDefaultLiteral( final CStringAtom p_functor, final T p_literal, final ITermCollection p_values, final ITermCollection p_annotations )
    {
        m_functor = p_functor;
        m_literal = p_literal;
        m_values = p_values;
        m_annotations = p_annotations;
    }


    @Override
    public ILiteral<T> clone( final CPath p_prefix )
    {
        return new CDefaultLiteral<T>( new CStringAtom( p_prefix.append( m_functor.get() ).toString() ), m_literal, m_values, m_annotations );
    }

    @Override
    public ITermCollection getAnnotation()
    {
        return m_annotations;
    }

    @Override
    public final boolean isBeliefbase()
    {
        return false;
    }

    @Override
    public final boolean isLiteral()
    {
        return true;
    }

    @Override
    public IAtom<String> getFunctor()
    {
        return m_functor;
    }

    @Override
    public T getLiteral()
    {
        return m_literal;
    }

    @Override
    public ITermCollection getValues()
    {
        return m_values;
    }

    @Override
    public final int hashCode()
    {
        return 41 * m_functor.hashCode() +
               43 * m_values.hashCode() +
               59 * m_annotations.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public String toString()
    {
        return m_functor.toString() + m_values.toString() + m_annotations.toString();
    }

    @Override
    public boolean instanceOf( final Class<?> p_class )
    {
        return ILiteral.class.isAssignableFrom( p_class );
    }

}
