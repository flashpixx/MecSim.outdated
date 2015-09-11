/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

    this.mc_idseperator = "sep_";
}

/** inheritance call **/
Configuration.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Configuration.prototype.getContent = function()
{
    return '<button id = "' + this.getID() + '" ></button >' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Configuration.prototype.getGlobalContent = function()
{
    return Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("text"),
        title     : this.generateSubID("dialogtitle")
    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
Configuration.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    MecSim.language({ url : "/clanguageenvironment/configurationlabel", target : this });

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
        title     : "OpenStreetMap",
        animation : "fade",
        trigger   : "hover",
        type      : "async",
        url       : "/clanguageenvironment/configurationlabel",
        content   : function( po_data ) { return po_data.mappopup_content; }
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
            ontext         : "On",
            offtext        : "Off",
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
    lo_elements.spinners.forEach( function(pc_item) {
        jQuery( "#"+pc_item ).spinner({
            spin: function( po_event, po_ui ) { self.updateConfiguration( po_event.target.id, po_ui.value ); }
        })
    });

    // text fields (string value)
    lo_elements.texts.forEach( function(pc_item) {
        jQuery( "#"+pc_item ).jqxInput({ height: 25, width: 450 }).on("change", function( po_event ) {
            self.updateConfiguration( po_event.target.id, jQuery(this).val() );
            console.log(po_event.target.id);
        });
    });

    // add graph
    jQuery( this.generateSubID("addgraph", "#") ).button().click( function(po_event){

        // set dialog content
        jQuery(self.generateSubID("text", "#")).empty().append(
            "<p>" +
            Layout.input({

                id: self.generateSubID("graphurl"),
                label : "Graph Download URL (PBF File)"

            }) +
            "</p>"
        );

        // open dialog
        jQuery(self.generateSubID("dialog", "#")).dialog({
            width   : "auto",
            modal   : true,
            buttons : {

                Add : function() {

                    // add element to the graph list and create an unique list
                    self.mo_configuration.simulation.traffic.map.graphs.push( jQuery(self.generateSubID("graphurl", "#")).val() );
                    self.mo_configuration.simulation.traffic.map.graphs = self.mo_configuration.simulation.traffic.map.graphs.unique();

                    // set data into configuration
                    MecSim.configuration().set(
                        self.buildObject( "simulation_traffic_map".split("_"), self.mo_configuration.simulation.traffic.map )
                    );

                    // update UI element
                    var lc_id = self.generateSubIDElementsInit("simulation_traffic_map_current", "#");

                    jQuery(lc_id).empty();
                    self.mo_configuration.simulation.traffic.map.graphs.forEach( function(pc_value) {
                        jQuery(lc_id).append( jQuery('<option></option>').attr("value", pc_value).text(pc_value) );
                    } );
                    jQuery(lc_id).val( self.mo_configuration.simulation.traffic.map.current );
                    jQuery(lc_id).selectmenu("refresh", true);

                    // close dialog
                    jQuery(this).dialog("close");
                },

                Cancel : function() { jQuery(this).dialog("close"); }

            }
        });

    });

    // remove graph
    jQuery( this.generateSubID("removegraph", "#") ).button().click( function(po_event){
        if (self.mo_configuration.simulation.traffic.map.graphs.length < 2)
            return;

        // get DOM id and remove item from the UI list and set first element by default
        var lc_id     = self.generateSubIDElementsInit("simulation_traffic_map_current", "#");
        var lc_remove = jQuery(lc_id).val();

        var ln_index = self.mo_configuration.simulation.traffic.map.graphs.indexOf(lc_remove);
        if (ln_index == -1)
            return;

        // remove element of the datastructures check for array type
        self.mo_configuration.simulation.traffic.map.graphs.splice(ln_index, 1);
        self.mo_configuration.simulation.traffic.map.current = self.mo_configuration.simulation.traffic.map.graphs[0];

        // remove element of the UI elements and refresh
        jQuery( ["select", lc_id, " option[value='", lc_remove,"']"].join("") ).remove();
        jQuery(lc_id).val( jQuery(lc_id).val() );
        jQuery(lc_id).selectmenu("refresh", true);

        // set data into configuration
        MecSim.configuration().set(
            self.buildObject( "simulation_traffic_map".split("_"), self.mo_configuration.simulation.traffic.map )
        );

        // delete graph by name
        MecSim.ajax({
            url     : "/cconfiguration/deletegraph",
            data    : { url : lc_remove }
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
    // graph elements can be one, so it is a string and must convert into an array
    if (!Array.isArray(this.mo_configuration.simulation.traffic.map.graphs))
        this.mo_configuration.simulation.traffic.map.graphs = [this.mo_configuration.simulation.traffic.map.graphs];

    // list with IDs
    var lo_elements = {
        selects  : [],
        switches : [],
        spinners : [],
        texts    : []
    };

    // --- add tab structure to the content div and create the jQuery definition -------------------------------------------------------------------------------
    jQuery( MecSim.ui().content("#") ).append(

        '<div id="' + this.generateSubID("tabs") + '">' +

        // tabs
        '<ul>' +
        '<li><a href="' + this.generateSubID("general", "#")    + '"></a></li>' +
        '<li><a href="' + this.generateSubID("ui", "#")         + '"></a></li>' +
        '<li><a href="' + this.generateSubID("simulation", "#") + '"></a></li>' +
        '<li><a href="' + this.generateSubID("database", "#")   + '"></a></li>' +
        '</ul>' +


        // general tab
        '<div id="' + this.generateSubID("general") + '">' +
        '<p>' + Layout.input({ id: this.generateSubIDElementsInit("uuid"),                   label : "",   list: lo_elements.texts,      value: this.mo_configuration.uuid })               + '</p>' +
        '<p>' + Layout.checkbox({ id: this.generateSubIDElementsInit("reset"),               label : "",   list: lo_elements.switches,   value: this.mo_configuration.reset })              + '</p>' +
        '<p>' + Layout.checkbox({ id: this.generateSubIDElementsInit("extractmasexamples"),  label : "",   list: lo_elements.switches,   value: this.mo_configuration.extractmasexamples }) + '</p>' +
        '<p>' + Layout.checkbox({ id: this.generateSubIDElementsInit("deleteonshutdown"),    label : "",   list: lo_elements.switches,   value: this.mo_configuration.deleteonshutdown })   + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubIDElementsInit("language_current"),    label : "",   list: lo_elements.selects,    value: this.mo_configuration.language.current,   options: this.mo_configuration.language.allow.convert( function( pc_item ) { return { id: pc_item }; } ) }) + '</p>' +
        '</div>' +


        // UI tab
        '<div id="' + this.generateSubID("ui") + '">' +
        '<p>' + Layout.input({ id: this.generateSubIDElementsInit("ui_server_host"),                 label : "",  list: lo_elements.texts,      value: this.mo_configuration.ui.server.host })               + '</p>' +
        '<p>' + Layout.input({ id: this.generateSubIDElementsInit("ui_server_port"),                 label : "",  list: lo_elements.spinners,   value: this.mo_configuration.ui.server.port })               + '</p>' +
        '<p>' + Layout.input({ id: this.generateSubIDElementsInit("ui_server_websocketheartbeat"),   label : "",  list: lo_elements.spinners,   value: this.mo_configuration.ui.server.websocketheartbeat }) + '</p>' +
        '<p>' + Layout.input({ id: this.generateSubIDElementsInit("ui_routepainterdelay"),           label : "",  list: lo_elements.spinners,   value: this.mo_configuration.ui.routepainterdelay })         + '</p>' +
        '</div>' +


        // simulation tab
        '<div id="' + this.generateSubID("simulation") + '">' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("simulation_traffic_cellsampling"),      label : "",  list: lo_elements.spinners,  value: this.mo_configuration.simulation.traffic.cellsampling }) + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("simulation_traffic_timesampling"),      label : "",  list: lo_elements.spinners,  value: this.mo_configuration.simulation.traffic.timesampling }) + '</p>' +
        '<p>' + Layout.select({   id: this.generateSubIDElementsInit("simulation_traffic_map_current"),       label : "",  list: lo_elements.selects,   value: this.mo_configuration.simulation.traffic.map.current,  options: this.mo_configuration.simulation.traffic.map.graphs }) +
        ' <button id="' + this.generateSubID("addgraph") + '" /> <button id="' + this.generateSubID("removegraph") + '" /> <a id="' + this.generateSubID("mappopup") + '"></a></p>' +
        '<p>' + Layout.checkbox({ id: this.generateSubIDElementsInit("simulation_traffic_map_reimport"),      label : "",  list: lo_elements.switches,  value: this.mo_configuration.simulation.traffic.map.reimport }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubIDElementsInit("simulation_traffic_routing_algorithm"), label : "",  list: lo_elements.selects,   value: this.mo_configuration.simulation.traffic.routing.algorithm,  options: this.mo_configuration.simulation.traffic.routing.allow.convert( function( pc_item ) { return { id: pc_item }; } ) }) +
        '</div>' +


        // database tab
        '<div id="' + this.generateSubID("database") + '">' +
        '<p>' + Layout.checkbox({ id: this.generateSubIDElementsInit("database_active"),       label : "",  list: lo_elements.switches,  value: this.mo_configuration.database.active })      + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("database_driver"),       label : "",  list: lo_elements.texts,     value: this.mo_configuration.database.driver })      + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("database_url"),          label : "",  list: lo_elements.texts,     value: this.mo_configuration.database.url })         + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("database_username"),     label : "",  list: lo_elements.texts,     value: this.mo_configuration.database.username })    + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("database_password"),     label : "",  list: lo_elements.texts,     value: this.mo_configuration.database.password })    + '</p>' +
        '<p>' + Layout.input({    id: this.generateSubIDElementsInit("database_tableprefix"),  label : "",  list: lo_elements.texts,     value: this.mo_configuration.database.tableprefix }) + '</p>' +
        '</div>' +

        '</div>'

    );

    // webui-popover-content / -title
    MecSim.language({ url : "/clanguageenvironment/configurationlabel",    target : this });
    MecSim.language({ url : "/clanguageenvironment/configurationelements", target : this, idgenerator : this.generateSubIDElements });
    MecSim.language({ url : "/clanguageenvironment/configurationheader",   target : this, idgenerator : this.generateSubIDHeader   });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return lo_elements;
}


/**
 * adapted generator function for DOM IDs of the configuration elements
 *
 * @param pc_id DOM ID name
 * @param pc_prefix prefix
 * @return ID
**/
Configuration.prototype.generateSubIDElements = function(pc_id, pc_prefix)
{
    var lc = this.generateSubIDElementsInit(pc_id, pc_prefix);
    if (["language_current", "simulation_traffic_routing_algorithm", "simulation_traffic_map_current"].indexOf(pc_id) > -1)
        lc += "-button";

    return lc;
}


/**
 * adapted generator function for DOM IDs of the configuration elements
 * that is used on ID initialization
 *
 * @param pc_id DOM ID name
 * @param pc_prefix prefix
 * @return ID
**/
Configuration.prototype.generateSubIDElementsInit = function(pc_id, pc_prefix)
{
    return this.generateSubID( this.mc_idseperator + pc_id, pc_prefix );
}



/**
 * adapted generator function for header items
 *
 * @param pc_id DOM ID name
 * @param pc_prefix prefix
 * @return ID
**/
Configuration.prototype.generateSubIDHeader = function(pc_id, pc_prefix)
{
    return 'a[href="' + this.generateSubID(pc_id, "#") + '"]';
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

