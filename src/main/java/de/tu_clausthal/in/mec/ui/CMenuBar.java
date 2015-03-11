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

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CCommonUI;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import de.tu_clausthal.in.mec.object.source.CSourceLayer;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * class for create the menubar
 * @deprecated
 */
public class CMenuBar extends JMenuBar implements ActionListener, ChangeListener
{
    /**
     * menu item storage
     */
    protected CMenuStorage m_items = new CMenuStorage();
    /**
     * current directory for file dialogs
     */
    protected File m_filepath = new File( System.getProperty( "user.dir" ) );
    /**
     * help dialog
     */
    //protected CHelpViewer m_help = new CHelpViewer( CSimulation.getInstance().getUI() );


    /**
     * ctor with menu items
     */
    public CMenuBar()
    {
        super();

        this.buildFileMenu();
        this.buildSimulationMenu();
        this.buildLayerMenu();
        this.buildCarSourcesMenu();
        this.buildGraphWeightsMenu();
        this.buildDrivingModelMenu();

        this.refreshDynamicItems();

        // get main menus to define order in the UI
        for ( String l_root : new String[]{"File", "Simulation", "Layer", "Car Sources", "Graph Weights", "Driving Model", "MAS"} )
            this.add( m_items.get( l_root ) );

/*
        String[] l_institution = {"Create Institution", "Delete Institution"};
        this.add(CMenuFactory.createMenu("Institution", l_institution, this, m_reference));

        String[] l_norm = {"Create Speed Norm", "Delete Norm"};
        this.add(CMenuFactory.createMenu("Norm", l_norm, this, m_reference));
*/

    }

    /**
     * builds the 'File' menu according to selected language
     */
    private void buildFileMenu()
    {
        m_items.addItem( new ImmutablePair<>( "File", CCommon.getResourceString( this, "File" ) ),
                new LinkedList<ImmutablePair<String, String>>()
                {{
                        add( new ImmutablePair<>( "About", CCommon.getResourceString( CMenuBar.this, "About" ) ) );
                        add( null );
                        add( new ImmutablePair<>( "Help", CCommon.getResourceString( CMenuBar.this, "Help" ) ) );
                        add( new ImmutablePair<>( "Preferences", CCommon.getResourceString( CMenuBar.this, "Preferences" ) ) );
                        add( null );
                        add( new ImmutablePair<>( "Load", CCommon.getResourceString( CMenuBar.this, "Load" ) ) );
                        add( new ImmutablePair<>( "Save", CCommon.getResourceString( CMenuBar.this, "Save" ) ) );
                        add( null );
                        add( new ImmutablePair<>( "Screenshot", CCommon.getResourceString( CMenuBar.this, "Screenshot" ) ) );
                    }},
                this
        );
    }

    /**
     * builds the 'Simulation' menu according to selected language
     */
    private void buildSimulationMenu()
    {
        m_items.addItem( new ImmutablePair<>( "Simulation", CCommon.getResourceString( CMenuBar.this, "Simulation" ) ),
                new LinkedList<ImmutablePair<String, String>>()
                {{
                        add( new ImmutablePair<>( "Start", CCommon.getResourceString( CMenuBar.this, "Start" ) ) );
                        add( new ImmutablePair<>( "Stop", CCommon.getResourceString( CMenuBar.this, "Stop" ) ) );
                        add( null );
                        add( new ImmutablePair<>( "Reset", CCommon.getResourceString( CMenuBar.this, "Reset" ) ) );
                        add( null );
                    }},
                this
        );

        buildSimulationSpeedSlider();
    }

    /**
     * adds a speed slider to the 'Simulation' menu
     */
    private void buildSimulationSpeedSlider()
    {
        m_items.addSlider( new ImmutablePair<>( "Simulation/Speed", CCommon.getResourceString( this, "Speed" ) ),
                150 - CConfiguration.getInstance().get().getThreadsleeptime(),
                CCommon.getResourceString( this, "slow" ),
                0,
                CCommon.getResourceString( this, "fast" ),
                150, this
        );
    }

    /**
     * builds the 'Layer' menu according to selected language
     */
    private void buildLayerMenu()
    {
        final String[] l_activity = new String[CSimulation.getInstance().getWorld().size()];
        CSimulation.getInstance().getWorld().keySet().toArray( l_activity );
        m_items.addRadioItems( new ImmutablePair<>( "Layer/Activity", CCommon.getResourceString( this, "Activity" ) ),
                l_activity,
                this
        );

        final List<String> l_visibility = new ArrayList<>();
        for ( Map.Entry<String, ILayer> l_item : CSimulation.getInstance().getWorld().entrySet() )
            if ( l_item.getValue() instanceof IViewableLayer )
                l_visibility.add( l_item.getKey() );

        m_items.addRadioItems( new ImmutablePair<>( "Layer/Visibility", CCommon.getResourceString( this, "Visibility" ) ),
                CCommon.CollectionToArray( String[].class, l_visibility ),
                this
        );
    }

