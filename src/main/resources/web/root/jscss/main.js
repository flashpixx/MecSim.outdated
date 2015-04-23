// --- JQUERY ----------------------------------------------------------------------------------------------------------
$(document).ready(function() {

    // singleton instanciation
    Logger();
    MecSim();
    UI();



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

        $(window).on("beforeunload", function() {
            ws_logerror.close();
            ws_logout.close();
            ws_inspector.close();
        });


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
            UI().getContent().empty();
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
            UI().getContent().empty();
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

    }

    // --- SOURCE PANEL ------------------------------------------------------------------------------------------------
    function sourceSlider(){

        //Load the Source-GUI
        $("#ui-id-5").on("click", function(data){
            UI().getContent().empty();
            UI().getContent().load("template/source.htm", function(){
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
        }).on("click", function(data){
            $.post(
              "cosmmouselistener/setsourcelayertool",
              { "tool" : "sourcemode" }
            );
        });

        //Listen to the Default Agent Car Tool Button
        $("#mecsim_source_generatormode").button({
            icons: {
                primary: "ui-icon-arrowrefresh-1-e"
            }
        }).on("click", function(data){
            $.post(
              "cosmmouselistener/setsourcelayertool",
              { "tool" : "generatormode" }
            );
        });

        //Listen to the Target Tool Button
        $("#mecsim_source_targetmode").button({
            icons: {
                primary: "ui-icon-flag"
            }
        }).on("click", function(data){
            $.post(
              "cosmmouselistener/setsourcelayertool",
              { "tool" : "targetmode" }
            );
        });
    }

    // --- EDITOR PANEL ------------------------------------------------------------------------------------------------
    function editorSlider(){

        var g_editor;

        $("#ui-id-7").on("click", function() {
            UI().getContent().empty();
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
            UI().getContent().empty();
        });
    }

    // --- HELP PANEL --------------------------------------------------------------------------------------------------
    function helpSlider(){

        $("#ui-id-10").on("click", function(data){
            UI().getContent().empty();
        });

        $("#mecsim_help_about").button().on("click", function(){
            $.getJSON( "cconfiguration/get", function( p_data ) {

                $("#mecsim_project_name")
                        .attr("href", p_data.manifest["project-url"])
                        .text(p_data.manifest["project-name"]);

                $("#mecsim_licence")
                        .attr("href", p_data.manifest["licence-url"])
                        .text(p_data.manifest["licence"]);

                $("#mecsim_buildversion").text(p_data.manifest["build-version"]);
                $("#mecsim_buildnumber").text(p_data.manifest["build-number"]);
                $("#mecsim_buildcommit").text(p_data.manifest["build-commit"]);

                $("#mecsim_about").dialog({
                    width: 500
                });
            });
        });

        $("#mecsim_help_userdoku").button().on("click", function(){
            $.get("/userdoc/", function( p_result ) {
                console.log(p_result);
                UI().getContent().empty();
                UI().getContent().append( p_result );
            });
        });

        $("#mecsim_help_devdoku").button().on("click", function(){
            UI().getContent().load("template/develdoc.htm");
        });
    }

    // --- ADDITIONAL FUNCTIONS ----------------------------------------------------------------------------------------
    function load_asl_files(){
        $.getJSON( "cagentenvironment/jason/list", function( p_data ) {
            var mecsim_agent_files = $("#mecsim_agent_files")
            mecsim_agent_files.empty();
            for(var i in p_data.agents){
                mecsim_agent_files
                    .append( $("<option></option>")
                    .attr("value",p_data.agents[i])
                    .text(p_data.agents[i]));
            }
            $("#mecsim_agent_files option:first").attr('selected', true);
            mecsim_agent_files.selectmenu('refresh');
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
