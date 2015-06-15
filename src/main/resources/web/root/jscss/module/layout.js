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
     * @param po_options Json object in the format { id: -DOM ID of the dialog-, contentid: -DOM ID of the content-, content: -dialog content-, title: -optional title- }
    **/
    px_modul.dialog = function( po_options )
    {
        return [ '<div id="' + po_options.id + '"',
                 (po_options.title ? 'title="' + po_options.title + '"' : "") + '>',
                 '<div ' + (po_options.contentid ? 'id= "' + po_options.contentid + '"' : "")  + '>',
                 (po_options.content ? po_options.content : ""),
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
                 (po_options.value ? "checked " : " "),
                 (po_options.name ? 'name="' + po_options.name + '"' : ""),
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
                 (po_options.value ? 'value="' + po_options.value + '" ' : ""),
                 (po_options.name ? 'name="' + po_options.name + '"' : ""),
                 ' />'
               ].join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- selectbox with label --------------------------------------------------------------------------------------------------------------------------------
    /**
     * function to build option item
     *
     * @param px string or Json object with the structure {id : -ID-, label : -optional label-}
     * @param px_select value which should be selected by default
     * @return option HTML string
    **/
    var lx_buildItem = function( px, px_select )
    {
        if (classof(px, "string"))
            return '<option value="' + px + '" ' + (px_select && px == px_select ? 'selected' : '') + '>' + px + '</option>';

        return '<option value="' + px.id + '" ' + (px_select && px.id == px_select ? 'selected' : '') + '>' + (px.label ? px.label : px.id)  + '</option>';
    }

    /**
     * creates a select menu with label
     *
     * @param po_options Json object { id: -DOM ID-, class: -DOM class-, label : -optional label-, value: -optional initializing value-, options: [ string or {id : -ID-, label : -optional label-} ] }
     * @return HTML string
    **/
    px_modul.select = function( po_options )
    {
        if (Array.isArray(po_options.list))
            po_options.list.push(po_options.id);

        var la = [];
        if (po_options.label)
            la.push( '<label for="' + po_options.id + '" >' + po_options.label + '</label > ' );


        la.push( '<select ' + (po_options.name ? 'name="' + po_options.name + '" ' : "") +  (po_options.class ? 'class="' + po_options.class + '"' : "") + ' id="' + po_options.id + '">' );
        if (po_options.options)
            po_options.options.forEach( function(px_item) { la.push( lx_buildItem(px_item, po_options.value) ); } );
        la.push("</select>");

        return la.join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- selectbox with label and option groups --------------------------------------------------------------------------------------------------------------
    /**
     * creates a select menu with label
     *
     * @param po_options Json object { id: -DOM ID-, class: -DOM class-, label : -optional label-, value: -optional initializing value-, options: { -grouplabel- : [{id : -ID-, label : -optional label-}] } }
     * @return HTML string
    **/
    px_modul.selectgroup = function( po_options )
    {
        if (Array.isArray(po_options.list))
            po_options.list.push(po_options.id);

        var la = [];
        if (po_options.label)
            la.push( '<label for="' + po_options.id + '" >' + po_options.label + '</label > ' );


        la.push( '<select ' + (po_options.name ? 'name="' + po_options.name + '" ' : "") +  (po_options.class ? 'class="' + po_options.class + '"' : "") + ' id="' + po_options.id + '">' );
        if (po_options.options)
            jQuery.each( po_options.options, function( pc_key, pa_values ) {
                if (pc_key.length > 0)
                    la.push( '<optgroup label="' + pc_key + '">' );

                pa_values.forEach( function( px_item ) { la.push( lx_buildItem(px_item, po_options.value) ); } );

                if (pc_key.length > 0)
                    la.push( '</optgroup>' );
            });
        la.push("</select>");

        return la.join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    return px_modul;

}(MecSim || {}));