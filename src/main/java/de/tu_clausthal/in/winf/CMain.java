/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
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

import de.tu_clausthal.in.winf.ui.CFrame;
import org.apache.commons.cli.*;
import org.pmw.tinylog.Level;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * main class of the application
 *
 * @note Main must be started with option "-Xmx2g", because we need memory to create graph structure
 */
public class CMain
{

    /**
     * main program
     *
     * @param p_args commandline arguments
     */
    public static void main( String[] p_args ) throws Exception
    {

        // --- define CLI options --------------------------------------------------------------------------------------
        Options l_clioptions = new Options();
        l_clioptions.addOption( "help", false, "shows this help" );
        l_clioptions.addOption( "configuration", true, "configuration directory" );
        l_clioptions.addOption( "graph", true, "OSM graph URL (see configuration file description of option 'RoutingMap')" );
        l_clioptions.addOption( "loglevel", true, "level of the logger" );
        l_clioptions.addOption( "logfile", true, "logfile (default: mecsim-<startup datetime>.txt)" );

        CommandLineParser l_parser = new BasicParser();
        CommandLine l_cli = l_parser.parse( l_clioptions, p_args );


        // --- process CLI arguments and set configuration -------------------------------------------------------------
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
        File l_config = new File( System.getProperty( "user.home" ) + File.separator + ".mecsim" );
        if ( l_cli.hasOption( "configuration" ) )
            l_config = new File( l_cli.getOptionValue( "configuration" ) );

        CConfiguration.getInstance().setConfigDir( l_config );
        CConfiguration.getInstance().read();

        if ( l_cli.hasOption( "graph" ) )
            CConfiguration.getInstance().get().RoutingMap = l_cli.getOptionValue( "graph" );

        CBootstrap.ConfigIsLoaded( CConfiguration.getInstance() );


        // --- invoke UI -----------------------------------------------------------------------------------------------
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                CFrame l_frame = new CFrame();
                l_frame.setTitle( "TU-Clausthal Wirtschaftsinformatik - MecSim" );
                l_frame.setVisible( true );
            }
        } );
    }

}
