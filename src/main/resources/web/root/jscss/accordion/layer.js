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

//"use strict";

// --- LAYER PANEL --------------------------------------------------------------------------------------------

var mecsim_layer,
    LayerPanel = {

        settings: {
            clickableUI: $("#mecsim_layer_clickableUI"),
            switchdiv: $("#mecsim_layer_switchUI")
        },

        init: function() {
            mecsim_layer = this.settings;
            this.bind_ui_actions();
            this.list_clickable_layer();
            this.list_static_layer();
        },

        bind_ui_actions: function() {
            $("#mecsim_layer_panel").on("click", function(data){
                MecSim.ui().content().empty();
                MecSim.ui().content().load("template/clean.htm");
            });
        },

        list_clickable_layer: function() {
            $.ajax({
                url     : "/cosmviewer/listclickablelayer",
                success : function(px_data){

                    // sort JSON objects depend on "click" property and store the ordered list in an array
                    var la_sorted = [];
                    Object.keys(px_data).sort(function(i,j){ return px_data[i].click ? -1 : 1 }).forEach(function(pc_key){ var lo=px_data[pc_key]; lo.name = pc_key; la_sorted.push(lo); });

                    $.each( la_sorted, function(pn_key, px_value){
                        LayerPanel.settings.clickableUI.append("<li class='ui-state-default' id="+ px_value.id +">" + px_value.name + "</li>" );
                    });

                    LayerPanel.settings.clickableUI.sortable({
                        placeholder: "ui-state-highlight",
                        stop: function(px_event, po_ui) {
                            $.ajax({
                                type: "POST",
                                url : "/cosmviewer/setclickablelayer",
                                data: {"id": $(this).children(".ui-sortable-handle:first").attr("id")}
                            });
                        }
                    });
                    LayerPanel.settings.clickableUI.disableSelection();
                }
            });
        },

        //TODO fix: undo if action failed
        list_static_layer: function() {
            //get layer list and create html strucutre
            $.ajax({
                url     : "/csimulation/listlayer",
                success : function( px_data ){
                    $.each( px_data, function( pc_key, px_value ) {
                        $("<label id='mecsim_simulation_switchlabel'>"+ pc_key +"</label>").appendTo(LayerPanel.settings.switchdiv);
                        $("<input class='mecsim_simulation_switchActive' type='checkbox' id='"+ px_value.id +"' "+ (px_value.active ? "checked" : "") +">").appendTo(LayerPanel.settings.switchdiv);
                        if(px_value.isviewable)
                            $("<input class='mecsim_simulation_switchVisible' type='checkbox' id='"+ px_value.id +"' " + (px_value.visible ? "checked" : "") + "></p>").appendTo(LayerPanel.settings.switchdiv);
                    });
                }
            }).done(function(){

                // fail closure
                var lx_failClosure = function( px_event ) {
                    return function(p_data) {
                        $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                        $("#mecsim_stop_error").dialog();
                    }
                }

                //create active switches and listen
                $(".mecsim_simulation_switchActive").bootstrapSwitch({
                    size: "mini",
                    onText: "active",
                    offText: "inactive",
                    onSwitchChange : function( px_event, pl_state) {
                        $.ajax({
                            type: "POST",
                            url: "/csimulation/disableenablelayer",
                            data: {"id": $(this).closest("input").attr("id"), "state": pl_state}
                        }).fail( lx_failClosure(px_event) );
                    }
                });

                //create visibility switches and listen
                $(".mecsim_simulation_switchVisible").bootstrapSwitch({
                    size: "mini",
                    onText: "visible",
                    offText: "invisible",
                    onSwitchChange : function( px_event, pl_state) {
                        $.ajax({
                            type: "POST",
                            url: "/csimulation/hideshowlayer",
                            data: {"id": $(this).closest("input").attr("id"), "state": pl_state}
                        }).fail(lx_failClosure(px_event) );
                    }
                });

            });
        }

    };
