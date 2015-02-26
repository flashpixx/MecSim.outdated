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

package de.tu_clausthal.in.mec.object.source;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgent;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import org.apache.commons.io.FileUtils;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


/**
 * source to create a Jason car agent
 */
public class CJasonAgentSourceFactory extends CDefaultSourceFactory
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * ASL file *
     */
    protected String m_asl = null;


    /**
     * ctor
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     * @param p_mean     mean value
     * @param p_distmean distribution mean
     */
    public CJasonAgentSourceFactory( final String p_asl, final GeoPosition p_position, final double p_mean, final double p_distmean )
    {
        super( p_position, Color.red, p_mean, p_distmean );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );
        m_asl = p_asl;
    }


    /**
     * ctor
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     * @param p_mean     mean value
     * @param p_distmean distribution mean
     * @param p_number   creating car number
     */
    public CJasonAgentSourceFactory( final String p_asl, final GeoPosition p_position, final double p_mean, final double p_distmean, final int p_number )
    {
        super( p_position, Color.red, p_mean, p_distmean, p_number );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );
        m_asl = p_asl;
    }


    /**
     * ctor
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     * @param p_number   creating car number
     */
    public CJasonAgentSourceFactory( final String p_asl, final GeoPosition p_position, final int p_number )
    {
        super( p_position, Color.red, p_number );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );
        m_asl = p_asl;
    }

    /**
     * ctor that sets the geo position of the source
     *
     * @param p_asl      ASL / agent name
     * @param p_position geo position object
     */
    public CJasonAgentSourceFactory( final String p_asl, final GeoPosition p_position )
    {
        super( p_position, Color.red );

        if ( ( p_asl == null ) || ( p_asl.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );
        m_asl = p_asl;
    }


    @Override
    public final Collection<ICar> step( final int p_currentstep, final ILayer p_layer )
    {
        final Collection<ICar> l_sources = new HashSet<>();
        if ( m_random.sample() < m_mean )
            return l_sources;

        for ( int i = 0; i < m_NumberCarsInStep; i++ )
            l_sources.add( new CCarJasonAgent( m_asl, m_position ) );

        return l_sources;
    }


    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }


    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws java.io.IOException    throws exception on reading
     * @throws ClassNotFoundException throws on deserialization
     * @todo on restore MAS agent content existing file should overwrite, if the hash of the file and stored content are
     * equal overwrite the file, otherwise rename existing file and create a new one with the store content
     */
    private final void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        // read the ASL file from the stream
        m_asl = (String) p_stream.readObject();
        final String l_data = (String) p_stream.readObject();
        final File l_output = IEnvironment.getAgentFile( m_asl );

        try
        {

            // check hashs are unequal
            if ( !CCommon.getHash( l_output, "MD5" ).equals( CCommon.getHash( l_data, "MD5" ) ) )
            {
                if ( l_output.exists() )
                {

                }

                FileUtils.write( l_output, l_data );
            }

        }
        catch ( Exception l_exception )
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
        p_stream.writeObject( m_asl );
        p_stream.writeObject( new String( Files.readAllBytes( Paths.get( IEnvironment.getAgentFile( m_asl ).toString() ) ) ) );
    }

}
