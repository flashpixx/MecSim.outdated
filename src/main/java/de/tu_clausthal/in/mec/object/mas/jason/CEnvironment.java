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
import jason.asSyntax.*;
import jason.environment.EnvironmentInfraTier;
import jason.runtime.RuntimeServicesInfraTier;

import java.util.*;


/**
 * class of the Jason environment
 *
 * @see http://jason.sourceforge.net/
 */
public class CEnvironment<T extends IStepable> extends IMultiLayer<CAgentContainer<T>>
{

    /**
     * global literal storage *
     */
    protected CLiteralStorage m_global = new CLiteralStorage();





    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        List<Literal> l_globalPercepts = new LinkedList();
        l_globalPercepts.add( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep ) ) );

        //for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
        //    l_globalPercepts.addAll( CLiteralStorage.getLiteralList( l_layer.analyse() ) );

        // clear all perceptions and renew the perception data
        //for ( Literal l_percept : l_globalPercepts )
        //    m_jason.addPercept( l_percept );


    }

    /**
     * wrapper class of a Jason environment
     * @see http://jason.sourceforge.net/api/jason/environment/package-summary.html
     */
    protected class CJasonEnvironmentInfraTier implements EnvironmentInfraTier
    {


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
        public void actionExecuted( String s, Structure structure, boolean b, Object o )
        {

        }
    }

}
