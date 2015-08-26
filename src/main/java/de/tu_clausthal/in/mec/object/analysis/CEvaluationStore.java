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

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CInconsistencyLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;

import java.util.Map;


/**
 * statistic evaluation access
 * @see http://db.apache.org/ddlutils/
 */
public class CEvaluationStore extends IDatabase
{
    /**
     * ctor
     */
    public CEvaluationStore()
    {
        super();
        m_data.add( new CCollectorInconsistency() );
    }

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
     * worker to inconsistency via reflection
     */
    protected static class CCollectorInconsistency extends IDatabase.CWorker
    {
        private final CReflection.CGetSet m_access = CReflection.getClassField( CInconsistencyLayer.class, "m_data" );


        @Override
        public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
        {
            try
            {
                final Map<IAgent<?>, Double> l_data = (Map<IAgent<?>, Double>) m_access.getGetter().invoke(
                        CSimulation.getInstance().getWorld().<CInconsistencyLayer>getTyped( "Jason Car Inconsistency" )
                );
                //System.out.println(l_data);
            }
            catch ( final Throwable p_throwable )
            {
                CLogger.error( p_throwable );
            }
        }

    }
}
