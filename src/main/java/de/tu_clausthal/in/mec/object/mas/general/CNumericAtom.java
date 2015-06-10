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
 * numeric atom class for agent literals
 */
public class CNumericAtom extends IDefaultAtom<Double>
{
    /**
     * ctor
     *
     * @param p_value the atom's value
     */
    public CNumericAtom(final Double p_value)
    {
        super(p_value);
    }

    /**
     * returns the atoms string representation
     *
     * @return string representation
     */
    public String toString()
    {
        long l_roundedValue = Math.round(this.get());
        return this.get() == (double) l_roundedValue ? String.valueOf(l_roundedValue) : String.valueOf(this.get());
    }
}
