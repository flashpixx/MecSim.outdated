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

import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.CFrame;
import org.apache.commons.cli.*;
import org.pmw.tinylog.Level;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * main class of the application http://blog.kennardconsulting.com/2013/04/java-based-json-and-json-schema-user.html
 */
public class CMain
{

    /**
     * main program
     *
     * @param p_args commandline arguments
     */
    public static void main( String[] p_args )
    {

        // --- define CLI options --------------------------------------------------------------------------------------
        Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, "shows this help" );
        l_clioptions.addOption( "configuration", true, "configuration directory" );
        l_clioptions.addOption( "nogui", true, "disables the GUI and loads the stored file for simulating" );
        l_clioptions.addOption( "step", true, "number of running simulation steps (must use in combination with 'nogui')" );
        l_clioptions.addOption( "loglevel", true, "level of the logger" );
        l_clioptions.addOption( "logfile", true, "logfile (default: mecsim-<startup datetime>.txt)" );

        CommandLineParser l_parser = new BasicParser();
        CommandLine l_cli = null;
        try
        {
            l_cli = l_parser.parse( l_clioptions, p_args );
        }
        catch ( Exception l_exception )
        {
            System.out.println( "argument parse error" );
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
        File l_defaultconfig = CConfiguration.getInstance().getConfigDir();
        if ( l_cli.hasOption( "configuration" ) )
            l_defaultconfig = new File( l_cli.getOptionValue( "configuration" ) );

        CConfiguration.getInstance().setConfigDir( l_defaultconfig );
        CConfiguration.getInstance().read();

        CBootstrap.ConfigIsLoaded( CConfiguration.getInstance() );


        // --- invoke UI or start simulation ---------------------------------------------------------------------------
        if ( !l_cli.hasOption( "nogui" ) )
            CSimulation.getInstance().setUI( new CFrame() );
        else
        {
            try
            {
                if ( ( !l_cli.hasOption( "step" ) ) )
                    throw new IllegalAccessException( "step value is not set" );

                FileInputStream l_stream = new FileInputStream( l_cli.getOptionValue( "nogui" ) );
                ObjectInputStream l_input = new ObjectInputStream( l_stream );
                CSimulation.getInstance().load( l_input );
                l_input.close();
                l_stream.close();

                CSimulation.getInstance().start( Integer.parseInt( l_cli.getOptionValue( "step" ) ) );

            }
            catch ( Exception l_exception )
            {
                CLogger.error( l_exception );
                CLogger.out( "Error on loading / running simulation data" );
                System.exit( -1 );
            }
        }

        System.exit( 0 );
    }

}
