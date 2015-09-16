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
 * ctor to create the help menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Help( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Help.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Help.prototype.getGlobalContent = function()
{
    return Layout.dialog({

        id      : this.generateSubID("dialog"),
        content : '<a id = "'   + this.generateSubID("name")   + '" ></a >' +
                  '<br /><br />' +
                  '<label for = "' + this.generateSubID("license") + '" >License: </label >' +
                  '<a id = "'      + this.generateSubID("license") + '" ></a >' +
                  '<br />' +
                  '<label for = "' + this.generateSubID("buildversion") + '" >Version: </label >' +
                  '<a id = "'      + this.generateSubID("buildversion") + '" ></a >' +
                  '<br />' +
                  '<label for = "' + this.generateSubID("buildnumber") + '" >Buildnumber: </label >' +
                  '<a id = "'      + this.generateSubID("buildnumber") + '" ></a >' +
                  '<br />' +
                  '<label for = "' + this.generateSubID("buildcommit") + '" >Buildcommit: </label >' +
                  '<a id = "'      + this.generateSubID("buildcommit") + '" ></a >'
    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
Help.prototype.getContent = function()
{
    return '<p><button class = "ui-menu-button" id = "' + this.generateSubID("about") + '" ></button></p>' +
           '<p><button class = "ui-menu-button" id = "' + this.generateSubID("userdoc") + '" ></button> ' +
           '<button class = "ui-menu-button" id = "' + this.generateSubID("devdoc") + '" ></button></p>' +
           Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Help.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    MecSim.language({ url : "/clanguageenvironment/help", target : this });

    jQuery(this.generateSubID("dialog", "#")).dialog({
        width    : "auto",
        modal    : true,
        autoOpen : false
    });


    // --- create about button & bind action to the button -----------------------------------------------------------------------------------------------------
    jQuery(this.generateSubID("about", "#")).button().click( function() {

        // click reads JSON data
        jQuery.getJSON( "/cconfiguration/get", function( po_data ) {

            jQuery(self.generateSubID("name", "#"))
                .attr("href", po_data.manifest["project-url"])
                .html( po_data.manifest["project-name"] ? po_data.manifest["project-name"].replace(/-/g, "<br/>") :  "" ) ;

            jQuery(self.generateSubID("license", "#"))
                .attr("href", po_data.manifest["license-url"])
                .text(po_data.manifest["license"]);

            jQuery(self.generateSubID("buildversion", "#"))
                .text(po_data.manifest["build-version"]);

            jQuery(self.generateSubID("buildnumber", "#"))
                .text(po_data.manifest["build-number"]);

            jQuery(self.generateSubID("buildcommit", "#"))
                .text(  po_data.manifest["build-commit"] ? po_data.manifest["build-commit"].substring(0,8) : ""  );

        }).done( function() { jQuery(self.generateSubID("dialog", "#")).dialog("open"); });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- create documentation button & bind action to button -------------------------------------------------------------------------------------------------
    jQuery(self.generateSubID("userdoc", "#")).button().click( function() {

        jQuery.get("/userdoc/", function( px_result ) {
            jQuery(MecSim.ui().content("#")).empty().append( px_result );
        });

    });

    jQuery(self.generateSubID("devdoc", "#")).button().click( function() {
        jQuery(MecSim.ui().content("#")).empty().append( '<iframe id = "devdoku" class = "template" src = "/develdoc/" seamless />' );
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}


/**
 * @Overwrite
**/
Help.prototype.setName = function(pc)
{
    this.mc_name = pc;
    jQuery( this.getID("#") ).append(this.mc_name);
}