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

package de.tu_clausthal.in.mec.ui;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.location.TreeLocationRoot;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * create the main frame of the simulation dockable component @see http://dock.javaforge.com/
 * @deprecated
 */
public class CFrame extends JFrame
{
    /**
     * control of the dock component
     */
    private CControl m_control = new CControl( this );
    /**
     * config file of the dockables
     */
    private File m_configfile = CConfiguration.getInstance().getConfigDir( "ui.xml" );
    /**
     * map with widgets
     */
    private Map<String, Component> m_widgets = new HashMap<>();

    /**
     * ctor with frame initialization
     */
    public CFrame()
    {
        super();

        if ( CConfiguration.getInstance().get().getResetui() )
            m_configfile.delete();
        CConfiguration.getInstance().get().setResetui( false );


        //COperatingSystem.setFrameProperties( this );
        this.setTitle( CConfiguration.getInstance().getManifest().get( "Project-Name" ) );
        this.setLayout( new BorderLayout() );
        this.setSize( CConfiguration.getInstance().get().getWindowwidth(), CConfiguration.getInstance().get().getWindowheight() );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( final WindowEvent p_event )
            {
                try
                {
                    m_control.writeXML( m_configfile );
                }
                catch ( IOException l_execption )
                {
                }

                CConfiguration.getInstance().get().setViewpoint( COSMViewer.getSimulationOSM().getCenterPosition() );
                CConfiguration.getInstance().get().setZoom( COSMViewer.getSimulationOSM().getZoom() );
                CConfiguration.getInstance().get().setWindowheight( p_event.getWindow().getHeight() );
                CConfiguration.getInstance().get().setWindowwidth( p_event.getWindow().getWidth() );
                CConfiguration.getInstance().write();
                p_event.getWindow().dispose();

                m_control.destroy();
            }
        } );
        this.add( m_control.getContentArea() );

        // UI loading can be run after the dock structure is full initialized
        try
        {
            //CBootstrap.afterFrameInit( this );
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
    private void createDockable( final String p_title, final Component p_panel, final CLocation p_location )
    {
        final DefaultSingleCDockable l_dock = new DefaultSingleCDockable( p_title, p_panel );
        m_widgets.put( p_title, p_panel );
        m_control.addDockable( l_dock );
        l_dock.setLocation( p_location );
        l_dock.setVisible( true );
        l_dock.setCloseable( false );
        l_dock.setTitleText( p_title );
    }

    /**
     * add component to the frame
     *
     * @param p_name  dockname
     * @param p_panel component
     */
    public final void addWidget( final String p_name, final Component p_panel )
    {
        if ( ( p_name == null ) || ( p_panel == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "namepanelempty" ) );

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
    public final void addWidget( final String p_name, final Component p_panel, final Position p_position, final double p_size )
    {
        if ( ( p_name == null ) || ( p_panel == null ) || ( p_name.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "namepanelempty" ) );

        TreeLocationRoot l_position = null;
        switch ( p_position )
        {
            case BOTTOM:
                l_position = CLocation.base().normalSouth( p_size );
                break;
            case TOP:
                l_position = CLocation.base().normalNorth( p_size );
                break;
            case RIGHT:
                l_position = CLocation.base().normalEast( p_size );
                break;
            case LEFT:
                l_position = CLocation.base().normalWest( p_size );
                break;
            default:
        }
        this.createDockable( p_name, p_panel, l_position );
    }

    /**
     * returns a widget
     *
     * @param p_name name of the widget
     * @return null or component
     */
    public final Component getWidget( final String p_name )
    {
        return m_widgets.get( p_name );
    }

    /**
     * checks if a widget exists
     *
     * @param p_name name of the widget
     * @return boolean existence
     */
    public final boolean containsWidget( final String p_name )
    {
        return m_widgets.containsKey( p_name );
    }

    /**
     * removes the widget
     *
     * @param p_name name of the widget
     * @return component
     */
    public final Component removeWidget( final String p_name )
    {
        final Component l_component = m_widgets.remove( p_name );
        if ( l_component != null )
            m_control.removeDockable( m_control.getSingleDockable( p_name ) );
        return l_component;
    }

    /**
     * returns all widgets
     *
     * @return widget collection
     */
    public final Collection<Component> getWidgetsComponent()
    {
        return m_widgets.values();
    }

    /**
     * returns a set of all widget names
     *
     * @return name set
     */
    public final Set<String> getWidgetNames()
    {
        return m_widgets.keySet();
    }

    /**
     * returns the entry set of all widgets
     *
     * @return widget entry set
     */
    public final Set<Map.Entry<String, Component>> getWidgetEntrySet()
    {
        return m_widgets.entrySet();
    }

    /**
     * enum of widget position *
     */
    public enum Position
    {
        BOTTOM, TOP, RIGHT, LEFT
    }

}
