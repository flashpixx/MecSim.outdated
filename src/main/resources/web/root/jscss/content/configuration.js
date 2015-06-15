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
**/
function Configuration( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    this.mc_idseperator = "id_";
}

/** inheritance call **/
Configuration.prototype = Object.create(Pane.prototype);



/**
 * @Overwrite
**/
Configuration.prototype.getContent = function()
{
    return '<button id = "' + this.generateSubID("configuration") + '" >Configuration</button >' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Configuration.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    // create button and bind action with Ajax call
    jQuery(self.generateSubID("configuration", "#")).button().click( function() {

        MecSim.configuration().get(
            function( po_data ) { self.mo_configuration = po_data; }
        ).done(function() {
            self.buildViewAndBind();
        });

    });

}


/**
 * creates the HTML elements and bind the actions
 * @see https://github.com/sandywalker/webui-popover
**/
Configuration.prototype.buildViewAndBind = function()
{
    var self = this;

    // build elements and get IDs
    jQuery( MecSim.ui().content("#") ).empty();
    var lo_elements = this.buildUIElements();

    // --- build UI tab panel & popup (with click bind) --------------------------------------------------------------------------------------------------------
    jQuery( this.generateSubID("tabs", "#") ).tabs();
    jQuery( this.generateSubID("mappopup", "#") ).webuiPopover({
        title     : "OpenStreetMap Datafile Links",
        animation : "fade",
        trigger   : "hover",
        content   : '<a href="http://wiki.openstreetmap.org/wiki/PBF_Format">PBF download links</a> for import are available at the following locations' +
                    '<ul>'  +
                    '<li><a href="http://download.geofabrik.de/">GeoFabrik</a></li>' +
                    '<li><a href="http://download.bbbike.org/">BB-Bike</a></li>' +
                    '</ul>'
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- bindings --------------------------------------------------------------------------------------------------------------------------------------------
    // the binding depends on the ID name of an element,
    // split ID on "config_" and on the second element split on the underscore
    // the array elements return the path of the configuration object

    // switch binds (boolean values)
    lo_elements.switches.forEach( function(pc_item) {

        jQuery( "#"+pc_item ).bootstrapSwitch({
            size           : "mini",
            onText         : "Yes",
            offText        : "No",
            onSwitchChange : function( po_event, pl_state ) { self.updateConfiguration( po_event.target.id, pl_state ); }
        });

    });

    // selects binds (text / number values)
    lo_elements.selects.forEach( function(pc_item) {
        jQuery( "#"+pc_item ).selectmenu({
            select: function(po_event, po_ui) { self.updateConfiguration( po_event.target.id, po_ui.item.value ); }
        })
    });

    // spinner binds (number value)
    lo_elements.spinner.forEach( function(pc_item) {
        jQuery( "#"+pc_item ).spinner({
            spin: function( po_event, po_ui ) { self.updateConfiguration( po_event.target.id, po_ui.value ); }
        })
    });

    // text fields (string value)
    lo_elements.text.forEach( function(pc_item) {
        jQuery( "#"+pc_item ).jqxInput({ height: 25, width: 450 }).on("change", function( po_event ) {
            self.updateConfiguration( po_event.target.id, jQuery(this).val() );
        });
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}


/**
 * builds the UI elements
 *
 * @return Json object with arrays of ID names
**/
Configuration.prototype.buildUIElements = function()
{
    // list with IDs
    var lo_elements = {
        selects  : [],
        switches : [],
        spinner  : [],
        text     : []
    };

    // --- add tab structure to the content div and create the jQuery definition -------------------------------------------------------------------------------
    jQuery( MecSim.ui().content("#") ).append(

        '<div id="' + this.generateSubID("tabs") + '">' +

        // tabs
        '<ul>' +
        '<li><a href="' + this.generateSubID("general", "#")    + '">General</a></li>' +
        '<li><a href="' + this.generateSubID("ui", "#")         + '">User Interface</a></li>' +
        '<li><a href="' + this.generateSubID("simulation", "#") + '">Simulation</a></li>' +
        '<li><a href="' + this.generateSubID("database", "#")   + '">Database</a></li>' +
        '</ul>' +

        // general tab
        '<div id="' + this.generateSubID("general") + '">' +
        '<p>' + Layout.checkbox({ id: this.generateSubID(this.mc_idseperator+"reset"),               label: "Reset Configuration",   list: lo_elements.switches,   value: this.mo_configuration.reset })              + '</p>' +
        '<p>' + Layout.checkbox({ id: this.generateSubID(this.mc_idseperator+"extractmasexamples"),  label: "Extract agent files",   list: lo_elements.switches,   value: this.mo_configuration.extractmasexamples }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID(this.mc_idseperator+"language_current"),    label: "Language",              list: lo_elements.selects,    value: this.mo_configuration.language.current,   options: this.mo_configuration.language.allow.convert( function( pc_item ) { return { id: pc_item }; } ) }) + '</p>' +
        '</div>' +

        // UI tab
        '<div id="' + this.generateSubID("ui") + '">' +
        '<p>' + Layout.input({ id: this.generateSubID(this.mc_idseperator+"ui_server_host"),                 label : "Server Host",                   list: lo_elements.text,      value: this.mo_configuration.ui.server.host })               + '</p>' +
        '<p>' + Layout.input({ id: this.generateSubID(this.mc_idseperator+"ui_server_port"),                 label : "Server Port",                   list: lo_elements.spinner,   value: this.mo_configuration.ui.server.port })               + '</p>' +
        '<p>' + Layout.input({ id: this.generateSubID(this.mc_idseperator+"ui_server_websocketheartbeat"),   label : "Websocket Heartbeat",           list: lo_elements.spinner,   value: this.mo_configuration.ui.server.websocketheartbeat }) + '</p>' +
        '<p>' + Layout.input({ id: this.generateSubID(this.mc_idseperator+"ui_routepainterdelay"),           label : "Route Paint Delay in sec",      list: lo_elements.spinner,   value: this.mo_configuration.ui.routepainterdelay })         + '</p>' +
        '</div>' +


        // simulation tab
        '<div id="' + this.generateSubID("simulation") + '">' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"simulation_traffic_cellsampling"),      label : "Cell Sampling in meter",        list: lo_elements.spinner,   value: this.mo_configuration.simulation.traffic.cellsampling }) + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"simulation_traffic_timesampling"),      label : "Time Sampling in sec",          list: lo_elements.spinner,   value: this.mo_configuration.simulation.traffic.timesampling }) + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"simulation_traffic_map_name"),          label : "Map Name",                      list: lo_elements.text,      value: this.mo_configuration.simulation.traffic.map.name })     + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"simulation_traffic_map_url"),           label : "Map URL",                       list: lo_elements.text,      value: this.mo_configuration.simulation.traffic.map.url })      + ' <a id="' + this.generateSubID("mappopup") + '">Download Information</a></p>' +
        '<p>' + Layout.checkbox({ id: this.generateSubID(this.mc_idseperator+"simulation_traffic_map_reimport"),      label : "Map Reimport",                  list: lo_elements.switches,  value: this.mo_configuration.simulation.traffic.map.reimport }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID(this.mc_idseperator+"simulation_traffic_routing_algorithm"), label : "Routing Algorithm",             list: lo_elements.selects,   value: this.mo_configuration.simulation.traffic.routing.algorithm,  options: this.mo_configuration.simulation.traffic.routing.allow.convert( function( pc_item ) { return { id: pc_item }; } ) }) +
        '</div>' +


        // database tab
        '<div id="' + this.generateSubID("database") + '">' +
        '<p>' + Layout.checkbox({ id: this.generateSubID(this.mc_idseperator+"database_active"),       label : "Activity",                     list: lo_elements.switches,  value: this.mo_configuration.database.active })      + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"database_driver"),       label : "Driver Name (JDBC Classname)", list: lo_elements.text,      value: this.mo_configuration.database.driver })      + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"database_url"),          label : "URL",                          list: lo_elements.text,      value: this.mo_configuration.database.url })         + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"database_username"),     label : "Username",                     list: lo_elements.text,      value: this.mo_configuration.database.username })    + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"database_password"),     label : "Password",                     list: lo_elements.text,      value: this.mo_configuration.database.password })    + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubID(this.mc_idseperator+"database_tableprefix"),  label : "Tableprefix",                  list: lo_elements.text,      value: this.mo_configuration.database.tableprefix }) + '</p>' +
        '</div>' +

        '</div>'

    );
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return lo_elements;
}

/**
 * builds a Json object from an array with keys
 *
 * @param pa_keys array with key names
 * @param px_value value of the last key
 * @return Json object
**/
Configuration.prototype.buildObject = function( pa_keys, px_value )
{
    if (pa_keys.length == 1)
    {
        var lo = {};
        lo[ pa_keys[0] ] = px_value;
        return lo;
    }

    var lo = {};
    lo[ pa_keys[0] ] = this.buildObject( pa_keys.slice(1), px_value);
    return lo;
}


/**
 * creates the configuration Ajax request
 *
 * @param pc_id element ID
 * @param px_value element value
**/
Configuration.prototype.updateConfiguration = function( pc_id, px_value )
{
    var la_keys  = pc_id.split(this.mc_idseperator);
    if (la_keys.length != 2)
        return;

    MecSim.configuration().set( this.buildObject( la_keys[1].split("_"), px_value ) );
}

