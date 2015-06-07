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

    px_module = function(element, options){

        if(element === undefined || element === null){
            console.log("DOM Element is missing");
            return;
        }

        var self = this;
        this._div               = element;
        this._width             = options.width            || 690;
        this._height            = options.height           || 500;
        this._background        = options.background       || "white";
        this._colors            = d3.scale.category10();

        this._div
          .append('svg')
          .attr('width', this._width)
          .attr('height', this._height)
          .append("test </p> test");

    };

    px_module.create = function(element, options){
        return new GraphEditor(element, options);
    };

    return px_module;

}(GraphEditor || {}));
