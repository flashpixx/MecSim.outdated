/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.object.world;

import de.tu_clausthal.in.winf.simulation.IStepable;
import de.tu_clausthal.in.winf.simulation.IVoidStepable;
import de.tu_clausthal.in.winf.ui.COSMViewer;
import de.tu_clausthal.in.winf.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * multilayer to create a collection for elements
 */
public abstract class IMultiLayer<T extends IStepable & Painter> implements Painter<COSMViewer>, Collection<T>, IViewableLayer, IDataLayer, IVoidStepable, ILayer
{

    /**
     * flag for visibility *
     */
    protected boolean m_visible = true;
    /**
     * flag for activity *
     */
    protected boolean m_active = true;
    /**
     * list of data items *
     */
    protected List<T> m_data = new LinkedList();


    @Override
    public boolean isActive()
    {
        return m_active;
    }

    @Override
    public void setActive( boolean p_active )
    {
        m_active = p_active;
    }

    @Override
    public void resetData()
    {

    }

    @Override
    public boolean isVisible()
    {
        COSMViewer.getInstance().repaint();
        return m_visible;
    }

    @Override
    public void setVisible( boolean p_visible )
    {
        m_visible = p_visible;
        COSMViewer.getInstance().repaint();
    }

    @Override
    public Map<String, Object> getData()
    {
        return null;
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
    }

    /**
     * method which is called before the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object      object
     */
    public void beforeStepObject( int p_currentstep, T p_object )
    {
    }

    /**
     * method which is called after the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object      object
     */
    public void afterStepObject( int p_currentstep, T p_object )
    {
    }

    @Override
    public int size()
    {
        return m_data.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_data.isEmpty();
    }

    @Override
    public boolean contains( Object o )
    {
        return m_data.contains( o );
    }

    @Override
    public Iterator<T> iterator()
    {
        return m_data.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return m_data.toArray();
    }

    @Override
    public <S> S[] toArray( S[] a )
    {
        return m_data.toArray( a );
    }

    @Override
    public boolean add( T t )
    {
        boolean l_return = m_data.add( t );
        COSMViewer.getInstance().repaint();

        return l_return;
    }

    @Override
    public boolean remove( Object o )
    {
        return m_data.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c )
    {
        for ( Object l_item : c )
            if ( !m_data.contains( l_item ) )
            {
                COSMViewer.getInstance().repaint();
                return false;
            }

        COSMViewer.getInstance().repaint();
        return true;
    }

    @Override
    public boolean addAll( Collection<? extends T> c )
    {
        boolean l_return = m_data.addAll( c );
        COSMViewer.getInstance().repaint();

        return l_return;
    }

    @Override
    public boolean removeAll( Collection<?> c )
    {
        for ( Object l_item : c )
        {
            if ( m_data.remove( l_item ) )
                continue;
            COSMViewer.getInstance().repaint();
            return false;
        }

        COSMViewer.getInstance().repaint();
        return true;
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return m_data.retainAll( c );
    }

    @Override
    public void clear()
    {
        m_data.clear();
        COSMViewer.getInstance().repaint();
    }


    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }


    @Override
    public synchronized void paint( Graphics2D g, COSMViewer object, int width, int height )
    {
        if ( !m_visible )
            return;

        Rectangle l_viewportBounds = object.getViewportBounds();
        g.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( T l_item : this )
            l_item.paint( g, object, width, height );
    }

}
