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

package de.tu_clausthal.in.mec.ui;

import bibliothek.gui.dock.common.*;
import bibliothek.gui.dock.common.location.TreeLocationRoot;
import de.tu_clausthal.in.mec.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * create the main frame of the simulation dockable component @see http://dock.javaforge.com/
 */
public class CFrame extends JFrame
{

    /**
     * control of the dock component
     */
    private CControl m_control = new CControl( this );

    ;
    /**
     * config file of the dockables
     */
    private File m_configfile = new File( CConfiguration.getInstance().getConfigDir() + "/" + "ui.xml" );
    /**
     * map with widgets
     */
    private Map<String, Component> m_widgets = new HashMap();

    /**
     * ctor with frame initialization
     */
    public CFrame()
    {
        super();
        this.setTitle( "TU-Clausthal - MecSim Traffic Simulation" );
        this.setLayout( new BorderLayout() );

        this.setSize( CConfiguration.getInstance().get().WindowWidth, CConfiguration.getInstance().get().WindowHeight );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener( new WindowAdapter()
        {

            @Override
            public void windowClosing( WindowEvent l_event )
            {
                try
                {
                    m_control.writeXML( m_configfile );
                }
                catch ( IOException l_execption )
                {
                }

                CConfiguration.getInstance().get().ViewPoint = COSMViewer.getSimulationOSM().getCenterPosition();
                CConfiguration.getInstance().get().Zoom = COSMViewer.getSimulationOSM().getZoom();
                CConfiguration.getInstance().get().WindowHeight = l_event.getWindow().getHeight();
                CConfiguration.getInstance().get().WindowWidth = l_event.getWindow().getWidth();
                CConfiguration.getInstance().write();
                l_event.getWindow().dispose();

                m_control.destroy();
            }
        } );

        this.add( m_control.getContentArea() );

        // UI loading can be run after the dock structure is full initialized
        try
        {
            CBootstrap.AfterFrameInit( this );
            m_control.readXML( m_configfile );
        }
        catch ( Exception l_execption )
        {
            CLogger.error( l_execption );
        }

        // visibility and menubar must be run at the end, because of modification within the bootstrap call
        this.setJMenuBar( new CMenuBar() );
        this.setVisible( true );
    }

    /**
     * creates a single dockable and adds it to the dockable control
     *
     * @param p_title    unique title
     * @param p_panel    panel
     * @param p_location location of the panel
     */
    private void createDockable( String p_title, Component p_panel, CLocation p_location )
    {
        DefaultSingleCDockable l_dock = new DefaultSingleCDockable( p_title, p_title );
        l_dock.setTitleText( p_title );
        l_dock.setCloseable( false );
        l_dock.add( p_panel );
        l_dock.setLocation( p_location );
        m_control.addDockable( l_dock );
        l_dock.setVisible( true );
        m_widgets.put( p_title, p_panel );
    }

    /**
     * add component to the frame
     *
     * @param p_name  dockname
     * @param p_panel component
     */
    public void addWidget( String p_name, Component p_panel )
    {
        if ( ( p_name == null ) || ( p_panel == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( "name or panel are empty" );

        this.createDockable( p_name, p_panel, CLocation.base().minimalSouth() );
    }

    /**
     * add component to the frame
     *
     * @param p_name     dockname
     * @param p_panel    component
     * @param p_position position of the widget on the frame
     * @param p_size     size of the widget
     */
    public void addWidget( String p_name, Component p_panel, Position p_position, double p_size )
    {
        if ( ( p_name == null ) || ( p_panel == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( "name or panel are empty" );

        TreeLocationRoot l_position = null;
        switch ( p_position )
        {
            case SOUTH:
                l_position = CLocation.base().normalSouth( p_size );
                break;
            case NORTH:
                l_position = CLocation.base().normalNorth( p_size );
                break;
            case EAST:
                l_position = CLocation.base().normalEast( p_size );
                break;
            case WEST:
                l_position = CLocation.base().normalWest( p_size );
                break;
        }
        this.createDockable( p_name, p_panel, l_position );
    }

    /**
     * returns a widget
     *
     * @param p_name name of the widget
     * @return null or component
     */
    public Component getWidget( String p_name )
    {
        return m_widgets.get( p_name );
    }

    /**
     * checks if a widget exists
     *
     * @param p_name name of the widget
     * @return boolean existence
     */
    public boolean containsWidget( String p_name )
    {
        return m_widgets.containsKey( p_name );
    }

    /**
     * removes the widget
     *
     * @param p_name name of the widget
     * @return component
     */
    public Component removeWidget( String p_name )
    {
        Component l_component = m_widgets.remove( p_name );
        if ( l_component != null )
            m_control.removeDockable( m_control.getSingleDockable( p_name ) );
        return l_component;
    }

    /**
     * returns all widgets
     *
     * @return widget collection
     */
    public Collection<Component> getWidgetsComponent()
    {
        return m_widgets.values();
    }

    /**
     * returns a set of all widget names
     *
     * @return name set
     */
    public Set<String> getWidgetNames()
    {
        return m_widgets.keySet();
    }

    /**
     * returns the entry set of all widgets
     *
     * @return widget entry set
     */
    public Set<Map.Entry<String, Component>> getWidgetEntrySet()
    {
        return m_widgets.entrySet();
    }

    /**
     * enum of widget position *
     */
    public enum Position
    {
        SOUTH, NORTH, EAST, WEST
    }

}
