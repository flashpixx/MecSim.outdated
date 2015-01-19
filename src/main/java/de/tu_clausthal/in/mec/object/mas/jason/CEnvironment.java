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
import de.tu_clausthal.in.mec.simulation.CSimulation;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.environment.Environment;

import java.util.LinkedList;
import java.util.List;


/**
 * class of the Jason environment
 *
 * @see http://jason.sourceforge.net/
 */
public class CEnvironment extends IMultiLayer<CAgent>
{

    protected transient CJasonEnvironmentWrapper m_jason = new CJasonEnvironmentWrapper();



    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        List<Literal> l_globalPercepts = new LinkedList();
        l_globalPercepts.add( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep ) ) );

        for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
            l_globalPercepts.addAll( CCommon.getLiteralList( l_layer.analyse() ) );



        //this.processPerceptions();
        //this.processEnvironment();
        //this.processAgents();
    }

    /**
     * class of the Jason environment
     */
    protected class CJasonEnvironmentWrapper extends Environment
    {

    }

}
