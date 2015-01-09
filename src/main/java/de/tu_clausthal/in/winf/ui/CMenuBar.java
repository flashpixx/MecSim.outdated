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

package de.tu_clausthal.in.winf.ui;

import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.CLogger;
import de.tu_clausthal.in.winf.object.car.CCarLayer;
import de.tu_clausthal.in.winf.object.norm.INormObject;
import de.tu_clausthal.in.winf.object.norm.institution.IInstitution;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.simulation.CSimulation;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


/**
 * class for create the menubar *
 */
public class CMenuBar extends JMenuBar implements ActionListener, ChangeListener
{

    /**
     * current directory for file dialogs *
     */
    private File m_filepath = new File( System.getProperty( "user.dir" ) );
    /**
     * map with object for action listener
     */
    private Map<String, Object> m_reference = new HashMap();
    /**
     * current weight
     */
    private String m_weight = "Default";
    /**
     * menu names of driving models
     */
    private String[] m_drivingmodelname = null;


    /**
     * ctor with menu items *
     */
    public CMenuBar()
    {
        super();

        String[] l_file = {"Load", "Save", null, "Screenshot"};
        this.add( CMenuFactory.createMenu( "File", l_file, this, m_reference ) );


        String[] l_actions = {"Start", "Stop", null, "Reset", null};
        JMenu l_simulation = CMenuFactory.createMenu( "Simulation", l_actions, this, m_reference );

        JSlider l_simulationspeed = new JSlider( 0, 150 );
        l_simulationspeed.addChangeListener( this );
        l_simulationspeed.setValue( l_simulationspeed.getMaximum() - Math.max( l_simulationspeed.getMinimum(), Math.min( l_simulationspeed.getMaximum(), CConfiguration.getInstance().get().ThreadSleepTime ) ) );
        l_simulation.add( l_simulationspeed );
        Hashtable<Integer, JLabel> l_simulationspeedlabel = new Hashtable();
        l_simulationspeedlabel.put( l_simulationspeed.getMinimum(), new JLabel( "slow" ) );
        l_simulationspeedlabel.put( l_simulationspeed.getMaximum(), new JLabel( "fast" ) );
        l_simulationspeedlabel.put( ( l_simulationspeed.getMaximum() - l_simulationspeed.getMinimum() ) / 2, new JLabel( "Speed" ) );
        l_simulationspeed.setLabelTable( l_simulationspeedlabel );
        l_simulationspeed.setPaintLabels( true );
        this.add( l_simulation );


        String[] l_layer = new String[CSimulation.getInstance().getWorld().size()];
        CSimulation.getInstance().getWorld().keySet().toArray( l_layer );
        JMenu l_visibilitylayer = new JMenu( "Layer" );
        l_visibilitylayer.add( CMenuFactory.createRadioMenu( "Activity", l_layer, this, m_reference ) );
        l_visibilitylayer.add( CMenuFactory.createRadioMenu( "Visibility", l_layer, this, m_reference ) );
        this.add( l_visibilitylayer );


        m_drivingmodelname = ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Car" ) ).getDrivingModelList();
        this.add( CMenuFactory.createRadioMenuGroup( "Driving Model", m_drivingmodelname, this, m_reference ) );
        for ( int i = 0; i < m_drivingmodelname.length; i++ )
            m_drivingmodelname[i] = "Driving Model::" + m_drivingmodelname[i];


/*

        String[] l_weights = {"Default", "Speed", "Traffic Jam", "Speed & Traffic Jam"};
        this.add(CMenuFactory.createRadioMenuGroup("Graph Weights", l_weights, this, m_reference));



        String[] l_institution = {"Create Institution", "Delete Institution"};
        this.add(CMenuFactory.createMenu("Institution", l_institution, this, m_reference));

        String[] l_norm = {"Create Speed Norm", "Delete Norm"};
        this.add(CMenuFactory.createMenu("Norm", l_norm, this, m_reference));
*/

        /*
        String[] l_mas = {"Modify Environment", "Create Agent", "Delete Agent", ""};
        this.add(CMenuFactory.createMenu("MAS", l_mas, this, m_reference));
        */

    }


    @Override
    public void stateChanged( ChangeEvent e )
    {
        CConfiguration.getInstance().get().ThreadSleepTime = ( (JSlider) e.getSource() ).getMaximum() - ( (JSlider) e.getSource() ).getValue();
    }


