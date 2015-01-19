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
import jason.asSyntax.*;
import jason.environment.Environment;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


/**
 * class of the Jason environment
 *
 * @see http://jason.sourceforge.net/
 */
public class CEnvironment extends IMultiLayer<CAgent>
{

    protected transient Method[] m_method = null;
    protected transient CJasonEnvironmentWrapper m_jason = new CJasonEnvironmentWrapper();


    public CEnvironment( Class p_class )
    {
        if ( p_class == null )
            throw new IllegalArgumentException( "class definition need not to be null" );

        m_method = p_class.getMethods();
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        List<Literal> l_globalPercepts = new LinkedList();
        l_globalPercepts.add( ASSyntax.createLiteral( "simulationstep", ASSyntax.createNumber( p_currentstep ) ) );

        for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
            l_globalPercepts.addAll( CCommon.getLiteralList( l_layer.analyse() ) );

        // @todo l_globalPercept -> global environment ?
    }

    /**
     * class of the Jason environment
     */
    protected class CJasonEnvironmentWrapper<T> extends Environment
    {

        @Override
        public boolean executeAction( String p_agent, Structure p_action )
        {

            for ( Method l_method : m_method )
                if ( p_action.getFunctor().equals( l_method.getName() ) )
                {
                    CLogger.info( "agent [" + p_agent + "] run method [" + l_method.getName() + "]" );
                    return true;
                }

            return false;
        }

    }

}
