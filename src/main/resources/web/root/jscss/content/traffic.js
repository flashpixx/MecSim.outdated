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
 * ctor to create the traffic menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Traffic( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Traffic.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Traffic.prototype.getGlobalContent = function()
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
Traffic.prototype.getContent = function()
{
    return '<div id="'  + this.generateSubID("graphweights")  + '" />' +
           '<ul id = "' + this.generateSubID("drivingmodel") + '" />';
}


/**
 * @Overwrite
**/
Traffic.prototype.afterDOMAdded = function()
{
    var self = this;


    // --- create sortable list, bind actions and fill the data ---------------------------
    jQuery( this.generateSubID("drivingmodel", "#") ).sortable({

        placeholder: "ui-state-highlight",
        stop: function(px_event, po_ui) {

            MecSim.ajax({
                url   : "/ctrafficenvironment/setdrivemodel",
                data  : {"id": jQuery( self.generateSubID("drivingmodel", "#") ).children("li:first").attr("id")}
            });

        }

    });

    // @todo remove ID attribute to avoid errors
    MecSim.ajax({

        url     : "/ctrafficenvironment/listdrivemodel",
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
                jQuery( self.generateSubID("drivingmodel", "#") ).append( '<li class="ui-state-default" id="'+ px_value.id + '">' + px_value.name + '</li>' );
            });

        }
    });

}
