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
 * ctor to create the about dialog instance
 *
 * @param pc_id ID
 * @param pc_name name of the panel
**/
function About( pc_id, pc_name )
{
    Pane.call(this, pc_id, pc_name );
}

/** inheritance call **/
About.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
About.prototype.getGlobalContent = function()
{
    return String.raw`<div id = "${this.generateSubID("dialog")}" >
        <p id = "${this.generateSubID("text")}" >
            <a id = "${this.generateSubID("name")}" ></a >
            <br /><br />
            <label for = "${this.generateSubID("license")}" >License: </label >
            <a id = "${this.generateSubID("license")}" ></a >
            <br />
            <label for = "${this.generateSubID("buildversion")}" >Buildversion: </label >
            <a id = "${this.generateSubID("buildversion")}" ></a >
            <br />
            <label for = "${this.generateSubID("buildcommit")}" >Buildcommit: </label >
            <a id = "${this.generateSubID("buildcommit")}" ></a >
        </p >
    </div >`;
}


/**
 * @Overwrite
**/
About.prototype.getContent = function()
{
    return String.raw`<button id = "${this.generateSubID("button")}" >${this.getName()}</button >`;
}


/**
 * @Overwrite
**/
About.prototype.afterDOMAdded = function()
{
    var self = this;

    // create button & bind action to the button
    jQuery(self.generateSubID("button", "#")).button().click( function() {

        // click reads JSON data
        jQuery.getJSON( "/cconfiguration/get", function( po_data ) {

            jQuery(self.generateSubID("name", "#"))
                .attr("href", po_data.manifest["project-url"])
                .text(po_data.manifest["project-name"]);

            jQuery(self.generateSubID("license", "#"))
                .attr("href", po_data.manifest["license-url"])
                .text(po_data.manifest["license"]);

            jQuery(self.generateSubID("buildversion", "#"))
                .text(po_data.manifest["build-version"]);


        }).done( function() {

            // after adding data - dialog is called
            jQuery(self.generateSubID("dialog", "#")).dialog({
                width: 500,
                modal: true
            });

        });

    });
}
