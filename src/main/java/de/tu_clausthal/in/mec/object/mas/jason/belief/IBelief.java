/**
 * @cond ###################################################################################### # GPL License # # # #
 * This file is part of the TUC Wirtschaftsinformatik - MecSim                        # # Copyright (c) 2014-15,
 * Philipp
 * Kraus (philipp.kraus@tu-clausthal.de)               # # This program is free software: you can redistribute it
 * and/or
 * modify               # # it under the terms of the GNU General Public License as                            # #
 * published by the Free Software Foundation, either version 3 of the # # License, or (at your option) any later
 * version.                                    # # # # This program is distributed in the hope that it will be useful,
 * # # but WITHOUT ANY WARRANTY; without even the implied warranty of # # MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the                      # # GNU General Public License for more details.
 * # # # # You should have received a copy of the GNU General Public License # # along with
 * this program. If not, see http://www.gnu.org/licenses/                  # ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import jason.asSyntax.Literal;

import java.util.Set;


/**
 * interface for belief structure
 */
public interface IBelief
{

    /**
     * get non-atom literals
     *
     * @return non-atom literals
     */
    public Set<Literal> getLiterals();

    /**
     * update the literals *
     */
    public void update();

    /**
     * clear all literals *
     */
    public void clear();

}
