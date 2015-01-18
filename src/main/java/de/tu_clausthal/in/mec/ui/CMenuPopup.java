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

import de.tu_clausthal.in.mec.object.source.CSourceFactoryLayer;
import de.tu_clausthal.in.mec.simulation.CSimulation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;


/**
 * popmenu
 */
public class CMenuPopup extends JPopupMenu implements ActionListener
{

    /**
     * menu item storage *
     */
    protected CMenuStorage m_items = new CMenuStorage();


    /**
     * ctor to create popup
     */
    public CMenuPopup()
    {
        super();


        String[] l_sources = ( (CSourceFactoryLayer) CSimulation.getInstance().getWorld().get( "Sources" ) ).getSourceNamesList();
        m_items.addRadioGroup( "Sources", l_sources, this );
        ( (JRadioButtonMenuItem) m_items.get( "Sources/" + l_sources[0] ) ).setSelected( true );


        // get main menus to define order in the UI
        for ( String l_root : new String[]{"Sources"} )
            this.add( m_items.get( l_root ) );
    }


    /**
     * returns the name of the selected source
     *
     * @return source name
     */
    public String getSelectedSource()
    {
        for ( Map.Entry<CMenuStorage.Path, JComponent> l_item : m_items.entrySet( "Sources" ) )
            if ( ( (JRadioButtonMenuItem) ( l_item.getValue() ) ).isSelected() )
                return l_item.getKey().getSuffix();

        return null;
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {

        /*
        if ( e.getSource() == m_reference.get( "default cars" ) )
        {
            this.setSource( "default cars" );
            return;
        }
        if ( e.getSource() == m_reference.get( "norm cars" ) )
        {
            this.setSource( "norm cars" );
            return;
        }

        m_institution = ( (JRadioButtonMenuItem) e.getSource() ).getText();
        */

    }


    /**
     * update method to modify internal menu structure
     */
    public void update()
    {
        /*
        if (m_institutionMenu != null) {
            this.remove(m_institutionMenu);
            m_institution = null;
        }
        if (CSimulationData.getInstance().getCarInstitutionQueue().getAll().isEmptyCell())
            return;

        ArrayList<String> l_list = new ArrayList();
        for (IInstitution l_item : CSimulationData.getInstance().getCarInstitutionQueue().getAll())
            l_list.add(l_item.getName());
        String[] l_array = new String[l_list.size()];
        l_list.toArray(l_array);

        m_institution = l_array[0];
        m_institutionMenu = CMenuFactory.createRadioMenuGroup("Institution", l_array, this, m_reference);
        this.add(m_institutionMenu);
        */
    }


}
