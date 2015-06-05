/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

 /** todo collection for source-ui ck

 targeting
 TODO waypoint list read from java
 TODO resize widget and hide overflow
 TODO name/id/lat/long/type/button/path length
 TODO default target (viewpoint)
 TODO tabs for makrov editor / weighting matrix
 TODO markrov editor buttons (reset / show or hide no connected nodes / remove edges /save)
 TODO makrov editor (see d3.js)
 TODO weighting matrix
 TODO relative vs absolute weighting (recalc relativ weighting)
 TODO java structure (makrov chain, routing)

 wizard
 TODO save toolbox in config/web
 TODO histogramm
 TODO better error messges

 feature
 TODO plot distribution -> responsive wizard height
 TODO bounding in minimized mode maybe snap to tab
 TODO layout multiple widgets (z index and bounding)
 TODO check last jquery slectors
 TODO distingusish between open and close klick
 **/

var SourcePanel = ( function (px_module) {

    px_module.settings = {
        labels  :   {},
        dom     :   {
            label           : {},
            panel           : $("#mecsim_source_panel"),
            toolbox         : $("#mecsim_source_toolbox"),
            createTool      : $("#mecsim_source_createTool"),
            targetingButton : $("#mecsim_source_targetingButton")
        },
        obj     :   {}
    };

    //method to initialize source-ui
    px_module.init = function() {

        //get labels and build the toolbox
        SourcePanel.getLabels();
        SourcePanel.buildToolbox();

        //load template if panel clicked
        SourcePanel.settings.dom.panel.on("click", function(data){
            MecSim.ui().content().empty();
            MecSim.ui().content().load("template/source.htm", function(){

                MecSim.ui().content().hide();

                //get dom elements and set labels
                SourcePanel.getDOMElements();
                MecSim.language("getstaticwaypointlabels", function(){

                    //create targeting widget
                    SourcePanel.settings.obj.targetingWidget = Widget.createWidget(
                        SourcePanel.settings.dom.targetingWidget,
                        {
                            name    : "Temp Name",
                            width   : 450,
                            height  : 575
                        }
                    );
                    SourcePanel.settings.obj.targetingWidget.close();

                    //dummy date will be replace in the next commit
                    var data = [
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Random", "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", ""],
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                        ["Waypoint1", 1, "Path"  , "<input class='mecsim_source_wizardInputSmall' value=" + 1 + "></input>", "<button>edit</button>"],
                    ];

                    //create waypoint table
                    SourcePanel.settings.obj.targetingTable = SourcePanel.settings.dom.targetingTable.DataTable( {
                            "data": data,
                            "paging": true,
                            "ordering": false,
                            "info":     false,
                            "autoWidth": false,
                            "columns": [
                                {"title": "Name"},
                                {"title": "ID"},
                                {"title": "Type"},
                                {"title": "Path Length"},
                                {"title": ""}
                            ]
                    });


                    //create wizard widget
                    SourcePanel.settings.obj.wizardWidget = Widget.createWidget(
                        SourcePanel.settings.dom.wizardWidget,
                        {
                            name     : SourcePanel.settings.labels.wizardwidget,
                            width    : 850
                        }
                    );
                    SourcePanel.settings.obj.wizardWidget.close();

                    //create wizard
                    SourcePanel.settings.obj.wizard = SourcePanel.settings.dom.wizard.steps({
                        headerTag: "h3",
                        bodyTag: "section",
                        transitionEffect: "slideLeft",
                        stepsOrientation: "vertical",
                        autoFocus: true,
                        preloadContent: false,
                        onInit: SourcePanel.buildContent,
                        onStepChanging: SourcePanel.validateWizardStep,
                        onFinished: SourcePanel.finishWizard,
                        labels: {
                          finish: SourcePanel.settings.labels.finish,
                          next: SourcePanel.settings.labels.next,
                          previous: SourcePanel.settings.labels.previous,
                        }
                    });

                    SourcePanel.settings.obj.slider = SourcePanel.settings.dom.lingerSlider.slider({
                        range:true,
                        min: 0,
                        max: 100,
                        values: [25, 75],
                        slide: function(event, ui){
                            SourcePanel.settings.dom.label.lingerSliderLabel.text(ui.values[0] + " - " + ui.values[1]  + "%");
                        }
                    });

                    //additional initial value
                    SourcePanel.settings.dom.selectRadius.val(0.75);
                    SourcePanel.settings.dom.generatorInputCarcount.val(1);
                    SourcePanel.settings.dom.toolName.attr("value", SourcePanel.settings.labels.selecttoolnamevalue);
                    SourcePanel.settings.dom.label.lingerSliderLabel.text(SourcePanel.settings.obj.slider.slider("values", 0) + " - " + SourcePanel.settings.obj.slider.slider("values", 1) + "%");

                    MecSim.ui().content().show();

                    //listen to different ui elements
                    SourcePanel.settings.dom.selectWaypointType.on("change", SourcePanel.updateWaypointSettings);
                    SourcePanel.settings.dom.selectFactory.on("change", SourcePanel.updateFactorySettings);
                    SourcePanel.settings.dom.selectGenerator.on("change", SourcePanel.updateGeneratorSettings);
                    SourcePanel.settings.dom.selectSpeedProb.on("change", SourcePanel.updateCarSettings);
                    SourcePanel.settings.dom.selectMaxSpeedProb.on("change", SourcePanel.updateCarSettings);
                    SourcePanel.settings.dom.selectAccProb.on("change", SourcePanel.updateCarSettings);
                    SourcePanel.settings.dom.selectDecProb.on("change", SourcePanel.updateCarSettings);
                });
            });
        });

        //listen to create tool button
        SourcePanel.settings.dom.createTool.button().on("click", function(data){
            SourcePanel.settings.obj.wizardWidget.close();
        });

        //listen to configure waypoint path button
        SourcePanel.settings.dom.targetingButton.button().on("click", function(data){
            SourcePanel.settings.obj.targetingWidget.close();
        });
    };

    //method to get DOM Elements
    px_module.getDOMElements = function(){

        //dom elements (no labels)
        SourcePanel.settings.dom.targetingWidget                 = $('#mecsim_source_targetingWidget');
        SourcePanel.settings.dom.targetingTable                  = $('#mecsim_source_targetingTable');
        SourcePanel.settings.dom.wizardWidget                    = $("#mecsim_source_wizardWidget");
        SourcePanel.settings.dom.wizard                          = $("#mecsim_source_wizard");
        SourcePanel.settings.dom.selectWaypointType              = $("#mecsim_source_selectWaypointType");
        SourcePanel.settings.dom.selectRadius                    = $("#mecsim_source_waypointRadius");
        SourcePanel.settings.dom.selectFactory                   = $("#mecsim_source_selectFactory");
        SourcePanel.settings.dom.selectAgentProgram              = $("#mecsim_source_selectAgentProgram");
        SourcePanel.settings.dom.selectGenerator                 = $("#mecsim_source_selectGenerator");
        SourcePanel.settings.dom.generatorInputCarcount          = $("#mecsim_source_generatorInputCarcount");
        SourcePanel.settings.dom.generatorInput1                 = $("#mecsim_source_generatorInput1");
        SourcePanel.settings.dom.generatorInput2                 = $("#mecsim_source_generatorInput2");
        SourcePanel.settings.dom.carSettings                     = $("#mecsim_source_carSettings");
        SourcePanel.settings.dom.selectSpeedProb                 = $("#mecsim_source_selectSpeedProb");
        SourcePanel.settings.dom.speedProbInput1                 = $("#mecsim_source_speedProbInput1");
        SourcePanel.settings.dom.speedProbInput2                 = $("#mecsim_source_speedProbInput2");
        SourcePanel.settings.dom.selectMaxSpeedProb              = $("#mecsim_source_selectMaxSpeedProb");
        SourcePanel.settings.dom.maxSpeedProbInput1              = $("#mecsim_source_maxSpeedProbInput1");
        SourcePanel.settings.dom.maxSpeedProbInput2              = $("#mecsim_source_maxSpeedProbInput2");
        SourcePanel.settings.dom.selectAccProb                   = $("#mecsim_source_selectAccProb");
        SourcePanel.settings.dom.accProbInput1                   = $("#mecsim_source_accProbInput1");
        SourcePanel.settings.dom.accProbInput2                   = $("#mecsim_source_accProbInput2");
        SourcePanel.settings.dom.selectDecProb                   = $("#mecsim_source_selectDecProb");
        SourcePanel.settings.dom.decProbInput1                   = $("#mecsim_source_decProbInput1");
        SourcePanel.settings.dom.decProbInput2                   = $("#mecsim_source_decProbInput2");
        SourcePanel.settings.dom.lingerSlider                    = $("#mecsim_source_lingerSlider");
        SourcePanel.settings.dom.colorpicker                     = $("#mecsim_source_colorpicker");
        SourcePanel.settings.dom.toolName                        = $("#mecsim_source_toolName");
        SourcePanel.settings.dom.errorMessage                    = $("#mecsim_source_errorMessage");

        //dom elements (dynamic labels)
        SourcePanel.settings.dom.label.generatorInput1label      = $("#mecsim_source_generatorInput1_label");
        SourcePanel.settings.dom.label.generatorInput2label      = $("#mecsim_source_generatorInput2_label");
        SourcePanel.settings.dom.label.speedprobinput1label      = $("#mecsim_source_speedProbInput1_label");
        SourcePanel.settings.dom.label.speedprobinput2label      = $("#mecsim_source_speedProbInput2_label");
        SourcePanel.settings.dom.label.maxSpeedprobinput1label   = $("#mecsim_source_maxSpeedProbInput1_label");
        SourcePanel.settings.dom.label.maxSpeedprobinput2label   = $("#mecsim_source_maxSpeedProbInput2_label");
        SourcePanel.settings.dom.label.accprobinput1label        = $("#mecsim_source_accProbInput1_label");
        SourcePanel.settings.dom.label.accprobinput2label        = $("#mecsim_source_accProbInput2_label");
        SourcePanel.settings.dom.label.decprobinput1label        = $("#mecsim_source_decProbInput1_label");
        SourcePanel.settings.dom.label.decprobinput2label        = $("#mecsim_source_decProbInput2_label");
        SourcePanel.settings.dom.label.lingerSliderLabel         = $("#mecsim_source_lingerLabel");
     };

    //method to get source-ui related labels which are dynamic
    px_module.getLabels = function(){
        $.ajax({
            url     : "/clanguageenvironment/getdynamicwaypointlabels",
            success : function( px_data ){

                for(var key in px_data){
                    SourcePanel.settings.labels[key] = px_data[key];
                }
            }
        });
    };

    //method to create toolbox
    px_module.buildToolbox = function(){
        $.ajax({
            url     : "/cwaypointenvironment/listtools",
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {
                    SourcePanel.createToolButton(pc_key, px_value.redValue, px_value.greenValue, px_value.blueValue, px_value.deleteable);
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

    //method to create a toolbutton
    px_module.createToolButton = function(toolname, redValue, greenValue, blueValue, deleteable){
        $("<button></button>")
            .text(toolname)
            .attr("class", "mecsim_source_toolButton")
            .attr("value", toolname)
            .button( deleteable ? {icons: {secondary: "ui-icon-close"}} : {})
            .prepend(
                $("<span></span>")
                    .css("background-color", "rgb("+ redValue +","+ greenValue +","+ blueValue +")")
                    .attr("class", "mecsim_source_toolIcon")
            )
            .on("click", {"toolname": toolname}, SourcePanel.listenToolButton)
            .appendTo(SourcePanel.settings.dom.toolbox)
            .find($(".ui-button-icon-secondary")).on("click", SourcePanel.deleteToolButton);
    };

    //method to delete a tool
    px_module.deleteToolButton = function(event){
        $.ajax({
            url     : "/cwaypointenvironment/deletetool",
            data    : {"toolname": event.target.parentNode.value},
            success : function(p_data){
                if(p_data){
                    event.target.parentNode.remove();
                }
            }
        });
    };

    //method to build up the wizard content
    px_module.buildContent = function() {

        SourcePanel.getDOMElements();

        //create selectmenu with waypoint types
        $.ajax({
            url     : "/cwaypointenvironment/listwaypointtypes",
            success : function( px_data ){
                px_data.forEach(function(data){
                    SourcePanel.settings.dom.selectWaypointType
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
            }
        });

        //create selectmenu with factory types
        $.ajax({
            url     : "/cwaypointenvironment/listfactories",
            success : function( px_data ){
                $.each( px_data, function( pc_key, px_value ) {
                    SourcePanel.settings.dom.selectFactory
                        .append( $("<option></option>")
                        .attr("value",pc_key)
                        .attr("requireAgent", px_value)
                        .text(pc_key));
                });
                SourcePanel.updateFactorySettings();
            }
        });

        //create selectmenu with possible agent programs
        $.ajax({
            url     : "/cagentenvironment/jason/list",
            success : function( px_data ){
                px_data.agents.forEach(function(data){
                    SourcePanel.settings.dom.selectAgentProgram
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
            }
        });

        //create all selectmenu with distribution select
        $.ajax({
            url     : "/cwaypointenvironment/listdistribution",
            success : function( px_data ){
                px_data.forEach(function(data){
                    $("<option></option>")
                        .attr("value",data)
                        .text(data)
                        .appendTo(".mecsim_source_distributionSelect");
                    });

                SourcePanel.updateGeneratorSettings();
                SourcePanel.updateCarSettings({target : {id: "mecsim_source_selectSpeedProb"} });
                SourcePanel.updateCarSettings({target : {id: "mecsim_source_selectMaxSpeedProb"} });
                SourcePanel.updateCarSettings({target : {id: "mecsim_source_selectAccProb"} });
                SourcePanel.updateCarSettings({target : {id: "mecsim_source_selectDecProb"} });
            }
        });

        //create carsettings accordion
        SourcePanel.settings.dom.carSettings.accordion({collapsible: true, heightStyle: "content", active: false});

        //create colorpicker
        SourcePanel.settings.obj.colorpicker = $("#mecsim_source_colorpicker").spectrum({
            showPaletteOnly: true,
            togglePaletteOnly: true,
            togglePaletteMoreText: 'more',
            togglePaletteLessText: 'less',
            color: '#008C4F',
            palette: [
                ["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
                ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
                ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"]
            ]
        });
    };

    //method to validate if the current wizard step is valide
    px_module.validateWizardStep = function(event , currentIndex, newIndex) {

        function validateDistributionInput(distribution, distributionInput1, distributionInput2){
            if( isNaN(distributionInput1) || isNaN(distributionInput2) )
                return false;

            if(distribution === "uniform distribution" || distribution === "Gleichverteilung"){
                if( (distributionInput1 >= distributionInput2) || (distributionInput1 < 0) )
                    return false;
            }else{
                if( distributionInput1 < distributionInput2){
                    return false;
                }
            }
            return true;
        }

        if(currentIndex===0){
            if($('#mecsim_source_selectFactory option:selected').attr('requireAgent')==="true" && SourcePanel.settings.dom.selectAgentProgram.val()===null)
                return false;
        }

        if(currentIndex===1){
            if( isNaN(Number(SourcePanel.settings.dom.generatorInputCarcount.val())) || SourcePanel.settings.dom.generatorInputCarcount.val() <= 0)
                return false;

            return validateDistributionInput(SourcePanel.settings.dom.selectGenerator.val(), Number(SourcePanel.settings.dom.generatorInput1.val()),  Number(SourcePanel.settings.dom.generatorInput2.val()));
        }

        if(currentIndex===2){
            return  validateDistributionInput(SourcePanel.settings.dom.selectSpeedProb.val(), Number(SourcePanel.settings.dom.speedProbInput1.val()),  Number(SourcePanel.settings.dom.speedProbInput2.val()))              &&
                    validateDistributionInput(SourcePanel.settings.dom.selectMaxSpeedProb.val(), Number(SourcePanel.settings.dom.maxSpeedProbInput1.val()),  Number(SourcePanel.settings.dom.maxSpeedProbInput2.val()))     &&
                    validateDistributionInput(SourcePanel.settings.dom.selectAccProb.val(), Number(SourcePanel.settings.dom.accProbInput1.val()),  Number(SourcePanel.settings.dom.accProbInput2.val()))                    &&
                    validateDistributionInput(SourcePanel.settings.dom.selectDecProb.val(), Number(SourcePanel.settings.dom.decProbInput1.val()),  Number(SourcePanel.settings.dom.decProbInput2.val()));
        }

        return true;
    };

    //method to finish the last wizard step
    px_module.finishWizard = function(){
        $.ajax({
            url     : "/cwaypointenvironment/createtool",
            data    : {
                "waypointtype"       : SourcePanel.settings.dom.selectFactory.val(),
                "radius"             : SourcePanel.settings.dom.selectRadius.val(),
                "factory"            : SourcePanel.settings.dom.selectFactory.val(),
                "agentprogram"       : SourcePanel.settings.dom.selectAgentProgram.val(),
                "generator"          : SourcePanel.settings.dom.selectGenerator.val(),
                "carcount"           : SourcePanel.settings.dom.generatorInputCarcount.val(),
                "generatorinput1"    : SourcePanel.settings.dom.generatorInput1.val(),
                "generatorinput2"    : SourcePanel.settings.dom.generatorInput2.val(),

                "speedprob"          : SourcePanel.settings.dom.selectSpeedProb.val(),
                "speedprobinput1"    : SourcePanel.settings.dom.speedProbInput1.val(),
                "speedprobinput2"    : SourcePanel.settings.dom.speedProbInput2.val(),

                "maxspeedprob"       : SourcePanel.settings.dom.selectMaxSpeedProb.val(),
                "maxspeedprobinput1" : SourcePanel.settings.dom.maxSpeedProbInput1.val(),
                "maxspeedprobinput2" : SourcePanel.settings.dom.maxSpeedProbInput2.val(),

                "accprob"            : SourcePanel.settings.dom.selectAccProb.val(),
                "accprobinput1"      : SourcePanel.settings.dom.accProbInput1.val(),
                "accprobinput2"      : SourcePanel.settings.dom.accProbInput2.val(),

                "decprob"            : SourcePanel.settings.dom.selectDecProb.val(),
                "decprobinput1"      : SourcePanel.settings.dom.decProbInput1.val(),
                "decprobinput2"      : SourcePanel.settings.dom.decProbInput2.val(),

                "lingerprobinput1"   : SourcePanel.settings.obj.slider.slider("values", 0)/100,
                "lingerprobinput2"   : SourcePanel.settings.obj.slider.slider("values", 1)/100,

                "name"               : SourcePanel.settings.dom.toolName.val(),
                "red"                : SourcePanel.settings.obj.colorpicker.spectrum("get")._r,
                "green"              : SourcePanel.settings.obj.colorpicker.spectrum("get")._g,
                "blue"               : SourcePanel.settings.obj.colorpicker.spectrum("get")._b
            },
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {
                    SourcePanel.createToolButton(pc_key, px_value.redValue, px_value.greenValue, px_value.blueValue, px_value.deleteable);
                });

                SourcePanel.settings.obj.wizardWidget.close();
                setTimeout(function(){
                    SourcePanel.settings.obj.wizard.steps("setStep", 0);
                }, SourcePanel.settings.obj.wizardWidget._animationTime);
            }
        }).fail(function(){
            SourcePanel.settings.dom.errorMessage.text(SourcePanel.settings.labels.toolcreationfailed);
        });
    };

    //method to update waypoint settings
    px_module.updateWaypointSettings = function(){
        if(SourcePanel.settings.dom.selectWaypointType.val() === "Auto Wegpunkt (Zufall)" || SourcePanel.settings.dom.selectWaypointType.val() === "random car waypoint"){
            SourcePanel.settings.dom.selectRadius.attr('disabled', false);
        }else{
            SourcePanel.settings.dom.selectRadius.attr('disabled', true);
        }
    };

    //method to update factory settings
    px_module.updateFactorySettings = function(){
        if($("#mecsim_source_selectFactory option:selected").attr("requireAgent") === "true"){
            SourcePanel.settings.dom.selectAgentProgram.attr('disabled', false);
        }else{
            SourcePanel.settings.dom.selectAgentProgram.attr('disabled', true);
        }
    };

    //method to update generator settings
    px_module.updateGeneratorSettings = function(){

        SourcePanel.updateLabels(
            SourcePanel.settings.dom.selectGenerator.val(),
            [
                {
                    expected: ["uniform distribution", "Gleichverteilung"],
                    config : [
                        { element : SourcePanel.settings.dom.label.generatorInput1label, text   : SourcePanel.settings.labels.selectyourlowerbound },
                        { element : SourcePanel.settings.dom.label.generatorInput2label, text   : SourcePanel.settings.labels.selectyourupperbound },
                        { element : SourcePanel.settings.dom.generatorInput1, value  : 1 },
                        { element : SourcePanel.settings.dom.generatorInput2, value  : 3 }
                    ]
                },
                {
                    expected: ["default"],
                    config : [
                        { element : SourcePanel.settings.dom.label.generatorInput1label, text  : SourcePanel.settings.labels.selectyourmean },
                        { element : SourcePanel.settings.dom.label.generatorInput2label, text  : SourcePanel.settings.labels.selectyourdeviation },
                        { element : SourcePanel.settings.dom.generatorInput1, value  : 5 },
                        { element : SourcePanel.settings.dom.generatorInput2, value  : 1 }
                    ]
                }
            ]
        );
    };

    //method to update car settings
    px_module.updateCarSettings = function(event){

        var l_element1, l_element2, l_element3, l_element4;
        switch(event.target.id){

            case "mecsim_source_selectSpeedProb":
                l_value    = SourcePanel.settings.dom.selectSpeedProb.val();
                l_element1 = SourcePanel.settings.dom.label.speedprobinput1label;
                l_element2 = SourcePanel.settings.dom.label.speedprobinput2label;
                l_element3 = SourcePanel.settings.dom.speedProbInput1;
                l_element4 = SourcePanel.settings.dom.speedProbInput2;
                l_defaultPara1 = 50;
                l_defaultPara2 = 25;
                break;

            case "mecsim_source_selectMaxSpeedProb":
                l_value    = SourcePanel.settings.dom.selectMaxSpeedProb.val();
                l_element1 = SourcePanel.settings.dom.label.maxSpeedprobinput1label;
                l_element2 = SourcePanel.settings.dom.label.maxSpeedprobinput2label;
                l_element3 = SourcePanel.settings.dom.maxSpeedProbInput1;
                l_element4 = SourcePanel.settings.dom.maxSpeedProbInput2;
                l_defaultPara1 = 250;
                l_defaultPara2 = 50;
                break;

            case "mecsim_source_selectAccProb":
                l_value    = SourcePanel.settings.dom.selectAccProb.val();
                l_element1 = SourcePanel.settings.dom.label.accprobinput1label;
                l_element2 = SourcePanel.settings.dom.label.accprobinput2label;
                l_element3 = SourcePanel.settings.dom.accProbInput1;
                l_element4 = SourcePanel.settings.dom.accProbInput2;
                l_defaultPara1 = 20;
                l_defaultPara2 = 5;
                break;

            case "mecsim_source_selectDecProb":
                l_value    = SourcePanel.settings.dom.selectDecProb.val();
                l_element1 = SourcePanel.settings.dom.label.decprobinput1label;
                l_element2 = SourcePanel.settings.dom.label.decprobinput2label;
                l_element3 = SourcePanel.settings.dom.decProbInput1;
                l_element4 = SourcePanel.settings.dom.decProbInput2;
                l_defaultPara1 = 20;
                l_defaultPara2 = 5;
                break;
        }

        SourcePanel.updateLabels(
            l_value,
            [
                {
                    expected: ["uniform distribution", "Gleichverteilung"],
                    config : [
                        { element : l_element1, text   : SourcePanel.settings.labels.selectyourlowerbound },
                        { element : l_element2, text   : SourcePanel.settings.labels.selectyourupperbound },
                        { element : l_element3, value  : l_defaultPara1 },
                        { element : l_element4, value  : l_defaultPara2 }
                    ]
                },
                {
                    expected: ["default"],
                    config : [
                        { element : l_element1, text  : SourcePanel.settings.labels.selectyourmean },
                        { element : l_element2, text  : SourcePanel.settings.labels.selectyourdeviation },
                        { element : l_element3, value  : l_defaultPara1 },
                        { element : l_element4, value  : l_defaultPara2 }
                    ]
                }
            ]
        );
    };

    //generic method to upate labels
    px_module.updateLabels = function(checkElement, options){
        var foundflag = false;

        options.forEach(function(p_option){
            if(foundflag)
                return;

            p_option.expected.forEach(function(p_expected){
                if(checkElement === p_expected || p_expected === "default"){
                    p_option.config.forEach(function(entry){
                        if(entry.text !== undefined && entry.text !== null)
                            entry.element.text(entry.text);
                        if(entry.value !== undefined && entry.value !== null)
                            entry.element.val(entry.value);
                    });
                    foundflag = true;
                }
            });
        });
    };

    return px_module;

}(SourcePanel || {}));
