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


import de.tu_clausthal.in.mec.common.CCommon;
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
     *
     * @param p_args connection data (database driver name & connection URL needed)
     */
    public CDatabase( String... p_args )
    {
        if ( ( p_args == null ) || ( p_args.length < 2 ) )
            throw new IllegalArgumentException( CCommon.getResouceString( this, "argument" ) );

        m_datasource = new BasicDataSource();
        m_datasource.setDriverClassName( p_args[0] );
        m_datasource.setUrl( p_args[1] );

        if ( p_args.length < 4 )
        {
            m_datasource.setUsername( p_args[2] );
            m_datasource.setPassword( p_args[3] );
        }

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
