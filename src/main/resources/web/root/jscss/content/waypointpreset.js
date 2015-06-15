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
        slider   : [],
        spinner  : []
    };

    return Wizard.prototype.getContent.call( this,

        // first step - general data
        '<h3 id="' + this.generateSubID("factory") + '" />' +
        '<section>' +
        '<p>' + Layout.select(  { id: this.generateSubID("type"),      list: lo_elements.select }) + '</p>' +
        '<p>' + Layout.input(   { id: this.generateSubID("radius"),    list: lo_elements.text })   + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("factory"),   list: lo_elements.select }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("agent"),     list: lo_elements.select }) + '</p>' +
        '</section >' +


        // second step - generator settings
        '<h3 id="' + this.generateSubID("generator") + '" />' +
        '<section>' +
        '<p>' + Layout.select( { id: this.generateSubID("distribution"),              list: lo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("distribution_bound_left"),   list: lo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("distribution_bound_right"),  list: lo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("carcount"),                  list: lo_elements.spinner }) + '</p>' +
        '</section >' +


        // third step - car settings
        '<h3 id="' + this.generateSubID("car") + '" />' +
        '<section id="' + this.generateSubID("carsettings") + '">' +

        '<h4 id="' + this.generateSubID("speed") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("speed_distribution"),              list: lo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speed_distribution_bound_left"),   list: lo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speed_distribution_bound_right"),  list: lo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("maxspeed") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("maxspeed_distribution"),              list: lo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeed_distribution_bound_left"),   list: lo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeed_distribution_bound_right"),  list: lo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("acceleration") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("acceleration_distribution"),              list: lo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("acceleration_distribution_bound_left"),   list: lo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("acceleration_distribution_bound_right"),  list: lo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("deceleration") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("deceleration_distribution"),              list: lo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("deceleration_distribution_bound_left"),   list: lo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("deceleration_distribution_bound_right"),  list: lo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("linger") + '" />' +
        '<div>' +
        '<p>' + Layout.input(  { id: this.generateSubID("linger_value"),   list: lo_elements.text })    + '</p>' +
        '</div>' +

        '</section >' +


        // forth step - customizing settings
        '<h3 id="' + this.generateSubID("custom") + '" />' +
        '<section>' +
        '<p>' + Layout.input(  { id: this.generateSubID("name"),    list: lo_elements.text }) + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("color") })                           + '</p>' +
        '<p id="' + this.generateSubID("error") + '"></p>'                                    +
        '</section >'
    );
}

