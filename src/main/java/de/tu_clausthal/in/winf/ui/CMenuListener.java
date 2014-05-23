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
import de.tu_clausthal.in.winf.objects.CDefaultSource;
import de.tu_clausthal.in.winf.objects.CSerializableGeoPosition;
import de.tu_clausthal.in.winf.objects.ICarSourceFactory;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.simulation.CSimulationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * menu listener for create actions on the menu items *
 */
public class CMenuListener implements ActionListener {

    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * file chooser dialog
     */
    private final JFileChooser m_filedialog = new JFileChooser();
    /**
     * map to create a key-value structure, so we can use object equality
     */
    private HashMap<String, Object> m_items = new HashMap();
    /**
     * object for driving model *
     */
    private IDriveModel m_drivemodel = new CNagelSchreckenberg();
    /**
     * sets the selected item
     */
    private Map<String, Object> m_itemSelected = new HashMap();


    /**
     * adds a new object to the map *
     *
     * @param p_name object name
     * @param p_obj  object
     */
    public void add(String p_name, Object p_obj) {
        m_items.put(p_name, p_obj);
    }

    /**
     * adds a new object to the map and defines for a default object
     *
     * @param p_name    object name
     * @param p_obj     object
     * @param p_default default option
     */
    public void add(String p_name, Object p_obj, String p_default) {
        m_items.put(p_name, p_obj);
        m_itemSelected.put(p_default, p_obj);
    }


    /**
     * action performing method, which uses object equality to run the
     * correct call
     *
     * @param p_event event object
     * @bug reset
     */
    public void actionPerformed(ActionEvent p_event) {
        try {

            if (p_event.getSource() == m_items.get("Screenshot"))
                this.screenshot();
            if (p_event.getSource() == m_items.get("Save Sources"))
                this.saveSources();
            if (p_event.getSource() == m_items.get("Load Sources"))
                this.loadSources();


            if (p_event.getSource() == m_items.get("Start"))
                CSimulation.getInstance().start(m_drivemodel);
            if (p_event.getSource() == m_items.get("Stop"))
                CSimulation.getInstance().stop();
            if (p_event.getSource() == m_items.get("Reset")) {
                CSimulation.getInstance().reset();
            }


            if (p_event.getSource() == m_items.get("Default")) {
                this.setGraphWeight("");
                m_itemSelected.put("weight", p_event.getSource());
            }
            if (p_event.getSource() == m_items.get("Speed")) {
                this.setGraphWeight("Speed");
                m_itemSelected.put("weight", p_event.getSource());
            }
            if (p_event.getSource() == m_items.get("Traffic Jam")) {
                this.setGraphWeight("Traffic Jam");
                m_itemSelected.put("weight", p_event.getSource());
            }
            if (p_event.getSource() == m_items.get("Speed & Traffic Jam")) {
                this.setGraphWeight("Speed & Traffic Jam");
                m_itemSelected.put("weight", p_event.getSource());
            }


            if (p_event.getSource() == m_items.get("Nagel-Schreckenberg")) {
                this.setDrivingModel(new CNagelSchreckenberg());
                m_itemSelected.put("drivemodel", p_event.getSource());
            }

        } catch (Exception l_exception) {
            if (m_itemSelected.get("drivemodel") != null)
                ((JRadioButtonMenuItem) m_itemSelected.get("drivemodel")).setSelected(true);
            if (m_itemSelected.get("weight") != null)
                ((JRadioButtonMenuItem) m_itemSelected.get("weight")).setSelected(true);

            JOptionPane.showMessageDialog(null, l_exception.getMessage(), "Warning", JOptionPane.CANCEL_OPTION);
        }
    }


    /**
     * sets the graph weights
     *
     * @param p_weight weight name (see "graphhopper" class "createWeighting" method)
     * @throws IllegalStateException
     */
    private void setGraphWeight(String p_weight) throws IllegalStateException {
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");
        CGraphHopper.getInstance().setWeights(p_weight);
    }


    /**
     * sets the driving model
     *
     * @param p_model model
     * @throws IllegalStateException
     */
    private void setDrivingModel(IDriveModel p_model) throws IllegalStateException {
        if (CSimulation.getInstance().isRunning())
            throw new IllegalStateException("simulation is running");
        if (p_model == null)
            throw new IllegalArgumentException("driving model need not be null");
        m_drivemodel = p_model;
    }


    /**
     * stores the list of sources
     *
     * @throws IOException
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
