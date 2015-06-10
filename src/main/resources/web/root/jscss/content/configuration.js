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

        MecSim.ajax({

            url     : "/cconfiguration/get",
            success : function( po_data ) {
                self.mo_configuration = po_data;
            }

        }).done(function() {
            self.view();
        });

    });

}


/**
 * creates the view with the configuration data
**/
Configuration.prototype.view = function()
{
    jQuery( MecSim.ui().content("#") ).empty();

    // global /main, ui, simulation, database
    console.log(this.mo_configuration);

    // list with IDs to define jQuery elements
    var lo_elements ={
        selects  : [],
        switches : [],
        spinner  : [],
        text     : []
    }

    // add tab structure to the content div and create the jQuery definition
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
        Layout.checkbox({ id: "config_reset",               label: "Reset Configuration",   list: lo_elements.switches,   value: this.mo_configuration.reset }) + '<br/>' +
        Layout.checkbox({ id: "config_extractmasexample",   label: "Extract agent files",   list: lo_elements.switches,   value: this.mo_configuration.extractmasexamples }) + '<br/>' +
        Layout.select(  { id: "config_language",            label: "Language",              list: lo_elements.selects,
                       value: this.mo_configuration.language.current,
                       options: this.mo_configuration.language.allow.convert( function( pc_item ) { return { id: pc_item }; } )
        }) +
        '</div>' +

        // UI tab
        '<div id="' + this.generateSubID("ui") + '">' +
        Layout.input({ id: "config_host",              label : "Server Host",                   list: lo_elements.text,      value: this.mo_configuration.ui.server.host }) +
        Layout.input({ id: "config_port",              label : "Server Port",                   list: lo_elements.spinner,   value: this.mo_configuration.ui.server.port }) +
        Layout.input({ id: "config_websocket",         label : "Websocket Heartbeat",           list: lo_elements.spinner,   value: this.mo_configuration.ui.server.websocketheartbeat }) +
        Layout.input({ id: "config_routepaintdelay",   label : "Route Paint Delay in sec",      list: lo_elements.spinner,   value: this.mo_configuration.ui.routepainterdelay }) +
        '</div>' +



        '<div id="' + this.generateSubID("simulation") + '">simulation</div>' +
        '<div id="' + this.generateSubID("database") + '">database</div>' +

        '</div>'

    );

    // build jQuery elements
    jQuery( this.generateSubID("tabs", "#") ).tabs();
    lo_elements.switches.forEach( function(pc_item) { jQuery( "#"+pc_item ).bootstrapSwitch({ size : "mini", onText : "Yes", offText : "No" }); });
    lo_elements.selects.forEach( function(pc_item) { jQuery( "#"+pc_item ).selectmenu(); });
    lo_elements.spinner.forEach( function(pc_item) { jQuery( "#"+pc_item ).spinner(); });
    lo_elements.text.forEach( function(pc_item) { jQuery( "#"+pc_item ).jqxInput({ height: 25, width: 200, minLength: 1 }); });
}








