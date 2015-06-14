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
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.waypoint.CCarWayPointLayer;
import de.tu_clausthal.in.mec.object.waypoint.factory.CDistributionAgentCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.CDistributionDefaultCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeExponentialDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeNormalDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeProfile;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeUniformDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarPathWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarRandomWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IPathWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPointBase;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
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
 * todo validation (carsettings)
 * todo java cleanup (doc, style)
 * todo remove singelton
 * todo check for casts
 * todo better way to get makrov chain ?
 */
public class CWaypointEnvironment
{
    /**
     * singelton instance
     */
    private static final CWaypointEnvironment s_instance = new CWaypointEnvironment();
    /**
     * waypoint layer to edit makrov chain
     */
    private static final CCarWayPointLayer m_waypointLayer = CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped( "Car WayPoints" );
    /**
     * selected tool for the waypoint layer
     */
    protected static CTool m_selectedTool;
    /**
     * toolbox - to save different tool settings
     */
    protected final Map<String, CTool> m_toolbox = new HashMap<>();
    /**
     * default properties (which also defines the the expected parameters)
     */
    protected final Map<String, Object> m_defaultProperties = new HashMap<String, Object>()
    {{
            put( "waypointtype", EWayPointType.CarWaypointRandom.m_name );
            put( "radius", 0.75 );
            put( "factory", EFactoryType.DefaultCarFactory.m_name );
            put( "agentprogram", "" );
            put( "generator", EDistributionType.Normal.m_name );
            put( "carcount", 1 );
            put( "generatorinput1", 5 );
            put( "generatorinput2", 1 );
            put( "speedprob", EDistributionType.Normal.m_name );
            put( "speedprobinput1", 50 );
            put( "speedprobinput2", 25 );
            put( "maxspeedprob", EDistributionType.Normal.m_name );
            put( "maxspeedprobinput1", 250 );
            put( "maxspeedprobinput2", 50 );
            put( "accprob", EDistributionType.Normal.m_name );
            put( "accprobinput1", 20 );
            put( "accprobinput2", 5 );
            put( "decprob", EDistributionType.Normal.m_name );
            put( "decprobinput1", 20 );
            put( "decprobinput2", 5 );
            put( "lingerprobinput1", 0 );
            put( "lingerprobinput2", 1 );
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "defaulttoolname" ) );
            put( "red", 255 );
            put( "green", 0 );
            put( "blue", 0 );
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
     * get waypoint by reference
     * @param p_reference
     * @return
     */
    public final static IWayPoint getWaypointByReference( final String p_reference)
    {
        for ( final IWayPoint<ICar> l_item : m_waypointLayer ){
            if ( l_item.toString().equals( p_reference ) ){
                return l_item;
            }
        }

        return null;
    }

    /**
     * method to get a list of all waypoints an properties
     *
     * @return
     */
    private final List<Map<String, Object>> web_static_listwaypoints()
    {
        CCarWayPointLayer l_waypointLayer = CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped( "Car WayPoints" );

        List<Map<String, Object>> l_waypointList = new ArrayList<>();

        for ( IWayPoint l_wayPoint : l_waypointLayer )
        {
            HashMap<String, Object> l_properties = new HashMap<>();

            Color l_color = l_wayPoint.getColor();
            l_properties.put( "redValue", l_color.getRed() );
            l_properties.put( "greenValue", l_color.getGreen() );
            l_properties.put( "blueValue", l_color.getBlue() );
            l_properties.put( "id", l_wayPoint.toString() );
            l_properties.put( "name", l_wayPoint.getName() );
            l_properties.put( "lat", l_wayPoint.getPosition().getLatitude() );
            l_properties.put( "long", l_wayPoint.getPosition().getLongitude() );

            if ( l_wayPoint instanceof CCarRandomWayPoint )
            {
                l_properties.put( "type", CCommon.getResourceString( this, "waypointrandomtype" ) );
                l_properties.put( "editable", false );
            }
            else
            {
                l_properties.put( "type", CCommon.getResourceString( this, "waypointtargettype" ) );
                l_properties.put( "editable", true );
            }

            l_waypointList.add( l_properties );
        }

        return l_waypointList;
    }

