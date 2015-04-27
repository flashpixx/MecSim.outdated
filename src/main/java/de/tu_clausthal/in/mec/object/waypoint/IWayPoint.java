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

package de.tu_clausthal.in.mec.object.waypoint;

import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * abstract class for a waypoint
 */
public abstract class IWayPoint<T, P extends IFactory<T>, N extends IGenerator> extends IInspectorDefault implements IReturnSteppable<T>, Painter<COSMViewer>, Serializable
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
    private final Map<String, Object> m_inspect = new HashMap()
    {{
            putAll( IWayPoint.super.inspect() );
            putAll( m_generator != null ? m_generator.inspect() : null );
            putAll( m_factory != null ? m_factory.inspect() : null );
        }};


    /**
     * ctor - source is a target only
     *
     * @param p_position geoposition
     */
    public IWayPoint( final GeoPosition p_position )
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
    public IWayPoint( final GeoPosition p_position, final N p_generator, final P p_factory )
    {
        m_position = p_position;
        m_generator = p_generator;
        m_factory = p_factory;
    }

    /**
     * checks if a generator and factory exists
     *
     * @return boolean flag of existance
     */
    public boolean hasFactoryGenerator()
    {
        return ( m_generator != null ) && ( m_factory != null );
    }

    /**
     * returns the position
     *
     * @return geoposition of the source
     */
    public final GeoPosition getPosition()
    {
        return m_position;
    }

    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

}
