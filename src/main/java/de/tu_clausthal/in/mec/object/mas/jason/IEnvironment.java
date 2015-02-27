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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.CBrowser;
import de.tu_clausthal.in.mec.ui.CFrame;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;


/**
 * class of the Jason environment - the task of this class is the communication with outside data (IO with other
 * structure), in this context the environment class encapsulate all behaviour inside, because it will be triggerd from
 * the simulation core thread
 *
 * @tparam T typ of agend binding objects
 * @warning Jason binds a WebMindInspector on all network interfaces at the port 3272, without any kind of changing the
 * binding https://sourceforge.net/p/jason/svn/1817/tree/trunk/src/jason/architecture/MindInspectorWeb.java
 * @see http://jason.sourceforge.net/
 */
public abstract class IEnvironment<T> extends IMultiLayer<CAgent<T>>
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;

    /**
     * browser of the mindinspector - binding to the server port can be done after the first agent is exists
     */
    protected transient CBrowser m_mindinspector = null;

    /**
     * ctor of Jason structure
     */
    public IEnvironment()
    {
        this( null );
    }

    /**
     * ctor of Jason structure
     *
     * @param p_frame frame object set Jason mindinspector
     */
    public IEnvironment( final CFrame p_frame )
    {
        this.setFrame( p_frame );
    }

    /**
     * creates an agent filename on an agent name
     *
     * @param p_agentname agent name
     * @return file object
     * @throws IOException throws IO exception on file creating error
     */
    public static File createAgentFile( final String p_agentname ) throws IOException
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( IEnvironment.class, "aslempty" ) );

        final File l_asl = CConfiguration.getInstance().getMASDir( p_agentname + ".asl" );
        if ( l_asl.exists() )
            throw new IllegalStateException( CCommon.getResourceString( IEnvironment.class, "aslexist" ) );

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
    public static File getAgentFile( final String p_agentname )
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( IEnvironment.class, "aslempty" ) );

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
        final List<String> l_list = new LinkedList<>();
        for ( String l_file : CConfiguration.getInstance().getMASDir().list( new WildcardFileFilter( "*.asl" ) ) )
            l_list.add( new File( l_file ).getName() );

        return CCommon.CollectionToArray( String[].class, l_list );
    }

    /**
     * checks the syntax of an agent
     *
     * @param p_agentname agent name
     * @note should throw exception on syntax error
     */
    public static void checkAgentFileSyntax( final String p_agentname )
    {
        try
        {
            Agent.create( new AgArch(), Agent.class.getName(), null, getAgentFile( p_agentname ).toString(), null );
        }
        catch ( Exception l_exception )
        {
            throw new IllegalStateException( CCommon.getResourceString( IEnvironment.class, "syntaxerror", p_agentname, l_exception.getMessage() ) );
        }
    }

    /**
     * set the frame - if not exists
     *
     * @param p_frame frame object set Jason mindinspector
     */
    public final void setFrame( final CFrame p_frame )
    {
        if ( m_mindinspector != null )
            throw new IllegalStateException( CCommon.getResourceString( IEnvironment.class, "frame" ) );

        // register web mindinspector (DoS threat)
        if ( ( p_frame != null ) && ( !p_frame.containsWidget( "Jason Mindinspector" ) ) )
        {
            m_mindinspector = new CBrowser();
            p_frame.addWidget( "Jason Mindinspector", m_mindinspector );
        }
    }

    @Override
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        super.step( p_currentstep, p_layer );

        // mindinspector needs to load if there exists agents
        if ( ( m_mindinspector != null ) && ( m_data.size() > 0 ) )
            m_mindinspector.load( "http://localhost:3272" );
    }

    @Override
    public final void release()
    {
        for ( IAgent l_agent : m_data )
            l_agent.release();
        m_data.clear();
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException            throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        this.setFrame( CSimulation.getInstance().getUI() );
    }

}
