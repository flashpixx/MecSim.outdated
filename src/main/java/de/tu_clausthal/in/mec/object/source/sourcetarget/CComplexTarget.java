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

package de.tu_clausthal.in.mec.object.source.sourcetarget;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * A ComplexTarget is a Collection of Atom Targets.
 * Which can be equipped with a relative Probability.
 * This Class also provides some Methods to get a Collection of AtomTarget over their Probabilities.
 */
public class CComplexTarget
{

    /**
     * Map with AtomTarget and relative Probability
     */
    private final Map<CAtomTarget, Double> m_probabilityMap = Collections.synchronizedMap( new LinkedHashMap<>() );
    /**
     * Map with AtomTarget and absolute Weight
     */
    private final Map<CAtomTarget, Double> m_weightingMap = Collections.synchronizedMap( new LinkedHashMap<>() );
    /**
     * Object to get Random Number (Uniform Distributed)
     */
    private final Random m_random = new Random();
    /**
     * Sum of all absolute weights (for calculating the relative Probability)
     */
    private double m_sum = 0;

    /**
     * Method to sort the Weighting Map
     *
     * @param p_unsortmap
     * @return sorted Weight Map
     */
    public static Map sortByValue( final Map p_unsortmap )
    {
        final List list = new CopyOnWriteArrayList( p_unsortmap.entrySet() );

        Collections.sort( list, new Comparator()
        {
            public int compare( final Object p_object1, final Object p_object2 )
            {
                return ( (Comparable) ( (Map.Entry) ( p_object2 ) ).getValue() )
                        .compareTo( ( (Map.Entry) ( p_object1 ) ).getValue() );
            }
        } );

        final Map l_sortedMap = Collections.synchronizedMap( new LinkedHashMap() );
        for ( Iterator l_iterator = list.iterator(); l_iterator.hasNext(); )
        {
            final Map.Entry l_entry = (Map.Entry) l_iterator.next();
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }
        return l_sortedMap;
    }

    /**
     * adds a atom Target to this complex Target with default weight of 1
     *
     * @param p_target atom target which should be added
     */
    public final void addTarget( final CAtomTarget p_target )
    {
        this.addTarget( p_target, 1 );
    }

    /**
     * Adds a atom Target to this complex Target with special absolute weight.
     * If the Atom Target already exist the weight will be updated.
     *
     * @param p_target atom target which should be added
     * @param p_weight weight of this atom target
     */
    public final void addTarget( final CAtomTarget p_target, final double p_weight )
    {

        //If a Target already Exist only the weight need to be updated
        if ( m_weightingMap.containsKey( p_target ) )
        {
            m_sum -= m_weightingMap.get( p_target ) + p_weight;
            m_weightingMap.replace( p_target, p_weight );
            this.calculateNewDistribution();
        }

        //Otherwise it should be added to the List
        m_sum += p_weight;
        m_weightingMap.put( p_target, p_weight );
        this.calculateNewDistribution();
    }

    /**
     * removes the Atom Target from this Complex Target
     *
     * @param p_target Atom Target which should be removed
     */
    public final void removeTarget( final CAtomTarget p_target )
    {

        if ( !( this.m_weightingMap.containsKey( p_target ) ) )
            return;

        m_sum -= m_weightingMap.get( p_target );
        m_probabilityMap.remove( p_target );
        m_weightingMap.remove( p_target );
        this.calculateNewDistribution();
    }

    /**
     * Calculate relative Probabilities for every Atom Target
     */
    public final void calculateNewDistribution()
    {

        for ( Map.Entry<CAtomTarget, Double> l_entry : this.m_weightingMap.entrySet() )
        {
            final CAtomTarget l_target = l_entry.getKey();
            final double l_weight = l_entry.getValue();
            m_probabilityMap.put( l_target, l_weight / m_sum );
        }
    }

    /**
     * This Methods returns exactly one AtomTarget threw their specific Probability
     *
     * @return a single Atom Target
     */
    public final CAtomTarget getSingleTarget()
    {
        final double l_random = m_random.nextDouble();
        double l_cumulate = 0;

        for ( Map.Entry<CAtomTarget, Double> l_entry : this.m_probabilityMap.entrySet() )
        {
            l_cumulate += l_entry.getValue();
            if ( l_cumulate >= l_random )
                return l_entry.getKey();
        }

        return null;
    }

    /**
     * This Methods returns a List of AtomTargets threw their specific relative Probability
     * The Length of the List has a Default Value of the Size of all AtomTargets
     *
     * @return a list of Atom Targets
     */
    public final Queue<CAtomTarget> getMultiTarget()
    {
        return this.getMultiTarget( m_probabilityMap.size() );
    }

    /**
     * This Methods returns a List of AtomTarget threw their specific relative Probability
     * The Length of the List can be passed in over Arguments
     *
     * @param p_targetLength Length of List of Atom Targets
     * @return a list of Atom Targets
     */
    public final Queue<CAtomTarget> getMultiTarget( final int p_targetLength )
    {
        final Queue<CAtomTarget> l_targetList = new ConcurrentLinkedQueue<>();
        for ( int i = 0; i < p_targetLength; i++ )
            l_targetList.add( this.getSingleTarget() );

        return l_targetList;
    }

    /**
     * This Method returns a List of AtomTargets which are ordered by their weight (Rising)
     * The Length of the List has a Default Value of the Size of all AtomTargets
     *
     * @return a sequence of Atom Targets
     */
    public final Queue<CAtomTarget> getSequenceTarget()
    {
        return this.getSequenceTarget( this.m_weightingMap.size() );
    }

    /**
     * This Method returns a List of AtomTargets which are ordered by their weight (Rising)
     * The Length of the List can be passed in over Arguments
     *
     * @param p_sequenceLength
     * @return a sequence of Atom Targets
     */
    public final Queue<CAtomTarget> getSequenceTarget( final int p_sequenceLength )
    {
        final Queue<CAtomTarget> l_targetList = new ConcurrentLinkedQueue<>();
        final Map<CAtomTarget, Double> l_sortedMap = Collections.synchronizedMap( this.sortByValue( this.m_weightingMap ) );

        for ( Map.Entry<CAtomTarget, Double> l_entry : l_sortedMap.entrySet() )
        {
            l_targetList.add( l_entry.getKey() );
        }

        return l_targetList;
    }

}
