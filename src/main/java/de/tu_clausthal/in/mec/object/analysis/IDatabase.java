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
import de.tu_clausthal.in.mec.object.IMultiEvaluateLayer;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import org.apache.commons.dbcp2.BasicDataSource;

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
     * datasource connection pool (static context)
     */
    protected final static BasicDataSource c_datasource = new BasicDataSource()
    {{
        if ( isConnectable() )
        {
            setDriverClassName( CConfiguration.getInstance().get().<String>get( "database/driver" ) );
            setUrl( CConfiguration.getInstance().get().<String>get( "database/url" ) );
            setUsername( CConfiguration.getInstance().get().<String>get( "database/username" ) );
            setPassword( CConfiguration.getInstance().get().<String>get( "database/password" ) );
            setPoolPreparedStatements( true );
        }
    }};
    /**
     * flag to set connectivity
     */
    private final boolean m_connectable = isConnectable();
    /**
     * table prefix
     */
    private final String m_tableprefix = Optional.ofNullable( CConfiguration.getInstance().get().<String>get( "database/tableprefix" ) ).orElse( "" );

    /**
     * ctor - context initialization
     */
    public IDatabase()
    {
        m_active = m_connectable;
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
     * creates the table structure
     *
     * @param p_tablename table name
     * @param p_fields field list
     * @param p_alter optional table alter command
     */
    protected final void createTable( final String p_tablename, final String[] p_fields, final String p_alter )
    {
        this.createTable( p_tablename, p_fields, new String[]{p_alter} );
    }


    /**
     * creates the table structure
     *
     * @param p_tablename table name
     * @param p_fields field list
     * @param p_alter optional table alter commands
     */
    protected final void createTable( final String p_tablename, final String[] p_fields, final String[] p_alter )
    {
        final String l_tablename = this.getTableName( p_tablename );
        try (
                final Connection l_connect = c_datasource.getConnection()
        )
        {
            final ResultSet l_result = l_connect.getMetaData().getTables( null, null, l_tablename, new String[]{"TABLE"} );
            if ( !l_result.next() )
            {
                l_connect.createStatement().execute(
                        CLogger.info(
                                MessageFormat.format(
                                        "create table {0} ({1})", l_tablename, String.join( ", ", p_fields )
                                )
                        )
                );
                if ( p_alter != null )
                    for ( final String l_item : p_alter )
                        l_connect.createStatement().execute( CLogger.info( MessageFormat.format( "alter table {0} {1}", l_tablename, l_item ) ) );
            }
            l_result.close();
        }
        catch ( final Exception l_exception )
        {
            m_active = false;
            CLogger.error( l_exception );
        }
    }

    /**
     * returns the full table name
     *
     * @param p_tablename table name
     * @return full table name
     */
    protected final String getTableName( final String p_tablename )
    {
        return MessageFormat.format( "{0}{1}", m_tableprefix, p_tablename );
    }

    /**
     * worker class to push data to the database *
     */
    protected abstract static class CWorker implements IVoidSteppable
    {
        @Override
        public final void release()
        {
        }
    }

}
