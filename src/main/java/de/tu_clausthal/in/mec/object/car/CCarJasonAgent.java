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

package de.tu_clausthal.in.mec.object.car;


import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.object.mas.jason.action.CMethodBind;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.HashSet;
import java.util.Set;


/**
 * agent car
 */
public class CCarJasonAgent extends CDefaultCar
{

    /**
     * agent object *
     */
    protected CAgent<CDefaultCar> m_agent = null;


    /**
     * ctor to create the initial values
     *
     * @param p_asl           agent ASL file
     * @param p_StartPosition start positions (position of the source)
     */
    public CCarJasonAgent( String p_asl, GeoPosition p_StartPosition )
    {
        super( p_StartPosition );

        try
        {

            m_agent = new CAgent( p_asl );

            // set forbidden elements for methods and properties
            Set<String> l_forbidden = new HashSet();
            l_forbidden.add( "m_agent" );
            l_forbidden.add( "m_graph" );
            l_forbidden.add( "m_inspect" );
            l_forbidden.add( "m_random" );
            l_forbidden.add( "m_endReached" );
            l_forbidden.add( "m_routeindex" );
            l_forbidden.add( "m_EndPosition" );
            l_forbidden.add( "m_StartPosition" );

            // add the belief bind to the agent
            de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind l_belief = new de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind( "self", this, l_forbidden );
            m_agent.getBelief().add( l_belief );

            // add the method bind to the agent
            CMethodBind l_method = new CMethodBind( "self", this );
            l_method.getForbidden( "self" ).addAll( l_forbidden );
            m_agent.getActions().put( "invoke", l_method );

            // add the pushback bind to the agent
            de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind l_pusback = new de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind( "self", this, l_forbidden );
            m_agent.getActions().put( "set", l_pusback );


            // add agent to layer
            ( (IMultiLayer) CSimulation.getInstance().getWorld().get( "Jason Car Agents" ) ).add( m_agent );

        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    @Override
    public void release()
    {
        super.release();
        if ( m_agent != null )
            m_agent.release();
    }
}
