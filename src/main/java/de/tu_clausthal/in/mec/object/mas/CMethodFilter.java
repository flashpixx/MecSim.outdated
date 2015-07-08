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

package de.tu_clausthal.in.mec.object.mas;


import de.tu_clausthal.in.mec.common.CReflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * class to filter methods *
 */
public class CMethodFilter implements CReflection.IMethodFilter
{
    @Override
    public boolean filter( final Method p_method )
    {
        boolean l_use = true;
        if ( p_method.isAnnotationPresent( CAgent.class ) )
            l_use = p_method.getAnnotation( CAgent.class ).bind();

        return l_use && ( !( Modifier.isAbstract( p_method.getModifiers() ) || Modifier.isInterface( p_method.getModifiers() ) || ( Modifier.isNative(
                p_method.getModifiers()
        ) || ( Modifier.isStatic( p_method.getModifiers() ) ) ) ) );
    }

    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.METHOD )
    public @interface CAgent
    {

        /**
         * defines the bind
         *
         * @return true if the method is bind
         */
        boolean bind() default true;
    }
}
