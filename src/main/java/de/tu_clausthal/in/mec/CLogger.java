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

package de.tu_clausthal.in.mec;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.FileWriter;

import java.util.Collection;


/**
 * Logger class with additional Log4j support
 */
public class CLogger
{

    /**
     * stack index of traces
     */
    private static final int c_stackindex;
    /**
     * defines the global log level
     */
    private static Level s_level = Level.OFF;

    /** initialization **/
    static
    {

        // stack trace difference between JDK 1.5 and 1.6
        int i = 0;
        for ( StackTraceElement l_trace : Thread.currentThread().getStackTrace() )
        {
            i++;
            if ( l_trace.getClassName().equals( CLogger.class.getName() ) ) break;
        }
        c_stackindex = i;
    }

    /**
     * private ctor - avoid instantiation
     */
    private CLogger()
    {
    }


    /**
     * creates the logger with properties
     *
     * @param p_level    log level
     * @param p_filename p_filename
     */
    public static void create( final Level p_level, final String p_filename )
    {
        s_level = p_level;
        Configurator.defaultConfig().writer( p_level == Level.OFF ? null : new FileWriter( p_filename ) ).level( p_level ).activate();
        Configurator.currentConfig().formatPattern( "{message}" ).activate();
    }


    /**
     * pad / cut string of a define length
     *
     * @param p_input  input string
     * @param p_filler fill character
     * @param p_length max string length
     * @return modified string
     */
    private static String padCut( final String p_input, final char p_filler, final int p_length )
    {
        if ( p_length < 1 ) return p_input;
        if ( p_input.length() < p_length ) return p_input + StringUtils.repeat( p_filler, p_length - p_input.length() );

        return p_input.substring( 0, p_length );
    }


    /**
     * creates a full log item
     *
     * @param p_status status name
     * @param p_add    additional log data
     */
    private static String createLogData( final Level p_status, final Object p_add )
    {
        // get invoker position (index depends on static main function)
        int l_invokerindex;
        try
        {
            getInvokingMethodNameFqn( 4 );
            l_invokerindex = 4;
        }
        catch ( final ArrayIndexOutOfBoundsException l_exception )
        {
            l_invokerindex = 3;
        }

        final String l_sep = StringUtils.repeat( " ", 5 );
        final StringBuffer l_str = new StringBuffer();

        l_str.append( l_sep ).
                append( padCut( "status [" + p_status + "]", ' ', 15 ) ).
                     append( l_sep ).
                     append( padCut( "thread [" + Thread.currentThread() + "]", ' ', 100 ) ).
                     append( l_sep ).
                     append( padCut( "invoker [" + getInvokingMethodNameFqn( l_invokerindex ) + "]", ' ', 100 ) ).
                     append( l_sep ).
                     append( padCut( "method [" + getCurrentMethodNameFqn( 3 ) + "]", ' ', 100 ) ).
                     append( l_sep ).
                     append( padCut( "line no [" + getCurrentLineNumber( 3 ) + "]", ' ', 25 ) );


        String l_add = "";
        if ( p_add != null )
        {
            if ( p_add instanceof Collection )
            {
                l_add = "[   ";
                for ( Object l_item : (Collection) p_add )
                    l_add += l_item + "   ";
                l_add += "]";
            }
            else l_add = p_add.toString();
        }

        if ( !l_add.isEmpty() )
        {
            l_str.append( l_sep ).
                    append( l_add.replace( "\n", "  " ).replace( "\t", "  " ).replace( "\r", "" ) );
        }

        return l_str.toString();
    }


    /**
     * adds a warn message
     */
    public static void warn()
    {
        warn( null, true );
    }

    /**
     * adds a warn message *
     *
     * @param p_write boolean on true message is written
     */
    public static void warn( final boolean p_write )
    {
        warn( null, p_write );
    }

    /**
     * adds a warn message
     *
     * @param p_data log data
     */
    public static <T> T warn( final T p_data )
    {
        warn( p_data, true );
        return p_data;
    }

    /**
     * adds a warn message
     *
     * @param p_data  log data
     * @param p_write boolean on true message is written
     */
    public static <T> T warn( final T p_data, final boolean p_write )
    {
        if ( p_write ) Logger.warn( createLogData( Level.WARNING, p_data ) );
        return p_data;
    }


    /**
     * adds a error message
     */
    public static void error()
    {
        error( null, true );
    }

    /**
     * adds a error message
     *
     * @param p_write boolean on true message is written
     */
    public static void error( final boolean p_write )
    {
        error( null, p_write );
    }

    /**
     * adds a error message
     *
     * @param p_data log data
     */
    public static <T> T error( final T p_data )
    {
        error( p_data, true );
        return p_data;
    }

