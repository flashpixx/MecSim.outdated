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
 * structur to generate HTML elements
 **/
var Layout = (function (px_modul) {

    // --- dialog div content ----------------------------------------------------------------------------------------------------------------------------------
    px_modul.dialog = function( po_options )
    {
        return '<div id = "' + po_options.dialog + '" ' + (po_options.title ? 'title="' + po_options.title + '"' : "") + '><div id = "' + po_options.content  + '" ></div></div>';
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- checkbox with label ---------------------------------------------------------------------------------------------------------------------------------
    px_modul.checkbox = function( po_options )
    {
        if (Array.isArray(po_object.list))
            po_object.list.push(lc_id);

        return (po_options.label ? '<label for = "' + po_options.id + '" >' + po_object.label + '</label >' : "") + '<input id="' + po_options.id + '" type="checkbox" '+ (po_object && po_object.value ? "checked" : "") +' />';
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------




    return px_modul;

}(MecSim || {}));