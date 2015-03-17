/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.object.source.sourceTarget;

import java.util.Queue;

/**
 * Interface which defines the Structure of every ComplexTarget
 * ComplexTarget is a Collection of AtomTargets
 * which might be assembled in some different Ways
 */
public interface IComplexTarget {

    /**
     * Generic Method to get a List of Atom Target.
     * How this List gets assembled will be defined in every special ComplexType
     * @return
     */
    public Queue<CAtomTarget> getTargetList();

    /**
     * Generic Method for adding an AtomTarget to a ComplexTarget
     * @param p_target
     */
    public void addTarget(CAtomTarget p_target, double p_weight);

    /**
     * Generic Method for removing an AtomTarget from a ComplexTarget
     * @param p_target
     */
    public void removeTarget(CAtomTarget p_target);

    /**
     * Generic Method to show the Probabilities of the ComplexTarget
     */
    public void printProbabilities();

    /**
     * This Method prints the Target List
     */
    public void printTargetList();

}