    /**
     * @param p_data
     * @return
     */
    private final List<Triple<String, String, List<Pair<String, Double>>>> web_static_getmakrovchain( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "waypoint" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidwaypoint" ) );


        final List<Triple<String, String, List<Pair<String, Double>>>> l_return = new ArrayList<>();
        IWayPoint l_waypoint = getWaypointByReference( String.valueOf( p_data.get( "waypoint" ) ) );

        if(l_waypoint instanceof IPathWayPoint){
            IPathWayPoint.CMakrovChain<IWayPoint> l_makrovChain = ( (IPathWayPoint) l_waypoint ).getMakrovChain();

            //build makrov result
            for ( final Map.Entry<IWayPoint, Map<IWayPoint, MutablePair<Double, Double>>> l_node : l_makrovChain.entrySet() ) {
                List<Pair<String, Double>> l_edges = new ArrayList<>();
                for ( Map.Entry<IWayPoint, MutablePair<Double, Double>> l_edge : l_node.getValue().entrySet() )
                    l_edges.add( new MutablePair<String, Double>( l_edge.getKey().toString(), l_edge.getValue().getRight() ) );

                l_return.add( new MutableTriple<>( l_node.getKey().toString(), l_node.getKey().getName(), l_edges ) );
            }
        }

        return l_return;
    }

