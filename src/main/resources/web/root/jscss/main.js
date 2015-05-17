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
        EditorPanel.init();
        MASPanel.init();
        StatisticsPanel.init();
        HelpPanel.init();

        // UI instantiation
        // @todo refactor
        MecSim.UI().screen.jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        MecSim.UI().menu.jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });
        MecSim.UI().accordion.accordion({ active: false, collapsible: true }),
        MecSim.UI().inspector.dialog({ autoOpen: false });

        // create logger websockets access
        MecSim.WebSocket( "/cconsole/output/log", {
            "onerror"   : function( po_event ) { MecSim.UI().log.prepend("<span class=\"mecsim_log_error\">"  + po_event.data + "</span>");  },
            "onmessage" : function( po_event ) { MecSim.UI().log.prepend("<span class=\"mecsim_log_output\">" + po_event.data + "</span>"); }
        });

        MecSim.WebSocket( "/cconsole/error/log", {
            "onerror"   : function( po_event ) { MecSim.UI().log.prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); },
            "onmessage" : function( po_event ) { MecSim.UI().log.prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); }
        });

        // create inspector websocket access
        MecSim.WebSocket( "/cinspector/show", {
            "onerror"   : function( po_event ) { MecSim.UI().log.prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); },
            "onmessage" : function( po_event ) {

                console.log( $.parseJSON(po_event.data.clearnull()) );

                MecSim.UI().inspector.empty();
                //$("#mecsim_object_inspector").prepend("<table id=\"mecsim_inspector_table\"><tbody><tr><td>" + p_event.data[acceleration] + "</td></tr></tbody></table>");
                MecSim.UI().inspector.prepend("<p></p>");
                //$('#mecsim_inspector_table').DataTable();
                MecSim.UI().inspector.dialog("open");

            }
        });

        // create realtime message-flow websocket access
        MecSim.WebSocket( "/cmessagesystem/flow", {
            "onmessage" : function( po_event ) { console.log( $.parseJSON(po_event.data.clearnull()) ); }
        });

    });
});
