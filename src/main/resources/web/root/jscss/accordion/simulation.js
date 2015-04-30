// --- SIMULATION PANEL --------------------------------------------------------------------------------------------

var mecsim_simulation,
    SimulationPanel = {

        settings: {
            start_button: $("#mecsim_simulation_start").button(),
            stop_button: $("#mecsim_simulation_stop").button(),
            reset_button: $("#mecsim_simulation_reset").button(),
            slider: $("#mecsim_speed_slider"),
            speedlabel: $("#speed"),
            clickableUI: $("#mecsim_simulation_clickableUI"),
            switchdiv: $("#mecsim_simulation_switchUI")
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

        },

        list_clickable_layer: function() {

            // @todo set must be called
            $.ajax({
                url     : "/cosmviewer/listclickablelayer",
                success : function(px_data){
                    $.each( px_data, function(pc_key, px_value){
                        SimulationPanel.settings.clickableUI.append("<li class='ui-state-default' id="+ px_value.id +">" + pc_key + "</li>" );
                    });

                    SimulationPanel.settings.clickableUI.sortable({ placeholder: "ui-state-highlight" });
                    SimulationPanel.settings.clickableUI.disableSelection();
                }
            });

        },

        list_static_layer: function() {
            //get layer list and create html strucutre
            $.ajax({
                url     : "/csimulation/listlayer",
                success : function( px_data ){
                    $.each( px_data, function( pc_key, px_value ) {
                        $("<label id='mecsim_simulation_switchlabel'>"+ pc_key +"</label>").appendTo(SimulationPanel.settings.switchdiv);
                        $("<input class='mecsim_simulaton_switchActiv' type='checkbox' id='"+ px_value.id +"' checked>").appendTo(SimulationPanel.settings.switchdiv);
                        $("<input class='mecsim_simulaton_switchVisibil' type='checkbox' id='"+ px_value.id +"' checked></p>").appendTo(SimulationPanel.settings.switchdiv);
                    });
                }
            }).done(function(){

                //create active switches and listen
                $(".mecsim_simulaton_switchActiv").bootstrapSwitch({
                    size: "mini",
                    onText: "active",
                    offText: "inactive",
                    onSwitchChange : function( px_event, pl_state) {
                        $.ajax({
                            type: "POST",
                            url: "csimulation/disableenablelayer",
                            data: {"id": $(this).closest("input").attr("id"), "state": pl_state}
                        }).fail(function(p_data){
                            $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                            $("#mecsim_stop_error").dialog();
                        });
                    }
                });

                //create visibility switches and listen
                $(".mecsim_simulaton_switchVisibil").bootstrapSwitch({
                    size: "mini",
                    onText: "visible",
                    offText: "invisible",
                    onSwitchChange : function( px_event, pl_state) {
                        $.ajax({
                            type: "POST",
                            url: "csimulation/hideshowlayer",
                            data: {"id": $(this).closest("input").attr("id"), "state": pl_state}
                        }).fail(function(p_data){
                            $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                            $("#mecsim_stop_error").dialog();
                        });
                    }
                });

            });
        }


    };
