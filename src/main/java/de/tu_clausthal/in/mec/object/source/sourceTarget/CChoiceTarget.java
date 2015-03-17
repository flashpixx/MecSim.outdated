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

import de.tu_clausthal.in.mec.CLogger;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A specific ComplexTarget which assembles the AtomTargets
 * by their relative Probability
 */
public class CChoiceTarget implements  IComplexTarget{

    /**
     * Map with AtomTarget and relative Probability
     */
    private Map<CAtomTarget, Double> m_probabilityMap = Collections.synchronizedMap(new LinkedHashMap<>());
    /**
     * Map with AtomTarget and absolute weight
     */
    private Map<CAtomTarget, Double> m_weightingMap = Collections.synchronizedMap(new LinkedHashMap<>());
    /**
     * Sum of all absolute weights (for calculating the relative Probability)
     */
    private double m_sum = 0;
    /**
     * Object to get Random Number
     */
    private Random m_random = new Random();


    /**
     * This Methods returns exactly one AtomTarget threw their specific relative Probability
     * @return
     */
    @Override
    public Queue<CAtomTarget> getTargetList() {
        Queue<CAtomTarget> l_targetList = new ConcurrentLinkedQueue<>();

        double l_random = m_random.nextDouble();
        double l_cumulate = 0;

        for ( Map.Entry<CAtomTarget, Double> l_entry  : this.m_probabilityMap.entrySet() )
        {
            l_cumulate = l_cumulate + l_entry.getValue();
            //CLogger.out("Entry:           " + l_entry.getValue() + " Random:           " + l_random + " Cumulate:           " + l_cumulate);
            if(l_cumulate >= l_random) {
                l_targetList.add(l_entry.getKey());
                return l_targetList;
            }
        }

        return l_targetList;
    }

    /**
     * Add a AtomTarget to the Weighting Map an Re-Calculate the relative Probabilities
     * @param p_target
     * @param p_weight
     */
    @Override
    public void addTarget(CAtomTarget p_target, double p_weight) {
        this.m_sum = this.m_sum + p_weight;
        this.m_weightingMap.put(p_target, p_weight);
        this.calculateNewDistribution();
    }

    /**
     * Remove a AtomTarget from Weight and Probability Map and Re-Calculate the relative Probabilities
     * @param p_target
     */
    @Override
    public void removeTarget(CAtomTarget p_target) {

        if(! (this.m_weightingMap.containsKey(p_target)))
            return;

        this.m_sum = this.m_sum - this.m_weightingMap.get(p_target);
        this.m_probabilityMap.remove(p_target);
        this.m_weightingMap.remove(p_target);
        this.calculateNewDistribution();
    }

    /**
     * Print the relative Probabilities
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

    /**
     * Prints a TargetList
     */
    @Override
    public void printTargetList() {
        Queue<CAtomTarget> l_targets = this.getTargetList();

        for(int i=0; i<l_targets.size(); i++){
            CLogger.out(l_targets.poll());
        }
    }

    /**
     * Calculate relative Probabilities
     */
    public void calculateNewDistribution(){

        for ( Map.Entry<CAtomTarget, Double> l_entry  : this.m_weightingMap.entrySet() )
        {
            CAtomTarget l_target = l_entry.getKey();
            double l_weight = l_entry.getValue();
            this.m_probabilityMap.put(l_target, l_weight / this.m_sum);
        }
    }

}
