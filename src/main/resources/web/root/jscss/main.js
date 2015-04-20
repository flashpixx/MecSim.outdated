// --- GLOBAL PROTOTYPE FUNCTIONS --------------------------------------------------------------------------------------

String.prototype.startsWith = function(prefix) {
    return this.indexOf(prefix) === 0;
}

String.prototype.endsWith = function(suffix) {
    return this.match(suffix+"$") == suffix;
};

// --- MECSIM ----------------------------------------------------------------------------------------------------------

var MecSim = (function () {

    // private variables and methods
    var s_instance;


    function _ctor() {return {

        getWebSocket : function( p_wspath ) {
            if ((p_wspath.startsWith("ws://")) || (p_wspath.startsWith("wss://")))
                return new WebSocket(p_wspath);

            return new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (p_wspath.startsWith("/") ? p_wspath : location.pathname + p_wspath ) );
        },

        getConfiguration : function ( p_data ) {
            var l_data = {};

            $.ajax({
                async: false,
                url : "/cconfiguration/get",
                success : function( p_data ) { l_data = p_data }
            });

            return l_data;
        },
        getMenu : function() {
            return $("#mecsim_global_menu");
        },
        getAccordion : function() {
            $("#mecsim_global_accordion").accordion({
                active: false,
                collapsible: true
            })
        },
        getContent : function() {
            return $("#mecsim_global_content");
        }
    };};


    return {

        getInstance: function () {
            if ( !s_instance )
                s_instance = _ctor();

            return s_instance;
        }

    };

})();

MecSim.getInstance().getConfiguration();

