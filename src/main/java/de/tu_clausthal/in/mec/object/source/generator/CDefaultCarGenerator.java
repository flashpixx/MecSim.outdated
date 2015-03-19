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

import de.tu_clausthal.in.mec.object.car.CDefaultCar;
import de.tu_clausthal.in.mec.object.car.ICar;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;

/**
 * Class to generate a Default Car
 */
public class CDefaultCarGenerator implements IGenerator
{

    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Position of this Generator
     */
    protected transient GeoPosition m_position = null;
    /**
     * Member Variable which handles the Settings of a Generator
     */
    protected CGeneratorSettings m_settings = new CGeneratorSettings();
    /**
     * Generating Cars in every Step is to much so here is a restriction variable
     */
    protected final int m_restriction = 15;


    /**
     * CTOR
     * @param p_position
     */
    public CDefaultCarGenerator( GeoPosition p_position )
    {
        this.m_position = p_position;
    }

    @Override
    public Color getColor()
    {
        return Color.CYAN;
    }

    @Override
    public Collection<ICar> generate(int p_currentStep)
    {
        final Collection<ICar> l_sources = new HashSet<>();

        if(p_currentStep % m_restriction == 0) {
            int l_numberOfCars = m_settings.getSample();

            for (int i = 0; i < l_numberOfCars; i++)
                l_sources.add(new CDefaultCar(m_position));
            }

        return l_sources;
    }

    @Override
    public CGeneratorSettings getSettings() {
        return m_settings;
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws java.io.IOException    throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        m_position = new GeoPosition( p_stream.readDouble(), p_stream.readDouble() );
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

        p_stream.writeDouble( m_position.getLatitude() );
        p_stream.writeDouble( m_position.getLongitude() );
    }

}
