// --- SIMULATION PANEL --------------------------------------------------------------------------------------------------

var mecsim_simulation,
    SimulationPanel = {

        settings: {
            configuration_button: $("#mecsim_simulation_config").button(),
            local_button: $("#mecsim_simulation_local").button(),
            start_button: $("#mecsim_simulation_start").button(),
            stop_button: $("#mecsim_simulation_stop").button(),
            reset_button: $("#mecsim_simulation_reset").button(),
            load_simulation_button: $("#mecsim_simulation_load").button(),
            save_simulation_button: $("#mecsim_simulation_save").button(),
            slider: $("#mecsim_simulation_speed_slider"),
            speedlabel: $("#mecsim_simulation_speed")
        },

        init: function() {
            mecsim_simulation = this.settings;
            this.bind_ui_actions();
        },

        bind_ui_actions: function() {
            $("#ui-id-1").on("click", function(data){
                MecSim.UI().Content().empty();
                MecSim.UI().Content().load("template/clean.htm");
            });

            SimulationPanel.settings.start_button.on("click", function(){
                $.ajax("csimulation/start").fail( function( p_data ) {
                    console.log(p_data);
                    $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_start_error").dialog();
                });

            });

            SimulationPanel.settings.stop_button.on("click", function(){
                $.ajax("csimulation/stop").fail( function( p_data ) {
                    console.log(p_data);
                    $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_stop_error").dialog();
                });
            });

            SimulationPanel.settings.reset_button.on("click", function(){
                $.ajax("csimulation/reset");
            });

            SimulationPanel.settings.slider.slider({
                range: "min",
                value: 1,
                min: 0,
                max: 10,
                slide: function( event, ui ) {
                    SimulationPanel.settings.speedlabel.val( ui.value );
                }
            });

            SimulationPanel.settings.speedlabel.val(SimulationPanel.settings.slider.slider("value"));

        }

    };
