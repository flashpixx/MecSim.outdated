// --- SIMULATION PANEL --------------------------------------------------------------------------------------------

var mecsim_simulation,
    SimulationPanel = {

        settings: {
            start_button: $("#mecsim_simulation_start").button(),
            stop_button: $("#mecsim_simulation_stop").button(),
            reset_button: $("#mecsim_simulation_reset").button()
        },

        init: function() {
            mecsim_simulation = this.settings;
            this.bind_ui_actions();
            this.list_clickable_layer();
            this.list_static_layer();
        },

        bind_ui_actions: function() {

            $("#ui-id-3").on("click", function(data){
                UI().getContent().empty();
            });

            SimulationPanel.settings.start_button.on("click", function(){
                $.post("csimulation/start").fail( function( p_data ) {
                    console.log(p_data);
                    $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_start_error").dialog();
                });

            });

            SimulationPanel.settings.stop_button.on("click", function(){
                $.post("csimulation/stop").fail( function( p_data ) {
                    console.log(p_data);
                    $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_stop_error").dialog();
                });
            });

            SimulationPanel.settings.reset_button.on("click", function(){
                $.post("csimulation/reset")
            });

            $( "#mecsim_speed_slider" ).slider({
                range: "min",
                value: 1,
                min: 0,
                max: 10,
                slide: function( event, ui ) {
                    $( "#speed" ).val( ui.value );
                }
            });

            $( "#speed" ).val( $( "#mecsim_speed_slider" ).slider( "value" ) );

        },

        list_clickable_layer: function() {

            // @todo set must be called
            $.ajax({
                url     : "/cosmviewer/listclickablelayer",
                success : function( px_data ){
                    $.each( px_data, function( pc_key, px_value ) {
                        $( "#mecsim_osmclickablelayer" ).append( "<li class=\"ui-state-default\" id=\"" + px_value.id + "\">" + pc_key + "</li>" );
                    });

                    $( "#mecsim_osmclickablelayer" ).sortable({ placeholder: "ui-state-highlight" });
                    $( "#mecsim_osmclickablelayer" ).disableSelection();
                }
            });

        },

        list_static_layer: function() {

            // @todo switch-buttons must be setup (http://www.bootstrap-switch.org/)
            $.ajax({
                url     : "/csimulation/listlayer",
                success : function( px_data ){
                    $.each( px_data, function( pc_key, px_value ) {
                        $( "#mecsim_simulationlayer" ).append( "<li class=\"ui-widget-content onoffswitch\" id=\"" + px_value.id + "\">"+ pc_key +
                                                               "<input class=\"active\" type=\"checkbox\" " + (px_value.active ? "checked" : "") + ">" +
                                                               "<input class=\"visible\" type=\"checkbox\" " + (px_value.active ? "checked" : "") + ">" +
                                                               "</li>" );
                    });

                    $( "#mecsim_simulationlayer" ).selectable();
                    $( "#mecsim_simulationlayer .active" ).bootstrapSwitch({ size : "mini", "onText" : "active", "offText" : "inactive" });
                    $( "#mecsim_simulationlayer .visible" ).bootstrapSwitch({ size : "mini", "onText" : "visible", "offText" : "invisible" });
                }
            });

        }

    };
