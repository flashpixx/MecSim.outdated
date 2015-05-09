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

        l_labels.put( "wizardwidget", CCommon.getResourceString( this, "wizardwidget" ) );
        l_labels.put( "factorysettings", CCommon.getResourceString( this, "factorysettings" ) );
        l_labels.put( "generatorsettings", CCommon.getResourceString( this, "generatorsettings" ) );
        l_labels.put( "carsettings", CCommon.getResourceString( this, "carsettings" ) );
        l_labels.put( "customizing", CCommon.getResourceString( this, "customizing" ) );
        l_labels.put( "selectyourfactory", CCommon.getResourceString( this, "selectyourfactory" ) );
        l_labels.put( "selectyourasl", CCommon.getResourceString( this, "selectyourasl" ) );
        l_labels.put( "selectyourgenerator", CCommon.getResourceString( this, "selectyourgenerator" ) );
        l_labels.put( "selectyourcarcount", CCommon.getResourceString( this, "selectyourcarcount" ) );
        l_labels.put( "selectyourmean", CCommon.getResourceString( this, "selectyourmean" ) );
        l_labels.put( "selectyourdeviation", CCommon.getResourceString( this, "selectyourdeviation" ) );
        l_labels.put( "selectyourlowerbound", CCommon.getResourceString( this, "selectyourlowerbound" ) );
        l_labels.put( "selectyourupperbound", CCommon.getResourceString( this, "selectyourupperbound" ) );
        l_labels.put( "selectcarspeedprob", CCommon.getResourceString( this, "selectcarspeedprob" ) );
        l_labels.put( "selectmaxcarspeedprob", CCommon.getResourceString( this, "selectmaxcarspeedprob" ) );
        l_labels.put( "selectaccprob", CCommon.getResourceString( this, "selectaccprob" ) );
        l_labels.put( "selectdecprob", CCommon.getResourceString( this, "selectdecprob" ) );
        l_labels.put( "selectlingerprob", CCommon.getResourceString( this, "selectlingerprob" ) );
        l_labels.put( "selecttoolname", CCommon.getResourceString( this, "selecttoolname" ) );
        l_labels.put( "selecttoolnamevalue", CCommon.getResourceString( this, "selecttoolnamevalue" ) );
        l_labels.put( "selecttoolcolor", CCommon.getResourceString( this, "selecttoolcolor" ) );
        l_labels.put( "previous", CCommon.getResourceString( this, "previous" ) );
        l_labels.put( "next", CCommon.getResourceString( this, "next" ) );
        l_labels.put( "finish", CCommon.getResourceString( this, "finish" ) );

        return l_labels;
    }

}
