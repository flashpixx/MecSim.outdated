/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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

package de.tu_clausthal.in.mec.runtime.message;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;


/**
 * default event message
 *
 * @tparam T type of the message data
 */
public class CMessage<T> implements IMessage<T>
{
    /**
     * data of the message
     */
    private final T m_data;
    /**
     * source of the message
     */
    private final CPath m_source;
    /**
     * title of the message
     */
    private final String m_title;
    /**
     * time-to-live value *
     */
    private int m_ttl = 10;

    /**
     * ctor - creates a message with data and name
     *
     * @param p_source path of the source
     * @param p_title title of the message
     */
    public CMessage( final CPath p_source, final String p_title )
    {
        if ( ( p_source == null ) || ( p_source.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "sourcenull" ) );
        if ( ( p_title == null ) || ( p_title.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "titlenull" ) );

        m_source = p_source;
        m_title = p_title;
        m_data = null;
    }

    /**
     * ctor - creates a message with data and name
     *
     * @param p_source path of the source
     * @param p_title title of the message
     * @param p_data data of the message
     */
    public CMessage( final CPath p_source, final String p_title, final T p_data )
    {
        if ( ( p_source == null ) || ( p_source.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "sourcenull" ) );
        if ( ( p_title == null ) || ( p_title.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "titlenull" ) );

        m_source = p_source;
        m_title = p_title;
        m_data = p_data;
    }

    @Override
    public final T getData()
    {
        return m_data;
    }

    @Override
    public final CPath getSource()
    {
        return m_source;
    }

    @Override
    public final String getTitle()
    {
        return m_title;
    }

    @Override
    public final int ttl()
    {
        return m_ttl--;
    }
}
