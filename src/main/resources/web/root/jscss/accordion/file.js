// --- FILE PANEL --------------------------------------------------------------------------------------------------

var mecsim_file,
    FilePanel = {

        settings: {
            configuration_button: $("#mecsim_file_config").button(),
            local_button: $("#mecsim_file_local").button(),
            load_simulation_button: $("#mecsim_simulation_load").button(),
            save_simulation_button: $("#mecsim_simulation_save").button()
        },

        init: function() {
            mecsim_file = this.settings;
            this.bind_ui_actions();
        },

        bind_ui_actions: function() {

            $("#ui-id-1").on("click", function(data){
                UI().getContent().empty();
            });

        }

    };