    /**
     * builds 'Car Sources' menu according to selected language
     */
    private void buildCarSourcesMenu()
    {
        final String[] l_sources = ( (CSourceLayer) CSimulation.getInstance().getWorld().get( "Sources" ) ).getSourceNamesList();
        m_items.addRadioGroup( new ImmutablePair<>( "Car Sources", CCommon.getResourceString( this, "Car Sources" ) ),
                l_sources,
                this
        );

        ( (JRadioButtonMenuItem) m_items.get( "Car Sources/" + l_sources[0] ) ).setSelected( true );
    }

    /**
     * builds 'Graph Weights' menu according to selected language
     */
    private void buildGraphWeightsMenu()
    {
        m_items.addRadioItems( new ImmutablePair<>( "Graph Weights", CCommon.getResourceString( this, "Graph Weights" ) ),
                ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getGraph().getWeightingList(),
                this
        );
    }

    /**
     * builds 'Driving Model' menu according to selected language
     */
    private void buildDrivingModelMenu()
    {
        m_items.addRadioGroup( new ImmutablePair<>( "Driving Model", CCommon.getResourceString( this, "Driving Model" ) ),
                ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getDrivingModelList(),
                this
        );
    }


    @Override
    public void stateChanged( final ChangeEvent p_event )
    {
        CConfiguration.getInstance().get().setThreadsleeptime( ( (JSlider) p_event.getSource() ).getMaximum() - ( (JSlider) p_event.getSource() ).getValue() );
    }


    /**
     * refreshes the dynamic items *
     *
     * @bug does not work correct - enable / disable flags will be inconsistent on multiple clicks
     */
    private void refreshDynamicItems()
    {
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Layer/Activity" ) )
            ( (JRadioButtonMenuItem) l_item.getValue() ).setSelected( CSimulation.getInstance().getWorld().get( l_item.getKey().getSuffix() ).isActive() );

        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Layer/Visibility" ) )
            ( (JRadioButtonMenuItem) l_item.getValue() ).setSelected( ( (IViewableLayer) CSimulation.getInstance().getWorld().get( l_item.getKey().getSuffix() ) ).isVisible() );


        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Driving Model" ) )
            ( (JRadioButtonMenuItem) l_item.getValue() ).setSelected( ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getDrivingModel().equals( l_item.getKey().getSuffix() ) );

        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Graph Weights" ) )
            ( (JRadioButtonMenuItem) l_item.getValue() ).setSelected( ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getGraphWeight().equals( l_item.getKey().getSuffix() ) );


        this.updateJasonMenu();
    }


    /**
     * create MAS-Jason menu
     */
    private void updateJasonMenu()
    {
        final List<String> l_menu = new ArrayList<>( Arrays.asList( IEnvironment.getAgentFiles() ) );
        l_menu.add( null );
        l_menu.add( "new agent" );
        l_menu.add( "check syntax" );

        final JComponent l_root = m_items.removeItems( "MAS/Jason" );
        m_items.addItem( new ImmutablePair<>( "MAS/Jason", CCommon.getResourceString( this, "Jason" ) ),
                CCommon.CollectionToArray( String[].class, l_menu ),
                this
        );
        if ( l_root != null )
            m_items.get( "MAS" ).remove( l_root );
    }


    /**
     * throws an exception if the simulation is running
     */
    private void throwSimulationRunningException()
    {
        if ( CSimulation.getInstance().isRunning() )
            throw new IllegalStateException( CCommon.getResourceString( this, "running" ) );
    }


