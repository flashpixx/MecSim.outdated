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

package de.tu_clausthal.in.mec.object.car;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.inspector.CInspector;
import de.tu_clausthal.in.mec.ui.inspector.IInspector;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;


/**
 * class for a default car *
 */
public class CDefaultCar extends IInspector implements ICar
{

    /**
     * random interface
     */
    protected Random m_random = new Random();
    /**
     * geo position of the start
     */
    protected GeoPosition m_StartPosition = null;
    /**
     * geo position of the end
     */
    protected GeoPosition m_EndPosition = null;
    /**
     * current speed
     */
    protected int m_speed = 0;
    /**
     * maximum speed definition
     */
    protected int m_maxSpeed = 200;
    /**
     * linger probability value
     */
    protected double m_LingerProbability = 0;
    /**
     * cell structure of the route
     */
    protected ArrayList<Pair<EdgeIteratorState, Integer>> m_route = null;
    /**
     * current position on the route
     */
    protected int m_routeindex = 0;
    /**
     * boolean flag for end reached
     */
    protected boolean m_endReached = false;
    /**
     * individual acceleration
     */
    protected int m_acceleration = 1;
    /**
     * individual deceleration *
     */
    protected int m_deceleration = 1;
    /**
     * reference to the graph
     */
    protected CGraphHopper m_graph = ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getGraph();


    /**
     * ctor to create the initial values
     *
     * @param p_StartPosition start positions (position of the source)
     */
    public CDefaultCar( GeoPosition p_StartPosition )
    {
        if ( p_StartPosition == null )
            throw new IllegalArgumentException( "startposition need not be null" );

        m_StartPosition = p_StartPosition;
        m_LingerProbability = m_random.nextDouble();
        while ( m_speed < 50 )
            m_speed = m_random.nextInt( m_maxSpeed );
        m_acceleration = m_random.nextInt( 40 ) + 20;
        m_deceleration = m_random.nextInt( 40 ) + 20;

        // we try to find a route within the geo data, so we get a random end position and try to calculate a
        // route between start and end position, so if an exception is cached, we create a new end position
        while ( true )
        {
            try
            {
                m_EndPosition = new GeoPosition( m_StartPosition.getLatitude() + m_random.nextDouble() - 0.5, m_StartPosition.getLongitude() + m_random.nextDouble() - 0.5 );
                List<List<EdgeIteratorState>> l_route = m_graph.getRoutes( m_StartPosition, m_EndPosition, 1 );
                if ( ( l_route != null ) && ( l_route.size() > 0 ) )
                {
                    m_route = m_graph.getRouteCells( l_route.get( 0 ) );
                    break;
                }
            }
            catch ( Exception l_exception )
            {
            }
        }
    }


    @Override
    public int getMaximumSpeed()
    {
        return m_maxSpeed;
    }


    @Override
    public int getCurrentSpeed()
    {
        return m_speed;
    }


    @Override
    public void setCurrentSpeed( int p_speed )
    {
        m_speed = Math.min( Math.max( p_speed, 15 ), m_maxSpeed );
    }


    @Override
    public double getLingerProbability()
    {
        return m_LingerProbability;
    }

    @Override
    public void reroute()
    {
        List<List<EdgeIteratorState>> l_route = m_graph.getRoutes( this.getGeoposition(), m_EndPosition, 1 );
        if ( ( l_route != null ) && ( l_route.size() > 0 ) )
        {
            if ( m_routeindex < m_route.size() - 1 )
                m_route.subList( m_routeindex + 1, m_route.size() ).clear();
            m_route.addAll( m_graph.getRouteCells( l_route.get( 0 ) ) );
        }
    }

    @Override
    public GeoPosition getGeoposition()
    {
        EdgeIteratorState l_edge = this.getEdge();
        if ( l_edge == null )
            return null;
        return m_graph.getEdge( l_edge ).getGeoposition( this );
    }

    @Override
    public boolean hasEndReached()
    {
        return m_endReached;
    }

    @Override
    public Map<Integer, ICar> getPredecessor()
    {
        Map<Integer, ICar> l_predecessordistance = new HashMap();

        for ( int i = m_routeindex + 1; ( i < m_route.size() ); i++ )
        {
            ICar l_object = (ICar) m_graph.getEdge( m_route.get( i ).getLeft() ).getObject( m_route.get( i ).getRight() );
            if ( l_object != null )
                l_predecessordistance.put( i - m_routeindex, l_object );
        }

        return l_predecessordistance;
    }

    @Override
    public EdgeIteratorState getEdge()
    {
        return this.getEdge( m_routeindex );
    }

