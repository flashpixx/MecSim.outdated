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
 * ctor to create the logger instance
 *
 * @param pc_id ID
 * @param pc_name name of the panel
**/
function Logger( pc_id )
{
    Pane.call(this, pc_id );
}

/** inheritance call **/
Logger.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Logger.prototype.afterDOMAdded = function()
{
    var self = this;

    MecSim.websocket( "/cconsole/output/log", {
        "onerror"   : function( po_event ) { jQuery(MecSim.ui().log("#")).prepend( '<span class="' + self.generateSubID("error") + '">' + po_event.data + '</span>' ); },
        "onmessage" : function( po_event ) { jQuery(MecSim.ui().log("#")).prepend( '<span class="' + self.generateSubID("output")  + '">' + po_event.data + '</span>' ); }
    });

    MecSim.websocket( "/cconsole/error/log", {
        "onerror"   : function( po_event ) { jQuery(MecSim.ui().log("#")).prepend( '<span class="' + self.generateSubID("error") + '">' + po_event.data + '</span>' ); },
        "onmessage" : function( po_event ) { jQuery(MecSim.ui().log("#")).prepend( '<span class="' + self.generateSubID("error")  + '">' + po_event.data + '</span>' ); }
    });
}


/**
 * @Overwrite
**/
Logger.prototype.getGlobalCSS = function()
{
   return this.generateSubID("error", ".") +
          '{' +
          'color: #8C1C00;' +
          'display: block;' +
          'font-family: monospace;' +
          'white-space: nowrap;' +
          '}' +

          this.generateSubID("output", ".") +
          '{' +
          'display: block;' +
          'font-family: monospace;' +
          'white-space: nowrap;' +
          '}' +

          MecSim.ui().log("#") +
          '{' +
          'overflow: auto;' +
          '}';
}