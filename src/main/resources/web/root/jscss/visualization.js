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


/**
 * base modul to represent all
 * visualization components
 **/
var Visualization = (function (px_modul) {

    var randomData = function() {

        result = [];
        names = [];

        //generate some nodes
        for(var h=0; h<75; h++)
            names.push("Knoten#"+h);

        for(var i=0; i<names.length; i++){

            var sourcemode = Math.random() > 0.5 ? true: false;
            randomTargets = [];

            //add some targets to every node by random
            for(var j=0; j<names.length; j++)
                if(Math.random() < 0.015)
                    randomTargets.push(names[j]);


            //push the new node object to the result array
            result.push({
                key: names[i].trim(),
                lat: Math.random() * 180,
                long: Math.random() * 90,
                children: []
            });

        }

        return result;
    }


    /**
     * creates a edge bundle with D3.JS
     * @see http://mbostock.github.io/d3/talk/20111116/bundle.html
     * @param pc_element element in which the graph is added
     * @param po_options options of the graph
     **/
    px_modul.HierarchicalEdgeBundling = function(pc_element, po_options)
    {
        var lo_options   = po_options || {};

        lo_options.datanodes    = lo_options.datanodes    || randomData();
        lo_options.size         = lo_options.size         || 800;
        lo_options.id           = lo_options.id           || null;
        lo_options.tension      = lo_options.tension      || 0.85;
        lo_options.radius       = lo_options.radius       || function( po_data ) { return po_data.y; };
        lo_options.angle        = lo_options.angle        || function( po_data ) { return po_data.x / 180 * Math.PI; };
        lo_options.nodeid       = lo_options.nodeid       || function( po_node ) { return "node-" + po_node.key; };
        lo_options.nodefilter   = lo_options.nodefilter   || function( po_node ) { return !po_node.children; };
        lo_options.nodetext     = lo_options.nodetext     || function( po_node ) { return po_node.key; };



        // graph bundle
        var lo_bundle = d3.layout.bundle();

        // add div for bundle
        var lo_main = d3.select(pc_element).insert("div")
            .style("width", lo_options.width+"px")
            .style("height", lo_options.size+"px")
            .style("-webkit-backface-visibility", "hidden");
        if (lo_options.id !== null)
            lo_main.attr("id", lo_options.id);

        // add svg graphic
        var lo_svg = lo_main.append("svg:svg")
            .attr("width", lo_options.size+"px")
            .attr("height", lo_options.size+"px")
            .append("svg:g")
            .attr("transform", "translate(" + lo_options.size/2 + "," + lo_options.size/2 + ")");

        lo_svg.append("svg:path")
            .attr("d", d3.svg.arc().outerRadius(lo_options.size/2 - 120).innerRadius(0).startAngle(0).endAngle(2 * Math.PI))
            //.on("mousedown", mousedown);
            //.attr("class", "arc")
        lo_svg.selectAll("g.node")
            .data( lo_options.datanodes.filter(lo_options.nodefilter) )
            .enter().append("svg:g")
            .attr("class", "node")
            .attr("id", lo_options.nodeid)
            .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })
            .append("svg:text")
            .attr("dx", function(d) { return d.x < 180 ? 8 : -8; })
            .attr("dy", ".31em")
            .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
            .attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
            .text(lo_options.nodetext);
            //.on("mouseover", mouseover)
            //.on("mouseout", mouseout);

        // add line structure
        var lo_line = d3.svg.line.radial()
            .interpolate("bundle")
            .tension(lo_options.tension)
            .radius(lo_options.radius)
            .angle(lo_options.angle);


    }




    return px_modul;

}(Visualization || {}));
