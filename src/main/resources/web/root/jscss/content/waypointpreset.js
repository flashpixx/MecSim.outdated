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
 * ctor to create the preset wizard
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
        selects   : [],
        texts     : [],
        sliders   : [],
        spinners  : []
    };
}

/** inheritance call **/
WaypointPreset.prototype = Object.create(Wizard.prototype);


/**
 * @Overwrite
**/
WaypointPreset.prototype.getGlobalContent = function()
{
    return Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("text"),
        title     : "Information"
    }) +
    Wizard.prototype.getGlobalContent.call(this);
}

/**
 * @Overwrite
**/
WaypointPreset.prototype.getContent = function()
{
    return Wizard.prototype.getContent.call( this,

        // first step - general data
        '<h3 id="' + this.generateSubID("factoryhead") + '" />' +
        '<section>' +
        '<p>' + Layout.select(  { id: this.generateSubID("type"),     label: " ",   list: this.mo_elements.selects }) + '</p>' +
        '<p>' + Layout.input(   { id: this.generateSubID("radius"),   label: " ",   list: this.mo_elements.texts })   + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("factory"),  label: " ",   list: this.mo_elements.selects }) + '</p>' +
        '<p>' + Layout.select(  { id: this.generateSubID("agent"),    label: " ",   list: this.mo_elements.selects }) + '</p>' +
        '</section >' +


        // second step - generator settings
        '<h3 id="' + this.generateSubID("basegeneratorhead") + '" />' +
        '<section>' +
        '<p>' + Layout.select( { id: this.generateSubID("basedistribution"),            class: this.generateSubID("distribution"),      label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("basedistributionleft"),                                                        label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("basedistributionright"),                                                       label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("carcount"),                                                                    label: " ",   list: this.mo_elements.spinners }) + '</p>' +
        '</section >' +


        // third step - car settings
        '<h3 id="' + this.generateSubID("carhead") + '" />' +
        '<section><div id="' + this.generateSubID("carsettings") + '">' +

        '<h4 id="' + this.generateSubID("speedhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("speeddistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speeddistributionleft"),                                                     label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speeddistributionright"),                                                    label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("maxspeedhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("maxspeeddistribution"),    class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeeddistributionleft"),                                             label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeeddistributionright"),                                            label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("accelerationhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("accelerationdistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("accelerationdistributionleft"),                                                     label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("accelerationdistributionright"),                                                    label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("decelerationhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("decelerationdistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("decelerationdistributionleft"),                                                     label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("decelerationdistributionright"),                                                    label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("lingerhead") + '" />' +
        '<div>' +
        '<p>' + Layout.input(  { id: this.generateSubID("linger"),   label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '</div></section >' +


        // forth step - customizing settings
        '<h3 id="' + this.generateSubID("customhead") + '" />' +
        '<section>' +
        '<p>' + Layout.input(  { id: this.generateSubID("name"),    label: " ",   list: this.mo_elements.texts }) + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("color"),   label: " " })                                 + '</p>' +
        '<p id="' + this.generateSubID("error") + '"></p>' +
        '</section >'
    );
}


