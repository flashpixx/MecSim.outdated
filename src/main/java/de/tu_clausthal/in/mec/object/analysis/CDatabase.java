/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.analysis;


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.object.IEvaluateLayer;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Map;


/**
 * class for writing data into a database
 *
 * @note JDBC driver is needed
 * @bug incomplete e.g. database creating are not exists
 * @see http://commons.apache.org/proper/commons-dbcp/
 */
public class CDatabase extends IEvaluateLayer
{
    /**
     * datasource *
     */
    protected BasicDataSource m_datasource = null;


    /**
     * ctor - context initialization
     */
    public CDatabase()
    {
        if ( !CConfiguration.getInstance().get().getDatabase().connectable() )
            return;


        m_datasource = new BasicDataSource();
        m_datasource.setDriverClassName( CConfiguration.getInstance().get().getDatabase().getDriver() );
        m_datasource.setUrl( CConfiguration.getInstance().get().getDatabase().getServer() );
        m_datasource.setUsername( CConfiguration.getInstance().get().getDatabase().getUsername() );
        m_datasource.setPassword( CConfiguration.getInstance().get().getDatabase().getPassword() );
    }

    @Override
    public int getCalculationIndex()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }
}
