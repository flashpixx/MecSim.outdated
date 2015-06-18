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
import de.tu_clausthal.in.mec.common.CNameHashMap;
import de.tu_clausthal.in.mec.object.waypoint.CCarWayPointLayer;
import de.tu_clausthal.in.mec.object.waypoint.factory.CDistributionAgentCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.CDistributionDefaultCarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.object.waypoint.point.CCarRandomWayPoint;
import de.tu_clausthal.in.mec.object.waypoint.point.IWayPoint;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * ui bundle which is responsible for the waypoint-tool settings
 *
 * @todo full refactor
 */
public class CWaypointEnvironment
{
    /**
     * map with distribution names
     */
    private final static Map<String, Map<String, String>> c_distribution = new HashMap<String, Map<String, String>>()
    {{
            for ( final EDistribution l_distribution : EDistribution.values() )
                put(
                        l_distribution.toString(), new HashMap()
                        {{
                                put( "left", l_distribution.getDeviationText().getLeft() );
                                put( "right", l_distribution.getDeviationText().getRight() );
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
                                put( "agentprogram", l_factory.getRequireAgent() );
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
     * static label for UI
     */
    private static final Map<String, String> c_label = new HashMap<String, String>()
    {{
            // window title
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "title" ) );


            // wizard step header
            put( "id_factoryhead", CCommon.getResourceString( CWaypointEnvironment.class, "headfactorysetting" ) );
            put( "id_carhead", CCommon.getResourceString( CWaypointEnvironment.class, "headcarsetting" ) );
            put( "id_customhead", CCommon.getResourceString( CWaypointEnvironment.class, "headcustomizing" ) );


            // wizard first step
            put( "id_factorysettingshead", CCommon.getResourceString( CWaypointEnvironment.class, "factorysettingshead" ) );
            put( "label_factory", CCommon.getResourceString( CWaypointEnvironment.class, "selectyourfactory" ) );
            put( "label_agent", CCommon.getResourceString( CWaypointEnvironment.class, "selectyouragentprogram" ) );

            put( "id_waypointsettingshead", CCommon.getResourceString( CWaypointEnvironment.class, "waypointsettingshead" ) );
            put( "label_waypoint", CCommon.getResourceString( CWaypointEnvironment.class, "selectwaypointtype" ) );
            put( "label_radius", CCommon.getResourceString( CWaypointEnvironment.class, "selectwaypointradius" ) );

            put( "id_generatorsettingshead", CCommon.getResourceString( CWaypointEnvironment.class, "generatorsettingshead" ) );
            put( "label_generatortyp", CCommon.getResourceString( CWaypointEnvironment.class, "labelgeneratortyp" ) );
            put( "label_carcount", CCommon.getResourceString( CWaypointEnvironment.class, "selectyourcarcount" ) );
            put( "label_generatordistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectyourgenerator" ) );


            // wizard third step
            put( "id_speedhead", CCommon.getResourceString( CWaypointEnvironment.class, "speedsettingslabel" ) );
            put( "label_speeddistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectspeedprob" ) );

            put( "id_maxspeedhead", CCommon.getResourceString( CWaypointEnvironment.class, "maxspeedsettingslabel" ) );
            put( "label_maxspeeddistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectmaxspeedprob" ) );

            put( "id_accelerationhead", CCommon.getResourceString( CWaypointEnvironment.class, "accsettingslabel" ) );
            put( "label_accelerationdistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectaccprob" ) );

            put( "id_decelerationhead", CCommon.getResourceString( CWaypointEnvironment.class, "decsettingslabel" ) );
            put( "label_decelerationdistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectdecprob" ) );

