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

 //modul to create GraphEditor
 var GraphEditor = ( function (px_module) {

    px_module = function(element, data, options){

        data = data || {};
        options = options || {};
        if(element === undefined || element === null){
            console.log("DOM Element is missing");
            return;
        }

        var self = this;
        this._div               = element;
        this._data              = data                     || {};
        this._width             = options.width            || 500;
        this._height            = options.height           || 500;
        this._background        = options.background       || "white";
        this._linkdistance      = options.linkdistance     || 150;
        this._charge            = options.charge           || -500;
        this._colors            = d3.scale.category10();

        var svg = d3.select(element)
            .append('svg')
            .attr('width', this._width)
            .attr('height', this._height);

        var force = d3.layout.force()
            .nodes(this._data.nodes)
            .links(this._data.links)
            .size([this._width, this._height])
            .linkDistance(this._linkdistance)
            .charge(this._charde)
            .on('tick', self.tick);

        svg.append('svg:defs').append('svg:marker')
                .attr('id', 'end-arrow')
                .attr('viewBox', '0 -5 10 10')
                .attr('refX', 6)
                .attr('markerWidth', 3)
                .attr('markerHeight', 3)
                .attr('orient', 'auto')
            .append('svg:path')
                .attr('d', 'M0,-5L10,0L0,5')
                .attr('fill', '#000');

        svg.append('svg:defs').append('svg:marker')
                .attr('id', 'start-arrow')
                .attr('viewBox', '0 -5 10 10')
                .attr('refX', 4)
                .attr('markerWidth', 3)
                .attr('markerHeight', 3)
                .attr('orient', 'auto')
            .append('svg:path')
                .attr('d', 'M10,-5L0,0L10,5')
                .attr('fill', '#000');

        var drag_line = svg.append('svg:path')
          .attr('class', 'link dragline hidden')
          .attr('d', 'M0,0L0,0');

        var path = svg.append('svg:g').selectAll('path'),
            circle = svg.append('svg:g').selectAll('g');

        var selected_node = null,
            selected_link = null,
            mousedown_link = null,
            mousedown_node = null,
            mouseup_node = null;
    };

    px_module.create = function(element, data, options){
        return new GraphEditor(element, data, options);
    };

    px_module.tick = function() {
        path.attr('d', function(d) {
            var deltaX = d.target.x - d.source.x,
                deltaY = d.target.y - d.source.y,
                dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
                normX = deltaX / dist,
                normY = deltaY / dist,
                sourcePadding = d.left ? 17 : 12,
                targetPadding = d.right ? 17 : 12,
                sourceX = d.source.x + (sourcePadding * normX),
                sourceY = d.source.y + (sourcePadding * normY),
                targetX = d.target.x - (targetPadding * normX),
                targetY = d.target.y - (targetPadding * normY);

            return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
        });

      circle.attr('transform', function(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
      });
  };

    return px_module;

}(GraphEditor || {}));
