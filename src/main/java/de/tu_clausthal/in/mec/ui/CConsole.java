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

package de.tu_clausthal.in.mec.ui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * text console to show log messages within the UI
 *
 * @note log messages can be written with System.out or System.err
 */
public class CConsole extends JPanel
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * output panel *
     */
    protected JEditorPane m_output = new JEditorPane();


    /**
     * ctor, that redirects the default console output streams
     */
    public CConsole()
    {
        super( new BorderLayout() );
        m_output.setEditable( false );

        JScrollPane l_scroll = new JScrollPane( m_output );
        l_scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        l_scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        this.add( l_scroll, BorderLayout.CENTER );

        // redirect default streams
        System.setOut( new PrintStream( new ConsoleOutputStream( m_output.getDocument(), Color.black ), true ) );
        System.setErr( new PrintStream( new ConsoleOutputStream( m_output.getDocument(), Color.red ), true ) );
    }

    /**
     * clears all text *
     */
    public void clear()
    {
        m_output.setText( null );
    }


    /**
     * inner class to replace default output stream writer
     *
     * @todo add line limit
     */
    protected class ConsoleOutputStream extends ByteArrayOutputStream
    {
        /* system default end-of-line separator **/
        private final String EOL = System.getProperty( "line.separator" );
        /**
         * format attributes *
         */
        private SimpleAttributeSet m_attributes;
        /**
         * buffer of the content *
         */
        private StringBuffer m_buffer = new StringBuffer( 120 );
        /**
         * first line flag - to remove a blank line at the begin *
         */
        private boolean m_isFirstLine = true;
        /**
         * reference to the document *
         */
        private Document m_document = null;


        /**
         * ctor
         *
         * @param p_document  document, which should be written
         * @param p_textcolor color of the text
         */
        public ConsoleOutputStream( Document p_document, Color p_textcolor )
        {
            m_document = p_document;

            if ( p_textcolor != null )
            {
                m_attributes = new SimpleAttributeSet();
                StyleConstants.setForeground( m_attributes, p_textcolor );
            }
        }

        /**
         * flushes the stream *
         */
        public void flush()
        {
            String l_message = toString();
            if ( l_message.length() == 0 ) return;

            this.append( l_message );
            reset();
        }

        /**
         * append a message to the stream
         *
         * @param p_message string
         */
        private void append( String p_message )
        {
            if ( m_document.getLength() == 0 )
                m_buffer.setLength( 0 );

            if ( EOL.equals( p_message ) )
                m_buffer.append( p_message );
            else
            {
                m_buffer.append( p_message );
                clear();
            }

        }

        /**
         * clears the document *
         */
        private void clear()
        {
            if ( ( m_isFirstLine ) && ( m_document.getLength() != 0 ) )
                m_buffer.insert( 0, EOL );

            m_isFirstLine = false;
            try
            {
                m_document.insertString( m_document.getLength(), m_buffer.toString(), m_attributes );
                m_output.setCaretPosition( m_document.getLength() );
            }
            catch ( BadLocationException ble )
            {
            }

            m_buffer.setLength( 0 );
        }
    }

}
