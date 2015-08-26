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

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;


/**
 * statistic evaluation access
 * @see http://db.apache.org/ddlutils/
 */
public class CEvaluationStore extends IDatabase
{

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    @Override
    protected String getTableName()
    {
        return "statistic";
    }

    @Override
    protected String[] getTableFields()
    {
        return new String[]{
                "instance char(36) not null",
                "process binary(128) not null"
        };
    }

    @Override
    protected String[] getTableAlter()
    {
        return new String[]{"add primary key (instance, process)"};
    }

    /**
     * worker to get all information via reflection
     */
    protected class CCollector extends IDatabase.CWorker
    {

        @Override
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {

        }

    }
}
