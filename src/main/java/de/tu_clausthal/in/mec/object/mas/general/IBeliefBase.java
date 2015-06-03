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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * agent belief base
 * @todo DefaultBeliefBase als Interface und abstrakte Klasse (diese umbenennen)
 * @todo add documentation
 * @todo equals und Hashcode ueberladen
 */
public abstract class IBeliefBase<T>
{
    /**
     * map of string/beliefbase
     */
    private Map<String, IBeliefBase<T>> m_beliefbases;
    /**
     * set of literals
     */
    private Set<ILiteral<T>> m_literals;

    /**
     * returns the aggregated beliefbase which contains
     * every nested literal
     */
    public Set<T> collapseLiterals()
    {
        final Set<T> l_beliefbase = new HashSet<>();

        collapseLiterals(this, l_beliefbase);

        return l_beliefbase;
    }

    private static void collapseLiterals( final IBeliefBase<?> p_bb, final Set p_return  )
    {
        for(final ILiteral<?> l_literal : p_bb.m_literals )
            p_return.add(l_literal.getLiteral());

        for( final IBeliefBase<?> l_bb : p_bb.m_beliefbases.values() )
            collapseLiterals(l_bb, p_return);
    }

    /**
     * returns collapsed beliefbase
     * @todo incomplete
     */
    public IBeliefBase collapseBeliefbase()
    {
        return null;
    }

    /**
     * update method for beliefbase
     * @todo move to CFieldBeliefbase - Binding structure
     */
    public abstract void update();

    /**
     * removes all literals of each beliefbase
     * @todo move to CFieldBeliefbase - Binding structure
     */
    public void clearLiterals()
    {
        m_literals.clear();

        for (String l_name : m_beliefbases.keySet())
            m_beliefbases.get(l_name).clearLiterals();
    }

    public void clear()
    {
        m_beliefbases.clear();
        m_literals.clear();
    }

    public boolean addAll(final Set<ILiteral<T>> p_literals)
    {
        return true;
    }

    ;
}
