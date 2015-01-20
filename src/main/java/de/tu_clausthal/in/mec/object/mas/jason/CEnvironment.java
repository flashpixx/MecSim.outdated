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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import jason.asSyntax.Structure;
import jason.environment.EnvironmentInfraTier;
import jason.runtime.RuntimeServicesInfraTier;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.*;


/**
 * class of the Jason environment
 *
 * @see http://jason.sourceforge.net/api/jason/environment/package-summary.html
 */
public class CEnvironment<T extends IStepable> extends IMultiLayer<CAgentContainer<T>> implements EnvironmentInfraTier
{

    /**
     * global literal storage *
     */
    protected CLiteralStorage m_global = new CLiteralStorage();
    /**
     * map with actions of the environment *
     */
    protected Map<String, Pair<Method, Object>> m_actions = new HashMap();


    /**
     * register object methods
     *
     * @param p_object object
     */
    public void registerObjectMethods( Object p_object )
    {
        m_actions.putAll( m_global.addObjectMethods( p_object ) );
    }


    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        m_global.add( "simulationstep", p_currentstep );
        m_global.addObjectFields( this );

    }

    @Override
    public void informAgsEnvironmentChanged( String... strings )
    {

    }

    @Override
    public void informAgsEnvironmentChanged( Collection<String> collection )
    {
    }

    @Override
    public RuntimeServicesInfraTier getRuntimeServices()
    {
        return null;
    }

    @Override
    public boolean isRunning()
    {
        return true;
    }

    @Override
    public void actionExecuted( String agName, Structure actTerm, boolean success, Object infraData )
    {
        if ( !success )
            return;

        for ( Map.Entry<String, Pair<Method, Object>> l_item : m_actions.entrySet() )
            if ( actTerm.getFunctor().equals( l_item.getKey() ) )
                CLogger.info( "agent [" + agName + "] runs method [" + l_item.getValue().getLeft().getName() + "] on object [" + l_item.getValue().getRight() + "]");
    }

}
