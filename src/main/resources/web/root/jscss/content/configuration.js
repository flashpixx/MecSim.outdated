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
 * ctor to create the configuration view
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Configuration( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Configuration.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Configuration.prototype.getGlobalContent = function()
{
    return '<div id = "' + this.generateSubID("dialog") + '" title = "Object Inspector" >' +
           '<div id = "' + this.generateSubID("table")  + '" ></div>' +
           '</div>' +
           Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
Configuration.prototype.getContent = function()
{
    return '<button id = "' + this.generateSubID("configuration") + '" >Configuration</button >' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Configuration.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    // create button and bind action with Ajax call
    jQuery(self.generateSubID("configuration", "#")).button().click( function() {

        MecSim.ajax({

            url     : "/cconfiguration/get",
            success : function( po_data ) {
                self.mo_configuration = po_data;
            }

        }).done(function() {
            self.view();
        });

    });

}


/**
 * creates the view with the configuration data
**/
Configuration.prototype.view = function()
{
    var self = this;
    jQuery( MecSim.ui().content("#") ).empty();

    // global /main, ui, simulation, database

    // add tab structure to the content div and create the jQuery definition
    jQuery( MecSim.ui().content("#") ).append(

        '<div id="' + this.generateSubID("tabs") + '">' +

        '<ul>' +
        '<li><a href="' + this.generateSubID("general", "#")    + '">General</a></li>' +
        '<li><a href="' + this.generateSubID("ui", "#")         + '">User Interface</a></li>' +
        '<li><a href="' + this.generateSubID("simulation", "#") + '">Simulation</a></li>' +
        '<li><a href="' + this.generateSubID("database", "#")   + '">Database</a></li>' +
        '</ul>' +

        '<div id="' + this.generateSubID("general") + '">general</div>' +
        '<div id="' + this.generateSubID("ui") + '">ui</div>' +
        '<div id="' + this.generateSubID("simulation") + '">simulation</div>' +
        '<div id="' + this.generateSubID("database") + '">database</div>' +

        '</div>'

    );

    jQuery( this.generateSubID("tabs", "#") ).tabs();
}
