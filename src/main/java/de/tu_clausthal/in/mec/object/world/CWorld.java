/**
 * @cond
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
 **/

package de.tu_clausthal.in.mec.object.world;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * world layer collection
 */
public class CWorld implements Map<String, ILayer>, Serializable
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;

    /**
     * map with layer
     */
    protected Map<String, ILayer> m_layer = new HashMap<>();

    /**
     * ctor
     */
    public CWorld()
    {
        CBootstrap.AfterWorldInit( this );
    }


    /**
     * creates a list with the layer objects depends on the ordering value
     *
     * @return returns a list of layer
     */
    public List<ILayer> getOrderedLayer()
    {

        final MultiMap<Integer, ILayer> l_order = new MultiValueMap<>();
        for ( ILayer l_layer : m_layer.values() )
            l_order.put( l_layer.getCalculationIndex(), l_layer );

        // get key values and sort it
        final Object[] l_sortkeys = l_order.keySet().toArray();
        Arrays.sort( l_sortkeys );

        // build the list of the layer
        final ArrayList<ILayer> l_list = new ArrayList<>();
        for ( Object l_key : l_sortkeys )
            l_list.addAll( (Collection<ILayer>) l_order.get( l_key ) );

        return l_list;
    }

    @Override
    public int size()
    {
        return m_layer.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_layer.isEmpty();
    }

    @Override
    public boolean containsKey( final Object p_key )
    {
        return m_layer.containsKey( p_key );
    }

    @Override
    public boolean containsValue( final Object p_value )
    {
        return m_layer.containsValue( p_value );
    }

    @Override
    public ILayer get( final Object p_key )
    {
        final ILayer l_layer = m_layer.get( p_key );
        CLogger.warn( "layer [" + l_layer.toString() + "] not exists", l_layer == null );
        return l_layer;
    }

    @Override
    public ILayer put( final String p_key, final ILayer p_value )
    {
        return m_layer.put( p_key, p_value );
    }

    @Override
    public ILayer remove( final Object p_key )
    {
        return m_layer.remove( p_key );
    }

    @Override
    public void putAll( final Map<? extends String, ? extends ILayer> p_value )
    {
        m_layer.putAll( p_value );
    }

    @Override
    public void clear()
    {
        m_layer.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return m_layer.keySet();
    }

    @Override
    public Collection<ILayer> values()
    {
        return m_layer.values();
    }

    @Override
    public Set<Entry<String, ILayer>> entrySet()
    {
        return m_layer.entrySet();
    }
}
