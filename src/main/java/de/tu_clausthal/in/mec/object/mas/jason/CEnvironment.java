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
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.simulation.IStepable;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * class of the Jason environment - the task of this class is the communication with outside
 * data (IO with other structure), in this context the environment class encapsulate all
 * behaviour inside, because it will be triggerd from the simulation core thread
 *
 * @see http://jason.sourceforge.net/api/jason/environment/package-summary.html
 */
public class CEnvironment<T extends IStepable> extends IMultiLayer<CAgentArchitecture<T>>
{

    /**
     * global literal storage *
     */
    protected CLiteralStorage m_percepts = new CLiteralStorage();
    /**
     * map with actions of the environment *
     */
    protected Map<String, Pair<Method, Object>> m_actions = new HashMap();

    /**
     * the centralised runner defines a structure, that the environment can communicate with the agent-architecture
     * structure to tranfer any data from / to the environment to the agent structure
     */
    //protected RunCentralisedMAS m_mas = new CRuntimeService();
    public CEnvironment()
    {
        m_actions = m_percepts.addObjectMethods( this );

        //Agent x = new Agent();
        //x.parseAS(  )

        new CAgentArchitecture<ICar>();
    }


    /**
     * register object methods
     *
     * @param p_object object
     */
    public void registerObjectMethods( Object p_object )
    {
        m_actions.putAll( m_percepts.addObjectMethods( p_object ) );
    }


    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        // get all data for global perceptions (get analyse function and all properties of the object
        m_percepts.addAll( this.analyse() );
        m_percepts.addObjectFields( this );
        m_percepts.add( "simulationstep", p_currentstep );


        // on each step the world can change - so inform all agents that there can be changes
        //m_mas.getEnvironmentInfraTier().informAgsEnvironmentChanged();
    }

}
