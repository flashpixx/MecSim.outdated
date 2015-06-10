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
    /**
     * creates a dialog div structure
     * @param po_options Json object in the format { dialog: -DOM ID of the dialog-, content: -DOM ID of the content-, title: -optional title- }
    **/
    px_modul.dialog = function( po_options )
    {
        return [ '<div id="' + po_options.dialog + '"',
                 (po_options.title ? 'title="' + po_options.title + '"' : "") + '>',
                 '<div id= "' + po_options.content  + '">',
                 '</div></div>'
               ].join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- checkbox with label ---------------------------------------------------------------------------------------------------------------------------------
    /**
     * creates a checkbox with label
     *
     * @param po_options Json object in the format { id: -DOM ID-, class: -DOM class-, label : -optional label-, list: -optional array to return ID-, value: -initial value- }
     * @return HTML string
    **/
    px_modul.checkbox = function( po_options )
    {
        if (Array.isArray(po_options.list))
            po_options.list.push(po_options.id);

        return [ (po_options.label ? '<label for="' + po_options.id + '" >' + po_options.label + '</label >' : ""),
                 ' <input ',
                 (po_options.class ? 'class="' + po_options.class + '"' : ""),
                 ' id="' + po_options.id + '"',
                 'type="checkbox" ',
                 (po_options.value ? "checked" : ""),
                 ' />'
               ].join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- textbox with label ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * creates a textbox with label
     *
     * @param po_options Json object in the format { id: -DOM ID-, class: -DOM class-, label : -optional label-, value: -optional initializing value- }
     * @return HTML string
    **/
    px_modul.input = function( po_options )
    {
        if (Array.isArray(po_options.list))
            po_options.list.push(po_options.id);

        return [ (po_options.label ? '<label for="' + po_options.id + '" >' + po_options.label + '</label >' : ""),
                 ' <input ',
                 (po_options.class ? 'class="' + po_options.class + '"' : ""),
                 ' id="' + po_options.id + '" ',
                 'type="text" ',
                 (po_options.value ? 'value="' + po_options.value + '"' : ""),
                 ' />'
               ].join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- selectbox with label --------------------------------------------------------------------------------------------------------------------------------
    /**
     * creates a select menu with label
     *
     * @param po_options Json object { id: -DOM ID-, class: -DOM class-, label : -optional label-, value: -optional initializing value-, options: [{id : -ID-, label : -optional label-}] }
     * @return HTML string
    **/
    px_modul.select = function( po_options )
    {
        if (Array.isArray(po_options.list))
            po_options.list.push(po_options.id);

        var la = [];
        if (po_options.label)
            la.push( '<label for="' + po_options.id + '" >' + po_options.label + '</label > ' );


        la.push( '<select ' + (po_options.class ? 'class="' + po_options.class + '"' : "") + ' id="' + po_options.id + '">' );
        po_options.options.forEach( function(po_item) {
            la.push( '<option id="' + po_item.id + '" ' + (po_options.value && (po_item.id == po_options.value) ? "selected" : "") + '>' +
                     (po_item.label ? po_item.label : po_item.id) + '</option>'
            );
        });
        la.push("</select>");

        return la.join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    return px_modul;

}(MecSim || {}));