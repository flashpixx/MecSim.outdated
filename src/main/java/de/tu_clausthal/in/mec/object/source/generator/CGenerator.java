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

package de.tu_clausthal.in.mec.object.source.generator;

import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.source.factory.IFactory;
import de.tu_clausthal.in.mec.object.source.generator.CTimeUniformDistribution;
import de.tu_clausthal.in.mec.object.source.generator.IGeneratorDistribution;

import java.util.Collection;
import java.util.HashSet;


/**
 * class which is responsible for generating cars in different ways
 */
public class CGenerator
{
    /**
     * member variable to create different types of car
     */
    private IFactory m_factory;
    /**
     * member variable to indicate the amount of cars which should be created
     */
    private IGeneratorDistribution m_settings = new CTimeUniformDistribution(5, 0, 10);
    /**
     * member variable to restrict the steps where cars are generated
     */
    private static int m_restriction = 15;


    /**
     * ctor - default no factory will be set
     */
    public CGenerator()
    {

    }

    /**
     * method to get the factory
     *
     * @return current factory
     */
    public IFactory getFactory()
    {
        return this.m_factory;
    }

    /**
     * method to set a new factory
     * @param p_factory new factory
     */
    public void setFactory(final IFactory p_factory)
    {
        this.m_factory = p_factory;
    }

    /**
     * method to remove the factory
     */
    public void removeFactory()
    {
        this.m_factory = null;
    }

    /**
     * method to generate cars
     * @param p_currentStep actual step
     * @return collection of cars which were generated
     */
    public Collection<ICar> generate( final int p_currentStep)
    {
        final Collection<ICar> l_sources = new HashSet<>();

        if ( p_currentStep % m_restriction == 0 && m_factory!=null)
            for ( int i = 0; i < m_settings.getCount(p_currentStep); i++ )
                continue;
                //l_sources.add( this.m_factory.createCar() );

        return l_sources;
    }

}
