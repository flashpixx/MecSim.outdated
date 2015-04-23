// --- SOURCE PANEL --------------------------------------------------------------------------------------------

var mecsim_source,
    SourcePanel = {

        settings: {
            // cluster config
            diameter: 600,
            radius: this.diameter / 2,
            innerRadius: this.radius - 120,
            defaultLat: 0,
            defaultLon: 0,
            selected: false
            //sortComp: SourcePanel.sortByDistanceComp()

            //cluster data
            /*var model;
            var nodes;
            var links;*/

            //target weightng

        },

        init: function() {
            mecsim_source = this.settings;
            this.bind_ui_actions();
        },

        //method to init the basic widget layout
        initLayout: function() {

            //basic layout (jquerry ui widgets)
            $("#mecsim_source_generatorsettings").resizable({
                animate: true,
                minHeight: 255,
                minWidth: 500
            });

            SourcePanel.settings.targetweighting = $("#mecsim_source_targetweighting")
            SourcePanel.settings.targetweighting.resizable({
                animate: true,
                minHeight: 375,
                minWidth: 500
            });

        },

        //method to init the basic widget layout
        initClusterWidget: function() {

            //listen to the sorting buttons
            $("#mecsim_source_sortByDistance").on("click", function(){sort(sortByDistanceComp)});
            $("#mecsim_source_sortByType").on("click", function(){sort(sortByTypeComp)});
            $("#mecsim_source_sortByName").on("click", function(){sort(sortByNameComp)});

            SourcePanel.settings.cluster = d3.layout.cluster()
                .size([360, SourcePanel.settings.innerRadius])
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

            //readFile("daten2.json", createCluster);
            //createCluster(generateData());

        },

        //method to init the settings widget
        initSettingsWidget: function() {

            //generator settings
            /*var selectDistribution = $("#mecsim_source_selectDistribution");
            selectDistribution.on("change", function(data){
                switch(selectDistribution.val()) {
                    case "Konstant":
                        $("#mecsim_source_input1Label").text("Anzahl der zu produzierenden Autos: ");
                        break;
                    case "Gleichverteilung":
                        $("#mecsim_source_input1Label").text("Untere Grenze: ");
                        $("#mecsim_source_input2Label").text("Obere Grenze: ");
                        break;
                    case "Normalverteilung":
                        $("#mecsim_source_input1Label").text("Erwartungswert: ");
                        $("#mecsim_source_input2Label").text("Standardabweichung: ");
                        break;
                    case "Exponentialverteilung":
                        $("#mecsim_source_input1Label").text("Erwartungswert: ");
                        $("#mecsim_source_input2Label").text("Standardabweichung: ");
                        break;
                    case "Benutzerdefiniert":
                        break;
                    default:
                        console.log("Keine Passende Verteilung gefunden!");
                }
            });*/

        },

        //method to init the target-weighing widget
        initTargetWeighting: function() {

            //target weighting
            /*$('#mecsim_source_weighting').dataTable({
                "paging": true,
                "lengthMenu": [4, 10, 25],
                "ordering": false,
                "info": false
            });

            $("#mecsim_source_weighting_length").on("change", function(data){
                var rows = $("select[name='mecsim_source_weighting_length']").val();
                var result = 215    + (37* parseInt(rows));
                targetweighting.height(result);
            });*/

        },

        //method to parse a json-file and call it with a callback function
        //readFile: function(file, callback) {
            /*d3.json(file, function(error, data){
                callback(data);
            });*/
        //},

        //method which is responsible for creating and updating the cluster
        //createCluster: function(input) {

            /*model = input;
            nodes = cluster.nodes(nodeWrapper(model));
            links = linkWrapper(nodes);

            nodecontainer = nodecontainer.data(nodes);

            //enter section
            nodecontainer.enter()
                .append("text")
                .attr("class", function(node){ return node.sourcemode ? "mecsim_source_node" + " mecsim_source_node-source" : "mecsim_source_node" + " mecsim_source_node-target"})
                .attr("dx", function(node){ return node.x < 180 ? 0 : "-" + String(node.key).length*6 + "px";})
                .attr("dy", ".31em")
                .attr("transform", function(node) { return "rotate(" + (node.x - 90) + ")translate(" + (node.y + 8) + ",0)" + (node.x < 180 ? "" : "rotate(180)"); })
                .text(function(node) { return node.key; })
                .on("mouseover", mouseovered)
                .on("mouseout", mouseouted)
                .on("click", leftclick)
                .on("contextmenu", rightclick);

            //exit section
            nodecontainer.exit().remove();

            linkcontainer = linkcontainer.data(bundle(links));

            //enter section
            linkcontainer.enter()
                .append("path")
                .each(function(link) {link.source = link[0]; link.target = link[link.length - 1]; })
                .attr("class", "mecsim_source_link")
                .attr("d", line);

            //exit section
            linkcontainer.exit().remove();*/

        //},

        //method which produces from a json Input the node-strucutre
        nodeWrapper: function() {

            /*var children = [];

            input.forEach(function(entry){
                children.push(entry);
                entry.key = entry.name;
            });

            return {name:"", children:children};*/

        },

        //method which produces from a node-strucutre the link strucutre
        //linkWrapper: function(nodeArray) {

            /*var map = {};
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

            return targets;*/

        //},

        //method which should be triggered if a node was leftclicked
        //leftclick: function(nodeclicked) {

            //if no node is selected or another node was clicked
            /*if(selected === false || nodeclicked !== selected){

                //save the new selected node and make it black
                selected=nodeclicked;
                nodecontainer.classed("mecsim_source_node-selected", function(node){ return node.name === nodeclicked.name });

                //all nodes which are not connected to the selected should be transparent
                nodecontainer.classed("mecsim_source_node-trans", function(node){
                    if(node === selected)
                        return false;

                    //check if a node is in the list of the selected node
                    var inTargets = true;
                    if(selected.targets.length <= 0) return false;
                    for(var i=0; i< selected.targets.length; i++){
                        if(selected.targets[i] === node.key)
                            inTargets = false;
                    }

                    //TODO check if selected node is in target list of node
                    //console.log(node.targets.length);

                    return inTargets;
                });

                //links should be thick if they are connected to the selected node
                linkcontainer.classed("mecsim_source_link-thick", function(link){ return link.source === selected || link.target === selected });
                //links should be hidden if they are not connected to the selected node
                linkcontainer.classed("mecsim_source_link-hidden", function(link){ return link.source !== selected && link.target !== selected});

            }else{
                //if the selected node was de-selected remove all effekts
                selected=false;
                nodecontainer.classed("mecsim_source_node-selected", false);
                nodecontainer.classed("mecsim_source_node-trans", false);
                linkcontainer.classed("mecsim_source_link-thick", false);
                linkcontainer.classed("mecsim_source_link-hidden", false);
            }*/

        //},

        //method which should be triggered if a node was rightclicked
        //rightclick: function(rightclicked) {

             /** was commented out before!!!
                d3.event.preventDefault();

                if(selected === false)
                    return;
                if(selected.sourcemode === nodeclicked.sourcemode)
                    return;

                //TODO updating failed (only last added link get source and target attribut)

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

        //},

        //method which sets a new comparator and resort the cluster
        //sort: function(comparator) {

            /*cluster.sort(comparator);
            nodes = cluster.nodes(nodeWrapper(model));
            links = linkWrapper(nodes);

            var transition = svg.transition().duration(2000);
            transition.selectAll(".mecsim_source_node")
                .attr("transform", function(node) { return "rotate(" + (node.x - 90) + ")translate(" + (node.y + 8) + ",0)" + (node.x < 180 ? "" : "rotate(180)"); })
                .attr("dx", function(node){ return node.x < 180 ? 0 : "-" + String(node.key).length*6 + "px";});

            transition.selectAll(".mecsim_source_link")
                .each(function(link) { link.source = link[0], link.target = link[link.length - 1]; })
                .attr("d", line);*/

        //},

        //comparator for sorting the cluster by distance
        sortByDistanceComp: function(a, b) {

            var distance1 = getDistance(a.lat, a.long);
            var distance2 = getDistance(b.lat, b.long);

            return distance1 > distance2 ? 1 : -1;

        },

        //comparator for sorting the cluster by type
        //sortByTypeComp: function(a, b) {
             //return a.sourcemode ?  -1 : 1;
        //},

        //comparator for sorting the cluster by name
        //sortByNameComp: function(a, b) {

            /*var splitA = a.name.split("#");
            var splitB = b.name.split("#");

            if((splitA.length == 1) || (splitB.length == 1))
                return a.name < b.name ? -1 : 1;

            var numberA = parseInt(splitA[splitA.length-1]);
            var numberB = parseInt(splitB[splitB.length-1]);

            return numberA < numberB ? -1 : 1;*/

        //},

        //method which should be triggered if the mouse hover-in to a node
        //mouseovered: function(node) {

            /*if(selected === false){
                //if no node is selected make some hover-effekt (for every link which is connected to the hoverd-node)
                linkcontainer.classed("mecsim_source_link-thick", function(link){ return link.source === node || link.target === node; });
            }else{
                //if a node is selected all links should be hidden (except the links which are connected to the hovered or selected node)
                linkcontainer.classed("mecsim_source_link-hidden", function(link){ return (link.source !== node && link.target !== node) && (link.source !== selected && link.target !== selected);});
            }*/

        //},

        //method which should be triggered if the mouse hover-out from a node
        //mouseouted: function(node) {

            /*if(selected === false){
                linkcontainer.classed("mecsim_source_link-thick", false);
            }else{
                //if a node is selected an the mouse hover out it is a good idea to save the hover-effekt
            }*/

        //},

        //method to calculate the distance to (lat: 0/ long: 0)
        //getDistance: function(lat1, lon1, lat2, lon2) {

            /*if(lat1 === undefined || lon1 === undefined){
                lat1 = defaultLat;
                lon1 = defaultLon;
                console.log("Warning no Arguments are passed in!");
            }

            if(!lat2 || !lon2){
                lat2 = defaultLat;
                lon2 = defaultLon;
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

            return d;*/

        //},

        //method to generate some random dummy data for testing
        generateData: function() {

            /*result = [];
            names = [];

            //generate some nodes
            for(var i=0; i<75; i++)
                names.push("Knoten#"+i);

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
                })
            }

            return result;*/

        },

        bind_ui_actions: function() {

            //Load the Source-GUI
            $("#ui-id-5").on("click", function(data){
                UI().getContent().empty();
                UI().getContent().load("template/source.htm", function(){
                    SourcePanel.initLayout();
                    SourcePanel.initClusterWidget();
                    //initSettingsWidget();
                    //initTargetWeighting();
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

    };
