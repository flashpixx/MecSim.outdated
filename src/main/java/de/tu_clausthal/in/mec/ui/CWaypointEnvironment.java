/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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
import de.tu_clausthal.in.mec.common.CNameHashMap;
import de.tu_clausthal.in.mec.common.EDistribution;
import de.tu_clausthal.in.mec.object.mas.EAgentLanguages;
import de.tu_clausthal.in.mec.object.waypoint.CCarWayPointLayer;
import de.tu_clausthal.in.mec.object.waypoint.factory.CAgentCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.CDefaultCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.CTimeDistribution;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarPathWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarRandomWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IPathWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPoint;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * ui bundle which is responsible for the waypoint-tool settings
 */
public class CWaypointEnvironment
{
    /**
     * waypoint layer reference
     */
    private final static CCarWayPointLayer c_waypointLayer = CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped( "Car WayPoints" );
    /**
     * map with distribution names
     */
    private final static Map<String, Object> c_distribution = new HashMap<String, Object>()
    {{
            for ( final EDistribution l_distribution : EDistribution.values() )
                put(
                        l_distribution.toString(), new HashMap()
                        {{
                                put( "firstmomentum", CCommon.getMap( "label", l_distribution.getFirstMomentum(), "used", l_distribution.hasFirstMomentum() ) );
                                put(
                                        "secondmomentum", CCommon.getMap(
                                                "label", l_distribution.getSecondMomentum(), "used", l_distribution.hasSecondMomentum()
                                        )
                                );
                                put( "id", l_distribution.name() );
                            }}
                );
        }};
    /**
     * map with factory names
     */
    private final static Map<String, Map<String, Object>> c_factory = new HashMap<String, Map<String, Object>>()
    {{
            for ( final EFactory l_factory : EFactory.values() )
                put(
                        l_factory.toString(), new HashMap()
                        {{
                                put( "requireagent", l_factory.getRequireAgent() );
                                put( "id", l_factory.name() );
                            }}
                );
        }};
    /**
     * map with name and ID
     */
    private final static Map<String, String> c_generator = new HashMap<String, String>()
    {{
            for ( final EGenerator l_generator : EGenerator.values() )
                put( l_generator.toString(), l_generator.name() );
        }};
    /**
     * static label for main menu UI
     */
    private static final Map<String, String> c_labelmainmenu = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypoint_name" ) );
            put( "id_newpreset", CCommon.getResourceString( CWaypointEnvironment.class, "ui_mainmenu_id_newpreset" ) );
            put( "id_waypointconnection", CCommon.getResourceString( CWaypointEnvironment.class, "ui_mainmenu_id_waypointconnection" ) );
        }};
    /**
     * static label for preset wizard UI
     */
    private static final Map<String, String> c_labelpresetwizard = new HashMap<String, String>()
    {{
            // window title
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_name" ) );


            // wizard step header
            put( "id_factoryhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_id_factoryhead" ) );
            put( "id_carhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_id_carhead" ) );
            put( "id_customhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_id_customhead" ) );


            // wizard first step
            put( "id_factorysettingshead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_id_factorysettingshead" ) );
            put( "label_factory", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_id_label_factory" ) );
            put( "label_agent", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_id_agent" ) );

            put( "id_waypointsettingshead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_id_waypointsettingshead" ) );
            put( "label_waypoint", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_label_waypoint" ) );
            put( "label_radius", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_label_radius" ) );

            put( "id_generatorsettingshead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_id_generatorsettingshead" ) );
            put( "label_generatortyp", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_label_generatortyp" ) );
            put( "label_carcount", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_label_carcount" ) );
            put( "label_generatordistribution", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_firststep_label_generatordistribution" ) );


            // wizard third step
            put( "id_speedhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_id_speedhead" ) );
            put( "label_speedfactor", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_label_speedfactor" ) );

            put( "id_maxspeedhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_id_maxspeedhead" ) );
            put( "label_maxspeeddistribution", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_label_maxspeeddistribution" ) );

            put( "id_accelerationhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_id_acceleration" ) );
            put(
                    "label_accelerationdistribution", CCommon.getResourceString(
                            CWaypointEnvironment.class, "ui_wizard_secondstep_label_accelerationdistribution"
                    )
            );

            put( "id_decelerationhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_id_deceleration" ) );
            put(
                    "label_decelerationdistribution", CCommon.getResourceString(
                            CWaypointEnvironment.class, "ui_wizard_secondstep_label_decelerationdistribution"
                    )
            );

            put( "id_lingerhead", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_id_linger" ) );
            put( "label_lingerdistribution", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_secondstep_label_lingerdistribution" ) );


            // wizard fourth step
            put( "label_name", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_thirdstep_label_name" ) );
            put( "label_color", CCommon.getResourceString( CWaypointEnvironment.class, "ui_wizard_thirdstep_label_color" ) );

        }};
    /**
     * static label for waypoint connection UI
     */
    private static final Map<String, String> c_labelwaypointconnection = new HashMap<String, String>()
    {{
            // window title
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_name" ) );

            // waypoint list
            put( "id_identifier", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_id_identifier" ) );
            put( "id_waypointname", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_id_waypointname" ) );
            put( "id_latitude", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_id_latitude" ) );
            put( "id_longitude", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_id_longitude" ) );
            put( "id_add", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_id_add" ) );
            put( "id_edit", CCommon.getResourceString( CWaypointEnvironment.class, "ui_waypointconnection_id_edit" ) );
        }};
    /**
     * map with name and ID
     */
    private final static Map<String, String> c_waypoint = new HashMap<String, String>()
    {{
            for ( final EWaypoint l_waypoint : EWaypoint.values() )
                put( l_waypoint.toString(), l_waypoint.name() );
        }};
    /**
     * map with objects, which are created by the UI call
     **/
    private final Map<String, Object> m_currentsettings = new HashMap<>();


    /**
     * ctor to define the waypoint environment
     */
    public CWaypointEnvironment()
    {
        CSimulation.getInstance().getStorage().add( "waypoint", this );
    }

    /**
     * creates a waypoint
     *
     * @param p_position geoposition of the waypoint
     */
    @SuppressWarnings( "unchecked" )
    public final void setWaypoint( final GeoPosition p_position )
    {
        if ( m_currentsettings.isEmpty() )
            throw new IllegalStateException( CCommon.getResourceString( this, "settingsnotexists" ) );

        c_waypointLayer.add(
                (IWayPoint) EWaypoint.valueOf( (String) m_currentsettings.get( "waypoint" ) ).get(
                        p_position,
                        (IGenerator) m_currentsettings.get( "generator" ),
                        (IFactory) m_currentsettings.get( "factory" ),
                        (Double) m_currentsettings.get( "radius" ),
                        (Color) m_currentsettings.get( "color" ),
                        (String) m_currentsettings.get( "name" )
                )
        );
    }

    /**
     * creates from a map object an distribution object
     *
     * @param p_object map object
     * @return distribution object
     */
    private AbstractRealDistribution createDistribution( final CNameHashMap.CImmutable p_object )
    {
        return EDistribution.valueOf( p_object.<String>getOrDefault( "distribution", "" ) ).get(
                p_object.<Number>get( "firstmomentum" ).doubleValue(),
                p_object.<Number>get( "secondmomentum" ).doubleValue()
        );
    }

    /**
     * returns all static label information for main menu
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_labelmainmenu()
    {
        return c_labelmainmenu;
    }

    /**
     * returns all static label information for wizard
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_labelpresetwizard()
    {
        return c_labelpresetwizard;
    }

    /**
     * returns all static label information for waypoint connection
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_labelwaypointconnection()
    {
        return c_labelwaypointconnection;
    }

    /**
     * returns all available path waypoints with additional information
     *
     * @return map all available path waypoints and additional information
     */
    private final Map<String, Map<String, String>> web_static_listpathwaypoints()
    {
        Map<String, Map<String, String>> result = new HashMap<>();

        for ( IWayPoint l_waypoint : c_waypointLayer )
        {
            Map<String, String> l_info = new HashMap<>();

            l_info.put( "id", String.valueOf( l_waypoint.getID() ) );
            l_info.put( "name", l_waypoint.getName() );
            l_info.put( "latitude", String.valueOf( l_waypoint.getPosition().getLatitude() ) );
            l_info.put( "longitude", String.valueOf( l_waypoint.getPosition().getLongitude() ) );

            if ( l_waypoint instanceof IPathWayPoint )
            {
                l_info.put( "type", "path" );
            }
            else
            {
                l_info.put( "type", "random" );
            }

            result.put( l_waypoint.toString(), l_info );
        }

        return result;
    }

    /**
     * method to get the makrov chain
     *
     * @return
     */
    private final IPathWayPoint.CMakrovChain web_static_getmakrovchain( final Map<String, Object> p_data )
    {
        for ( IWayPoint l_waypoint : c_waypointLayer )
            if ( l_waypoint.toString().equals( p_data.get( "makrovwaypoint" ) ) )
                if ( l_waypoint instanceof IPathWayPoint )
                    return ( (IPathWayPoint) l_waypoint ).getMakrovChain();

        return null;
    }

    /**
     * method to add a node to a makrov chain
     *
     * @param p_data
     */
    private final void web_static_addnode( final Map<String, Object> p_data )
    {
        IPathWayPoint l_makrovWaypoint = null;
        IWayPoint l_newWaypoint = null;

        for ( IWayPoint l_waypoint : c_waypointLayer )
        {
            if ( l_waypoint.toString().equals( p_data.get( "makrovwaypoint" ) ) )
                if ( l_waypoint instanceof IPathWayPoint )
                    l_makrovWaypoint = (IPathWayPoint) l_waypoint;

            if ( l_waypoint.toString().equals( p_data.get( "waypoint" ) ) )
                l_newWaypoint = l_waypoint;
        }

        if ( l_makrovWaypoint != null && l_newWaypoint != null )
            l_makrovWaypoint.getMakrovChain().addNode( l_newWaypoint );
    }

    /**
     * method to remove a node from makrov chain
     *
     * @param p_data
     */
    private final void web_static_removenode( final Map<String, Object> p_data )
    {
        IPathWayPoint l_makrovWaypoint = null;
        IWayPoint l_removeWaypoint = null;

        for ( IWayPoint l_waypoint : c_waypointLayer )
        {
            if ( l_waypoint.toString().equals( p_data.get( "makrovwaypoint" ) ) )
                if ( l_waypoint instanceof IPathWayPoint )
                    l_makrovWaypoint = (IPathWayPoint) l_waypoint;

            if ( l_waypoint.toString().equals( p_data.get( "waypoint" ) ) )
                l_removeWaypoint = l_waypoint;
        }

        if ( l_makrovWaypoint != null && l_removeWaypoint != null )
            l_makrovWaypoint.getMakrovChain().removeNode( l_removeWaypoint );
    }

    /**
     * method to add an edge to a makrov chain
     *
     * @param p_data
     */
    private final void web_static_addedge( final Map<String, Object> p_data )
    {
        IPathWayPoint l_makrovWaypoint = null;
        IWayPoint l_source = null;
        IWayPoint l_target = null;

        for ( IWayPoint l_waypoint : c_waypointLayer )
        {
            if ( l_waypoint.toString().equals( p_data.get( "makrovwaypoint" ) ) )
                if ( l_waypoint instanceof IPathWayPoint )
                    l_makrovWaypoint = (IPathWayPoint) l_waypoint;

            if ( l_waypoint.toString().equals( p_data.get( "source" ) ) )
                l_source = l_waypoint;

            if ( l_waypoint.toString().equals( p_data.get( "target" ) ) )
                l_target = l_waypoint;
        }

        if ( l_makrovWaypoint != null && l_source != null && l_target != null )
            l_makrovWaypoint.getMakrovChain().addEdge( l_source, l_target, 1 );
    }

    /**
     * method to remove an edge from makrov chain
     *
     * @param p_data
     */
    private final void web_static_removeedge( final Map<String, Object> p_data )
    {
        IPathWayPoint l_makrovWaypoint = null;
        IWayPoint l_source = null;
        IWayPoint l_target = null;

        for ( IWayPoint l_waypoint : c_waypointLayer )
        {
            if ( l_waypoint.toString().equals( p_data.get( "makrovwaypoint" ) ) )
                if ( l_waypoint instanceof IPathWayPoint )
                    l_makrovWaypoint = (IPathWayPoint) l_waypoint;

            if ( l_waypoint.toString().equals( p_data.get( "source" ) ) )
                l_source = l_waypoint;

            if ( l_waypoint.toString().equals( p_data.get( "target" ) ) )
                l_target = l_waypoint;
        }

        if ( l_makrovWaypoint != null && l_source != null && l_target != null )
            l_makrovWaypoint.getMakrovChain().removeEdge( l_source, l_target );
    }

    /**
     * list all possible distribution types
     *
     * @return map with names of distribution and IDs
     */
    private final Map<String, Object> web_static_listdistribution()
    {
        return c_distribution;
    }

    /**
     * list all possible factory types
     *
     * @return map with language name and map of ID and required-agent-program flag
     */
    private final Map<String, Map<String, Object>> web_static_listfactory()
    {
        return c_factory;
    }

    /**
     * list all possible generator types
     *
     * @return map with language name and ID
     */
    private final Map<String, String> web_static_listgenerator()
    {
        return c_generator;
    }

    /**
     * list all possible waypoint types
     *
     * @return map with language name and ID
     */
    private final Map<String, String> web_static_listwaypoint()
    {
        return c_waypoint;
    }

    /**
     * creates the current settings of
     *
     * @param p_data input data
     */
    private final void web_static_set( final Map<String, Object> p_data )
    {
        // create travable map and clear cached settings
        m_currentsettings.clear();
        final CNameHashMap.CImmutable l_data = new CNameHashMap.CImmutable( p_data );

        // create items from the UI data and set internal properties
        m_currentsettings.put( "name", l_data.<String>get( "name" ) );
        m_currentsettings.put( "waypoint", l_data.<String>get( "waypoint" ) );
        m_currentsettings.put( "radius", l_data.<Number>get( "radius" ).doubleValue() );
        m_currentsettings.put(
                "color", new Color(
                        l_data.<Number>get( "color/red" ).intValue(),
                        l_data.<Number>get( "color/green" ).intValue(),
                        l_data.<Number>get( "color/blue" ).intValue()
                )
        );

        m_currentsettings.put(
                "factory",
                EFactory.valueOf( l_data.<String>get( "factory" ) ).get(
                        l_data.<Number>get( "speedfactor" ).doubleValue(),
                        this.createDistribution( l_data.get( "distribution/maxspeed" ) ),
                        this.createDistribution( l_data.get( "distribution/acceleration" ) ),
                        this.createDistribution( l_data.get( "distribution/deceleration" ) ),
                        this.createDistribution( l_data.get( "distribution/linger" ) ),
                        l_data.<String>get( "agent/agent" ),
                        EAgentLanguages.valueOf( l_data.<String>get( "agent/type" ) )
                )
        );

        m_currentsettings.put(
                "generator",
                EGenerator.valueOf( l_data.<String>getOrDefault( "generator", "" ) ).get(

                        this.createDistribution( l_data.get( "distribution/generator" ) ),
                        l_data.<Number>get( "carcount" ).intValue()

                )
        );

    }

    /**
     * enum for factories
     */
    private enum EFactory
    {
        /**
         * default car factory
         **/
        DefaultCarFactory( CCommon.getResourceString( EFactory.class, "defaultcarfactory" ), false ),
        /**
         * default agent-car factory
         **/
        DefaultAgentCarFactory( CCommon.getResourceString( EFactory.class, "defaultagentcarfactory" ), true );

        /**
         * variable indicate if this factory type require an agent program
         */
        private final boolean m_requireAgent;
        /**
         * name of this factory type
         */
        private final String m_text;

        /**
         * ctor
         *
         * @param p_text language depended name of the factory
         * @param p_requireAgent boolean flag that an agent-program is required
         */
        EFactory( final String p_text, final boolean p_requireAgent )
        {
            this.m_text = p_text;
            this.m_requireAgent = p_requireAgent;
        }

        /**
         * returns the factory with the current settings
         *
         * @param p_data array with all parameters (depend on factory call)
         * @return factory
         */
        @SuppressWarnings( "unchecked" )
        public final IFactory<?> get( final Object... p_data )
        {
            switch ( this )
            {
                case DefaultCarFactory:
                    return new CDefaultCarFactory(
                            // maxspeed factor
                            (Double) p_data[0],
                            // maxspeed distribution
                            (AbstractRealDistribution) p_data[1],
                            // acceleration distribution
                            (AbstractRealDistribution) p_data[2],
                            // deceleration distribution
                            (AbstractRealDistribution) p_data[3],
                            // linger distribution
                            (AbstractRealDistribution) p_data[4]
                    );

                case DefaultAgentCarFactory:
                    return new CAgentCarFactory(
                            // maxspeed factor
                            (Double) p_data[0],
                            // maxspeed distribution
                            (AbstractRealDistribution) p_data[1],
                            // acceleration distribution
                            (AbstractRealDistribution) p_data[2],
                            // deceleration distribution
                            (AbstractRealDistribution) p_data[3],
                            // linger distribution
                            (AbstractRealDistribution) p_data[4],
                            // agent name
                            (String) p_data[5],
                            // agent type
                            (EAgentLanguages) p_data[6]
                    );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EFactory.class, "unknownfactory" ) );
            }
        }

        /**
         * returns the boolean flag if an agent program is required for the factory
         *
         * @return agent-program-require boolean flag
         */
        public boolean getRequireAgent()
        {
            return m_requireAgent;
        }

        @Override
        public String toString()
        {
            return m_text;
        }


    }

    /**
     * generator enum
     */
    private enum EGenerator
    {
        TimeDistribution( CCommon.getResourceString( EGenerator.class, "timedistribution" ) );

        /**
         * name of this generator
         */
        private final String m_text;

        /**
         * ctor
         *
         * @param p_text language depended text
         */
        EGenerator( final String p_text )
        {
            m_text = p_text;
        }

        /**
         * returns the generator object
         *
         * @param p_data input data
         * @return generator object
         */
        @SuppressWarnings( "unchecked" )
        public final IGenerator get( final Object... p_data )
        {
            switch ( this )
            {
                case TimeDistribution:
                    return new CTimeDistribution( (AbstractRealDistribution) p_data[0], (Integer) p_data[1] );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EGenerator.class, "unknowgenerator" ) );
            }
        }

        @Override
        public String toString()
        {
            return m_text;
        }


    }

    /**
     * enum for waypoints
     */
    private enum EWaypoint
    {
        /**
         * random car waypoint
         **/
        CarWaypointRandom( CCommon.getResourceString( EWaypoint.class, "carwaypointrandom" ) ),
        /**
         * path car waypoint
         */
        CarWaypointPath( CCommon.getResourceString( EWaypoint.class, "carwaypointpath" ) );

        /**
         * name of this waypoint type
         */
        private final String m_text;

        /**
         * ctor
         *
         * @param p_text language dependend name
         */
        EWaypoint( final String p_text )
        {
            this.m_text = p_text;
        }

        /**
         * returns the waypoint of the current settings
         *
         * @return waypoint
         */
        @SuppressWarnings( "unchecked" )
        public final IWayPoint<?> get( final Object... p_data )
        {

            switch ( this )
            {
                case CarWaypointPath:
                    return new CCarPathWayPoint(
                            (GeoPosition) p_data[0], (IGenerator) p_data[1], (ICarFactory) p_data[2], (Color) p_data[4], (String) p_data[5]
                    );

                case CarWaypointRandom:
                    return new CCarRandomWayPoint(
                            (GeoPosition) p_data[0], (IGenerator) p_data[1], (ICarFactory) p_data[2], (Double) p_data[3], (Color) p_data[4], (String) p_data[5]
                    );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EWaypoint.class, "unknownwaypoint" ) );
            }
        }

        @Override
        public String toString()
        {
            return m_text;
        }

    }

}