    @Override
    public void actionPerformed( final ActionEvent p_event )
    {

        // refresh menubar reference within the editor window and refresh the data
        //( (CSourceEditor) CSimulation.getInstance().getUI().getWidget( "Editor" ) ).addActionListener( this );
        final CPath l_actionpath = m_items.get( (JComponent) p_event.getSource() );
        this.refreshDynamicItems();

        if ( l_actionpath == null )
            return;

        // the following items will be skipped
        if ( l_actionpath.getPath().startsWith( "Car Sources" ) )
            return;


        // file / simulation menu
        try
        {

            if ( "File/Screenshot".equals( l_actionpath.toString() ) )
            {
                this.screenshot();
                return;
            }
            if ( "File/Load".equals( l_actionpath.toString() ) )
            {
                this.load();
                return;
            }
            if ( "File/Save".equals( l_actionpath.toString() ) )
            {
                this.save();
                return;
            }
            if ( "File/Preferences".equals( l_actionpath.toString() ) )
            {
                this.preferences();
                return;
            }
            if ( "File/Help".equals( l_actionpath.toString() ) )
            {
                //m_help.setVisible( true );
                return;
            }
            if ( "File/About".equals( l_actionpath.toString() ) )
            {
                this.about();
                return;
            }

            if ( "Simulation/Start".equals( l_actionpath.toString() ) )
            {
                CSimulation.getInstance().start();
                return;
            }
            if ( "Simulation/Stop".equals( l_actionpath.toString() ) )
            {
                CSimulation.getInstance().stop();
                return;
            }
            if ( "Simulation/Reset".equals( l_actionpath.toString() ) )
            {
                CSimulation.getInstance().reset();
                return;
            }

        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
        }


        // layer action / visibility
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Layer/Activity" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                try
                {
                    this.throwSimulationRunningException();
                    final ILayer l_layer = CSimulation.getInstance().getWorld().get( l_item.getKey().getSuffix() );
                    l_layer.setActive( !l_layer.isActive() );
                }
                catch ( Exception l_exception )
                {
                    JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
                }
                return;
            }

        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Layer/Visibility" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                final IViewableLayer l_layer = (IViewableLayer) CSimulation.getInstance().getWorld().get( l_item.getKey().getSuffix() );
                l_layer.setVisible( !l_layer.isVisible() );
                return;
            }


        // driving models
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Driving Model" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                try
                {
                    this.throwSimulationRunningException();
                    ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).setDriveModel( l_item.getKey().getSuffix() );
                }
                catch ( Exception l_exception )
                {
                    JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
                }
                return;
            }


        // graph weights
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Graph Weights" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).enableDisableGraphWeight( l_item.getKey().getSuffix() );
                return;
            }

        // MAS Jason elements

        try
        {
            if ( "MAS/Jason/new agent".equals( l_actionpath.toString() ) )
            {
                final String l_name = CCommonUI.openTextInputDialog( CCommon.getResourceString( this, "jasoncreatetitle" ), CCommon.getResourceString( this, "jasoncreatedescription" ) );
                if ( ( l_name != null ) && ( !l_name.isEmpty() ) )
                    //( (CSourceEditor) CSimulation.getInstance().getUI().getWidget( "Editor" ) ).open( IEnvironment.createAgentFile( l_name ) );
                this.refreshDynamicItems();
                return;
            }

            if ( "MAS/Jason/check syntax".equals( l_actionpath ) )
            {
                for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "MAS/Jason" ) )
                    if ( ( !"MAS/Jason/check syntax".equals( l_item.getKey().toString() ) ) && ( !"MAS/Jason/new agent".equals( l_item.getKey().toString() ) ) )
                        IEnvironment.checkAgentFileSyntax( ( (JMenuItem) l_item.getValue() ).getText() );
                return;
            }

            for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "MAS/Jason" ) )
                if ( ( !"MAS/Jason/check syntax".equals( l_item.getKey().toString() ) ) && ( !"MAS/Jason/new agent".equals( l_item.getKey().toString() ) ) )
                {
                    //( (CSourceEditor) CSimulation.getInstance().getUI().getWidget( "Editor" ) ).open( IEnvironment.getAgentFile( ( (JMenuItem) l_item.getValue() ).getText() ) );
                    return;
                }
        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResourceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
        }

    }


    // --- External Caller ---------------------------------------------------------------------------------------------

    /**
     * returns the name of the selected source
     *
     * @return source name
     */
    public String getSelectedSourceName()
    {
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Car Sources" ) )
            if ( ( (JRadioButtonMenuItem) ( l_item.getValue() ) ).isSelected() )
                return l_item.getKey().getSuffix();

        return null;
    }


    // --- File Menu ---------------------------------------------------------------------------------------------------

    /**
     * handles the preferences
     */
    private void preferences()
    {
        final CConfigurationDialog l_dialog = new CConfigurationDialog( CSimulation.getInstance().getUI() );
        l_dialog.setVisible( true );
    }

    private void about()
    {
        final CAboutDialog l_about = new CAboutDialog( CSimulation.getInstance().getUI() );
        l_about.setVisible( true );
    }


    /**
     * stores the list of sources
     */
    private void save() throws IOException
    {
        final File l_store = CCommonUI.openFileSaveDialog( m_filepath, new String[][]{{".mecsim", "Mec-Simulation (MecSim)"}} );
        if ( l_store == null )
            return;

        try
        {
            CSimulation.getInstance().store( CCommon.addFileExtension( l_store, ".mecsim" ) );
        }
        catch ( Exception l_exception )
        {
            throw new IOException( CCommon.getResourceString( this, "serialization" ) );
        }

    }


    /**
     * loads the list of sources
     */
    private void load() throws IOException
    {
        final File l_load = CCommonUI.openFileLoadDialog( m_filepath, new String[][]{{".mecsim", "Mec-Simulation (MecSim)"}} );
        if ( l_load == null )
            return;

        try
        {
            CSimulation.getInstance().load( l_load );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( CCommon.getResourceString( this, "deserialization" ) );
        }
    }


    /**
     * create a screenshot of the OSM view
     */
    private void screenshot() throws IOException
    {
        final BufferedImage l_image = new BufferedImage( COSMViewer.getSimulationOSM().getWidth(), COSMViewer.getSimulationOSM().getHeight(), BufferedImage.TYPE_INT_RGB );
        COSMViewer.getSimulationOSM().paint( l_image.getGraphics() );

        final File l_store = CCommonUI.openFileSaveDialog( m_filepath, new String[][]{{".png", "Portable Network Graphics (PNG)"}} );
        if ( l_store == null )
            return;

        ImageIO.write( l_image, "png", CCommon.addFileExtension( l_store, ".png" ) );
    }

    // -----------------------------------------------------------------------------------------------------------------

}