    /**
     * throws an exception if the simulation is running
     */
    private void throwSimulationRunningException()
    {
        if ( CSimulation.getInstance().isRunning() )
            throw new IllegalStateException( "simulation is running" );
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        try
        {

            if ( e.getSource() == m_reference.get( "File::Screenshot" ) )
                this.screenshot();
            if ( e.getSource() == m_reference.get( "File::Load" ) )
                this.load();
            if ( e.getSource() == m_reference.get( "File::Save" ) )
                this.save();


            if ( e.getSource() == m_reference.get( "Simulation::Start" ) )
                CSimulation.getInstance().start();
            if ( e.getSource() == m_reference.get( "Simulation::Stop" ) )
                CSimulation.getInstance().stop();
            if ( e.getSource() == m_reference.get( "Simulation::Reset" ) )
                CSimulation.getInstance().reset();


            if ( e.getSource() == m_reference.get( "Activity::Source" ) )
            {
                this.throwSimulationRunningException();
                ILayer l_layer = CSimulation.getInstance().getWorld().get( "Source" );
                l_layer.setActive( !l_layer.isActive() );
            }
            if ( e.getSource() == m_reference.get( "Activity::Car" ) )
            {
                this.throwSimulationRunningException();
                ILayer l_layer = CSimulation.getInstance().getWorld().get( "Car" );
                l_layer.setActive( !l_layer.isActive() );
            }


            if ( e.getSource() == m_reference.get( "Visibility::Source" ) )
            {
                IViewableLayer l_layer = (IViewableLayer) CSimulation.getInstance().getWorld().get( "Source" );
                l_layer.setVisible( !l_layer.isVisible() );
            }
            if ( e.getSource() == m_reference.get( "Visibility::Car" ) )
            {
                IViewableLayer l_layer = (IViewableLayer) CSimulation.getInstance().getWorld().get( "Car" );
                l_layer.setVisible( !l_layer.isVisible() );
            }


            for ( String l_model : m_drivingmodelname )
                if ( e.getSource() == m_reference.get( l_model ) )
                {
                    this.throwSimulationRunningException();
                    ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Car" ) ).setDriveModel( l_model.replace( "Driving Model::", "" ) );
                }


        }
        catch ( Exception l_exception )
        {
            JOptionPane.showMessageDialog( null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION );
        }

        /*
            if (e.getSource() == m_reference.get("Create Institution"))
                this.createInstitution();
            if (e.getSource() == m_reference.get("Delete Institution"))
                this.deleteInstitution();

            if (e.getSource() == m_reference.get("Create Speed Norm"))
                this.createSpeedNorm();
            if (e.getSource() == m_reference.get("Delete Norm"))
                this.deleteNorm();
*/
    }


    /**
     * creates a new speed norm
     */
    private void createSpeedNorm()
    {
        /*
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");

        if (CSimulationData.getInstance().getCarInstitutionQueue().getAll().isEmptyCell())
            throw new IllegalStateException("institutions are not exist, add an institution first");

        String[] l_names = this.getInstitutionList();
        String l_name = (String) JOptionPane.showInputDialog(null, "push an institution for the norm", "Norm Institution", JOptionPane.QUESTION_MESSAGE, null, l_names, l_names[0]);
        if ((l_name == null) || (l_name.isEmptyCell()))
            return;

        IInstitution<INormObject> l_item = this.getInstitution(l_name);
        if (l_item != null) {
            String l_normname = (String) JOptionPane.showInputDialog(null, "insert a norm name", "name", JOptionPane.PLAIN_MESSAGE);
            if ((l_normname == null) || (l_normname.isEmptyCell()))
                return;
            if (l_normname.contains(":"))
                throw new IllegalArgumentException(": not allowed");

            String l_speed = (String) JOptionPane.showInputDialog(null, "insert a speed limit", "speed limit norm", JOptionPane.PLAIN_MESSAGE);
            if ((l_speed == null) || (l_speed.isEmptyCell()))
                return;

            l_item.add(new CSpeedNorm(l_item, l_normname, Integer.valueOf(l_speed)));
        }
        */
    }


