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
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiEvaluateLayer;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Optional;


/**
 * class for writing data into a database
 *
 * @note JDBC driver is needed
 * @see http://commons.apache.org/proper/commons-dbcp/
 */
public abstract class IDatabase extends IMultiEvaluateLayer<IDatabase.CWorker>
{
    /**
     * flag to set connectivity
     */
    private final boolean m_connectable = isConnectable();
    /**
     * datasource *
     */
    private final BasicDataSource m_datasource = new BasicDataSource();

    /**
     * ctor - context initialization
     */
    public IDatabase()
    {
        m_active = m_connectable;
        if ( !m_connectable )
            return;

        m_datasource.setDriverClassName( CConfiguration.getInstance().get().<String>get( "database/driver" ) );
        m_datasource.setUrl( CConfiguration.getInstance().get().<String>get( "database/url" ) );
        m_datasource.setUsername( CConfiguration.getInstance().get().<String>get( "database/username" ) );
        m_datasource.setPassword( CConfiguration.getInstance().get().<String>get( "database/password" ) );

        this.createTable();
    }

    @Override
    public final int getCalculationIndex()
    {
        return Integer.MAX_VALUE;
    }

    /**
     * check if database is connectable
     *
     * @return boolean of connectivity
     */
    protected static boolean isConnectable()
    {
        final String l_driver = CConfiguration.getInstance().get().<String>get( "database/driver" );
        final String l_url = CConfiguration.getInstance().get().<String>get( "database/url" );
        if ( ( l_driver == null ) || ( l_driver.isEmpty() ) || ( l_url == null ) || ( l_url.isEmpty() ) )
            return false;

        return CConfiguration.getInstance().get().<Boolean>get( "database/active" );
    }

    /**
     * returns the table name
     *
     * @return table name
     */
    protected abstract String getTableName();

    /**
     * returns the field list with field attributes
     *
     * @return fields
     */
    protected abstract String[] getTableFields();

    /**
     * returns optional alter definitions
     *
     * @return null or alter definitions
     */
    protected abstract String[] getTableAlter();

    /**
     * creates the table structure
     */
    private void createTable()
    {
        final String l_tablename = MessageFormat.format(
                "{0}{1}", Optional.ofNullable( CConfiguration.getInstance().get().<String>get( "database/tableprefix" ) ).orElse( "" ), this.getTableName()
        );
        try (
                final Connection l_connect = m_datasource.getConnection()
        )
        {
            final ResultSet l_result = l_connect.getMetaData().getTables( null, null, l_tablename, new String[]{"TABLE"} );
            if ( !l_result.next() )
            {
                l_connect.createStatement().execute(
                        CLogger.info(
                                MessageFormat.format(
                                        "create table {0} ({1})", l_tablename, StringUtils.join(
                                                this.getTableFields(), ","
                                        )
                                )
                        )
                );
                final String[] l_alter = this.getTableAlter();
                if ( l_alter != null )
                    for ( final String l_item : l_alter )
                        l_connect.createStatement().execute( CLogger.info( MessageFormat.format( "alter table {0} {1}", l_tablename, l_item ) ) );
            }
            l_result.close();
        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    /**
     * worker class to push data to the database *
     */
    protected class CWorker implements IVoidSteppable
    {

        @Override
        public final void release()
        {
        }

        @Override
        public final void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
        }
    }

}
