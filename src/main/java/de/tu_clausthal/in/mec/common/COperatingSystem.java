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

package de.tu_clausthal.in.mec.common;


//import com.apple.eawt.Application;
//import com.apple.eawt.FullScreenUtilities;

import java.awt.*;


/**
 * OS specific support
 *
 * @see http://moomoohk.github.io/snippets/java_osx.html
 * @see http://www.oracle.com/technetwork/java/javatomac2-138389.html
 * @see http://www.oracle.com/technetwork/articles/javase/javatomac-140486.html
 */
public class COperatingSystem
{

    /**
     * Mac OS X check
     *
     * @return true on OSX
     */
    public static boolean isOSX()
    {
        return System.getProperty( "os.name" ).startsWith( "Mac OS X" );
    }


    /**
     * Windows check
     *
     * @return true on Windows
     */
    public static boolean isWindows()
    {
        return false;
    }


    /**
     * Linux check
     *
     * @return true on Linux
     */
    public static boolean isLinux()
    {
        return false;
    }


    /**
     * set the system properties depends on the OS
     */
    public static void setSystemProperties()
    {
        if ( isOSX() )
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );
    }


    /**
     * sets the frame properties depends on the OS
     *
     * @param p_frame main frame
     */
    public static void setFrameProperties( Frame p_frame )
    {
        if ( isOSX() )
        {
            //FullScreenUtilities.setWindowCanFullScreen( p_frame, true );
            //Application.getApplication().requestToggleFullScreen( p_frame );
        }
    }

}
