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
 * ctor to create the inconsistency item
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASInconsistency( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
MASInconsistency.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MASInconsistency.prototype.getContent = function()
{
    return '<button id = "' + this.getID() + '" ></button >' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
MASInconsistency.prototype.afterDOMAdded = function()
{
    console.log(this.mc_id);
    var self = this;
    MecSim.language({ url : "/cinconsistencyenvironment/" + this.mc_id + "/label", target : this });

    jQuery(this.getID("#")).button().click( function() {

        MecSim.ajax({
            url  : "/cinconsistencyenvironment/" + this.mc_id + "/setmetric",
            data : { id : "SymmetricDifference", path : [] }
        }).fail( function(po_event) { console.log(po_event.responseJSON); } );

    });
}