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
     * dynamic waypoint labels
     */
    private static final Map<String, String> m_dynamicWaypointLabels = new HashMap<String, String>()
    {{
            put( "previous", CCommon.getResourceString( CLanguageEnvironment.class, "previous" ) );
            put( "next", CCommon.getResourceString( CLanguageEnvironment.class, "next" ) );
            put( "finish", CCommon.getResourceString( CLanguageEnvironment.class, "finish" ) );
            put( "nodata", CCommon.getResourceString( CLanguageEnvironment.class, "nodata" ) );

            put( "wizardwidget", CCommon.getResourceString( CLanguageEnvironment.class, "wizardwidget" ) );

            put( "selectyourmean", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourmean" ) );
            put( "selectyourdeviation", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourdeviation" ) );
            put( "selectyourlowerbound", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourlowerbound" ) );
            put( "selectyourupperbound", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourupperbound" ) );

            put( "selecttoolnamevalue", CCommon.getResourceString( CLanguageEnvironment.class, "selecttoolnamevalue" ) );
            put( "toolcreationfailed", CCommon.getResourceString( CLanguageEnvironment.class, "toolcreationfailed" ) );

            put( "waypointlist", CCommon.getResourceString( CLanguageEnvironment.class, "waypointlist" ) );
            put( "waypointname", CCommon.getResourceString( CLanguageEnvironment.class, "waypointname" ) );
            put( "waypointtyp", CCommon.getResourceString( CLanguageEnvironment.class, "waypointtyp" ) );
            put( "configuretarget", CCommon.getResourceString( CLanguageEnvironment.class, "configuretarget" ) );
            put( "addtarget", CCommon.getResourceString( CLanguageEnvironment.class, "addtarget" ) );
    }};

    private static final Map<String, String> m_waypointpreset = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "wizardwidget" ) );

            put( "id_factoryhead", CCommon.getResourceString( CLanguageEnvironment.class, "factorysettings" ) );
            put( "id_basegeneratorhead", CCommon.getResourceString( CLanguageEnvironment.class, "generatorsettings" ) );
            put( "id_carhead", CCommon.getResourceString( CLanguageEnvironment.class, "carsettings" ) );
            put( "id_customhead", CCommon.getResourceString( CLanguageEnvironment.class, "customizing" ) );

            put( "label_type", CCommon.getResourceString( CLanguageEnvironment.class, "selectwaypointtype" ) );
            put( "label_radius", CCommon.getResourceString( CLanguageEnvironment.class, "selectwaypointradius" ) );
            put( "label_factory", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourfactory" ) );
            put( "label_agent", CCommon.getResourceString( CLanguageEnvironment.class, "selectyouragentprogram" ) );

            put( "label_carcount", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourcarcount" ) );

            put( "id_speedhead", CCommon.getResourceString( CLanguageEnvironment.class, "speedsettingslabel" ) );
            put( "id_maxspeedhead", CCommon.getResourceString( CLanguageEnvironment.class, "maxspeedsettingslabel" ) );
            put( "id_accelerationhead", CCommon.getResourceString( CLanguageEnvironment.class, "accsettingslabel" ) );
            put( "id_decelerationhead", CCommon.getResourceString( CLanguageEnvironment.class, "decsettingslabel" ) );
            put( "id_lingerhead", CCommon.getResourceString( CLanguageEnvironment.class, "lingerersettingslabel" ) );

            //put( "label_");
            put( "label_linger", CCommon.getResourceString( CLanguageEnvironment.class, "selectlingerprob" ) );
        }};
    /**
     * static waypoint labels
     */
    private static final Map<String, String> m_staticWaypointLabels = new HashMap<String, String>()
    {{
            //general wizard labels
            put( "#mesim_source_factorySettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "factorysettings" ) );
            put( "#mesim_source_generatorSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "generatorsettings" ) );
            put( "#mesim_source_carSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "carsettings" ) );
            put( "#mesim_source_customizing_label", CCommon.getResourceString( CLanguageEnvironment.class, "customizing" ) );

            //wizardstep#1 (factory settings)
            put( "#mecsim_source_selectWaypointType_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectwaypointtype" ) );
            put( "#mecsim_source_waypointRadius_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectwaypointradius" ) );
            put( "#mecsim_source_selectFactory_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourfactory" ) );
            put( "#mecsim_source_selectAgentProgram_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectyouragentprogram" ) );

            //wizardstep#2 (generator settings)
            put( "#mecsim_source_selectGenerator_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourgenerator" ) );
            put( "#mecsim_source_generatorInputCarcount_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourcarcount" ) );

            //wizardstep#3 (car settings)
            put( "#mecsim_source_speedSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "speedsettingslabel" ) );
            put( "#mecsim_source_selectSpeedProb_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectspeedprob" ) );
            put( "#mecsim_source_maxSpeedSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "maxspeedsettingslabel" ) );
            put( "#mecsim_source_selectMaxSpeedProb_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectmaxspeedprob" ) );
            put( "#mecsim_source_accSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "accsettingslabel" ) );
            put( "#mecsim_source_selectAccProb_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectaccprob" ) );
            put( "#mecsim_source_decSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "decsettingslabel" ) );
            put( "#mecsim_source_selectDecProb_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectdecprob" ) );
            put( "#mecsim_source_lingerSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "lingerersettingslabel" ) );
            put( "#mecsim_source_selectLingerProb_label", CCommon.getResourceString( CLanguageEnvironment.class, "selectlingerprob" ) );

            //wizardstep#4 (customozing)
            put( "#mecsim_source_toolName_label", CCommon.getResourceString( CLanguageEnvironment.class, "selecttoolnamelabel" ) );
            put( "#mecsim_source_toolColor_label", CCommon.getResourceString( CLanguageEnvironment.class, "selecttoolcolor" ) );

            //waypoint config
            put( "#mecsim_source_makrovEditor_label", CCommon.getResourceString( CLanguageEnvironment.class, "makroveditor" ) );
            put( "#mecsim_source_makrovWeighting_label", CCommon.getResourceString( CLanguageEnvironment.class, "makrovweighting" ) );
            put( "#mecsim_source_waypointSettings_label", CCommon.getResourceString( CLanguageEnvironment.class, "waypointsettings" ) );
            put( "#mecsim_source_saveGraph", CCommon.getResourceString( CLanguageEnvironment.class, "savemakrovchain" ) );
            put( "#mecsim_source_resetGraph", CCommon.getResourceString( CLanguageEnvironment.class, "resetmakrovchain" ) );
        }};
    /*
    private final Map<String, String> web_static_getdynamicwaypointlabels()
    {
        return m_dynamicWaypointLabels;
    }

    /**
     * method to read waypoint specific labels and resource strings
     *
     * @return
     *
    private final Map<String, String> web_static_getstaticwaypointlabels()
    {
        return m_staticWaypointLabels;
    }
     */
    private final Map<String, String> web_static_waypointpreset()
    {
        return m_waypointpreset;
    }

}
