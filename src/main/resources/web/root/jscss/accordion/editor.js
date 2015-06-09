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

    var save_interval;
    var opened_tabs = [];

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
            var selected_file = EditorPanel.ui_actions().get_tab_id();
            $("#tabs ul").append("<li id='" + EditorPanel.ui_actions().get_tab_id() + "_li'><a id='" + EditorPanel.ui_actions().get_tab_id() + "_tab' href='#" + EditorPanel.ui_actions().get_tab_id() + "'>" + $("#mecsim_agent_files").val() + "</a><span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>");
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
        },

        // save current selected file
        "save_file" : function() {

            var selected_tab_id = $(
                 '#tabs > div:eq(' + $('#tabs').tabs('option', 'active') + ')'
            ).get(0).id;

            $.ajax({
                url : "/cagentenvironment/jason/write",
                type: "POST",
                data: { "name" : $("#mecsim_agent_files").val(),
                "source" : EditorPanel.g_editor[selected_tab_id].getValue(),
                "data" : EditorPanel.g_editor[selected_tab_id].getValue()},
                success : function( px_data )
                {
                    console.log("file saved");
                }
            });
        }

    };}

    px_module.bind_ui_actions = function() {

        // bind select menu, initialize dialog, and load agent files
        EditorPanel.ui().mecsim_agent_files().selectmenu();
        EditorPanel.ui_actions().initDialog();
        EditorPanel.ui_actions().load_agent_files();



        // initialization of editor panel action
        EditorPanel.ui().mecsim_editor_panel().on("click", function() {

            // TODO: if another accordion tab is opened, timer goes on
            if( MecSim.ui().accordion().accordion( "option", "active" ) ) {

                MecSim.ui().content().empty();

                // open all files previously opened

                if(opened_tabs.length == 0){
                    EditorPanel.ui_actions().append_tab_div();
                    EditorPanel.ui_actions().add_tab();
                    $("#tabs").tabs();

                    // close icon: removing the tab on click TODO: if last is removed no file can be added anymore
                    $("#tabs").tabs().delegate( "span.ui-icon-close", "click", function() {
                    var panelId = $( this ).closest( "li" ).remove().attr( "aria-controls" );
                    $( "#" + panelId ).remove();
                        $("#tabs").tabs( "refresh" );
                    });

                    // json object that holds all editor instances
                    EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()] = CodeMirror($("#" + EditorPanel.ui_actions().get_tab_id() + "")[0], {lineNumbers: true});
                    EditorPanel.ui_actions().load_selected_file();
                } else {
                    var tab_id;
                    for(tab_id in opened_tabs){
                        // TODO: load opened files
                        //EditorPanel.ui_actions().append_tab_div();
                        //EditorPanel.ui_actions().add_tab();
                        //$("#tabs").tabs();
                    }
                }



                // save current selected file every five seconds
                save_interval = setInterval(function () {
                    EditorPanel.ui_actions().save_file();
                }, 5000);

            } else {
                clearInterval(save_interval);
                EditorPanel.ui_actions().save_file();
            }
        });

        // create new file
        EditorPanel.ui().new_file_button().button().on("click", function(p_data){
            $("#mecsim_create_file_form").dialog("open");
            $("#mecsim_file_type").selectmenu();
        });

        // load file
        EditorPanel.ui().load_file_button().button().on("click", function(p_data){

            if( EditorPanel.g_editor[EditorPanel.ui_actions().get_tab_id()] ) {

               // focus tab which is already loaded
               $("#" + EditorPanel.ui_actions().get_tab_id() + "_tab").trigger("click");

            } else {

                // create new tab and load file content
                EditorPanel.ui_actions().add_tab();
                $("div#tabs").tabs("refresh");

                var tab_index = $('#tabs a[href="#' + EditorPanel.ui_actions().get_tab_id() + '"]').parent().index();
                $('#tabs').tabs( "option", "active", tab_index );
                $("div#tabs").tabs("refresh");

                EditorPanel.ui_actions().add_code_mirror();
                EditorPanel.ui_actions().load_selected_file();
            }

        });

        // delete file
        EditorPanel.ui().delete_file_button().button().on("click", function(p_data){
            EditorPanel.ui().mecsim_editor_delete_confirmation().dialog({
                width: 500,
                modal: true
            });
        });

        // save file button action
        // TODO: parametrize agent file type (e.g. jason, goal)
        EditorPanel.ui().save_file_button().button().on("click", function(p_data){
            EditorPanel.ui_actions().save_file();
        });

        EditorPanel.ui().mecsim_editor_delete_file_yes().button().on("click", function() {

            $.ajax({
                url : "/cagentenvironment/jason/delete",
                type: "POST",
                data: { "name" : $("#mecsim_agent_files").val() },
                success : function( px_data )
                {
                    EditorPanel.ui_actions().load_agent_files();
                }
            });

            // remove tab
            $("#" + EditorPanel.ui_actions().get_tab_id() + "_li").remove();
            $("#" + EditorPanel.ui_actions().get_tab_id()).remove();
            $("#tabs").tabs("refresh");

            EditorPanel.ui().mecsim_editor_delete_confirmation().dialog("close");

        });

        EditorPanel.ui().mecsim_editor_delete_file_no().button().on("click", function() {
            EditorPanel.ui().mecsim_editor_delete_confirmation().dialog("close");
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
        "mecsim_editor_panel"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_editor_panel"   : $("#mecsim_editor_panel"); },
        /** reference to editor delete confirmation popup **/
        "mecsim_editor_delete_confirmation"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_editor_delete_confirmation"   : $("#mecsim_editor_delete_confirmation"); },
        /** reference to editor delete file 'yes' button **/
        "mecsim_editor_delete_file_yes"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_editor_delete_file_yes"   : $("#mecsim_editor_delete_file_yes"); },
        /** reference to editor delete file 'no' button **/
        "mecsim_editor_delete_file_no"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_editor_delete_file_no"   : $("#mecsim_editor_delete_file_no"); }
    };}
    // -----------------------------------------------------------------------------------------------------------------

    return px_module;

}(EditorPanel || {}));