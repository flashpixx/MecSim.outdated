/**
 * @cond LICENSE
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

package de.tu_clausthal.in.mec.ui.inspector;

import de.tu_clausthal.in.mec.common.CCommon;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * table model to use the inspect object call
 *
 * @deprecated
 */
public class CInspectorModel implements TableModel
{

    /**
     * array with names
     */
    private ArrayList<String> m_names = new ArrayList<>();
    /**
     * array with object values
     */
    private ArrayList<Object> m_values = new ArrayList<>();
    /**
     * push for model listeners
     */
    private Set<TableModelListener> m_listener = new HashSet<>();


    /**
     * sets a new object
     *
     * @param p_object object
     */
    public final void set( final IInspector p_object )
    {
        m_names.clear();
        m_values.clear();
        if ( p_object == null ) return;

        final Map<String, Object> l_data = p_object.inspect();
        if ( ( l_data == null ) || ( l_data.isEmpty() ) ) return;

        for ( Map.Entry<String, Object> l_item : l_data.entrySet() )
        {
            m_names.add( l_item.getKey() );
            m_values.add( l_item.getValue() );
        }

        final TableModelEvent l_event = new TableModelEvent( this );
        for ( TableModelListener l_listener : m_listener )
            l_listener.tableChanged( l_event );
    }

    @Override
    public final int getRowCount()
    {
        if ( ( m_values.isEmpty() ) || ( m_names.isEmpty() ) ) return 0;

        return m_names.size();
    }

    @Override
    public final int getColumnCount()
    {
        return 2;
    }

    @Override
    public final String getColumnName( final int p_column )
    {
        switch ( p_column )
        {
            case 0:
                return "Property";
            case 1:
                return "Value";
            default:
        }

        throw new IllegalArgumentException( CCommon.getResourceString( this, "position" ) );
    }

    @Override
    public final Class<?> getColumnClass( final int p_column )
    {
        switch ( p_column )
        {
            case 0:
                return String.class;
            case 1:
                return Object.class;
            default:
        }

        throw new IllegalArgumentException( CCommon.getResourceString( this, "position" ) );
    }

    @Override
    public final boolean isCellEditable( final int p_row, final int p_column )
    {
        return false;
    }

    @Override
    public final Object getValueAt( final int p_row, final int p_column )
    {
        if ( ( ( m_values == null ) || ( m_names == null ) ) || ( p_row < 0 ) || ( p_row >= m_names.size() ) )
            return null;

        switch ( p_column )
        {
            case 0:
                return m_names.get( p_row );
            case 1:
                return m_values.get( p_row );
            default:
        }

        throw new IllegalArgumentException( CCommon.getResourceString( this, "position" ) );
    }

    @Override
    public void setValueAt( final Object p_value, final int p_row, final int p_column )
    {
    }

    @Override
    public final void addTableModelListener( final TableModelListener p_listener )
    {
        m_listener.add( p_listener );
    }

    @Override
    public final void removeTableModelListener( final TableModelListener p_listener )
    {
        m_listener.remove( p_listener );
    }
}
