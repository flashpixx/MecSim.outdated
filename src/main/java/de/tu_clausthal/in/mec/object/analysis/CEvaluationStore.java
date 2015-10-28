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

package de.tu_clausthal.in.mec.object.analysis;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CCarJasonAgentLayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.generic.ILiteral;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CInconsistencyLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


/**
 * statistic evaluation access
 *
 * @see http://db.apache.org/ddlutils/
 */
public final class CEvaluationStore extends IDatabase
{
    /**
     * table name of agent table
     **/
    private static String c_tableagent = "agent";
    /**
     * table name of inconsistency table
     */
    private static String c_tableinconsistency = "inconsistency";
    /**
     * table name of beliefbase table
     */
    private static String c_tablebeliefbase = "beliefbase";
    /**
     * table name of fundamental diagram
     */
    private static String c_fundamentaldata = "fundamentaldata";


    /**
     * ctor
     */
    public CEvaluationStore()
    {
        super();

        // --- create worker (for faster inserting referential integrity is not used) ---
        try
        {
            m_data.add( new CCollectorInconsistency() );
            m_data.add( new CCollectorBelief() );
            m_data.add( new CCollectorAgent() );
            m_data.add( new CCollectorFundamentalDiagram() );
        }
        catch ( final SQLException l_exception )
        {
            CLogger.error( l_exception );
        }

        // --- create table structures ---


        this.createTable(
                c_tableagent,
                this.createTableFieldList(
                        "agenthash int not null", "agentsource varchar(256) not null", "agentname varchar(256) not null", "agentcycle bigint not null" ),
                this.createPrimaryKey( "agenthash" )
        );

        this.createTable(
                c_tableinconsistency,
                this.createTableFieldList( "agenthash int not null", "value double not null" ),
                this.createPrimaryKey( "agenthash" )
        );

        this.createTable(
                c_tablebeliefbase,
                this.createTableFieldList( "agenthash int not null", "beliefhash int not null", "belief longtext not null" ),
                this.createPrimaryKey( "agenthash", "beliefhash" )
        );

        this.createTable(
                c_fundamentaldata,
                this.createTableFieldList(
                        "edgeid bigint unsigned not null", "traffic_power double unsigned not null", "traffic_density double unsigned not null",
                        "harmonic_mean_speed double unsigned not null", "average_speed double unsigned not null"
                ),
                this.createPrimaryKey( "edgeid" )

        );
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * builds the table field list
     *
     * @param p_fields additional fields
     * @return array with field list
     */
    protected final String[] createTableFieldList( final String... p_fields )
    {
        return ArrayUtils.addAll(
                new String[]{
                        "instance char(36) not null",
                        "process bigint not null",
                        "run int unsigned not null",
                        "step int unsigned not null",
                }, p_fields
        );
    }

    /**
     * builds the primary key SQL call
     *
     * @param p_fields additional field list
     * @return string with SQL
     */
    protected final String createPrimaryKey( final String... p_fields )
    {
        return MessageFormat.format(
                "add primary key ({0})", String.join(
                        ", ", new LinkedList()
                        {{
                            add( "instance" );
                            add( "process" );
                            add( "run" );
                            add( "step" );
                            addAll( Arrays.asList( p_fields ) );
                        }}
                )
        );
    }

    /**
     * creates the insert statement
     *
     * @param p_tablename table name
     * @param p_fieldnumber number of fields
     * @return prepare statement
     *
     * @throws SQLException is thrown on error
     * @note the first two fields will be set with the instance definition
     */
    protected final PreparedStatement createInsertStatement( final String p_tablename, final int p_fieldnumber ) throws SQLException
    {
        final PreparedStatement l_statement = c_datasource.getConnection().prepareStatement(
                MessageFormat.format(
                        "insert ignore into {0} values ({1})", CEvaluationStore.this.getTableName( p_tablename ), StringUtils.repeat(
                                "?", ",", p_fieldnumber
                        )
                )
        );

        l_statement.setString( 1, CConfiguration.getInstance().get().<String>get( "uuid" ) );
        l_statement.setObject( 2, CConfiguration.getInstance().getProcessID(), Types.BIGINT );

        return l_statement;
    }


    /**
     * updates the current statement
     *
     * @param p_statement statement
     * @param p_currentstep current step
     * @throws SQLException is thrown on error
     * @note the second two fields are updates with the step definitions
     */
    private final void updateInsertStatement( final PreparedStatement p_statement, final int p_currentstep ) throws SQLException
    {
        p_statement.setInt( 3, CSimulation.getInstance().getNumberOfRuns() );
        p_statement.setInt( 4, p_currentstep );
    }


    /**
     * worker to collect inconsistency via reflection
     */
    protected class CCollectorInconsistency extends IDatabase.CWorker
    {
        /**
         * access to the field of the inconsistency layer
         **/
        private final CReflection.CGetSet m_access = CReflection.getClassField( CInconsistencyLayer.class, "m_data" );
        /**
         * prepare statement
         **/
        private final PreparedStatement m_statement = CEvaluationStore.this.createInsertStatement( c_tableinconsistency, 6 );


        /**
         * ctor
         *
         * @throws SQLException preparing throws exceptions
         */
        public CCollectorInconsistency() throws SQLException
        {
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            CEvaluationStore.this.updateInsertStatement( m_statement, p_currentstep );

            try
            {
                // get data via reflection and iterate over dataset
                ( ( (Map<IAgent<?>, Double>) m_access.getGetter().invoke(
                        CSimulation.getInstance().getWorld().<CInconsistencyLayer>getTyped( "Jason Car Inconsistency" )
                ) ).entrySet() ).parallelStream().forEach(
                        ( l_item ) ->
                        {
                            try
                            {
                                m_statement.setInt( 5, l_item.getKey().hashCode() );
                                m_statement.setDouble( 6, l_item.getValue() );
                                m_statement.execute();
                            }
                            catch ( final SQLException l_exception )
                            {
                                CLogger.error( l_exception );
                            }
                        }
                );

            }
            catch ( final Throwable p_throwable )
            {
                CLogger.error( p_throwable );
            }
        }
    }


    /**
     * worker to get beliefs of the agents
     */
    protected class CCollectorBelief extends IDatabase.CWorker
    {
        /**
         * prepare statement
         **/
        private final PreparedStatement m_statement = CEvaluationStore.this.createInsertStatement( c_tablebeliefbase, 7 );

        /**
         * ctor
         *
         * @throws SQLException preparing throws exceptions
         */
        public CCollectorBelief() throws SQLException
        {
        }

        @Override
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            CEvaluationStore.this.updateInsertStatement( m_statement, p_currentstep );

            CSimulation.getInstance().getWorld().<CCarJasonAgentLayer>getTyped( "Jason Car Agents" ).parallelStream().forEach(
                    ( l_agent ) ->
                    {
                        try
                        {
                            m_statement.setInt( 5, l_agent.hashCode() );

                            for ( final Iterator<? extends ILiteral<?>> l_iterator = l_agent.getBeliefBase().iteratorLiteral(); l_iterator.hasNext(); )
                            {
                                final ILiteral<?> l_literal = l_iterator.next();

                                m_statement.setInt( 6, l_literal.hashCode() );
                                m_statement.setString( 7, l_literal.toString() );
                                m_statement.execute();
                            }
                        }
                        catch ( final SQLException l_exception )
                        {
                            CLogger.error( l_exception );
                        }
                    }
            );
        }
    }


