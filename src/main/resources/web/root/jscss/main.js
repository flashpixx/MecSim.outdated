// --- JQUERY ----------------------------------------------------------------------------------------------------------
$(document).ready(function() {

$.when(
    $.getScript("jscss/accordion/editor/editor.js"),
    $.getScript("jscss/accordion/file/file.js"),
    $.getScript("jscss/accordion/help/help.js"),
    $.getScript("jscss/accordion/simulation/simulation.js"),
    $.getScript("jscss/accordion/source/source.js"),
    $.getScript("jscss/accordion/statistics/statistics.js")
).done(function(){

    console.log("Foobar");

    // singleton instantiation
    Logger();
    MecSim();
    UI();

    // module instantiation
    EditorPanel.init();

    // splitter
    $("#mecsim_global_screen").jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
    $("#mecsim_global_screen_right").jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });

    // logger
    var ws_logerror = MecSim().getWebSocket("/cconsole/error/log");
    ws_logerror.onmessage = function( p_event ) {
        $("#mecsim_global_log").append("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
    };
    ws_logerror.onerror = function( p_event ) {
        $("#mecsim_global_log").append("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
    }

    var ws_logout = MecSim().getWebSocket("/cconsole/output/log");
    ws_logout.onmessage = function( p_event ) {
        $("#mecsim_global_log").append("<span class=\"mecsim_log_output\">" + p_event.data + "</span>");
    };
    ws_logout.onerror = function( p_event ) {
        $("#mecsim_global_log").append("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
    }

    var ws_inspector = MecSim().getWebSocket("/cinspector/show");
    ws_inspector.onmessage = function( p_event ) {
        console.log( p_event.data );
    };

    // @todo set must be called
    $.ajax({
        url     : "/cosmviewer/listclickablelayer",
        success : function( px_data ){
            $.each( px_data, function( pc_key, px_value ) {
                $( "#mecsim_osmclickablelayer" ).append( "<li class=\"ui-state-default\" id=\"" + px_value.id + "\">" + pc_key + "</li>" );
            });

            $( "#mecsim_osmclickablelayer" ).sortable({ placeholder: "ui-state-highlight" });
            $( "#mecsim_osmclickablelayer" ).disableSelection();
        }
    });

    // @todo switch-buttons must be setup (http://www.bootstrap-switch.org/)
    $.ajax({
        url     : "/csimulation/listlayer",
        success : function( px_data ){
            $.each( px_data, function( pc_key, px_value ) {
                $( "#mecsim_simulationlayer" ).append( "<li class=\"ui-widget-content\" id=\"" + px_value.id + "\">" + pc_key + "</li>" );
            });

            $( "#mecsim_simulationlayer" ).selectable();
        }
    });

});

        //TODO Check if really needed ?
        /*$("a.template").click(function( p_event ) {
            p_event.preventDefault();
            $("#mecsim_content").load( this.href );
        });

        $("a.template_button").button().click(function( p_event ) {
            p_event.preventDefault();
            $("#mecsim_content").load( this.href );
        });*/

});