// --- JQUERY ----------------------------------------------------------------------------------------------------------
$(document).ready(function() {

    var form   = "",
        dialog = "";

    //TODO not work on external include of main.js

    layoutInit();
    fileSlider();
    simulationSlider(),
    sourceSlider();
    editorSlider();
    statisticsSlider();
    helpSlider();

    function layoutInit() {

        // splitter
        $("#mecsim_global_screen").jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        $("#mecsim_global_screen_right").jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });


        // logger
        var ws_logerror = MecSim.getInstance().getWebSocket("/cconsole/error/log");
        ws_logerror.onmessage = function( p_event ) {
            $("#mecsim_global_log").append("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        };
        ws_logerror.onerror = function( p_event ) {
            $("#mecsim_global_log").append("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        }

        var ws_logout = MecSim.getInstance().getWebSocket("/cconsole/output/log");
        ws_logout.onmessage = function( p_event ) {
            $("#mecsim_global_log").append("<span class=\"mecsim_log_output\">" + p_event.data + "</span>");
        };
        ws_logout.onerror = function( p_event ) {
            $("#mecsim_global_log").append("<span class=\"mecsim_log_error\">" + p_event.data + "</span>");
        }

        var ws_inspector = MecSim.getInstance().getWebSocket("/cinspector/show");
        ws_inspector.onmessage = function( p_event ) {
            console.log( p_event.data );
        };

        $(window).on("beforeunload", function() {
            ws_logerror.close();
            ws_logout.close();
            ws_inspector.close();
        });

        // load accordion
        MecSim.getInstance().getAccordion();


        //TODO Check if really needed ?
        $("a.template").click(function( p_event ) {
            p_event.preventDefault();
            $("#mecsim_content").load( this.href );
        });

        $("a.template_button").button().click(function( p_event ) {
            p_event.preventDefault();
            $("#mecsim_content").load( this.href );
        });

    }

    // --- FILE PANEL --------------------------------------------------------------------------------------------------
    function fileSlider() {

        $("#ui-id-1").on("click", function(data){
            MecSim.getInstance().getContent().empty();
        });

        $("#mecsim_file_preferences").button().on("click", function() {

        });

        $("#mecsim_file_config").button().on("click", function() {

        });

        $("#mecsim_file_local").button().on("click", function() {

        });
    }

    // --- SIMULATION PANEL --------------------------------------------------------------------------------------------
    function simulationSlider(){

        $("#ui-id-3").on("click", function(data){
            MecSim.getInstance().getContent().empty();
        });

        $("#mecsim_simulation_start").button().on("click", function(){
            $.post("csimulation/start").fail( function( p_data ) {
                console.log(p_data);
                $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                $("#mecsim_start_error").dialog();
            });
        });

        $("#mecsim_simulation_stop").button().on("click", function(){
            $.post("csimulation/stop").fail( function( p_data ) {
                console.log(p_data);
                $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                $("#mecsim_stop_error").dialog();
            });
        });

        $("#mecsim_simulation_reset").button().on("click", function(){
            $.post("csimulation/reset")
        });

        $("#mecsim_simulation_load").button().on("click", function(){

        });

        $("#mecsim_simulation_save").button().on("click", function(){

        });

        $( "#mecsim_speed_slider" ).slider({
            range: "min",
            value: 1,
            min: 0,
            max: 10,
            slide: function( event, ui ) {
                $( "#speed" ).val( ui.value );
            }
        });

        $( "#speed" ).val( $( "#mecsim_speed_slider" ).slider( "value" ) );
    }

    // --- SOURCE PANEL ------------------------------------------------------------------------------------------------
    function sourceSlider(){

        //Load the Source-GUI
        $("#ui-id-5").on("click", function(data){
            MecSim.getInstance().getContent().empty();
            MecSim.getInstance().getContent().load("template/source.htm", function(){
                initLayout();
                initClusterWidget();
                initSettingsWidget();
                initTargetWeighting();
            });
        });

        //Listen to the Default Car Tool Button
        $("#mecsim_source_sourcemode").button({
            icons: {
                primary: "ui-icon-pin-w"
            }
        }).on("click", function(data){});

        //Listen to the Default Agent Car Tool Button
        $("#mecsim_source_generatormode").button({
            icons: {
                primary: "ui-icon-arrowrefresh-1-e"
            }
        }).on("click", function(data){});

        //Listen to the Target Tool Button
        $("#mecsim_source_targetmode").button({
            icons: {
                primary: "ui-icon-flag"
            }
        }).on("click", function(data){});
    }

    // --- EDITOR PANEL ------------------------------------------------------------------------------------------------
    function editorSlider(){

        var g_editor;

        $("#ui-id-7").on("click", function() {
            MecSim.getInstance().getContent().empty();
            g_editor = CodeMirror($("#mecsim_global_content")[0], {
                lineNumbers: true
            });
        });

        load_asl_files();
        $("#mecsim_agent_files").selectmenu();

        // TODO: catch exception if no file exists
        $("#mecsim_load_asl").button().on("click", function(p_data){
            $.post(
              "cagentenvironment/jason/read",
              { "name" : $("#mecsim_agent_files").val() },
              function( px_data ) {
                  g_editor.setValue( px_data.source );
              }
            );
        });

        $("#mecsim_delete_asl").button().on("click", function(p_data){
            $.post(
                "cagentenvironment/jason/delete",
                { "name" : $("#mecsim_agent_files").val() }
            ).done(function() {
                load_asl_files();
            });

        });

        // TODO: data field -> right?
        $("#mecsim_save_asl").button().on("click", function(p_data){
            $.post(
                "cagentenvironment/jason/write",
                { "name" : $("#mecsim_agent_files").val(),
                "source" : g_editor.getValue(),
                "data" : g_editor.getValue()}
          );
        });

        $("#mecsim_new_asl").button().on("click", function(p_data){
            dialog.dialog("open");
        });

        dialog = $("#mecsim_create_asl_form").dialog({
            autoOpen: false,
            buttons: {
                "Create": create_new_asl,
                Cancel: function() {
                    dialog.dialog( "close" );
                }
            }
        });
    }

    // --- STATISTICS PANEL --------------------------------------------------------------------------------------------
    function statisticsSlider(){
        //TODO need to be implemented
        $("#ui-id-8").on("click", function(data){
            MecSim.getInstance().getContent().empty();
        });
    }

    // --- HELP PANEL --------------------------------------------------------------------------------------------------
    function helpSlider(){

        $("#ui-id-10").on("click", function(data){
            MecSim.getInstance().getContent().empty();
        });

        $("#mecsim_help_about").button().on("click", function(){
            $.getJSON( "cconfiguration/get", function( p_data ) {
                console.log(p_data);
                $("#mecsim_about_text").append(
                    "<div id=\"about\">" +
                    "<div class=\"table\">" +
                    "<div class=\"row\">  <div class=\"cell text_align_center\"><a href=\"" + p_data.manifest["project-url"] + "\">" + p_data.manifest["project-name"] + "</a></div>  </div>" +
                    "<br/>" +
                    "<div class=\"row\"><div class=\"header\"><div class=\"cell\">License</div></div>       <div class=\"cell\"><a href=\"" + p_data.manifest["license-url"] + "\">" + p_data.manifest["license"] + "</a></div></div>" +
                    "<div class=\"row\"><div class=\"header\"><div class=\"cell\">Buildversion</div></div>  <div class=\"cell\">" + p_data.manifest["build-version"] + "</div></div>" +
                    "<div class=\"row\"><div class=\"header\"><div class=\"cell\">Buildnumber</div></div>   <div class=\"cell\">" + p_data.manifest["build-number"] + "</a></div></div>" +
                    "<div class=\"row\"><div class=\"header\"><div class=\"cell\">Buildcommit</div></div>   <div class=\"cell\">" + p_data.manifest["build-commit"] + "</a></div></div>" +
                    "</div>" +
                    "</div>"
                );
                $("#mecsim_about").dialog({
                    width: 500
                });
            });
        });

        $("#mecsim_help_userdoku").button().on("click", function(){
            $.get("/userdoc/", function( p_result ) {
                console.log(p_result);
                MecSim.getInstance().getContent().empty();
                MecSim.getInstance().getContent().append( p_result );
            });
        });

        $("#mecsim_help_devdoku").button().on("click", function(){
            MecSim.getInstance().getContent().load("template/develdoc.htm");
        });
    }

    // --- ADDITIONAL FUNCTIONS ----------------------------------------------------------------------------------------
    function load_asl_files(){
        $.getJSON( "cagentenvironment/jason/list", function( p_data ) {
            $("#mecsim_agent_files").empty();
            for(var i in p_data.agents){
                $("#mecsim_agent_files")
                    .append( $("<option></option>")
                    .attr("value",p_data.agents[i])
                    .text(p_data.agents[i]));
            }
            $("#mecsim_agent_files option:first").attr('selected', true);
            $('#mecsim_agent_files').selectmenu('refresh', true);
        });
    }

    function create_new_asl(){

        if( $("#new_asl").val() ) {
            $.post(
                "cagentenvironment/jason/create",
                { "name" : $("#new_asl").val() }
            ).done(function() {
                load_asl_files();
                dialog.dialog("close");
            });
        } else {
            alert("Please enter a file name");
        }

    }

});
