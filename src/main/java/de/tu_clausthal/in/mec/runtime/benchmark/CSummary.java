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

package de.tu_clausthal.in.mec.runtime.benchmark;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * class to store benchmarking content
 *
 * @see http://www.objc.io/issues/11-android/dependency-injection-in-java/
 */
public final class CSummary
{
    /**
     * singleton instance
     **/
    private static final CSummary c_instance = new CSummary();
    /**
     * statistic object
     **/
    private final Map<String, DescriptiveStatistics> m_result = new ConcurrentHashMap<>();
    /**
     * storing filename
     **/
    private String m_filename;


    /**
     * retruns singleton instance
     *
     * @return object
     */
    public static CSummary getInstance()
    {
        return c_instance;
    }


    /**
     * sets the storing filename
     *
     * @param p_filename filename
     */
    public void setFilename( final String p_filename )
    {
        if ( ( m_filename != null ) && ( !m_filename.isEmpty() ) )
            return;

        m_filename = p_filename;
    }


    /**
     * sets the time value
     *
     * @param p_label full-qualified method name
     * @param p_time elapsed time value
     * @warning check explict of null value to avoid NPE on initialization call
     */
    public void setTime( final String p_label, final long p_time )
    {
        final DescriptiveStatistics l_statistic = m_result.putIfAbsent( p_label, new SynchronizedDescriptiveStatistics() );
        if ( l_statistic != null )
            l_statistic.addValue( p_time );
    }


    /**
     * stores the benchmark data into a Json file
     */
    public void store()
    {
        if ( ( m_filename == null ) || ( m_filename.isEmpty() ) )
            return;

        final Map<String, Map<String, Object>> l_benchmark = new HashMap<>();
        for ( final Map.Entry<String, DescriptiveStatistics> l_item : m_result.entrySet() )
            l_benchmark.put(
                    l_item.getKey(), CCommon.getMap(
                            "max", l_item.getValue().getMax(),
                            "min", l_item.getValue().getMin(),
                            "kurtosis", l_item.getValue().getKurtosis(),
                            "arithmetic mean", l_item.getValue().getMean(),
                            "geometric mean", l_item.getValue().getGeometricMean(),
                            "50-percentile", l_item.getValue().getPercentile( 50 ),
                            "25-percentile", l_item.getValue().getPercentile( 25 ),
                            "75-percentile", l_item.getValue().getPercentile( 75 ),
                            "standard deviation", l_item.getValue().getStandardDeviation(),
                            "skewness", l_item.getValue().getSkewness(),
                            "count", l_item.getValue().getN(),
                            "sum", l_item.getValue().getSum(),
                            "sum square", l_item.getValue().getSumsq(),
                            "variance", l_item.getValue().getVariance()
                    )
            );

        if ( l_benchmark.isEmpty() )
            return;

        try
        {
            FileUtils.writeStringToFile( new File( m_filename ), CCommon.toJson( l_benchmark ), "UTF-8" );
        }
        catch ( final IOException l_exception )
        {
            CLogger.error( l_exception );
        }
    }

}
