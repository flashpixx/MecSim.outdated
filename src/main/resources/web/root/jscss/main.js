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

// --- JQUERY ----------------------------------------------------------------------------------------------------------
$(document).ready(function() {

    $.when(
        $.getScript("jscss/accordion/simulation.js"),
        $.getScript("jscss/accordion/layer.js"),
        $.getScript("jscss/accordion/source.js"),
        $.getScript("jscss/accordion/editor.js"),
        $.getScript("jscss/accordion/mas.js"),
        $.getScript("jscss/accordion/statistics.js"),
        $.getScript("jscss/accordion/help.js")
    ).done(function(){

        // module instantiation
        LayerPanel.init();
        SourcePanel.init();
        MASPanel.init();
        StatisticsPanel.init();
        HelpPanel.init();

        // UI instantiation
        // @todo refactor
        MecSim.ui().screen().jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        MecSim.ui().screenmenu().jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });
        MecSim.ui().accordion().accordion({ active: false, collapsible: true });
        MecSim.ui().inspector().dialog({ autoOpen: false });

        // Editor Panel instantiation
        EditorPanel.bind_ui_actions();

        // Simulation Panel instantiation
        SimulationPanel.bind_ui_actions();

        // create logger websockets access
        MecSim.websocket( "/cconsole/output/log", {
            "onerror"   : function( po_event ) { MecSim.ui().log().prepend("<span class=\"mecsim_log_error\">"  + po_event.data + "</span>");  },
            "onmessage" : function( po_event ) { MecSim.ui().log().prepend("<span class=\"mecsim_log_output\">" + po_event.data + "</span>"); }
        });

        MecSim.websocket( "/cconsole/error/log", {
            "onerror"   : function( po_event ) { MecSim.ui().log().prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); },
            "onmessage" : function( po_event ) { MecSim.ui().log().prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); }
        });

        // create inspector websocket access
        MecSim.websocket( "/cinspector/show", {
            "onerror"   : function( po_event ) { MecSim.ui().log().prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); },
            "onmessage" : function( po_event ) {

                console.log( po_event.data.toJSON() );

                MecSim.ui().inspector().empty();
                //$("#mecsim_object_inspector").prepend("<table id=\"mecsim_inspector_table\"><tbody><tr><td>" + p_event.data[acceleration] + "</td></tr></tbody></table>");
                MecSim.ui().inspector().prepend("<p></p>");
                //$('#mecsim_inspector_table').DataTable();
                MecSim.ui().inspector().dialog("open");

            }
        });

        // create realtime message-flow websocket access
        MecSim.websocket( "/cmessagesystem/flow", {
            "onmessage" : function( po_event ) { console.log( po_event.data.toJSON() ); }
        });


        // --- visualization test ---
/*
        d3.json( "http://mbostock.github.io/d3/talk/20111116/flare-imports.json", function(classes) {

            //console.log(classes);
            //console.log( packages.root(classes) );

            Visualization.HierarchicalEdgeBundling("#mecsim_global_content", {
                id   : "graphtest",
                data : { x : { value : 123, children : ["b", "c"] }, b : { value : 234}, c : { value : 345 } },
                //link : packages.imports
            });

        });
*/
    });
});
