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

package de.tu_clausthal.in.winf.objects.norm;

import de.tu_clausthal.in.winf.mas.norm.IInstitution;
import de.tu_clausthal.in.winf.mas.norm.INorm;
import de.tu_clausthal.in.winf.mas.norm.INormCheckResult;
import de.tu_clausthal.in.winf.mas.norm.INormObject;
import de.tu_clausthal.in.winf.objects.CDefaultCar;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.HashMap;
import java.util.Map;


/**
 * creates a car with norm context
 */
public class CNormCar extends CDefaultCar implements INormObject {

    /**
     * set with norms, that are matched *
     */
    protected Map<INorm<INormObject>, INormCheckResult> m_norms = new HashMap();


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

        for (Map.Entry<INorm<INormObject>, INormCheckResult> l_item : m_norms.entrySet()) {
            IInstitution l_institution = l_item.getKey().getInstitution();
            l_map.put("institution [" + l_institution.getName() + "] / norm [" + l_item.getKey().getName() + "] / weight", l_item.getValue().getWeight());
        }

        return l_map;
    }


    @Override
    public void setMatchedNorm(Map<INorm<INormObject>, INormCheckResult> p_norm) {
        m_norms.putAll(p_norm);
    }

    @Override
    public void clearMatchedNorm() {
        m_norms.clear();
    }


}
