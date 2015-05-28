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

package de.tu_clausthal.in.mec.object.mas.inconsistency;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Mult;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.ISingleEvaluateLayer;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * layer with inconsistence data
 */
public class CInconsistencyLayer<T extends IAgent> extends ISingleEvaluateLayer
{

    /**
     * algorithm to calculate stationary probability
     **/
    private final EAlgorithm m_algorithm;
    /**
     * map with object and pair of metric data and inconsistency value
     **/
    private final Map<T, Pair<List<Double>, Double>> m_data = new LinkedHashMap<>();
    /**
     * epsilon value to create an aperiodic markow-chain
     **/
    private final double m_epsilon;
    /**
     * number of iterations of the stochastic algorithm
     **/
    private final int m_iteration;
    /**
     * metric object to create the value of two objects
     **/
    private final IMetric<T> m_metric;

    /**
     * ctor - use numeric algorithm
     *
     * @param p_metric object metric
     */
    public CInconsistencyLayer( final IMetric<T> p_metric )
    {
        m_metric = p_metric;
        m_algorithm = EAlgorithm.Numeric;
        m_iteration = 0;
        m_epsilon = 0;
    }


    /**
     * ctor - use stochastic algorithm
     *
     * @param p_metric object metric
     * @param p_iteration iterations
     * @param p_epsilon epsilon value
     */
    public CInconsistencyLayer( final IMetric<T> p_metric, final int p_iteration,
            final double p_epsilon
    )
    {
        m_metric = p_metric;
        m_algorithm = EAlgorithm.Stochastic;
        m_iteration = p_iteration;
        m_epsilon = p_epsilon;
    }

    /**
     * adds a new object to the
     * inconsistency structure
     *
     * @param p_object
     */
    public synchronized void add( final T p_object )
    {
        if ( ( p_object == null ) || ( m_data.containsKey( p_object ) ) )
            return;

        final List<Double> l_column = new LinkedList<>();
        for ( final Map.Entry<T, Pair<List<Double>, Double>> l_item : m_data.entrySet() )
        {
            // calculate metric value
            final double l_value = m_metric.calculate( p_object, l_item.getKey() );

            // add value to the existing column and define new column
            l_item.getValue().getLeft().add( l_value );
            l_column.add( l_value );
        }

        m_data.put( p_object, new MutablePair<>( l_column, new Double( 0 ) ) );
    }

    /**
     * builds the matrix
     *
     * @return matrix
     */
    private DoubleMatrix2D buildMatrix()
    {
        final DoubleMatrix2D l_matrix = new DenseDoubleMatrix2D( m_data.size(), m_data.size() );
        int i = 0;
        for ( final Map.Entry<T, Pair<List<Double>, Double>> l_item : m_data.entrySet() )
            l_matrix.viewColumn( i++ ).assign(
                    new DenseDoubleMatrix1D(
                            ArrayUtils.toPrimitive(
                                    l_item.getValue().getLeft().toArray(
                                            new Double[m_data.size()]
                                    )
                            )
                    )
            );

        return l_matrix;
    }

    @Override
    public int getCalculationIndex()
    {
        return 500;
    }


    @Override
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        if ( m_data.size() < 2 )
            return;


        // build matrix and create markow-chain
        final DoubleMatrix2D l_matrix = this.buildMatrix();
        for ( int i = 0; i < l_matrix.rows(); ++i )
            l_matrix.viewRow( i ).assign( Mult.div( l_matrix.viewRow( i ).zSum() ) );


        // get the stationary probility with eigen decomposition
        final DoubleMatrix1D l_eigenvector;
        switch ( m_algorithm )
        {
            case Stochastic:
                l_eigenvector = this.getPerronFrobenius( l_matrix );
                break;

            case Numeric:
                l_eigenvector = this.getLargestEigenvector( l_matrix );

            default:
                throw new IllegalStateException( CCommon.getResourceString( CInconsistencyLayer.class, "algroithm" ) );
        }


        // set inconsistency value for each entry
        int i = 0;
        for ( final Map.Entry<T, Pair<List<Double>, Double>> l_item : m_data.entrySet() )
            l_item.getValue().setValue( l_eigenvector.get( i++ ) );
    }

    /**
     * get the largest eigen vector with QR decomposition
     *
     * @param p_matrix matrix
     * @return largest eigenvector
     */
    private DoubleMatrix1D getLargestEigenvector( final DoubleMatrix2D p_matrix )
    {
        final EigenvalueDecomposition l_eigen = new EigenvalueDecomposition( p_matrix );

        // gets the position of the largest eigenvalue
        final DoubleMatrix1D l_eigenvalues = l_eigen.getRealEigenvalues();

        int l_position = 0;
        for ( int i = 0; i < l_eigenvalues.size(); ++i )
            if ( l_eigenvalues.get( i ) > l_eigenvalues.get( l_position ) )
                l_position = i;

        // gets the largest eigenvector
        return l_eigen.getV().viewColumn( l_position );
    }

    /**
     * get the largest eigen vector based on the perron-frobenius theorem
     *
     * @param p_matrix matrix
     * @return largest eigenvector
     *
     * @see http://en.wikipedia.org/wiki/Perron%E2%80%93Frobenius_theorem
     */
    private DoubleMatrix1D getPerronFrobenius( final DoubleMatrix2D p_matrix )
    {
        DoubleMatrix1D l_probability = DoubleFactory1D.dense.random( p_matrix.rows() );
        for ( int i = 0; i < m_iteration; ++i )
        {
            //l_probability =

            //l_probability = l_probability.mmul( l_matrix );
            //l_probability = l_probability.div( l_probability.norm2() );
        }

        // normalisiere den Eigenvektor um Wahrscheinlichkeiten zu erhalten
        //l_probability = l_probability.div( l_probability.sum() );


        return l_probability;
    }


    /**
     * removes an object from the
     * inconsistency structure
     *
     * @param p_object
     */
    public synchronized void remove( final T p_object )
    {
        if ( ( p_object == null ) || ( !m_data.containsKey( p_object ) ) )
            return;

        // get index of the removing item
        final int l_position = new ArrayList<T>( m_data.keySet() ).indexOf( p_object );
        m_data.remove( p_object );

        for ( final Map.Entry<T, Pair<List<Double>, Double>> l_item : m_data.entrySet() )
            l_item.getValue().getLeft().remove( l_position );
    }


    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }


    /**
     * numeric algorithm structure
     */
    public enum EAlgorithm
    {
        /**
         * use numeric algorithm
         **/
        Numeric,
        /**
         * use stochastic algorithm
         **/
        Stochastic;
    }


    /**
     * class to create a bind between
     * agent and inconsistency value
     */
    public class CBind
    {
        @CFieldFilter.CAgent( bind = false )
        final T m_bind;


        /**
         * ctor
         *
         * @param p_agent sets the agent
         */
        public CBind( final T p_agent )
        {
            m_bind = p_agent;
        }


        /**
         * returns the agent inconsistency value
         *
         * @return value
         */
        public final double getInconsistency()
        {
            final Pair<List<Double>, Double> l_value = m_data.get( m_bind );
            return l_value == null ? 0 : l_value.getRight().doubleValue();
        }

    }
}