    /**
     * deletes a norm
     *
     * @todo complete
     */
    private void deleteNorm()
    {
        /*
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");

        if (CSimulationData.getInstance().getCarInstitutionQueue().getAll().isEmptyCell())
            throw new IllegalStateException("institutions are not exist, add an institution first");

        ArrayList<String> l_names = new ArrayList();
        for (IInstitution<INormObject> l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
            for (INorm<INormObject> l_norm : l_item)
                l_names.add(l_item.getName() + ":" + l_norm.getName());
        String l_name = (String) JOptionPane.showInputDialog(null, "delete an norm", "Norm", JOptionPane.QUESTION_MESSAGE, null, l_names.toArray(), l_names.toArray()[0]);
        if ((l_name == null) || (l_name.isEmptyCell()))
            return;

        String[] l_parts = l_name.split("\\:");
        if (l_parts.length != 2)
            throw new IllegalStateException("cannot split institution and norm name");

        IInstitution<INormObject> l_item = this.getInstitution(l_parts[0]);
        if (l_item == null)
            throw new IllegalStateException("cannot found exception");

        for (INorm<INormObject> l_norm : l_item)
            if (l_norm.getName().equals(l_parts[1])) {
                l_item.remove(l_norm);
                l_norm.release();
            }
        */
    }


    /**
     * create a new institution
     */
    private void createInstitution()
    {
        /*
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");

        String l_input = JOptionPane.showInputDialog(null, "add a new institution name");
        if ((l_input == null) || (l_input.isEmptyCell()))
            return;
        if (l_input.contains(":"))
            throw new IllegalArgumentException(": not allowed");

        new CDefaultInstitution(l_input);
        COSMViewer.getInstance().getMainMouseListener().getPopupListener().update();
        */
    }


    /**
     * deletes an institution
     */
    private void deleteInstitution()
    {
        /*
        if (CSimulationData.getInstance().getCarInstitutionQueue().isEmptyCell())
            return;
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");

        String[] l_names = this.getInstitutionList();
        String l_name = (String) JOptionPane.showInputDialog(null, "delete an institution", "Institution", JOptionPane.QUESTION_MESSAGE, null, l_names, l_names[0]);
        IInstitution<INormObject> l_item = this.getInstitution(l_name);
        if (l_item != null)
            l_item.release();
        COSMViewer.getInstance().getMainMouseListener().getPopupListener().update();
        */
    }

    /**
     * returns a string list of the institutions
     *
     * @return array
     */
    private String[] getInstitutionList()
    {
        /*
        Queue<IInstitution<INormObject>> l_data = CSimulationData.getInstance().getCarInstitutionQueue().getAll();
        String[] l_names = new String[l_data.size()];
        int i = 0;
        for (IInstitution<INormObject> l_item : l_data) {
            l_names[i] = l_item.getName();
            i++;
        }
        return l_names;
        */
        return null;
    }


    /**
     * returns an institution object by the name
     *
     * @param p_name name
     * @return null or institution
     */
    private IInstitution<INormObject> getInstitution( String p_name )
    {
        /*
        if ((p_name == null) && (p_name.isEmptyCell()))
            return null;

        for (IInstitution<INormObject> l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
            if (l_item.getName().equals(p_name))
                return l_item;

        return null;
        */
        return null;
    }


    /**
     * sets the graph weights
     *
     * @param p_weight weight name (see "graphhopper" class "createWeighting" method)
     * @throws IllegalStateException
     */
    private void setGraphWeight( String p_weight ) throws IllegalStateException
    {
        if ( CSimulation.getInstance().isRunning() )
        {
            ( (JRadioButtonMenuItem) m_reference.get( m_weight ) ).setSelected( true );
            throw new IllegalStateException( "simulation is running" );
        }

        CCarLayer l_layer = (CCarLayer) CSimulation.getInstance().getWorld().get( "Car" );
        if ( l_layer != null )
        {
            l_layer.setGraphWeight( p_weight );
            m_weight = p_weight;
        }
    }


    /**
     * stores the list of sources
     *
     * @throws java.io.IOException
     */
    private void save() throws IOException
    {
        File l_store = this.openFileSaveDialog( new String[][]{{".mecsim", "Mec-Simulation (MecSim)"}} );
        if ( l_store == null )
            return;

        try
        {
            FileOutputStream l_stream = new FileOutputStream( this.addFileExtension( l_store, ".mecsim" ) );
            ObjectOutputStream l_output = new ObjectOutputStream( l_stream );
            l_output.writeObject( CSimulation.getInstance() );
            l_output.close();
            l_stream.close();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( "error on serialization process" );
        }

    }


