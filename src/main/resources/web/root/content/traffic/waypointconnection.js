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
 * ctor to create the waypoint connection widget
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function WaypointConnection( pc_id, pc_name, pa_panel, po_options )
{
    Widget.call(this, pc_id, pc_name, pa_panel, po_options );
}

/** inheritance call **/
WaypointConnection.prototype = Object.create(Widget.prototype);


/**
 * @Overwrite
**/
WaypointConnection.prototype.afterDOMAdded = function()
{
    var self = this;
    MecSim.language({

        url    : "/cwaypointenvironment/labelwaypointconnection",
        target : this,

        finish : function() {
            Widget.prototype.afterDOMAdded.call(self);
        }
    });
}