    @Override
    public int getAcceleration()
    {
        return m_acceleration;
    }

    @Override
    public int getDeceleration()
    {
        return m_deceleration;
    }

    /**
     * returns the edge from an index
     *
     * @param p_index index
     * @return null or edge
     */
    private EdgeIteratorState getEdge( int p_index )
    {
        if ( m_route == null )
            return null;

        return p_index < m_route.size() ? m_route.get( p_index ).getLeft() : null;
    }

    /**
     * returns the icon size
     *
     * @param viewer viewer object
     * @return circle size
     */
    private int iconsize( JXMapViewer viewer )
    {
        return Math.max( 9 - viewer.getZoom(), 2 );
    }

    @Override
    public void onClick( MouseEvent e, JXMapViewer viewer )
    {
        GeoPosition l_position = this.getGeoposition();
        if ( l_position == null )
            return;

        int l_zoom = this.iconsize( viewer );
        Point2D l_point = viewer.getTileFactory().geoToPixel( l_position, viewer.getZoom() );
        Ellipse2D l_circle = new Ellipse2D.Double( l_point.getX() - viewer.getViewportBounds().getX(), l_point.getY() - viewer.getViewportBounds().getY(), l_zoom, l_zoom );

        if ( l_circle.contains( e.getX(), e.getY() ) )
            ( (CInspector) CSimulation.getInstance().getUI().getWidget( "Inspector" ) ).set( this );
    }

    @Override
    public Map<String, Object> inspect()
    {
        Map<String, Object> l_map = super.inspect();

        l_map.put( "current speed", m_speed );
        l_map.put( "maximum speed", m_maxSpeed );
        l_map.put( "acceleration", m_acceleration );
        l_map.put( "deceleration", m_deceleration );
        l_map.put( "start position", m_StartPosition );
        l_map.put( "end position", m_EndPosition );
        l_map.put( "street name", m_route.get( m_routeindex ).getLeft().getName() );
        l_map.put( "current geoposition", this.getGeoposition() );

        return l_map;
    }

    @Override
    public void paint( Graphics2D graphics2D, COSMViewer o, int i, int i2 )
    {
        GeoPosition l_position = this.getGeoposition();
        if ( l_position == null )
            return;

        int l_zoom = this.iconsize( o );
        Point2D l_point = o.getTileFactory().geoToPixel( l_position, o.getZoom() );

        // speed limit color defined with http://wiki.openstreetmap.org/wiki/File:Speed_limit_Germany.png
        graphics2D.setColor( Color.DARK_GRAY );
        if ( m_speed >= 50 )
            graphics2D.setColor( Color.MAGENTA );
        if ( m_speed >= 60 )
            graphics2D.setColor( Color.PINK );
        if ( m_speed >= 80 )
            graphics2D.setColor( Color.BLUE );
        if ( m_speed >= 100 )
            graphics2D.setColor( Color.CYAN );
        if ( m_speed >= 130 )
            graphics2D.setColor( Color.RED );

        graphics2D.fillOval( (int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom );

        // @todo draw route (solid for driven way, dashed for driving way)
    }


    @Override
    public void step( int p_currentstep, ILayer p_layer ) throws Exception
    {

        // if the car is at the end
        if ( this.hasEndReached() )
            return;

        // if the car reaches the end
        int l_speed = this.getCurrentSpeed();
        if ( m_routeindex + l_speed >= m_route.size() )
        {
            m_endReached = true;
            l_speed = m_route.size() - m_routeindex - 1;
        }


        // if the route index equal to zero, push it car on the first item or wait until it is free
        if ( m_routeindex == 0 )
        {

            if ( !m_graph.getEdge( m_route.get( l_speed ).getLeft() ).isEmpty( m_route.get( l_speed ).getRight().intValue() ) )
                return;

            try
            {
                m_graph.getEdge( m_route.get( l_speed ).getLeft() ).setObject( this, m_route.get( l_speed ).getRight().intValue() );
                m_routeindex += l_speed;
            }
            catch ( IllegalAccessException l_ex )
            {
            }

        }
        else
        {

            try
            {
                m_graph.getEdge( m_route.get( m_routeindex ).getLeft() ).removeObject( this );
                m_graph.getEdge( m_route.get( m_routeindex + l_speed ).getLeft() ).setObject( this, m_route.get( m_routeindex + l_speed ).getRight() );
                m_routeindex += l_speed;
            }
            catch ( IllegalAccessException l_ex )
            {
                m_graph.getEdge( m_route.get( m_routeindex ).getLeft() ).removeObject( this );
            }

        }

    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }
}
