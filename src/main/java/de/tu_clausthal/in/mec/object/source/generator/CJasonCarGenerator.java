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

package de.tu_clausthal.in.mec.object.source.generator;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgent;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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


/**
 * Class to Generate Jason Cars
 */
public class CJasonCarGenerator extends CDefaultCarGenerator
{
    /**
     * Name of the ASL File
     */
    private String m_aslName = null;


    /**
     * CTOR
     */
    public CJasonCarGenerator( final GeoPosition p_position, final String p_aslName )
    {
        super( p_position );

        if ( ( p_aslName == null ) || ( p_aslName.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );

        this.m_aslName = p_aslName;
    }

    @Override
    public final Color getColor()
    {
        return Color.RED;
    }

    @Override
    public final Collection<ICar> generate(int p_currentStep)
    {
        final Collection<ICar> l_sources = new HashSet<>();

        if(p_currentStep % m_restriction == 0){
            int l_numberOfCars = m_settings .getSample();

            for ( int i = 0; i < l_numberOfCars; i++ )
                l_sources.add( new CCarJasonAgent( m_aslName, m_position ) );
        }

        return l_sources;
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws java.io.IOException    throws exception on reading
     * @throws ClassNotFoundException throws on deserialization
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        // read the ASL file from the stream
        final String l_aslname = m_aslName;
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
                    m_aslName = FilenameUtils.getBaseName( l_aslname ) + "_" + i;
                    l_output = IEnvironment.getAgentFile( m_aslName );
                }
                FileUtils.write( l_output, l_asldata );
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
        p_stream.writeObject( new String( Files.readAllBytes( Paths.get( IEnvironment.getAgentFile( m_aslName ).toString() ) ) ) );
    }

}
