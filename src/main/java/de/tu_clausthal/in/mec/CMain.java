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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.benchmark.CSummary;
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
     * private ctor - avoid instantiation
     */
    private CMain()
    {
    }

    /**
     * main program
     *
     * @param p_args commandline arguments
     */
    public static void main( final String[] p_args )
    {

        // --- define CLI options --------------------------------------------------------------------------------------
        final Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, CCommon.getResourceString( CMain.class, "help" ) );
        l_clioptions.addOption( "configuration", true, CCommon.getResourceString( CMain.class, "config" ) );
        l_clioptions.addOption( "resetconfig", false, CCommon.getResourceString( CMain.class, "resetconfig" ) );
        l_clioptions.addOption( "nogui", true, CCommon.getResourceString( CMain.class, "nogui" ) );
        l_clioptions.addOption( "uibindport", true, CCommon.getResourceString( CMain.class, "uibindport" ) );
        l_clioptions.addOption( "uibindhost", true, CCommon.getResourceString( CMain.class, "uibindhost" ) );
        l_clioptions.addOption( "step", true, CCommon.getResourceString( CMain.class, "step" ) );
        l_clioptions.addOption( "benchmark", true, CCommon.getResourceString( CMain.class, "benchmark" ) );
        l_clioptions.addOption( "loglevel", true, CCommon.getResourceString( CMain.class, "loglevel" ) );
        l_clioptions.addOption( "logfile", true, CCommon.getResourceString( CMain.class, "logfile" ) );

        final CommandLineParser l_parser = new BasicParser();
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
            final HelpFormatter l_formatter = new HelpFormatter();
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


        // run startup checks
        for ( final String l_item : CCommon.startupchecks() )
            CLogger.out( l_item );


        // read the configuration directory (default ~/.mecsim)
        File l_defaultconfig = CConfiguration.getInstance().getLocation( "root" );
        if ( l_cli.hasOption( "configuration" ) )
            l_defaultconfig = new File( l_cli.getOptionValue( "configuration" ) );

        CConfiguration.getInstance().setConfigDir( l_defaultconfig );
        if ( l_cli.hasOption( "resetconfig" ) )
            CConfiguration.getInstance().delete();
        if ( !CConfiguration.getInstance().read() )
        {
            System.err.println( CCommon.getResourceString( CMain.class, "configload" ) );
            System.exit( -1 );
        }
        CBootstrap.configurationIsLoaded( CConfiguration.getInstance() );


        // define benchmark
        if ( l_cli.hasOption( "benchmark" ) )
            CSummary.getInstance().setFilename( l_cli.getOptionValue( "benchmark" ) );


        // --- invoke UI or start simulation ---------------------------------------------------------------------------
        try
        {
            if ( ( l_cli.hasOption( "uibindport" ) ) )
            {
                CConfiguration.getInstance().get().set( "ui/server/port", Integer.parseInt( l_cli.getOptionValue( "uibindport" ) ) );
                CLogger.info( CCommon.getResourceString( CMain.class, "bindportoverwrite", l_cli.getOptionValue( "uibindport" ) ) );
            }
        }
        catch ( final NumberFormatException l_exception )
        {
        }
        if ( ( l_cli.hasOption( "uibindhost" ) ) )
        {
            CConfiguration.getInstance().get().set( "ui/server/host", l_cli.getOptionValue( "uibindhost" ) );
            CLogger.info( CCommon.getResourceString( CMain.class, "bindhostoverwrite", l_cli.getOptionValue( "uibindhost" ) ) );
        }


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
                CBootstrap.onApplicationClose();

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
