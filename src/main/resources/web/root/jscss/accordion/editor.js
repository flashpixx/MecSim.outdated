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

    px_module.g_editor = {}

    px_module.ui_actions = function() { return {

        // load asl files which are stored under ~/.mecsim/mas/
        "load_agent_files" : function() {
            $.getJSON( "cagentenvironment/jason/list", function( p_data ) {
                $("#mecsim_agent_files").empty();
                for(var i in p_data.agents){
                    $("#mecsim_agent_files")
                        .append( $("<option></option>")
                        .attr("value",p_data.agents[i])
                        .text(p_data.agents[i]));
                }
                $("#mecsim_agent_files option:first").attr('selected', true);
                $("#mecsim_agent_files").selectmenu('refresh');
            });
        },

        // load selected file into editor
        "load_selected_file" : function() {

            $.ajax({
                url : "/cagentenvironment/jason/read",
                type: "POST",
                data: { "name" : $("#mecsim_agent_files").val() },
                success : function( px_data )
                {
                    EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()].setValue( px_data.source );
                }
            });

        },

        // append new tab <div> elements to dom of editor
        "append_tab_div" : function() {
             MecSim.ui().content().append("<div id='tabs'><ul></ul></div>");
        },

        // add a tab to the editor
        "add_tab" : function() {
            // TODO: check if file is already open in another tab
            var selected_file = EditorPanel.ui_actions().get_tab_id();
            $("#tabs ul").append("<li><a href='#" + EditorPanel.ui_actions().get_tab_id() + "'>" + $("#mecsim_agent_files").val() + "</a></li>");
            $("#tabs").append("<div id='" + EditorPanel.ui_actions().get_tab_id() + "'></div>");
        },

        // get current tab id of selected file
        "get_tab_id" : function() {
            return $("#mecsim_agent_files").val().split(".").join("");
        },

        // initialization of dialog for creating new files
        "initDialog" : function() {
            $("#mecsim_create_file_form").dialog({
                autoOpen: false,
                buttons: [
                    {
                        text: "Create",
                        click: function() {
                            EditorPanel.ui_actions().create_new_file();
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

        // create new file which is then stored under ~/.mecsim/mas/
        "create_new_file" : function(){

            if( $("#new_file").val() ) {

                $.ajax({
                    url : "/cagentenvironment/jason/create",
                    type: "POST",
                    data: { "name" : $("#new_file").val() },
                    success : function( px_data )
                    {
                        EditorPanel.ui_actions().load_agent_files();
                        $("#mecsim_create_file_form").dialog("close");
                    }
                });

            } else {
                alert("Please enter a file name");
            }

        },

        // add a code mirror object to the editor
        "add_code_mirror" : function() {
            EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()] = CodeMirror($("#" + EditorPanel.ui_actions().get_tab_id() + "")[0], {lineNumbers: true});
            //$("#" + EditorPanel.ui_actions().get_tab_id() + "").css("height", "100%");
        }

    };}

    px_module.bind_ui_actions = function() {

        // bind select menu, initialize dialog, and load agent files
        EditorPanel.ui().mecsim_agent_files().selectmenu();
        EditorPanel.ui_actions().initDialog();
        EditorPanel.ui_actions().load_agent_files();

        // initialization of editor panel action
        EditorPanel.ui().mecsim_editor_panel().on("click", function() {

            if( MecSim.ui().accordion().accordion( "option", "active" ) ) {

                MecSim.ui().content().empty();
                EditorPanel.ui_actions().append_tab_div();
                EditorPanel.ui_actions().add_tab();
                $("#tabs").tabs();

                // json object that holds all editor instances
                EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()] = CodeMirror($("#" + EditorPanel.ui_actions().get_tab_id() + "")[0], {lineNumbers: true});
                EditorPanel.ui_actions().load_selected_file();

            }
        });

        // create new file
        EditorPanel.ui().new_file_button().button().on("click", function(p_data){
            $("#mecsim_create_file_form").dialog("open");
            $("#mecsim_file_type").selectmenu();
        });

        // load file
        EditorPanel.ui().load_file_button().button().on("click", function(p_data){
            EditorPanel.ui_actions().add_tab();
            $("div#tabs").tabs("refresh");

            var tab_index = $('#tabs a[href="#' + EditorPanel.ui_actions().get_tab_id() + '"]').parent().index();
            $('#tabs').tabs( "option", "active", tab_index );
            $("div#tabs").tabs("refresh");

            EditorPanel.ui_actions().add_code_mirror();
            EditorPanel.ui_actions().load_selected_file();
        });

        // delete file
        EditorPanel.ui().delete_file_button().button().on("click", function(p_data){

            $.ajax({
                url : "/cagentenvironment/jason/delete",
                type: "POST",
                data: { "name" : $("#mecsim_agent_files").val() },
                success : function( px_data )
                {
                    EditorPanel.ui_actions().load_agent_files();
                }
            });

        });

        // save file
        // TODO: parametrize agent file type (e.g. jason, goal)
        EditorPanel.ui().save_file_button().button().on("click", function(p_data){

            $.ajax({
                url : "/cagentenvironment/jason/write",
                type: "POST",
                data: { "name" : $("#mecsim_agent_files").val(),
                "source" : EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()].getValue(),
                "data" : EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()].getValue()},
                success : function( px_data )
                {
                    console.log("file saved");
                }
            });

        });

    }

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
        "select_file_type_menu" : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_file_type"   : $("#mecsim_file_type"); },
        /** reference to accordion editor panel h3 element **/
        "mecsim_editor_panel"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_editor_panel"   : $("#mecsim_editor_panel"); }
    };}
    // -----------------------------------------------------------------------------------------------------------------

    return px_module;

}(EditorPanel || {}));