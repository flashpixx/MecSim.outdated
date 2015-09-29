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
 *
 * @tparam T agent template type
 */
public abstract class IAgentTemplateFactory<T, L>
{
    /**
     * thread-safe storage
     */
    private final Map<File, T> m_storage = new ConcurrentHashMap<>();


    /**
     * agent instantiate function
     *
     * @param p_source source of the agent
     * @param p_task optional tasks which runs over the agenttemplate
     * @param p_any any object definition
     * @return agent
     */
    public final <N> N instantiate( final File p_source, final ITask<T> p_task, final L... p_any ) throws Exception
    {
        final T l_agenttemplate = m_storage.getOrDefault( p_source, this.create( p_source ) );
        m_storage.putIfAbsent( p_source, l_agenttemplate );

        if ( p_task != null )
            p_task.performtemplate( l_agenttemplate );

        return this.clone( l_agenttemplate, p_any );
    }

    /**
     * clears the current cache
     */

    public final void clear()
    {
        m_storage.clear();
    }

    /**
     * clones the agent from template
     *
     * @param p_agent agent object, which should be cloned
     * @param p_any any object definition
     * @return cloned agent
     */
    protected abstract <N> N clone( final T p_agent, final L... p_any ) throws Exception;

    /**
     * builds the agent-template from source file
     *
     * @param p_source source file
     * @return agent template
     */
    protected abstract T create( final File p_source ) throws Exception;

    /**
     * task interface which to performtemplate action on the template
     *
     * @tparam <O> template agent type
     */
    public interface ITask<O>
    {

        /**
         * performtemplate method
         *
         * @param p_agent agent template
         */
        public void performtemplate( final O p_agent );

    }
}
