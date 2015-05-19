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

// --- EDITOR PANEL MODULE ---------------------------------------------------------------------------------------------

var EditorPanel = ( function (px_module) {


        // bind dom elements to variables
        /*settings: {
            globalContent: $("#mecsim_global_content"),
            mecsim_agent_files: $("#mecsim_agent_files"),
            new_asl_button: $("#mecsim_new_asl"),
            load_asl_button: $("#mecsim_load_asl").button(),
            delete_asl_button: $("#mecsim_delete_asl").button(),
            save_asl_button: $("#mecsim_save_asl").button(),
            select_asl_menu: $("#mecsim_agent_files").selectmenu(),
            g_editor: {}
        },

        // initialization functions for editor
        init: function() {
            mecsim_editor = this.settings;
            this.initDialog();
            this.bind_ui_actions();
            this.load_asl_files();
        },


        // bind actions to ui elements
        bind_ui_actions: function() {



            // quick fix since #ui-id-8 function from above is not working TODO: fix that
            $("#mecsim_code_mirror_button").button().on("click", function(p_data) {
                MecSim.ui().content().empty();
                EditorPanel.append_tab_div();
                EditorPanel.add_tab();
                $("#tabs").tabs();

                // json object that holds all editor instances
                EditorPanel.settings.g_editor[EditorPanel.get_tab_id()] = CodeMirror($("#" + EditorPanel.get_tab_id() + "")[0], {lineNumbers: true});
                EditorPanel.load_selected_file();
            });

            $("#mecsim_new_asl").button().on("click", function(p_data){
                $("#mecsim_create_asl_form").dialog("open");
            });

            EditorPanel.settings.load_asl_button.on("click", function(p_data){
                EditorPanel.add_tab();
                $("div#tabs").tabs("refresh");

                var tab_index = $('#tabs a[href="#' + EditorPanel.get_tab_id() + '"]').parent().index();
                $('#tabs').tabs( "option", "active", tab_index );
                $("div#tabs").tabs("refresh");

                EditorPanel.add_code_mirror();
                EditorPanel.load_selected_file();
            });

            EditorPanel.settings.delete_asl_button.on("click", function(p_data){
                $.post(
                    "cagentenvironment/jason/delete",
                    { "name" : $("#mecsim_agent_files").val() }
                ).done(function() {
                    EditorPanel.load_asl_files();
                });

            });

            EditorPanel.settings.save_asl_button.on("click", function(p_data){
                $.post(
                    "cagentenvironment/jason/write",
                    { "name" : $("#mecsim_agent_files").val(),
                    "source" : EditorPanel.settings.g_editor[EditorPanel.get_tab_id()].getValue(),
                    "data" : EditorPanel.settings.g_editor[EditorPanel.get_tab_id()].getValue()}
              );
            });

        },

        // add a tab to the editor
        add_tab: function() {
            console.log("hello from addTab");
            // TODO: check if file is already open in another tab
            var selected_file = EditorPanel.get_tab_id();
            $("#tabs ul").append("<li><a href='#" + EditorPanel.get_tab_id() + "'>" + $("#mecsim_agent_files").val() + "</a></li>");
            $("#tabs").append("<div id='" + EditorPanel.get_tab_id() + "'></div>");
        },

        // add a code mirror object to the editor
        add_code_mirror: function() {
            EditorPanel.settings.g_editor[EditorPanel.get_tab_id()] = CodeMirror($("#" + EditorPanel.get_tab_id() + "")[0], {lineNumbers: true});
        },

        // get current tab id of selected file
        get_tab_id: function() {
            return $("#mecsim_agent_files").val().split(".").join("");
        },

        // load selected file into editor
        load_selected_file: function() {

            $.post(
                "cagentenvironment/jason/read",
                { "name" : $("#mecsim_agent_files").val() },
                function( px_data ) {
                    EditorPanel.settings.g_editor[EditorPanel.get_tab_id()].setValue( px_data.source );
                }
            );
        },

        // append new tab <div> elements to dom of editor
        append_tab_div: function() {
             MecSim.ui().content().append("<div id='tabs'><ul></ul></div>");
        },

        // load asl files which are stored under ~/.mecsim/mas/
        load_asl_files: function() {
            $.getJSON( "cagentenvironment/jason/list", function( p_data ) {
                EditorPanel.settings.mecsim_agent_files.empty();
                for(var i in p_data.agents){
                    EditorPanel.settings.mecsim_agent_files
                        .append( $("<option></option>")
                        .attr("value",p_data.agents[i])
                        .text(p_data.agents[i]));
                }
                $("#mecsim_agent_files option:first").attr('selected', true);
                EditorPanel.settings.mecsim_agent_files.selectmenu('refresh');
            });
        },

        // create new asl file which is then stored under ~/.mecsim/mas/
        create_new_asl: function(){

            if( $("#new_asl").val() ) {
                $.post(
                    "cagentenvironment/jason/create",
                    { "name" : $("#new_asl").val() }
                ).done(function() {
                    EditorPanel.load_asl_files();
                    $("#mecsim_create_asl_form").dialog("close");
                });
            } else {
                alert("Please enter a file name");
            }

        }

    };*/

    px_module.ui_actions = function() { return {

        // #ui-id-8 onclick function not working, seems to have a problem with the id
        "initAccordionAction" : function() {

            /*MecSim.ui().accordion().accordion({ active: false, collapsible: true , activate: function(event, ui) {
                console.log(ui.newHeader.context.id);
            }});

            $("#ui-id-8").on("click", function() {

                MecSim.ui().content().empty();
                EditorPanel.append_tab_div();
                EditorPanel.add_tab();
                $("#tabs").tabs();

                // json object that holds all editor instances
                EditorPanel.settings.g_editor[EditorPanel.get_tab_id()] = CodeMirror($("#" + EditorPanel.get_tab_id() + "")[0], {lineNumbers: true});
                EditorPanel.load_selected_file();
            });*/

        },

        // initialization of dialog for creating new files
        "initDialog" : function() {
            $("#mecsim_create_file_form").dialog({
                autoOpen: false,
                buttons: [
                    {
                        text: "Create",
                        click: function() {
                            EditorPanel.create_new_asl();
                            $( this ).dialog( "close" );
                        }
                    },
                    {
                        text: "Cancel",
                        click: function() {
                            $( this ).dialog( "close" );
                        }
                    }
                ]
            });
        },

        "initNewFileButton" : function() {
            $("#mecsim_new_file").button().on("click", function(p_data){
                $("#mecsim_create_file_form").dialog("open");
                $("#mecsim_file_type").selectmenu();
            });
        }

    };}

    // --- UI references -----------------------------------------------------------------------------------------------

    /**
     * references to static UI components of the editor panel
     **/
    px_module.ui = function() {return {

        /** reference to mecsim agent files select menu **/
        "mecsim_agent_files" : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_agent_files" : $("#mecsim_agent_files"); },
        /** reference to 'new file' button **/
        "new_file_button"    : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_new_file"    : $("#mecsim_new_file"); },
        /** reference to 'load file' button **/
        "load_file_button"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_load_file"   : $("#mecsim_load_file"); },
        /** reference to 'delete file' button **/
        "delete_file_button" : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_delete_file" : $( "#mecsim_delete_file" ); },
        /** reference to 'save file' button **/
        "save_file_button"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_save_file"   : $("#mecsim_save_file"); },
        /** reference to 'select file type' menu **/
        "select_file_type_menu"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_file_type"   : $("#mecsim_file_type"); }

    };}
    // -----------------------------------------------------------------------------------------------------------------

    return px_module;

}(EditorPanel || {}));