            put( "id_lingerhead", CCommon.getResourceString( CWaypointEnvironment.class, "lingerersettingslabel" ) );
            put( "label_lingerdistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectlingerprob" ) );


            // wizard fourth step
            put( "label_name", CCommon.getResourceString( CWaypointEnvironment.class, "selecttoolnamelabel" ) );
            put( "label_color", CCommon.getResourceString( CWaypointEnvironment.class, "selecttoolcolor" ) );

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
    private final Map<String, Object> m_currentsetting = new HashMap<>();


    /**
     * creates a waypoint
     *
     * @param p_position geoposition of the waypoint
     */
    public final void setWaypoint( final GeoPosition p_position )
    {
        CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped( "Car WayPoints" ).add(
                (IWayPoint) EWaypoint.valueOf( (String) m_currentsetting.get( "type" ) ).get(
                        p_position,
                        null,
                        (IFactory) m_currentsetting.get( "factory" ),
                        (Double) m_currentsetting.get( "radius" ),
                        (Color) m_currentsetting.get( "color" ),
                        (String) m_currentsetting.get( "name" )
                )
        );
    }

    /**
     * returns all static label information
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_label()
    {
        return c_label;
    }

    /**
     * list all possible distribution types
     *
     * @return map with names of distribution and IDs
     */
    private final Map<String, Map<String, String>> web_static_listdistribution()
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
     * set method of the UI data
     *
     * @param p_data input data
     */
    private final void web_static_set( final Map<String, Object> p_data )
    {
        // create travable map
        m_currentsetting.clear();
        final CNameHashMap.CImmutable l_data = new CNameHashMap.CImmutable( p_data );


        // create items from the UI data and set internal properties
        m_currentsetting.put( "color", Color.WHITE );
        m_currentsetting.put( "name", l_data.<String>getOrDefault( "name", "" ) );
        m_currentsetting.put( "radius", l_data.<Double>get( "radius" ) );
        m_currentsetting.put( "carcount", l_data.<Integer>getOrDefault( "carcount", 0 ) );
        m_currentsetting.put( "type", EWaypoint.valueOf( l_data.<String>getOrDefault( "type", "" ) ) );
        m_currentsetting.put(
                "factory",
                EFactory.valueOf( l_data.<String>getOrDefault( "factory", "" ) ).get(

                        EDistribution.valueOf( l_data.<String>getOrDefault( "speed/distribution", "" ) ).get(
                                l_data.<Float>get( "speed/left" ), l_data.<Float>get(
                                        "speed/right"
                                )
                        ),
                        EDistribution.valueOf( l_data.<String>getOrDefault( "maxspeed/distribution", "" ) ).get(
                                l_data.<Float>get( "maxspeed/left" ), l_data.<Float>get( "maxspeed/right" )
                        ),
                        EDistribution.valueOf( l_data.<String>getOrDefault( "acceleration/distribution", "" ) ).get(
                                l_data.<Float>get( "acceleration/left" ), l_data.<Float>get( "acceleration/right" )
                        ),
                        EDistribution.valueOf( l_data.<String>getOrDefault( "deceleration/distribution", "" ) ).get(
                                l_data.<Float>get( "deceleration/left" ), l_data.<Float>get( "deceleration/right" )
                        ),
                        EDistribution.valueOf( l_data.<String>getOrDefault( "linger/distribution", "" ) ).get(
                                l_data.<Float>get( "linger/left" ), l_data.<Float>get(
                                        "linger/right"
                                )
                        ),
                        l_data.<String>getOrDefault( "agent", "" )
                )
        );
        m_currentsetting.put( "generator", EGenerator.valueOf( l_data.<String>getOrDefault( "generatortyp", "" ) ) );

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
        /** path waypoint **/
        CayWaypointPath( CCommon.getResourceString( EWaypoint.class, "carwaypointpath" ) );

        /**
         * name of this waypoint type
         */
        private final String m_text;

        /**
         * ctor
         *
         * @param p_text language dependend name
         */
        private EWaypoint( final String p_text )
        {
            this.m_text = p_text;
        }

        /**
         * returns the waypoint of the current settings
         *
         * @return waypoint
         */
        public final IWayPoint<?> get( final Object... p_data )
        {

            switch ( this )
            {

                case CarWaypointRandom:
                    return new CCarRandomWayPoint(
                            (GeoPosition) p_data[0], (IGenerator) p_data[1], (ICarFactory) p_data[2], (Double) p_data[3], (Color) p_data[4], (String)p_data[5] );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EWaypoint.class, "unkownwaypoint" ) );
            }
        }

        @Override
        public String toString()
        {
            return m_text;
        }

    }

    /**
     * enum for factories
     */
    private enum EFactory
    {
        /** default car factory **/
        DefaultCarFactory( CCommon.getResourceString( EFactory.class, "defaultcarfactory" ), false ),
        /** default agent-car factory **/
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
        private EFactory( final String p_text, final boolean p_requireAgent )
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
        public final IFactory<?> get( final Object... p_data )
        {
            switch ( this )
            {
                case DefaultCarFactory:
                    return new CDistributionDefaultCarFactory(
                            (AbstractRealDistribution) p_data[0], (AbstractRealDistribution) p_data[1], (AbstractRealDistribution) p_data[2],
                            (AbstractRealDistribution) p_data[3], (AbstractRealDistribution) p_data[4]
                    );

                case DefaultAgentCarFactory:
                    return new CDistributionAgentCarFactory(
                            (AbstractRealDistribution) p_data[0], (AbstractRealDistribution) p_data[1], (AbstractRealDistribution) p_data[2],
                            (AbstractRealDistribution) p_data[3], (AbstractRealDistribution) p_data[4], (String) p_data[5]
                    );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EFactory.class, "unkownfactory" ) );
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
        private EGenerator( final String p_text )
        {
            m_text = p_text;
        }

        public final IGenerator get( final Object... p_data )
        {
            return null;
        }

        @Override
        public String toString()
        {
            return m_text;
        }


    }

    /**
     * enum for distributions
     */
    private enum EDistribution
    {
        /** normal distribution **/
        Normal(
                CCommon.getResourceString( EDistribution.class, "normaldistribution" ), CCommon.getResourceString(
                EDistribution.class, "normaldistributionleft"
        ), CCommon.getResourceString( EDistribution.class, "normaldistributionright" )
        ),
        /** uniform distribution **/
        Uniform(
                CCommon.getResourceString( EDistribution.class, "uniformdistribution" ), CCommon.getResourceString(
                EDistribution.class, "uniformdistributionleft"
        ), CCommon.getResourceString( EDistribution.class, "uniformdistributionright" )
        ),
        /** exponential distribution **/
        Exponential(
                CCommon.getResourceString( EDistribution.class, "exponentialdistribution" ), CCommon.getResourceString(
                EDistribution.class, "exponentialdistributionleft"
        ), CCommon.getResourceString( EDistribution.class, "exponentialdistributionright" )
        );


        /**
         * tupel of bound / deviation names
         */
        private final Pair<String, String> m_deviation;
        /**
         * name of this distribution type
         */
        private final String m_text;


        /**
         * ctor
         *
         * @param p_text language depend name,
         * @param p_left lower / first deviation language depend name
         * @param p_right higher / second deviation language depend name
         */
        private EDistribution( final String p_text, final String p_left, final String p_right )
        {
            m_text = p_text;
            m_deviation = new ImmutablePair<>( p_left, p_right );
        }

        /**
         * returns a distribution object
         *
         * @param p_left lower / first deviation
         * @param p_right higher / second deviation
         * @return distribution
         */
        public final AbstractRealDistribution get( final double p_left, final double p_right )
        {
            switch ( this )
            {
                case Uniform:
                    return new UniformRealDistribution( p_left, p_right );

                case Normal:
                    return new NormalDistribution( p_left, p_right );

                case Exponential:
                    return new ExponentialDistribution( p_left, p_right );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EDistribution.class, "unkowndistribution" ) );
            }
        }

        /**
         * @return bound name values
         */
        public Pair<String, String> getDeviationText()
        {
            return m_deviation;
        }

        @Override
        public String toString()
        {
            return m_text;
        }


    }

}
