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
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.ui.CBrowser;
import de.tu_clausthal.in.mec.ui.CFrame;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * class of the Jason environment - the task of this class is the communication with outside data (IO with other
 * structure), in this context the environment class encapsulate all behaviour inside, because it will be triggerd from
 * the simulation core thread
 *
 * @warning Jason binds a WebMindInspector on all network interfaces at the port 3272, without any kind of changing the
 * binding https://sourceforge.net/p/jason/svn/1817/tree/trunk/src/jason/architecture/MindInspectorWeb.java
 * @see http://jason.sourceforge.net/
 */
public abstract class IEnvironment<T> extends IMultiLayer<CAgent<T>>
{

    /**
     * browser of the mindinspector - binding to the server port can be done after the first agent is exists
     */
    protected CBrowser m_mindinspector = null;


    /**
     * ctor of Jason structure
     *
     * @param p_frame frame object set Jason mindinspector
     */
    public IEnvironment( CFrame p_frame )
    {
        // register web mindinspector (DoS threat)
        if ( ( p_frame != null ) && ( !p_frame.containsWidget( "Jason Mindinspector" ) ) )
        {
            m_mindinspector = new CBrowser();
            p_frame.addWidget( "Jason Mindinspector", m_mindinspector );
        }
    }

    /**
     * creates an agent filename on an agent name
     *
     * @param p_agentname agent name
     * @return file object
     */
    public static File createAgentFile( String p_agentname ) throws IOException
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
     * @todo add ASL build-in files with the resource directory
     */
    public static File getAgentFile( String p_agentname )
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( "ASL file name need not be empty" );

        return CConfiguration.getInstance().getMASDir( p_agentname.endsWith( ".asl" ) ? p_agentname : p_agentname + ".asl" );
    }

    /**
     * gets a list of all agents file names
     *
     * @return string list with the filenames only
     * @todo add ASL build-in files with the resource directory
     */
    public static String[] getAgentFiles()
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
        try
        {
            Agent.create( new AgArch(), Agent.class.getName(), null, getAgentFile( p_agentname ).toString(), null );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalStateException( "agent [" + p_agentname + "] error: " + l_exception.getMessage() );
        }
    }


    @Override
    public void release()
    {
        for ( de.tu_clausthal.in.mec.object.mas.IAgent l_agent : m_data )
            l_agent.release();
        m_data.clear();
    }


    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        // mindinspector needs to load if there exists agents
        if ( ( m_mindinspector != null ) && ( m_data.size() > 0 ) )
            m_mindinspector.load( "http://localhost:3272" );
    }
}
