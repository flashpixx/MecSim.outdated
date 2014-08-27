/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


/**
 * Logger class with additional Log4j support
 */
public class CLogger {
    private static final String s_loggername = "MecSim";

    /**
     * Stack Index des Traces *
     */
    private static final int s_client_code_stack_index;

    /** statische Initialisierung **/
    static {

        // Stack Trace  verhält sich unterschiedlich bei JDK 1.5 and 1.6
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(CLogger.class.getName()))
                break;
        }
        s_client_code_stack_index = i;
    }


    /**
     * creates the logger with prooperties
     *
     * @param p_level    log level
     * @param p_filename p_filename
     */
    public static void create(Level p_level, String p_filename) {
        Logger.getLogger(s_loggername).setLevel(p_level);

        if (Logger.getLogger(s_loggername).getLevel() != Level.OFF) {
            FileAppender appender = new FileAppender();
            appender.setName("FileLogger");
            appender.setFile(p_filename);
            appender.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
            appender.setThreshold(Level.ALL);
            appender.setAppend(true);
            appender.activateOptions();

            Logger.getLogger(s_loggername).addAppender(appender);
        }
    }


    /**
     * String auf eine bestimmte Länge mit einem Zeichen aufzufüllen
     *
     * @param p_in     Eingabestring
     * @param p_Filler Filler-Character
     * @param p_len    maximale Stringlänge
     * @return modifizierter String
     */
    private static String padCut(String p_in, char p_Filler, int p_len) {
        if (p_len < 1)
            return p_in;
        if (p_in.length() < p_len)
            return p_in + StringUtils.repeat(p_Filler, p_len - p_in.length());

        return p_in.substring(0, p_len);
    }


    /**
     * erzeut einen Logeintrag
     *
     * @param p_status status name
     * @param p_add    zusätzliche Logdaten
     */
    private static String createLogData(Level p_status, Object p_add) {
        String l_SEP = StringUtils.repeat(" ", 3);
        StringBuffer l_Str = new StringBuffer();

        l_Str.append(padCut("status [" + p_status + "]", ' ', 15));
        l_Str.append(l_SEP);
        l_Str.append(padCut("thread [" + Thread.currentThread() + "]", ' ', 50));
        l_Str.append(l_SEP);
        l_Str.append(padCut("method [" + getCurrentMethodNameFqn(2) + "]", ' ', 100));
        l_Str.append(l_SEP);
        l_Str.append(padCut("line no [" + getCurrentLineNumber(2) + "]", ' ', 15));
        l_Str.append(l_SEP);
        l_Str.append(padCut("invoker [" + getInvokingMethodNameFqn(3) + "]", ' ', 100));

        String l_add = (p_add != null) ? p_add.toString() : "";
        if (!l_add.isEmpty()) {
            l_Str.append(l_SEP);
            l_Str.append(l_add.replace("\n", "  ").replace("\t", "  ").replace("\r", ""));
        }

        return l_Str.toString();
    }


    public static void warn() {
        warn(null);
    }

    /**
     * erzeugt einen Logeintrag an dem aktuellen Punkt *
     */
    public static void warn(Object p_data) {
        if (Logger.getLogger(s_loggername).getLevel().toInt() < Level.WARN_INT)
            return;
        Logger.getLogger(s_loggername).info(createLogData(Level.WARN, p_data));
    }


    public static void error() {
        error(null);
    }

    /**
     * erzeugt einen Logeintrag an dem aktuellen Punkt *
     */
    public static void error(Object p_data) {
        if (Logger.getLogger(s_loggername).getLevel().toInt() < Level.ERROR_INT)
            return;
        Logger.getLogger(s_loggername).info(createLogData(Level.ERROR, p_data));
    }


    public static void info() {
        info(null);
    }

    /**
     * erzeugt einen Logeintrag an dem aktuellen Punkt *
     */
    public static void info(Object p_data) {
        if (Logger.getLogger(s_loggername).getLevel().toInt() < Level.INFO_INT)
            return;
        Logger.getLogger(s_loggername).info(createLogData(Level.INFO, p_data));
    }


    public static void debug() {
        debug(null);
    }

    /**
     * erzeugt einen Logeintrag an dem aktuellen Punkt *
     */
    public static void debug(Object p_data) {
        if (Logger.getLogger(s_loggername).getLevel().toInt() < Level.DEBUG_INT)
            return;
        Logger.getLogger(s_loggername).info(createLogData(Level.DEBUG, p_data));
    }


    public static void fatal() {
        fatal(null);
    }

    /**
     * erzeugt einen Logeintrag an dem aktuellen Punkt *
     */
    public static void fatal(Object p_data) {
        if (Logger.getLogger(s_loggername).getLevel().toInt() < Level.FATAL_INT)
            return;
        Logger.getLogger(s_loggername).info(createLogData(Level.FATAL, p_data));
    }


    /**
     * liefert den aktuellen Methodennamen anhand des Stack-Trace Index
     *
     * @return Methodenname
     */
    private static String getCurrentMethodName(int offset) {
        return Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getMethodName();
    }


    /**
     * liefert den aktuellen Klassennamen
     *
     * @return Klassenname
     */
    private static String getCurrentClassName(int offset) {
        return Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getClassName();
    }


    /**
     * liefert den aktuellen Dateinamen anhand des Stack-Trace Index
     *
     * @return Dateiname
     */
    private static String getCurrentFileName(int offset) {
        String filename = Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getFileName();
        int lineNumber = Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getLineNumber();

        return filename + ":" + lineNumber;
    }


    /**
     * liefert die aktuelle Zeilennummer anhand des Stack-Trace Index
     *
     * @return Nummer
     */
    private static int getCurrentLineNumber(int offset) {
        return Thread.currentThread().getStackTrace()[s_client_code_stack_index + offset].getLineNumber();
    }


    /**
     * liefert den Namen der aufrufenden Methode anhand des Stack-Trace Index
     *
     * @return Methodenname
     */
    private static String getInvokingMethodName(int offset) {
        return getCurrentMethodName(offset + 1);
    }


    /**
     * liefert den Klassennamen der aufrufenden Methode anhand des Stack-Trace Index
     *
     * @return Klassenname
     */
    private static String getInvokingClassName(int offset) {
        return getCurrentClassName(offset + 1);
    }


    /**
     * liefert den Dateinamen der aufrufenden Methode anhand des Stack-Trace Index
     *
     * @return Dateiname
     */
    private static String getInvokingFileName(int offset) {
        return getCurrentFileName(offset + 1);
    }


    /**
     * liefert den aktuellen FQN-Methodennamen anhand des Stack-Trace Index
     *
     * @return FQN-Methodenname
     */
    private static String getCurrentMethodNameFqn(int offset) {
        String currentClassName = getCurrentClassName(offset + 1);
        String currentMethodName = getCurrentMethodName(offset + 1);

        return currentClassName + "." + currentMethodName;
    }


    /**
     * liefert den aktuellen aufrufenenden FQN-Methodennamen anhand des Stack-Trace Index
     *
     * @return FQN-Methodenname
     */
    private static String getInvokingMethodNameFqn(int offset) {
        String invokingClassName = getInvokingClassName(offset + 1);
        String invokingMethodName = getInvokingMethodName(offset + 1);

        return invokingClassName + "." + invokingMethodName;
    }

}

