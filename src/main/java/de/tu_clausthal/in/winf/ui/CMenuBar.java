/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
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

import de.tu_clausthal.in.winf.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.winf.drivemodel.IDriveModel;
import de.tu_clausthal.in.winf.graph.CGraphHopper;
import de.tu_clausthal.in.winf.mas.norm.IInstitution;
import de.tu_clausthal.in.winf.objects.CDefaultSource;
import de.tu_clausthal.in.winf.objects.CSerializableGeoPosition;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.objects.norms.CDefaultInstitution;
import de.tu_clausthal.in.winf.objects.norms.INormCar;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.simulation.CSimulationData;
import de.tu_clausthal.in.winf.util.CMenuFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


/**
 * class for create the menubar *
 */
public class CMenuBar extends JMenuBar implements ActionListener {

    /**
     * file chooser dialog
     */
    private final JFileChooser m_filedialog = new JFileChooser();
    /**
     * map with object for action listener
     */
    private Map<String, Object> m_reference = new HashMap();
    /**
     * driving model *
     */
    private String m_drivemodel = "Nagel-Schreckenberg";
    /**
     * current weight
     */
    private String m_weight = "Default";


    /**
     * ctor with menu items *
     */
    public CMenuBar() {
        super();

        String[] l_file = {"Load Sources", "Save Sources", null, "Screenshot"};
        this.add(CMenuFactory.createMenu("File", l_file, this, m_reference));

        String[] l_actions = {"Start", "Stop", null, "Reset"};
        this.add(CMenuFactory.createMenu("Action", l_actions, this, m_reference));

        String[] l_weights = {"Default", "Speed", "Traffic Jam", "Speed & Traffic Jam"};
        this.add(CMenuFactory.createRadioMenu("Graph Weights", l_weights, this, m_reference));

        String[] l_drivemodel = {"Nagel-Schreckenberg"};
        this.add(CMenuFactory.createRadioMenu("Driving Model", l_drivemodel, this, m_reference));

        String[] l_institution = {"Create", "Delete"};
        this.add(CMenuFactory.createMenu("Institution", l_institution, this, m_reference));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {

            if (e.getSource() == m_reference.get("Screenshot"))
                this.screenshot();
            if (e.getSource() == m_reference.get("Save Sources"))
                this.saveSources();
            if (e.getSource() == m_reference.get("Load Sources"))
                this.loadSources();


            if (e.getSource() == m_reference.get("Start"))
                CSimulation.getInstance().start(this.generateDrivingModel());
            if (e.getSource() == m_reference.get("Stop"))
                CSimulation.getInstance().stop();
            if (e.getSource() == m_reference.get("Reset"))
                CSimulation.getInstance().reset();


            if (e.getSource() == m_reference.get("Default"))
                this.setGraphWeight("Default");
            if (e.getSource() == m_reference.get("Speed"))
                this.setGraphWeight("Speed");
            if (e.getSource() == m_reference.get("Traffic Jam"))
                this.setGraphWeight("Traffic Jam");
            if (e.getSource() == m_reference.get("Speed & Traffic Jam"))
                this.setGraphWeight("Speed & Traffic Jam");

            if (e.getSource() == m_reference.get("Create"))
                this.createInstitution();
            if (e.getSource() == m_reference.get("Delete"))
                this.deleteInstitution();


            if (e.getSource() == m_reference.get("Nagel-Schreckenberg"))
                this.setDrivingModel("Nagel-Schreckenberg");


        } catch (Exception l_exception) {
            JOptionPane.showMessageDialog(null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION);
        }

    }


    /**
     * create a new institution
     */
    private void createInstitution() {
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");

        String l_input = JOptionPane.showInputDialog(null, "add a new institution name");
        if ((l_input == null) || (l_input.isEmpty()))
            return;

        CSimulationData.getInstance().getCarInstitutionQueue().add(new CDefaultInstitution<INormCar>(l_input));
    }


