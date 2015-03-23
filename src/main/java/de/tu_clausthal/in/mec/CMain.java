/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.CUI;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.pmw.tinylog.Level;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * main class of the application
 */
public class CMain
{

    /**
     * gigabyte multiplier
     */
    private static final long c_gb = 1024 * 1024 * 1024;
    /**
     * needed memory in gigabyte
     */
    private static final float c_neededmemory = 2.5f;


    /**
     * private ctor - avoid instantiation
     */
    private CMain()
    {
    }

    ;


    /**
     * main program
     *
     * @param p_args commandline arguments
     * @todo create docking structure https://gist.github.com/jewelsea/9579047
     */
    public static void main( String[] p_args )
    {

        // --- define CLI options --------------------------------------------------------------------------------------
        Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, CCommon.getResourceString( CMain.class, "help" ) );
        l_clioptions.addOption( "configuration", true, CCommon.getResourceString( CMain.class, "config" ) );
        l_clioptions.addOption( "nogui", true, CCommon.getResourceString( CMain.class, "nogui" ) );
        l_clioptions.addOption( "step", true, CCommon.getResourceString( CMain.class, "step" ) );
        l_clioptions.addOption( "loglevel", true, CCommon.getResourceString( CMain.class, "loglevel" ) );
        l_clioptions.addOption( "logfile", true, CCommon.getResourceString( CMain.class, "logfile" ) );

        CommandLineParser l_parser = new BasicParser();
        CommandLine l_cli = null;
        try
        {
            l_cli = l_parser.parse( l_clioptions, p_args );
        }
        catch ( Exception l_exception )
        {
            System.out.println( CCommon.getResourceString( CMain.class, "parseerror" ) );
            System.exit( -1 );
        }


        // --- process CLI arguments and push configuration ------------------------------------------------------------
        if ( l_cli.hasOption( "help" ) )
        {
            HelpFormatter l_formatter = new HelpFormatter();
            l_formatter.printHelp( ( new java.io.File( CMain.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getName() ), l_clioptions );
            System.exit( 0 );
        }


        // create logger instance
        String l_logfile = "mecsim-" + ( new SimpleDateFormat( "yyyy-dd-MM-HH-mm" ) ).format( Calendar.getInstance().getTime() ) + ".txt";
        if ( l_cli.hasOption( "logfile" ) )
            l_logfile = l_cli.getOptionValue( "logfile" );

        Level l_loglevel = Level.OFF;
        if ( l_cli.hasOption( "loglevel" ) )
            l_loglevel = Level.valueOf( l_cli.getOptionValue( "loglevel" ).toUpperCase() );

        CLogger.create( l_loglevel, l_logfile );


        // read the configuration directory (default ~/.mecsim)
        File l_defaultconfig = CConfiguration.getInstance().getLocation( "root" );
        if ( l_cli.hasOption( "configuration" ) )
            l_defaultconfig = new File( l_cli.getOptionValue( "configuration" ) );

        CConfiguration.getInstance().setConfigDir( l_defaultconfig );
        CConfiguration.getInstance().read();

        CBootstrap.configIsLoaded( CConfiguration.getInstance() );


        // --- invoke UI or start simulation ---------------------------------------------------------------------------
        CLogger.out( CCommon.getResourceString( CMain.class, "memory", c_neededmemory, Runtime.getRuntime().maxMemory() / c_gb ), Runtime.getRuntime().maxMemory() / c_gb < c_neededmemory );


        // run application
        if ( !l_cli.hasOption( "nogui" ) )
            CUI.main( null );
        else
        {
            try
            {
                if ( ( !l_cli.hasOption( "step" ) ) )
                    throw new IllegalAccessException( CCommon.getResourceString( CMain.class, "stepnotset" ) );

                CSimulation.getInstance().load( new File( l_cli.getOptionValue( "nogui" ) ) );
                CSimulation.getInstance().start( Integer.parseInt( l_cli.getOptionValue( "step" ) ) );

            }
            catch ( final Exception l_exception )
            {
                CLogger.error( l_exception );
                CLogger.out( CCommon.getResourceString( CMain.class, "loadingerror" ) );
                System.exit( -1 );
            }
        }

    }

}