    /**
     * worker to get beliefs of the agents
     */
    protected class CCollectorAgent extends IDatabase.CWorker
    {
        /**
         * prepare statement
         **/
        private final PreparedStatement m_statement = CEvaluationStore.this.createInsertStatement( c_tableagent, 8 );

        /**
         * ctor
         *
         * @throws SQLException preparing throws exceptions
         */
        public CCollectorAgent() throws SQLException
        {
        }

        @Override
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            CEvaluationStore.this.updateInsertStatement( m_statement, p_currentstep );

            CSimulation.getInstance().getWorld().<CCarJasonAgentLayer>getTyped( "Jason Car Agents" ).parallelStream().forEach(
                    ( l_agent ) -> {
                        try
                        {
                            m_statement.setInt( 5, l_agent.hashCode() );

                            m_statement.setString( 6, l_agent.getSource() );
                            m_statement.setString( 7, l_agent.getName() );
                            m_statement.setInt( 8, l_agent.getCycle() );
                            m_statement.execute();

                        }
                        catch ( final SQLException l_exception )
                        {
                            CLogger.error( l_exception );
                        }
                    }
            );

        }
    }


    /**
     * worker to get data for fundamental diagram
     */
    protected class CCollectorFundamentalDiagram extends IDatabase.CWorker
    {
        /**
         * prepare statement
         **/
        private final PreparedStatement m_statement = CEvaluationStore.this.createInsertStatement( c_fundamentaldata, 9 );


        /**
         * ctor
         *
         * @throws SQLException preparing throws exceptions
         */
        public CCollectorFundamentalDiagram() throws SQLException
        {
        }

        @Override
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            CEvaluationStore.this.updateInsertStatement( m_statement, p_currentstep );
            final CCarLayer l_cars = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" );

            l_cars.getGraph().getEdgeCollection().parallelStream().filter( i -> i.getNumberOfObjects() > 0 ).forEach(
                    ( l_edge ) ->
                    {
                        final double l_carcount = l_edge.getNumberOfObjects();

                        // calculate sum over all speeds for averages
                        final double l_speedsum = l_edge.getCellList().stream().filter( i -> i != null ).mapToDouble( i -> i.getCurrentSpeed() ).sum();
                        final double l_speedsuminvert = l_edge.getCellList().stream().filter( i -> i != null ).mapToDouble( i -> 1.0 / i.getCurrentSpeed() )
                                                              .sum();

                        try
                        {
                            m_statement.setObject( 5, l_edge.getEdgeID(), Types.BIGINT );

                            // traffic power = cars / time (in seconds)
                            m_statement.setDouble( 6, l_carcount / l_cars.getUnitConvert().getTime() );
                            // traffic density = cars / distance (in meter)
                            m_statement.setDouble( 7, l_carcount / l_edge.getDistance() );

                            // harmonic mean speed = sum car speed / sum ( 1 / car speed )
                            m_statement.setDouble( 8, l_speedsum / l_speedsuminvert );
                            // average speed
                            m_statement.setDouble( 9, l_speedsum / l_carcount );

                            m_statement.execute();

                        }
                        catch ( final SQLException l_exception )
                        {
                            CLogger.error( l_exception );
                        }

                    }
            );
        }
    }

}
