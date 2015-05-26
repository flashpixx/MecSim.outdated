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
TODO finish allround restruct
TODO cleanup css
TODO remove unused code
TODO rename asl
TODO rename input
TODO check last jquery slectors
TODO differ between open and close
TODO refactor build content
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

    px_module.settings = {
        labels  :   {},
        dom     :   {
            panel       : $("#mecsim_source_panel"),
            toolbox     : $("#mecsim_source_toolbox"),
            createTool  : $("#mecsim_source_createTool")
        },
        obj     :   {}
    };

    //method to initialize source-ui
    px_module.init = function() {

        //get labels and build the toolbox
        SourcePanel.getLabels();
        SourcePanel.buildToolbox();

        SourcePanel.settings.dom.panel.on("click", function(data){
            MecSim.ui().content().empty();
            MecSim.ui().content().load("template/source.htm", function(){

                //general stuff after template is loaded
                SourcePanel.getDOMElements();
                SourcePanel.setLabels();

                //create widget
                SourcePanel.settings.obj.widget = Widget.createWidget(
                    SourcePanel.settings.dom.widget,
                    {
                        name: SourcePanel.settings.labels.wizardwidget
                    }
                );

                //create wizard
                SourcePanel.settings.obj.wizard = SourcePanel.settings.dom.wizard.steps({
                    headerTag: "h3",
                    bodyTag: "section",
                    transitionEffect: "slideLeft",
                    stepsOrientation: "vertical",
                    autoFocus: true,
                    onInit: SourcePanel.buildContent,
                    onStepChanging: SourcePanel.validateWizardStep,
                    onFinished: SourcePanel.finishWizard
                });

                //get dom elements (needed because dom tree changes)
                SourcePanel.getDOMElements();

                //listen to ui elements
                SourcePanel.settings.dom.selectFactory.on("change", SourcePanel.updateFactorySettings);
                SourcePanel.settings.dom.selectGenerator.on("change", SourcePanel.updateGeneratorSettings);

            });
        });

        //listen to create tool button
        SourcePanel.settings.dom.createTool.button().on("click", function(data){
            SourcePanel.settings.obj.widget.close();
        });
    };

    //method to get DOM Elements (template)
    px_module.getDOMElements = function(){
        //labels
        SourcePanel.settings.dom.factorySettingsLabel      = $("#mesim_source_factorySettings_label");
        SourcePanel.settings.dom.generatorSettingsLabel    = $("#mesim_source_generatorSettings_label");
        SourcePanel.settings.dom.carSettingsLabel          = $("#mesim_source_carSettings_label");
        SourcePanel.settings.dom.customizingLabel          = $("#mesim_source_customizing_label");
        SourcePanel.settings.dom.selectFactoryLabel        = $("#mecsim_source_selectFactory_label");
        SourcePanel.settings.dom.selectASLLabel            = $("#mecsim_source_selectASL_label");
        SourcePanel.settings.dom.selectGeneratorLabel      = $("#mecsim_source_selectGenerator_label");
        SourcePanel.settings.dom.generatorInput1Label      = $("#mecsim_source_generatorInput1_label");
        SourcePanel.settings.dom.generatorInput2Label      = $("#mecsim_source_generatorInput2_label");
        SourcePanel.settings.dom.generatorInput3Label      = $("#mecsim_source_generatorInput3_label");
        SourcePanel.settings.dom.speedProbLabel            = $("#mecsim_source_speedProb_label");
        SourcePanel.settings.dom.maxSpeedProbLabel         = $("#mecsim_source_maxSpeedProb_label");
        SourcePanel.settings.dom.accProbLabel              = $("#mecsim_source_accProb_label");
        SourcePanel.settings.dom.decProbLabel              = $("#mecsim_source_decProb_label");
        SourcePanel.settings.dom.lingerProbLabel           = $("#mecsim_source_lingerProb_label");
        SourcePanel.settings.dom.toolNameLabel             = $("#mecsim_source_toolName_label");
        SourcePanel.settings.dom.toolName                  = $("#mecsim_source_toolName");
        SourcePanel.settings.dom.toolColorLabel            = $("#mecsim_source_toolColor_label");

        //futher dom elements
        SourcePanel.settings.dom.widget                    = $("#mecsim_source_widget");
        SourcePanel.settings.dom.wizard                    = $("#mecsim_source_wizard");
        SourcePanel.settings.dom.colorpicker               = $("#mecsim_source_colorpicker");
        SourcePanel.settings.dom.selectFactory             = $("#mecsim_source_selectFactory");
        SourcePanel.settings.dom.selectASL                 = $("#mecsim_source_selectASL");
        SourcePanel.settings.dom.ASLContainer              = $("#mecsim_source_selectASLContainer");
        SourcePanel.settings.dom.selectGenerator           = $("#mecsim_source_selectGenerator");
        SourcePanel.settings.dom.generatorInput1           = $("#mecsim_source_generatorInput1");
        SourcePanel.settings.dom.generatorInput2           = $("#mecsim_source_generatorInput2");
        SourcePanel.settings.dom.generatorInput3           = $("#mecsim_source_generatorInput3");
     };

    //method to get source-ui related labels
    px_module.getLabels = function(){
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
    };

    //method to set source-ui related labels
    px_module.setLabels = function(){
        SourcePanel.settings.dom.factorySettingsLabel.text(SourcePanel.settings.labels.factorysettings);
        SourcePanel.settings.dom.generatorSettingsLabel.text(SourcePanel.settings.labels.generatorsettings);
        SourcePanel.settings.dom.carSettingsLabel.text(SourcePanel.settings.labels.carsettings);
        SourcePanel.settings.dom.customizingLabel.text(SourcePanel.settings.labels.customizing);
        SourcePanel.settings.dom.selectFactoryLabel.text(SourcePanel.settings.labels.selectyourfactory);
        SourcePanel.settings.dom.selectASLLabel.text(SourcePanel.settings.labels.selectyourasl);
        SourcePanel.settings.dom.selectGeneratorLabel.text(SourcePanel.settings.labels.selectyourgenerator);
        SourcePanel.settings.dom.generatorInput1Label.text(SourcePanel.settings.labels.selectyourcarcount);
        SourcePanel.settings.dom.generatorInput2Label.text(SourcePanel.settings.labels.selectyourlowerbound);
        SourcePanel.settings.dom.generatorInput3Label.text(SourcePanel.settings.labels.selectyourupperbound);
        SourcePanel.settings.dom.speedProbLabel.text(SourcePanel.settings.labels.selectcarspeedprob);
        SourcePanel.settings.dom.maxSpeedProbLabel.text(SourcePanel.settings.labels.selectmaxcarspeedprob);
        SourcePanel.settings.dom.accProbLabel.text(SourcePanel.settings.labels.selectaccprob);
        SourcePanel.settings.dom.decProbLabel.text(SourcePanel.settings.labels.selectdecprob);
        SourcePanel.settings.dom.lingerProbLabel.text(SourcePanel.settings.labels.selectlingerprob);
        SourcePanel.settings.dom.toolNameLabel.text(SourcePanel.settings.labels.selecttoolname);
        SourcePanel.settings.dom.toolName.attr("value", SourcePanel.settings.labels.selecttoolnamevalue);
        SourcePanel.settings.dom.toolColorLabel.text(SourcePanel.settings.labels.selecttoolcolor);
    };

    //method to create toolbox
    px_module.buildToolbox = function(){
        $.ajax({
            url     : "/cwaypointenvironment/listtools",
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {

                    $("</p>").appendTo(SourcePanel.settings.dom.toolbox);

                    var button = $("<button></button>")
                        .text(pc_key)
                        .attr("class", "mecsim_global_accordeonbutton")
                        .attr("value",pc_key)
                        .button()
                        .on("click", {"toolname": pc_key}, SourcePanel.listenToolButton)
                        .appendTo(SourcePanel.settings.dom.toolbox);

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

    //method to build up the wizard content
    px_module.buildContent = function() {

        //create selectmenu with factory types
        $.ajax({
            url     : "/cwaypointenvironment/listfactories",
            success : function( px_data ){
                px_data.factories.forEach(function(data){
                    $.each( data, function( pc_key, px_value ) {
                        SourcePanel.settings.dom.selectFactory
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
                    SourcePanel.settings.dom.selectASL
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
                    SourcePanel.settings.dom.selectGenerator
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
                SourcePanel.updateGeneratorSettings();
            }
        });

        //create colorpicker
        SourcePanel.settings.obj.colorpicker = $("#mecsim_source_colorpicker").spectrum({
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

        //validate first step
        if(currentIndex===0){
            if($('#mecsim_source_selectFactory option:selected').attr('requireASL')==="true" && SourcePanel.settings.dom.selectASL.val()===null)
                return false;
        }

        //validate second step
        if(currentIndex===1){
            var input1 = Number(SourcePanel.settings.dom.generatorInput1.val());
            var input2 = Number(SourcePanel.settings.dom.generatorInput2.val());
            var input3 = Number(SourcePanel.settings.dom.generatorInput3.val());

            if( isNaN(input1) || isNaN(input2) || isNaN(input3) || input1 <= 0)
                return false;

            if(SourcePanel.settings.dom.selectGenerator.val() === "uniform distribution" || SourcePanel.settings.dom.selectGenerator.val() === "Gleichverteilung"){
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
                        "factory"  : SourcePanel.settings.dom.selectFactory.val(),
                        "asl"      : SourcePanel.settings.dom.selectASL.val(),
                        "generator": SourcePanel.settings.dom.selectGenerator.val(),
                        "input1"   : SourcePanel.settings.dom.generatorInput1.val(),
                        "input2"   : SourcePanel.settings.dom.generatorInput2.val(),
                        "input3"   : SourcePanel.settings.dom.generatorInput3.val(),
                        "name"     : SourcePanel.settings.dom.toolName.val(),
                        "r"        : SourcePanel.settings.obj.colorpicker.spectrum("get")._r,
                        "g"        : SourcePanel.settings.obj.colorpicker.spectrum("get")._g,
                        "b"        : SourcePanel.settings.obj.colorpicker.spectrum("get")._b
                    },
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {

                    $("</p>").appendTo(SourcePanel.settings.dom.toolbox);

                    var button = $("<button></button>")
                        .text(pc_key)
                        .attr("class", "mecsim_global_accordeonbutton")
                        .attr("value",pc_key)
                        .button()
                        .on("click", {"toolname": pc_key}, SourcePanel.listenToolButton)
                        .appendTo(SourcePanel.settings.dom.toolbox);

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
        if($("#mecsim_source_selectFactory option:selected").attr("requireASL") === "true"){
            SourcePanel.settings.dom.ASLContainer.show();
        }else{
            SourcePanel.settings.dom.ASLContainer.hide();
        }
    };

    //method to update generator settings
    px_module.updateGeneratorSettings = function(){
        SourcePanel.settings.dom.generatorInput1.val(3);
        if(SourcePanel.settings.dom.selectGenerator.val() === "uniform distribution" || SourcePanel.settings.dom.selectGenerator.val() === "Gleichverteilung"){
            SourcePanel.settings.dom.generatorInput2Label.text(SourcePanel.settings.labels.selectyourlowerbound);
            SourcePanel.settings.dom.generatorInput3Label.text(SourcePanel.settings.labels.selectyourupperbound);
            SourcePanel.settings.dom.generatorInput2.val(3);
            SourcePanel.settings.dom.generatorInput3.val(7);
        }else{
            SourcePanel.settings.dom.generatorInput2Label.text(SourcePanel.settings.labels.selectyourmean);
            SourcePanel.settings.dom.generatorInput3Label.text(SourcePanel.settings.labels.selectyourdeviation);
            SourcePanel.settings.dom.generatorInput2.val(5);
            SourcePanel.settings.dom.generatorInput3.val(1);
        }
    };

    return px_module;

}(SourcePanel || {}));
