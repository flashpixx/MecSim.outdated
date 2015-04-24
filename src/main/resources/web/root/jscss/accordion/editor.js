// --- EDITOR PANEL MODULE------------------------------------------------------------------------------------------------

var mecsim_editor,
    EditorPanel = {

        settings: {
            globalContent: $("#mecsim_global_content"),
            mecsim_agent_files: $("#mecsim_agent_files"),
            new_asl_button: $("#mecsim_new_asl"),
            load_asl_button: $("#mecsim_load_asl").button(),
            delete_asl_button: $("#mecsim_delete_asl").button(),
            save_asl_button: $("#mecsim_save_asl").button(),
            select_asl_menu: $("#mecsim_agent_files").selectmenu()
        },

        init: function() {
            mecsim_editor = this.settings;
            this.initDialog();
            this.bind_ui_actions();
            this.load_asl_files();
        },

        initDialog: function() {
            $("#mecsim_create_asl_form").dialog({
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

        bind_ui_actions: function() {

            $("#ui-id-7").on("click", function() {
                UI().getContent().empty();
                EditorPanel.append_tab_div();
                EditorPanel.add_tab();
                $("#tabs").tabs();
                EditorPanel.settings.g_editor = CodeMirror($("#" + EditorPanel.get_tab_id() + "")[0], {
                    lineNumbers: true
                });
                EditorPanel.load_selected_file();
                console.log(EditorPanel.settings.g_editor.EditorPanel.get_tab_id());
            });

            $("#mecsim_new_asl").button().on("click", function(p_data){
                $("#mecsim_create_asl_form").dialog("open");
            });

            EditorPanel.settings.load_asl_button.on("click", function(p_data){
                EditorPanel.add_tab();
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
                    "source" : EditorPanel.settings.g_editor.getValue(),
                    "data" : EditorPanel.settings.g_editor.getValue()}
              );
            });

        },

        add_tab: function() {
            // TODO: check if file is already open in another tab
            $("#tabs ul").append("<li><a href='#" + EditorPanel.get_tab_id() + "'>" + $("#mecsim_agent_files").val() + "</a></li>");
            $("#tabs").append("<div id='" + EditorPanel.get_tab_id() + "'></div>");
        },

        add_code_mirror: function() {

        },

        get_tab_id: function() {
            return $("#mecsim_agent_files").val().split(".").join("");
        },

        load_selected_file: function() {
            $.post(
                "cagentenvironment/jason/read",
                { "name" : $("#mecsim_agent_files").val() },
                function( px_data ) {
                  EditorPanel.settings.g_editor.setValue( px_data.source );
                }
            );
        },

        append_tab_div: function() {

             UI().getContent().append("<div id='tabs'><ul></ul></div>");

        },

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

    };
