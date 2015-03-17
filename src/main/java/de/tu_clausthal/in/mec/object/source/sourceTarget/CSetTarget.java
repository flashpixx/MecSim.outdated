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
//test
package de.tu_clausthal.in.mec.object.source.sourceTarget;

import de.tu_clausthal.in.mec.CLogger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A specific ComplexTarget which assembles the AtomTargets
 * by their absolute Probability  (which must between 0 and 1)
 */
public class CSetTarget implements IComplexTarget{

    /**
     * Map with Atom Target and their Probabilities
     */
    private Map<CAtomTarget, Double> m_probabilityMap = Collections.synchronizedMap(new LinkedHashMap<>());
    /**
     * Random Object
     */
    private Random m_random = new Random();
    /**
     * Boolean flag to indicate if the List of Targets should be ordered by their absolute Probability
     */
    private boolean m_ordered = false;


    /**
     * This Method returns a Target by an absolute Probability
     * Contrary to the CChoiceTarget this Method also returns Multiple Targets
     * Which might be ordered if the m_ordered Flag is set to true
     * @return
     */
    @Override
    public Queue<CAtomTarget> getTargetList() {
        Queue<CAtomTarget> l_targetList = new ConcurrentLinkedQueue<>();

        double l_random = m_random.nextDouble();

        for ( Map.Entry<CAtomTarget, Double> l_entry  : this.m_probabilityMap.entrySet() )
        {
            if(l_entry.getValue() >= l_random)
                l_targetList.add(l_entry.getKey());
        }

        return l_targetList;
    }

    /**
     * Add an AtomTarget with a absolute Probability
     * @param p_target
     * @param p_probability
     */
    @Override
    public void addTarget(CAtomTarget p_target, double p_probability) {
        this.m_probabilityMap.put(p_target, p_probability);
    }

    /**
     * Remove an AtomTarget
     * @param p_target
     */
    @Override
    public void removeTarget(CAtomTarget p_target) {
        if(this.m_probabilityMap.containsKey(p_target))
            this.m_probabilityMap.remove(p_target);
    }

    /**
     * Print the Probability Map of this ComplexTarget
     */
    @Override
    public void printProbabilities() {
        double l_sum = 0;
        for ( Map.Entry<CAtomTarget, Double> l_entry  : this.m_probabilityMap.entrySet() )
        {
            l_sum += l_entry.getValue();
            CLogger.out("Target: " + l_entry.getKey() + "Probability: " + l_entry.getValue());
        }
        CLogger.out("Sum of all Probabilities: " + l_sum);
    }

}
