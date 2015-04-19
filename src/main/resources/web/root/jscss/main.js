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

        MecSim.getInstance().getContent().empty();

        $("#mecsim_file_preferences").button().on("click", function() {

        });

        $("#mecsim_file_config").button().on("click", function() {

        });

        $("#mecsim_file_local").button().on("click", function() {

        });
    }

    // --- SIMULATION PANEL --------------------------------------------------------------------------------------------

    function simulationSlider(){
        $("#mecsim_simulation_start").button().on("click", function(){
            $.post("csimulation/start")
        });

        $("#mecsim_simulation_stop").button().on("click", function(){
            $.post("csimulation/stop")
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

        $("#mecsim_load_asl").button();
        $("#mecsim_delete_asl").button();
        $("#mecsim_save_asl").button();
        $("#mecsim_agent_files").selectmenu();
        $("#mecsim_new_asl").button();

        // load existing asl files
        load_asl_files();
        function load_asl_files()
        {
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

        // form to create new asl file
        dialog = $("#mecsim_create_asl_form").dialog({
            autoOpen: false,
            buttons: {
                "Create": create_new_asl,
                Cancel: function() {
                    dialog.dialog( "close" );
                }
            }
        });

        $("#mecsim_new_asl").click(function() {
            dialog.dialog("open");
        });

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

        // delete asl file
        $("#mecsim_delete_asl").click(function(){
            $.post(
                "cagentenvironment/jason/delete",
                { "name" : $("#mecsim_agent_files").val() }
            ).done(function() {
                load_asl_files();
            });
        });

        // TODO: catch exception if no file exists
        // load selected asl file
        $("#mecsim_load_asl").click(function(){
            $.post(
              "cagentenvironment/jason/read",
              { "name" : $("#mecsim_agent_files").val() },
              function( px_data ) {
                  g_editor.setValue( px_data.source );
              }
            );
        });


        // save asl file
        // TODO: data field -> right?
        $("#mecsim_save_asl").click(function(){
            $.post(
                "cagentenvironment/jason/write",
                { "name" : $("#mecsim_agent_files").val(),
                "source" : g_editor.getValue(),
                "data" : g_editor.getValue()}
          );
        });

    }

    // --- STATISTICS PANEL --------------------------------------------------------------------------------------------
    function statisticsSlider(){
        //TODO need to be implemented
    }

    // help panel
    function helpSlider(){
        $("#mecsim_help_about").button().on("click", function(){

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

    // stuff from clean up (configuration.htm)
    function test(){
        /**
        //build table with configuration data
        $.getJSON( "cconfiguration/get", function( p_data ) {
            //$( "#routingalgorithm" ).selectmenu();
            $( "#routingalgorithm_allowed" ).selectmenu();
        });

        //post request example
        $.post(
            "cconfiguration/set",
            { "console" : {"LineBuffer":20,"LineNumber":20}, "ui" : { "xxx" : 123, "yyy" : "hallo", "map" : { "x" : 1, "y" : 2, "z" : 3 } } }
        );


        //get request example
        $.get(
            "cconfiguration/set",
            { type : "get", bar : "foo", "foo_number" : 3, "bar_bool" : false }
        );
        **/
    }

});
