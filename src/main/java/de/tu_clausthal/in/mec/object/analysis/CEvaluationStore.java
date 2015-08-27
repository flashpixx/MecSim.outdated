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

package de.tu_clausthal.in.mec.object.analysis;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CInconsistencyLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;


/**
 * statistic evaluation access
 *
 * @see http://db.apache.org/ddlutils/
 */
public class CEvaluationStore extends IDatabase
{
    /**
     * table name
     **/
    private static String c_tablename = "inconsistency";

    /**
     * ctor
     */
    public CEvaluationStore()
    {
        super();

        // create worker
        try
        {
            m_data.add( new CCollectorInconsistency() );
        }
        catch ( final SQLException l_exception )
        {
            CLogger.error( l_exception );
        }

        // create table structures
        this.createTable(
                c_tablename,
                new String[]{
                        "instance char(36) not null",
                        "process bigint not null",
                        "run int unsigned not null",
                        "step int unsigned not null",
                        "agenthash int not null",
                        "agentsource varchar(256) not null",
                        "value double not null"
                },
                new String[]{
                        "add primary key (instance, process, run, step, agenthash)"
                }
        );
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * worker to inconsistency via reflection
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
        private final PreparedStatement m_statement;


        /**
         * ctor - set prepare statement with fixed values
         *
         * @throws SQLException preparing throws exceptions
         */
        public CCollectorInconsistency() throws SQLException
        {
            m_statement = c_datasource.getConnection().prepareStatement(
                    "insert into " + CEvaluationStore.this.getTableName( c_tablename ) + " values ( ?, ?,  ?, ?,  ?, ?, ? )"
            );

            m_statement.setString( 1, CConfiguration.getInstance().get().<String>get( "uuid" ) );
            m_statement.setObject( 2, CConfiguration.getInstance().getProcessID(), Types.BIGINT );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            try
            {
                // set iteration-fixed values within the statement
                m_statement.setInt( 3, CSimulation.getInstance().getNumberOfRuns() );

                // get data via reflection and iterate over dataset
                for ( final Map.Entry<IAgent<?>, Double> l_item : ( (Map<IAgent<?>, Double>) m_access.getGetter().invoke(
                        CSimulation.getInstance().getWorld().<CInconsistencyLayer>getTyped( "Jason Car Inconsistency" )
                ) ).entrySet() )
                {
                    m_statement.setInt( 4, p_currentstep );
                    m_statement.setInt( 5, l_item.hashCode() );
                    m_statement.setString( 6, l_item.getKey().getSource() );
                    m_statement.setDouble( 7, l_item.getValue() );

                    m_statement.execute();
                }

            }
            catch ( final Throwable p_throwable )
            {
                CLogger.error( p_throwable );
            }
        }

    }
}
