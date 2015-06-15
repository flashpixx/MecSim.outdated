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

    // list with IDs
    this.mo_elements = {
        selects  : [],
        text     : [],
        slider   : [],
        spinner  : []
    };
}

/** inheritance call **/
WaypointPreset.prototype = Object.create(Wizard.prototype);


/**
 * @Overwrite
**/
WaypointPreset.prototype.getContent = function()
{
    return Wizard.prototype.getContent.call( this,

        // first step - general data
        '<h3 id="' + this.generateSubID("factory") + '" />' +
        '<section>' +
        '<p>' + Layout.select(  { id: this.generateSubID("type"),      list: this.mo_elements.select }) + '</p>' +
        '<p>' + Layout.input(   { id: this.generateSubID("radius"),    list: this.mo_elements.text })   + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("factory"),   list: this.mo_elements.select }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("agent"),     list: this.mo_elements.select }) + '</p>' +
        '</section >' +


        // second step - generator settings
        '<h3 id="' + this.generateSubID("generator") + '" />' +
        '<section>' +
        '<p>' + Layout.select( { id: this.generateSubID("distribution"),              list: this.mo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("distribution_bound_left"),   list: this.mo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("distribution_bound_right"),  list: this.mo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("carcount"),                  list: this.mo_elements.spinner }) + '</p>' +
        '</section >' +


        // third step - car settings
        '<h3 id="' + this.generateSubID("car") + '" />' +
        '<section id="' + this.generateSubID("carsettings") + '">' +

        '<h4 id="' + this.generateSubID("speed") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("speed_distribution"),              list: this.mo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speed_distribution_bound_left"),   list: this.mo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speed_distribution_bound_right"),  list: this.mo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("maxspeed") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("maxspeed_distribution"),              list: this.mo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeed_distribution_bound_left"),   list: this.mo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeed_distribution_bound_right"),  list: this.mo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("acceleration") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("acceleration_distribution"),              list: this.mo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("acceleration_distribution_bound_left"),   list: this.mo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("acceleration_distribution_bound_right"),  list: this.mo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("deceleration") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("deceleration_distribution"),              list: this.mo_elements.select })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("deceleration_distribution_bound_left"),   list: this.mo_elements.text })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("deceleration_distribution_bound_right"),  list: this.mo_elements.text })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("linger") + '" />' +
        '<div>' +
        '<p>' + Layout.input(  { id: this.generateSubID("linger_value"),   list: this.mo_elements.text })    + '</p>' +
        '</div>' +

        '</section >' +


        // forth step - customizing settings
        '<h3 id="' + this.generateSubID("custom") + '" />' +
        '<section>' +
        '<p>' + Layout.input(  { id: this.generateSubID("name"),    list: this.mo_elements.text }) + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("color") })                           + '</p>' +
        '<p id="' + this.generateSubID("error") + '"></p>'                                    +
        '</section >'
    );
}


/**
 * @Overwrite
**/
WaypointPreset.prototype.afterDOMAdded = function()
{
    var self = this;
    Wizard.prototype.afterDOMAdded.call(this);

    jQuery( this.generateSubID("carsettings", "#") ).accordion();
    //this.mo_elements.selects.forEach( function( pc_id ) { jQuery( self.generateSubID(pc_id, "#") ).selectmenu(); });
    //this.mo_elements.spinner.forEach( function( pc_id ) { jQuery( self.generateSubID(pc_id, "#") ).spinner(); });
    //this.mo_elements.text.forEach(    function( pc_id ) { jQuery( self.generateSubID(pc_id, "#") ).jqxInput({ height: 25, width: 50 }); });
}

