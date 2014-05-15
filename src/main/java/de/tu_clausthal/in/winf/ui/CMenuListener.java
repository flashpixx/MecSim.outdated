/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenpraktikum.   #
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
 @endcond
 **/

package de.tu_clausthal.in.winf.ui;

import de.tu_clausthal.in.winf.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.winf.drivemodel.IDriveModel;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


/**
 * menu listener for create actions on the menu items *
 */
public class CMenuListener implements ActionListener {

    /**
     * logger instance *
     */
    private final Logger m_Logger = LoggerFactory.getLogger(getClass());
    /**
     * map to create a key-value structure, so we can use object equality
     */
    private HashMap<String, Object> m_items = new HashMap();
    /**
     * object for driving modell *
     */
    private IDriveModel m_drivemodel = new CNagelSchreckenberg();


    /**
     * adds a new object to the map *
     */
    public void add(String p_name, Object p_obj) {
        m_items.put(p_name, p_obj);
    }


    /**
     * action performing method, which uses object equality to run the
     * correct call
     *
     * @param p_event event object
     */
    public void actionPerformed(ActionEvent p_event) {
        try {

            if (p_event.getSource() == m_items.get("Start"))
                CSimulation.getInstance().start(m_drivemodel);


            if (p_event.getSource() == m_items.get("Stop"))
                CSimulation.getInstance().stop();

            if (p_event.getSource() == m_items.get("Reset")) {
                CSimulation.getInstance().reset();
                COSMViewer.getInstance().reset();
            }

        } catch (IllegalAccessException l_exception) {
            m_Logger.error(l_exception.getMessage());
        }
    }

}
