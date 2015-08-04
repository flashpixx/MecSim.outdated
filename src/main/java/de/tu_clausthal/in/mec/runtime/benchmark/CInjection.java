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

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


/**
 * injection class to build benchmark structure within a class
 *
 * @see https://www.informit.com/guides/content.aspx?g=java&seqNum=589
 */
public final class CInjection implements ClassFileTransformer
{
    /**
     * class pool
     */
    private static final ClassPool c_pool = ClassPool.getDefault();


    @Override
    public byte[] transform( final ClassLoader p_loader, final String p_classname, final Class<?> p_redefine, final ProtectionDomain p_protecteddomain,
            final byte[] p_buffer
    ) throws IllegalClassFormatException
    {
        try
        {
            return this.inject( p_classname );
        }
        catch ( final NotFoundException | ClassNotFoundException | CannotCompileException | IOException l_exception )
        {
            System.err.println(l_exception);
        }
        return null;
    }


    /**
     * inject the class code
     *
     * @param p_class class name
     * @return byte code
     * @throws NotFoundException class loading error
     * @throws ClassNotFoundException class loading error
     * @throws CannotCompileException compiling error
     * @throws IOException io exception
     */
    private byte[] inject( final String p_class ) throws NotFoundException, ClassNotFoundException, CannotCompileException, IOException
    {
        final CtClass l_timer = c_pool.getCtClass( CTimer.class.getCanonicalName() );
        final CtClass l_class = c_pool.getCtClass( p_class );
        l_class.stopPruning( false );


        for ( final CtMethod l_method : l_class.getDeclaredMethods() )
        {
            if ( l_method.getAnnotation( IBenchmark.class ) == null )
                continue;

            l_method.addLocalVariable( "l_bechmarktimer", l_timer );
            l_method.insertBefore( "final l_bechmarktimer = new " + CTimer.class.getCanonicalName() + "().start();" );
            l_method.insertAfter(  "l_bechmarktimer.stop(\"" + l_method.getLongName() + "\");" );
        }

        l_class.stopPruning( true );
        return l_class.toBytecode();
    }
}
