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

package de.tu_clausthal.in.mec.object.waypoint.factory;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import jason.JasonException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * class to generate Jason cars with a distribution of the attributes
 */
public class CDistributionAgentCarFactory extends CDistributionDefaultCarFactory
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * inspect data
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CDistributionAgentCarFactory.super.inspect() );
        }};
    /**
     * name of the ASL file
     */
    private String m_aslname;

    /**
     * ctor
     *
     * @param p_speed distribution of speed
     * @param p_maxspeed distribution of max-speed
     * @param p_acceleration distribution of acceleration
     * @param p_deceleration distribution of deceleration
     * @param p_lingerdistribution distribution of linger-probability
     * @param p_asl agent name
     */
    public CDistributionAgentCarFactory( final AbstractRealDistribution p_speed, final AbstractRealDistribution p_maxspeed,
                                         final AbstractRealDistribution p_acceleration, final AbstractRealDistribution p_deceleration,
                                         final AbstractRealDistribution p_lingerdistribution, final String p_asl
    )
    {
        super( p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerdistribution );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );
        this.m_aslname = p_asl;

        m_inspect.put( CCommon.getResourceString( CDistributionAgentCarFactory.class, "factoryasl" ), m_aslname );
    }


    @Override
    protected ICar getCar( final ArrayList<Pair<EdgeIteratorState, Integer>> p_cells )
    {
        try
        {
            return new de.tu_clausthal.in.mec.object.car.CCarJasonAgent(
                    p_cells, (int) m_speed.sample(), (int) m_maxspeed.sample(), (int) m_acceleration.sample(), (int) m_deceleration.sample(),
                    m_lingerdistribution.sample(), m_aslname
            );
        }
        catch ( final JasonException l_exception )
        {
            CLogger.error( l_exception );
        }
        return null;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws java.io.IOException throws exception on reading
     * @throws ClassNotFoundException throws on deserialization
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        // read the ASL file from the stream
        final String l_aslname = m_aslname;
        final String l_asldata = (String) p_stream.readObject();
        File l_output = IEnvironment.getAgentFile( l_aslname );

        try
        {
            if ( !l_output.exists() )
                FileUtils.write( l_output, l_asldata );
            else if ( !CCommon.getHash( l_output, "MD5" ).equals( CCommon.getHash( l_asldata, "MD5" ) ) )
            {
                l_output = IEnvironment.getAgentFile( FilenameUtils.getBaseName( l_aslname ) + "_0" );
                for ( int i = 1; l_output.exists(); i++ )
                {
                    m_aslname = FilenameUtils.getBaseName( l_aslname ) + "_" + i;
                    l_output = IEnvironment.getAgentFile( m_aslname );
                }
                FileUtils.write( l_output, l_asldata );
            }
        }
        catch ( final Exception l_exception )
        {
        }
    }

    /**
     * write call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws the exception on loading data
     */
    private void writeObject( final ObjectOutputStream p_stream ) throws IOException
    {
        p_stream.defaultWriteObject();

        // write the ASL file to the stream
        p_stream.writeObject( new String( Files.readAllBytes( Paths.get( IEnvironment.getAgentFile( m_aslname ).toString() ) ) ) );
    }
}
