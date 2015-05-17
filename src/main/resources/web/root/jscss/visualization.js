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
     * creates a edge bundle with D3.JS
     * @see http://mbostock.github.io/d3/talk/20111116/bundle.html
     * @param pc_element element in which the graph is added
     * @param po_options options of the graph
     **/
    px_modul.HierarchicalEdgeBundle = function(pc_element, po_options)
    {
        var lo_options   = po_options || {};

        lo_options.size    = lo_options.size    || 800;
        lo_options.id      = lo_options.id      || null;
        lo_options.tension = lo_options.tension || 0.85;
        lo_options.radius  = lo_options.radius  || function( po_data ) { return po_data.y; };
        lo_options.angle   = lo_options.angle   || function( po_data ) { return po_data.x / 180 * Math.PI; }

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

        // add line structure
        var lo_line = d3.svg.line.radial()
            .interpolate("bundle")
            .tension(lo_options.tension)
            .radius(lo_options.radius)
            .angle(lo_options.angle);

    }




    return px_modul;

}(Visualization || {}));
