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
 * ctor to create the layer menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
**/
function Layer( pc_id, pc_name )
{
    Pane.call(this, pc_id, pc_name );
}

/** inheritance call **/
Layer.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Layer.prototype.getGlobalContent = function()
{
    return '<div id = "' + this.generateSubID("dialog") + '">' +
           '<div class = "dialog-error" > ' +
           '<p id = "' + this.generateSubID("text") + '" >' +
           '<span class = "ui-icon ui-icon-alert" ></span >' +
           '</p >' +
           '</div >' +
           '</div >';
}


/**
 * @Overwrite
**/
Layer.prototype.getContent = function()
{
    var self = this;
    var lc = "";

    // switch items


    // clickable layer
    lc += '<ul id = "' + this.generateSubID("clickable") + '">';
    jQuery.ajax({

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
                jQuery( self.generateSubID("clickable", "#") ).append("<li class='ui-state-default' id="+ px_value.id +">" + px_value.name + "</li>" );
            });

        }
    });
    lc += "</ul>";

    return lc;
}


/**
 * @Overwrite
**/
Layer.prototype.afterDOMAdded = function()
{
    var self = this;

    // create bind of the clickable layer
    jQuery( this.generateSubID("clickable", "#") ).sortable({

        placeholder: "ui-state-highlight",
        stop: function(px_event, po_ui) {

            MecSim.ajax({
                url   : "/cosmviewer/setclickablelayer",
                data  : {"id": jQuery( self.generateSubID("clickable", "#") ).children("li:first").attr("id")}
            });

        }

    });

}
