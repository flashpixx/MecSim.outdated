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

"use strict";

// --- HELP PANEL ------------------------------------------------------------------------------------------------------

var HelpPanel = (function (px_module) {

    px_module.bind_ui_actions = function() {

        HelpPanel.ui().help_panel().on("click", function(data){
            MecSim.ui().content().empty();
        });

        HelpPanel.ui().about_button().button().on("click", function(){

            $.getJSON( "cconfiguration/get", function( p_data ) {
                $("#mecsim_project_name")
                        .attr("href", p_data.manifest["project-url"])
                        .text(p_data.manifest["project-name"]);

                $("#mecsim_license")
                        .attr("href", p_data.manifest["license-url"])
                        .text(p_data.manifest["license"]);

                $("#mecsim_buildversion").text(p_data.manifest["build-version"]);
                $("#mecsim_buildnumber").text(p_data.manifest["build-number"]);
                $("#mecsim_buildcommit").text(p_data.manifest["build-commit"]);

            }).done( function() {
                $("#mecsim_about").dialog({
                    width: 500,
                    modal: true
                });
            });

        });

        HelpPanel.ui().user_doc_button().button().on("click", function(){
            $.get("/userdoc/", function( p_result ) {
                MecSim.ui().content().empty();
                MecSim.ui().content().append( p_result );
            });
        });

        HelpPanel.ui().developer_doc_button().button().on("click", function(){
            MecSim.ui().content().load("template/develdoc.htm");
        });

    }

    // --- UI references ---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * references to static UI components
     **/
    px_module.ui = function() {return {

        /** reference to about button **/
        about_button : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_help_about"    : $("#mecsim_help_about"); },
        /** reference to documentation button **/
        user_doc_button : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_help_userdoku"    : $("#mecsim_help_userdoku"); },
        /** reference to developer documentation button **/
        developer_doc_button : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_help_devdoku"    : $("#mecsim_help_devdoku"); },
        /** reference to help panel h3 element **/
        help_panel : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_help_panel"    : $("#mecsim_help_panel"); }

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return px_module;

}(HelpPanel || {}));
