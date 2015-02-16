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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * layer with all sources
 */
public class CSourceFactoryLayer extends IMultiLayer<ISourceFactory>
{
    /**
     * serialize version ID *
     */
    static final long serialVersionUID = 1L;
    /**
     * Last Source which might get a Destination via "shift+double-left-click"
     */
    static ISourceFactory m_lastSource;
    /**
     * Map which maps the sources to a Destination
     */
    static Map<ISourceFactory, GeoPosition> m_destinationMap = new ConcurrentHashMap<>();


    /**
     * returns a list of source names
     *
     * @return source names
     */
    public String[] getSourceNamesList()
    {
        return new String[]{"Default", "Norm", "Jason Agent", "Profile"};
    }

    /**
     * creates a source
     *
     * @param p_name     name of the source type
     * @param p_position geo position of the source
     * @param p_varargs  optional arguments of the sources
     * @return source object
     * @todo add profile source
     */
    public ISourceFactory getSource( String p_name, GeoPosition p_position, Object... p_varargs )
    {
        if ( "Default".equals( p_name ) )
            return new CDefaultSourceFactory( p_position );
        if ( "Norm".equals( p_name ) )
            return new CNormSourceFactory( p_position );
        if ( ( "Jason Agent".equals( p_name ) ) && ( p_varargs != null ) && ( p_varargs.length > 0 ) )
            return new CJasonAgentSourceFactory( (String) p_varargs[0], p_position );
        //if ("Profile".equals( p_name ))
        //    return new CProfileSourceFactory( p_position );

        throw new IllegalArgumentException( CCommon.getResouceString( this, "nosource" ) );
    }

    @Override
    public int getCalculationIndex()
    {
        return 2;
    }

    /**
     * release overwrite, because all sources will be removed of the reset is called
     */
    @Override
    public void release()
    {

    }

    /**
     * add overwrite, beceause we need to safe the last source Source which was added
     * @param p_source
     * @return l_return
     */
    @Override
    public boolean add(ISourceFactory p_source)
    {
        boolean l_return = m_data.add(p_source);
        m_lastSource = p_source;

        try
        {
            COSMViewer.getSimulationOSM().repaint();
        }
        catch ( Exception l_exception )
        {
        }

        return l_return;
    }

    /**
     * Overwrite paint because we need to paint Destinations
     * @param g
     * @param object
     * @param width
     * @param height
     */
    public void paint( Graphics2D g, COSMViewer object, int width, int height )
    {
        if ( !m_visible )
            return;

        //Paint Data from the Source Layer (Sources)
        Rectangle l_viewportBounds = object.getViewportBounds();
        g.translate(-l_viewportBounds.x, -l_viewportBounds.y);
        for (ISourceFactory l_item : this )
            l_item.paint( g, object, width, height );

        //Paint Destinations
        for (ISourceFactory l_source : m_destinationMap.keySet()) {
            if(m_destinationMap.get(l_source) != null) {
                //Paint Destinations as Red Rectangles (Might be changed later)
                int l_zoom = Math.max(20 - object.getZoom(), 3);
                g.setColor(l_source.getColor());
                Point2D l_point = object.getTileFactory().geoToPixel(m_destinationMap.get(l_source), object.getZoom());
                g.fillRect((int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom);
                g.setColor(Color.BLACK);
                g.drawRect((int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom);
            }
        }

    }

    /**
     * Checks if there is an Source so the User is able to add a Destination
     * @return
     */
    public boolean checkForSource()
    {
        return m_lastSource != null;
    }

    /**
     * Method which adds a Destination to the Last Source
     * @param p_destination
     */
    public void addDestination(GeoPosition p_destination)
    {
        m_destinationMap.put(m_lastSource, p_destination);
        try
        {
            COSMViewer.getSimulationOSM().repaint();
        }
        catch ( Exception l_exception )
        {
        }
    }


}