    /**
     * adds a error message
     *
     * @param p_data  log data
     * @param p_write boolean on true message is written
     */
    public static <T> T error( final T p_data, final boolean p_write )
    {
        if ( p_write ) Logger.error( createLogData( Level.ERROR, p_data ) );
        return p_data;
    }


    /**
     * adds an info message
     */
    public static void info()
    {
        info( null, true );
    }

    /**
     * adds an info message
     *
     * @param p_write boolean on true message is written
     */
    public static void info( final boolean p_write )
    {
        info( null, p_write );
    }


    /**
     * adds an info message
     *
     * @param p_data log data
     */
    public static <T> T info( final T p_data )
    {
        info( p_data, true );
        return p_data;
    }

    /**
     * adds an info message
     *
     * @param p_data  log data
     * @param p_write boolean on true message is written
     */
    public static <T> T info( final T p_data, final boolean p_write )
    {
        if ( p_write ) Logger.info( createLogData( Level.INFO, p_data ) );
        return p_data;
    }


    /**
     * adds a debug message
     */
    public static void debug()
    {
        debug( null, true );
    }

    /**
     * adds a debug message
     *
     * @param p_write boolean on true message is written
     */
    public static void debug( final boolean p_write )
    {
        debug( null, p_write );
    }

    /**
     * adds a debug message
     *
     * @param p_data log data
     */
    public static <T> T debug( final T p_data )
    {
        debug( p_data, true );
        return p_data;
    }

    /**
     * adds a debug message
     *
     * @param p_data  log data
     * @param p_write boolean on true message is written
     */
    public static <T> T debug( final T p_data, final boolean p_write )
    {
        if ( p_write ) Logger.debug( createLogData( Level.DEBUG, p_data ) );
        return p_data;
    }


    /**
     * adds a output message
     */
    public static void out()
    {
        out( null, true );
    }

    /**
     * adds a output message
     *
     * @param p_write boolean on true message is written
     */
    public static void out( final boolean p_write )
    {
        out( null, p_write );
    }


    /**
     * adds a output message
     *
     * @param p_data log data
     */
    public static <T> T out( final T p_data )
    {
        out( p_data, true );
        return p_data;
    }

    /**
     * adds a output message
     *
     * @param p_data  log data
     * @param p_write boolean on true message is written
     */
    public static <T> T out( final T p_data, final boolean p_write )
    {
        if ( !p_write ) return p_data;

        if ( s_level == Level.OFF ) System.out.println( p_data );
        else Logger.info( createLogData( Level.INFO, p_data ) );
        return p_data;
    }


    /**
     * gets the current method name
     *
     * @return method name
     */
    private static String getCurrentMethodName( final int p_offset )
    {
        return Thread.currentThread().getStackTrace()[Math.min( c_stackindex + p_offset, Thread.currentThread().getStackTrace().length )].getMethodName();
    }


    /**
     * gets the current class name
     *
     * @return class name
     */
    private static String getCurrentClassName( final int p_offset )
    {
        return Thread.currentThread().getStackTrace()[Math.min( c_stackindex + p_offset, Thread.currentThread().getStackTrace().length )].getClassName();
    }


    /**
     * gets the current line number
     *
     * @return number
     */
    private static int getCurrentLineNumber( final int p_offset )
    {
        return Thread.currentThread().getStackTrace()[Math.min( c_stackindex + p_offset, Thread.currentThread().getStackTrace().length )].getLineNumber();
    }


    /**
     * gets the current invoker method name
     *
     * @return method name
     */
    private static String getInvokingMethodName( final int p_offset )
    {
        return getCurrentMethodName( p_offset + 1 );
    }


    /**
     * gets the current invoker class name
     *
     * @return class name
     */
    private static String getInvokingClassName( final int p_offset )
    {
        return getCurrentClassName( p_offset + 1 );
    }


    /**
     * gets the current FQN method name
     *
     * @return FQN method name
     */
    private static String getCurrentMethodNameFqn( final int p_offset )
    {
        final String l_currentClassName = getCurrentClassName( p_offset + 1 );
        final String l_currentMethodName = getCurrentMethodName( p_offset + 1 );

        return l_currentClassName + "." + l_currentMethodName;
    }


    /**
     * gets the current invoker FQN method name
     *
     * @return FQN method name
     */
    private static String getInvokingMethodNameFqn( final int p_offset )
    {
        final String l_invokingClassName = getInvokingClassName( p_offset + 1 );
        final String l_invokingMethodName = getInvokingMethodName( p_offset + 1 );

        return l_invokingClassName + "." + l_invokingMethodName;
    }

}

