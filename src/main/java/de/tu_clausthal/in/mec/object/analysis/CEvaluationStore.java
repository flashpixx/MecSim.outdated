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
import java.util.Map;


/**
 * statistic evaluation access
 *
 * @see http://db.apache.org/ddlutils/
 */
public class CEvaluationStore extends IDatabase
{
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
                        "process bigint unsigned not null",
                        "run bigint unsigned not null",
                        "step bigint unsigned not null",
                        "agenthash bigint unsigned not null",
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
        private final PreparedStatement m_statement;


        /**
         * ctor
         *
         * @throws SQLException
         */
        public CCollectorInconsistency() throws SQLException
        {
            m_statement = c_datasource.getConnection().prepareStatement( "insert into ? values ( ?, ?, ?, ?, ?, ? )" );

            m_statement.setString( 1, CEvaluationStore.this.getTableName( c_tablename ) );
            m_statement.setString( 2, CConfiguration.getInstance().get().<String>get( "uuid" ) );
            m_statement.setObject( 3, CConfiguration.getInstance().getProcessID() );
        }

        @Override
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            try
            {
                m_statement.setObject( 4, CSimulation.getInstance().getNumberOfRuns() );

                for ( final Map.Entry<IAgent<?>, Double> l_item : ( (Map<IAgent<?>, Double>) m_access.getGetter().invoke(
                        CSimulation.getInstance().getWorld().<CInconsistencyLayer>getTyped( "Jason Car Inconsistency" )
                ) ).entrySet() )
                {
                    m_statement.setInt( 5, p_currentstep );
                    m_statement.setInt( 6, l_item.hashCode() );
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
