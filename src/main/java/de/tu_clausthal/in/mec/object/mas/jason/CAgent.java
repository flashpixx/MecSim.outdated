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
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;


/**
 * class of a Jason agent
 *
 * @see http://jason.sourceforge.net/
 */
public class CAgent<T extends IStepable> extends AgArch implements IVoidStepable, Painter
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
     * methods of the source object to call it for Jason actions
     */
    protected Method[] m_sourcemethods = null;


    /**
     * ctor
     *
     * @param p_source source object of the agent
     */
    public CAgent( T p_source )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( "source value need not to be null" );

        m_source = p_source;
        m_sourcemethods = p_source.getClass().getDeclaredMethods();
    }


    /**
     * adds a new agent to the architecture
     *
     * @param p_name name of the agent
     */
    public void createAgent( String p_name )
    {
        // @todo filter for method names

        try
        {
            new CJasonAgentWrapper( s_aslpath + p_name.toLowerCase() + ".asl" );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    @Override
    public Map<String, Object> analyse()
    {
        return m_source.analyse();
    }

    @Override
    public void paint( Graphics2D graphics2D, Object o, int i, int i1 )
    {
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {
        List<Literal> l_localPercepts = new LinkedList();
        l_localPercepts.addAll( CCommon.getLiteralList( this.analyse() ) );

        // @todo object reference set to percepts ?
        //l_localPercepts.add( ASSyntax.createLiteral( "layer", ASSyntax.crea ) ) );
        //l_localPercepts.add( ASSyntax.createLiteral( "source", ASSyntax.crea ) ) );

        // @todo cycle here correct?
        this.getTS().reasoningCycle();
    }

    @Override
    public boolean canSleep()
    {
        return false;
    }

    @Override
    public boolean isRunning()
    {
        return true;
    }


    /**
     * a class of a Jason agent *
     */
    protected class CJasonAgentWrapper extends Agent
    {

        public CJasonAgentWrapper( String p_aslfile ) throws JasonException
        {
            new TransitionSystem( this, null, null, CAgent.this );
            this.initAg( p_aslfile );
        }

    }

}
