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

import de.tu_clausthal.in.winf.mas.norm.IInstitution;
import de.tu_clausthal.in.winf.simulation.CSimulationData;
import de.tu_clausthal.in.winf.util.CMenuFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * popmenu
 */
public class CMenuPopup extends JPopupMenu implements ActionListener {

    /**
     * map with object for action listener
     */
    private Map<String, Object> m_reference = new HashMap();
    /**
     * selected car definition *
     */
    private String m_sources = "default cars";
    /**
     * reference to the institution menu, to remove it on update *
     */
    private JMenu m_institutionMenu = null;
    /**
     * name of the institution
     */
    private String m_institution = null;


    /**
     * ctor to create popup
     */
    public CMenuPopup() {

        String[] l_cars = {"default cars", "norm cars"};
        this.add(CMenuFactory.createRadioMenu("Sources", l_cars, this, m_reference));
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == m_reference.get("default cars")) {
            this.setSource("default cars");
            return;
        }
        if (e.getSource() == m_reference.get("norm cars")) {
            this.setSource("norm cars");
            return;
        }

        m_institution = ((JRadioButtonMenuItem) e.getSource()).getText();

    }


    /**
     * update method to modify internal menu structure
     */
    public void update() {
        if (m_institutionMenu != null) {
            this.remove(m_institutionMenu);
            m_institution = null;
        }
        if (CSimulationData.getInstance().getCarInstitutionQueue().getAll().isEmpty())
            return;

        ArrayList<String> l_list = new ArrayList();
        for (IInstitution l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
            l_list.add(l_item.getName());
        String[] l_array = new String[l_list.size()];
        l_list.toArray(l_array);

        m_institution = l_array[0];
        m_institutionMenu = CMenuFactory.createRadioMenu("Institution", l_array, this, m_reference);
        this.add(m_institutionMenu);
    }


    /**
     * sets the source option
     *
     * @param p_name name
     */
    private void setSource(String p_name) {
        m_sources = p_name;
        ((JRadioButtonMenuItem) m_reference.get(p_name)).setSelected(true);
    }


    /**
     * returns the current source selection
     *
     * @return string with definition
     */
    public String getSourceSelection() {
        return m_sources;
    }


    /**
     * returns the selected institution
     *
     * @return name
     */
    public String getInstitutionSelection() {
        return m_institution;
    }

}
