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

package de.tu_clausthal.in.mec.object.waypoint.point;

import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.HashMap;
import java.util.Map;


/**
 * abstract class for a waypoint
 */
public abstract class IWayPointBase<T, P extends IFactory<T>, N extends IGenerator> extends IInspectorDefault implements IWayPoint<T>
{

    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * position of the source within the map
     */
    protected final GeoPosition m_position;
    /**
     * generator of this source
     */
    protected final N m_generator;
    /**
     * factory of this source
     */
    protected final P m_factory;
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( IWayPointBase.super.inspect() );
        }};


    /**
     * ctor - source is a target only
     *
     * @param p_position geoposition
     */
    public IWayPointBase( final GeoPosition p_position )
    {
        m_position = p_position;
        m_generator = null;
        m_factory = null;
    }

    /**
     * ctor - source generates elements
     *
     * @param p_position position
     * @param p_generator generator
     * @param p_factory factory
     */
    public IWayPointBase( final GeoPosition p_position, final N p_generator, final P p_factory )
    {
        m_position = p_position;
        m_generator = p_generator;
        m_factory = p_factory;

        if ( this.hasFactoryGenerator() )
        {
            m_inspect.putAll( m_generator.inspect() );
            m_inspect.putAll( m_factory.inspect() );
        }
    }

    @Override
    public boolean hasFactoryGenerator()
    {
        return ( m_generator != null ) && ( m_factory != null );
    }

    @Override
    public final GeoPosition getPosition()
    {
        return m_position;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

}
