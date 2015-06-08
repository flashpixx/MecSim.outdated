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
 * ctor to create the simulation start / stop / reset button
 *
 * @param pc_id ID
 * @param pc_name name of the panel
**/
function Run( pc_id, pc_name )
{
    Pane.call(this, pc_id, pc_name );
}

/** inheritance call **/
Run.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Run.prototype.getContent = function()
{
    return String.raw`<button id = "${this.generateSubID("start")}" >Start</button >
        <button id = "${this.generateSubID("stop")}" >Stop</button >
        <button id = "${this.generateSubID("reset")}" >Reset</button >`;
}


/**
 * @Overwrite
**/
Run.prototype.getGlobalContent = function()
{
    return String.raw`<div id = "${this.generateSubID("dialog")}" >
        <div class = "dialog-error" >
            <p id = "${this.generateSubID("text")}" >
                <span class = "ui-icon ui-icon-alert" ></span >
            </p >
        </div >
    </div >`;
}


/**
 * @Overwrite
**/
Run.prototype.afterDOMAdded = function()
{
    var self = this;

    // create buttons & bind actions to the button
    ["start", "stop", "reset"].forEach( function(pc_item) {

        jQuery(self.generateSubID(pc_item, "#")).button().click( function() {

            jQuery.ajax("/csimulation/"+pc_item).fail( function( po_data ) {

                jQuery( self.generateSubID("text", "#")   ).text(po_data.responseJSON.error);
                jQuery( self.generateSubID("dialog", "#") ).dialog();

            });

        });

    });
}
