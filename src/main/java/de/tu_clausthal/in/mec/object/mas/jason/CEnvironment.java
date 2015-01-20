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
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.IStepable;
import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.EnvironmentInfraTier;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * class of the Jason environment
 *
 * @see http://jason.sourceforge.net/
 */
public class CEnvironment<T extends IStepable> extends IMultiLayer<CAgent<T>>
{

    /**
     * map with action of the environment
     */
    protected transient Map<Method, Object> m_action = new HashMap();
    /**
     * Jason environment object
     */
    protected transient CJasonEnvironment m_jason = new CJasonEnvironment();


    /**
     * registered from each object all public methods for environment actions
     *
     * @param p_object any object
     */
    public void registerAction( Object p_object )
    {
        for ( Method l_item : p_object.getClass().getDeclaredMethods() )
        {
            if ( m_action.containsKey( l_item.getName() ) )
            {
                CLogger.error( "method [" + l_item.getName() + "] is registered with the object [" + m_action.get( l_item.getName() ) + "] - skipped" );
                continue;
            }

            // only public method can be registred for Jason actions
            if ( Modifier.isPublic( l_item.getModifiers() ) )
                m_action.put( l_item, p_object );
        }
    }


    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        List<Literal> l_globalPercepts = new LinkedList();
        l_globalPercepts.add( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep ) ) );

        for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
            l_globalPercepts.addAll( CCommon.getLiteralList( l_layer.analyse() ) );

        // clear all perceptions and renew the perception data
        m_jason.clearAllPercepts();
        for ( Literal l_percept : l_globalPercepts )
            m_jason.addPercept( l_percept );

        m_jason.step();
    }

    /**
     * class of the Jason environment
     * @note the environment must be full-redesigned because the Jason
     * environment is a fixed implemented class with thread-pool
     * structure. A manual calling interface does not exist
     * so we create a own Jason environment to use it with
     * the simulation
     */
    protected class CJasonEnvironment
    {
        // centerialized
        protected EnvironmentInfraTier m_infrastructure = null;


        public void addPercept( Literal p_literal )
        {}

        public void clearAllPercepts()
        {}

        public boolean executeAction( String p_agent, Structure p_action )
        {
            for ( Map.Entry<Method, Object> l_item : m_action.entrySet() )
                if ( p_action.getFunctor().equals( l_item.getKey().getName() ) )
                {
                    CLogger.info( "agent [" + p_agent + "] invoke method [" + l_item.getKey().getName() + "]" );
                    return true;
                }

            return false;
        }

        public void step()
        {

        }

    }

}
