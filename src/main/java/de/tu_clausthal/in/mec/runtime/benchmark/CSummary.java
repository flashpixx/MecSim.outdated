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
import de.tu_clausthal.in.mec.common.CReflection;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * class to store benchmarking content
 *
 * @see http://www.objc.io/issues/11-android/dependency-injection-in-java/
 * @see http://stackoverflow.com/questions/29451704/using-javassist-to-log-method-calls-and-argument-values-how-to-make-a-logger-cl
 * @see http://docs.oracle.com/javase/7/docs/api/java/lang/instrument/Instrumentation.html
 * @see https://wiki.openjdk.java.net/display/HotSpot/MicroBenchmarks
 */
public final class CSummary
{
    /** singleton instance **/
    private static final CSummary c_instance = new CSummary();
    /** statistic object **/
    private final Map<String, SummaryStatistics> m_result = new ConcurrentHashMap<>();


    /**
     * retruns singleton instance
     *
     * @return object
     */
    public static final CSummary getInstance()
    {
        return c_instance;
    }


    /**
     * sets the time value
     *
     * @param p_fqnmethodname full-qualified method name
     * @param p_time elapsed time value
     */
    public void set( final String p_fqnmethodname, final long p_time )
    {
        // microbenchmark should ignore the first set
        m_result.putIfAbsent( p_fqnmethodname, new SummaryStatistics() ).addValue( p_time );
    }


    /**
     * sets a class for benchmarking
     * @param p_class class
     */
    public void set( final Class p_class )
    {
        // http://www.tomsquest.com/blog/2014/01/intro-java-agent-and-bytecode-manipulation/
        // https://thoughtpage.wordpress.com/2012/07/29/inject-code-using-javaassist-simple-example/
        // http://www.ibm.com/developerworks/library/j-dyn0203/

        CtClass l_class;

        try {
            l_class = ClassPool.getDefault().get( p_class.getCanonicalName() );
        } catch ( final Exception l_exception ) {
            CLogger.error(l_exception);
            return;
        }

        for ( final CtMethod l_method : l_class.getDeclaredMethods() )
            try
            {
                if (l_method.getAnnotation( IBenchmark.class ) == null)
                    continue;

                l_method.addLocalVariable( "l_injectElapsedTime", CtClass.longType );
                l_method.insertBefore( "l_injectElapsedTime = System.nanoTime();" );
                l_method.insertAfter( "de.tu_clausthal.in.mec.runtime.benchmark.CSummary.getInstance().set(\"" + l_method.getLongName() + "\", l_injectElapsedTime);" );

            } catch ( final Exception l_exception ) {
                CLogger.error( l_exception );
            }
    }


    /**
     * stores the benchmark data into a Json file
     *
     * @param p_filename filename
     */
    public void store( String p_filename )
    {
        final Map<String, Map<String, Object>> l_benchmark = new HashMap<>();
        for( final Map.Entry<String, SummaryStatistics> l_item : m_result.entrySet() )
            l_benchmark.put(
                    l_item.getKey(), CCommon.getMap(
                            CCommon.getResourceString( this, "max" ), l_item.getValue().getMax(),
                            CCommon.getResourceString( this, "min" ), l_item.getValue().getMin(),
                            CCommon.getResourceString( this, "mean" ), l_item.getValue().getMean(),
                            CCommon.getResourceString( this, "stddeviation" ), l_item.getValue().getStandardDeviation(),
                            CCommon.getResourceString( this, "count" ), l_item.getValue().getN(),
                            CCommon.getResourceString( this, "sum" ), l_item.getValue().getSum(),
                            CCommon.getResourceString( this, "variance" ), l_item.getValue().getVariance()
                    ) );

        try
        {
            FileUtils.writeStringToFile( new File(p_filename), CCommon.toJson( l_benchmark ), "UTF-8" );
        }
        catch ( final IOException l_exception )
        {
            CLogger.error(l_exception);
        }
    }


    /**
     * filter class for method filter
     */
    private static final class CMethodFilter implements CReflection.IMethodFilter
    {

        @Override
        public boolean filter( final Method p_method )
        {
            return p_method.isAnnotationPresent( IBenchmark.class );
        }
    }


}
