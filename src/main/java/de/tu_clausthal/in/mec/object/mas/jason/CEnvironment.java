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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.ui.CBrowser;
import de.tu_clausthal.in.mec.ui.CFrame;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.runtime.Settings;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * class of the Jason environment - the task of this class is the communication with outside data (IO with other
 * structure), in this context the environment class encapsulate all behaviour inside, because it will be triggerd from
 * the simulation core thread
 *
 * @todo try to refactor - Jason binds a WebMindInspector on all network interfaces at the port 3272, without any kind
 * of disabeling / modifiying - https://sourceforge.net/p/jason/svn/1817/tree/trunk/src/jason/architecture/MindInspectorWeb.java
 * @see http://jason.sourceforge.net/api/jason/environment/package-summary.html
 */
public class CEnvironment<T> extends IMultiLayer<CAgent<T>>
{

    /**
     * global literal storage *
     */
    protected CLiteralStorage m_literals = new CLiteralStorage();
    /**
     * map of all actions which can be called by the agents *
     */
    protected Map<String, Pair<Method, Object>> m_methods = new HashMap();
    /**
     * field list of object fields that converted to belief-base data
     */
    protected Map<String, Pair<Field, Object>> m_fields = new HashMap();
    /**
     * browser of the mindinspector - binding to the server port can be done after the first agent is exists
     */
    protected CBrowser m_mindinspector = new CBrowser();


    /**
     * ctor of Jason structure
     *
     * @param p_frame frame object set Jason mindinspector
     */
    public CEnvironment( CFrame p_frame )
    {
        p_frame.addWidget( "Jason Mindinspector", m_mindinspector );
        this.addObjectMethods( this );
        try
        {
            m_data.add( new CAgent<T>( "I'm the first", "agent" ) );
            m_data.add( new CAgent<T>( "I'm the first", "agent" ) );
        }
        catch ( Exception l_exception )
        {
        }
    }

    /**
     * creates an agent filename on an agent name
     *
     * @param p_agentname agent name
     * @return file object
     */
    public static File getAgentFilename( String p_agentname ) throws IOException
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file name need not be empty" );

        File l_asl = CConfiguration.getInstance().getMASDir( p_agentname + ".asl" );
        if ( l_asl.exists() )
            throw new IllegalStateException( "ASL file exists" );

        l_asl.createNewFile();
        return l_asl;
    }

    /**
     * get from an agent name the storing file name
     *
     * @param p_agentname agent name
     * @return existing file object
     * @note must support a agent filename as file with extension and without extension
     */
    public static File getFilename( String p_agentname )
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file name need not be empty" );

        return CConfiguration.getInstance().getMASDir( p_agentname.endsWith( ".asl" ) ? p_agentname : p_agentname + ".asl" );
    }

    /**
     * gets a list of all agents file names
     *
     * @return string list with the filenames only
     */
    public static String[] getAgentFilenames()
    {
        List<String> l_list = new LinkedList();
        for ( String l_file : CConfiguration.getInstance().getMASDir().list( new WildcardFileFilter( "*.asl" ) ) )
            l_list.add( new File( l_file ).getName() );

        return CCommon.ColletionToArray( String[].class, l_list );
    }

    /**
     * checks the syntax of an agent
     *
     * @param p_agentname agent name
     * @note should throw exception on syntax error
     */
    public static void checkAgentFileSyntax( String p_agentname )
    {
        Settings x = new Settings();
        System.out.println( x.logLevel() );
        try
        {
            Agent.create( new AgArch(), Agent.class.getName(), null, getFilename( p_agentname ).toString(), x );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalStateException( "agent [" + p_agentname + "] error: " + l_exception.getMessage() );
        }
    }

    /**
     * adds all object fields to the agent, fields are converted to literals by means of their data type
     *
     * @param p_object object
     */
    public void addObjectFields( Object p_object )
    {
        m_fields.putAll( m_literals.addObjectFields( p_object ) );
    }

    /**
     * adds all object methods to the agent, methods are converted to internal actions
     *
     * @param p_object object
     */
    public void addObjectMethods( Object p_object )
    {
        m_methods.putAll( m_literals.addObjectMethods( p_object ) );
    }


    @Override
    public void resetData()
    {
        for ( IAgent l_agent : m_data )
            l_agent.release();
        m_data.clear();
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        // get all data for global perceptions (get analyse function and all properties of the object
        m_literals.addAll( this.analyse() );
        m_literals.addObjectFields( this );

        try
        {
            // mind inspector works after an agent exists, so we need
            // to bind the browser after the first agent exists
            m_data.add( new CAgent( "agent", new CTestAgent() ) );

            if ( m_data.size() > 0 )
                m_mindinspector.load( "http://localhost:3272" );

        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }

    }
}
