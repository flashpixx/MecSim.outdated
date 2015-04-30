// --- SOURCE PANEL --------------------------------------------------------------------------------------------

var mecsim_source,
    SourcePanel = {

        settings: {

            diameter: 600,
            radius: 600 / 2,
            innerRadius: (600 / 2) - 120,
            defaultLat: 0,
            defaultLon: 0,
            selected: false,
            clusterwidget: $("#mecsim_source_clusterwidget"),
            waypointsettings: $("#mecsim_source_waypointsettings"),
            targetweighting: $("#mecsim_source_targetweighting"),
            toolbox: $("#mecsim_source_toolbox")
            //sortComp: SourcePanel.sortByDistanceComp

        },

        init: function() {
            mecsim_source = this.settings;
            this.bind_ui_actions();
        },

        //method to init the basic widget layout
        initLayout: function() {

            //basic layout (jquerry ui widgets)
            SourcePanel.settings.waypointsettings.resizable({
                animate: true,
                minHeight: 255,
                minWidth: 500
            });

            SourcePanel.settings.targetweighting.resizable({
                animate: true,
                minHeight: 375,
                minWidth: 500
            });

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

        //method to init the settings widget
        initSettingsWidget: function() {

            //TODO implement possiblty for different waypoint tools
            //TODO move jquery selectors
            $("#mecsim_source_colorpicker").spectrum({
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

        //method to init the target-weighing widget
        initTargetWeighting: function() {

            //TODO build the target-weighting-table from a json object out of simulation and remove/move jquery selecors
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

                    //TODO check if selected node is in target list of node

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

            //TODO On Righclick add Waypoint to a Markrov Chain
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

        //load asl files in the select menu
        loadASL: function() {
            /** TODO will be used later on
            $.getJSON( "cagentenvironment/jason/list", function(data){
                SourcePanel.settings.aslSelect.empty();
                for(var i in data.agents){
                    SourcePanel.settings.aslSelect
                        .append( $("<option></option>")
                        .attr("value",data.agents[i])
                        .text(data.agents[i]));
                }
            }).done(function(){
                SourcePanel.settings.aslselect.selectmenu("refresh");
            });
            **/
        },

        bind_ui_actions: function() {
            //Load the Source-GUI
            $("#ui-id-5").on("click", function(data){
                SourcePanel.loadASL();
                UI().getContent().empty();
                UI().getContent().load("template/source.htm", function(){
                    SourcePanel.initLayout();
                    SourcePanel.initClusterWidget();
                    SourcePanel.initSettingsWidget();
                    SourcePanel.initTargetWeighting();
                });
            });
        }

    };