    /**
     * deletes an institution
     */
    private void deleteInstitution() {
        if (CSimulationData.getInstance().getCarInstitutionQueue().isEmpty())
            return;
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");

        Queue<IInstitution<INormCar>> l_data = CSimulationData.getInstance().getCarInstitutionQueue().getAll();
        String[] l_names = new String[l_data.size()];
        int i = 0;
        for (IInstitution<INormCar> l_item : l_data) {
            l_names[i] = l_item.getName();
            i++;
        }

        String l_name = (String) JOptionPane.showInputDialog(null, "delete an institution", "Institution", JOptionPane.QUESTION_MESSAGE, null, l_names, l_names[0]);
        if ((l_name != null) && (!l_name.isEmpty()))
            for (IInstitution<INormCar> l_item : l_data)
                if (l_item.getName().equals(l_name)) {
                    CSimulationData.getInstance().getCarInstitutionQueue().remove(l_item);
                    break;
                }
    }


    /**
     * sets the graph weights
     *
     * @param p_weight weight name (see "graphhopper" class "createWeighting" method)
     * @throws IllegalStateException
     */
    private void setGraphWeight(String p_weight) throws IllegalStateException {
        if (CSimulation.getInstance().isRunning()) {
            ((JRadioButtonMenuItem) m_reference.get(m_weight)).setSelected(true);
            throw new IllegalStateException("simulation is running");
        }
        CGraphHopper.getInstance().setWeights(p_weight);
        m_weight = p_weight;
    }


    /**
     * sets the driving model
     *
     * @param p_model model
     * @throws IllegalStateException
     */
    private void setDrivingModel(String p_model) throws IllegalStateException {
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");
        m_drivemodel = p_model;
    }


    /**
     * generates the driving model
     *
     * @return model
     */
    private IDriveModel generateDrivingModel() {
        if (m_drivemodel.equals("Nagel-Schreckenberg"))
            return new CNagelSchreckenberg();

        return null;
    }


    /**
     * stores the list of sources
     *
     * @throws java.io.IOException
     */
    private void saveSources() throws IOException {
        if (m_filedialog.showSaveDialog(COSMViewer.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

        Set<CSerializableGeoPosition> l_positions = new HashSet();
        for (ICarSourceFactory l_item : CSimulationData.getInstance().getSourceQueue().getAll())
            l_positions.add(new CSerializableGeoPosition(l_item.getPosition()));

        FileOutputStream l_stream = new FileOutputStream(this.addFileExtension(m_filedialog.getSelectedFile(), ".src"));
        ObjectOutputStream l_output = new ObjectOutputStream(l_stream);
        l_output.writeObject(l_positions);
        l_output.close();
        l_stream.close();
    }


    /**
     * loads the list of sources
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    @SuppressWarnings(value = "unchecked")
    private void loadSources() throws IOException, ClassNotFoundException, IllegalAccessException {
        if (m_filedialog.showOpenDialog(COSMViewer.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

        FileInputStream l_stream = new FileInputStream(m_filedialog.getSelectedFile());
        ObjectInputStream l_input = new ObjectInputStream(l_stream);
        Object l_data = l_input.readObject();
        l_input.close();
        l_stream.close();

        if (l_data instanceof Set)
            for (CSerializableGeoPosition l_item : (Set<CSerializableGeoPosition>) l_data)
                CSimulationData.getInstance().getSourceQueue().add(new CDefaultSource(l_item.getObject()));
    }


    /**
     * create a screenshot of the OSM view
     *
     * @throws IOException
     */
    private void screenshot() throws IOException {
        BufferedImage l_image = new BufferedImage(COSMViewer.getInstance().getWidth(), COSMViewer.getInstance().getHeight(), BufferedImage.TYPE_INT_RGB);
        COSMViewer.getInstance().paint(l_image.getGraphics());

        if (m_filedialog.showSaveDialog(COSMViewer.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

        ImageIO.write(l_image, "png", this.addFileExtension(m_filedialog.getSelectedFile(), ".png"));
    }


    /**
     * adds a file extension if necessary
     *
     * @param p_file   file object
     * @param p_suffix suffix
     * @return file with extension
     */
    private File addFileExtension(File p_file, String p_suffix) {
        File l_file = p_file;
        if (!l_file.getAbsolutePath().endsWith(p_suffix))
            l_file = new File(l_file + p_suffix);
        return l_file;
    }
}
