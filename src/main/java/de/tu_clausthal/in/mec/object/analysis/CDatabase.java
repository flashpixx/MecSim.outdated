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
import de.tu_clausthal.in.mec.object.IEvaluateLayer;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;


/**
 * class for writing data into a database
 *
 * @bug incomplete e.g. table creating process does not exists exists
 * @todo add an example to write down data e.g. OD-matrix structure
 * @note JDBC driver is needed
 * @see http://commons.apache.org/proper/commons-dbcp/
 */
public class CDatabase extends IEvaluateLayer<CDatabase.CWorker>
{
    /**
     * datasource *
     */
    private final BasicDataSource m_datasource = new BasicDataSource();


    /**
     * ctor - context initialization
     */
    public CDatabase()
    {
        if ( !this.isConnectable() )
            return;

        m_datasource.setDriverClassName( CConfiguration.getInstance().get().<String>get( "database/driver" ) );
        m_datasource.setUrl( CConfiguration.getInstance().get().<String>get( "database/url" ) );
        m_datasource.setUsername( CConfiguration.getInstance().get().<String>get( "database/username" ) );
        m_datasource.setPassword( CConfiguration.getInstance().get().<String>get( "database/password" ) );

        this.createTableIfNotExists(
                "zonecount", "(step bigint(20) unsigned not null, zonegroup varchar(64) not null, zone varchar(64) not null, value double not null)",
                new String[]{"add primary key (step,zonegroup,zone)"}
        );
    }

    /**
     * check if database is connectable
     *
     * @return boolean of connectivity
     */
    private boolean isConnectable()
    {
        final String l_driver = CConfiguration.getInstance().get().<String>get( "database/driver" );
        final String l_url = CConfiguration.getInstance().get().<String>get( "database/url" );

        return CConfiguration.getInstance().get().<Boolean>get(
                "database/active"
        ) && ( l_driver != null ) && ( !l_driver.isEmpty() ) && ( l_url != null ) && ( !l_url.isEmpty() );
    }

    /**
     * creates the table structure
     *
     * @param p_tablename table name without prefix (will append automatically)
     * @param p_createsql create sql without "create >tablename<"
     * @param p_altertable alter sql statements, that will run after the create, also without "alter table <tablename>"
     * @todo check database independence
     */
    private void createTableIfNotExists( final String p_tablename, final String p_createsql, final String[] p_altertable )
    {
        final String l_table = CConfiguration.getInstance().get().<String>get(
                "database/tableprefix"
        ) == null ? p_tablename : CConfiguration.getInstance().get().<String>get( "database/tableprefix" ) + p_tablename;

        try (
                final Connection l_connect = m_datasource.getConnection();
        )
        {
            final ResultSet l_result = l_connect.getMetaData().getTables( null, null, l_table, new String[]{"TABLE"} );
            if ( !l_result.next() )
            {
                l_connect.createStatement().execute( "create table " + l_table + " " + p_createsql );
                for ( String l_item : p_altertable )
                    l_connect.createStatement().execute( "alter table " + l_table + " " + l_item );
            }
            l_result.close();
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    @Override
    public final int getCalculationIndex()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * worker class to push data to the database *
     */
    protected class CWorker implements IVoidSteppable
    {

        @Override
        public final void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
        }

        @Override
        public final Map<String, Object> analyse()
        {
            return null;
        }

        @Override
        public final void release()
        {
        }
    }

}
