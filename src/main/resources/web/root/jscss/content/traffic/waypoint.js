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
 * ctor to create the waypoint menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Waypoint( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    this.mo_wizardpreset = new WaypointPreset( "waypointpreset" );
    this.mo_wizardpreset.setParent(this);
}

/** inheritance call **/
Waypoint.prototype = Object.create(Pane.prototype);



/**
 * @Overwrite
**/
Waypoint.prototype.getGlobalCSS = function()
{
    return this.mo_wizardpreset.getGlobalCSS() + Pane.prototype.getGlobalCSS.call(this);
}


/**
 * @Overwrite
**/
Waypoint.prototype.getGlobalContent = function()
{
    jQuery( MecSim.ui().static("#") ).append(this.mo_wizardpreset.getContent());

    return Pane.prototype.getGlobalContent.call(this);
}

/**
 * @Overwrite
**/
Waypoint.prototype.getContent = function()
{
    return '<button id = "' + this.generateSubID("newpreset") + '" ></button ><br/>' +
           '<button id = "' + this.generateSubID("listpreset") + '" ></button ><br/>' +
           Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Waypoint.prototype.afterDOMAdded = function()
{
    var self = this;

    Pane.prototype.afterDOMAdded.call(this);
    this.mo_wizardpreset.afterDOMAdded();
    MecSim.language({ url : "/cwaypointenvironment/labelmainmenu", target : this });

    jQuery( this.generateSubID("listpreset", "#") ).button();
    jQuery( this.generateSubID("newpreset", "#") ).button().click( function() {
        jQuery(MecSim.ui().content("#")).empty();
        self.mo_wizardpreset.show();
    });
}


/**
 * @Overwrite
**/
Waypoint.prototype.setName = function(pc)
{
    this.mc_name = pc;
    jQuery( this.getID("#") ).append(this.mc_name);
}