/**
 * @Overwrite
**/
WaypointPreset.prototype.afterDOMAdded = function()
{
    var self = this;
    MecSim.language(

        "/cwaypointenvironment/label",

        // set static labels
        function( pc_key, pc_value ) {
            var la = pc_key.split("_");

            if ((la.length == 1) && (la[0] === "name"))
                self.setTitle( pc_value );

            if (la.length != 2)
                return;
            if (la[0] === "id")
                jQuery( self.generateSubID(la[1], "#") ).text(pc_value);
            if (la[0] === "label")
                jQuery( 'label[for="' + self.generateSubID(la[1]) + '"]' ).text(pc_value);
        },

        // set dynamic content, action binds and layout after finishing language labels
        function() {

            // read distribution list
            MecSim.ajax({
                url     : "/cwaypointenvironment/listdistribution",
                success : function( po_data ) {
                    jQuery.each( po_data, function( pc_key, po_object ) {
                        jQuery("<option></option>")
                            .attr("value", po_object.id)
                            .attr("data-bound", JSON.stringify({left : po_object.left, right : po_object.right}))
                            .text(pc_key)
                            .appendTo(self.generateSubID("distribution", "."));
                    });
                }
            });

            // read factory list
            MecSim.ajax({
                url     : "/cwaypointenvironment/listfactory",
                success : function( po_data ) {
                    jQuery.each( po_data, function( pc_key, po_object ) {
                        jQuery("<option></option>")
                            .attr("value", po_object.id)
                            .attr("requireagent", po_object.agentprogram)
                            .text(pc_key)
                            .appendTo(self.generateSubID("factory", "#"));
                    });
                }
            });

            // read waypoint types
            MecSim.ajax({
                url     : "/cwaypointenvironment/listwaypointtype",
                success : function( po_data ) {
                    jQuery.each( po_data, function( pc_key, pc_id ) {
                        jQuery("<option></option>")
                            .attr("value", pc_id)
                            .text(pc_key)
                            .appendTo(self.generateSubID("type", "#"));
                }); }
            });


            // --- initialize layout ---
            Wizard.prototype.afterDOMAdded.call(self);

            // set accordion for wizard step four
            jQuery( self.generateSubID("carsettings", "#") ).accordion({ header: "h4", collapsible: true, heightStyle: "content", active: false });
            // set color picker
            jQuery( self.generateSubID("color", "#") ).spectrum({
                showPaletteOnly: true,
                togglePaletteOnly: true,
                togglePaletteMoreText: 'more',
                togglePaletteLessText: 'less',
                color: '#008C4F',
                palette: [
                    ["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
                    ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
                    ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"]
                ]
            });

            // set jQuery element definition
            self.mo_elements.selects.forEach(  function( pc_id ) { jQuery( "#"+pc_id ).selectmenu(); });
            self.mo_elements.spinners.forEach( function( pc_id ) { jQuery( "#"+pc_id ).spinner(); });
            self.mo_elements.texts.forEach(    function( pc_id ) { jQuery( "#"+pc_id ).jqxInput({ height: 25, width: 50 }); });

            // set distribution binds
            jQuery(self.generateSubID("distribution", ".")).on('selectmenuchange', function( po_event, po_ui ) {

                var lo_bound     = jQuery(po_ui.item.element).data("bound");
                jQuery( 'label[for="' + po_event.target.id + 'left"]' ).text(lo_bound.left);
                jQuery( 'label[for="' + po_event.target.id + 'right"]' ).text(lo_bound.right);

            });
        }
    );
}

/**
 * @Overwrite
**/
WaypointPreset.prototype.finish = function()
{
    // collect wizard options
    var self = this;
    var lo   = {};
    [
      { id: "color",    isnumber : false},
      { id: "name",     isnumber : false},
      { id: "linger",   isnumber : true},
      { id: "carcount", isnumber : true},
      { id: "type",     isnumber : false},
      { id: "radius",   isnumber : true},
      { id: "factory",  isnumber : false},
      { id: "agent",    isnumber : false}
    ].forEach( function( po_object ) {
        lo[po_object.id] = po_object.isnumber ? Number.parseFloat(jQuery(self.generateSubID(po_object.id, "#")).val()) : jQuery(self.generateSubID(po_object.id, "#")).val();
    });

    [ "basedistribution", "speeddistribution", "maxspeeddistribution",
      "accelerationdistribution", "decelerationdistribution"
    ].forEach( function( pc_key ) {
        lo[pc_key] = {
            distribution : jQuery(self.generateSubID(pc_key, "#")).val(),
            left         : Number.parseFloat(jQuery(self.generateSubID(pc_key+"left", "#")).val()),
            right        : Number.parseFloat(jQuery(self.generateSubID(pc_key+"right", "#")).val())
        };
    });


    // send Ajax request for creating preset
    MecSim.ajax({

        url     : "/cwaypointenvironment/set",
        data    : lo,
        success : function()
        {
            console.log("ok");
        }

    }).fail( function(po_data) {

        jQuery( self.generateSubID("text", "#")   ).text(po_data.responseJSON.error);
        jQuery( self.generateSubID("dialog", "#") ).dialog();

    });
}