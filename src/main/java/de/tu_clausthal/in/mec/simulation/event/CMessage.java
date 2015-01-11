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

package de.tu_clausthal.in.mec.simulation.event;

import java.util.UUID;


/**
 * default event message
 */
public class CMessage<T> implements IMessage<T>
{
    /**
     * UUID of the message *
     */
    protected UUID m_id = UUID.randomUUID();
    /**
     * name of the message *
     */
    protected String m_name = m_id.toString();
    /**
     * data of the message *
     */
    protected T m_data = null;
    /**
     * source of the message *
     */
    protected IParticipant m_source = null;


    public CMessage( IParticipant p_source, T p_data )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( "source need not to be null" );

        m_data = p_data;
        m_source = p_source;
    }

    public CMessage( IParticipant p_source, String p_name, T p_data )
    {
        if ( p_source == null )
            throw new IllegalArgumentException( "source need not to be null" );

        m_data = p_data;
        m_source = p_source;
        m_name = p_name;
    }


    @Override
    public T getData()
    {
        return m_data;
    }

    @Override
    public UUID getID()
    {
        return m_id;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public IParticipant getSource()
    {
        return m_source;
    }
}
