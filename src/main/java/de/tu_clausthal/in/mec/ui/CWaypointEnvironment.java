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
import de.tu_clausthal.in.mec.object.waypoint.factory.CDistributionAgentCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.CDistributionDefaultCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeExponentialDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeNormalDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeProfile;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeUniformDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarRandomWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPointBase;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
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
     * selected tool for the waypoint layer
     */
    protected static CTool m_selectedTool;
    /**
     * toolbox - to save different tool settings
     */
    protected final Map<String, CTool> m_toolbox = new HashMap<>();


    /**
     * ctor
     * todo multilanguage default toolname
     */
    private CWaypointEnvironment()
    {
        CTool l_defaultTool = new CTool();
        this.m_selectedTool = l_defaultTool;
        this.m_toolbox.put( CCommon.getResourceString( this, "defaulttoolname" ), l_defaultTool );
    }

    /**
     * getter
     *
     * @return
     */
    public static CWaypointEnvironment getInstance()
    {
        return s_instance;
    }

    /**
     * list all possible waypoint types
     *
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
     *
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
     *
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

    /**
     * list all possible tools
     *
     * @return
     */
    private final Map<String, List<String>> web_static_listtools()
    {

        List<String> l_tools = new ArrayList<>();

        for ( String l_tool : this.m_toolbox.keySet() )
        {
            l_tools.add( l_tool );
        }

        return new HashMap<String, List<String>>()
        {{
                put( "tools", l_tools );
            }};
    }

    /**
     * method to read waypoint specific labels and resource stirngs
     *
     * @return
     */
    private final Map<String, String> web_static_getlabels()
    {
        HashMap<String, String> l_labels = new HashMap<>();
        l_labels.put( "carcount", CCommon.getResourceString( this, "carcount" ) );
        l_labels.put( "mean", CCommon.getResourceString( this, "mean" ) );
        l_labels.put( "deviation", CCommon.getResourceString( this, "deviation" ) );
        l_labels.put( "lowerbound", CCommon.getResourceString( this, "lowerbound" ) );
        l_labels.put( "upperbound", CCommon.getResourceString( this, "upperbound" ) );
        return l_labels;
    }

    /**
     * class which is able to deliver a Waypoint threw configerable settings
     * todo read default tool settings from config
     * todo check if a tool can be better strucutred into subclasses (maybe for changes on the tool it might be good idea to have on single tool class)
     */
    protected class CTool
    {
        /**
         * type of waypoint (random/path)
         */
        protected EWayPointType m_wayPointType = EWayPointType.CARWAYPOINTRANDOM;
        /**
         * factory type (which car should be produced)
         */
        protected EFactoryType m_factoryType = EFactoryType.DEFAULTCARFACTORY;
        /**
         * generator type defines the probability and amount of cars
         */
        protected EGeneratorType m_generatorType = EGeneratorType.UNIFORM;
        /**
         * radius for random target
         */
        protected double m_radius = 0.1;
        /**
         * color of the waypoint
         */
        protected Color m_color = Color.RED;
        /**
         * asl programm for agent cars
         */
        protected String m_asl = null;
        /**
         * amount of cars that should be generated und a special probability
         */
        protected int m_carcount = 1;
        /**
         * mean for the generator which defines the probability to generate cars
         */
        protected double m_mean = 1;
        /**
         * deviation for the generator which defines the probability to generate cars
         */
        protected double m_deviation = 1;
        /**
         * lower bound for the generator which defines the probability to generate cars
         */
        protected double m_lower = 1;
        /**
         * upper bound for the generator which defines the probability to generate cars
         */
        protected double m_upper = 10;
        /**
         * histrogramm for profile generators
         */
        protected int[] m_histrogram = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        /**
         * method which returns a waypoint with settings from the ui
         *
         * @param p_position position of the waypoint
         * @return configured waypoint
         */
        public final IWayPointBase getWaypoint( GeoPosition p_position )
        {
            //todo implement switch to distinguish between random and path waypoints
            //todo maybe empty waypoint is a goo default waypoint ?

            return new CCarRandomWayPoint( p_position, this.getGenerator(), this.getFactory(), m_radius, m_color );
        }

        /**
         * method which returns a factory with settings from the ui
         *
         * @return configured factory
         */
        public final ICarFactory getFactory()
        {

            //todo replace default-dummy parameters threw parameters which are configured by the ui
            //todo better way to store factory distribution settings
            //todo check for right asl
            switch ( m_factoryType )
            {
                case DEFAULTCARFACTORY:
                    return new CDistributionDefaultCarFactory(
                            new NormalDistribution( 50, 25 ), new NormalDistribution( 250, 50 ), new NormalDistribution( 20, 5 ), new NormalDistribution(
                            20, 5
                    ), new NormalDistribution()
                    );

                case DEFAULTAGENTCARFACTORY:
                    return new CDistributionAgentCarFactory(
                            new NormalDistribution( 50, 25 ), new NormalDistribution( 250, 50 ), new NormalDistribution( 20, 5 ), new NormalDistribution(
                            20, 5
                    ), new NormalDistribution(), m_asl
                    );

                default:
                    return new CDistributionDefaultCarFactory(
                            new NormalDistribution( 50, 25 ), new NormalDistribution( 250, 50 ), new NormalDistribution( 20, 5 ), new NormalDistribution(
                            20, 5
                    ), new NormalDistribution()
                    );
            }
        }

        /**
         * method which returns a generator with settings from the ui
         *
         * @return configured generator
         */
        public final IGenerator getGenerator()
        {

            switch ( m_generatorType )
            {
                case UNIFORM:
                    return new CTimeUniformDistribution( m_carcount, m_lower, m_upper );

                case NORMAL:
                    return new CTimeNormalDistribution( m_carcount, m_mean, m_deviation );

                case EXPONENTIAL:
                    return new CTimeExponentialDistribution( m_carcount, m_mean, m_deviation );

                case PROFILE:
                    return new CTimeProfile( m_histrogram );

                default:
                    return new CTimeUniformDistribution( m_carcount, m_lower, m_upper );
            }
        }
    }

    /**
     * enum for waypoint type
     */
    protected enum EWayPointType
    {
        CARWAYPOINTRANDOM( CCommon.getResourceString( EWayPointType.class, "carwaypointrandom" ) ),
        CARWAYPOINTPATH( CCommon.getResourceString( EWayPointType.class, "carwaypointpath" ) );

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

    /**
     * enum for factory type
     */
    protected enum EFactoryType
    {
        DEFAULTCARFACTORY( CCommon.getResourceString( EFactoryType.class, "defaultcarfactory" ) ),
        DEFAULTAGENTCARFACTORY( CCommon.getResourceString( EFactoryType.class, "defaultagentcarfactory" ) );

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

    /**
     * enum for generator type
     */
    protected enum EGeneratorType
    {
        UNIFORM( CCommon.getResourceString( EGeneratorType.class, "uniformdistribution" ) ),
        NORMAL( CCommon.getResourceString( EGeneratorType.class, "normaldistribution" ) ),
        EXPONENTIAL( CCommon.getResourceString( EGeneratorType.class, "exponentialdistribution" ) ),
        PROFILE( CCommon.getResourceString( EGeneratorType.class, "profiledistribution" ) );

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

}