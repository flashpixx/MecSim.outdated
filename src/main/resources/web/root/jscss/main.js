$(document).ready(function(){

    //Global Variables
    var mecsim_global_menu = $("#mecsim_global_menu")
    var mecsim_global_accordion = $( "#mecsim_global_accordion" )
    var mecsim_global_content = $("#mecsim_global_content");
    var form;
    var dialog;

    //Global Stuff
    layoutInit();
    fileSlider();
    simulationSlider(),
    sourceSlider();
    editorSlider();
    statisticsSlider();
    helpSlider();

    // global
    function layoutInit(){
      $('#jqxSplitter').jqxSplitter({ width: '100%', height: '100%', panels: [{ size: '20%', min: 250 }, { size: '80%'}] });

      mecsim_global_accordion.accordion({
          active: false,
          collapsible: true
      });

      //TODO Check if really needed ?
      $("a.template").click(function( p_event ){
          p_event.preventDefault();
          $("#mecsim_content").load( this.href );
      });

      $("a.template_button").button().click(function( p_event ){
          p_event.preventDefault();
          $("#mecsim_content").load( this.href );
      });
    }

    // file slider
    function fileSlider(){
        $("#mecsim_file_preferences").button().on("click", function(){

        });

        $("#mecsim_file_config").button().on("click", function(){

        });

        $("#mecsim_file_local").button().on("click", function(){

        });
    }

    // simulation slider
    function simulationSlider(){
        $("#mecsim_simulation_start").button().on("click", function(){

        });

        $("#mecsim_simulation_stop").button().on("click", function(){

        });

        $("#mecsim_simulation_reset").button().on("click", function(){

        });

        $("#mecsim_simulation_load").button().on("click", function(){

        });

        $("#mecsim_simulation_save").button().on("click", function(){

        });

        $("#mecsim_simulation_local").button().on("click", function(){

        });

        $("#mecsim_simulation_").button().on("click", function(){

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

    // source slider
    function sourceSlider(){

      //Load the Source-GUI
      $("#ui-id-5").on("click", function(data){
        mecsim_global_content.load("template/source.htm", function(){
          initLayout();
          initClusterWidget();
          initSettingsWidget();
          initTargetWeighting();
        });
      });

      //Listen to the Default Car Tool Button
      $("#mecsim_source_defaultCar").button({
        icons: {
          primary: " ui-icon-pin-w"
        }
      })
      .on("click", function(data){
        $.post(
            "cosmmouselistener/test"
        ).fail( function( p_data )
            {
                console.log(p_data);
                $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                $("#mecsim_start_error").dialog();
        });
      });

      //Listen to the Default Agent Car Tool Button
      $("#mecsim_source_defaultAgent").button({
        icons: {
          primary: " ui-icon-pin-w"
        }
      })
      .on("click", function(data){

      });

      //Listen to the Target Tool Button
      $("#mecsim_source_target").button({
        icons: {
          primary: "ui-icon-flag"
        }
      })
      .on("click", function(data){

      });
    }

    // editor slider
    function editorSlider(){
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
              //$('#mecsim_agent_files').selectmenu('refresh', true);
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
          if( $("#new_asl").val() )
          {
              $.post(
                  "cagentenvironment/jason/create",
                  { "name" : $("#new_asl").val() }
              ).done(function() {
                  load_asl_files();
                  dialog.dialog("close");
              });
          }
          else
          {
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

      /** TODO Move Javascript out of HTML Template.
      var g_selected_asl = $("#mecsim_agent_files").val();

      var g_editor = CodeMirror(function(elt) {
          source.parentNode.replaceChild(elt, source);
      }, {
           value: source.value,
           lineNumbers: true,
      });

      // TODO: catch exeption if no file exists
      $.post(
          "cagentenvironment/jason/read",
          { "name" : g_selected_asl },
          function( px_data ) {
              g_editor.setValue( px_data.source );
          }
      )

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
      **/

    }

    // statistics slider
    function statisticsSlider(){
        //TODO need to be implemented
    }

    // help slider
    function helpSlider(){
        $("#mecsim_help_help").button().on("click", function(){

        });

        $("#mecsim_help_userdoku").button().on("click", function(){
            //$.get("/userdoc/", function( p_result ) {
            //    $("#mecsim_content").append( p_result );
            //});
        });

        $("#mecsim_help_devdoku").button().on("click", function(){
            //mecsim_global_content.load("template/develdoc.htm");
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
