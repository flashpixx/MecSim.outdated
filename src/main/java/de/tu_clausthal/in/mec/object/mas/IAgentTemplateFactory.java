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


package de.tu_clausthal.in.mec.object.mas;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * storage to create templates of agents for avoid reparsing on instantiation
 */
public abstract class IAgentTemplateFactory<T>
{

    /**
     * thread-safe storage
     */
    private final Map<File, T> m_storage = new ConcurrentHashMap<>();


    /**
     * agent instantiate function
     *
     * @param p_source source of the agent
     * @return agent
     */
    public final <N> N instantiate( final File p_source )
    {
        final T l_agenttemplate = m_storage.getOrDefault( p_source, this.create( p_source ) );
        m_storage.putIfAbsent( p_source, l_agenttemplate );
        return this.clone( l_agenttemplate );
    }


    /**
     * clones the agent from template
     *
     * @param p_agent agent object, which should be cloned
     * @return cloned agent
     */
    protected abstract <N> N clone( final T p_agent );


    /**
     * builds the agent-template from source file
     *
     * @param p_source source file
     * @return agent template
     */
    protected abstract T create( final File p_source );

}
