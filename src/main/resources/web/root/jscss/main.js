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

        // logger
        var ws_logerror = MecSim().getWebSocket("/cconsole/error/log");
        ws_logerror.onmessage = function( p_event ) {
            $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        };
        ws_logerror.onerror = function( p_event ) {
            $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        };

        var ws_logout = MecSim().getWebSocket("/cconsole/output/log");
        ws_logout.onmessage = function( p_event ) {
            $("#mecsim_global_log").prepend("<span class=\"mecsim_log_output\">" + p_event.data + "</span>");
        };
        ws_logout.onerror = function( p_event ) {
            $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        };

        var ws_inspector = MecSim().getWebSocket("/cinspector/show");
        ws_inspector.onerror = function( p_event ) {
            $("#mecsim_global_log").prepend("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        };
        ws_inspector.onmessage = function( p_event ) {
            console.log( $.parseJSON(p_event.data.clearnull()) );

            $("#mecsim_object_inspector").empty();
            //$("#mecsim_object_inspector").prepend("<table id=\"mecsim_inspector_table\"><tbody><tr><td>" + p_event.data[acceleration] + "</td></tr></tbody></table>");
            $("#mecsim_object_inspector").prepend("<p></p>");
            //$('#mecsim_inspector_table').DataTable();
            $("#mecsim_object_inspector").dialog("open");

        };

        var ws_messageflow = MecSim().getWebSocket("/cmessagesystem/flow");
        ws_messageflow.onmessage = function( p_event ) {
            console.log( p_event.data );
        };
    });
});
