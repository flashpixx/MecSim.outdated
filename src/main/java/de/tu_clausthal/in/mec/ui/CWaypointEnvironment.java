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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ui bundle which is responsible for the waypoint-tool settings
 */
public class CWaypointEnvironment
{
    /**
     * singelton instance
     */
    private static final CWaypointEnvironment s_instance = new CWaypointEnvironment();

    /**
     * type of waypoint (random/path)
     */
    private EWayPointType m_wayPointType = EWayPointType.CARWAYPOINTRANDOM;

    /**
     * factory type (which car should be produced)
     */
    private EFactoryType m_factoryType = EFactoryType.DEFAULTCARFACTORY;

    /**
     * generator type defines the probability and amount of cars
     */
    private EGeneratorType m_generatorType = EGeneratorType.UNIFORM;

    /**
     * ctor
     */
    private CWaypointEnvironment()
    {
        super();
    }

    /**
     * getter
     * @return
     */
    public static CWaypointEnvironment getInstance()
    {
        return s_instance;
    }

    /**
     * list all possible waypoint types
     * @return
     */
    private final Map<String, List<String>> web_static_listwaypointtypes()
    {

        List<String> l_waypointtypes = new ArrayList<>();
        for ( EWayPointType l_waypoint : EWayPointType.values() )
        {
            l_waypointtypes.add( l_waypoint.toString() );
        }

        return new HashMap<String, List<String>>()
        {{
                put( "waypointtypes", l_waypointtypes );
            }};
    }

    /**
     * list all possible factory types
     * @return
     */
    private final Map<String, List<String>> web_static_listfactories()
    {

        List<String> l_factories = new ArrayList<>();
        for ( EFactoryType l_factory : EFactoryType.values() )
        {
            l_factories.add( l_factory.toString() );
        }

        return new HashMap<String, List<String>>()
        {{
                put( "factories", l_factories );
            }};
    }

    /**
     * list all possible generator types
     * @return
     */
    private final Map<String, List<String>> web_static_listgenerator()
    {

        List<String> l_generators = new ArrayList<>();
        for ( EGeneratorType l_generator : EGeneratorType.values() )
        {
            l_generators.add( l_generator.toString() );
        }

        return new HashMap<String, List<String>>()
        {{
                put( "generators", l_generators );
            }};
    }

}

enum EWayPointType
{
    CARWAYPOINTRANDOM( "carwaypointrandom" ),
    CARWAYPOINTPATH( "carwaypointpath" );

    private final String text;

    private EWayPointType( final String text )
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}

enum EFactoryType
{
    DEFAULTCARFACTORY( "defaultcarfactory" ),
    DEFAULTAGENTCARFACTORY(  "defaultagentcarfactory" );

    private final String text;

    private EFactoryType( final String text )
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}

enum EGeneratorType
{
    UNIFORM( "uniformdistribution" ),
    NORMAL( "normaldistribution" ),
    EXPONENTIAL( "exponentialdistribution" ),
    PROFILE( "profiledistribution" );

    private final String text;

    private EGeneratorType( final String text )
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}