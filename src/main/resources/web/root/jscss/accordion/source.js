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
        TODO Further wizard validation
        TODO Further GUI Elements (WaypointType, Radius, CarSettings Patameters, Histrogramm)
        TODO move jquery selectors into settings for better configuration
        TODO bounding in minimized mode maybe snap to tab
        TODO plot distribution
        TODO Car Settings
        TODO Tool creation
        TODO show error dialog if tool can not created (check for correctness on java side)
        TODO distingusish between init and refresh(asl, ...)
        TODO source list table (JTable or JQuery Plugin)
        TODO source weighting table (JTable or JQuery Plugin)
        TODO radial refactor (bugs and generic)
        TODO clean up source-ui styles inside of layout.css
        TODO remove old gui if new is ready
        **/

        //method to initialize source-ui
        init: function() {
            mecsim_source = this.settings;
            this.getLabels();
            this.setToolbox();

            $("#mecsim_source_oldgui").button().on("click", function(data){
                UI().getContent().empty();
                UI().getContent().load("template/sourceold.htm", function(){
                    SourcePanel.initClusterWidget();
                    SourcePanel.initSettingsWidget();
                    SourcePanel.initTargetWeighting();
                });
            });

            $("#ui-id-5").on("click", function(data){
                UI().getContent().empty();
                UI().getContent().load("template/source.htm", function(){
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
                success : function( px_data ){
                    px_data.tools.forEach(function(data){
                        $("<button></button>")
                            .text(data)
                            .attr("class", "mecsim_global_accordeonbutton")
                            .attr("value",data)
                            .button({ icons: { primary: "mecsim_source_cyanCircle" } })
                            .appendTo($("#mecsim_source_toolbox"));
                    });
                }
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
                success : function( px_data ){
                    px_data.tools.forEach(function(data){
                        $("</p>").appendTo($("#mecsim_source_toolbox"));
                        $("<button></button>")
                            .text(data)
                            .attr("class", "mecsim_global_accordeonbutton")
                            .attr("value",data)
                            .button({ icons: { primary: "mecsim_source_cyanCircle" } })
                            .appendTo($("#mecsim_source_toolbox"));
                    });                }
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

        //method to init the settings widget
        initSettingsWidget: function() {

            //basic layout (jquerry ui widgets)
            $("#mecsim_source_waypointsettings").draggable().resizable({
                animate: true,
                minHeight: 255,
                minWidth: 500
            });

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
                }
            }).done(function(){
                SourcePanel.updateLabels();
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

            // listen to the generator-select and change label
            $("#mecsim_source_selectGenerator").on("change", function(){
                SourcePanel.updateLabels();
            });

            //listen to the addtool-button
            $("#mecsim_source_addTool").on("click", function(){
                var color = SourcePanel.settings.colorpicker.spectrum("get");
                $.ajax({
                    url     : "/cwaypointenvironment/createtool",
                    data    : {
                                "factory"  : $("#mecsim_source_selectFactory").val(),
                                "asl"      : $("#mecsim_source_selectASL").val(),
                                "generator": $("#mecsim_source_selectGenerator").val(),
                                "input1"   : $("#mecsim_source_generatorinput1").val(),
                                "input2"   : $("#mecsim_source_generatorinput2").val(),
                                "input3"   : $("#mecsim_source_generatorinput3").val(),
                                "name"     : $("#mecsim_source_toolName").val(),
                                "r"        : color._r,
                                "g"        : color._g,
                                "b"        : color._b
                            },
                    success : function( px_data ){
                        px_data.tools.forEach(function(data){
                            $("</p>").appendTo($("#mecsim_source_toolbox"));
                            $("<button></button>")
                                .text(data)
                                .attr("value",data)
                                .button()
                                .appendTo($("#mecsim_source_toolbox"));
                        });
                    }
                }).fail(function(){
                    console.log("Not a valid tool");
                });

            });

        },

        //method to init the target-weighing widget
        initTargetWeighting: function() {

            $("#mecsim_source_targetweighting") .resizable({
                animate: true,
                minHeight: 375,
                minWidth: 500
            });

            /**
            $('#mecsim_source_weighting').dataTable({
                "paging": true,
                "lengthMenu": [4, 10, 25],
                "ordering": false,
                "info": false
            });

            $("#mecsim_source_weighting_length").on("change", function(data){
                var rows = $("select[name='mecsim_source_weighting_length']").val();
                var result = 215    + (37* parseInt(rows));
                SourcePanel.settings.targetweighting.height(result);
            });
            **/

        },

        //method to init the basic widget layout
        initClusterWidget: function() {

            //listen to the sorting buttons
            $("#mecsim_source_sortByDistance").on("click", function(){SourcePanel.sort(SourcePanel.sortByDistanceComp);});
            $("#mecsim_source_sortByType").on("click", function(){SourcePanel.sort(SourcePanel.sortByTypeComp);});
            $("#mecsim_source_sortByName").on("click", function(){SourcePanel.sort(SourcePanel.sortByNameComp);});

            SourcePanel.settings.cluster = d3.layout.cluster()
                .size([360, SourcePanel.settings.innerRadius]);
                //.sort(SourcePanel.settings.sortComp);

            SourcePanel.settings.bundle = d3.layout.bundle();

            SourcePanel.settings.line = d3.svg.line.radial()
                .interpolate("bundle")
                .tension(0.75)
                .radius(function(d) { return d.y; })
                .angle(function(d) { return d.x / 180 * Math.PI; });

            SourcePanel.settings.svg = d3.select("#mecsim_source_cluster").append("svg")
                .attr("width", SourcePanel.settings.diameter)
                .attr("height", SourcePanel.settings.diameter)
                .attr("oncontextmenu", "return false;")
                .append("g")
                .attr("transform", "translate(" + SourcePanel.settings.radius + "," + SourcePanel.settings.radius + ")");

            SourcePanel.settings.nodecontainer = SourcePanel.settings.svg.append("g").selectAll(".mecsim_source_node");
            SourcePanel.settings.linkcontainer = SourcePanel.settings.svg.append("g").selectAll(".mecsim_source_link");

            SourcePanel.createCluster(SourcePanel.generateData());
        },

        //method to parse a json-file and call it with a callback function
        readFile: function(file, callback) {
            d3.json(file, function(error, data){
                callback(data);
            });
        },

        //method which is responsible for creating and updating the cluster
        createCluster: function(input) {

            SourcePanel.settings.model = input;
            SourcePanel.settings.nodes = SourcePanel.settings.cluster.nodes(SourcePanel.nodeWrapper(SourcePanel.settings.model));
            SourcePanel.settings.links = SourcePanel.linkWrapper(SourcePanel.settings.nodes);

            SourcePanel.settings.nodecontainer = SourcePanel.settings.nodecontainer.data(SourcePanel.settings.nodes);

            //enter section
            SourcePanel.settings.nodecontainer.enter()
                .append("text")
                .attr("class", function(node){ return node.sourcemode ? "mecsim_source_node" + " mecsim_source_node-source" : "mecsim_source_node" + " mecsim_source_node-target";})
                .attr("dx", function(node){ return node.x < 180 ? 0 : "-" + String(node.key).length*6 + "px";})
                .attr("dy", ".31em")
                .attr("transform", function(node) { return "rotate(" + (node.x - 90) + ")translate(" + (node.y + 8) + ",0)" + (node.x < 180 ? "" : "rotate(180)"); })
                .text(function(node) { return node.key; })
                .on("mouseover", SourcePanel.mouseovered)
                .on("mouseout", SourcePanel.mouseouted)
                .on("click", SourcePanel.leftclick)
                .on("contextmenu", SourcePanel.rightclick);

            //exit section
            SourcePanel.settings.nodecontainer.exit().remove();

            SourcePanel.settings.linkcontainer = SourcePanel.settings.linkcontainer.data(SourcePanel.settings.bundle(SourcePanel.settings.links));

            //enter section
            SourcePanel.settings.linkcontainer.enter()
                .append("path")
                .each(function(link) {link.source = link[0]; link.target = link[link.length - 1]; })
                .attr("class", "mecsim_source_link")
                .attr("d", SourcePanel.settings.line);

            //exit section
            SourcePanel.settings.linkcontainer.exit().remove();

        },

        //method which produces from a json Input the node-strucutre
        nodeWrapper: function(input) {

            var children = [];

            input.forEach(function(entry){
                children.push(entry);
                entry.key = entry.name;
            });

            return {name:"", children:children};

        },

        //method which produces from a node-strucutre the link strucutre
        linkWrapper: function(nodeArray) {

            var map = {};
            var targets = [];

            //create a map with node-name and node
            nodeArray.forEach(function(entry) {
                map[entry.name] = entry;
            });

            //go threw the node array and create for every target of a node a link
            nodeArray.forEach(function(entry) {
                if (entry.targets){
                    entry.targets.forEach(function(targetsEntry) {

                        if(map[targetsEntry] !== undefined)
                            targets.push({source: map[entry.name], target: map[targetsEntry]});
                    });
                }
            });

            return targets;

        },

        //method which should be triggered if a node was leftclicked
        leftclick: function(nodeclicked) {

            //if no node is selected or another node was clicked
            if(SourcePanel.settings.selected === false || nodeclicked !== SourcePanel.settings.selected){

                //save the new selected node and make it black
                SourcePanel.settings.selected=nodeclicked;
                SourcePanel.settings.nodecontainer.classed("mecsim_source_node-selected", function(node){ return node.name === nodeclicked.name; });

                //all nodes which are not connected to the selected should be transparent
                SourcePanel.settings.nodecontainer.classed("mecsim_source_node-trans", function(node){
                    if(node === SourcePanel.settings.selected)
                        return false;

                    //check if a node is in the list of the selected node
                    var inTargets = true;
                    if(SourcePanel.settings.selected.targets.length <= 0) return false;
                    for(var i=0; i< SourcePanel.settings.selected.targets.length; i++){
                        if(SourcePanel.settings.selected.targets[i] === node.key)
                            inTargets = false;
                    }

                    return inTargets;
                });

                //links should be thick if they are connected to the selected node
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-thick", function(link){ return link.source === SourcePanel.settings.selected || link.target === SourcePanel.settings.selected; });
                //links should be hidden if they are not connected to the selected node
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-hidden", function(link){ return link.source !== SourcePanel.settings.selected && link.target !== SourcePanel.settings.selected; });

            }else{
                //if the selected node was de-selected remove all effekts
                selected=false;
                SourcePanel.settings.nodecontainer.classed("mecsim_source_node-selected", false);
                SourcePanel.settings.nodecontainer.classed("mecsim_source_node-trans", false);
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-thick", false);
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-hidden", false);
            }

        },

        //method which should be triggered if a node was rightclicked
        rightclick: function(rightclicked) {

            /** was commented out before!!!
                d3.event.preventDefault();

                if(selected === false)
                    return;
                if(selected.sourcemode === nodeclicked.sourcemode)
                    return;

                var exist = false;
                selected.targets.forEach(function(entry){
                    if(entry === nodeclicked.key){
                        exist = true;
                    }
                })

                if(!exist)
                    selected.targets.push(nodeclicked.name);

                createCluster(model);
             */

        },

        //method which sets a new comparator and resort the cluster
        sort: function(comparator) {

            SourcePanel.settings.cluster.sort(comparator);
            SourcePanel.settings.nodes = SourcePanel.settings.cluster.nodes(SourcePanel.nodeWrapper(SourcePanel.settings.model));
            SourcePanel.settings.links = SourcePanel.linkWrapper(SourcePanel.settings.nodes);

            var transition = SourcePanel.settings.svg.transition().duration(2000);
            transition.selectAll(".mecsim_source_node")
                .attr("transform", function(node) { return "rotate(" + (node.x - 90) + ")translate(" + (node.y + 8) + ",0)" + (node.x < 180 ? "" : "rotate(180)"); })
                .attr("dx", function(node){ return node.x < 180 ? 0 : "-" + String(node.key).length*6 + "px";});

            transition.selectAll(".mecsim_source_link")
                .each(function(link) { link.source = link[0]; link.target = link[link.length - 1]; })
                .attr("d", SourcePanel.settings.line);

        },

        //comparator for sorting the cluster by distance
        sortByDistanceComp: function(a, b) {

            var distance1 = SourcePanel.getDistance(a.lat, a.long);
            var distance2 = SourcePanel.getDistance(b.lat, b.long);

            return distance1 > distance2 ? 1 : -1;

        },

        //comparator for sorting the cluster by type
        sortByTypeComp: function(a, b) {
             return a.sourcemode ?  -1 : 1;
        },

        //comparator for sorting the cluster by name
        sortByNameComp: function(a, b) {

            var splitA = a.name.split("#");
            var splitB = b.name.split("#");

            if((splitA.length == 1) || (splitB.length == 1))
                return a.name < b.name ? -1 : 1;

            var numberA = parseInt(splitA[splitA.length-1]);
            var numberB = parseInt(splitB[splitB.length-1]);

            return numberA < numberB ? -1 : 1;

        },

        //method which should be triggered if the mouse hover-in to a node
        mouseovered: function(node) {

            if(SourcePanel.settings.selected === false){
                //if no node is selected make some hover-effekt (for every link which is connected to the hoverd-node)
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-thick", function(link){ return link.source === node || link.target === node; });
            }else{
                //if a node is selected all links should be hidden (except the links which are connected to the hovered or selected node)
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-hidden", function(link){ return (link.source !== node && link.target !== node) && (link.source !== SourcePanel.settings.selected && link.target !== SourcePanel.settings.selected);});
            }

        },

        //method which should be triggered if the mouse hover-out from a node
        mouseouted: function(node) {

            if(SourcePanel.settings.selected === false){
                SourcePanel.settings.linkcontainer.classed("mecsim_source_link-thick", false);
            }else{
                //if a node is selected an the mouse hover out it is a good idea to save the hover-effekt
            }

        },

        //method to calculate the distance to (lat: 0/ long: 0)
        getDistance: function(lat1, lon1, lat2, lon2) {

            if(lat1 === undefined || lon1 === undefined){
                lat1 = defaultLat;
                lon1 = defaultLon;
                console.log("Warning no Arguments are passed in!");
            }

            if(!lat2 || !lon2){
                lat2 = SourcePanel.settings.defaultLat;
                lon2 = SourcePanel.settings.defaultLon;
            }

            var R = 6371000;
            var lon1 = lon1 * (Math.PI / 180);
            var lon2 = lon2 * (Math.PI / 180);
            var lonDiff = (lon2-lon1) * (Math.PI / 180);
            var latDiff = (lat2-lat1) * (Math.PI / 180);

            var a = Math.sin(latDiff/2) * Math.sin(latDiff/2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                    Math.sin(lonDiff/2) * Math.sin(lonDiff/2);

            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            var d = R * c;

            return d;

        },

        //method to generate some random dummy data for testing
        generateData: function() {

            result = [];
            names = [];

            //generate some nodes
            for(var h=0; h<75; h++)
                names.push("Knoten#"+h);

            for(var i=0; i<names.length; i++){

                var sourcemode = Math.random() > 0.5 ? true: false;
                randomTargets = [];

                //add some targets to every node by random
                for(var j=0; j<names.length; j++){
                    if(Math.random() < 0.015)
                        randomTargets.push(names[j]);
                }

                //push the new node object to the result array
                result.push({ name: names[i].trim(),
                        sourcemode: sourcemode,
                        lat: Math.random() * 180,
                        long: Math.random() * 90,
                        targets: randomTargets
                });
            }

            return result;

        },

    };
