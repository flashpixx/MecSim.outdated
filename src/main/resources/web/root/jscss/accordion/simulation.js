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

// --- SIMULATION PANEL MODULE ---------------------------------------------------------------------------------------------

var SimulationPanel = ( function (px_module) {

    // bind actions to ui components
    px_module.bind_ui_actions = function() {

        // load clean.htm when simulation panel is clicked
        SimulationPanel.ui().mecsim_simulation_panel().on("click", function(data){

            if( MecSim.ui().accordion().accordion( "option", "active" ) ) {
                MecSim.ui().content().empty();
                MecSim.ui().content().load("template/clean.htm");
            }

        });

        // start simulation
        SimulationPanel.ui().start_button().button().on("click", function(){
            $.ajax("csimulation/start").fail( function( p_data ) {
                console.log(p_data);
                $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                $("#mecsim_start_error").dialog();
            });

        });

        // stop simulation
        SimulationPanel.ui().stop_button().button().on("click", function(){
            $.ajax("csimulation/stop").fail( function( p_data ) {
                console.log(p_data);
                $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                $("#mecsim_stop_error").dialog();
            });
        });

        // reset simulation
        SimulationPanel.ui().reset_button().button().on("click", function(){
            $.ajax("csimulation/reset");
            MecSim.ui().log().empty();
        });

        // configure speed of simulation
        SimulationPanel.ui().speed_slider().slider({
            range: "min",
            value: 1,
            min: 0,
            max: 10,
            slide: function( event, ui ) {
                SimulationPanel.ui().speed_label().val( ui.value );
            }
        });

        SimulationPanel.ui().speed_label().val(SimulationPanel.ui().speed_slider().slider("value"));

    }

    // --- UI references -----------------------------------------------------------------------------------------------

    /**
     * references to static UI components of the simulation panel
     **/
    px_module.ui = function() {return {

        /** reference to configuration button **/
        "configuration_button" : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_config" : $("#mecsim_simulation_config"); },
        /** reference to local button **/
        "local_button"    : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_local"    : $("#mecsim_simulation_local"); },
        /** reference to start button **/
        "start_button"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_start"   : $("#mecsim_simulation_start"); },
        /** reference to stop button **/
        "stop_button" : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_stop" : $( "#mecsim_simulation_stop" ); },
        /** reference to reset button **/
        "reset_button"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_reset"   : $("#mecsim_simulation_reset"); },
        /** reference to 'load simulation' button **/
        "load_simulation_button"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_load"   : $("#mecsim_simulation_load"); },
        /** reference to 'save simulation' button **/
        "save_simulation_button"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_save"   : $("#mecsim_simulation_save"); },
        /** reference to speed slider **/
        "speed_slider"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_speed_slider"   : $("#mecsim_simulation_speed_slider"); },
        /** reference to speed label **/
        "speed_label"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_speed"   : $("#mecsim_simulation_speed"); },
        /** reference to accordion simulation panel h3 element **/
        "mecsim_simulation_panel"   : function(pc_type) { var lc_type = pc_type || "object";  return lc_type === "id" ? "#mecsim_simulation_panel"   : $("#mecsim_simulation_panel"); }
    };}
    // -----------------------------------------------------------------------------------------------------------------

    return px_module;

}(SimulationPanel || {}));
