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
 * class which is responsible for multi language support in the UI
 */
@SuppressWarnings( "serial" )
public class CLanguageEnvironment
{
    /**
     * labels of the configuration elements
     */
    private static final Map<String, String> c_configurationelements = new HashMap<String, String>()
    {{
            put( "label_uuid", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_uuid" ) );
            put( "label_reset", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_reset" ) );
            put( "label_extractmasexamples", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_extractmasexample" ) );
            put( "label_deleteonshutdown", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_deleteonshutdown" ) );
            put( "label_language_current", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_language_current" ) );
            put( "label_ui_server_host", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_ui_server_host" ) );
            put( "label_ui_server_port", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_ui_server_port" ) );
            put(
                    "label_ui_server_websocketheartbeat", CCommon.getResourceString(
                            CLanguageEnvironment.class, "ui_configuration_ui_server_websocketheartbeat"
                    )
            );
            put( "label_ui_routepainterdelay", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_ui_routepainterdelay" ) );
            put(
                    "label_simulation_traffic_cellsampling", CCommon.getResourceString(
                            CLanguageEnvironment.class, "ui_configuration_simulation_traffic_cellsampling"
                    )
            );
            put(
                    "label_simulation_traffic_timesampling", CCommon.getResourceString(
                            CLanguageEnvironment.class, "ui_configuration_simulation_traffic_timesampling"
                    )
            );
            put(
                    "label_simulation_traffic_map_current", CCommon.getResourceString(
                            CLanguageEnvironment.class, "ui_configuration_simulation_traffic_map_current"
                    )
            );
            put(
                    "label_simulation_traffic_map_reimport", CCommon.getResourceString(
                            CLanguageEnvironment.class, "ui_configuration_simulation_traffic_map_reimport"
                    )
            );
            put(
                    "label_simulation_traffic_routing_algorithm", CCommon.getResourceString(
                            CLanguageEnvironment.class, "ui_configuration_simulation_traffic_routing_algorithm"
                    )
            );
            put( "label_database_active", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database_active" ) );
            put( "label_database_driver", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database_driver" ) );
            put( "label_database_url", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database_url" ) );
            put( "label_database_username", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database_username" ) );
            put( "label_database_password", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database_password" ) );
            put( "label_database_tableprefix", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database_tableprefix" ) );
        }};
    /**
     * labels of the configuration menu
     */
    private static final Map<String, String> c_configurationheader = new HashMap<String, String>()
    {{
            put( "id_general", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_general" ) );
            put( "id_ui", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_ui" ) );
            put( "id_simulation", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_simulation" ) );
            put( "id_database", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_database" ) );
        }};
    /**
     * labels of the configuration header
     */
    private static final Map<String, String> c_configurationlabel = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_name" ) );
            put( "id_mappopup", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_mappopup" ) );
            put( "mappopup_content", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_mappopup_content" ) );
            put( "id_addgraph", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_addgraph" ) );
            put( "id_removegraph", CCommon.getResourceString( CLanguageEnvironment.class, "ui_configuration_removegraph" ) );
        }};
    /**
     * labels of the help menu
     */
    private static final Map<String, String> c_help = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_help_name" ) );
            put( "id_about", CCommon.getResourceString( CLanguageEnvironment.class, "ui_help_about" ) );
            put( "id_userdoc", CCommon.getResourceString( CLanguageEnvironment.class, "ui_help_userdoc" ) );
            put( "id_devdoc", CCommon.getResourceString( CLanguageEnvironment.class, "ui_help_devdoc" ) );
        }};
    /**
     * labels of the object inspector
     */
    private static final Map<String, String> c_inspector = new HashMap<String, String>()
    {{
            put( "title_dialog", CCommon.getResourceString( CLanguageEnvironment.class, "ui_inspector_dialogtitle" ) );
        }};
    /**
     * labels of the layer menu
     */
    private static final Map<String, String> c_layer = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_layer_name" ) );
            put( "title_dialog", CCommon.getResourceString( CLanguageEnvironment.class, "ui_layer_dialogtitle" ) );
        }};
    /**
     * labels of the MAS menu
     */
    private static final Map<String, String> c_mas = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_name" ) );
        }};
    /**
     * labels of the MAS Jasonmind menu
     */
    private static final Map<String, String> c_mascommunicate = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_communication" ) );
        }};
    /**
     * labels of the MAS editor menu
     */
    private static final Map<String, String> c_maseditor = new HashMap<String, String>()
    {{
            put( "id_new", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_editornew" ) );
            put( "id_remove", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_editorremove" ) );
            put( "id_check", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_editorcheck" ) );
            put( "title_dialog", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_editordialogtitle" ) );
            put( "label_agents", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_editoragentlist" ) );
        }};
    /**
     * labels of the MAS mind inspector menu
     */
    private static final Map<String, String> c_masmind = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_mas_jasonmind" ) );
        }};
    /**
     * labels of the simulation menu
     */
    private static final Map<String, String> c_simulation = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_name" ) );
            put( "id_start", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_start" ) );
            put( "id_stop", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_stop" ) );
            put( "id_reset", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_reset" ) );
            put( "id_load", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_load" ) );
            put( "id_save", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_save" ) );
            put( "title_dialog", CCommon.getResourceString( CLanguageEnvironment.class, "ui_simulation_dialogtitle" ) );
        }};
    /**
     * labels of the traffic menu
     */
    private static final Map<String, String> c_traffic = new HashMap<String, String>()
    {{
            put( "name", CCommon.getResourceString( CLanguageEnvironment.class, "ui_traffic_name" ) );
            put( "title_dialog", CCommon.getResourceString( CLanguageEnvironment.class, "ui_traffic_dialogtitle" ) );
        }};

    /**
     * returns all dynamic label for the configuration menu
     *
     * @return map with dynamic labels
     */
    private final Map<String, String> web_static_configurationelements()
    {
        return c_configurationelements;
    }

    /**
     * returns all static label for the configuration menu
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_configurationheader()
    {
        return c_configurationheader;
    }

    /**
     * returns all static label for the configuration menu
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_configurationlabel()
    {
        return c_configurationlabel;
    }

    /**
     * returns all static label for the MAS
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_help()
    {
        return c_help;
    }

    /**
     * returns all static label for the object inspector
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_inspector()
    {
        return c_inspector;
    }

    /**
     * returns all static label for the layer menu
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_layer()
    {
        return c_layer;
    }

    /**
     * returns all static label for the MAS
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_mas()
    {
        return c_mas;
    }

    /**
     * returns all static label for the MAS communicator
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_mascommunicate()
    {
        return c_mascommunicate;
    }

    /**
     * returns all static label for the MAS editor
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_maseditor()
    {
        return c_maseditor;
    }

    /**
     * returns all static label for the MAS mind inspectorr
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_masmind()
    {
        return c_masmind;
    }

    /**
     * returns all static label for the simulation
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_simulation()
    {
        return c_simulation;
    }

    /**
     * returns all static label for the traffic menu
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_traffic()
    {
        return c_traffic;
    }
}
