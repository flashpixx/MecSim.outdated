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

import javassist.NotFoundException;

import java.lang.instrument.Instrumentation;


/**
 * premain executable class for benchmarking
 *
 * @note Jar file must be run with @code java -javaagent:<MecSim Jar> -jar <MecSim Jar> --benchmark <output Json file> @endcode
 */
public final class CBenchmark
{

    /**
     * private ctor
     */
    private CBenchmark()
    {
    }

    /**
     * premain for benchmarking
     *
     * @param p_args arguments of the agent - will pass to the normal main
     * @param p_instrumentation instrumentation to inject class data
     */
    public static void premain( final String p_args, final Instrumentation p_instrumentation ) throws NotFoundException
    {
        p_instrumentation.addTransformer( new CInjection() );
    }

}
