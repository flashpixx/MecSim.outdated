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
        SimulationPanel.init();
        LayerPanel.init();
        SourcePanel.init();
        MASPanel.init();
        StatisticsPanel.init();
        HelpPanel.init();

        // UI instantiation
        // @todo refactor
        MecSim.ui().screen().jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        MecSim.ui().screenMenu().jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });
        MecSim.ui().accordion().accordion({ active: false, collapsible: true });
        MecSim.ui().inspector().dialog({ autoOpen: false });

        // Editor Panel instantiation
        EditorPanel.ui().mecsim_agent_files().selectmenu();
        EditorPanel.ui().new_file_button().button();
        EditorPanel.ui().load_file_button().button();
        EditorPanel.ui().delete_file_button().button();
        EditorPanel.ui().save_file_button().button();
        EditorPanel.ui_actions().initDialog();
        EditorPanel.ui_actions().initNewFileButton();

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

                console.log( $.parseJSON(po_event.data.clearnull()) );

                MecSim.ui().inspector().empty();
                //$("#mecsim_object_inspector").prepend("<table id=\"mecsim_inspector_table\"><tbody><tr><td>" + p_event.data[acceleration] + "</td></tr></tbody></table>");
                MecSim.ui().inspector().prepend("<p></p>");
                //$('#mecsim_inspector_table').DataTable();
                MecSim.ui().inspector().dialog("open");

            }
        });

        // create realtime message-flow websocket access
        MecSim.websocket( "/cmessagesystem/flow", {
            "onmessage" : function( po_event ) { console.log( $.parseJSON(po_event.data.clearnull()) ); }
        });

        //Visualization.HierarchicalEdgeBundle("#mecsim_global_content", { "id" : "graphtest" });
    });
});
