// --- SOURCE PANEL --------------------------------------------------------------------------------------------

/** todo collection for source-ui ck
TODO ajax kapseln
TODO function with parameters
TODO save toolbox in config/web
TODO Further GUI Elements (WaypointType, Radius, CarSettings Patameters, Histrogramm)
TODO bounding in minimized mode maybe snap to tab
TODO plot distribution
TODO Car Settings
TODO show error dialog if tool can not created (check for correctness on java side)
TODO distingusish between init and refresh(asl, ...)
TODO source list table (JTable or JQuery Plugin)
TODO source weighting table (JTable or JQuery Plugin)
TODO radial refactor (bugs and generic)
**/

var SourcePanel = ( function (px_module) {

    px_module.sourcesettings = {
        labels: {},
        wizardWidget: {},
        wizardWidgetStatus: true,
        colorpicker: {}
    };

    //method to initialize source-ui
    px_module.init = function() {
        this.getLabels();
        this.setToolbox();

        $("#mecsim_source_panel").on("click", function(data){
            MecSim.ui().content().empty();
            MecSim.ui().content().load("template/source.htm", function(){
                SourcePanel.initToolWizard();
            });
        });

        $("#mecsim_source_createtool").button().on("click", function(data){
            SourcePanel.sourcesettings.wizardWidget.show("drop", 350);
        });

    };

    //method to get source-ui related labels
    px_module.getLabels = function(){
        $.ajax({
            url     : "/clanguageenvironment/getwaypointlabels",
            success : function( px_data ){
                 SourcePanel.sourcesettings.labels.wizardwidget = px_data.wizardwidget;
                 SourcePanel.sourcesettings.labels.factorysettings = px_data.factorysettings;
                 SourcePanel.sourcesettings.labels.generatorsettings = px_data.generatorsettings;
                 SourcePanel.sourcesettings.labels.carsettings = px_data.carsettings;
                 SourcePanel.sourcesettings.labels.customizing = px_data.customizing;
                 SourcePanel.sourcesettings.labels.selectyourfactory = px_data.selectyourfactory;
                 SourcePanel.sourcesettings.labels.selectyourasl = px_data.selectyourasl;
                 SourcePanel.sourcesettings.labels.selectyourgenerator = px_data.selectyourgenerator;
                 SourcePanel.sourcesettings.labels.selectyourcarcount = px_data.selectyourcarcount;
                 SourcePanel.sourcesettings.labels.selectyourmean = px_data.selectyourmean;
                 SourcePanel.sourcesettings.labels.selectyourdeviation = px_data.selectyourdeviation;
                 SourcePanel.sourcesettings.labels.selectyourlowerbound = px_data.selectyourlowerbound;
                 SourcePanel.sourcesettings.labels.selectyourupperbound = px_data.selectyourupperbound;
                 SourcePanel.sourcesettings.labels.selectcarspeedprob = px_data.selectcarspeedprob;
                 SourcePanel.sourcesettings.labels.selectmaxcarspeedprob = px_data.selectmaxcarspeedprob;
                 SourcePanel.sourcesettings.labels.selectaccprob = px_data.selectaccprob;
                 SourcePanel.sourcesettings.labels.selectdecprob = px_data.selectdecprob;
                 SourcePanel.sourcesettings.labels.selectlingerprob = px_data.selectlingerprob;
                 SourcePanel.sourcesettings.labels.selecttoolname = px_data.selecttoolname;
                 SourcePanel.sourcesettings.labels.selecttoolnamevalue = px_data.selecttoolnamevalue;
                 SourcePanel.sourcesettings.labels.selecttoolcolor = px_data.selecttoolcolor;
                 SourcePanel.sourcesettings.labels.previous = px_data.previous;
                 SourcePanel.sourcesettings.labels.next = px_data.next;
                 SourcePanel.sourcesettings.labels.finish = px_data.finish;
            }
        });
    };

    //method to set source-ui related labels
    px_module.setLabels = function(){
        $("#mecsim_source_wizardwidget_label").append(SourcePanel.sourcesettings.labels.wizardwidget);
        $("#mesim_source_factorysettings_label").text(SourcePanel.sourcesettings.labels.factorysettings);
        $("#mesim_source_generatorsettings_label").text(SourcePanel.sourcesettings.labels.generatorsettings);
        $("#mesim_source_carsettings_label").text(SourcePanel.sourcesettings.labels.carsettings);
        $("#mesim_source_customizing_label").text(SourcePanel.sourcesettings.labels.customizing);
        $("#mecsim_source_selectfactory_label").text(SourcePanel.sourcesettings.labels.selectyourfactory);
        $("#mecsim_source_selectasl_label").text(SourcePanel.sourcesettings.labels.selectyourasl);
        $("#mecsim_source_selectgenerator_label").text(SourcePanel.sourcesettings.labels.selectyourgenerator);
        $("#mecsim_source_generatorinput1_label").text(SourcePanel.sourcesettings.labels.selectyourcarcount);
        $("#mecsim_source_generatorinput2_label").text(SourcePanel.sourcesettings.labels.selectyourlowerbound);
        $("#mecsim_source_generatorinput3_label").text(SourcePanel.sourcesettings.labels.selectyourupperbound);
        $("#mecsim_source_speedprob_label").text(SourcePanel.sourcesettings.labels.selectcarspeedprob);
        $("#mecsim_source_maxspeedprob_label").text(SourcePanel.sourcesettings.labels.selectmaxcarspeedprob);
        $("#mecsim_source_accprob_label").text(SourcePanel.sourcesettings.labels.selectaccprob);
        $("#mecsim_source_decprob_label").text(SourcePanel.sourcesettings.labels.selectdecprob);
        $("#mecsim_source_lingerprob_label").text(SourcePanel.sourcesettings.labels.selectlingerprob);
        $("#mecsim_source_toolname_label").text(SourcePanel.sourcesettings.labels.selecttoolname);
        $("#mecsim_source_toolname").attr("value", SourcePanel.sourcesettings.labels.selecttoolnamevalue);
        $("#mecsim_source_toolcolor_label").text(SourcePanel.sourcesettings.labels.selecttoolcolor);
    };

    //method to update labels
    px_module.updateLabels = function(){
        if($("#mecsim_source_selectGenerator").val() === "uniform distribution" || $("#mecsim_source_selectGenerator").val() === "Gleichverteilung"){
            $("#mecsim_source_generatorinput2_label").text(SourcePanel.sourcesettings.labels.selectyourlowerbound);
            $("#mecsim_source_generatorinput3_label").text(SourcePanel.sourcesettings.labels.selectyourupperbound);
        }else{
            $("#mecsim_source_generatorinput2_label").text(SourcePanel.sourcesettings.labels.selectyourmean);
            $("#mecsim_source_generatorinput3_label").text(SourcePanel.sourcesettings.labels.selectyourdeviation);
        }
    };

    //method to create toolbox
    px_module.setToolbox = function(){
        $.ajax({
            url     : "/cwaypointenvironment/listtools",
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {

                    $("</p>").appendTo($("#mecsim_source_toolbox"));

                    var button = $("<button></button>")
                        .text(pc_key)
                        .attr("class", "mecsim_global_accordeonbutton")
                        .attr("value",pc_key)
                        .button()
                        .on("click", {"toolname": pc_key}, SourcePanel.listenToolButton)
                        .appendTo($("#mecsim_source_toolbox"));

                    $("<span></span>")
                        .css("background-color", "rgb("+ px_value.redValue +","+ px_value.greenValue +","+ px_value.blueValue +")")
                        .attr("class", "mecsim_source_toolIcon")
                        .prependTo(button);

                });
            }
        });
    };

    //method to listen to toolbutton
    px_module.listenToolButton = function(event){
        $.ajax({
            url     : "/cwaypointenvironment/settool",
            data    : {"toolname": event.data.toolname}
        });
    };

    //method to initalize the tool wizard
    px_module.initToolWizard = function(){

        //layout wizard widget
            SourcePanel.sourcesettings.wizardWidget = $("#mecsim_source_toolwizardwidget")
            .draggable({
                drag: function(event, ui) {
                    ui.position.top = Math.max( -500, ui.position.top );
                    ui.position.left = Math.max( -700, ui.position.left );
                }
            }).resizable({
                animate: true,
                minWidth: 750,
                minHeight: 550
            }).hide();

        SourcePanel.setLabels();

        //create and listen to minimize button
        $("#mecsim_source_collapse_wizard").button({icons: { primary: " ui-icon-newwin"},text: false}).on("click", function(data){
            if(SourcePanel.sourcesettings.wizardWidgetStatus){
                SourcePanel.sourcesettings.wizardWidgetStatus = false;
                $("#mecsim_source_toolwizard").hide();
                $("#mecsim_source_toolwizardwidget").animate({width: "400px", height: "20px"}, 400);
                $("#mecsim_source_toolwizardwidget").resizable('disable');
            }else{
                SourcePanel.sourcesettings.wizardWidgetStatus = true;
                $("#mecsim_source_toolwizard").show();
                $("#mecsim_source_toolwizardwidget").animate({width: "750px", height: "550px"}, 400);
                $("#mecsim_source_toolwizardwidget").resizable('enable');
            }
        });

        //create and listen to the close button
        $("#mecsim_source_close_wizard").button({icons: { primary: "ui-icon-closethick"},text: false}).on("click", function(data){
            $("#mecsim_source_toolwizardwidget").hide("drop", 350);
        });

        //create wizard
        $("#mecsim_source_toolwizard").steps({
            headerTag: "h3",
            bodyTag: "section",
            transitionEffect: "slideLeft",
            stepsOrientation: "vertical",
            autoFocus: true,
            onInit: SourcePanel.buildWizardContent,
            onStepChanging: SourcePanel.validateWizardStep,
            onFinished: SourcePanel.finishWizard
        });

        //listen to factory select
        $("#mecsim_source_selectFactory").on("change", function(){
            SourcePanel.updateFactorySettings();
        });

        //listen to generator select
        $("#mecsim_source_selectGenerator").on("change", function(){
            SourcePanel.updateLabels();
            SourcePanel.updateGeneratorSettings();
        });

    };

    //method to build up the wizard content
    px_module.buildWizardContent = function() {
        //create selectmenu with factory types
        $.ajax({
            url     : "/cwaypointenvironment/listfactories",
            success : function( px_data ){
                px_data.factories.forEach(function(data){
                    $.each( data, function( pc_key, px_value ) {
                        $("#mecsim_source_selectFactory")
                            .append( $("<option></option>")
                            .attr("value",pc_key)
                            .attr("requireASL", px_value)
                            .text(pc_key));
                    });
                });
                SourcePanel.updateFactorySettings();
            }
        });

        //create selectmenu with possible agent programs
        $.ajax({
            url     : "/cagentenvironment/jason/list",
            success : function( px_data ){
                px_data.agents.forEach(function(data){
                    $("#mecsim_source_selectASL")
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
            }
        });

        //create selectmenu with possible generator types
        $.ajax({
            url     : "/cwaypointenvironment/listgenerator",
            success : function( px_data ){
                px_data.generators.forEach(function(data){
                    $("#mecsim_source_selectGenerator")
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
                SourcePanel.updateLabels();
                SourcePanel.updateGeneratorSettings();
            }
        });

        //create colorpicker
        SourcePanel.sourcesettings.colorpicker = $("#mecsim_source_colorpicker").spectrum({
            showPaletteOnly: true,
            togglePaletteOnly: true,
            togglePaletteMoreText: 'more',
            togglePaletteLessText: 'less',
            color: 'blanchedalmond',
            palette: [
                ["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
                ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
                ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"]
            ]
        });
    };

    //method to validate if the current wizard step is valide
    px_module.validateWizardStep = function(event , currentIndex, newIndex) {
        if(currentIndex===0){
            if($('#mecsim_source_selectFactory option:selected').attr('requireASL')==="true" && $("#mecsim_source_selectASL").val()===null)
                return false;
        }

        if(currentIndex===1){
            var input1 = Number($("#mecsim_source_generatorinput1").val());
            var input2 = Number($("#mecsim_source_generatorinput2").val());
            var input3 = Number($("#mecsim_source_generatorinput3").val());

            if( isNaN(input1) || isNaN(input2) || isNaN(input3) || input1 <= 0)
                return false;

            if($("#mecsim_source_selectGenerator").val() === "uniform distribution" || $("#mecsim_source_selectGenerator").val() === "Gleichverteilung"){
                if( (input2 >= input3) || (input2 < 0) )
                    return false;
            }else{
                if( input2 < input3){
                    return false;
                }
            }
        }

        return true;
    };

    //method to finish the last wizard step
    px_module.finishWizard = function(){
        $.ajax({
            url     : "/cwaypointenvironment/createtool",
            data    : {
                        "factory"  : $("#mecsim_source_selectFactory").val(),
                        "asl"      : $("#mecsim_source_selectASL").val(),
                        "generator": $("#mecsim_source_selectGenerator").val(),
                        "input1"   : $("#mecsim_source_generatorinput1").val(),
                        "input2"   : $("#mecsim_source_generatorinput2").val(),
                        "input3"   : $("#mecsim_source_generatorinput3").val(),
                        "name"     : $("#mecsim_source_toolname").val(),
                        "r"        : SourcePanel.sourcesettings.colorpicker.spectrum("get")._r,
                        "g"        : SourcePanel.sourcesettings.colorpicker.spectrum("get")._g,
                        "b"        : SourcePanel.sourcesettings.colorpicker.spectrum("get")._b
                    },
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {

                    $("</p>").appendTo($("#mecsim_source_toolbox"));

                    var button = $("<button></button>")
                        .text(pc_key)
                        .attr("class", "mecsim_global_accordeonbutton")
                        .attr("value",pc_key)
                        .button()
                        .on("click", {"toolname": pc_key}, SourcePanel.listenToolButton)
                        .appendTo($("#mecsim_source_toolbox"));

                    $("<span></span>")
                        .css("background-color", "rgb("+ px_value.redValue +","+ px_value.greenValue +","+ px_value.blueValue +")")
                        .attr("class", "mecsim_source_toolIcon")
                        .prependTo(button);

                });
            }
        }).fail(function(){
            console.log("tool creation failed!");
        });
    };

    //method to update factory settings
    px_module.updateFactorySettings = function(){
        if($( "#mecsim_source_selectFactory option:selected").attr("requireASL") === "true"){
            $("#mecsim_source_selectASLContainer").show();
        }else{
            $("#mecsim_source_selectASLContainer").hide();
        }
    };

    //method to update generator settings
    px_module.updateGeneratorSettings = function(){
        $("#mecsim_source_generatorinput1").val(3);
        if($("#mecsim_source_selectGenerator").val() === "uniform distribution" || $("#mecsim_source_selectGenerator").val() === "Gleichverteilung"){
            $("#mecsim_source_generatorinput2").val(3);
            $("#mecsim_source_generatorinput3").val(7);
        }else{
            $("#mecsim_source_generatorinput2").val(5);
            $("#mecsim_source_generatorinput3").val(1);
        }
    };

    return px_module;

}(SourcePanel || {}));
