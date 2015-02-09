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

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.*;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import de.tu_clausthal.in.mec.object.source.CSourceFactoryLayer;
import de.tu_clausthal.in.mec.simulation.CSimulation;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


/**
 * class for create the menubar
 *
 * @todo add multilanguage support
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
    protected CHelpViewer m_help = new CHelpViewer( CSimulation.getInstance().getUI() );


    /**
     * ctor with menu items
     */
    public CMenuBar()
    {
        super();

        m_items.addItem( "File", new String[]{"Help", "Preferences", null, "Load", "Save", null, "Screenshot"}, this );

        m_items.addItem( "Simulation", new String[]{"Start", "Stop", null, "Reset", null}, this );
        m_items.addSlider( "Simulation/Speed", 150 - CConfiguration.getInstance().get().getThreadsleeptime(), "slow", 0, "fast", 150, this );

        m_items.addRadioGroup( "Graph Weights", ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getGraph().getWeightingList(), this );
        m_items.addRadioGroup( "Driving Model", ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getDrivingModelList(), this );


        String[] l_activity = new String[CSimulation.getInstance().getWorld().size()];
        CSimulation.getInstance().getWorld().keySet().toArray( l_activity );
        m_items.addRadioItems( "Layer/Activity", l_activity, this );


        List<String> l_visibility = new ArrayList();
        for ( Map.Entry<String, ILayer> l_item : CSimulation.getInstance().getWorld().entrySet() )
            if ( l_item.getValue() instanceof IViewableLayer )
                l_visibility.add( l_item.getKey() );
        m_items.addRadioItems( "Layer/Visibility", CCommon.ColletionToArray( String[].class, l_visibility ), this );


        String[] l_sources = ( (CSourceFactoryLayer) CSimulation.getInstance().getWorld().get( "Sources" ) ).getSourceNamesList();
        m_items.addRadioGroup( "Car Sources", l_sources, this );
        ( (JRadioButtonMenuItem) m_items.get( "Car Sources/" + l_sources[0] ) ).setSelected( true );

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

    @Override
    public void stateChanged( ChangeEvent e )
    {
        CConfiguration.getInstance().get().setThreadsleeptime( ( (JSlider) e.getSource() ).getMaximum() - ( (JSlider) e.getSource() ).getValue() );
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


        this.MASJasonMenu();
    }


    /**
     * create MAS-Jason menu
     */
    private void MASJasonMenu()
    {
        List<String> l_menu = new ArrayList( Arrays.asList( IEnvironment.getAgentFiles() ) );
        l_menu.add( null );
        l_menu.add( "new agent" );
        l_menu.add( "check syntax" );

        JComponent l_root = m_items.removeItems( "MAS/Jason" );
        m_items.addItem( "MAS/Jason", CCommon.ColletionToArray( String[].class, l_menu ), this );
        if ( l_root != null )
            m_items.get( "MAS" ).remove( l_root );
    }


    /**
     * throws an exception if the simulation is running
     */
    private void throwSimulationRunningException()
    {
        if ( CSimulation.getInstance().isRunning() )
            throw new IllegalStateException( CCommon.getResouceString( this, "running" ) );
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {

        // refresh menubar reference within the editor window and refresh the data
        ( (CSourceEditor) CSimulation.getInstance().getUI().getWidget( "Editor" ) ).addActionListener( this );
        CPath l_actionpath = m_items.get( (JComponent) e.getSource() );
        this.refreshDynamicItems();

        if ( l_actionpath == null )
            return;

        // the following items will be skipped
        if ( l_actionpath.getPath().startsWith( "Car Sources" ) )
            return;


        // file / simulation menu
        try
        {

            if ( l_actionpath.equals( "File/Screenshot" ) )
            {
                this.screenshot();
                return;
            }
            if ( l_actionpath.equals( "File/Load" ) )
            {
                this.load();
                return;
            }
            if ( l_actionpath.equals( "File/Save" ) )
            {
                this.save();
                return;
            }
            if ( l_actionpath.equals( "File/Preferences" ) )
            {
                this.preferences();
                return;
            }
            if ( l_actionpath.equals( "File/Help" ) )
            {
                m_help.setVisible( true );
                return;
            }

            if ( l_actionpath.equals( "Simulation/Start" ) )
            {
                CSimulation.getInstance().start();
                return;
            }
            if ( l_actionpath.equals( "Simulation/Stop" ) )
            {
                CSimulation.getInstance().stop();
                return;
            }
            if ( l_actionpath.equals( "Simulation/Reset" ) )
            {
                CSimulation.getInstance().reset();
                return;
            }

        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResouceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
        }


        // layer action / visibility
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Layer/Activity" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                try
                {
                    this.throwSimulationRunningException();
                    ILayer l_layer = CSimulation.getInstance().getWorld().get( l_item.getKey().getSuffix() );
                    l_layer.setActive( !l_layer.isActive() );
                }
                catch ( Exception l_exception )
                {
                    JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResouceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
                }
                return;
            }

        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Layer/Visibility" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                IViewableLayer l_layer = (IViewableLayer) CSimulation.getInstance().getWorld().get( l_item.getKey().getSuffix() );
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
                    JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResouceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
                }
                return;
            }


        // graph weights
        for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "Graph Weights" ) )
            if ( l_actionpath.equals( l_item.getKey() ) )
            {
                try
                {
                    this.throwSimulationRunningException();
                    ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).setGraphWeight( l_item.getKey().getSuffix() );
                }
                catch ( Exception l_exception )
                {
                    JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResouceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
                }
                return;
            }

        // MAS Jason elements

        try
        {
            if ( l_actionpath.equals( "MAS/Jason/new agent" ) )
            {
                String l_name = CCommonUI.openTextInputDialog( CCommon.getResouceString( this, "jasoncreatetitle" ), CCommon.getResouceString( this, "jasoncreatedescription" ) );
                if ( ( l_name != null ) && ( !l_name.isEmpty() ) )
                    ( (CSourceEditor) CSimulation.getInstance().getUI().getWidget( "Editor" ) ).open( IEnvironment.createAgentFile( l_name ) );
                this.refreshDynamicItems();
                return;
            }

            if ( l_actionpath.equals( "MAS/Jason/check syntax" ) )
            {
                for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "MAS/Jason" ) )
                    if ( ( !l_item.getKey().equals( "MAS/Jason/check syntax" ) ) && ( !l_item.getKey().equals( "MAS/Jason/new agent" ) ) )
                        IEnvironment.checkAgentFileSyntax( ( (JMenuItem) l_item.getValue() ).getText() );
                return;
            }

            for ( Map.Entry<CPath, JComponent> l_item : m_items.entrySet( "MAS/Jason" ) )
                if ( ( !l_item.getKey().equals( "MAS/Jason/check syntax" ) ) && ( !l_item.getKey().equals( "MAS/Jason/new agent" ) ) )
                {
                    ( (CSourceEditor) CSimulation.getInstance().getUI().getWidget( "Editor" ) ).open( IEnvironment.getAgentFile( ( (JMenuItem) l_item.getValue() ).getText() ) );
                    return;
                }
        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), CCommon.getResouceString( this, "warning" ), JOptionPane.CANCEL_OPTION );
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
        CConfigurationDialog l_dialog = new CConfigurationDialog( CSimulation.getInstance().getUI() );
        l_dialog.setVisible( true );
    }


    /**
     * stores the list of sources
     */
    private void save() throws IOException
    {
        File l_store = CCommonUI.openFileSaveDialog( m_filepath, new String[][]{{".mecsim", "Mec-Simulation (MecSim)"}} );
        if ( l_store == null )
            return;

        try (
                FileOutputStream l_stream = new FileOutputStream( CCommon.addFileExtension( l_store, ".mecsim" ) );
                ObjectOutputStream l_output = new ObjectOutputStream( l_stream );
        )
        {
            CSimulation.getInstance().store( l_output );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( "error on serialization process" );
        }

    }


    /**
     * loads the list of sources
     */
    private void load() throws IOException
    {
        File l_load = CCommonUI.openFileLoadDialog( m_filepath, new String[][]{{".mecsim", "Mec-Simulation (MecSim)"}} );
        if ( l_load == null )
            return;

        try (
                FileInputStream l_stream = new FileInputStream( l_load );
                ObjectInputStream l_input = new ObjectInputStream( l_stream );
        )
        {
            CSimulation.getInstance().load( l_input );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( "error on deserialization process" );
        }
    }


    /**
     * create a screenshot of the OSM view
     */
    private void screenshot() throws IOException
    {
        BufferedImage l_image = new BufferedImage( COSMViewer.getSimulationOSM().getWidth(), COSMViewer.getSimulationOSM().getHeight(), BufferedImage.TYPE_INT_RGB );
        COSMViewer.getSimulationOSM().paint( l_image.getGraphics() );

        File l_store = CCommonUI.openFileSaveDialog( m_filepath, new String[][]{{".png", "Portable Network Graphics (PNG)"}} );
        if ( l_store == null )
            return;

        ImageIO.write( l_image, "png", CCommon.addFileExtension( l_store, ".png" ) );
    }

    // -----------------------------------------------------------------------------------------------------------------

}
