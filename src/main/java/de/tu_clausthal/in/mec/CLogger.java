/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.*;
import org.pmw.tinylog.writers.FileWriter;


/**
 * Logger class with additional Log4j support
 */
public class CLogger
{

    /**
     * stack index of traces
     */
    private static final int s_client_code_stack_index;

    /** initialization **/
    static
    {

        // strack trace difference between JDK 1.5 and 1.6
        int i = 0;
        for ( StackTraceElement ste : Thread.currentThread().getStackTrace() )
        {
            i++;
            if ( ste.getClassName().equals( CLogger.class.getName() ) )
                break;
        }
        s_client_code_stack_index = i;
    }

    /**
     * defines the global log level
     */
    private static Level s_level = Level.OFF;

    /**
     * creates the logger with properties
     *
     * @param p_level    log level
     * @param p_filename p_filename
     */
    public static void create( Level p_level, String p_filename )
    {
        s_level = p_level;
        Configurator.defaultConfig().writer( p_level == Level.OFF ? null : new FileWriter( p_filename ) ).level( p_level ).activate();
        Configurator.currentConfig().formatPattern( "{message}" ).activate();
    }


    /**
     * pad / cut string of a define length
     *
     * @param p_in     input string
     * @param p_Filler fill character
     * @param p_len    max string length
     * @return modified string
     */
    private static String padCut( String p_in, char p_Filler, int p_len )
    {
        if ( p_len < 1 )
            return p_in;
        if ( p_in.length() < p_len )
            return p_in + StringUtils.repeat( p_Filler, p_len - p_in.length() );

        return p_in.substring( 0, p_len );
    }


    /**
     * creates a full log item
     *
     * @param p_status status name
     * @param p_add    additional log data
     */
    private static String createLogData( Level p_status, Object p_add )
    {
        String l_SEP = StringUtils.repeat( " ", 5 );
        StringBuffer l_Str = new StringBuffer();

        l_Str.append( l_SEP );
        l_Str.append( padCut( "status [" + p_status + "]", ' ', 15 ) );
        l_Str.append( l_SEP );
        l_Str.append( padCut( "thread [" + Thread.currentThread() + "]", ' ', 100 ) );
        l_Str.append( l_SEP );
        l_Str.append( padCut( "method [" + getCurrentMethodNameFqn( 2 ) + "]", ' ', 100 ) );
        l_Str.append( l_SEP );
        l_Str.append( padCut( "line no [" + getCurrentLineNumber( 2 ) + "]", ' ', 15 ) );
        l_Str.append( l_SEP );
        l_Str.append( padCut( "invoker [" + getInvokingMethodNameFqn( 3 ) + "]", ' ', 100 ) );

        String l_add = ( p_add != null ) ? p_add.toString() : "";
        if ( !l_add.isEmpty() )
        {
            l_Str.append( l_SEP );
            l_Str.append( l_add.replace( "\n", "  " ).replace( "\t", "  " ).replace( "\r", "" ) );
        }

        return l_Str.toString();
    }


    /**
     * adds a warn message
     */
    public static void warn()
    {
        warn( null );
    }

    /**
     * adds a warn message
     *
     * @param p_data log data
     */
    public static void warn( Object p_data )
    {
        Logger.warn( createLogData( Level.WARNING, p_data ) );
    }


    /**
     * adds a error message
     */
    public static void error()
    {
        error( null );
    }


    /**
     * adds a error message
     *
     * @param p_data log data
     */
    public static void error( Object p_data )
    {
        Logger.error( createLogData( Level.ERROR, p_data ) );
    }


    /**
     * adds an info message
     */
    public static void info()
    {
        info( null );
    }


    /**
     * adds an info message
     *
     * @param p_data log data
     */
    public static void info( Object p_data )
    {
        Logger.info( createLogData( Level.INFO, p_data ) );
    }


    /**
     * adds a debug message
     */
    public static void debug()
    {
        debug( null );
    }


    /**
     * adds a debug message
     *
     * @param p_data log data
     */
    public static void debug( Object p_data )
    {
        Logger.debug( createLogData( Level.DEBUG, p_data ) );
    }


    /**
     * adds a output message
     */
    public static void out()
    {
        out( null );
    }

    /**
     * adds a output message
     *
     * @param p_data log data
     */
    public static void out( Object p_data )
    {
        if ( s_level == Level.OFF )
            System.out.println( p_data );
        else
            Logger.info( createLogData( Level.INFO, p_data ) );
    }


    /**
     * gets the current method name
     *
     * @return method name
     */
    private static String getCurrentMethodName( int offset )
    {
        return Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getMethodName();
    }


    /**
     * gets the current class name
     *
     * @return class name
     */
    private static String getCurrentClassName( int offset )
    {
        return Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getClassName();
    }


    /**
     * gets the current line number
     *
     * @return number
     */
    private static int getCurrentLineNumber( int offset )
    {
        return Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getLineNumber();
    }


    /**
     * gets the current invoker method name
     *
     * @return method name
     */
    private static String getInvokingMethodName( int offset )
    {
        return getCurrentMethodName( offset + 1 );
    }


    /**
     * gets the current invoker class name
     *
     * @return class name
     */
    private static String getInvokingClassName( int offset )
    {
        return getCurrentClassName( offset + 1 );
    }


    /**
     * gets the current FQN method name
     *
     * @return FQN method name
     */
    private static String getCurrentMethodNameFqn( int offset )
    {
        String currentClassName = getCurrentClassName( offset + 1 );
        String currentMethodName = getCurrentMethodName( offset + 1 );

        return currentClassName + "." + currentMethodName;
    }


    /**
     * gets the current invoker FQN method name
     *
     * @return FQN method name
     */
    private static String getInvokingMethodNameFqn( int offset )
    {
        String invokingClassName = getInvokingClassName( offset + 1 );
        String invokingMethodName = getInvokingMethodName( offset + 1 );

        return invokingClassName + "." + invokingMethodName;
    }

}

