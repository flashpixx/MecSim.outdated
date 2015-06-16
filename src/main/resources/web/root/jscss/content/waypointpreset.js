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
        '<p>' + Layout.input(  { id: this.generateSubID("basedistributionboundleft"),                                                   label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("basedistributionboundright"),                                                  label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("carcount"),                                                                    label: " ",   list: this.mo_elements.spinners }) + '</p>' +
        '</section >' +


        // third step - car settings
        '<h3 id="' + this.generateSubID("carhead") + '" />' +
        '<section><div id="' + this.generateSubID("carsettings") + '">' +

        '<h4 id="' + this.generateSubID("speedhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("speeddistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speeddistributionboundleft"),                                                label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("speeddistributionboundright"),                                               label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("maxspeedhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("maxspeeddistribution"),    class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeedboundleft"),                                                    label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("maxspeedboundright"),                                                   label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("accelerationhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("accelerationdistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("accelerationdistributionboundleft"),                                                label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("accelerationdistributionboundright"),                                               label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '</div>' +

        '<h4 id="' + this.generateSubID("decelerationhead") + '" />' +
        '<div>' +
        '<p>' + Layout.select( { id: this.generateSubID("decelerationdistribution"),            class: this.generateSubID("distribution"),   label: " ",   list: this.mo_elements.selects })  + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("decelerationdistributionboundleft"),                                                label: " ",   list: this.mo_elements.texts })    + '</p>' +
        '<p>' + Layout.input(  { id: this.generateSubID("decelerationdistributionboundright"),                                               label: " ",   list: this.mo_elements.texts })    + '</p>' +
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

        function() {

            MecSim.ajax({
                url     : "/cwaypointenvironment/listdistribution",
                success : function( po_data ) {
                    jQuery.each( po_data, function( pc_key, po_object ) {
                        jQuery("<option></option>")
                            .attr("value", pc_key)
                            .attr("data-bound", JSON.stringify(po_object))
                            .text(pc_key)
                            .appendTo(self.generateSubID("distribution", "."));
                    });
                }
            });

            MecSim.ajax({
                url     : "/cwaypointenvironment/listfactories",
                success : function( po_data ){ jQuery.each( po_data, function( pc_key, pl_value ) {

                    jQuery(self.generateSubID("factory", "#"))
                        .append( jQuery("<option></option>")
                            .attr("value", pc_key)
                            .attr("requireagent", pl_value)
                            .text(pc_key)
                        );

                }); }
            });

            MecSim.ajax({
                url     : "/cwaypointenvironment/listwaypointtypes",
                success : function( pa_data ) { pa_data.forEach(function( pc_data ) {

                   jQuery(self.generateSubID("type", "#"))
                        .append( jQuery("<option></option>")
                            .attr("value", pc_data)
                            .text(pc_data)
                        );

                }); }
            });

            Wizard.prototype.afterDOMAdded.call(self);

            jQuery( self.generateSubID("carsettings", "#") ).accordion({ header: "h4", collapsible: true, heightStyle: "content", active: false });
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

            self.mo_elements.selects.forEach(  function( pc_id ) { jQuery( "#"+pc_id ).selectmenu(); });
            self.mo_elements.spinners.forEach( function( pc_id ) { jQuery( "#"+pc_id ).spinner(); });
            self.mo_elements.texts.forEach(    function( pc_id ) { jQuery( "#"+pc_id ).jqxInput({ height: 25, width: 50 }); });

        }
    );
}
