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
        this._nodes             = data.nodes               || {};
        this._links             = data.links               || {};
        this._lastNodeId        = data.lastNodeId          || 0;
        this._width             = options.width            || 500;
        this._height            = options.height           || 500;
        this._background        = options.background       || "white";
        this._linkdistance      = options.linkdistance     || 150;
        this._charge            = options.charge           || -500;
        this._colors            = d3.scale.category10();

        this._svg = d3.select(element)
            .append('svg')
            .attr('width', this._width)
            .attr('height', this._height);

        this._force = d3.layout.force()
            .nodes(this._nodes)
            .links(this._links)
            .size([this._width, this._height])
            .linkDistance(this._linkdistance)
            .charge(this._charge)
            .on('tick', this.tick.bind(this));

        this._svg.append('svg:defs').append('svg:marker')
                .attr('id', 'end-arrow')
                .attr('viewBox', '0 -5 10 10')
                .attr('refX', 6)
                .attr('markerWidth', 3)
                .attr('markerHeight', 3)
                .attr('orient', 'auto')
            .append('svg:path')
                .attr('d', 'M0,-5L10,0L0,5')
                .attr('fill', '#000');

        this._svg.append('svg:defs').append('svg:marker')
                .attr('id', 'start-arrow')
                .attr('viewBox', '0 -5 10 10')
                .attr('refX', 4)
                .attr('markerWidth', 3)
                .attr('markerHeight', 3)
                .attr('orient', 'auto')
            .append('svg:path')
                .attr('d', 'M10,-5L0,0L10,5')
                .attr('fill', '#000');

        this._drag_line = this._svg.append('svg:path')
            .attr('class', 'link dragline hidden')
            .attr('d', 'M0,0L0,0');

        this._path = this._svg.append('svg:g').selectAll('path');
        this._circle = this._svg.append('svg:g').selectAll('g');

        this._selected_node = null;
        this._selected_link = null;
        this._mousedown_link = null;
        this._mousedown_node = null;
        this._mouseup_node = null;
        this._lastKeyDown = -1;

        this._svg.on('mousedown', this.mousedown)
            .on('mousemove', this.mousemove)
            .on('mouseup', this.mouseup);

        d3.select(window)
            .on('keydown', this.keydown)
            .on('keyup', this.keyup);

        this.restart();
    };

    px_module.create = function(element, data, options){
        return new GraphEditor(element, data, options);
    };

    px_module.prototype.resetMouseVars = function() {
        this._mousedown_node = null;
        this._mouseup_node = null;
        this._mousedown_link = null;
    };

    px_module.prototype.tick = function() {

        this._path.attr('d', function(d) {
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

        this._circle.attr('transform', function(d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        });
    };

    px_module.prototype.restart = function() {
        var self = this;
        this._path = this._path.data(this._links);

        this._path.classed('selected', function(d) { return d === this._selected_link; })
            .style('marker-start', function(d) { return d.left ? 'url(#start-arrow)' : ''; })
            .style('marker-end', function(d) { return d.right ? 'url(#end-arrow)' : ''; });


        this._path.enter().append('svg:path')
            .attr('class', 'link')
            .classed('selected', function(d) { return d === this._selected_link; })
            .style('marker-start', function(d) { return d.left ? 'url(#start-arrow)' : ''; })
            .style('marker-end', function(d) { return d.right ? 'url(#end-arrow)' : ''; })
            .on('mousedown', function(d) {
                if(d3.event.ctrlKey) return;

                this._mousedown_link = d;
                if(this._mousedown_link === this._selected_link) this._selected_link = null;
                else this._selected_link = this._mousedown_link;
                this._selected_node = null;

                this.restart();
            });

        this._path.exit().remove();

        this._circle = this._circle.data(this._nodes, function(d) { return d.id; });

        this._circle.selectAll('circle')
            .style('fill', function(d) { return (d === this._selected_node) ? d3.rgb(self._colors(d.id)).brighter().toString() : self._colors(d.id); })
            .classed('reflexive', function(d) { return d.reflexive; });

        var g = this._circle.enter().append('svg:g');

        g.append('svg:circle')
            .attr('class', 'node')
            .attr('r', 12)
            .style('fill', function(d) { return (d === this._selected_node) ? d3.rgb(self._colors(d.id)).brighter().toString() : self._colors(d.id); })
            .style('stroke', function(d) { return d3.rgb(self._colors(d.id)).darker().toString(); })
            .classed('reflexive', function(d) { return d.reflexive; })
            .on('mouseover', function(d) {
                if(!this._mousedown_node || d === this._mousedown_node)
                    return;

                d3.select(this).attr('transform', 'scale(1.1)');
            })
            .on('mouseout', function(d) {
                if(!this._mousedown_node || d === this._mousedown_node)
                    return;

                d3.select(this).attr('transform', '');
            })
            .on('mousedown', function(d) {
                if(d3.event.ctrlKey) return;

                this._mousedown_node = d;
                if(this._mousedown_node === this._selected_node) this._selected_node = null;
                else this._selected_node = this._mousedown_node;
                this._selected_link = null;

                this._drag_line
                    .style('marker-end', 'url(#end-arrow)')
                    .classed('hidden', false)
                    .attr('d', 'M' + this._mousedown_node.x + ',' + this._mousedown_node.y + 'L' + this._mousedown_node.x + ',' + this._mousedown_node.y);

                this._restart();
            })
            .on('mouseup', function(d) {
                if(!this._mousedown_node)
                    return;

                this._drag_line
                    .classed('hidden', true)
                    .style('marker-end', '');

                this._mouseup_node = d;
                if(this._mouseup_node === this._mousedown_node) {
                    this._resetMouseVars();
                    return;
                }

                d3.select(this).attr('transform', '');

                var source, target, direction;
                if(this._mousedown_node.id < this._mouseup_node.id) {
                    source = this._mousedown_node;
                    target = this._mouseup_node;
                    direction = 'right';
                } else {
                    source = this._mouseup_node;
                    target = this._mousedown_node;
                    direction = 'left';
                }

                var link;
                link = this._links.filter(function(l) {
                    return (l.source === source && l.target === target);
                })[0];

                if(link) {
                    link[direction] = true;
                } else {
                    link = {source: source, target: target, left: false, right: false};
                    link[direction] = true;
                    this._links.push(link);
                }

                this._selected_link = link;
                this._selected_node = null;

                this._restart();
        });

        g.append('svg:text')
            .attr('x', 0)
            .attr('y', 4)
            .attr('class', 'id')
            .text(function(d) { return d.id; });

        this._circle.exit().remove();

        this._force.start();
    };

    px_module.prototype.mousedown = function() {
        this._svg.classed('active', true);

        if(d3.event.ctrlKey || this._mousedown_node || this._mousedown_link)
            return;

        var point = d3.mouse(this),
            node = {id: ++this._lastNodeId, reflexive: false};
            node.x = point[0];
            node.y = point[1];
            nodes.push(node);

        this._restart();
    };

    px_module.prototype.mousemove = function() {
        if(!this._mousedown_node)
            return;

        this._drag_line.attr('d', 'M' + this._mousedown_node.x + ',' + this._mousedown_node.y + 'L' + d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);
        this._restart();
    };

    px_module.prototype.mouseup = function() {
        if(this._mousedown_node) {
            this._drag_line
                .classed('hidden', true)
                .style('marker-end', '');
        }

        this._svg.classed('active', false);
        this._resetMouseVars();
    };

    px_module.prototype.spliceLinksForNode = function(node) {
        var toSplice = this._links.filter(function(l) {
            return (l.source === node || l.target === node);
        });

        toSplice.map(function(l) {
            this._links.splice(this._links.indexOf(l), 1);
        });
    };

    px_module.prototype.keydown = function() {
        d3.event.preventDefault();

        if(this._lastKeyDown !== -1)
            return;
        this._lastKeyDown = d3.event.keyCode;

        if(d3.event.keyCode === 17) {
            this._circle.call(this._force.drag);
            this._svg.classed('ctrl', true);
        }

        if(!this._selected_node && !this._selected_link)
            return;

        switch(d3.event.keyCode) {
            case 8:
            case 46:
                if(this._selected_node) {
                    this._nodes.splice(this._nodes.indexOf(this._selected_node), 1);
                    this.spliceLinksForNode(this._selected_node);
                } else if(this._selected_link) {
                    this._links.splice(this._links.indexOf(this._selected_link), 1);
                }
                this._selected_link = null;
                this._selected_node = null;
                this.restart();
                break;

            case 66:
                if(this._selected_link) {
                    this._selected_link.left = true;
                    this._selected_link.right = true;
                }
                this.restart();
                break;

            case 76:
                if(this._selected_link) {
                    this._selected_link.left = true;
                    this._selected_link.right = false;
                }
                this.restart();
                break;

            case 82:
                if(this._selected_node) {
                    this._selected_node.reflexive = !this._selected_node.reflexive;
                } else if(this._selected_link) {
                    this._selected_link.left = false;
                    this._selected_link.right = true;
                }
                this.restart();
                break;
        }
    };

    px_module.prototype.keyup = function() {
        this._lastKeyDown = -1;

        if(d3.event.keyCode === 17) {
            this._circle
                .on('mousedown.drag', null)
                .on('touchstart.drag', null);
                this._svg.classed('ctrl', false);
        }
    };

    return px_module;

}(GraphEditor || {}));
