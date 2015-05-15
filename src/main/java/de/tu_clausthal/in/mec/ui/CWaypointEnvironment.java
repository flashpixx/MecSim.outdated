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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ui bundle which is responsible for the waypoint-tool settings
 * todo java cleanup (doc, style)
 * todo remove singelton
 * todo check if input is correct
 * todo read default tool settings from config
 * todo check for casts
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
     * ctor which create a default tool
     */
    private CWaypointEnvironment()
    {
        CTool l_defaultTool = new CTool(
                EWayPointType.CarWaypointRandom, EFactoryType.DefaultCarFactory, EGeneratorType.Uniform, 0.75, Color.RED, null, 1, 1, 1, 0, 10,
                new int[]{1, 2, 3, 4, 5}
        );
        this.m_selectedTool = l_defaultTool;
        this.m_toolbox.put( CCommon.getResourceString( this, "defaulttoolname" ), l_defaultTool );
    }

    /**
     * getter
     *
     * @return
     */
    public final static CWaypointEnvironment getInstance()
    {
        return s_instance;
    }

    /**
     * method to create a new tool
     *
     * @param p_data
     */
    private final Map<String, Map<String, Integer>> web_static_createtool( final Map<String, Object> p_data )
    {
        //validate json
        if ( !p_data.containsKey( "factory" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidfactory" ) );
        if ( !p_data.containsKey( "asl" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidasl" ) );
        if ( !p_data.containsKey( "generator" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgenerator" ) );
        if ( !p_data.containsKey( "input1" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgeneratorinput" ) );
        if ( !p_data.containsKey( "input2" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgeneratorinput" ) );
        if ( !p_data.containsKey( "input3" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgeneratorinput" ) );
        if ( !p_data.containsKey( "name" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidname" ) );
        if ( !p_data.containsKey( "r" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidredvalue" ) );
        if ( !p_data.containsKey( "g" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidgreenvalue" ) );
        if ( !p_data.containsKey( "b" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidbluevalue" ) );

        //read data
        EWayPointType l_waypointtype = this.getWaypointEnum( "" );
        EFactoryType l_factorytype = this.getFactoryEnum( (String) p_data.get( "factory" ) );
        EGeneratorType l_generatortype = this.getGeneratorEnum( (String) p_data.get( "generator" ) );

        int l_input1 = Integer.parseInt( String.valueOf( p_data.get( "input1" ) ) );
        int l_input2 = Integer.parseInt( String.valueOf( p_data.get( "input2" ) ) );
        int l_input3 = Integer.parseInt( String.valueOf( p_data.get( "input3" ) ) );
        int[] l_histrogramm = new int[]{1, 2, 3, 4, 5};

        double l_radius = 0.1;
        double r = Double.parseDouble( String.valueOf( p_data.get( "r" ) ) );
        double g = Double.parseDouble( String.valueOf( p_data.get( "g" ) ) );
        double b = Double.parseDouble( String.valueOf( p_data.get( "b" ) ) );

        Color l_color = new Color( (int) r, (int) g, (int) b );
        String l_asl = (String) p_data.get( "asl" );
        String l_name = (String) p_data.get( "name" );

        //validate settings
        if ( m_toolbox.containsKey( l_name ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidname" ) );

        //create and set tool
        CTool l_newTool = new CTool(
                l_waypointtype, l_factorytype, l_generatortype, l_radius, l_color, l_asl, l_input1, l_input2, l_input3, l_input2, l_input3, l_histrogramm
        );

        this.m_selectedTool = l_newTool;
        this.m_toolbox.put( l_name, l_newTool );

        //return data
        Map<String, Map<String, Integer>> l_tools = new HashMap<>();

        Map<String, Integer> l_properties = new HashMap<>();
        l_properties.put( "redValue", l_newTool.m_color.getRed() );
        l_properties.put( "greenValue", l_newTool.m_color.getGreen() );
        l_properties.put( "blueValue", l_newTool.m_color.getBlue() );

        l_tools.put( l_name, l_properties );

        return l_tools;
    }

    /**
     * method to get tje WayPoint type by name-string
     *
     * @param p_waypointtype
     * @return specified WayPoint type or default (CarRandomWaypoint)
     */
    private final EWayPointType getWaypointEnum( final String p_waypointtype )
    {
        for ( final EWayPointType l_waypoint : EWayPointType.values() )
        {
            if ( l_waypoint.toString().equals( p_waypointtype ) )
                return l_waypoint;
        }
        return EWayPointType.CarWaypointRandom;
    }

    /**
     * method to get the Factory type by name-string
     *
     * @param p_factory
     * @return specified Factory type or default (DefaultCarFactory)
     */
    private final EFactoryType getFactoryEnum( final String p_factory )
    {
        for ( final EFactoryType l_factory : EFactoryType.values() )
        {
            if ( l_factory.toString().equals( p_factory ) )
                return l_factory;
        }
        return EFactoryType.DefaultCarFactory;
    }

    /**
     * method to get the Generator type by name-string
     *
     * @param p_generator
     * @return specified Generator type or default (Uniform)
     */
    private final EGeneratorType getGeneratorEnum( final String p_generator )
    {
        for ( final EGeneratorType l_generator : EGeneratorType.values() )
        {
            if ( l_generator.toString().equals( p_generator ) )
                return l_generator;
        }
        return EGeneratorType.Uniform;
    }

    /**
     * method to set a new tool selected
     *
     * @param p_data
     */
    private final void web_static_settool( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "toolname" ) || !m_toolbox.containsKey( p_data.get( "toolname" ) ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidtool" ) );

        m_selectedTool = m_toolbox.get( p_data.get( "toolname" ) );
    }

    /**
     * list all possible waypoint types
     *
     * @return
     */
    private final Map<String, List<String>> web_static_listwaypointtypes()
    {

        List<String> l_waypointtypes = new ArrayList<>();
        for ( final EWayPointType l_waypoint : EWayPointType.values() )
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
    private final Map<String, List<ImmutablePair<String, Boolean>>> web_static_listfactories()
    {

        List<ImmutablePair<String, Boolean>> l_factories = new ArrayList<>();
        for ( final EFactoryType l_factory : EFactoryType.values() )
        {
            l_factories.add( new ImmutablePair<>( l_factory.toString(), l_factory.m_requireASL ) );
        }

        return new HashMap<String, List<ImmutablePair<String, Boolean>>>()
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
        for ( final EGeneratorType l_generator : EGeneratorType.values() )
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
    private final Map<String, Map<String, Integer>> web_static_listtools()
    {

        Map<String, Map<String, Integer>> l_tools = new HashMap<>();
        for ( final String l_tool : this.m_toolbox.keySet() )
        {
            Map<String, Integer> l_properties = new HashMap<>();
            l_properties.put( "redValue", this.m_toolbox.get( l_tool ).m_color.getRed() );
            l_properties.put( "greenValue", this.m_toolbox.get( l_tool ).m_color.getGreen() );
            l_properties.put( "blueValue", this.m_toolbox.get( l_tool ).m_color.getBlue() );

            l_tools.put( l_tool, l_properties );
        }

        return l_tools;
    }

    /**
     * enum for waypoint type
     */
    protected enum EWayPointType
    {
        CarWaypointRandom( CCommon.getResourceString( EWayPointType.class, "carwaypointrandom" ) ),
        CayWaypointPath( CCommon.getResourceString( EWayPointType.class, "carwaypointpath" ) );

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
        DefaultCarFactory( CCommon.getResourceString( EFactoryType.class, "defaultcarfactory" ), false ),
        DefaultAgentCarFactory( CCommon.getResourceString( EFactoryType.class, "defaultagentcarfactory" ), true );

        private final String m_text;
        private final Boolean m_requireASL;

        private EFactoryType( final String p_text, final Boolean p_requireASL )
        {
            this.m_text = p_text;
            this.m_requireASL = p_requireASL;
        }

        @Override
        public String toString()
        {
            return this.m_text;
        }
    }

    /**
     * enum for generator type
     */
    protected enum EGeneratorType
    {
        Uniform( CCommon.getResourceString( EGeneratorType.class, "uniformdistribution" ) ),
        Normal( CCommon.getResourceString( EGeneratorType.class, "normaldistribution" ) ),
        Exponential( CCommon.getResourceString( EGeneratorType.class, "exponentialdistribution" ) ),
        Profile( CCommon.getResourceString( EGeneratorType.class, "profiledistribution" ) );

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

    /**
     * class which is able to deliver a Waypoint threw configerable settings
     *
     * @deprecated change to immutable hashmap
     */
    @Deprecated
    protected class CTool
    {
        /**
         * type of waypoint (random/path)
         */
        protected final EWayPointType m_wayPointType;
        /**
         * factory type (which car should be produced)
         */
        protected final EFactoryType m_factoryType;
        /**
         * generator type defines the probability and amount of cars
         */
        protected final EGeneratorType m_generatorType;
        /**
         * radius for random target
         */
        protected final double m_radius;
        /**
         * color of the waypoint
         */
        protected final Color m_color;
        /**
         * asl programm for agent cars
         */
        protected final String m_asl;
        /**
         * amount of cars that should be generated und a special probability
         */
        protected final int m_carcount;
        /**
         * mean for the generator which defines the probability to generate cars
         */
        protected final double m_mean;
        /**
         * deviation for the generator which defines the probability to generate cars
         */
        protected final double m_deviation;
        /**
         * lower bound for the generator which defines the probability to generate cars
         */
        protected final double m_lower;
        /**
         * upper bound for the generator which defines the probability to generate cars
         */
        protected final double m_upper;
        /**
         * histrogramm for profile generators
         */
        protected final int[] m_histrogram;

        /**
         * @param p_wayPointType
         * @param p_factoryType
         * @param p_generatorType
         * @param p_radius
         * @param p_color
         * @param p_asl
         * @param p_carcount
         * @param p_mean
         * @param p_deviation
         * @param p_lower
         * @param p_upper
         * @param p_histrogram
         */
        protected CTool( final EWayPointType p_wayPointType, final EFactoryType p_factoryType, final EGeneratorType p_generatorType, final double p_radius,
                final Color p_color, final String p_asl, final int p_carcount, final int p_mean, final int p_deviation, final int p_lower, final int p_upper,
                final int[] p_histrogram
        )
        {
            this.m_wayPointType = p_wayPointType;
            this.m_factoryType = p_factoryType;
            this.m_generatorType = p_generatorType;
            this.m_radius = p_radius;
            this.m_color = p_color;
            this.m_asl = p_asl;
            this.m_carcount = p_carcount;
            this.m_mean = p_mean;
            this.m_deviation = p_deviation;
            this.m_lower = p_lower;
            this.m_upper = p_upper;
            this.m_histrogram = p_histrogram;
        }

        /**
         * method which returns a waypoint with settings from the ui
         *
         * @param p_position position of the waypoint
         * @return configured waypoint
         */
        protected final IWayPointBase getWaypoint( GeoPosition p_position )
        {

            return new CCarRandomWayPoint( p_position, this.getGenerator(), this.getFactory(), m_radius, m_color );
        }

        /**
         * method which returns a generator with settings from the ui
         *
         * @return configured generator
         */
        protected final IGenerator getGenerator()
        {

            switch ( m_generatorType )
            {
                case Uniform:
                    return new CTimeUniformDistribution( m_carcount, m_lower, m_upper );

                case Normal:
                    return new CTimeNormalDistribution( m_carcount, m_mean, m_deviation );

                case Exponential:
                    return new CTimeExponentialDistribution( m_carcount, m_mean, m_deviation );

                case Profile:
                    return new CTimeProfile( m_histrogram );

                default:
                    return new CTimeUniformDistribution( m_carcount, m_lower, m_upper );
            }
        }

        /**
         * method which returns a factory with settings from the ui
         *
         * @return configured factory
         */
        protected final ICarFactory getFactory()
        {

            switch ( m_factoryType )
            {
                case DefaultCarFactory:
                    return new CDistributionDefaultCarFactory(
                            new NormalDistribution( 50, 25 ), new NormalDistribution( 250, 50 ), new NormalDistribution( 20, 5 ), new NormalDistribution(
                            20, 5
                    ), new UniformRealDistribution()
                    );

                case DefaultAgentCarFactory:
                    return new CDistributionAgentCarFactory(
                            new NormalDistribution( 50, 25 ), new NormalDistribution( 250, 50 ), new NormalDistribution( 20, 5 ), new NormalDistribution(
                            20, 5
                    ), new UniformRealDistribution(), m_asl
                    );

                default:
                    return new CDistributionDefaultCarFactory(
                            new NormalDistribution( 50, 25 ), new NormalDistribution( 250, 50 ), new NormalDistribution( 20, 5 ), new NormalDistribution(
                            20, 5
                    ), new UniformRealDistribution()
                    );
            }
        }
    }

}
