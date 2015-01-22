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

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;
import de.tu_clausthal.in.mec.simulation.event.IMessage;
import de.tu_clausthal.in.mec.simulation.event.IReceiver;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;


/**
 * class of a Jason agent architecture
 *
 * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
 */
public class CAgentArchitecture<T extends IStepable> implements IVoidStepable, Painter, IReceiver
{


    protected CLiteralStorage m_percepts;
    protected Map<String, Pair<Method, Object>> m_action;
    /**
     * source object that is connect with the agents
     */
    protected T m_source = null;

    /*
        http://jason.sourceforge.net/api/jason/asSemantics/Agent.html
        http://jason.sourceforge.net/api/jason/asSemantics/TransitionSystem.html

        Agent x = new Agent();
        x.initDefaultFunctions();

       	addBel(Literal bel)
        addInitialBel(Literal b)
        addInitialBelsInBB()
        addInitialGoal(Literal g)
        addInitialGoalsInTS()

        Agent create(AgArch arch, java.lang.String agClass, ClassParameters bbPars, java.lang.String asSrc, Settings stts)


        x.parseAS( new File( CConfiguration.getInstance().getMASDir() + File.separator + "test.asl") );
     */


    public CAgentArchitecture()
    {
        AgArch l_arch = new AgArch();

        // read all ASL files from the agent directors and build from each ASL file an agent
        for ( String l_asl : CConfiguration.getInstance().getMASDir().list( new WildcardFileFilter( "*.asl" ) ) )
            try
            {
                Agent.create( l_arch, CAgent.class.getName(), null, CConfiguration.getInstance().getMASDir( l_asl ).toString(), null );
            }
            catch ( Exception l_exception )
            {
                CLogger.error( l_exception );
            }
    }

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
