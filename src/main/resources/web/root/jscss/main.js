// --- JQUERY ----------------------------------------------------------------------------------------------------------
$(document).ready(function() {

$.when(
    $.getScript("jscss/accordion/editor.js"),
    $.getScript("jscss/accordion/file.js"),
    $.getScript("jscss/accordion/help.js"),
    $.getScript("jscss/accordion/simulation.js"),
    $.getScript("jscss/accordion/source.js"),
    $.getScript("jscss/accordion/statistics.js")
).done(function(){

    // singleton instantiation
    Logger();
    MecSim();
    UI();

    // module instantiation
    EditorPanel.init();
    SimulationPanel.init();
    FilePanel.init();
    HelpPanel.init();
    SourcePanel.init();

    // about dialog will close when clicked outside of the dialog
    // TODO: there's still a bug when initializing that function since the dialog is not defined at that stage
    jQuery('body').bind('click', function(e){
        if( jQuery('#mecsim_about').dialog('isOpen')
            && !jQuery(e.target).is('.ui-dialog, a')
            && !jQuery(e.target).closest('.ui-dialog').length) {

            jQuery('#mecsim_about').dialog('close');

        }
    });

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