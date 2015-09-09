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

    /**
     * function creates a tag with attributes
     *
     * @param pc_tag tag name without brackets
     * @param po_option Json object options in the structure { id: -DOM ID-, class: -DOM class-, addon: -any additional attribute values-, content: -content of the tag- }
    **/
    var lx_basetag = function( pc_tag, po_options )
    {
        return [[
            '<'+pc_tag,
            po_options.id ? 'id="' + po_options.id + '"' : "",
            po_options.class ? 'class="' + po_options.class + '"' : "",
            po_options.addon ? po_options.addon : "",
            '>',
         ].join(" "),
         po_options.content ? po_options.content : "",
         '</' + pc_tag + '>'
         ].join("");
    }



    // --- dialog div content ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * creates a dialog div structure
     * @param po_options Json object in the format { id: -DOM ID of the dialog-, outer/innerclass: -DOM class-, contentid: -DOM ID of the content-, content: -dialog content-, title: -optional title- }
    **/
    px_modul.dialog = function( po_options )
    {
        return lx_basetag('div', {

            class   : po_options.outerclass,
            id      : po_options.id,
            addon   : po_options.title === undefined ? null : 'title="' + po_options.title + '"',

            content : lx_basetag("div", {

                class   : po_options.innerclass,
                id      : po_options.contentid,
                content : po_options.content

            })
        });
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

        return [ (po_options.label !== undefined ? '<label for="' + po_options.id + '" >' + po_options.label + '</label >' : "") + " ",
                 lx_basetag( 'input', {

                    id    : po_options.id,
                    class : po_options.class,
                    addon : ['type="checkbox"', po_options.value ? "checked " : "", po_options.name ? 'name="' + po_options.name + '"' : ""].join(" "),

                 })
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

        return [ (po_options.label !== undefined ? '<label for="' + po_options.id + '" >' + po_options.label + '</label > ' : "") + " ",
                 lx_basetag( 'input', {

                    id    : po_options.id,
                    class : po_options.class,
                    addon : ['type="text"', po_options.value ? 'value="' + po_options.value + '"' : '', po_options.name ? 'name="' + po_options.name + '"' : ""].join(" ")

                 })
               ].join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- textarea with label ---------------------------------------------------------------------------------------------------------------------------------
    /**
     * creates a textarea with label
     *
     * @param po_options Json object in the format { id: -DOM ID-, class: -DOM class-, label : -optional label-, value: -optional initializing value- }
     * @return HTML string
    **/
    px_modul.area = function( po_options )
    {
        if (Array.isArray(po_options.list))
            po_options.list.push(po_options.id);

        return [ (po_options.label !== undefined ? '<label for="' + po_options.id + '" >' + po_options.label + '</label > ' : "") + " ",
                 lx_basetag( 'textarea', {

                    id      : po_options.id,
                    class   : po_options.class,
                    addon   : po_options.name ? 'name="' + po_options.name + '"' : "",
                    content : po_options.content ? po_options.content : ""

                 })
               ].join("");
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- selectbox with label --------------------------------------------------------------------------------------------------------------------------------

    /**
     * set option by value name
     *
     * @param pc_id DOM ID / selector of the select menu
     * @param pc_value value that should be set
    **/
    px_modul.optionbyvalue = function( pc_id, pc_value )
    {
        jQuery( pc_id + ' option').removeAttr('selected');
        jQuery( pc_id + ' option[value="'+pc_value+'"]').attr('selected', 'selected');
        jQuery( pc_id ).val(pc_value).selectmenu('refresh').change();
    }

    /**
     * function to build option item
     *
     * @param px string or Json object with the structure {id : -ID-, label : -optional label-}
     * @param px_select value which should be selected by default
     * @return option HTML string
    **/
    px_modul.option = function( px, px_select )
    {
        if (classof(px, 'string'))
            return lx_basetag("option", {
                addon   : ['value="' + px + '"', px_select && px == px_select ? 'selected' : ''].join(" "),
                content : px
            });

        return lx_basetag('option', {
            addon   : ['value="' + px.id + '"', px_select && px.id == px_select ? 'selected' : ''].join(" "),
            content : px.label ? px.label : px.id
        });
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
        var lx_selected = po_options.value;

        if (po_options.options)
            po_options.options.forEach( function(px_item) { la.push( px_modul.option(px_item, lx_selected) ); } );

        if (classof(lx_selected, 'number'))
            lx_selected = po_options.options[lx_selected];

        return (po_options.label !== undefined ? '<label for="' + po_options.id + '" >' + po_options.label + '</label >' : "") + " " +
                lx_basetag('select', {

                    id      : po_options.id,
                    class   : po_options.class,
                    addon   : po_options.name ? 'name="' + po_options.name + '"' : '',
                    content : la.join("")

        });
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- selectbox with label and option groups --------------------------------------------------------------------------------------------------------------

    /**
     * creates the option group items
     *
     * @param po { -grouplabel- : [{id : -ID-, label : -optional label-}] }
     * @param px_value selected vaöue
     * @return option group HTML string
    **/
    px_modul.opentiongroup = function( po, px_value )
    {
        if (jQuery.isEmptyObject(po))
            return

        var la = [];
        jQuery.each( po, function( pc_key, pa_values ) {
            if (pc_key.length > 0)
                la.push( '<optgroup label="' + pc_key + '">' );

            pa_values.forEach( function( px_item ) { la.push( px_modul.option(px_item, px_value) ); } );

            if (pc_key.length > 0)
                la.push( '</optgroup>' );
        });

        return la.join("");
    }


    /**
     * creates a select menu with label
     *
     * @param po_options Json object { id: -DOM ID-, class: -DOM class-, label : -optional label-, value: -optional initializing value-, options: -see optiongrouo function- }
     * @return HTML string
    **/
    px_modul.selectgroup = function( po )
    {
        if (jQuery.isEmptyObject(po))
            return;

        if (Array.isArray(po.list))
            po.list.push(po.id);

        var lx_selected = po.value;
        if (classof(lx_selected, 'number'))
            lx_selected = po.options[lx_selected];

        return (po.label !== undefined  ? '<label for="' + po.id + '" >' + po.label + '</label >' : "") + " " +
                lx_basetag('select', {

                    id      : po.id,
                    class   : po.class,
                    addon   : po.name ? 'name="' + po.name + '"' : '',
                    content : po.options ? px_modul.opentiongroup(po.options, lx_selected) : ""

        });
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    return px_modul;

}(MecSim || {}));