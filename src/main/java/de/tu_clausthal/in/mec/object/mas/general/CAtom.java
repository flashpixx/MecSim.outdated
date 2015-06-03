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
 * generic atom class for agent literals
 */
public class CAtom<T> implements IAtom<T>
{
    /**
     * value the atom represents (e.g. string, number)
     */
    private final T m_value;

    /**
     * class of generic type
     */
    private Class<T> m_type;

    /**
     * ctor
     *
     * @param p_value the atom's value
     */
    public CAtom(final T p_value)
    {
        m_value = p_value;
    }

    /**
     * getter for atom value
     *
     * @return the atom's value
     */
    @Override
    public T get()
    {
        return m_value;
    }

    /**
     * check for the atom's class type
     *
     * @param p_class matching class
     * @return true if the atom's type is assignable from matching class
     */
    @Override
    public boolean instanceOf(Class<?> p_class)
    {
        return m_type.isAssignableFrom(p_class);
    }

    /**
     * hashcode method to compare atoms
     *
     * @return the atom's hashcode
     */
    @Override
    public final int hashCode()
    {
        return m_value.hashCode();
    }

    /**
     * method to check equivalence
     *
     * @param p_object
     * @return true if input parameter equals the atom
     */
    @Override
    public final boolean equals(final Object p_object)
    {
        return this.hashCode() == p_object.hashCode();
    }

}
