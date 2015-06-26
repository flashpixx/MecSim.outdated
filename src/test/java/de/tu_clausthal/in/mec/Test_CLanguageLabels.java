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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * test all resource strings
 */
public class Test_CLanguageLabels
{
    /**
     * set with all labels *
     */
    private final Set<String> m_labels = new HashSet<>();
    /**
     * reg expression to extract label data *
     */
    private final Pattern m_language = Pattern.compile( "CCommon.getResourceString.+\\)" );

    /**
     * checks all labels within a Java file
     *
     * @param p_file
     */
    private void checkFile( final Path p_file ) throws IOException
    {
        if ( !p_file.toString().endsWith( ".java" ) )
            return;

        try
                (
                        final FileInputStream l_stream = new FileInputStream( p_file.toFile() );
                )
        {
            new MyMethodVisitor().visit( JavaParser.parse( l_stream ), null );
        }
        catch ( final ParseException l_exception )
        {
            fail( l_exception.getMessage() );
        }
    }

    /**
     * test-case all resource strings
     */
    @Test
    public void testResourceString()
    {
        // --- check source -> label definition
/*
        try
        {
            final List<Path> l_files = new ArrayList<>();
            Files.walk( Paths.get( CCommon.concatURL( CCommon.getResourceURL(), "../../src/main/java/" ).toURI() ) ).filter( Files::isRegularFile ).forEach(
                    path -> l_files.add( path )
            );

            for ( final Path l_item : l_files )
                this.checkFile( l_item );

        }
        catch ( final Exception l_exception )
        {
            fail( l_exception.getMessage() );
            return;
        }


        // --- check label -> property definition
        for ( final String l_language : CConfiguration.getInstance().get().<List<String>>get( "language/allow" ) )
        {
            final Set<String> l_labels = CConfiguration.getInstance().getResourceBundle( l_language ).keySet();
            l_labels.removeAll( m_labels );

            assertTrue(
                    String.format( "the following keys in language [%s] are unused: %s", l_language, StringUtils.join( l_labels, ", " ) ), l_labels.isEmpty()
            );
        }
*/
    }

    /**
     * AST visitor class
     */
    private class MyMethodVisitor extends VoidVisitorAdapter
    {
        /**
         * inner class name *
         */
        private String m_innerclass = "";
        /**
         * outer class name *
         */
        private String m_outerclass = "";
        /**
         * package name *
         */
        private String m_package = "";

        /**
         * checks all languages
         *
         * @param p_classname full qualified class name
         * @param p_label label name
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
            for ( final String l_language : CConfiguration.getInstance().get().<List<String>>get( "language/allow" ) )
                try
                {
                    final String l_label = CCommon.getResourceString( l_language, l_class, p_label );
                    assertFalse(
                            String.format(
                                    "label [%s] in language [%s] within class [%s] not found", CCommon.getResourceStringLabel( l_class, p_label ), l_language,
                                    p_classname
                            ), ( l_label == null ) || ( l_label.isEmpty() )
                    );
                }
                catch ( final IllegalStateException l_exception )
                {
                    return;
                }

            m_labels.add( CCommon.getResourceStringLabel( l_class, p_label ) );
        }

        /**
         * gets the class name and label name
         *
         * @param p_line input timmed line
         * @return null or array with class & label name
         */
        private String[] getParameter( final String p_line, final String p_package, final String p_outerclass, final String p_innerclass )
        {
            final Matcher l_matcher = m_language.matcher( p_line );
            if ( !l_matcher.find() )
                return null;

            final String[] l_split = l_matcher.group( 0 ).split( "," );
            final String[] l_return = new String[2];

            // class name
            l_return[0] = l_split[0].replace( "CCommon.getResourceString", "" ).replace( "(", "" ).trim();
            // label name
            l_return[1] = l_split[1].replace( ")", "" ).replace( "\"", "" ).split( ";" )[0].trim().toLowerCase();

            // setup class name
            if ( "this".equals( l_return[0] ) )
                l_return[0] = p_package + ClassUtils.PACKAGE_SEPARATOR + p_innerclass;
            else if ( l_return[0].endsWith( ".class" ) )
            {
                l_return[0] = l_return[0].replace( ".class", "" );
                if ( !l_return[0].contains( ClassUtils.PACKAGE_SEPARATOR ) )
                    l_return[0] = p_package + ( !m_innerclass.equals( m_outerclass ) ?
                            ClassUtils.PACKAGE_SEPARATOR + m_outerclass + ClassUtils.INNER_CLASS_SEPARATOR : ClassUtils.PACKAGE_SEPARATOR ) + l_return[0];
            }

            return l_return;
        }

        @Override
        public void visit( final ClassOrInterfaceDeclaration p_class, final Object p_arg )
        {
            if ( m_outerclass.isEmpty() )
            {

                m_outerclass = p_class.getName();
                m_innerclass = m_outerclass;
            }
            else
                m_innerclass = m_outerclass + ClassUtils.INNER_CLASS_SEPARATOR + p_class.getName();

            super.visit( p_class, p_arg );
        }

        @Override
        public void visit( final EnumDeclaration p_enum, final Object p_arg )
        {

            final String l_resetinner;
            final String l_resetouter;

            if ( m_outerclass.isEmpty() )
            {
                l_resetinner = null;
                l_resetouter = null;

                m_outerclass = p_enum.getName();
                m_innerclass = m_outerclass;
            }
            else if ( m_innerclass.isEmpty() )
            {
                l_resetinner = null;
                l_resetouter = null;

                m_innerclass = m_outerclass + ClassUtils.INNER_CLASS_SEPARATOR + p_enum.getName();
            }
            else
            {
                l_resetinner = m_innerclass;
                l_resetouter = m_outerclass;

                m_innerclass = m_outerclass + ClassUtils.INNER_CLASS_SEPARATOR + m_innerclass + ClassUtils.INNER_CLASS_SEPARATOR + p_enum.getName();

            }

            super.visit( p_enum, p_arg );

            if ( l_resetinner != null )
                m_innerclass = l_resetinner;
            if ( l_resetouter != null )
                m_outerclass = l_resetouter;
        }

        @Override
        public void visit( final MethodCallExpr p_methodcall, final Object p_arg )
        {
            final String[] l_label = this.getParameter( p_methodcall.toStringWithoutComments(), m_package, m_outerclass, m_innerclass );
            if ( l_label != null )
                this.checkLabel( l_label[0], l_label[1] );
            super.visit( p_methodcall, p_arg );
        }

        @Override
        public void visit( final PackageDeclaration p_package, final Object p_arg )
        {
            m_package = p_package.getName().toStringWithoutComments();
            super.visit( p_package, p_arg );
        }
    }

}
