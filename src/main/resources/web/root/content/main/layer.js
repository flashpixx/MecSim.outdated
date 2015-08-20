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
 * ctor to create the layer menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Layer( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Layer.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Layer.prototype.getGlobalContent = function()
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
Layer.prototype.getContent = function()
{
    return '<div id="'  + this.generateSubID("switches")  + '" />' +
           '<ul id = "' + this.generateSubID("clickable") + '" />' +
           Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Layer.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    MecSim.language({ url : "/clanguageenvironment/layer", target : this });


    // --- create activity / visibility switches for layer and bind actions ------------------------------------------------------------------------------------
    MecSim.ajax({

        url     : "/csimulation/listlayer",
        success : function( px_data ) {

            jQuery.each( px_data, function( pc_key, px_value ) {

                var lc = '<p>' + Layout.checkbox({
                    id    : self.generateSubID("activity_" + px_value.id),
                    class : self.generateSubID("switchactivity"),
                    value : px_value.active,
                    name  : px_value.id,
                    label : pc_key
                });

                if (px_value.isviewable)
                    lc += Layout.checkbox({
                        id    : self.generateSubID("visibility_"+px_value.id),
                        class : self.generateSubID("switchvisibility"),
                        value : px_value.visible,
                        name  : px_value.id
                    });
                lc += '</p>';
                jQuery( self.generateSubID("switches", "#") ).append(lc);

            });
        }

    }).done(function() {

        // fail closure function to open an error dialog
        var lx_failclosure = function( po_event ) {

            return function( po_data ) {
                jQuery(self.generateSubID("text", "#")).text(po_data.responseJSON.error);
                jQuery(self.generateSubID("dialog", "#")).dialog({
                    modal    : true,
                    overlay  : { background: "black" }
                });
            }

        };

        // create action bind to both switch types
        // @todo on fail state must be reset
        [
            { url: "/csimulation/disableenablelayer", ontext: "active",  offtext: "inactive",  id: "switchactivity" },
            { url: "/csimulation/hideshowlayer",      ontext: "visible", offtext: "invisible", id: "switchvisibility" },
        ].forEach( function( po_item ) {

            jQuery( self.generateSubID(po_item.id, ".") ).bootstrapSwitch({
                size           : "mini",
                onText         : po_item.ontext,
                offText        : po_item.offtext,
                onSwitchChange : function( px_event, pl_state ) {

                    MecSim.ajax({

                        url  : po_item.url,
                        data : {
                            "id"    : jQuery(this).closest("input").attr("name"),
                            "state" : pl_state
                        }

                    }).fail( lx_failclosure(px_event) );

                }
            });

        });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- create sortable list for clickable layer, bind actions and fill the data ----------------------------------------------------------------------------
    jQuery( this.generateSubID("clickable", "#") ).sortable({

        placeholder: "ui-state-highlight",
        stop: function(px_event, po_ui) {

            MecSim.ajax({
                url   : "/cosmviewer/setclickablelayer",
                data  : {"id": jQuery( self.generateSubID("clickable", "#") ).children("li:first").attr("name")}
            }).fail( lx_failclosure(px_event) );

        }

    });

    MecSim.ajax({

        url     : "/cosmviewer/listclickablelayer",
        success : function(px_data) {

            // sort JSON objects depend on "click" property and store the ordered list in an array
            var la_sorted = [];
            Object.keys(px_data).sort(function(i,j){ return px_data[i].click ? -1 : 1 }).forEach( function(pc_key) {
                var lo  = px_data[pc_key];
                lo.name = pc_key;
                la_sorted.push(lo);
            });

            // add list items to the DOM
            jQuery.each( la_sorted, function(pn_key, px_value){
                jQuery( self.generateSubID("clickable", "#") ).append( '<li class="ui-state-default" id="'+ self.generateSubID("clickable_"+px_value.id) + '" name="' + px_value.id + '">' + px_value.name + '</li>' );
            });

        }
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}

/**
 * @Overwrite
**/
Layer.prototype.setName = function(pc)
{
    this.mc_name = pc;
    jQuery( this.getID("#") ).append(this.mc_name);
}
