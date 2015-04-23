// --- EDITOR PANEL MODULE------------------------------------------------------------------------------------------------

var mecsim_editor,
    EditorPanel = {

        settings: {
            globalContent: $("#mecsim_global_content"),
            mecsim_agent_files: $("#mecsim_agent_files"),
            new_asl_button: $("#mecsim_new_asl").button(),
            load_asl_button: $("#mecsim_load_asl").button(),
            delete_asl_button: $("#mecsim_delete_asl").button(),
            save_asl_button: $("#mecsim_save_asl").button(),
            mecsim_dialog: $("#mecsim_create_asl_form"),
            select_asl_menu: $("#mecsim_agent_files").selectmenu()
        },

        init: function() {
            //this.initDialog();
            //mecsim_editor = this.settings;
            //this.bind_ui_actions();
            //this.load_asl_files();
        },

        initDialog: function() {
            mecsim_editor.settings.mecsim_dialog = mecsim_editor.settings.mecsim_dialog.dialog({
                autoOpen: false,
                buttons: {
                    "Create": mecsim_editor.create_new_asl(),
                    Cancel: function() {
                        mecsim_editor.settings.mecsim_dialog.dialog( "close" );
                    }
                }
            });
        },
        /*
        bind_ui_actions: function() {

            $("#ui-id-7").on("click", function() {
                mecsim_editor.settings.g_editor = CodeMirror(mecsim_editor.settings.globalContent[0], {
                    lineNumbers: true
                })
            });

            mecsim_editor.settings.new_asl_button.on("click", function(p_data){
                mecsim_editor.settings.mecsim_dialog.dialog("open");
            });

            mecsim_editor.settings.load_asl_files.on("click", function(p_data){
                $.post(
                  "cagentenvironment/jason/read",
                  { "name" : $("#mecsim_agent_files").val() },
                  function( px_data ) {
                      mecsim_editor.settings.g_editor.setValue( px_data.source );
                  }
                );
            });

            mecsim_editor.settings.delete_asl_button.on("click", function(p_data){
                $.post(
                    "cagentenvironment/jason/delete",
                    { "name" : $("#mecsim_agent_files").val() }
                ).done(function() {
                    this.load_asl_files();
                });

            });

            mecsim_editor.settings.save_asl_button.on("click", function(p_data){
                $.post(
                    "cagentenvironment/jason/write",
                    { "name" : $("#mecsim_agent_files").val(),
                    "source" : mecsim_editor.settings.g_editor.getValue(),
                    "data" : mecsim_editor.settings.g_editor.getValue()}
              );
            });

        },

        load_asl_files: function() {
            $.getJSON( "cagentenvironment/jason/list", function( p_data ) {
                mecsim_editor.settings.mecsim_agent_files.empty();
                for(var i in p_data.agents){
                    mecsim_editor.settings.mecsim_agent_files
                        .append( $("<option></option>")
                        .attr("value",p_data.agents[i])
                        .text(p_data.agents[i]));
                }
                $("#mecsim_agent_files option:first").attr('selected', true);
                mecsim_editor.settings.mecsim_agent_files.selectmenu('refresh');
            });
        },

        create_new_asl: function(){

            if( $("#new_asl").val() ) {
                $.post(
                    "cagentenvironment/jason/create",
                    { "name" : $("#new_asl").val() }
                ).done(function() {
                    this.load_asl_files();
                    dialog.dialog("close");
                });
            } else {
                alert("Please enter a file name");
            }

        }*/

    };
