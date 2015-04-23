// --- SIMULATION PANEL --------------------------------------------------------------------------------------------
function simulationSlider(){

    $("#ui-id-3").on("click", function(data){
        UI().getContent().empty();
    });

    $("#mecsim_simulation_start").button().on("click", function(){
        $.post("csimulation/start").fail( function( p_data ) {
            console.log(p_data);
            $("#mecsim_start_error_text").text(p_data.responseJSON.error);
            $("#mecsim_start_error").dialog();
        });
    });

    $("#mecsim_simulation_stop").button().on("click", function(){
        $.post("csimulation/stop").fail( function( p_data ) {
            console.log(p_data);
            $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
            $("#mecsim_stop_error").dialog();
        });
    });

    $("#mecsim_simulation_reset").button().on("click", function(){
        $.post("csimulation/reset")
    });

    $("#mecsim_simulation_load").button().on("click", function(){

    });

    $("#mecsim_simulation_save").button().on("click", function(){

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
}