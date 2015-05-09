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
 */

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.common.CCommon;

import java.util.HashMap;
import java.util.Map;


/**
 * class which is responsible for multi language support in the web ui
 */
public class CLanguageEnvironment
{

    /**
     * method to read waypoint specific labels and resource strings
     *
     * @return
     */
    private final Map<String, String> web_static_getwaypointlabels()
    {
        HashMap<String, String> l_labels = new HashMap<>();
        l_labels.put( "carcount", CCommon.getResourceString( this, "carcount" ) );
        l_labels.put( "mean", CCommon.getResourceString( this, "mean" ) );
        l_labels.put( "deviation", CCommon.getResourceString( this, "deviation" ) );
        l_labels.put( "lowerbound", CCommon.getResourceString( this, "lowerbound" ) );
        l_labels.put( "upperbound", CCommon.getResourceString( this, "upperbound" ) );
        return l_labels;
    }

}
