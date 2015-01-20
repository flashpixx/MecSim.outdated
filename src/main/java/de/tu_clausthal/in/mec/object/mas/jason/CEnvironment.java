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

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import jason.asSemantics.ActionExec;
import jason.asSyntax.Structure;
import jason.environment.EnvironmentInfraTier;
import jason.infra.centralised.CentralisedAgArch;
import jason.infra.centralised.RunCentralisedMAS;
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

    protected RunCentralisedMAS m_runner = null;


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
    public void informAgsEnvironmentChanged( String... agents )
    {
        if ( agents.length == 0 )
            for ( CentralisedAgArch l_agentarchitecture : m_runner.getAgs().values() )
                l_agentarchitecture.wake();

        else
            for ( String l_agent : agents )
            {
                CentralisedAgArch l_agentarchitecture = m_runner.getAg( l_agent );
                if ( l_agentarchitecture != null )
                    l_agentarchitecture.wake();
            }
    }

    @Override
    public void informAgsEnvironmentChanged( Collection<String> agentsToNotify )
    {
        if ( agentsToNotify == null )
            informAgsEnvironmentChanged();

        else
            for ( String l_agent : agentsToNotify )
            {
                CentralisedAgArch l_agentarchitecture = m_runner.getAg( l_agent );
                if ( l_agentarchitecture != null )
                    l_agentarchitecture.wake();
            }
    }

    @Override
    public RuntimeServicesInfraTier getRuntimeServices()
    {
        return new CRuntimeService( m_runner );
    }

    @Override
    public boolean isRunning()
    {
        return true;
    }

    @Override
    public void actionExecuted( String agName, Structure actTerm, boolean success, Object infraData )
    {
        ActionExec l_action = (ActionExec) infraData;
        l_action.setResult( success );
        CentralisedAgArch l_agent = m_runner.getAg( agName );
        if ( l_agent != null )
            l_agent.actionExecuted( l_action );

    }

}
