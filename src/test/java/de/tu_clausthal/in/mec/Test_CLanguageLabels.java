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
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;


/**
 * test all resource strings
 */
public class Test_CLanguageLabels
{
    /** set with all labels * */
    private final Set<String> m_labels = new HashSet<>();
    /** reg expression to extract label data * */
    private final Pattern m_language = Pattern.compile( "CCommon.getResourceString.+\\)" );
    /** regexpression to extract class name * */
    private final Pattern m_class = Pattern.compile( ".*class\\s+.*(implements|extends)?" );

    /**
     * test-case all resource strings
     */
    @Test
    public void testResourceString()
    {
        // --- check  source -> label definition
        final String[] l_input = CCommon.getResourceURL().toString().split( File.separator );
        String[] l_search = new String[l_input.length + 1];
        System.arraycopy( l_input, 0, l_search, 0, l_search.length - 3 );
        l_search[l_search.length - 3] = "src";
        l_search[l_search.length - 2] = "main";
        l_search[l_search.length - 1] = "java";

        try
        {

            final List<Path> l_files = new ArrayList<>();
            Files.walk( Paths.get( new URL( StringUtils.join( l_search, File.separator ) ).getPath() ) ).filter( Files::isRegularFile ).forEach( path -> l_files.add( path ) );

            for ( Path l_item : l_files )
                this.checkFile( l_item );

        }
        catch ( final Throwable l_throwable )
        {
            fail( l_throwable.getMessage() );
            return;
        }


        // --- check label -> property definition
        for ( String l_language : CConfiguration.getInstance().get().<List<String>>getTraverse( "language/allow" ) )
        {
            final Set<String> l_labels = CConfiguration.getInstance().getResourceBundle( l_language ).keySet();
            l_labels.removeAll( m_labels );
            if ( !l_labels.isEmpty() )
            {
                fail( String.format( "the following keys in language [%s] are unused: %s", l_language, StringUtils.join( l_labels, ", " ) ) );
                return;
            }
        }
    }


    /**
     * checks all labels within a Java file
     *
     * @param p_file
     */
    private void checkFile( final Path p_file ) throws IOException
    {
        if ( !p_file.toString().endsWith( ".java" ) )
            return;

        String l_classname = "";
        String l_subclassname = "";
        String l_package = "";
        for ( String l_line : Files.readAllLines( p_file ) )
        {
            final String l_trimline = l_line.trim();
            if ( ( l_trimline.startsWith( "//" ) ) || ( l_trimline.startsWith( "/*" ) ) || ( l_trimline.startsWith( "*" ) ) )
                continue;

            String l_help = this.getPackagename( l_line );
            if ( ( l_package.isEmpty() ) && ( !l_help.isEmpty() ) )
            {
                l_package = this.getPackagename( l_line );
                continue;
            }

            l_help = this.getClassname( l_line );
            if ( !l_help.isEmpty() )
            {
                // outer & inner class
                l_classname = l_classname.isEmpty() ? l_help : l_classname;
                l_subclassname = l_package + "." + ( l_subclassname.isEmpty() ? l_classname : l_classname + "$" + l_help );
                continue;
            }

            final String[] l_parameter = this.getParameter( l_line );
            if ( ( l_parameter == null ) || ( l_parameter[0] == null ) || ( l_parameter[0].isEmpty() ) || ( l_parameter[1] == null ) || ( l_parameter[1].isEmpty() ) )
                continue;

            // check class parameter
            if ( !l_parameter[0].equals( "this" ) )
                l_subclassname = l_parameter[0].contains( "." ) ? l_parameter[0] : l_package + "." + l_parameter[0];

            // check label and add label to the set
            this.checkLabel( l_subclassname, l_parameter[1] );
        }
    }


    /**
     * gets the package name of the file
     *
     * @param p_line input timmed line
     * @return empty string or package name
     */
    private String getPackagename( final String p_line )
    {
        if ( ( p_line.startsWith( "package" ) ) && ( p_line.endsWith( ";" ) ) )
            return p_line.replace( ";", "" ).replace( "package", "" ).trim();

        return "";
    }


    /**
     * gets the class name of the file
     *
     * @param p_line input timmed line
     * @return empty string or class name
     */
    private String getClassname( final String p_line )
    {
        final Matcher l_matcher = m_class.matcher( p_line );
        if ( ( l_matcher.find() ) && ( !p_line.endsWith( ";" ) ) && ( !p_line.contains( "." ) ) && ( !p_line.contains( "(" ) ) && ( !p_line.contains( ")" ) ) )
            return l_matcher.group( 0 ).split( "class" )[1].trim().split( " " )[0].trim().split( "<" )[0].trim();

        return "";
    }


    /**
     * gets the class name and label name
     *
     * @param p_line input timmed line
     * @return null or string array
     */
    private String[] getParameter( final String p_line )
    {
        final Matcher l_matcher = m_language.matcher( p_line );
        if ( l_matcher.find() )
        {
            final String[] l_split = l_matcher.group( 0 ).split( "," );
            final String[] l_return = new String[2];
            l_return[0] = l_split[0].replace( "CCommon.getResourceString", "" ).replace( "(", "" ).replace( ".class", "" ).trim();
            l_return[1] = l_split[1].replace( ")", "" ).replace( "\"", "" ).trim().toLowerCase();
            return l_return;
        }

        return null;
    }


    /**
     * checks all languages
     *
     * @param p_classname
     * @param p_label
     */
    private void checkLabel( final String p_classname, final String p_label )
    {
        // construct class object
        final Class<?> l_class;
        try
        {
            l_class = Class.forName( p_classname );
        }
        catch ( final ClassNotFoundException l_exception )
        {
            fail( String.format( "class [%s] not found", p_classname ) );
            return;
        }

        // check resource
        for ( String l_language : CConfiguration.getInstance().get().<List<String>>getTraverse( "language/allow" ) )
            try
            {
                final String l_label = CCommon.getResourceString( l_language, l_class, p_label );
                if ( ( l_label == null ) || ( l_label.isEmpty() ) )
                    throw new IllegalStateException();
            }
            catch ( final IllegalStateException l_exception )
            {
                fail( String.format( "label [%s] in language [%s] within class [%s] not found", CCommon.getResourceStringLabel( l_class, p_label ), l_language, p_classname ) );
                return;
            }

        m_labels.add( CCommon.getResourceStringLabel( l_class, p_label ) );
    }

}
