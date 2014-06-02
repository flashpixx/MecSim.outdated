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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * table model to use the inspect object call *
 */
public class CInspectorModel implements TableModel {

    /**
     * array with names *
     */
    ArrayList<String> m_names = new ArrayList();
    /**
     * array with object values *
     */
    ArrayList<Object> m_values = new ArrayList();

    private Set<TableModelListener> m_listener = new HashSet();


    /**
     * sets a new object
     *
     * @param p_object object
     */
    public void set(IObject p_object) {
        m_names.clear();
        m_values.clear();
        if (p_object == null)
            return;

        Map<String, Object> l_data = p_object.inspect();
        if ((l_data == null) || (l_data.isEmpty()))
            return;

        for (Map.Entry<String, Object> l_item : l_data.entrySet()) {
            m_names.add(l_item.getKey());
            m_values.add(l_item.getValue());
        }

        TableModelEvent l_event = new TableModelEvent(this);
        for (TableModelListener l_listener : m_listener)
            l_listener.tableChanged(l_event);
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Property";
            case 1:
                return "Value";
        }

        throw new IllegalArgumentException("illegal position");
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Object.class;
        }

        throw new IllegalArgumentException("illegal position");
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getRowCount() {
        if ((m_values.isEmpty()) || (m_names.isEmpty()))
            return 0;

        return m_names.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (((m_values == null) || (m_names == null)) || (rowIndex < 0) || (rowIndex >= m_names.size()))
            return null;

        switch (columnIndex) {
            case 0:
                return m_names.get(rowIndex);
            case 1:
                return m_values.get(rowIndex);
        }

        throw new IllegalArgumentException("illegal position");
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        m_listener.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        m_listener.remove(l);
    }
}