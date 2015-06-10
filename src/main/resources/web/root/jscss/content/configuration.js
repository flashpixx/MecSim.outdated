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
Configuration.prototype.getGlobalContent = function()
{
    return '<div id = "' + this.generateSubID("dialog") + '" title = "Object Inspector" >' +
           '<div id = "' + this.generateSubID("table")  + '" ></div>' +
           '</div>' +
           Pane.prototype.getGlobalContent.call(this);
}


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
    //console.log(this.mo_configuration);

    // list with IDs to define jQuery elements
    var la_selects  = [];
    var la_switches = [];

    // add tab structure to the content div and create the jQuery definition
    jQuery( MecSim.ui().content("#") ).append(

        '<div id="' + this.generateSubID("tabs") + '">' +

        '<ul>' +
        '<li><a href="' + this.generateSubID("general", "#")    + '">General</a></li>' +
        '<li><a href="' + this.generateSubID("ui", "#")         + '">User Interface</a></li>' +
        '<li><a href="' + this.generateSubID("simulation", "#") + '">Simulation</a></li>' +
        '<li><a href="' + this.generateSubID("database", "#")   + '">Database</a></li>' +
        '</ul>' +

        '<div id="' + this.generateSubID("general") + '">' +
        this.getCheckbox({ id: "config_reset",               label: "Reset Configuration",   list: la_switches,   value: this.mo_configuration.reset }) + '<br/>' +
        this.getCheckbox({ id: "config_extractmasexample",   label: "Extract agent files",   list: la_switches,   value: this.mo_configuration.extractmasexamples }) + '<br/>' +
        this.getSelect(  { id: "config_language",            label: "Language",              list: la_selects,
                       value: this.mo_configuration.language.current,
                       options: this.mo_configuration.language.allow.convert( function( pc_item ) { return { id: pc_item }; } )
        }) +
        '</div>' +

        '<div id="' + this.generateSubID("ui") + '">ui</div>' +
        '<div id="' + this.generateSubID("simulation") + '">simulation</div>' +
        '<div id="' + this.generateSubID("database") + '">database</div>' +

        '</div>'

    );

    // build jQuery elements
    jQuery( this.generateSubID("tabs", "#") ).tabs();
    la_switches.forEach( function(pc_item) { jQuery( "#"+pc_item ).bootstrapSwitch({ size : "mini" }); });
    la_selects.forEach( function(pc_item) { jQuery( "#"+pc_item ).selectmenu(); });
}


/**
 * creates a checkbox with label
 *
 * @param po_object Json object with id & label (and optional list to get all IDs)
 * @return HTML string
**/
Configuration.prototype.getCheckbox = function( po_object )
{
    var lc_id = this.generateSubID(po_object.id)
    if (Array.isArray(po_object.list))
        po_object.list.push(lc_id);

    return '<label for = "' + lc_id + '" >' + po_object.label + '</label > <input id="' + lc_id + '" type="checkbox" '+ (po_object && po_object.value ? "checked" : "") +' />';
}


/**
 * creates a select menu with label
 *
 * @param po_object Json object with id & label (and optional list to get all IDs and options with Json object (ID & label) for the option values)
 * @return HTML string
**/
Configuration.prototype.getSelect = function( po_object )
{
    var lc_id = this.generateSubID(po_object.id)
    if (Array.isArray(po_object.list))
        po_object.list.push(lc_id);

    var lc = '<label for = "' + lc_id + '" >' + po_object.label + '</label > <select id = "' + lc_id + '">';

    if (Array.isArray(po_object.options))
        po_object.options.forEach( function(po_item) { lc += '<option id="' + po_item.id + '" ' + (po_object.value && (po_item.id == po_object.value) ? "selected" : "") + '>' + (po_item.label ? po_item.label : po_item.id) + '</option>'; } );

    lc += "</select>";

    return lc;
}