    /**
     *
     * @param p_data
     */
    private final void web_static_addnode( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "waypoint" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidwaypoint" ) );
        if ( !p_data.containsKey( "node" ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidwaypoint" ) );

        IWayPoint l_waypoint = getWaypointByReference( String.valueOf( p_data.get( "waypoint" ) ) );
        IWayPoint l_node = getWaypointByReference( String.valueOf( p_data.get( "node" ) ) );

        if(l_waypoint instanceof IPathWayPoint && l_waypoint != null && l_node != null){
            IPathWayPoint.CMakrovChain<IWayPoint> l_makrovChain = ( (IPathWayPoint) l_waypoint ).getMakrovChain();
            l_makrovChain.addNode( l_node );
        }
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
    private final Map<String, Map<String, Object>> web_static_listtools()
    {

        Map<String, Map<String, Object>> l_tools = new HashMap<>();
        for ( Map.Entry<String, CTool> l_tool : m_toolbox.entrySet() )
        {
            Map<String, Object> l_properties = new HashMap<>();
            l_properties.put( "redValue", l_tool.getValue().m_color.getRed() );
            l_properties.put( "greenValue", l_tool.getValue().m_color.getGreen() );
            l_properties.put( "blueValue", l_tool.getValue().m_color.getBlue() );
            l_properties.put( "deleteable", l_tool.getValue().m_deleteable );

            l_tools.put( l_tool.getKey(), l_properties );
        }

        return l_tools;
    }

    /**
     * method to create a new tool
     *
     * @param p_data
     */
    private final Map<String, Map<String, Object>> web_static_createtool( final Map<String, Object> p_data )
    {
        for ( String l_parameter : m_defaultProperties.keySet() )
        {

            //check for expected parameters
            if ( !p_data.containsKey( l_parameter ) )
            {
                throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidtoolconfiguration" ) );
            }

            //check if tool already exist
            if ( m_toolbox.containsKey( p_data.get( "name" ) ) )
            {
                throw new IllegalArgumentException( CCommon.getResourceString( this, "novalidname" ) );
            }
        }

        //create tool
        CTool l_newTool = new CTool( p_data );
        this.m_selectedTool = l_newTool;
        this.m_toolbox.put( l_newTool.m_name, l_newTool );

        //return data
        Map<String, Map<String, Object>> l_tools = new HashMap<>();
        Map<String, Object> l_properties = new HashMap<>();
        l_properties.put( "redValue", l_newTool.m_color.getRed() );
        l_properties.put( "greenValue", l_newTool.m_color.getGreen() );
        l_properties.put( "blueValue", l_newTool.m_color.getBlue() );
        l_properties.put( "deleteable", l_newTool.m_deleteable );

        l_tools.put( l_newTool.m_name, l_properties );

        return l_tools;
    }

    /**
     * method to delete a tool
     *
     * @param p_data
     * @return
     */
    private final boolean web_static_deletetool( final Map<String, Object> p_data )
    {
        if ( !p_data.containsKey( "toolname" ) )
            return false;

        String l_toolname = String.valueOf( p_data.get( "toolname" ) );

        if ( m_toolbox.containsKey( l_toolname ) && !l_toolname.equals( CCommon.getResourceString( this, "defaulttoolname" ) ) )
        {
            m_selectedTool = m_toolbox.get( CCommon.getResourceString( this, "defaulttoolname" ) );
            m_toolbox.remove( l_toolname );
            return true;
        }

        return false;
    }

    /**
     * list all possible waypoint types
     *
     * @return
     */
    private final List<String> web_static_listwaypointtypes()
    {
        List<String> l_waypointtypes = new ArrayList<>();

        for ( final EWayPointType l_waypoint : EWayPointType.values() )
            l_waypointtypes.add( l_waypoint.m_name );

        return l_waypointtypes;
    }

    /**
     * list all possible factory types
     *
     * @return
     */
    private final Map<String, Boolean> web_static_listfactories()
    {
        Map<String, Boolean> l_factories = new HashMap<>();

        for ( final EFactoryType l_factory : EFactoryType.values() )
            l_factories.put( l_factory.m_name, l_factory.m_requireAgentProgram );

        return l_factories;
    }

    /**
     * list all possible generator types
     *
     * @return
     */
    private final List<String> web_static_listdistribution()
    {
        List<String> l_distributions = new ArrayList<>();

        for ( final EDistributionType l_distribution : EDistributionType.values() )
            l_distributions.add( l_distribution.m_name );

        return l_distributions;
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
        private final boolean m_requireAgentProgram;

        /**
         * ctor
         *
         * @param p_name
         * @param p_requireAgentProgram
         */
        private EFactoryType( final String p_name, final boolean p_requireAgentProgram )
        {
            this.m_name = p_name;
            this.m_requireAgentProgram = p_requireAgentProgram;
        }

        /**
         * method to get factory type by name
         *
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
        Normal( CCommon.getResourceString( EDistributionType.class, "normaldistribution" ) ),
        Uniform( CCommon.getResourceString( EDistributionType.class, "uniformdistribution" ) ),
        Exponential( CCommon.getResourceString( EDistributionType.class, "exponentialdistribution" ) ),
        Profile( CCommon.getResourceString( EDistributionType.class, "profiledistribution" ) );

        /**
         * name of this distribution type
         */
        private final String m_name;

        /**
         * ctor
         *
         * @param p_name
         */
        private EDistributionType( final String p_name )
        {
            this.m_name = p_name;
        }

        /**
         * method to get distribution type by name
         *
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
     */
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
         * agent program for agent cars
         */
        protected final String m_agentProgram;
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
         * generator type defines the probability and amount of cars
         */
        protected final EDistributionType m_speedProb;
        /**
         * generatorInput1 (lower bound or mean)
         */
        protected final double m_speedProbInput1;
        /**
         * generatorInput2 (upper bound or deviation)
         */
        protected final double m_speedProbInput2;
        /**
         * generator type defines the probability and amount of cars
         */
        protected final EDistributionType m_maxSpeedProb;
        /**
         * generatorInput1 (lower bound or mean)
         */
        protected final double m_maxSpeedProbInput1;
        /**
         * generatorInput2 (upper bound or deviation)
         */
        protected final double m_maxSpeedProbInput2;
        /**
         * generator type defines the probability and amount of cars
         */
        protected final EDistributionType m_accProb;
        /**
         * generatorInput1 (lower bound or mean)
         */
        protected final double m_accProbInput1;
        /**
         * generatorInput2 (upper bound or deviation)
         */
        protected final double m_accProbInput2;
        /**
         * generator type defines the probability and amount of cars
         */
        protected final EDistributionType m_decProb;
        /**
         * generatorInput1 (lower bound or mean)
         */
        protected final double m_decProbInput1;
        /**
         * generatorInput2 (upper bound or deviation)
         */
        protected final double m_decProbInput2;
        /**
         * generatorInput1 (lower bound or mean)
         */
        protected final double m_lingerProbInput1;
        /**
         * generatorInput2 (upper bound or deviation)
         */
        protected final double m_lingerProbInput2;
        /**
         * name of the tool
         */
        protected final String m_name;
        /**
         * color of the waypoint
         */
        protected final Color m_color;
        /**
         * indicates if this tool is deleteable
         */
        protected final boolean m_deleteable;

        /**
         * ctor
         *
         * @param p_parameter
         */
        protected CTool( final Map<String, Object> p_parameter )
        {

            int l_redValue = (int) Double.parseDouble( String.valueOf( p_parameter.get( "red" ) ) );
            int l_greenValue = (int) Double.parseDouble( String.valueOf( p_parameter.get( "green" ) ) );
            int l_blueValue = (int) Double.parseDouble( String.valueOf( p_parameter.get( "blue" ) ) );

            this.m_wayPointType = EWayPointType.getWaypointTypeByName( String.valueOf( p_parameter.get( "waypointtype" ) ) );
            ;
            this.m_radius = Double.parseDouble( String.valueOf( p_parameter.get( "radius" ) ) );
            this.m_factoryType = EFactoryType.getFactoryTypeByName( String.valueOf( p_parameter.get( "factory" ) ) );
            ;
            this.m_agentProgram = String.valueOf( p_parameter.get( "agentprogram" ) );
            this.m_generatorType = EDistributionType.getDistributionTypeByName( String.valueOf( p_parameter.get( "generator" ) ) );
            ;
            this.m_carcount = Integer.parseInt( String.valueOf( p_parameter.get( "carcount" ) ) );
            this.m_generatorInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "generatorinput1" ) ) );
            this.m_generatorInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "generatorinput2" ) ) );
            this.m_histrogram = new int[]{};
            this.m_speedProb = EDistributionType.getDistributionTypeByName( String.valueOf( p_parameter.get( "speedprob" ) ) );
            this.m_speedProbInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "speedprobinput1" ) ) );
            this.m_speedProbInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "speedprobinput2" ) ) );
            this.m_maxSpeedProb = EDistributionType.getDistributionTypeByName( String.valueOf( p_parameter.get( "maxspeedprob" ) ) );
            this.m_maxSpeedProbInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "maxspeedprobinput1" ) ) );
            this.m_maxSpeedProbInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "maxspeedprobinput2" ) ) );
            this.m_accProb = EDistributionType.getDistributionTypeByName( String.valueOf( p_parameter.get( "accprob" ) ) );
            this.m_accProbInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "accprobinput1" ) ) );
            this.m_accProbInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "accprobinput2" ) ) );
            this.m_decProb = EDistributionType.getDistributionTypeByName( String.valueOf( p_parameter.get( "decprob" ) ) );
            this.m_decProbInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "decprobinput1" ) ) );
            this.m_decProbInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "decprobinput2" ) ) );
            this.m_lingerProbInput1 = Double.parseDouble( String.valueOf( p_parameter.get( "lingerprobinput1" ) ) );
            this.m_lingerProbInput2 = Double.parseDouble( String.valueOf( p_parameter.get( "lingerprobinput2" ) ) );
            this.m_color = new Color( l_redValue, l_greenValue, l_blueValue );
            this.m_name = (String) p_parameter.get( "name" );
            this.m_deleteable = m_name.equals( m_defaultProperties.get( "name" ) ) ? false : true;
        }

        /**
         * method which returns a waypoint with settings from the ui
         *
         * @param p_position position of the waypoint
         * @return configured waypoint
         */
        protected final IWayPointBase getWaypoint( GeoPosition p_position )
        {
            switch ( m_wayPointType )
            {
                case CarWaypointRandom:
                    return new CCarRandomWayPoint(
                            p_position,
                            this.getGenerator(),
                            this.getFactory(),
                            m_radius,
                            m_color,
                            CCommon.getResourceString( this, "defaultwaypointname" ) + CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped(
                                    "Car WayPoints"
                            ).size()
                    );

                case CayWaypointPath:
                    return new CCarPathWayPoint(
                            p_position,
                            this.getGenerator(),
                            this.getFactory(),
                            m_color,
                            CCommon.getResourceString( this, "defaultwaypointname" ) + CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped(
                                    "Car WayPoints"
                            ).size()
                    );

                default:
                    return new CCarRandomWayPoint(
                            p_position,
                            this.getGenerator(),
                            this.getFactory(),
                            m_radius,
                            m_color,
                            CCommon.getResourceString( this, "defaultwaypointname" ) + CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped(
                                    "Car WayPoints"
                            ).size()
                    );
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
                            getDistribution( m_speedProb, m_speedProbInput1, m_speedProbInput2 ),
                            getDistribution( m_maxSpeedProb, m_maxSpeedProbInput1, m_maxSpeedProbInput2 ),
                            getDistribution( m_accProb, m_accProbInput1, m_accProbInput2 ),
                            getDistribution( m_decProb, m_decProbInput1, m_decProbInput2 ),
                            new UniformRealDistribution( m_lingerProbInput1, m_lingerProbInput2 )
                    );

                case DefaultAgentCarFactory:
                    return new CDistributionAgentCarFactory(
                            getDistribution( m_speedProb, m_speedProbInput1, m_speedProbInput2 ),
                            getDistribution( m_maxSpeedProb, m_maxSpeedProbInput1, m_maxSpeedProbInput2 ),
                            getDistribution( m_accProb, m_accProbInput1, m_accProbInput2 ),
                            getDistribution( m_decProb, m_decProbInput1, m_decProbInput2 ),
                            new UniformRealDistribution( m_lingerProbInput1, m_lingerProbInput2 ),
                            m_agentProgram
                    );

                default:
                    return new CDistributionDefaultCarFactory(
                            getDistribution( m_speedProb, m_speedProbInput1, m_speedProbInput2 ),
                            getDistribution( m_maxSpeedProb, m_maxSpeedProbInput1, m_maxSpeedProbInput2 ),
                            getDistribution( m_accProb, m_accProbInput1, m_accProbInput2 ),
                            getDistribution( m_decProb, m_decProbInput1, m_decProbInput2 ),
                            new UniformRealDistribution( m_lingerProbInput1, m_lingerProbInput2 )
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

        /**
         * method which returns a distribution with settings from the ui
         *
         * @param p_distribution
         * @param p_distributionInput1
         * @param p_distributionInput2
         * @return
         */
        protected final AbstractRealDistribution getDistribution( EDistributionType p_distribution, double p_distributionInput1, double p_distributionInput2 )
        {

            switch ( p_distribution )
            {
                case Uniform:
                    return new UniformRealDistribution( p_distributionInput1, p_distributionInput2 );

                case Normal:
                    return new NormalDistribution( p_distributionInput1, p_distributionInput2 );

                case Exponential:
                    return new ExponentialDistribution( p_distributionInput1, p_distributionInput2 );

                default:
                    return new UniformRealDistribution( p_distributionInput1, p_distributionInput2 );
            }
        }
    }
}
