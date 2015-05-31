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
 * todo validation
 * todo java cleanup (doc, style)
 * todo remove singelton
 * todo rename asl and input
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
     * list of all expected parameters for tool creation
     */
    protected final List<String> m_expectedParameter = new ArrayList<String>()
    {{
            add( "waypointtype" );
            add( "radius" );
            add( "factory" );
            add( "agentprogram" );
            add( "generator" );
            add( "carcount" );
            add( "generatorinput1" );
            add( "generatorinput2" );
            /**
            add( "speedprob" );
            add( "speedprobinput1" );
            add( "speedprobinput2" );
            add( "maxspeedprob" );
            add( "maxspeedprobinput1" );
            add( "maxspeedprobinput2" );
            add( "accprob" );
            add( "accprobinput1" );
            add( "accprobinput2" );
            add( "decprob" );
            add( "decprobinput1" );
            add( "decprobinput2" );
            add( "decprob" );
            add( "lingerprob" );
            add( "lingerprobinput1" );
            add( "lingerprobinput2" );
            **/
            add( "name" );
            add( "red" );
            add( "green" );
            add( "blue" );
        }};
    protected final Map<String, Object> m_defaultProperties = new HashMap<String, Object>()
    {{
            put("waypointtype", EWayPointType.CarWaypointRandom.m_name);
            put("radius", 0.75);
            put("factory", EFactoryType.DefaultCarFactory.m_name);
            put("agentprogram", "");
            put("generator", EDistributionType.Uniform.m_name);
            put("carcount", 1);
            put("generatorinput1", 1);
            put("generatorinput2", 3);
            put("name", CCommon.getResourceString( CWaypointEnvironment.class, "defaulttoolname" ));
            put("red", 255);
            put("green", 0);
            put("blue", 0);
    }};

    /**
     * ctor which create a default tool
     */
    private CWaypointEnvironment()
    {
        this.web_static_createtool( m_defaultProperties );
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

            l_tools.put(l_tool, l_properties);
        }

        return l_tools;
    }

    /**
     * method to create a new tool
     *
     * @param p_data
     */
    private final Map<String, Map<String, Integer>> web_static_createtool( final Map<String, Object> p_data )
    {
        for(String l_parameter : m_expectedParameter){

            //check for expected parameters
            if( !p_data.containsKey( l_parameter )){
                throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidtoolconfiguration" ) );
            }

            //check if tool already exist
            if(m_toolbox.containsKey( p_data.get( "name" ) )){
                throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidname" ) );
            }
        }

        //create tool
        CTool l_newTool = new CTool( p_data );
        this.m_selectedTool = l_newTool;
        this.m_toolbox.put( l_newTool.m_name, l_newTool );

        //return data
        Map<String, Map<String, Integer>> l_tools = new HashMap<>();
        Map<String, Integer> l_properties = new HashMap<>();
        l_properties.put( "redValue", l_newTool.m_color.getRed() );
        l_properties.put( "greenValue", l_newTool.m_color.getGreen() );
        l_properties.put( "blueValue", l_newTool.m_color.getBlue() );
        l_tools.put( l_newTool.m_name, l_properties );

        return l_tools;
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
            l_waypointtypes.add( l_waypoint.m_name );
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
            l_factories.add(new ImmutablePair<>(l_factory.m_name, l_factory.m_requireASL));
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
    private final Map<String, List<String>> web_static_listdistribution()
    {

        List<String> l_generators = new ArrayList<>();
        for ( final EDistributionType l_generator : EDistributionType.values() )
        {
            l_generators.add( l_generator.m_name );
        }

        return new HashMap<String, List<String>>()
        {{
                put( "generators", l_generators );
            }};
    }

    /**
     * enum for waypoint types
     */
    protected enum EWayPointType
    {
        CarWaypointRandom( CCommon.getResourceString( EWayPointType.class, "carwaypointrandom" ) ),
        CayWaypointPath( CCommon.getResourceString( EWayPointType.class, "carwaypointpath" ) );

        /**
         * name of this waypoint type
         */
        private final String m_name;

        /**
         * ctor
         *
         * @param p_name
         */
        private EWayPointType( final String p_name )
        {
            this.m_name = p_name;
        }

        /**
         * method to get waypoint type by name
         *
         * @param p_name
         * @return
         */
        private static final EWayPointType getWaypointTypeByName( final String p_name )
        {
            for ( final EWayPointType l_waypoint : EWayPointType.values() )
            {
                if ( l_waypoint.m_name.equals( p_name ) )
                    return l_waypoint;
            }
            return EWayPointType.CarWaypointRandom;
        }
    }

    /**
     * enum for factory type
     */
    protected enum EFactoryType
    {
        DefaultCarFactory( CCommon.getResourceString( EFactoryType.class, "defaultcarfactory" ), false ),
        DefaultAgentCarFactory( CCommon.getResourceString( EFactoryType.class, "defaultagentcarfactory" ), true );

        /**
         * name of this factory type
         */
        private final String m_name;

        /**
         * variable indicate if this factory type require an agent program
         */
        private final Boolean m_requireASL;

        /**
         * ctor
         * @param p_name
         * @param p_requireASL
         */
        private EFactoryType( final String p_name, final Boolean p_requireASL )
        {
            this.m_name = p_name;
            this.m_requireASL = p_requireASL;
        }

        /**
         * method to get factory type by name
         * @param p_name
         * @return
         */
        protected static final EFactoryType getFactoryTypeByName( final String p_name )
        {
            for ( final EFactoryType l_factory : EFactoryType.values() )
            {
                if ( l_factory.m_name.equals( p_name ) )
                    return l_factory;
            }
            return EFactoryType.DefaultCarFactory;
        }
    }

    /**
     * enum for generator type
     */
    protected enum EDistributionType
    {
        Uniform( CCommon.getResourceString( EDistributionType.class, "uniformdistribution" ) ),
        Normal( CCommon.getResourceString( EDistributionType.class, "normaldistribution" ) ),
        Exponential( CCommon.getResourceString( EDistributionType.class, "exponentialdistribution" ) ),
        Profile( CCommon.getResourceString( EDistributionType.class, "profiledistribution" ) );

        /**
         * name of this distribution type
         */
        private final String m_name;

        /**
         * ctor
         * @param p_name
         */
        private EDistributionType( final String p_name )
        {
            this.m_name = p_name;
        }

        /**
         * method to get distribution type by name
         * @param p_name
         * @return
         */
        private static final EDistributionType getDistributionTypeByName( final String p_name )
        {
            for ( final EDistributionType l_generator : EDistributionType.values() )
            {
                if ( l_generator.m_name.equals( p_name ) )
                    return l_generator;
            }
            return EDistributionType.Uniform;
        }
    }

    /**
     * class which is able to deliver a waypoint threw configerable settings
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
         * radius for random target
         */
        protected final double m_radius;
        /**
         * factory type (which car should be produced)
         */
        protected final EFactoryType m_factoryType;
        /**
         * asl program for agent cars
         */
        protected final String m_asl;
        /**
         * generator type defines the probability and amount of cars
         */
        protected final EDistributionType m_generatorType;
        /**
         * amount of cars that should be generated under a special probability
         */
        protected final int m_carcount;
        /**
         * generatorInput1 (lower bound or mean)
         */
        protected final double m_generatorInput1;
        /**
         * generatorInput2 (upper bound or deviation)
         */
        protected final double m_generatorInput2;
        /**
         * histrogram for profile generators
         */
        protected final int[] m_histrogram;
        /**
         * name of the tool
         */
        protected final String m_name;
        /**
         * color of the waypoint
         */
        protected final Color m_color;


        /**
         * ctor
         * @param p_parameter
         */
        protected CTool( final Map<String, Object> p_parameter){

            int l_redValue = (int) Double.parseDouble( String.valueOf( p_parameter.get( "red" ) ) );
            int l_greenValue = (int) Double.parseDouble( String.valueOf( p_parameter.get( "green" ) ) );
            int l_blueValue = (int) Double.parseDouble( String.valueOf( p_parameter.get( "blue" ) ) );

            this.m_wayPointType = EWayPointType.getWaypointTypeByName( "" );;
            this.m_radius = Double.parseDouble( String.valueOf( p_parameter.get( "radius" ) ) );
            this.m_factoryType = EFactoryType.getFactoryTypeByName( (String) p_parameter.get( "factory" ) );;
            this.m_asl = String.valueOf( p_parameter.get( "agentprogram" ) );
            this.m_generatorType = EDistributionType.getDistributionTypeByName( (String) p_parameter.get( "generator" ) );;
            this.m_carcount = Integer.parseInt( String.valueOf( p_parameter.get( "carcount" ) ) );
            this.m_generatorInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "generatorinput1" ) ) );
            this.m_generatorInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "generatorinput2" ) ) );
            this.m_histrogram = new int[]{};
            this.m_color = new Color(l_redValue, l_greenValue, l_blueValue);
            this.m_name = (String) p_parameter.get( "name" );
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
                    return new CTimeUniformDistribution( m_carcount, m_generatorInput1, m_generatorInput2 );

                case Normal:
                    return new CTimeNormalDistribution( m_carcount, m_generatorInput1, m_generatorInput2 );

                case Exponential:
                    return new CTimeExponentialDistribution( m_carcount, m_generatorInput1, m_generatorInput2 );

                case Profile:
                    return new CTimeProfile( m_histrogram );

                default:
                    return new CTimeUniformDistribution( m_carcount, m_generatorInput1, m_generatorInput2 );
            }
        }
    }
}
