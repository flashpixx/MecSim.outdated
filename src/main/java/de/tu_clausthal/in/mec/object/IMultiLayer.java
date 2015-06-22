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

package de.tu_clausthal.in.mec.object;

import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.ISerializable;
import de.tu_clausthal.in.mec.runtime.ISteppable;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * multilayer to create a collection for elements
 *
 * @tparam T object type of the layer
 */
public abstract class IMultiLayer<T extends ISteppable & Painter> implements Painter<COSMViewer>, Collection<T>, IViewableLayer, IVoidSteppable, ILayer, ISerializable
{
    /**
     * flag for activity
     */
    protected boolean m_active = true;
    /**
     * list of data items
     */
    protected final Queue<T> m_data = new ConcurrentLinkedDeque<>();
    /**
     * flag for visibility
     */
    protected boolean m_visible = true;


    /**
     * method which is called after the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object object
     */
    public abstract void afterStepObject( final int p_currentstep, final T p_object );

    /**
     * method which is called before the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object object
     */
    public abstract void beforeStepObject( final int p_currentstep, final T p_object );

    @Override
    public int getCalculationIndex()
    {
        return 0;
    }

    @Override
    public final boolean isActive()
    {
        return m_active;
    }

    @Override
    public final void setActive( final boolean p_active )
    {
        m_active = p_active;
    }

    @Override
    public final boolean isVisible()
    {
        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

        return m_visible;
    }

    @Override
    public final void setVisible( final boolean p_visible )
    {
        m_visible = p_visible;

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();
    }

    @Override
    public void onDeserializationComplete()
    {
        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().getCompoundPainter().addPainter(
                    (Painter) this
            );
    }

    @Override
    public void onDeserializationInitialization()
    {
        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().getCompoundPainter()
                       .removePainter(
                               (Painter) this
                       );
    }

    @Override
    public void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_visible )
            return;

        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        p_graphic.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( final T l_item : this )
            l_item.paint( p_graphic, p_viewer, p_width, p_height );
    }

    @Override
    public void release()
    {
        for ( final ISteppable l_item : m_data )
            l_item.release();
    }

    @Override
    public final int size()
    {
        return m_data.size();
    }

    @Override
    public final boolean isEmpty()
    {
        return m_data.isEmpty();
    }

    @Override
    public final boolean contains( final Object p_object )
    {
        return m_data.contains( p_object );
    }

    @Override
    public final Iterator<T> iterator()
    {
        return m_data.iterator();
    }

    @Override
    public final Object[] toArray()
    {
        return m_data.toArray();
    }

    @Override
    public final <S> S[] toArray( final S[] p_value )
    {
        return m_data.toArray( p_value );
    }

    @Override
    public final boolean add( final T p_value )
    {
        final boolean l_return = m_data.add( p_value );

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

        return l_return;
    }

    @Override
    public final boolean remove( final Object p_object )
    {
        final boolean l_result = m_data.remove( p_object );

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

        return l_result;
    }

    @Override
    public final boolean containsAll( final Collection<?> p_collection )
    {
        for ( final Object l_item : p_collection )
            if ( !m_data.contains( l_item ) )
                return false;

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

        return true;
    }

    @Override
    public final boolean addAll( final Collection<? extends T> p_collection )
    {
        final boolean l_return = m_data.addAll( p_collection );

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

        return l_return;
    }

    @Override
    public final boolean removeAll( final Collection<?> p_collection )
    {
        for ( final Object l_item : p_collection )
        {
            if ( m_data.remove( l_item ) )
                continue;

            return false;
        }

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();

        return true;
    }

    @Override
    public final boolean retainAll( final Collection<?> p_collection )
    {
        return m_data.retainAll( p_collection );
    }

    @Override
    public final void clear()
    {
        m_data.clear();

        if ( CSimulation.getInstance().getStorage().exists() )
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().repaint();
    }

    @Override
    public abstract void step( final int p_currentstep, final ILayer p_layer );

}
