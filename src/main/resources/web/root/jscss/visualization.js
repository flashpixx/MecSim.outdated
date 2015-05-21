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


    /**
     * function for building tree structure
     * @note empty key == root node
     * @param po_data input data
     * @return tree JSON object
    **/
    function buildtree(po_data){
        var lo_node = {}

        function traverse( pc_key, pc_object )
        {

        }


        $.each(po_data, function(lc_key, lx_value) {


        });

        return lo_data;
    }





    /**
     * function to define the link structure
     * @param po_nodes node tree
     * @return link object structure
    **/
    function linknode( po_nodes )
    {
        var la_links = [];

        $.each(po_nodes, function(pc_key, po_object) {
            if (po_object.children)
                po_object.children.forEach( function(po_child) {
                    la_links.push({
                        source : pc_key,
                        target : po_child.key
                    });
                });
            });

      return la_links;
    }


    /**
     * creates a edge bundle with D3.JS
     * @see http://mbostock.github.io/d3/talk/20111116/bundle.html
     * @param pc_element element in which the graph is added
     * @param po_options options of the graph (option definition below)
     **/
    px_modul.HierarchicalEdgeBundling = function(pc_element, po_options)
    {
        var lo_options   = po_options || {};

        // --- option definition ---
        /** data object with tree and link structure **/
        lo_options.data         = lo_options.data         || {};
        /** image size **/
        lo_options.size         = lo_options.size         || 1000;
        /** size of the cluster (quarter of the size) **/
        lo_options.clustersize  = lo_options.clustersize  || lo_options.size/4;
        /** tension value **/
        lo_options.tension      = lo_options.tension      || 0.75;
        /** DOM ID of the graphic **/
        lo_options.id           = lo_options.id           || null;
        /** DOM class of the arc **/
        lo_options.pathclass    = lo_options.pathclass    || (lo_options.id == null ? "" : lo_options.id+"-") + "arc";
        /** DOM class of the node **/
        lo_options.nodeclass    = lo_options.nodeclass    || (lo_options.id == null ? "" : lo_options.id+"-") + "node";

        /** function to create the connection structure between nodes **/
        lo_options.link         = lo_options.link         || null;
        /** data sorting function **/
        lo_options.sort         = lo_options.sort         || null;
        /** radius function **/
        lo_options.radius       = lo_options.radius       || function( po_data ) { return po_data.y; };
        /** angle function **/
        lo_options.angle        = lo_options.angle        || function( po_data ) { return po_data.x / 180 * Math.PI; };

        /** function to create DOM ID of the node **/
        lo_options.nodeid       = lo_options.nodeid       || function( po_node ) { return (lo_options.id == null ? "" : lo_options.id+"-") + "node-" + po_node.key; };
        /** function to create DOM class of the links **/
        lo_options.linkclass    = lo_options.linkclass    || function( po_link ) { var lc_prefix = (lo_options.id == null ? "" : lo_options.id+"-"); return [lc_prefix+"link", lc_prefix+"source-"+po_link.source.key, lc_prefix+"target-"+po_link.target.key].join(" "); };
        /** function for filtering the nodes **/
        lo_options.nodefilter   = lo_options.nodefilter   || function( po_node ) { return !po_node.children; };
        /** function to return the node text **/
        lo_options.nodetext     = lo_options.nodetext     || function( po_node ) { return po_node.key; };
        // -------------------------



        // create cluster initialization call (size = 360 degree, cluster size)
        var lo_cluster = d3.layout.cluster()
            .size( [360, lo_options.clustersize] )
            .sort( lo_options.sort );

        var lo_bundle = d3.layout.bundle();

        var lo_line = d3.svg.line.radial()
            .interpolate( "bundle" )
            .tension(     lo_options.tension )
            .radius(      lo_options.radius )
            .angle(       lo_options.angle );

        // appends the visualization in the element in an inner div
        var div = d3.select( pc_element )
            .insert( "div" )
            .style(  "width",  lo_options.size + "px")
            .style(  "height", lo_options.size + "px")
        if (lo_options.id  != null)
            div.attr("id", lo_options.id);

        // adds an SVG image in the div
        var lo_svg = div.append("svg:svg")
            .attr(   "width",     lo_options.size )
            .attr(   "height",    lo_options.size )
            .append( "svg:g" )
            .attr(   "transform", "translate(" +  lo_options.size/2  + "," +  lo_options.size/2  + ")" );

        lo_svg.append("svg:path")
            .attr("class", lo_options.pathclass)
            .attr("d",     d3.svg.arc().outerRadius( lo_options.size/2 ).innerRadius( 0 ).startAngle( 0 ).endAngle( 2 * Math.PI ) );



        // creates the visualization of the data
        var lo_tree = buildtree(lo_options.data) , la_nodes = lo_cluster.nodes(lo_tree), la_links = linknode(lo_tree);
        //console.log(la_nodes);
        //console.log(la_links);

        // link visualization
        var path = lo_svg.selectAll( "path.link" )
            .data( la_links )
            .enter().append( "svg:path" )
            .attr( "class", lo_options.linkclass )
            .attr( "d",     function(po_link, pn_index) { return lo_line(lo_bundle(la_links)[pn_index]); });

        // node visualisation
        lo_svg.selectAll( "g.node" )
            .data( la_nodes.filter( lo_options.nodefilter) )
            .enter().append( "svg:g" )
            .attr( "class",       lo_options.nodeclass )
            .attr( "id",          lo_options.nodeid )
            .attr( "transform",   function(po_link) { return "rotate(" + (po_link.x - 90) + ") translate(" + po_link.y + ")"; })
            .append( "svg:text" )
            .attr( "dx",          function(po_link) { return po_link.x < 180 ? 8 : -8; } )
            .attr( "dy", "0.3em" )
            .attr( "text-anchor", function(po_link) { return po_link.x < 180 ? "start" : "end"; } )
            .attr( "transform",   function(po_link) { return po_link.x < 180 ? null : "rotate(180)"; } )
            .text( lo_options.nodetext );
    }



    return px_modul;

}(Visualization || {}));
