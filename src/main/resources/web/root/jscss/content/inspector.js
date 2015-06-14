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
 * ctor to create the inspector instance
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Inspector( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Inspector.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Inspector.prototype.getGlobalContent = function()
{
    return Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("table"),
        title     : "Object Inspector"
    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
Inspector.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);


    /**
     * function to format a JSON object in a table
     *
     * @param po_object JSON object
     * @return HTML string
    **/
    function json2table( po_object )
    {
        var lc = '<table>';
        jQuery.each( po_object, function(pc_key, px_value) {
            lc += '<tr><th>' + pc_key + '</th><td>' + (px_value instanceof Object ? json2table(px_value) : px_value + '</td></tr>' );
        });
        return lc + '</table>';
    }


    // --- bind action on the websocket ---------------------------
    var self = this;

    jQuery( self.generateSubID("dialog", "#") ).dialog({ autoOpen: false, width: "auto" });

    MecSim.websocket( "/cinspector/show", {
        "onerror"   : function( po_event ) { jQuery(MecSim.ui().log("#")).prepend( '<span class="' + self.generateSubID("error") + '">' + po_event.data + '</span>' ); },
        "onmessage" : function( po_event ) {

            jQuery( self.generateSubID("table", "#") ).empty().append( json2table(po_event.data.toJSON()) );
            jQuery( self.generateSubID("dialog", "#") ).dialog("open");

        }
    });
}
