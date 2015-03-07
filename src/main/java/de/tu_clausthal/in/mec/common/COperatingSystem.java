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

import org.apache.commons.lang3.StringUtils;

import java.awt.*;


/**
 * OS specific support
 */
public class COperatingSystem
{
    /**
     * OS minor version *
     */
    private static int m_minor = 0;
    /**
     * OS major version *
     */
    private static int m_major = 0;

    static
    {
        final String[] l_parts = StringUtils.split( System.getProperty( "os.version" ), "." );

        if ( l_parts.length > 0 )
            m_major = Integer.parseInt( l_parts[0] );
        if ( l_parts.length > 1 )
            m_minor = Integer.parseInt( l_parts[1] );
    }


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
        return System.getProperty( "os.name" ).startsWith( "Windows" );
    }


    /**
     * Linux check
     *
     * @return true on Linux
     */
    public static boolean isLinux()
    {
        return System.getProperty( "os.name" ).startsWith( "Linux" );
    }


    /**
     * set the system properties depends on the OS
     *
     * @see http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    public static void setSystemProperties()
    {
        if ( isOSX() )
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );
        if ( isWindows() )
            System.setProperty( "swing.defaultlaf", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
    }


    /**
     * sets the frame properties depends on the OS
     *
     * @param p_frame main frame
     * @note independed with reflection api to avoid compiler errors on other OS
     * @see http://moomoohk.github.io/snippets/java_osx.html
     * @see http://www.oracle.com/technetwork/java/javatomac2-138389.html
     * @see http://www.oracle.com/technetwork/articles/javase/javatomac-140486.html
     */
    public static void setFrameProperties( Frame p_frame )
    {
        if ( ( isOSX() ) && ( getMajorVersion() >= 10 ) && ( getMinorVersion() >= 7 ) )
        {
            try
            {
                //FullScreenUtilities.setWindowCanFullScreen( p_frame, true );
                CReflection.getClassMethod( Class.forName( "com.apple.eawt.FullScreenUtilities" ), "setWindowCanFullScreen", new Class<?>[]{Window.class, Boolean.TYPE} ).getHandle().invoke( p_frame, true );

                //Application.getApplication().requestToggleFullScreen( p_frame );
                CReflection.getClassMethod( Class.forName( "com.apple.eawt.Application" ), "requestToggleFullScreen", new Class<?>[]{Window.class} ).getHandle().invoke( p_frame );

            }
            catch ( ClassNotFoundException | IllegalAccessException l_exception )
            {
            }
            catch ( Throwable throwable )
            {
            }
        }
    }


    /**
     * returns the OS minor version
     *
     * @return version
     */
    public static int getMinorVersion()
    {
        return m_minor;
    }

    /**
     * returns the OS major version
     *
     * @return version
     */
    public static int getMajorVersion()
    {
        return m_major;
    }

}
