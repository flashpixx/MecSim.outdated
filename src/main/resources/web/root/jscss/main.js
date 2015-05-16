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

        // singleton instantiation
        Logger();
        MecSim();
        UI();

        // module instantiation
        SimulationPanel.init();
        LayerPanel.init();
        SourcePanel.init();
        EditorPanel.init();
        MASPanel.init();
        StatisticsPanel.init();
        HelpPanel.init();

        $( "#mecsim_object_inspector" ).dialog({
            autoOpen: false,
        });

        // splitter
        $("#mecsim_global_screen").jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        $("#mecsim_global_screen_right").jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });


        // create logger websockets access
        MecSimNew.connectWebSocket( "/cconsole/output/log", {
            "onerror"   : function( po_event ) { $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">"  + po_event.data + "</span>");  },
            "onmessage" : function( po_event ) { $("#mecsim_global_log").prepend("<span class=\"mecsim_log_output\">" + po_event.data + "</span>"); }
        });

        MecSimNew.connectWebSocket( "/cconsole/error/log", {
            "onerror"   : function( po_event ) { $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); },
            "onmessage" : function( po_event ) { $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); }
        });


        // create inspector websocket access
        MecSimNew.connectWebSocket( "/cinspector/show", {
            "onerror"   : function( po_event ) { $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + po_event.data + "</span>"); },
            "onmessage" : function( po_event ) {

                console.log( $.parseJSON(po_event.data.clearnull()) );

                $("#mecsim_object_inspector").empty();
                //$("#mecsim_object_inspector").prepend("<table id=\"mecsim_inspector_table\"><tbody><tr><td>" + p_event.data[acceleration] + "</td></tr></tbody></table>");
                $("#mecsim_object_inspector").prepend("<p></p>");
                //$('#mecsim_inspector_table').DataTable();
                $("#mecsim_object_inspector").dialog("open");

            }
        });


        // create realtime message-flow websocket access
        MecSimNew.connectWebSocket( "/cmessagesystem/flow", {
            "onmessage" : function( po_event ) { console.log( $.parseJSON(po_event.data.clearnull()) ); }
        });



    });
});
