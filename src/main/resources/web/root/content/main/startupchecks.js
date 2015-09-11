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
 * ctor to create the start-up checks instance
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function StartUpChecks( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
StartUpChecks.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
StartUpChecks.prototype.getGlobalContent = function()
{
    return Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("dialogtext"),
        title     : this.generateSubID("dialogtitle")
    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
StartUpChecks.prototype.afterDOMAdded = function()
{
    var self = this;
    Pane.prototype.afterDOMAdded.call(this);

    // run startup checks and show result
    MecSim.ajax({
        url : "/cconfiguration/startupchecks"
    }).done(function(po) {

        if (po.messages.length == 0)
            return;

        jQuery(self.generateSubID("dialogtext", "#")).empty().append(
            "<p><ul><li>" +
            po.messages.join("</li><li>") +
            "</li></ul></p>"
        );

        jQuery(self.generateSubID("dialog", "#")).dialog({
            title   : "Start-Up Checks",
            width   : "auto",
            modal   : true,
            buttons : {
                Ok : function() { jQuery(this).dialog("close"); }
            }
        });

    });
}

/**
 * @Overwrite
**/
StartUpChecks.prototype.isHidden = function()
{
    return true;
}