    /**
     * loads the list of sources
     *
     * @throws IOException
     */
    private void load() throws IOException
    {
        File l_load = this.openFileLoadDialog( new String[][]{{".mecsim", "Mec-Simulation (MecSim)"}} );
        if ( l_load == null )
            return;

        try
        {
            FileInputStream l_stream = new FileInputStream( l_load );
            ObjectInputStream l_input = new ObjectInputStream( l_stream );
            CSimulation l_instance = (CSimulation) l_input.readObject();
            l_input.close();
            l_stream.close();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
            throw new IOException( "error on serialization process" );
        }
    }


    /**
     * create a screenshot of the OSM view
     *
     * @throws IOException
     */
    private void screenshot() throws IOException
    {
        BufferedImage l_image = new BufferedImage( COSMViewer.getInstance().getWidth(), COSMViewer.getInstance().getHeight(), BufferedImage.TYPE_INT_RGB );
        COSMViewer.getInstance().paint( l_image.getGraphics() );

        File l_store = this.openFileSaveDialog( new String[][]{{".png", "Portable Network Graphics (PNG)"}} );
        if ( l_store == null )
            return;

        ImageIO.write( l_image, "png", this.addFileExtension( l_store, ".png" ) );
    }


    /**
     * creates a filesave dialog, which stores the current path
     *
     * @return File or null
     */
    private File openFileSaveDialog( String[][] p_fileextensions )
    {
        JFileChooser l_filedialog = this.initFileDialog( p_fileextensions );
        if ( l_filedialog.showSaveDialog( COSMViewer.getInstance() ) != JFileChooser.APPROVE_OPTION )
            return null;
        m_filepath = l_filedialog.getCurrentDirectory();

        return l_filedialog.getSelectedFile();
    }


    /**
     * creates a fileload dialog, which stores the current path
     *
     * @return File or null
     */
    private File openFileLoadDialog( String[][] p_fileextensions )
    {
        JFileChooser l_filedialog = this.initFileDialog( p_fileextensions );
        if ( l_filedialog.showOpenDialog( COSMViewer.getInstance() ) != JFileChooser.APPROVE_OPTION )
            return null;
        m_filepath = l_filedialog.getCurrentDirectory();

        return l_filedialog.getSelectedFile();
    }

    /**
     * creates file dialog with extension list
     *
     * @param p_fileextensions arra with extension and description
     * @return filechooser
     */
    private JFileChooser initFileDialog( String[][] p_fileextensions )
    {
        JFileChooser l_filedialog = new JFileChooser();
        l_filedialog.setCurrentDirectory( m_filepath );
        if ( p_fileextensions != null )
        {
            l_filedialog.setAcceptAllFileFilterUsed( false );
            for ( String[] l_item : p_fileextensions )
                l_filedialog.addChoosableFileFilter( l_item.length == 1 ? new UIFileFilter( l_item[0] ) : new UIFileFilter( l_item[0], l_item[1] ) );
        }
        return l_filedialog;
    }

    /**
     * adds a file extension if necessary
     *
     * @param p_file   file object
     * @param p_suffix suffix
     * @return file with extension
     */
    private File addFileExtension( File p_file, String p_suffix )
    {
        File l_file = p_file;
        if ( !l_file.getAbsolutePath().endsWith( p_suffix ) )
            l_file = new File( l_file + p_suffix );
        return l_file;
    }


    /**
     * file filter class to create a filter list *
     */
    private class UIFileFilter extends FileFilter
    {
        /**
         * type description *
         */
        private String m_description = "";
        /**
         * extension *
         */
        private String m_extension = "";

        /**
         * ctor
         *
         * @param p_extension extension
         */
        public UIFileFilter( String p_extension )
        {
            m_extension = p_extension;
        }

        /**
         * ctor
         *
         * @param p_extension   extension
         * @param p_description description
         */
        public UIFileFilter( String p_extension, String p_description )
        {
            m_extension = p_extension;
            m_description = p_description;

        }

        @Override
        public boolean accept( File p_file )
        {
            if ( p_file.isDirectory() )
                return true;
            return ( p_file.getName().toLowerCase().endsWith( m_extension ) );
        }

        @Override
        public String getDescription()
        {
            return m_description;
        }
    }

}
