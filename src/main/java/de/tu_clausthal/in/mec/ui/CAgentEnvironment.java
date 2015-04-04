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

package de.tu_clausthal.in.mec.ui;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * encapsulating class for multi-agent UI components
 */
public class CAgentEnvironment
{

    /** type of the MAS name * */
    private final EType m_type;
    /** base URI * */
    private final String m_baseuri;

    /**
     * ctor
     *
     * @param p_type MAS type
     */
    public CAgentEnvironment( final EType p_type )
    {
        m_type = p_type;
        switch ( m_type )
        {
            case Jason:
                m_baseuri = "jason";
                break;
            default:
                m_baseuri = null;
        }
    }

    /**
     * UI method - returns URI base
     *
     * @return base name
     */
    private String web_uribase()
    {
        return m_baseuri;
    }

    /**
     * creates a new agent
     *
     * @param p_data input data
     */
    private void web_static_create( final Map<String, Object> p_data ) throws IOException
    {
        switch ( m_type )
        {
            case Jason:
                IEnvironment.createAgentFile( this.getAgentName( p_data ) );
                break;
            default:
        }
    }

    /**
     * UI method - delete an agent
     *
     * @param p_data input data
     */
    private void web_static_delete( final Map<String, Object> p_data )
    {
        switch ( m_type )
        {
            case Jason:
                FileUtils.deleteQuietly( IEnvironment.getAgentFile( this.getAgentName( p_data ) ) );
                break;

            default:
        }
    }

    /**
     * UI method - reads the file content of an agent
     *
     * @param p_data input data
     * @return file content
     */
    private Map<String, String> web_static_read( final Map<String, Object> p_data ) throws IOException
    {
        switch ( m_type )
        {
            case Jason:
                final String l_agent = this.getAgentName( p_data );
                return new HashMap<String, String>()
                {{
                        put( "name", l_agent );
                        put( "source", FileUtils.readFileToString( IEnvironment.getAgentFile( l_agent ) ) );
                    }};

            default:
        }

        return null;
    }

    /**
     * UI method - writes the content to the agent file
     *
     * @param p_data input data
     */
    private void web_static_write( final Map<String, Object> p_data ) throws IOException
    {
        if ( !p_data.containsKey( "source" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "noagentdata" ) );

        switch ( m_type )
        {
            case Jason:
                FileUtils.writeStringToFile( IEnvironment.getAgentFile( this.getAgentName( p_data ) ), (String) p_data.get( "data" ) );
                break;

            default:
        }
    }

    /**
     * UI method - returns a list of all agents
     *
     * @return agent list
     */
    private Map<String, List<String>> web_static_list()
    {
        switch ( m_type )
        {
            case Jason:
                return new HashMap<String, List<String>>()
                {{ put( "agents", Arrays.asList( IEnvironment.getAgentFiles() ) ); }};

            default:
        }

        return null;
    }

    /**
     * UI method - checks the syntax of an agent
     *
     * @param p_data input data
     */
    private void web_static_check( final Map<String, Object> p_data )
    {
        switch ( m_type )
        {
            case Jason:
                IEnvironment.checkAgentFileSyntax( this.getAgentName( p_data ) );
                break;

            default:
        }
    }

    /**
     * UI method - gets the agent name from the map
     *
     * @param p_data input data
     * @return agent name
     */
    private String getAgentName( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "name" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "noagentname" ) );

        return (String) p_data.get( "name" );
    }


    /** MAS types * */
    public enum EType
    {
        Jason
    }

}
