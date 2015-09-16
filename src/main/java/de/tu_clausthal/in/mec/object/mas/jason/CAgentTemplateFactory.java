/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.IAgentTemplateFactory;
import jason.JasonException;
import jason.architecture.MindInspectorWeb;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.PlanLibrary;
import jason.bb.BeliefBase;

import java.io.File;
import java.util.ArrayList;


/**
 * Jason agent template factory
 */
public class CAgentTemplateFactory extends IAgentTemplateFactory<Agent>
{


    @Override
    protected final CAgent clone( final Agent p_agent )
    {
        final CAgent l_agent = null;

        // --- initialize the agent, that is used within the simulation context ---
        l_agent.setTS( new TransitionSystem( this, null, null, p_architecture ) );
        l_agent.setBB( (BeliefBase) m_beliefbaserootmask );
        l_agent.setPL( new PlanLibrary() );
        l_agent.initDefaultFunctions();

        try
        {
            CReflection.getClassField( l_agent.getClass(), "initialGoals" ).getSetter().invoke( l_agent, new ArrayList<>() );
            CReflection.getClassField( l_agent.getClass(), "initialBels" ).getSetter().invoke( l_agent, new ArrayList<>() );

            // create internal actions map - reset the map and overwrite not useable actions with placeholder
            CReflection.getClassField( this.getClass(), "internalActions" ).getSetter().invoke( this, IEnvironment.INTERNALACTION );
        }
        catch ( final Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }

        this.load( p_asl.toString() );
        MindInspectorWeb.get().registerAg( this );


        return null;
    }

    @Override
    protected final Agent create( final File p_source )
    {
        final Agent l_agent = new Agent();
        try
        {
            l_agent.initAg( p_source.toString() );
        }
        catch ( final JasonException l_exception )
        {
            CLogger.error( l_exception );
        }

        return l_agent;
    }
}
