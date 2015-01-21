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
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.*;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import de.tu_clausthal.in.mec.simulation.event.IReceiver;
import jason.architecture.AgArchInfraTier;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Message;
import jason.asSyntax.Literal;
import jason.runtime.RuntimeServicesInfraTier;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;


/**
 * class of a Jason agent architecture
 *
 * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
 */
public class CAgentArchitecture<T extends IStepable> implements AgArchInfraTier, IVoidStepable, Painter, IReceiver
{


    protected CLiteralStorage m_percepts;
    protected Map<String, Pair<Method, Object>> m_action;
    /**
     * source object that is connect with the agents
     */
    protected T m_source = null;


    /**
     * ctor
     *
     * @param p_source source object of the agent
     */
    public CAgentArchitecture( T p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( "source value need not to be null" );

        m_source = p_source;
    }


    @Override
    public List<Literal> perceive()
    {
        m_percepts.get();
    }

    @Override
    public void checkMail()
    {

    }

    @Override
    public void act( ActionExec actionExec, List<ActionExec> list )
    {
        for ( Map.Entry<String, Pair<Method, Object>> l_action : m_action.entrySet() )
            if ( actionExec.getActionTerm().getFunctor().equals( l_action.getKey() ) )
                try
                {
                    l_action.getValue().getLeft().invoke( l_action.getValue().getRight(), null );
                }
                catch ( Exception l_exception )
                {
                    CLogger.error( "agent action error [" + l_action.getKey() + "] on [" + l_action.getValue().getRight() + "]: " + l_exception.getMessage() );
                }

        list.add( actionExec );
    }

    @Override
    public boolean canSleep()
    {
        return false;
    }

    @Override
    public String getAgName()
    {
        return null;
    }


    @Override
    public void sendMsg( Message message ) throws Exception
    {
        CSimulation.getInstance().getEventManager().pushMessage( new CPath( message.getReceiver() ), new CAgentMessage( message ) );
    }

    @Override
    public void broadcast( Message message ) throws Exception
    {
        CSimulation.getInstance().getEventManager().pushMessage( new CPath(), new CAgentMessage( message ) );
    }

    @Override
    public boolean isRunning()
    {
        return true;
    }

    @Override
    public void sleep()
    {

    }

    @Override
    public void wake()
    {

    }

    @Override
    public RuntimeServicesInfraTier getRuntimeServices()
    {
        return null;
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {

    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void paint( Graphics2D graphics2D, Object o, int i, int i1 )
    {

    }

    @Override
    public void receiveMessage( Set<IMessage> p_messages )
    {
    }
}
