// --- SOURCE PANEL --------------------------------------------------------------------------------------------

var mecsim_source,
    SourcePanel = {

        settings: {
            labels: {},
            wizardWidget: {},
            wizardWidgetStatus: true,
            diameter: 600,
            radius: 600 / 2,
            innerRadius: (600 / 2) - 120,
            defaultLat: 0,
            defaultLon: 0,
            selected: false,
            colorpicker: {}
            //sortComp: SourcePanel.sortByDistanceComp

        },

        /** todo collection for source-ui ck
        TODO bug finish wizard
        TODO remove old gui
        TODO remove old css
        TODO accordion fix (better)
        TODO ajax kapseln
        TODO function with parameters
        TODO javascript structure like philipp
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

        //method to initialize source-ui
        init: function() {
            mecsim_source = this.settings;
            this.getLabels();
            this.setToolbox();

            $("#mecsim_source_slider").parent().on("click", function(data){
                MecSim.ui().content().empty();
                MecSim.ui().content().load("template/source.htm", function(){
                    SourcePanel.initToolWizard();
                });
            });

            $("#mecsim_source_createtool").button().on("click", function(data){
                SourcePanel.settings.wizardWidget.show("drop", 350);
            });

        },

        //method to get source-ui related labels
        getLabels: function(){
            $.ajax({
                url     : "/clanguageenvironment/getwaypointlabels",
                success : function( px_data ){
                     SourcePanel.settings.labels.wizardwidget = px_data.wizardwidget;
                     SourcePanel.settings.labels.factorysettings = px_data.factorysettings;
                     SourcePanel.settings.labels.generatorsettings = px_data.generatorsettings;
                     SourcePanel.settings.labels.carsettings = px_data.carsettings;
                     SourcePanel.settings.labels.customizing = px_data.customizing;
                     SourcePanel.settings.labels.selectyourfactory = px_data.selectyourfactory;
                     SourcePanel.settings.labels.selectyourasl = px_data.selectyourasl;
                     SourcePanel.settings.labels.selectyourgenerator = px_data.selectyourgenerator;
                     SourcePanel.settings.labels.selectyourcarcount = px_data.selectyourcarcount;
                     SourcePanel.settings.labels.selectyourmean = px_data.selectyourmean;
                     SourcePanel.settings.labels.selectyourdeviation = px_data.selectyourdeviation;
                     SourcePanel.settings.labels.selectyourlowerbound = px_data.selectyourlowerbound;
                     SourcePanel.settings.labels.selectyourupperbound = px_data.selectyourupperbound;
                     SourcePanel.settings.labels.selectcarspeedprob = px_data.selectcarspeedprob;
                     SourcePanel.settings.labels.selectmaxcarspeedprob = px_data.selectmaxcarspeedprob;
                     SourcePanel.settings.labels.selectaccprob = px_data.selectaccprob;
                     SourcePanel.settings.labels.selectdecprob = px_data.selectdecprob;
                     SourcePanel.settings.labels.selectlingerprob = px_data.selectlingerprob;
                     SourcePanel.settings.labels.selecttoolname = px_data.selecttoolname;
                     SourcePanel.settings.labels.selecttoolnamevalue = px_data.selecttoolnamevalue;
                     SourcePanel.settings.labels.selecttoolcolor = px_data.selecttoolcolor;
                     SourcePanel.settings.labels.previous = px_data.previous;
                     SourcePanel.settings.labels.next = px_data.next;
                     SourcePanel.settings.labels.finish = px_data.finish;
                }
            });
        },

        //method to set source-ui related labels
        setLabels: function(){
            $("#mecsim_source_wizardwidget_label").append(SourcePanel.settings.labels.wizardwidget);
            $("#mesim_source_factorysettings_label").text(SourcePanel.settings.labels.factorysettings);
            $("#mesim_source_generatorsettings_label").text(SourcePanel.settings.labels.generatorsettings);
            $("#mesim_source_carsettings_label").text(SourcePanel.settings.labels.carsettings);
            $("#mesim_source_customizing_label").text(SourcePanel.settings.labels.customizing);
            $("#mecsim_source_selectfactory_label").text(SourcePanel.settings.labels.selectyourfactory);
            $("#mecsim_source_selectasl_label").text(SourcePanel.settings.labels.selectyourasl);
            $("#mecsim_source_selectgenerator_label").text(SourcePanel.settings.labels.selectyourgenerator);
            $("#mecsim_source_generatorinput1_label").text(SourcePanel.settings.labels.selectyourcarcount);
            $("#mecsim_source_generatorinput2_label").text(SourcePanel.settings.labels.selectyourlowerbound);
            $("#mecsim_source_generatorinput3_label").text(SourcePanel.settings.labels.selectyourupperbound);
            $("#mecsim_source_speedprob_label").text(SourcePanel.settings.labels.selectcarspeedprob);
            $("#mecsim_source_maxspeedprob_label").text(SourcePanel.settings.labels.selectmaxcarspeedprob);
            $("#mecsim_source_accprob_label").text(SourcePanel.settings.labels.selectaccprob);
            $("#mecsim_source_decprob_label").text(SourcePanel.settings.labels.selectdecprob);
            $("#mecsim_source_lingerprob_label").text(SourcePanel.settings.labels.selectlingerprob);
            $("#mecsim_source_toolname_label").text(SourcePanel.settings.labels.selecttoolname);
            $("#mecsim_source_toolname").attr("value", SourcePanel.settings.labels.selecttoolnamevalue);
            $("#mecsim_source_toolcolor_label").text(SourcePanel.settings.labels.selecttoolcolor);
        },

        //method to update labels
        updateLabels: function(){
            if($("#mecsim_source_selectGenerator").val() === "uniform distribution" || $("#mecsim_source_selectGenerator").val() === "Gleichverteilung"){
                $("#mecsim_source_generatorinput2_label").text(SourcePanel.settings.labels.selectyourlowerbound);
                $("#mecsim_source_generatorinput3_label").text(SourcePanel.settings.labels.selectyourupperbound);
            }else{
                $("#mecsim_source_generatorinput2_label").text(SourcePanel.settings.labels.selectyourmean);
                $("#mecsim_source_generatorinput3_label").text(SourcePanel.settings.labels.selectyourdeviation);
            }
        },

        //method to create toolbox
        setToolbox: function(){
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
        },

        //method to listen to toolbutton
        listenToolButton: function(event){
            $.ajax({
                url     : "/cwaypointenvironment/settool",
                data    : {"toolname": event.data.toolname}
            });
        },

        //method to initalize the tool wizard
        initToolWizard: function(){

            //layout wizard widget
                SourcePanel.settings.wizardWidget = $("#mecsim_source_toolwizardwidget")
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
                if(SourcePanel.settings.wizardWidgetStatus){
                    SourcePanel.settings.wizardWidgetStatus = false;
                    $("#mecsim_source_toolwizard").hide();
                    $("#mecsim_source_toolwizardwidget").animate({width: "400px", height: "20px"}, 400);
                    $("#mecsim_source_toolwizardwidget").resizable('disable');
                }else{
                    SourcePanel.settings.wizardWidgetStatus = true;
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

        },

        //method to build up the wizard content
        buildWizardContent: function() {
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
            SourcePanel.settings.colorpicker = $("#mecsim_source_colorpicker").spectrum({
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
        },

        //method to validate if the current wizard step is valide
        validateWizardStep: function(event , currentIndex, newIndex) {
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
        },

        //method to finish the last wizard step
        finishWizard :function(){
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
                            "r"        : SourcePanel.settings.colorpicker.spectrum("get")._r,
                            "g"        : SourcePanel.settings.colorpicker.spectrum("get")._g,
                            "b"        : SourcePanel.settings.colorpicker.spectrum("get")._b
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
        },

        //method to update factory settings
        updateFactorySettings: function(){
            if($( "#mecsim_source_selectFactory option:selected").attr("requireASL") === "true"){
                $("#mecsim_source_selectASLContainer").show();
            }else{
                $("#mecsim_source_selectASLContainer").hide();
            }
        },

        //method to update generator settings
        updateGeneratorSettings: function(){
            $("#mecsim_source_generatorinput1").val(3);
            if($("#mecsim_source_selectGenerator").val() === "uniform distribution" || $("#mecsim_source_selectGenerator").val() === "Gleichverteilung"){
                $("#mecsim_source_generatorinput2").val(3);
                $("#mecsim_source_generatorinput3").val(7);
            }else{
                $("#mecsim_source_generatorinput2").val(5);
                $("#mecsim_source_generatorinput3").val(1);
            }
        },

    };
