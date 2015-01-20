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
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import de.tu_clausthal.in.mec.simulation.IVoidStepable;
import jason.architecture.AgArchInfraTier;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Message;
import jason.asSyntax.Literal;
import jason.runtime.RuntimeServicesInfraTier;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * class of a Jason agent architecture
 *
 * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
 */
public class CAgentContainer<T extends IStepable> implements AgArchInfraTier, IVoidStepable, Painter
{
    /**
     * path to Jason ASL files *
     */
    protected static String s_aslpath = CConfiguration.getInstance().getConfigDir() + File.separator + "mas" + File.separator + "json" + File.separator;
    /**
     * source object that is connect with the agents
     */
    protected T m_source = null;


    /**
     * ctor
     *
     * @param p_source source object of the agent
     */
    public CAgentContainer( T p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( "source value need not to be null" );

        m_source = p_source;
    }


    @Override
    public List<Literal> perceive()
    {
        return null;
    }

    @Override
    public void checkMail()
    {

    }

    @Override
    public void act( ActionExec actionExec, List<ActionExec> list )
    {

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

    }

    @Override
    public void broadcast( Message message ) throws Exception
    {

    }

    @Override
    public boolean isRunning()
    {
        return false;
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
}
