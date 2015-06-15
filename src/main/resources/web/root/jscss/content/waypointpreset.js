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


"use strict";

//@todo fix HTML5 data attributes


/**
 * ctor to create the configuration view
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
 * @param po_options configuration options
**/
function WaypointPreset( pc_id, pc_name, pa_panel, po_options )
{
    Wizard.call(this, pc_id, pc_name, pa_panel, po_options );
}

/** inheritance call **/
WaypointPreset.prototype = Object.create(Wizard.prototype);


/**
 * @Overwrite
**/
WaypointPreset.prototype.getContent = function()
{

    // list with IDs
    var lo_elements = {
        selects  : [],
        text     : [],
        slider   : []
    };

    return Wizard.prototype.getContent.call( this,

        '<h3 id="' + this.generateSubID("factory") + '">blub</h3>' +
        '<section>' +
        '<p>' + Layout.select(  { id: this.generateSubID("type"),      list: lo_elements.select }) + '</p>' +
        '<p>' + Layout.input(   { id: this.generateSubID("radius"),    list: lo_elements.text })   + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("factory"),   list: lo_elements.select }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("agent"),     list: lo_elements.select }) + '</p>' +
        '</section >'

    );
}

