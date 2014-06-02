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

import de.tu_clausthal.in.winf.objects.IObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Map;


/**
 * inspector class to create a visual view of an object
 * @see http://openbook.galileocomputing.de/javainsel9/javainsel_19_019.htm#mj4d8b3bc0b550304f97713fa1788ff0f6
 */
public class CInspector extends JScrollPane {

    /**
     * singleton instance *
     */
    private static CInspector s_inspector = new CInspector();
    /**
     * table model *
     */
    private CTableModel m_model = new CTableModel();

    /**
     * private ctor *
     */
    private CInspector() {
        JTable l_table = new JTable(m_model);
        l_table.createDefaultColumnsFromModel();
        l_table.setFillsViewportHeight(true);
        this.add(l_table);
    }

    /**
     * return singleton instance
     *
     * @return instance
     */
    public static CInspector getInstance() {
        return s_inspector;
    }

    /**
     * sets a new object
     *
     * @param p_object object
     */
    public void set(IObject p_object) {
        m_model.set(p_object);
    }

    /**
     * table model to use the inspect object call *
     */
    private class CTableModel extends AbstractTableModel {

        /**
         * array with names *
         */
        String[] m_names = null;
        /**
         * array with object values *
         */
        Object[] m_values = null;


        /**
         * sets a new object
         *
         * @param p_object object
         */
        public void set(IObject p_object) {
            m_names = null;
            m_values = null;

            if (p_object == null)
                return;

            Map<String, Object> l_data = p_object.inspect();
            if ((l_data == null) || (l_data.isEmpty()))
                return;

            m_names = new String[l_data.size()];
            m_values = new Object[l_data.size()];
            int i = 0;
            for (Map.Entry<String, Object> l_item : l_data.entrySet()) {
                m_names[i] = l_item.getKey();
                m_values[i] = l_item.getValue();
                //System.out.println(l_item.getKey()+"\t"+l_item.getValue());
                i++;
            }
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Property";
                case 1:
                    return "Value";
            }

            return null;
        }

        @Override
        public int getRowCount() {
            if ((m_values == null) || (m_names == null))
                return 0;

            return m_names.length;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (((m_values == null) || (m_names == null)) || (rowIndex < 0) || (rowIndex >= m_names.length))
                return null;

            switch (columnIndex) {
                case 0:
                    return m_names[rowIndex];
                case 1:
                    return m_values[rowIndex];
            }

            return null;
        }
    }

}
