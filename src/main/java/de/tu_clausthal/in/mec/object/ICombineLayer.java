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

package de.tu_clausthal.in.mec.object;


import de.tu_clausthal.in.mec.simulation.IVoidSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * combined layer to define block-structured layer data for parallizing / sequential object calculation
 */
public class ICombineLayer implements Painter<COSMViewer>, Collection<IMultiLayer<?>>, IViewableLayer, IVoidSteppable, ILayer
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * list of data items
     */
    protected final Map<String, IMultiLayer<?>> m_data = new ConcurrentHashMap<>();
    /**
     * flag for visibility
     */
    protected boolean m_visible = true;
    /**
     * flag for activity
     */
    protected boolean m_active = true;

    @Override
    public int size()
    {
        int l_size = 0;
        for ( IMultiLayer<?> l_item : m_data.values() )
            l_size += l_item.size();

        return l_size;
    }

    @Override
    public boolean isEmpty()
    {
        return m_data.isEmpty();
    }

    @Override
    public boolean contains( final Object p_key )
    {
        return m_data.containsKey( p_key );
    }

    @Override
    public Iterator<IMultiLayer<?>> iterator()
    {
        return m_data.values().iterator();
    }

    @Override
    public Object[] toArray()
    {
        return m_data.values().toArray();
    }

    @Override
    public <T> T[] toArray( final T[] p_value )
    {
        return m_data.values().toArray( p_value );
    }

    @Override
    public boolean add( final IMultiLayer<?> p_layer )
    {
        return false;
    }

    @Override
    public boolean remove( final Object p_key )
    {
        return m_data.remove( p_key ) != null;
    }

    @Override
    public boolean containsAll( final Collection<?> p_collection )
    {
        return m_data.values().containsAll( p_collection );
    }

    @Override
    public boolean addAll( final Collection<? extends IMultiLayer<?>> p_collection )
    {
        return false;
    }

    @Override
    public boolean removeAll( final Collection<?> p_collection )
    {
        return false;
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return false;
    }

    @Override
    public void clear()
    {
        m_data.clear();
    }

    @Override
    public boolean isActive()
    {
        return m_active;
    }

    @Override
    public void setActive( final boolean p_active )
    {
        m_active = p_active;
    }

    @Override
    public int getCalculationIndex()
    {
        return 0;
    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {

    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void release()
    {

    }

    @Override
    public boolean isVisible()
    {
        return m_visible;
    }

    @Override
    public void setVisible( final boolean p_visible )
    {
        m_visible = p_visible;
    }

    @Override
    public void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {

    }
}
