/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.jason.action.CBeliefRemove;
import de.tu_clausthal.in.mec.object.mas.jason.action.CInternalEmpty;
import de.tu_clausthal.in.mec.object.mas.jason.action.CLiteral2Number;
import de.tu_clausthal.in.mec.runtime.ISerializable;
import de.tu_clausthal.in.mec.ui.web.CBrowser;
import jason.asSemantics.Agent;
import jason.asSemantics.InternalAction;
import jason.asSyntax.parser.as2j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


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
@SuppressWarnings( "serial" )
public abstract class IEnvironment<T> extends IMultiLayer<CAgent<T>> implements ISerializable
{

    /**
     * agent storage for fast instantiation
     */
    public static final CAgentTemplateFactory AGENTTEMPLATEFACTORY = new CAgentTemplateFactory();
    /**
     * static list of internal overwrite actions
     */
    public static final Map<String, InternalAction> INTERNALACTION = new HashMap<String, InternalAction>()
    {{
            // overwrite default internal actions
            final CInternalEmpty l_empty13 = new CInternalEmpty( 1, 3 );
            put( "jason.stdlib.clone", new CInternalEmpty() );
            put( "jason.stdlib.wait", l_empty13 );
            put( "jason.stdlib.create_agent", l_empty13 );
            put( "jason.stdlib.kill_agent", new CInternalEmpty( 1, 1 ) );
            put( "jason.stdlib.stopMAS", new CInternalEmpty( 0, 0 ) );

            // add own function
            put( "mecsim.literal2number", new CLiteral2Number() );
            put( "mecsim.removeBelief", new CBeliefRemove() );
        }};
    /**
     * browser of the mindinspector - binding to the server port can be done after the first agent is exists
     */
    private transient CBrowser m_mindinspector;
    /**
     * agent file suffix *
     */
    private static final String c_filesuffix = ".asl";

    /**
     * checks the syntax of an agent
     *
     * @param p_agentname agent name
     * @note should throw exception on syntax error
     * @warning exception is not thrown on parsing error, the error is only send to log (see "Agent.parseAS" method), so we pass the log message
     * to a variable, check the parsing result and the variable content for throwing an own exception
     */
    public static void checkAgentFileSyntax( final String p_agentname )
    {
        try
        {
            // initialize agent manually to modify the internal agent structure (with reflection and pass the
            // log message to an own logger to get the parsing error messages). The logger on the agent class
            // is a member variable on the parser class a static variable
            final CLogger l_logger = new CLogger();

            final Agent l_agent = new Agent();
            CReflection.getClassField( Agent.class, "logger" ).getSetter().invokeWithArguments( l_agent, l_logger );
            // create internal actions map - reset the map and overwrite not useable actions with placeholder
            CReflection.getClassField( l_agent.getClass(), "internalActions" ).getSetter().invoke( l_agent, INTERNALACTION );

            final as2j l_parser = new as2j( FileUtils.openInputStream( getAgentFile( p_agentname ) ) );
            CReflection.getClassField( as2j.class, "logger" ).getSetter().invokeWithArguments( l_logger );

            // performtemplate agent initalizing and parsing
            l_agent.initAg();
            l_parser.agent( l_agent );

            // on log message wie report an error an create linebreaks for later splitting
            if ( !l_logger.get().isEmpty() )
                throw new IllegalStateException( StringUtils.join( l_logger.get(), "\n" ) );

        }
        catch ( final Exception l_exception )
        {
            throw new IllegalStateException( CCommon.getResourceString( IEnvironment.class, "syntaxerror", p_agentname, "\n\n" + l_exception.getMessage() ) );
        }
        catch ( final Throwable l_throwable )
        {
            throw new IllegalStateException( CCommon.getResourceString( IEnvironment.class, "syntaxerror", p_agentname, "\n\n" + l_throwable.getMessage() ) );
        }

    }

    /**
     * creates an agent filename on an agent name
     *
     * @param p_agentname agent name
     * @return file object
     *
     * @throws IOException throws IO exception on file creating error
     */
    public static File createAgentFile( final String p_agentname ) throws IOException
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( IEnvironment.class, "aslempty" ) );

        final File l_asl = CConfiguration.getInstance().getLocation( "mas", p_agentname + c_filesuffix );
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
     *
     * @note must support a agent filename as file with extension and without extension
     */
    public static File getAgentFile( final String p_agentname )
    {
        if ( ( p_agentname == null ) || ( p_agentname.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( IEnvironment.class, "aslempty" ) );

        return CConfiguration.getInstance().getLocation( "mas", p_agentname.endsWith( c_filesuffix ) ? p_agentname : p_agentname + c_filesuffix );
    }

    /**
     * gets a list of all agents file names
     *
     * @return string list with the filenames only
     */
    public static String[] getAgentFiles()
    {
        final List<String> l_list = new LinkedList<>();
        for ( final String l_file : CConfiguration.getInstance().getLocation( "mas" ).list( new WildcardFileFilter( "*" + c_filesuffix ) ) )
            l_list.add( new File( l_file ).getName().replace( c_filesuffix, "" ) );

        return CCommon.convertCollectionToArray( String[].class, l_list );
    }

    @Override
    public final void onSimulationReset()
    {
        AGENTTEMPLATEFACTORY.clear();
    }

    /**
     * @bug UI frame
     */
    @Override
    public final void onDeserializationComplete()
    {
        //this.setFrame( CSimulation.getInstance().getUIServer() );
    }

    /**
     * @bug UI frame
     */
    @Override
    public final void onDeserializationInitialization()
    {
        //if ( CSimulation.getInstance().hasUI() )
        //    CSimulation.getInstance().getUIServer().removeWidget( "Jason Mindinspector" );
    }

    @Override
    public final void release()
    {
        for ( final IAgent l_agent : m_data )
            l_agent.release();
        m_data.clear();
    }

    @Override
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        // mindinspector needs to load if there exists agents
        if ( ( m_mindinspector != null ) && ( m_data.size() > 0 ) )
            m_mindinspector.load( "http://localhost:3272" );
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     * @bug UI frame
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        //this.setFrame( CSimulation.getInstance().getUIServer() );
    }


    /**
     * interal log writer to catch Jason parsing messages
     */
    private static class CLogger extends java.util.logging.Logger
    {
        /**
         * log list
         **/
        private final LinkedList<String> m_logs = new LinkedList<>();


        public CLogger()
        {
            this( null, null );
        }


        protected CLogger( final String p_name, final String p_resourceBundleName )
        {
            super( p_name, p_resourceBundleName );
            this.setUseParentHandlers( false );
        }

        /**
         * returns the full log list
         *
         * @return list
         */
        public LinkedList<String> get()
        {
            return m_logs;
        }

        /**
         * clears the log list
         */
        public void clear()
        {
            m_logs.clear();
        }

        @Override
        public void log( final Level p_level, final String p_msg )
        {
            final String[] l_split = StringUtils.split( p_msg, ":" );
            if ( l_split.length < 3 )
                return;
            m_logs.add(
                    CCommon.getResourceString(
                            this, "message", StringUtils.split( l_split[1], "]" )[0], StringUtils.join(
                                    l_split, " ", 2, l_split.length
                            )
                    )
            );
        }

    }
}
