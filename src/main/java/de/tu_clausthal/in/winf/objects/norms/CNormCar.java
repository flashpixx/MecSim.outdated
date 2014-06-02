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

package de.tu_clausthal.in.winf.objects.norms;

import de.tu_clausthal.in.winf.mas.norm.IInstitution;
import de.tu_clausthal.in.winf.mas.norm.INorm;
import de.tu_clausthal.in.winf.mas.norm.INormCheckResult;
import de.tu_clausthal.in.winf.objects.CDefaultCar;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Map;


/**
 * creates a car with norm context
 */
public class CNormCar extends CDefaultCar implements INormCar {

    /**
     * set with norms, that are matched *
     */
    protected Map<INorm<INormCar>, INormCheckResult> m_norms = null;


    /**
     * ctor to create the initial values
     *
     * @param p_StartPosition start positions (position of the source)
     */
    public CNormCar(GeoPosition p_StartPosition) {
        super(p_StartPosition);
    }


    @Override
    public Map<String, Object> inspect() {
        Map<String, Object> l_map = super.inspect();

        for (Map.Entry<INorm<INormCar>, INormCheckResult> l_item : m_norms.entrySet()) {
            IInstitution l_institution = l_item.getKey().getInstitution();
            if (l_institution == null)
                l_map.put(l_item.getKey().getName(), "matched with weight " + l_item.getValue().getWeight());
            else
                l_map.put(l_item.getKey().getName(), "matched in institution " + l_institution.getName() + " weight " + l_item.getValue().getWeight());
        }

        return l_map;
    }


    @Override
    public void isNormMatch(Map<INorm<INormCar>, INormCheckResult> p_norm) {
        m_norms = p_norm;
    }
}
