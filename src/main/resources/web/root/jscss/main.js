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
        $.getScript("jscss/accordion/help.js")
    ).done(function(){

        // module instantiation
        LayerPanel.init();
        SourcePanel.init();
        MASPanel.init();
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

                MecSim.ui().inspector().empty();
                MecSim.ui().inspector().prepend("<p></p>");
                MecSim.ui().inspector().dialog("open");

            }
        });

        // create realtime message-flow websocket access
        MecSim.websocket( "/cmessagesystem/flow", {
            "onmessage" : function( po_event ) {


                var lo_matrix = {};
                po_event.data.toJSON().cells.forEach( function( po_object ) {

                    // add source node and connecting between source -> target
                    if (!lo_matrix[po_object.source.path])
                        lo_matrix[po_object.source.path] = { children : [], connect : [] };
                    lo_matrix[po_object.source.path].connect.push( po_object.target.path );

                    // add receiver and add it to the parent target
                    if (!lo_matrix[po_object.target.path])
                        lo_matrix[po_object.target.path] = { children : [], connect : [] };
/*
                    for( var i=1, la = po_object.target.path.split(po_object.target.path.sperator), ln_length = la.length; i < ln_length; ++i )
                    {
                        var lc_parent = la.slice(0, i-1).join(po_object.target.path.sperator);
                        if (!lo_matrix[lc_parent])
                            lo_matrix[lc_parent] = { children : [], connect : [] };

                        lo_matrix[lc_parent].children.push(la.slice(0, i).join(po_object.target.path.sperator));
                    }
*/

                } );

//                console.dir(lo_matrix);
/*
                Visualization.HierarchicalEdgeBundling("#mecsim_global_content", {
                    id   : "messageflow",
                    data : lo_matrix
                });
*/
            }
        });


        // --- visualization test ---
/*)
        Visualization.HierarchicalEdgeBundling("#mecsim_global_content", {
            id   : "graphtest",
            //data : { test : { children : ["subtest1", "subtest2"] }, subtest1 : { connect : ["subtest2"] }, subtest2 : {} },
            data : {
                "traffic/car/agentcar 7" : { "children":["traffic/car/agentcar 7/agent"], "connect":[] },
                "traffic/car/agentcar 7/agent" : { "children":[], "connect":["traffic/car/agentcar 5","traffic/car/agentcar 1","traffic/car/agentcar 0"] },

                "traffic/car/agentcar 6" : { "children":["traffic/car/agentcar 6/agent"], "connect":[]},
                "traffic/car/agentcar 6/agent" : { "children":[], "connect":["traffic/car/agentcar 0","traffic/car/agentcar 1","traffic/car/agentcar 5"]},

                "traffic/car/agentcar 5" : { "children":["traffic/car/agentcar 5/agent"], "connect":["traffic/car/agentcar 5/agent"]},
                "traffic/car/agentcar 5/agent":{"children":[],"connect":["traffic/car/agentcar 0","traffic/car/agentcar 1","traffic/car/agentcar 5"]},

                "traffic/car/agentcar 4":{"children":["traffic/car/agentcar 4/agent"],"connect":["traffic/car/agentcar 1","traffic/car/agentcar 0","traffic/car/agentcar 5"]},
                "traffic/car/agentcar 4/agent":{"children":[],"connect":["traffic/car/agentcar 1","traffic/car/agentcar 0","traffic/car/agentcar 5"]},

                "traffic/car/agentcar 3":{"children":["traffic/car/agentcar 3/agent"],"connect":["traffic/car/agentcar 0","traffic/car/agentcar 1"]},
                "traffic/car/agentcar 3/agent":{"children":[],"connect":["traffic/car/agentcar 0","traffic/car/agentcar 1"]},

                "traffic/car/agentcar 2":{"children":["traffic/car/agentcar 2/agent"],"connect":["traffic/car/agentcar 1","traffic/car/agentcar 0"]},
                "traffic/car/agentcar 2/agent":{"children":[],"connect":["traffic/car/agentcar 1","traffic/car/agentcar 0"]},

                "traffic/car/agentcar 1":{"children":[],"connect":["traffic/car/agentcar 1/agent"]},
                "traffic/car/agentcar 1/agent":{"children":[],"connect":["traffic/car/agentcar 1","traffic/car/agentcar 0"]},

                "traffic/car/agentcar 0":{"children":[],"connect":["traffic/car/agentcar 0/agent"]},
                "traffic/car/agentcar 0/agent":{"children":[],"connect":["traffic/car/agentcar 0"]}
            }
        });
*/
    });
});
