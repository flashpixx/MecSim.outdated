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

// --- HELP PANEL ------------------------------------------------------------------------------------------------------

//"use strict"

var mecsim_help,
    HelpPanel = {

        settings: {
            about_button: $("#mecsim_help_about").button(),
            user_doc_button: $("#mecsim_help_userdoku").button(),
            developer_doc_button: $("#mecsim_help_devdoku").button()
        },

        init: function() {
            mecsim_help = this.settings;
            this.bind_ui_actions();
        },

        bind_ui_actions: function() {

            $("#mecsim_help_panel").on("click", function(data){
                MecSim.ui().content().empty();
            });

            HelpPanel.settings.about_button.on("click", function(){

                $.getJSON( "cconfiguration/get", function( p_data ) {
                    console.log(p_data);
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

            HelpPanel.settings.user_doc_button.on("click", function(){
                $.get("/userdoc/", function( p_result ) {
                    console.log(p_result);
                    MecSim.ui().content().empty();
                    MecSim.ui().content().append( p_result );
                });
            });

            HelpPanel.settings.developer_doc_button.on("click", function(){
                MecSim.ui().content().load("template/develdoc.htm");
            });

        }

    };
